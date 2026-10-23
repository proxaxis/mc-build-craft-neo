# mc_build_craft_neo

Neo Builder MOD for Minecraft Fabric servers.

## testserver の操作 (Minecraft MCP)

`testserver` サービス (Docker Compose の `mc_build_craft_neo_testserver`, itzg/minecraft-server + Fabric) は Devin CLI の MCP サーバ経由で操作できる。

### MCP 設定

- MCP サーバ実装: `.devin/mcp/testserver/server.mjs` (stdio, Node.js)
  - Source RCON プロトコルを TCP で直接実装。依存: `@modelcontextprotocol/sdk`, `zod` (`npm install` 済み)
- 登録: `.devin/mcp_config.json` の `testserver` エントリ
- 権限: `.devin/config.json` で `mcp__testserver__*` を自動許可済み

### 接続情報

- RCON 接続先: `testserver:25575` (Docker ネットワーク経由。operationer コンテナから `localhost` では届かないのでホスト名で接続すること)
- パスワード: `devpassword` (docker-compose.yml の `RCON_PASSWORD` と同じ)
- 環境変数で上書き可能: `MC_RCON_HOST`, `MC_RCON_PORT`, `MC_RCON_PASSWORD`

### 提供ツール

| ツール | 内容 |
|---|---|
| `execute_command` | 任意の Minecraft コマンドを RCON 実行 (`give`, `tp`, `gamemode`, `op` など。先頭の `/` は不要) |
| `list_players` | オンラインプレイヤー一覧 |
| `say` | チャット全体通知 |
| `check_connection` | RCON 接続の疎通確認 |

### 補足

- testserver は `docker compose up testserver` で起動。`docker exec mc_build_craft_neo_testserver rcon-cli <cmd>` でも同等の操作が可能 (コンテナ内 `RCON_PASSWORD` 環境変数を使用)
- RCON コマンドは直列化して送信し、切断時は自動で再接続・1 回リトライする
- 手動確認: `devin mcp list` / `devin mcp get testserver`
