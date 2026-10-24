# Wall-Jump! (TXF) — MOD リファレンス

> **原作**: [Wall-Jump TXF (by jahirxtrap)](https://github.com/jahirxtrap/walljump)
> **対応**: Fabric / Quilt / Forge / NeoForge（マルチローダー）、クライアント主体（動作はクライアント側演算 + サーバへパケット同期）
> **出典**: `master` ブランチのソース（`logic/*.java`・`ModConfig.java`）と README

## 概要

Minecraft に**アクションゲーム風の移動技**を追加する軽量 MOD。壁掴み・壁蹴り・二段ジャンプ・
スプリント/エリトラ加速・段差オートステップを導入する。専用キー **Wall Jump / Wall Cling**
（デフォルト LSHIFT）を使う。バニラ互換で新規コンテンツはなく、純粋に移動挙動だけを改造する。

## 特徴

- 全技がクライアント予測+サーバ同期（`MessageWallJump` / `MessageFallDistance` パケット）
- 壁への掴み・滑りはブロックごとの音・パーティクル付き演出
- 飢餓システム連動（満腹度 0 では発動不可、壁蹴りで疲労消費 `exhaustionWallJump` 0.8）
- 掴める壁をブラックリスト/ホワイトリストで設定可能（`blockList`/`blockListMode`）
- すべての挙動がサーバ同期コンフィグで調整可能
- 任意のエンチャント化パック同梱（下記）

## できること（操作）

| 操作 | 結果 |
| :--- | :--- |
| 壁へ向かってジャンプ中に LSHIFT を押す | **壁掴み（Wall Cling）** — 壁に貼り付く |
| 掴み中に LSHIFT を離しながら移動キー入力 | **壁蹴り（Wall Jump）** — 壁から跳躍 |
| 空中でジャンプキー | **ダブルジャンプ**（設定/エンチャントで有効化） |
| スプリント | **スピードブースト**で加速（設定値による） |
| エリトラ滑空 + スプリントキー+前入力 | **エリトラ加速**（スニークで減速） |
| 段差へ向かって歩く/跳ぶ | **ステップアシスト**で自動乗り越え（フェンス越え含む） |

## 全技（メカニクス詳細）

### Wall Cling（壁掴み）

- 空中で壁に接触した状態で専用キーを押し始めると発動（4 tick 以内の押下のみ有効 = タップ判定）。
- 発動条件: 非地上・非飛行・非液体・手が空いている・満腹度 1 以上・上昇中でない（Δy ≤ 0.1）。
  `onFallWallCling` が false なら高速落下中（Δy < -0.8）は不可。
- 掴み中は水平位置をロック（X/Z 固定）、上昇は 0 にキャンセル。
- **壁滑り**: 掴み開始から `wallSlideDelay`（15 tick）後に -0.1/tick でゆっくり滑り落ちる。
  `stopWallSlideDelay`（72000 tick = 1 時間）を超えると完全に滑り状態が停止する保護あり。
- 掴み/滑り中に壁ブロックのヒット音・破片パーティクルが出る。
- 落下ダメージを滑り速度に応じて軽減（`motionY²×8` をサーバへ送って距離を補正）。

### Wall Jump（壁蹴り）

- 掴み中にキーを離し、移動入力（W/A/S/D）があれば壁から跳躍。上方向速度 `wallJumpHeight`（0.55）
  + 入力方向への水平加速（視線を向けた反対方向に約 0.45 倍の押し出し）。Jump Boost で +0.125/lv。
- キーを離すだけでは跳ばず、移動キーが必須（その場落下も選択可）。
- **再掴み制限**: `allowReClinging` false 時は同一面への連続掴み禁止（別の壁 or 1 ブロック上昇が必要）。
- `maxWallJumps` で 1 滞空あたりの蹴り回数を制限可能（デフォルト 72000 ≒ 実質無制限）。
- 壁蹴りごとに `exhaustionWallJump` の飢餓疲労。

### Double Jump（二段ジャンプ）

- 空中でジャンプキーを押すと追加ジャンプ。回数 = 基本 (`useDoubleJump` 有効時 +1) + ブーツの
  Double Jump エンチャント Lv 分（複数段可）。
- 発動条件: 非地上・非液体・非掴み中・非騎乗・満腹度あり・下降速度が緩やか（Δy < 0.333）。
  `onFallDoubleJump` false で急落下中は不可。
- 発動時に落下距離リセット（着地ダメージ回避）。
- `minFallDistance`（3.0）は落下音/距離補正の閾値。

### Speed Boost（スピードブースト）

- **スプリント中**: `sprintSpeedBoost` + ブーツの Speed Boost エンチャント ×0.25（`speedBoostMultiplier`/2）
  で視線方向へ加速。空中では効果 1/3.125。
- **エリトラ滑空中**: スプリントキー+前入力で `elytraSpeedBoost` + チェストの Speed Boost エンチャント
  ×0.5 で視線方向へ加速（推進）。スニークで 5% 減速。速度が高いと花火パーティクル。
- エリトラ対象は `data/walljump/tags/item/elytras.json` のタグ。

### Step Assist（ステップアシスト / フェンスジャンプ）

- 空中でも段差（`maxUpStep` 以内）を自動で乗り越える（疑似的に onGround 化）。
- スプリント中は水平衝突フラグを潰してフェンス等をまたげる。

## エンチャント（任意パック `walljump:enchantments`）

`enableEnchantments` で有効化すると、各技がエンチャント駆動になる（素手では無効化可能）。

| エンチャント | 部位 | 効果 |
| :--- | :--- | :--- |
| Wall Jump | ブーツ | 壁掴み/壁蹴りを解禁（`useWallJump` false 時に必要） |
| Double Jump | ブーツ | Lv ごとに空中ジャンプ回数 +1 |
| Speed Boost | ブーツ/チェスト | ブーツ=スプリント加速、チェスト=エリトラ加速に加算 |

## 設定（主な項目）

`useWallJump`（既定 on）/ `useDoubleJump`（既定 off）/ `wallJumpHeight` 0.55 /
`wallSlideDelay` 15t / `maxWallJumps` / `allowReClinging` / `autoRotation`（掴み時に壁へ自動で
体を向ける）/ `stepAssist` on / `sprintSpeedBoost`・`elytraSpeedBoost`（既定 0 = 無効）/
`exhaustionWallJump` 0.8 / `blockList` ブラック・ホワイトリスト / `playFallingSound`。

## BCN での近似メモ

- AGENTS.md の通りクライアント移動制御が本質。RCON 検証は不可。
- サーバ側で近似可能な部分: 二段ジャンプの回数管理・飢餓消費・落下ダメージリセット、壁掴みの
  最大継続時間/回数制限ロジック。掴み・滑り・キー入力連動の手触りはクライアント MOD の領域。
