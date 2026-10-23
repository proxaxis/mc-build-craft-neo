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

## 実装済みシステム (buildcraftneo MOD)

- 陣営/職業: `/bcn fake spawn|set|give|use|status` で RCON から全検証可能 (FakePlayer ハーネス)
- 資源: MD(ウォレット)/MP/EFP = attachment。EFP は ServerLevel attachment、閾値超過で襲撃イベント
- 工業: `stirling_engine`/`quarry`/`macerator`/`item_pipe`/`teleport_pipe`/`request_pipe`/`nuclear_reactor`/`shop`/`dimensional_gate` ブロック。`/bcn machine info|insert|extract|mj`
- teleport_pipe: 同一チャネル(0-7)間で無線輸送、次元横断可。右クリック=チャネル、スニーク+右クリック=mode(both/send/receive)。NBT `mode`/`channel` でも設定可。配送直後のコンテナは 200 tick pull 抑制(逆流防止)
- request_pipe: 隣接コンテナ=出力先、パイプ網経由で要求アイテムを取寄せ。`/bcn machine request <pos> <item>`
- shop: `/bcn shop offer|collect`、`/bcn fake buy`。右クリック購入、在庫=内部27スロット
- GUI(Menu): `ShopMenu`(27スロット+プレイヤーinv) + クライアント Screen (`ShopScreen`, `client` entrypoint)。`/bcn fake menuopen|menuclick|menuclose` でサーバ側、`/bcn shop menu <pos> <player>` で実クライアント検証。**Xvfb実機検証済**: app コンテナに xvfb+mesa(llvmpipe)+xdotool 導入、`DISPLAY=:99 ./gradlew runClient` で DevClient 自動接続(`--quickPlayMultiplayer testserver:25565`)、`import -window root` でスクリーンショット取得。注意: Menu の stillValid は8ブロック距離制限があるため openMenu 対象プレイヤーは近くに tp 必須
- オークション: `/bcn auction list|sell|buy|collect`。出品=手持ちスタック、MD 決済、出品者不在時は pending→collect。overworld attachment 永続化
- ダンジョン/ボス: `/bcn dungeon gen <pos>`(9x5x5 石レンガ部屋+祭壇+宝箱)、`boss_altar` 起動で Twilight Knight(HP200)召喚。ボスバー連動・討伐でコイン/ダイヤドロップ
- dimensional_gate: 非衝突ポータル。進入エンティティを目標次元スポーンへ転移(右クリックで target 循環: twilight→skyreach→overworld)
- 次元: `buildcraftneo:twilight`/`buildcraftneo:skyreach` (datapack)。`DimBuffs`: twilight mob HPx3/DMGx2、skyreach HPx2/DMGx3
- 核反応炉: heat>=100 でメルトダウン(爆発+EFP+500)。水隣接で冷却
- 武器/魔法: GunItem(矢弾), SpellItem, AbilityItem(職業ゲートMP消費)。26.3 は武器タグ判定 (SwordsItem無し、ItemTags.SWORDS 等)
- 災害: `/bcn disaster trigger|threshold`
- 一括採掘: スニーク+ツール破壊で発動、`/bcn vein <pos>` でテスト
- 経済: 6種コイン、fake deposit/withdraw

## ビルド・デプロイ (app/)

- MOD 本体: `app/` = 単一統合 Fabric MOD `buildcraftneo` (Mojmap。MC 26.3 は非難読化のため `dev.architectury.loom-no-remap` を使用。fabric-loom + mappings は不可)
- ビルド&デプロイ一括: `scripts/build-and-deploy.sh --restart` (app コンテナ内で `./gradlew build` し jar を `/data/mods/` へコピー&再起動)
- 手動ビルド: `docker exec -e JAVA_HOME=/home/developper/tools/jdk-25 -e PATH=/home/developper/tools/jdk-25/bin:$PATH mc_build_craft_neo_app bash -c 'cd /app && ./gradlew build --no-daemon -x javadoc'`
- testserver の mods: `fabric-api-0.165.0+26.3.jar` (Modrinth から手動配置済み) + `buildcraftneo-*.jar`
- Java 25 必須 (operationer コンテナに Java は無い。app コンテナ内 `/home/developper/tools/jdk-25` に Temurin 25、`tools/gradle-9.8.2` をインストール済み)
- ログ確認: `docker logs mc_build_craft_neo_testserver`
- RCON テスト: MCP `testserver` 経由 (`execute_command` など)
