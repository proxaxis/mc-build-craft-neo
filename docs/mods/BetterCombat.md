# Better Combat — MOD リファレンス

> **原作**: [Better Combat (by Daedelus / ZsoltMolnarrr)](https://www.curseforge.com/minecraft/mc-mods/better-combat-by-daedelus)
> **リポジトリ**: https://github.com/ZsoltMolnarrr/BetterCombat （ライセンス ARR）
> **対応**: Fabric / Forge / NeoForge, クライアント＋サーバ, MC 1.18.2 〜 26.x
> **依存**: Cloth Config, playerAnimator, MixinExtras

## 概要

**Minecraft Dungeons 風の近接戦闘システム**にバニラの戦闘を作り替える MOD。
アイテムを追加するのではなく、「武器の振り方・当たり判定・コンボ」そのものを定義し直す戦闘フレームワークで、
すべての武器挙動は **JSON データファイル（weapon_attributes）** で宣言される。
他 MOD 製の武器にもプリセットや独自 JSON を割り当てるだけで対応できる設計（JSON API）になっており、
Simply Swords / MC Dungeons Weapons / Mythic Metals など多数の武器 MOD が公式連携している。

設計方針:
- Minecraft Dungeons の戦闘体験の再現
- 他 MOD が上に載せられる汎用武器基盤の提供
- MOD 固有コンテンツ（独自武器など）は追加しない（競合回避）

## 特徴

### 主要機能

- **武器コンボ**: 連続攻撃すると武器固有の攻撃シーケンスが順に出る。各段でダメージ倍率・リーチ・判定角度・アニメ・音が個別に違う。最後まで行くと先頭にループ。一定時間無操作（`combo_reset_rate × 武器クールダウン`、デフォルト 3 倍）でコンボリセット。
- **専用攻撃アニメーション**: 片手・両手・二刀流それぞれの攻撃モーションと待機ポーズを同梱（playerAnimator ベース）。
- **正確な振り判定**: カーソル先の点ではなく、武器の軌跡を **OBB + SAT** で立体判定。三人称視点でも当たる。
- **アップスイング（溜め）**: クリックから実ヒットまで武器クールダンの一定割合（各技の `upswing` 値 × サーバ設定 `upswing_multiplier` 0.5）の溜め動作が入り、実在感のある間合いになる。
- **一振りで複数ヒット**: 判定ボックスに入った敵はまとめて巻き込む。
- **武器属性はサーバ→クライアント同期**: サーバ側 JSON がそのまま全クライアントに効く。

### 補助機能

- **二刀流**: 片手武器どうしを両手に持つと、攻撃キーで左右交互に攻撃。攻撃速度 **+20%**（`dual_wielding_attack_speed_multiplier` 1.2、サーバ設定可）。
- **両手武器**: `two_handed` 指定の武器を持つ間はオフハンドが完全に無効化。弓・クロスボウ用プリセットもあり。
- **Sweeping Edge 作り替え**: 複数ヒットした振りは追加ターゲットごとにダメージ減衰（最大 -50%、`reworked_sweeping_*`）。Sweeping Edge エンチャントで全員にフルダメージ。バニラの掃討はデフォルト無効。
- **攻撃長押し**: 攻撃キー押しっぱなしでクールダウンごとに自動連打（クライアント設定）。
- **フェイント**: アップスイング中に攻撃キャンセル可能（クライアント設定）。
- **草貫通（swing thru grass）**: 草など硬度 0 ブロックを壊さず攻撃優先（クライアント設定）。
- **武器での採掘を抑止**する設定、**ヒットボックス内ターゲット表示**設定（クライアント）。
- **攻撃中の移動速度減少**: デフォルト ×0.5（サーバ設定、滑らか適用、騎乗中はデフォルト無効）。
- **高速攻撃のノックバック減衰**: クールダウン 12.5t 未満の連打はノックバック比例減少。
- **関係性フィルタ**: プレイヤー/村人/ゴーレムは `NEUTRAL`（直視しないと斬れない）など、流れ斬りが誰に当たるかを細かく設定可能。

## できること（プレイヤー視点）

- 武器を持って攻撃するだけで自動的に専用モーション＋範囲判定になる（有効化操作は不要）。
- 連続攻撃でコンボが進み、技ごとに間合い・火力・範囲が変わる。
- 片手武器 2 つで二刀流。両手剣はオフハンドを封じる代わりに大振り＆高火力。
- 三人称視点でも正確に当たる。
- アイテムのデータコンポーネントで任意アイテムにプリセットを付けられる:
  `/give @p minecraft:wooden_sword[bettercombat:preset_id="bettercombat:claymore"]`

## 技（アタック）の仕組み

各武器の `attacks` 配列 = コンボ。1 要素が 1 技で、以下のプロパティを持つ:

| プロパティ | 意味 |
| :--- | :--- |
| `conditions` | 発動条件（AND）。満たさない技はスキップ |
| `hitbox` | 判定形状: `FORWARD_BOX`（正面直線）/ `HORIZONTAL_PLANE`（水平面=薙ぎ払い）/ `VERTICAL_PLANE`（垂直面=縦斬り・振り下ろし） |
| `damage_multiplier` | その段のダメージ倍率（1.3 = +30%） |
| `angle` | 視線中心の判定角度（度）。0 = 角度チェック無し |
| `upswing` | クールダウンの何割でヒットさせるか（0.5 が標準） |
| `range_multiplier` / `movement_speed_multiplier` | その段だけのリーチ/移動速度倍率 |
| `animation` / `swing_sound` / `trail_particles` | 演出 |

発動条件 `Condition`: `NOT_DUAL_WIELDING` / `DUAL_WIELDING_ANY` / `DUAL_WIELDING_SAME` / `DUAL_WIELDING_SAME_CATEGORY` / `NO_OFFHAND_ITEM` / `OFF_HAND_SHIELD` / `MAIN_HAND_ONLY` / `OFF_HAND_ONLY` / `MOUNTED` / `NOT_MOUNTED`

### 同梱アニメーション（技モーション）一覧

| 系統 | アニメーション ID（`bettercombat:`） |
| :--- | :--- |
| 片手斬撃 | `one_handed_slash_horizontal_left` / `right`、`one_handed_slash_switch_blade_left` / `right`（剣先切替斬り）、`one_handed_swipe_horizontal_right`、`one_handed_uppercut_right` |
| 片手突き | `one_handed_stab`、`one_handed_stab_mounted`（騎乗突き） |
| 片手打撃 | `one_handed_punch`、`one_handed_slam`、`one_handed_slam_instant` |
| 両手斬撃 | `two_handed_slash_horizontal_left` / `right`、`two_handed_slash_vertical_left` / `right` |
| 両手突き | `two_handed_stab_left` / `right` |
| 両手打撃 | `two_handed_slam`、`two_handed_slam_heavy` |
| 回転 | `two_handed_spin`（360° 薙ぎ） |
| 二刀流 | `dual_handed_slash_cross` / `uncross`（交差斬り）、`dual_handed_stab`（両手持ち突き） |
| 待機ポーズ | `pose_one_handed_backwards`、`pose_two_handed_sword` / `katana` / `heavy` / `polearm` / `scythe` / `bow` / `crossbow` |

## 武器プリセット別 全コンボ一覧

`bettercombat:` 名前空間の同梱プリセット全 33 種（弓系 4 種は攻撃なし・ポーズ専用）。
「倍率」= damage_multiplier、「角度」= 判定角度、「判定」= hitbox（横=HORIZONTAL_PLANE / 縦=VERTICAL_PLANE / 前=FORWARD_BOX）、リーチは `range_bonus`。

### 片手武器

| プリセット | リーチ | コンボ（順に出る技） |
| :--- | :--- | :--- |
| `sword` | ±0 | ① 横斬り右（横・120°・×1.0）→ ② 横斬り左（横・120°・×1.0）→ ③ 突き（前・×1.0） |
| `cutlass` | -0.25 | sword 継承の 2 段版: ① 横斬り右 → ② 横斬り左（横・120°・×1.0） |
| `coral_blade` | -0.25 | ① 横斬り右 → ② 横斬り左（横・120°・×1.0）→ ③ アッパーカット（縦・120°・×1.0） |
| `dagger` | -0.5 | ① 横斬り右 → ② 横斬り左（横・150°・×0.9）→ ③ 両手突き（前・×1.4）※③は同カテゴリ二刀流+メインハンド時のみ |
| `rapier` | ±0 | ① 横斬り左（横・90°・×0.9）→ ② 突き（前・×1.1） |
| `sickle` | -0.25 | ① 横斬り右 → ② 横斬り左（横・120°・×0.9）→ ③ 交差斬りクロス（前・×1.2）→ ④ 交差斬りアンクロス（前・×1.3）※③④は同カテゴリ二刀流時のみ（③メイン/④オフハンド） |
| `mace` | -0.25 | ① 横打ち右 → ② 横打ち左（横・150°・×0.8）→ ③ 振り下ろし（縦・90°・×1.4） |
| `vanilla_mace` | -0.25 | ① 即座の振り下ろし（前・90°・×1.0、upswing 0.1）— バニラ mace 専用 |
| `axe` | -0.25 | ① 横斬り右 → ② 横斬り左（横・90°・×1.0、upswing 0.3 = 素早い出） |
| `pickaxe` | -0.5 | ① 振り下ろし（縦・90°・×1.0）※フォールバックでは pickaxe はブラックリスト済み（採掘優先）。プリセット自体は他 MOD 用に存在 |
| `fist` | -0.75 | ① パンチ（前・×1.0）— 素手/ガントレット系 |
| `claw` | -0.5 | ① パンチ（前・×1.0）→ ② スワイプ（横・120°・×1.0） |
| `wand` | -0.5 | ① 突き（前・×1.0）— staff/wand/sceptre 等に自動適用 |
| `lance` | +1.0 | ① 突き（前・×1.0）— 歩行時 `NOT_MOUNTED` と騎乗時 `MOUNTED` でモーション分岐 |
| `trident` | +0.25 | spear 継承 ① 突き（前・×1.0、片手版）— バニラトライデントにも適用 |

### 両手武器（`two_handed: true`）

| プリセット | リーチ | コンボ |
| :--- | :--- | :--- |
| `claymore` | +0.25 | ① 大横薙ぎ（横・150°・×0.75）→ ② 両手突き（前・×1.0）→ ③ 両手振り下ろし（縦・150°・×1.25） |
| `katana` | ±0 | ① 横薙ぎ右（横・120°・×0.8）→ ② 縦斬り左（縦・120°・×1.2）→ ③ 縦斬り右（縦・120°・×1.2）→ ④ 横薙ぎ左（横・120°・×0.8） |
| `twin_blade` | ±0 | ① 剣先切替斬り右 → ② 同左（横・150°・×1.0） |
| `double_axe` | ±0 | ① 大横薙ぎ（横・180°・×1.0）→ ② 360° スピン（横・360°・×1.0） |
| `heavy_axe` | ±0 | double_axe 継承（待機ポーズのみ heavy に変更） |
| `scythe` | +0.5 | ① 横薙ぎ右 → ② 横薙ぎ左（横・150°・×1.0） |
| `glaive` | +0.5 | ① 縦斬り右（縦・150°・×0.8）→ ② 縦斬り左（縦・150°・×0.8）→ ③ 大横薙ぎ（横・180°・×1.4） |
| `halberd` | +0.75 | ① 縦斬り（縦・150°・×0.8）→ ② 突き（前・×1.2） |
| `spear` | +1.0 | ① 両手突き（前・×1.0）— 長リーチ単発 |
| `staff` | +0.25 | ① 横薙ぎ右（横・160°・×0.8）→ ② 横薙ぎ左（横・160°・×1.0）→ ③ 振り下ろし（縦・120°・×1.2） |
| `battlestaff` | +0.5 | 6 連: ① 横薙ぎ右（横・180°・×0.8）→ ② 横薙ぎ左（横・180°・×1.0）→ ③ 360° スピン（×1.2）→ ④ 振り下ろし（縦・160°・×1.4）→ ⑤ 突き左（前・×0.8）→ ⑥ 突き右（前・×0.8） |
| `hammer` | ±0 | ① 両手振り下ろし（前・×1.0）— 単発ヘビー |
| `anchor` | ±0 | ① 両手ヘビー振り下ろし（前・×1.0、`two_handed_slam_heavy`） |
| `soul_knife` | -0.25 | ① 突き（前・×1.0）— two_handed だがモーションは片手突き |

### 射撃武器（攻撃なし・両手持ちポーズ専用）

| プリセット | 用途 |
| :--- | :--- |
| `bow_two_handed_light` / `bow_two_handed_heavy` | 弓。heavy は `pose_two_handed_bow` 付き |
| `crossbow_two_handed_light` / `crossbow_two_handed_heavy` | クロスボウ。heavy は `pose_two_handed_crossbow` 付き |

## バニラ武器への適用

- **個別 JSON 割当**（`data/minecraft/weapon_attributes/`）: 全剣種（木〜ネザライト）→ `sword`、`mace` → `vanilla_mace`、`trident` → `trident`。
- **フォールバック自動割当**（`fallback_compatibility.json`、攻撃力属性を持つ全アイテムに正規表現マッチ）:
  `claymore|greatsword`→claymore、`warhammer|maul`→hammer、`waraxe|greataxe`→double_axe、`scythe`→scythe、`halberd`→halberd、`glaive`→glaive、`spear`→spear、`lance`→lance、`anchor`→anchor、`battlestaff`→battlestaff、`claw`→claw、`fist|gauntlet`→fist、`trident|javelin`→trident、`katana`→katana、`rapier`→rapier、`sickle`→sickle、`dagger|knife`→dagger、`staff|wand|rod`→wand、`mace|flail`→mace、`axe`→axe、`twinblade`→twin_blade、`cutlass|scimitar|machete`→cutlass、`sword|blade`→sword。
  ※ `pickaxe` はブラックリスト（採掘ツール扱いを維持）。
- **データコンポーネント指定**: `[bettercombat:preset_id="bettercombat:claymore"]` で任意アイテムに強制適用可。

## 設定ファイル

- サーバ: `config/bettercombat/server.json5`（アップスイング倍率・掃討減衰・二刀流倍率・移動速度・関係性フィルタなど。クライアントへ同期）
- フォールバック: `config/bettercombat/fallback_compatibility.json`
- クライアント: Mod Menu 経由（長押し連打・フェイント・草貫通・ヒットボックス表示など）

## BCN での近似メモ

- サーバ側で再現可能な要素: コンボ倍率・判定角度/形状・リーチボーナス・二刀流速度バフ・掃討減衰・カテゴリ条件。
- 実クライアント必須で検証不可の要素（AGENTS.md 既記）: アップスイング演出・攻撃アニメーション・ポーズ・フェイント等のクライアント描画系。
- BCN 側の武器パワー（`PowerWeaponItem` 右クリック発動）は Simply Swords 系の独自実装であり、Better Combat のコンボ自体はサーバ側近似の対象。
