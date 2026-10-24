# Shield Expansion — MOD リファレンス

> **原作**: [Shield Expansion (by Infernal Studios)](https://github.com/infernalstudios/Shield-Expansion)
> **対応**: Forge / NeoForge / Fabric（マルチローダー）、クライアント＋サーバ
> **出典**: `1.21.1` ブランチのソース（`events/ShieldEvents.java`・`config`・`data/*/shields/*.json`）と README

## 概要

バニラの盾をリバランスし、**技術介入型の防御**に変える MOD。
盾にティア（木〜ネザライト）を追加し、2 つの新機構 —— **Off-Guard Cooldown（盾のスタミナ/冷却）**と
**Parry（ジャストガード）** —— を導入する。盾性能は盾ごとの JSON（データパック）で定義され、
他 MOD の盾にも自動適用される設計。

## 特徴

- **盾のティア化**: 木・金・鉄・ダイヤ・ネザライトの盾を追加（耐久もティア比例）
- **Off-Guard Cooldown**: 攻撃を受けるたびにスタミナを消費し、尽きると盾がクールダウン（使用不能）に。
  盾を自分から下ろした場合もクールダウンに入る（貼りっぱなし防止）
- **Parry（パリィ）**: 被弾直前に盾を上げると発動。近接攻撃者をノックバック+ティア比例の反撃ダメージ
  （**Parry-Bash**）、飛び道具は 100% 反射。パリィ成功時はスタミナを消費しない
- **爆発処理**: 爆発は `blastResistance` 分だけ軽減。残った威力に応じてスタミナ消費・クールダウン・
  強制解除が段階発生。`ADVANCED_EXPLOSIONS` 有効時は軽減分が素通りして partial blast ダメージを受ける
- **ブロック中の移動速度調整**: バニラの鈍足を緩和（`speedFactor` ×4 を MOVEMENT_SPEED に乗算）
- **射線判定**: 盾は正面（視線方向 ±90°）からの飛び道具のみ防ぐ。貫通矢（Piercing）は貫く
- **HUD インジケータ**: クロスヘア横に盾のスタミナ/クールダウン表示
- **全盾に適用**: `shields/*.json` がデータドリブン。他 MOD の盾にも自動適用、設定の白/黒リストで制御可

## できること（プレイヤー視点）

- 右クリック長押しで盾を掲げてガード（バニラと同じ操作、ただし耐久・スタミナあり）
- **攻撃モーション中はガードを始められない**（`attackAnim == 0` が条件）。攻撃と防御の切替がテンポを生む
- 被弾直前に盾を上げる = パリィ。タイミングが決まればノーダメ+反撃
- 盾を下ろす/持ち替えると Off-Guard 冷却（無防備な間隙）
- 高ティア盾ほどパリィ反撃が強いが、パリィ受付時間は短く、クールダウンは長い（ハイリスクハイリターン）

## 盾ティア一覧（`shieldexp:` + バニラ盾）

| 盾 | 耐久 | CD(tick) | 速度補正 | パリィ窓 | パリィ反撃 | スタミナ | 爆発耐性 |
| :--- | ---: | ---: | ---: | ---: | :--- | ---: | ---: |
| `minecraft:shield`（バニラ盾） | 336 | 30 | 0.60 | 5t | 0% + 固定1 | 2 | 10% |
| `wooden_shield` | 55 | 20 | 0.78 | 5t | 0% + 0 | 2 | 0% |
| `golden_shield` | 32 | 25 | 0.90 | 6t | 0% + 0 | 2 | 0% |
| `iron_shield` | 165 | 30 | 0.60 | 5t | 20% + 2 | 3 | 20% |
| `diamond_shield` | 363 | 45 | 0.38 | 4t | 35% + 4 | 5 | 30% |
| `netherite_shield` | 607（耐火） | 60 | 0.25 | 3t | 50% + 6 | 7 | 50% |
| `griefer_shield`（要 Savage and Ravage） | 282 | 30 | 0.60 | 5t | 10% + 3 | 4 | **100%** |
| `paragon_shield`（要 Mining Master、ダイヤ盾から鍛造） | 640 | 35 | 0.60 | 4t | 70% + 5 | 5 | 40% |
| 未登録の他 MOD 盾 | — | 30 | 0.65 | 5t | 10% + 1 | 2 | 0% （`default.json`） |

パリィ反撃 = 受けたはずのダメージ × `parryDamage` + `flatDamage`。木・金は反撃なし（弾きのみ）。

## 全技（メカニクス詳細）

### ガード（基本動作）

- 盾を持って右クリック: ガード状態へ。発動時にパリィ窓が `parryTicks` だけ開く
- ガード成立中は正面からの近接・飛び道具を無効化（バニラ準拠の方向判定）
- ガード中は移動速度に `4.0 × speedFactor` の乗算補正（バニラより速く動ける）
- 攻撃モーション中（attackAnim > 0）はガード不可・即座に解除される → 攻撃後の隙に被弾しやすい

### Off-Guard Cooldown（スタミナ + クールダウン）

- ガードで受けた通常ヒット: ダメージ ≤5 でスタミナ -2、>5 で -3、飛び道具は -1
- スタミナ消費が盾の `stamina` に達すると **Off-Guard**: 盾が `cooldownTicks` 使用不能になり強制解除
- 自分から盾を下ろした場合（`stashingCooldown`）も同じクールダウンが付く
- `lenientStamina` でスタミナ量 2 倍にできる

### Parry（パリィ）

- ガード開始から `parryTicks`（3〜6tick = 0.15〜0.3 秒、`lenientParry` で 2 倍）以内に攻撃を受けると成立
- 近接: 攻撃者をノックバック（0.55+0.5 の 2 段）+ `parryDamage×被ダメ+flatDamage` の反撃（sting ダメージ）
- 飛び道具: 弾を速度そのままに 180° 反射（撃ち返し）
- パリィ時の耐久消費は常に 1、スタミナ消費なし → 連続パリィが決まれば盾は削れない
- 失敗（窓外）: 耐久を被ダメ分消費しスタミナ消費 → スタミナ切れで Off-Guard

### 爆発（特殊処理）

- 爆発は `blastResistance` を引いた残り × 元ダメージで判定（griefer は完全無効）
- パリィ窓中の爆発: 威力 ≥15 で `cooldownTicks×damageFactor` の CD+解除、≥10 で半分の CD+解除
- 窓外の爆発: 威力 >5 で即 CD（≥10/≥15 でさらに延長）
- `ADVANCED_EXPLOSIONS` on: 軽減しきれなかった分が `partialblast` ダメージとして素通りする

## 設定

`GENERAL_COOLDOWN` / `STASHING_COOLDOWN`（下ろし時 CD）/ `SPEED_MODIFICATION` /
`LENIENT_PARRY`・`LENIENT_STAMINA`（緩和モード）/ `ADVANCED_EXPLOSIONS` /
`ITEM_ONLY_MODE`（全部無効化して他盾 MOD と共存）/ 盾ホワイト・ブラックリスト /
HUD・ツールチップ表示の個別 ON/OFF。

## BCN での近似メモ

- パリィ窓・ノックバック・飛び道具反射・スタミナ/CD は**すべてサーバ側で再現可能**。
  BCN なら `LivingEntityAccess` 相当を attachment + use-tick フックで実装できる。
- 「攻撃モーション中はガード不可」は attackAnim の取得がクライアント依存なため、サーバ近似では
  スイング tick から推定する必要あり。
- AGENTS.md の通り原作パリィの**見た目演出**はクライアント領域。動作ロジック自体は実装可。
