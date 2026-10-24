# Archers + Archers Expansion（RPG Series）— MOD リファレンス

> **原作**: [Archers (RPG Series, by ZsoltMolnarrr)](https://github.com/ZsoltMolnarrr/Archers) +
> [Archers Expansion (by ProfessorFichte)](https://github.com/ProfessorFichte/Archers-Expansion)
> **対応**: Fabric / Forge / NeoForge（RPG Series）、クライアント＋サーバ。**要 Spell Engine**
> **出典**: 各 `1.21.1` ブランチ `data/*/spell/*.json` + `en_us.json` + spell book タグ

## 概要（Archers 本体）

Spell Engine ベースの弓職 MOD。**Archer（レンジャー）**クラスを追加し、弓・クロスボウ・槍の
豊富な武器群と **Archery Manual**（専用スペルブック）に刻まれる 6 つのスキルで遠距離戦を強化する。
弓で敵に刻む **Hunter's Mark** などの独自状態異常も持つ。

## 特徴

- **弓系武器の多段進行**: Shortbow/Longbow/Composite Longbow/Royal Longbow/Crystal・Mechanical 系、
  Heavy/Rapid Crossbow、Sky Crossbow、固有武器 Silver Bow of the Acropolis / Valkyrie Ballista
- **槍（Spear）系**: Flint→Iron→Golden→Diamond→Ruby→Netherite→Aeternium/Holy Spear（投擲可能）
- **矢筒**: Quiver / Hunting Quiver / Battle Quiver / Auto-Fire Hook
- **防具**: Ranger → Archer → Netherite Ranger セット
- **スペル**: Archery Manual 6 スペル。矢を消費するものと無消費のものが混在

## 全技（Archers 本体 — Archery Manual の 6 スペル）

| スペル | CD | コスト | 効果 |
| :--- | ---: | :--- | :--- |
| Barrage | 10 | 矢（非消費） | 複数の矢を短時間に連射 |
| Magic Arrow | 8 | 矢 1 | 貫通する魔法の矢。長く溜めるほど威力・飛距離増 |
| Power Shot | 8 | — | 次の `{cap}` 本の矢に **Hunter's Mark** を付与（被ダメ増・最大 `{cap}` スタック） |
| Entangling Roots | 18 | — | 周辺エリアに根を生やし、敵を `{cloud_duration}` 秒スロウ |
| Rain of Arrows | 15 | — | 対象エリアへ 5 秒間の矢雨。`{impact_range}` 内の敵に各 `{damage}` |
| Spirit Wolf Pack | 45 | — | `{count}` 匹の霊狼を `{duration}` 秒召喚。自分の遠距離ダメージで強化 |

## Archers Expansion（拡張 3 ジョブ）

3 つの派生スペルブックを追加: **Deadeye**（精密狙撃+毒）、**Tundra Hunter**（氷結制御）、
**War Archer**（範囲制圧）。各 6 スペル + 既存技強化パッシブ。
専用防具: Bowman/Deadeye/Tundra/Polar Stalker/Sentinel Archer/Bounty Hunter セット + Netherite 版。

### Deadeye（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Fast Shot | 15 | 次の数ショットに Range Haste（連射速度上昇） |
| Bouncing Arrow | 10 | 敵を跳び回る矢（矢消費） |
| Disabling Shot | 12 | 射撃後に後方ジャンプ、対象を `{duration}` 秒スロウ |
| Choking Gas | 28 | 着弾点に毒ガス雲（半径 5B）を生成+ダメージ |
| Venom Cask | 16 | 毒の樽を投げ、着弾で毒雲が残る（毒継続） |
| Alter Ego | 40 | 分身デコイを出して自分は透明化。デコイは衝突/消滅時に爆発 |

### Tundra Hunter（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Frozen Shot | 10 | 次のショットに冷気付与（対象を徐々に凍結/鈍足） |
| Frozen Pact | 22 | 対象の凍結 tick 比例ダメージ+攻撃力を 5 秒減少 |
| Arctic Volley | 20 | 冷気の 8 連射（矢消費）。命中で凍結 |
| Enchanted Crystal Arrow | 42 | 対象をスタン+凍結させる結晶矢（詠唱 2 秒） |
| Polar Bearward | 60 | シロクマを `{duration}` 秒召喚。遠距離ダメージで強化、遠距離標的に突進ブースト |
| Frozen Fusillade | 24 | 自分の周囲に氷の乱射。近接敵スロウ+味方加速 |

### War Archer（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Dual Shot | 7 | 一度に 2 本の矢を放つ（矢消費） |
| Smoldering Arrow | 12 | 次のショットの着弾点に小爆発+燃焼 |
| Point-Blank Shot | 16 | 至近距離ほど強いノックバック付きの矢（距離比例） |
| Pin Down | 28 | 命中した対象を `{duration}` 秒、移動・ジャンプ不能にする |
| Scorched Earth | 30 | 前方の地面を線状に焼き払う炎の弾 |
| Explosive Barrel | 8 | 爆発樽を設置。被撃/敵接近で大爆発+点火 |

### 強化パッシブ（Passive）

| スペル | 効果 |
| :--- | :--- |
| Improved Arctic Volley | Arctic Volley の CD を短縮 |
| Improved Disabling Shot | Disabling Shot の効果時間を延長 |
| Improved Point Blank Shot | Point-Blank Shot の威力倍率を上昇 |

## BCN での近似メモ

- BCN 実装済み: `marksman` 職業（常時 Speed）、`ability_volley`（5 連射）、`marksman_bow`（矢消費射撃）
- 原作の Hunter's Mark/召喚狼/設置物（樽・バレル）などはサーバ側で再現可能
- スペルの発動自体はアイテム use で近似できるが、チャネル詠唱の視覚・矢の軌道演出はクライアント領域
