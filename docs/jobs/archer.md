# Archer — 弓使い

> **陣営**: 魔法技術陣営 / **役割**: 遠距離狙撃（単体撃破・制圧射撃）
> **源流MOD**: Archers（RPG Series）+ Archers Expansion
> **MOD詳細**: [Archers.md](../mods/Archers.md)

魔法陣営で唯一「弓・クロスボウ・槍」の使用が許された遠距離火力職。
源流 MOD の設計では、Archer は **Spell Engine ベースの弓術職** ——
専用スペルブック **Archery Manual** の 6 スペルに加え、拡張では
**Deadeye（精密狙撃+毒）/ Tundra Hunter（氷結制御）/ War Archer（範囲制圧）** の 3 派生スタイルがある。

## できること

- **弓・クロスボウ・槍の使用**: 魔法陣営で飛び道具を使えるのは Archer だけ。遠距離制圧はあなたの独占領域。
- **Archery Manual の 6 スペル**: 連射・魔法矢・デバフ・範囲攻撃・召喚まで揃う基本セット。
- **拡張 3 スタイル**: Deadeye / Tundra Hunter / War Archer の専用スペルブック（各6スペル）。
- **Hunter's Mark**: 弓で敵に刻む被ダメ増デバフ（スタック制）。
- **豊富な装備進行**: 弓・クロスボウ・槍・矢筒・防具の多段階ティア。

## できないこと・制約

- **接敵が弱点**: 近接特化の手段がなく、取り付かれると不利。間合いの管理が生命線。
- **近接武器は護身用のみ**: 本領は弓。前線は Warrior/Guardian に任せる。
- スペルには矢消費・クールダウンがあるものが混在 — 矢筒と残弾管理も立ち回りの一部。

## スペルシステム（Archers）

- **スペルブック**: Archery Manual に 6 スペルを刻んで切替発動（Spell Engine ベース）。
- **コスト**: スペルごとに矢消費/非消費が異なる + 個別クールダウン。
- **Hunter's Mark**: 特定スペルで付与する被ダメ増デバフ。スタックして火力を伸ばす。
- **装備進行**:
  - 弓: Shortbow / Longbow / Composite Longbow / Royal Longbow / Crystal・Mechanical 系
  - クロスボウ: Heavy / Rapid Crossbow / Sky Crossbow
  - 槍: Flint → Iron → Golden → Diamond → Ruby → Netherite → Aeternium / Holy Spear（投擲可）
  - 矢筒: Quiver / Hunting Quiver / Battle Quiver / Auto-Fire Hook
  - 防具: Ranger → Archer → Netherite Ranger セット
  - 固有武器: Silver Bow of the Acropolis / Valkyrie Ballista

## 全技① — Archery Manual（基本 6 スペル）

| スペル | CD | コスト | 効果 |
| :--- | ---: | :--- | :--- |
| Barrage | 10 | 矢（非消費） | 複数の矢を短時間に連射。**単体火力の主力** |
| Magic Arrow | 8 | 矢 1 | 貫通する魔法の矢。長く溜めるほど威力・飛距離増。**単発狙撃** |
| Power Shot | 8 | — | 次の `{cap}` 本の矢に **Hunter's Mark** を付与（被ダメ増・最大 `{cap}` スタック）。ボス戦の前置き |
| Entangling Roots | 18 | — | 周辺エリアに根を生やし、敵を `{cloud_duration}` 秒スロウ。**足止め制御** |
| Rain of Arrows | 15 | — | 対象エリアへ 5 秒間の矢雨。`{impact_range}` 内の敵に各 `{damage}`。**面制圧** |
| Spirit Wolf Pack | 45 | — | `{count}` 匹の霊狼を `{duration}` 秒召喚。自分の遠距離ダメージで強化。**盾・囮・追加火力** |

## 全技② — Archers Expansion（3 派生スタイル）

### Deadeye — 精密狙撃 + 毒（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Fast Shot | 15 | 次の数ショットに Range Haste（連射速度上昇） |
| Bouncing Arrow | 10 | 敵を跳び回る矢（矢消費）。集団への跳弾 |
| Disabling Shot | 12 | 射撃後に後方ジャンプ、対象を `{duration}` 秒スロウ。**距離を取りながら足止め** |
| Choking Gas | 28 | 着弾点に毒ガス雲（半径 5B）を生成+ダメージ |
| Venom Cask | 16 | 毒の樽を投げ、着弾で毒雲が残る（毒継続） |
| Alter Ego | 40 | 分身デコイを出して自分は透明化。デコイは衝突/消滅時に爆発。**緊急離脱+反撃** |

### Tundra Hunter — 氷結制御（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Frozen Shot | 10 | 次のショットに冷気付与（対象を徐々に凍結/鈍足） |
| Frozen Pact | 22 | 対象の凍結 tick 比例ダメージ+攻撃力を 5 秒減少 |
| Arctic Volley | 20 | 冷気の 8 連射（矢消費）。命中で凍結 |
| Enchanted Crystal Arrow | 42 | 対象をスタン+凍結させる結晶矢（詠唱 2 秒） |
| Polar Bearward | 60 | シロクマを `{duration}` 秒召喚。遠距離ダメージで強化、遠距離標的に突進ブースト |
| Frozen Fusillade | 24 | 自分の周囲に氷の乱射。近接敵スロウ+味方加速 |

### War Archer — 範囲制圧（6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Dual Shot | 7 | 一度に 2 本の矢を放つ（矢消費） |
| Smoldering Arrow | 12 | 次のショットの着弾点に小爆発+燃焼 |
| Point-Blank Shot | 16 | 至近距離ほど強いノックバック付きの矢（距離比例）。**接敵時の反撃** |
| Pin Down | 28 | 命中した対象を `{duration}` 秒、移動・ジャンプ不能にする |
| Scorched Earth | 30 | 前方の地面を線状に焼き払う炎の弾 |
| Explosive Barrel | 8 | 爆発樽を設置。被撃/敵接近で大爆発+点火。**トラップ設置** |

### 強化パッシブ（Passive）

| スペル | 効果 |
| :--- | :--- |
| Improved Arctic Volley | Arctic Volley の CD を短縮 |
| Improved Disabling Shot | Disabling Shot の効果時間を延長 |
| Improved Point Blank Shot | Point-Blank Shot の威力倍率を上昇 |

## 立ち回りガイド

- **最長射程を常に維持**: 敵の攻撃が届かない距離から Magic Arrow / Barrage で削るのが基本。高所・障害物の上など、射線が通りにくい位置を確保する。
- **Hunter's Mark を先に刻む**: Power Shot で被ダメ増デバフを乗せてから主力射撃へ。ボス戦では必須のルーチン。
- **足止めが命綱**: Entangling Roots / Frozen Shot / Disabling Shot / Pin Down で距離を作り続ける。取り付かれたら Disabling Shot（後方ジャンプ+鈍足）か Alter Ego（透明化）で離脱。
- **優先ターゲット**: スケルトン等の射撃敵・呪術系・集団の後衛。前衛に任せる敵と自分で潰すべき敵を瞬時に選別する。
- **召喚は盾にもなる**: Spirit Wolf Pack / Polar Bearward は追加火力であり、自分への攻撃を引き受ける壁でもある。
- **設置型で道を作る**: Explosive Barrel / Venom Cask / Choking Gas / Spirit Wolf Pack の設置物・召喚物で、撤退路や敵の進路をコントロールできる。
- **災害防衛**: 城壁・見張りから襲撃者を遠距離で撃ち抜く最重要火力。ピリジャー（射撃敵）はあなたが最優先で処理。
- **異世界遠征**: 黄昏の森の大型ボスは間合いを取って削りやすい相手。浮島世界では遠距離から安全に浮島間の敵を処理できる。

## お金の稼ぎ方

- 遠距離火力要員としての遠征・ダンジョン同行。
- 災害防衛の対射撃敵担当としての報酬。
- 高級弓・矢筒・固有武器（Silver Bow of the Acropolis / Valkyrie Ballista）の売買。
- ボス戦での削り役として高額報酬分配。

## こんな人に向いている

- エイムと距離管理が好きな人
- 前線に出ずに大ダメージを与えたい人
- 「敵に触らせない」立ち回りの緊張感を楽しめる人
