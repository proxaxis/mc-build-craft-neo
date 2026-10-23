import net from "node:net";
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";

const RCON_HOST = process.env.MC_RCON_HOST || "testserver";
const RCON_PORT = Number(process.env.MC_RCON_PORT || 25575);
const RCON_PASSWORD = process.env.MC_RCON_PASSWORD || "";
const REQUEST_TIMEOUT_MS = 15000;

const AUTH = 3;
const COMMAND = 2;
const RESPONSE = 0;

class Rcon {
  constructor(host, port, password) {
    this.host = host;
    this.port = port;
    this.password = password;
    this.socket = null;
    this.buffer = Buffer.alloc(0);
    this.handlers = new Set();
    this.nextId = 1;
    this.connecting = null;
    // Serialize all requests: Minecraft RCON answers in order, so one
    // outstanding command chain keeps request-id matching unambiguous.
    this.queue = Promise.resolve();
  }

  connect() {
    if (this.socket && !this.socket.destroyed) return Promise.resolve();
    if (this.connecting) return this.connecting;
    this.connecting = this.#open().finally(() => {
      this.connecting = null;
    });
    return this.connecting;
  }

  async #open() {
    const socket = net.createConnection(this.port, this.host);
    this.socket = socket;
    this.buffer = Buffer.alloc(0);
    socket.on("data", (chunk) => this.#onData(chunk));
    socket.on("error", () => {});
    socket.on("close", () => {
      if (this.socket === socket) this.socket = null;
      const err = new Error("RCON connection closed");
      for (const h of this.handlers) h.reject(err);
      this.handlers.clear();
    });
    await new Promise((resolve, reject) => {
      socket.once("connect", resolve);
      socket.once("error", reject);
    });
    const res = this.#awaitResponse(0);
    this.#send(0, AUTH, this.password);
    const packet = await res;
    if (packet.id === -1) {
      socket.destroy();
      throw new Error("RCON authentication failed");
    }
  }

  #onData(chunk) {
    this.buffer = Buffer.concat([this.buffer, chunk]);
    while (this.buffer.length >= 4) {
      const length = this.buffer.readInt32LE(0);
      if (this.buffer.length < 4 + length) return;
      const packet = this.buffer.subarray(4, 4 + length);
      this.buffer = this.buffer.subarray(4 + length);
      const parsed = {
        id: packet.readInt32LE(0),
        type: packet.readInt32LE(4),
        payload: packet.subarray(8, packet.length - 2).toString("utf8"),
      };
      for (const h of this.handlers) h.onPacket(parsed);
    }
  }

  #send(id, type, payload) {
    const body = Buffer.alloc(4 + 4 + Buffer.byteLength(payload) + 2);
    body.writeInt32LE(id, 0);
    body.writeInt32LE(type, 4);
    body.write(payload, 8, "utf8");
    const packet = Buffer.alloc(4 + body.length);
    packet.writeInt32LE(body.length, 0);
    body.copy(packet, 4);
    this.socket.write(packet);
  }

  #awaitResponse(id) {
    return new Promise((resolve, reject) => {
      const handler = {
        onPacket: (packet) => {
          if (packet.id !== id && packet.id !== -1) return;
          clearTimeout(timer);
          this.handlers.delete(handler);
          resolve(packet);
        },
        reject: (err) => {
          clearTimeout(timer);
          reject(err);
        },
      };
      const timer = setTimeout(() => {
        this.handlers.delete(handler);
        reject(new Error("RCON request timed out"));
      }, REQUEST_TIMEOUT_MS);
      this.handlers.add(handler);
    });
  }

  command(cmd) {
    const run = this.queue.then(() => this.#doCommand(cmd));
    this.queue = run.catch(() => {});
    return run;
  }

  async #doCommand(cmd) {
    let lastErr;
    for (let attempt = 0; attempt < 2; attempt++) {
      try {
        await this.connect();
        return await this.#exec(cmd);
      } catch (err) {
        lastErr = err;
        this.#kill();
      }
    }
    throw lastErr;
  }

  #exec(cmd) {
    const id = this.nextId++ % 50000;
    const sentinelId = id + 50000;
    const parts = [];
    return new Promise((resolve, reject) => {
      const handler = {
        onPacket: (packet) => {
          if (packet.id === id) {
            parts.push(packet.payload);
          } else if (packet.id === sentinelId) {
            // Responses arrive in order, so the sentinel response marks the
            // end of the (possibly multi-packet) command output.
            clearTimeout(timer);
            this.handlers.delete(handler);
            resolve(parts.join(""));
          }
        },
        reject: (err) => {
          clearTimeout(timer);
          reject(err);
        },
      };
      const timer = setTimeout(() => {
        this.handlers.delete(handler);
        reject(new Error("RCON command timed out"));
      }, REQUEST_TIMEOUT_MS);
      this.handlers.add(handler);
      this.#send(id, COMMAND, cmd);
      this.#send(sentinelId, RESPONSE, "end");
    });
  }

  #kill() {
    const socket = this.socket;
    if (socket) socket.destroy();
    if (this.socket === socket) this.socket = null;
  }
}

const rcon = new Rcon(RCON_HOST, RCON_PORT, RCON_PASSWORD);

async function run(cmd) {
  const out = await rcon.command(cmd);
  return out.trim() || "(no output)";
}

const text = (s) => ({ content: [{ type: "text", text: s }] });

const server = new McpServer({
  name: "minecraft-testserver",
  version: "1.0.0",
});

server.registerTool(
  "execute_command",
  {
    title: "Execute Minecraft command",
    description:
      "Run any Minecraft server command on the testserver via RCON (e.g. 'list', 'give Steve diamond 1', 'tp Steve 0 80 0', 'gamemode creative Steve').",
    inputSchema: { command: z.string().describe("The Minecraft command to execute, without leading '/'") },
  },
  async ({ command }) => text(await run(command))
);

server.registerTool(
  "list_players",
  {
    title: "List online players",
    description: "List players currently online on the testserver.",
    inputSchema: {},
  },
  async () => text(await run("list"))
);

server.registerTool(
  "say",
  {
    title: "Broadcast chat message",
    description: "Broadcast a message to all players on the testserver.",
    inputSchema: { message: z.string().describe("Message to broadcast") },
  },
  async ({ message }) => text(await run(`say ${message}`))
);

server.registerTool(
  "check_connection",
  {
    title: "Check testserver connection",
    description: "Verify the RCON connection to the testserver is working.",
    inputSchema: {},
  },
  async () => {
    try {
      return text(`OK: ${await run("list")}`);
    } catch (err) {
      return text(`FAILED: ${err.message}`);
    }
  }
);

const transport = new StdioServerTransport();
await server.connect(transport);
