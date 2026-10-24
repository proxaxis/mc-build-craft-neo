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

## 未実施（理由つき・検証不可 or 規模外）

- **Better Combat / Wall-Jump / Shield Expansion のパリィ演出**: クライアント移動・アニメーション制御が本質。実装は可能だが実クライアントなしに「動作確認済」と言えないため未着手
- **車両 (HMG)**: 実装は可能だが運転検証に実クライアント操作必須
- **原作パイプ描画の厳密な再現**: BuildCraft 原作は UV マッピングが特殊（テクスチャは帯状で大半が透明）。本実装は動的ベイクドモデル（`FabricBlockStateModel.emitQuads` + `ModelLoadingPlugin.modifyBlockModelAfterBake`）で細パイプ+接続アームを実現済みだが、見た目の細部は原作と異なる。テクスチャは源流の不透明色でベース化した変換版を使用
- **Iron's Spells の詠唱モーション/スキルツリー GUI**: クライアント専用 UI/アニメで RCON 検証不可

## ルール

- **クライアント描画の検証は必ず仮想デスクトップ (Xvfb) を使う**。app コンテナに xvfb+mesa(llvmpipe)+xdotool 導入済み。`DISPLAY=:99 ./gradlew runClient` で DevClient が `testserver:25565` へ自動接続するので、`import -window root /tmp/shot.png` でスクリーンショットを取得して目視確認する。「実装した」だけで「検証した」とは言わない

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
- スペル拡張 (Iron's Spells系): heal/lightning/blink/holy_smite/smoke_bomb/fortify。MP消費・職業ゲート済み、spell_icons テクスチャ使用
- ProjectRed系回路: `timer` (40tick周期4tickパルス、poweredブロック状態+getSignal)、`logic_gate` (facing=出力、右クリックで not/or/and/xor 切替、NBT `mode`、入力=背面or左右)。`execute if block` で検証済み
- 次元ボス拡張: boss_altar は次元判定 — skyreach では Skyreach Guardian (Ravager HP300×DimBuffs=600)、その他 Twilight Knight
- Aether系: `ambrosium_ore` (loot_table で ambrosium_shard 1-3個ドロップ)、`ambrosium_shard` (使用で4HP回復)
- 追加武器 (Simply Swords系): glaive/rapier/claymore/war_dagger/scythe (irons_spellbooks テクスチャ)
- 動的パイプ描画: `PipeBlockStateModel` (クライアント) が emitQuads で隣接パイプ/コンテナを検出し細パイプ+アームを動的生成。Xvfb実機確認済
- 武器パワー (Simply Swords系): 全近接武器が右クリック発動 (PowerWeaponItem)。katana→ダッシュ斬撃, greatsword→衝撃波+ノックバック, spear→ラウンジ, glaive→旋風, rapier→連撃バフ, claymore→範囲打撃+弱体, war_dagger→毒付与, scythe→ドレイン。クールダウン60-100t、MP消費なし
- ProjectRed拡張: logic_gate 全7モード (not/or/and/xor/nand/nor/xnor、右クリック or NBT mode:0-6)、`lamp` (RedstoneLamp系、powered→lit、illumar テクスチャ)
- Cataclysm系ボス: boss_altar は3次元対応 — overworld→Nether Monstrosity (WitherSkeleton HP350 ATK16), skyreach→Skyreach Guardian (Ravager HP300→DimBuffs 600), twilight/その他→Twilight Knight (HP200)
- Aether拡張: `zanite_ore` (loot_table で zanite_gemstone 1-2個) + `zanite_gemstone` (使用でMP+20)
- 銃マガジン (Point Blank系): durability=弾数 (pistol20/rifle30/sniper10/lmg60)、空だと use FAIL
- Player Skills系 XP: `xp` attachment、Mob/boss kill でXP加算 (boss=100, monster=5)、level=xp/100+1、`/bcn fake set xp`・status表示、レベルアップ通知
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

## 追加実装 (commit 以降追記)

- **Logistics Pipes 拡張**: `supplier_pipe` (隣接コンテナを閾値まで自動補充、`/bcn machine supply <pos> <item> <keep>`), `sorting_pipe` (フィルタ一致のみ抽出・隣接コンテナはフィルタ専用宛先、`/bcn machine filter`), `crafting_pipe` (NBTレシピ `in*count -> out*count` をネットワークから材料集めてクラフト、`/bcn machine craft <pos> <in> <ic> <out> <oc>`)。`PipeNetwork` が BFS 共有。配送直後コンテナは 200tick pull 抑制 (`PipeNetwork.recentlySupplied`) で往復防止。**検証済**: sorting は鉄のみ dest へ (金残留)、crafting は鉄ブロックx2 生成、supplier は chest 補充確認
- **Iron's Spells 深化**: `spellbook` (CUSTOM_DATA `spell` index、スニーク+右クリで7スペル切替、MP消費、magic陣営/caster職ゲート)、`scroll_*` 4種 (単発消費スクロール)。MP 自動回復は JobEffects 既存 (mage=3,magic=1/20tick)。**検証済**: heal→MP消費+regen、sneak cycle、scroll消費後 "holds nothing"
- **Aether 拡張**: `gravitite_ore` (4tick毎に上のairへ浮遊移動、loot自壊ドロップ)、`skyroot_log`/`skyroot_planks`、`parachute` (slow_falling 20s + 上昇、耐久12回)。**検証済**: gravitite 設置後元位置が air、parachute fx=slow_falling
- **Twilight 拡張**: boss_altar に NBT `boss` (0=Twilight Knight,1=Naga=Slime size6 HP300,2=Lich=Evoker HP250)。スニーク右クリ or `/bcn machine boss <pos> <idx>`。**構造物**: `/bcn dungeon maze <pos>` (15x15 迷路+祭壇), `/bcn dungeon tower <pos>` (5x5x12 石レンガ塔+頂上祭壇/宝箱)。**検証済**: Naga HP300、Lich=Evoker (DimBuffs twilight で HP750・名 "Twilight evoker" に上書きされる点に注意)、tower/maze の altar 配置
- **Point Blank 拡張**: `smg`(7dmg,cd2,mag40), `shotgun`(5ペレットx4dmg,mag6), `ammo` アイテム。銃の耐久=マガジン残弾、空時に inv の ammo 消費でリロード (次回 use)。**検証済**: sniper 9発→Fail→ammo1消費でリロード→再射
- **RPG series**: 新職業 `paladin`/`priest`/`marksman` (magic/none陣営許可)。アビリティ `ability_barrier`/`ability_consecrate`(アンデッド特効)/`ability_volley`(5連射)、武器 `paladin_mace`(holySmite)/`holy_staff`(PRIEST専用 sanctuary)/`marksman_bow`(SimpleBowItem: invの矢を消費して発射)。JobEffects: paladin=Absorption常時、marksman=Speed、priest=healerオーラ共有。**検証済**: marksman_bow 矢無しFail→矢で発射、各職ゲート/発動
- **GvC 近似**: `/bcn disaster soldiers` → Vindicator x5("Guerrilla Soldier") + Pillager x3("Mercenary Gunner") + 重装ゾンビ("Heavy Trooper")。**検証済**: spawned=9
- **ItemLocks 近似**: `/bcn lock <player|fake> <slot>` でスロットのスタックをスナップショット登録、10tick毎に消失/減少したスロットを復元。attachment `buildcraftneo:locked` (Map<String,ItemStack>) 永続化、`fake status` に `locks=` 表示。fake 検証用 `/bcn fake dropslot <name> <slot>`。**検証済**: diamondスロットを drop→10tickで復元を確認。実クライアントのQキー防止はクライアント制御のため近似
- **ATM**: `atm` ブロック (Lightman's近似)。右クリ=全コイン入金、スニーク右クリ=500MD引出。`/bcn fake atm <name> <pos> [amount]`。**検証済**: gold_coin x3→300MD入金、100MD引出
- **新コマンド**: `/bcn fake giveinv <name> <item> <count>` (inventory投入)、`/bcn fake sneak <name> <bool>`、`/bcn fake dropslot`、上記 machine/lock/dungeon/disaster 系
- クライアント: 動的パイプモデルに supplier/sorting/crafting を追加 (LPテクスチャ: pipe_supplier/pipe_basic/pipe_crafting、不透明化変換済み)

## 追加実装 (commit e1419d0 以降)

- **IC2深化**: `compressor` 機械 (300MJ+60t/処理、スロット0=入力,1-2=出力)。レシピ: uranium_ore→uranium_ingot (炉燃料チェーン完成)、coal_block x8→diamond、ingot x9→block 系(鉄/金/ダイヤ/エメラルド/ラピス/レッドストーン)、quartz x4→block、wheat x9→hay、clay x4→block、snowball x4→block。`uranium_ore` ブロック (IC2 jar テクスチャ、自壊ドロップ)。**検証済**: uranium_ore→uranium_ingot→reactor fuel=1579t、coal_block x8→diamond
- **ProjectRed**: logic_gate mode 7 `rs_latch` (左=S set、右=R reset、set優先、latch状態永続化)。**検証済**: S点灯→入力除去後も保持、Rで消灯
- **Cataclysm フェーズ**: BossManager が追跡ボスに攻撃フェーズを実装 — 80tick毎に半径5のAoEスマッシュ(6dmg+ノックバック)、HP<50%で "Monstrosity Minion" (WitherSkeleton+鉄剣) x2 召喚(1回)、HP<25%で半径8のバースト(12dmg+4s燃焼)。**検証済**: damage→HP50%でミニオン2体、AoEで隣接pigが死亡
- **Twilight 進行ゲート**: boss_altar NBT `unlocked` (twilight次元のみ、ボスindex<=unlocked まで召喚可)。ボス討伐で `unlocked` が index+1 に解放 (BossManager が祭壇位置を追跡)。**検証済**: Lich選択→locked拒否、Knight討伐→unlocked=1→Naga召喚可
- **ATM実GUI**: `AtmMenu` (9入金スロット+player inv、閉じるとコイン→MD変換、非コイン返却) + `AtmScreen` (クライアント)。atm右クリ=GUI、スニーク右クリ=500MD引出(従来通り)。`/bcn shop menu`/`fake menuopen` が atm も対応。**検証済**: fake menuopen→menuclick→menuclose で gold_coin x3→300MD、XvfbでAtmScreen描画確認
- **機械共通GUI**: `MachineMenu` (任意 MachineBlockEntity のスロット≤9を表示、pos距離stillValid) + `MachineScreen` + `/bcn machine menu <pos> <player>`。`fake menuopen` も全機械に対応。**検証済**: fake menuopen で MachineMenu slots=39、Xvfbで MachineScreen(Compressor) 描画確認

## 追加実装 (commit d5d4157 以降)

- **ProjectRed 追加gate**: logic_gate mode 8 `buffer` (back入力をそのまま出力)、mode 9 `d_latch` (back=clock立上りで left=data をラッチ、prevClock永続化)。`powered()` は weak+direct 両信号を読む (insulated_wire入力対応)。**検証済**: d_latch で D=1+clock立上り→Q=1、clock保持中D=0でもQ保持、clock再立上りでQ=0
- **insulated_wire** (ProjectRed transmission近似): POWER 0-15、wire間は-1減衰で伝搬、イベント駆動scheduleTick。`getSignal=0`/`getDirectSignal=POWER` + `isRedstoneConductor=false` で弱電力漏れなし → gateはdirect signalで読めるが隣接lamp/dustには通電しない「絶縁」。テクスチャ=ProjectRed transmission jar由来。**検証済**: lever→wire 15/14/13減衰→buffer gate→lamp点灯、隣接lamp非点灯
- **Point Blank 弾種**: `ap_ammo` (威力x1.5+crit)、`inc_ammo` (焼夷5s燃焼弾)。リロード時に最初に見つかった弾薬を装填し CUSTOM_DATA `rounds`="ap"/"inc"/"" を銃に記録。`fake status` が手持ちの dmg/data を表示するよう拡張。**検証済**: 徹甲弾装填→`data={rounds:"ap"}`、焼夷弾装填→`rounds:"inc"`
- **Twilight ボス固有ドロップ**: Naga討伐→`naga_scale` x4、Lich討伐→`lich_scepter` (20MP消費で雷撃発動する非消耗SpellItem)。twilight_scepter/naga_scale テクスチャ=twilightforest jar由来。**検証済**: type=slime kill→naga_scale item entity、type=evoker kill→lich_scepter、scepter使用でMP100→80
- **機械GUIメータ**: MachineMenu が DataSlot で `mjPct`/`progPct` を broadcastChanges 同期 (`MachineBlockEntity.progressPercent()`、compressor/maceratorがoverride)。MachineScreen に右端MJメータ(橙)+スロット上の進捗バー(緑)を描画。**検証済**: Xvfb で Compressor 画面にMJメータ+鉱石スタック描画を確認

## BASE_MOD_LIST 実装マトリクス (最終棚卸し)

| MOD | 実装状況 |
|---|---|
| ItemLocks | ✅ `/bcn lock <player> <slot>` スロットスナップショット+10tick復元。Qキー抑止はクライアント制御のため近似 |
| Player Skills | ✅ `xp` attachment + kill XP + `/bcn fake set xp` + status表示 |
| Better Combat | ⚠️ 未実施 (クライアントアニメーション制御が本質) |
| Simply Swords | ✅ 近接武器8種+右クリックパワー (PowerWeaponItem) |
| Wall-Jump! | ⚠️ 未実施 (クライアント移動制御) |
| Shield Expansion | ✅ ティア盾5種 (耐久120-800)。ジャストガード演出は近似なし |
| Iron's Spells | ✅ スペル14種+MP+職業ゲート+spellbook(7スペル切替)+scroll4種。詠唱モーション/ツリーGUIは未実施 |
| Paladins & Priests | ✅ paladin/priest職+ability_barrier/consecrate+paladin_mace/holy_staff |
| Archers | ✅ marksman職+ability_volley+marksman_bow (既存archer職+rain_of_arrows等も) |
| Twilight Forest | ✅ twilight次元+DimBuffs+祭壇ボス3種+進行ゲート+迷路/塔+ボス固有ドロップ(naga_scale/lich_scepter) |
| Cataclysm | ✅ overworld祭壇→Nether Monstrosity + フェーズ攻撃(AoE/ミニオン/バースト) |
| The Aether | ✅ skyreach次元+ambrosium/zanite鉱石+gravitite浮遊+skyroot+parachute |
| GvC | ✅ `/bcn disaster soldiers` 兵士レイド (Vindicator/Pillager/重装Zombie近似) |
| HMG | ✅ 銃系で近似 (車両はクライアント運転制御のため未実施) |
| BuildCraft | ✅ engine/quarry/item_pipe/teleport_pipe+MJ系+動的パイプ描画 |
| Logistics Pipes | ✅ request/supplier/sorting/crafting_pipe + PipeNetwork共有BFS |
| Additional Pipes | ✅ teleport_pipe次元横断チャネル0-7 (both/send/receive) |
| IndustrialCraft 2 | ✅ macerator+粉砕dust+compressor+uranium鉱石→ingot→reactor燃料+核メルトダウン (EU体系はMJ近似) |
| ProjectRed | ✅ timer+logic_gate 10モード(not/or/and/xor/nand/nor/xnor/rs_latch/buffer/d_latch)+lamp+insulated_wire。bundled wireは未実施 |
| Vic's Point Blank | ✅ 銃6種+マガジン耐久+弾種3種(通常/徹甲/焼夷)リロード+pellet (アニメーション/アタッチメントは近似なし) |
| Lightman's Currency | ✅ 6種コイン+MD+shop+auction+ATM(GUI付) |

## 検証ノート (追加)

- FakePlayer は `getPlayerList()` に含まれない → tick ベース強制処理 (ItemLocks等) は `TestFakes.all()` も回すこと
- 遠方テストエリアは `forceload add` 必須 (setblock は chunk border 内のみ成功、"That position is not loaded")
- DimBuffs は次元mobの HP/名を上書きする (twilight: HPx3、"Twilight <type>" 名) — `name=` セレクタではなく `type=` で検証
- `inventory.add(stack)` は stack を空にする → 渡した stack の参照は add 後に空 (giveinv の表示は 0x air になるが実際は投入成功)
- スロットindex: 0-8=ホットバー (selected=0 が mainhand 装備と共有)、9+=メイン
- 実クライアントの openMenu 検証: プレイヤーが落下すると stillValid 距離超過で即クローズする → tp先の足元にブロック (fill ~ ~-1 ~) を敷くこと。「moved too quickly」「standing on air」ログが兆候
- boss damage 検証: RCON の `@e[limit=1]` は実行地点 (ワールドスポーン) からの近距離順 → `execute positioned X Y Z run damage @e[distance=..N]` で対象を限定すること

## 追加実装 (commit 623c234 以降)

- **色付き絶縁ワイヤ** (ProjectRed近似): `insulated_wire_red/green/blue`。`InsulatedWireBlock` に color フィールドを追加し、同色 wire 間のみ -1 減衰で伝搬 (異色/無色とは絶縁)。素の insulated_wire は "none" 色として独立。テクスチャ=既存wireを ImageIO で色相変換。**検証済**: red鎖 14/12 減衰時に隣接 green/none は power=0、green鎖も独立して 14 伝搬
- **IC2 パワードアーマー近似**: `nano_chestplate` (DIAMOND素材) / `quantum_chestplate` (NETHERITE素材) = humanoidArmor 装着可。`ArmorEffects` が20tick毎に全player+fakeの胸スロットを検査し nano=Resistance I、quantum=Resistance II+Fire Resistance を付与 (EU充電は近似せず常時効果)。`/bcn fake equip <name> <item>` で胸スロット装備。**検証済**: fake status fx=resistance / resistance+fire_resistance
- **Twilight 深化**: `zombie_scepter` (25MPでゾンビミニオンx3召喚する非消耗SpellItem、twilightforest jarテクスチャ)、`naga_chestplate` (DIAMOND素材チェストプレート+shapedレシピ `S S/SSS/SSS` of naga_scale)。**検証済**: scepter使用でMP100→75+ミニオン3体、`recipe give` で naga_chestplate レシピ登録確認
- **Cataclysm 遠距離攻撃+構造物**: BossManager が60tick毎に4-24ブロック内のplayerへ SmallFireball 発射 (`fireballVolley`)。`/bcn dungeon arena <pos>` = 半径6のblackstoneアリーナ (gilded上縁/中央祭壇/溶岩濠/東西入口)。**検証済**: overworld祭壇ボスが DevClient (距離8) に SmallFireball 発射→DevClient死亡 (実効確認)、arena の altar/blackstone/lava 配置
- **Point Blank アタッチメント**: `ext_mag`/`scope` アイテム。`/bcn gun attach <player> <ext_mag|scope>` が手持ち銃に適用 (invから1個消費、CUSTOM_DATA `bonus`/`scope`)。ext_mag=空マガジン時に予備マガジン自動交換 (dmgリセット)、scope=弾速8.0/散布0.1/威力x1.25。**検証済**: smg+ext_mag で dmg39→use→bonus消費dmg=1で再発射、scope:1b タグ確認
- **ATM残高表示**: AtmMenu に DataSlot で `balance` 同期 (broadcastChanges、long→int clamp)、AtmScreen 右上に "Balance: N MD" 描画。**検証済**: Xvfbで "Balance: 777 MD" ライブ更新を確認

## 検証ノート (追加2)

- FakePlayer はエンティティセレクタ/エンティティリストに載らない → 装備検証用に `/bcn fake equip` (胸スロット) を追加。`fake status` の `fx=` でエフェクト確認可
- DevClient が死亡すると attachment (MD等) はリセットされる (コピー未設定) — ATM検証等で生存が前提の値は死亡に注意
- Xvfb の GUI クリックは `xdotool search --name Minecraft windowactivate` 後に mousemove+click。死亡画面のボタン位置は "Respawn"=中央上段、"Title Screen"=下段 (誤クリックで切断確認画面が出る → 右側 "Respawn" を押す)
- ボス範囲攻撃の実機検証は DevClient を survival にして近接配置する (4-24ブロック)。fireball は飛翔時間が短いので実体検出は `kill @e[type=small_fireball]` の "Killed" 出力で確実に確認

## 追加実装 (commit 12f7823 以降)

- **ProjectRed bundled wire** (近似: 16ch→4ch): `bundled_wire` ブロック。`MASK` 0-15 (bit0=無色,1=red,2=green,3=blue)。同色 insulated wire 経由でのみ入出力 — 絶縁ワイヤの `BUNDLED` フラグが「バス駆動」を示し、バス側はそれを入力として再読みしない (フィードバックループによる自己ラッチ防止)。tick 時に接続された全 bundled wire を BFS してネットワーク mask = 全外部入力の OR を全ノードに適用 (相互ORによるラッチを解消)。**検証済**: lever→red wire→bundled x2→red wire(p15,bundled=true)、green tap は0、レバーOFFで全クリア (ラッチなし)
- **ProjectRed frame + frame_motor**: `frame` (ラティス状ブロック)、`frame_motor` (FACING 方向)。レッドストーン立上りまたは `/bcn machine nudge <pos>` で、正面の接続フレーム構造 (BFS、上限64) + フレームに接する持ち運び可能ブロック (BEなし/非流体/破壊可) を1マス移動。フレームは接触した全ブロックを掴む = 床上では床を引きずれず移動不可 (原作同様)。**検証済**: 浮遊フレーム2個+接する石ブロックが1マス移動、2回目は構造がモーターから離れたため "no frame / blocked" (正しい動作)
- **IC2 フルスーツ近似**: nano/quantum の helmet/leggings/boots 追加 (humanoidArmor、部位別ArmorType)。`ArmorEffects` が全スロットを検査: nano=helm水中呼吸/leg速度I/boots跳躍I/chest耐性I、quantum=helm暗視+水中呼吸/leg速度II/boots跳躍II/chest耐性II+耐火。`/bcn fake equip` は EQUIPPABLE コンポーネントから正しいスロットへ装備。**検証済**: quantumフルセット装備で fx=fire_resistance,night_vision,resistance,jump_boost,speed,water_breathing の6種確認
- **IC2 solar_panel** (近似): `solar_panel` 機械 = 日照時 (canSeeSky+isBrightOutside+非雨天) に 1 MJ/t 発電し隣接機械へ push。**検証済**: time set day で隣の macerator mj=122 に蓄積
- **Twilight 追加**: `naga_leggings` (naga_scale レシピ付)、`lich_crown` (GOLD素材ヘルメット、Lich討伐ドロップ)。**検証済**: twilight祭壇 Lich 撃破で lich_crown+lich_scepter のアイテムエンティティ確認
- **Point Blank アタッチメント追加**: `grip` (散布x0.5)、`suppressor` (威力x0.9 + 矢を setSilent + `bcn_suppressed` タグ)。`/bcn gun attach <p> grip|suppressor`。**検証済**: 装着で `data={grip:1b,suppressor:1b}`、発射 Success
- **shop 取引履歴**: `ShopBlockEntity` に直近10件の販売履歴 (揮発性、非永続化)。`/bcn shop history <pos>`。**検証済**: fake購入後に "buyer1 bought 1xminecraft:diamond for 500 MD" 出力
- **クライアント**: 新ブロック全般を Xvfb で描画確認 (solar_panel/bundled_wire/frame/frame_motor/色付きwire)

## 検証ノート (追加3)

- **スポーンチャンク外は tick しない**: `setworldspawn` でスポーンが動くと、setblock/fill は成功するがブロックの scheduled tick が走らず「ワイヤが反応しない」に見える。`forceload query` で対象チャンクが forceload 済みか確認し、未ロードなら spawn 近くか forceload 下でテストすること。今回 spawn を 620,64,620 に移した際 x=120 付近が非tick領域になり全ワイヤが沈黙した
- **26.3 の絶縁ワイヤ自己ラッチ対策**: ワイヤ→導体(床)→弱電力漏れ→ワイヤ が自己持続ループになるため、`getDirectSignal` は導体ブロックへ 0 を返す (読み手 = `pos.relative(direction.getOpposite())`)。ゲート/モーターは導体なので読めなくなる → `LogicGateBlockEntity.powered()`/`FrameMotorBlock` は instanceof で wire の POWER を直接読む
- bundled_wire は「バス全体=全外部入力のOR」をネットワーク単位で再計算する (局所ORだと相互ラッチ)。絶縁ワイヤの `BUNDLED` フラグで bus 出力を入力へ逆流させない
