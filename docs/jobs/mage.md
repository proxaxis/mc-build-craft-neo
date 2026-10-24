# Mage — 魔道士

> **陣営**: 魔法技術陣営 / **役割**: 広範囲術者（範囲殲滅・属性砲台）
> **源流MOD**: Iron's Spells 'n Spellbooks
> **MOD詳細**: [IronsSpells.md](../mods/IronsSpells.md)

膨大なマナを駆使し、9 つの魔法学派・100 以上のスペルで敵集団を一掃する砲台役。
源流 MOD の設計では、魔道士は**最も深いリソース管理を要する職** ——
マナ・詠唱時間・クールダウン・スペルレベル・学派パワーの全てを組み合わせて火力を設計する。

## できること

- **全 9 学派 111 スペルへのアクセス**: Blood/Ender/Evocation/Fire/Holy/Ice/Lightning/Nature/Eldritch。
- **スペルブック運用**: スロット数付きの魔法書にスペルを刻み、R キー等で切替詠唱。
- **スクロール作成**: Scroll Forge / Inscription Table で紙+インクを刻み、単発消費スクロールの量産・レベルアップ。
- **学派装備で火力設計**: 学派ごとの法衣・リング・オーブで特定属性の威力を最大化。
- **武器スロットスペル**: 一部武器にスペルホットバーが内蔵される。

## できないこと・制約

- **弓・クロスボウは装備不可**（Archer のみ）。
- **極めて紙装甲**: 防御面のボーナスを持たず、接敵されると一瞬で沈む。前衛（Guardian/Warrior）の後方で戦うのが鉄則。
- **マナが命**: 全火力がマナ依存。詠唱中は無防備 — 詠唱時間の長い大技は味方のカバーが前提。
- **スペルレベル制**: スペルは低レベルから始まり、スクロールや装備で強化していく。

## 魔法システム（Iron's Spells 'n Spellbooks）

| 要素 | 内容 |
| :--- | :--- |
| **マナ（Mana）** | スペル詠唱で消費、時間経過で自動回復。装具・属性で最大値/回復力を強化 |
| **詠唱方式（CastType）** | instant（即発）/ long（溜め詠唱）/ continuous（継続詠唱）/ charge など、スペルごとに異なる |
| **学派 × スペルパワー** | 装備やポーションで学派ごとの火力を伸ばす。敵側にも学派別の弱点/耐性がある |
| **スペルレベル** | スペルごとに最大 Lv10 前後。レベルで威力・効果時間・弾数が伸びる |
| **スペルブック** | スロット数付き魔法書にスペルを刻んで切替詠唱。スクロール（単発消費）もある |
| **スクロール作成** | Scroll Forge / Inscription Table で紙+インクを刻んでスクロール化・複製習得・レベルアップ |
| **武器スロット** | 一部武器にスペルホットバー内蔵 |
| **ステータス効果** | Abyssal Shroud（全攻撃回避）、Evasion（被撃時ワープ）、Guided、Echoing Strikes、Blighted、Chilled、Frostbite 等数十種 |

### 関連コンテンツ

- **ブロック**: Inscription Table、Scroll Forge、Arcane Anvil、Alchemist Cauldron、Portal Frame、Mithril 鉱石、Wisewood 本棚
- **防具**: 学派ごとの法衣セット（Pyromancer/Cryomancer/Electromancer/Cultist/Plagued/Archevoker/Priest/Shadowwalker 等）＋ネザライト系上位装
- **リング/護符**: Mana Ring、Cast Time Ring、Cooldown Ring、Affinity Ring（学派強化）、各種 Ward Ring 等
- **アップグレード**: `*_upgrade_orb` で装備強化（mana/cooldown/学派別）
- **モブ/ボス**: Dead King、Archevoker、Pyromancer、Cryomancer、Priest、Necromancer 等 + 構造物

## 全スペル一覧（学派別・111 種）

### Fire（火 — 13 種）: 範囲殲滅の主学派

| スペル | 効果 |
| :--- | :--- |
| Blaze Storm | ブレイズの嵐で炎の玉をばら撒く |
| Burning Dash | 炎を纏って突進、通過した敵を燃やす |
| Fireball | 着弾で爆発する火球を発射 |
| Firebolt | 速い小火球を連射 |
| Fire Breath | 前方へ炎を吐き続ける継続詠唱 |
| Magma Bomb | 着弾でマグマを撒く重い投擲弾 |
| Wall of Fire | 火の壁を設置して通行を焼く |
| Heat Surge | 自分を強化しながら周囲を燃やすバフ |
| Flaming Strike | 武器に炎を纏わせて範囲斬撃 |
| Scorch | 対象を内側から焼く単体高火力 |
| Flaming Barrage | 炎弾を扇状に連続発射 |
| Fire Arrow | 炎の矢を放つ |
| Raise Hell | 地面を割って業火の柱を噴き上げる範囲技 |

### Ice（氷 — 12 種）: 制御・足止めの主学派

| スペル | 効果 |
| :--- | :--- |
| Cone of Cold | 前方へ冷気の扇を放ち敵を冷やす |
| Frost Step | 氷上走行＋ダッシュ移動 |
| Ice Block | 氷ブロックを出して足場/盾にする |
| Icicle | 氷のつららを発射 |
| Summon Polar Bear | シロクマの眷属を召喚 |
| Ray of Frost | 冷凍ビームで継続ダメ+凍結 |
| Frostwave | 自分中心に氷の波を広げる |
| Ice Spikes | 地面から氷の槍を連続で突き上げる |
| Ice Tomb | 対象を氷の墓に封じて行動不能化 |
| Snowball | 強化雪玉を発射 |
| Frostbite | 付与中、完全凍結した敵を倒すと Ice Shadow に変える |
| Blizzard | 吹雪を発生し範囲内を継続凍結 |

### Lightning（雷 — 10 種）: 連鎖・高火力

| スペル | 効果 |
| :--- | :--- |
| Ascension | 上昇して浮遊（近接回避+位置取り） |
| Chain Lightning | 敵間を跳ぶ連鎖雷 |
| Charge | 自分を帯電させ攻撃力・速度上昇 |
| Electrocute | 対象へ直接感電の連続ダメージ |
| Lightning Bolt | 雷を召喚して落雷させる |
| Lightning Lance | 雷の槍を一直線に放つ |
| Shockwave | 着地時に雷の衝撃波を起こす |
| Thunderstorm | 自分の周りに継続落雷する嵐 |
| Ball Lightning | ゆっくり進み敵へ感電させる球電 |
| Volt Strike | 突進して電撃で抜き斬る |

### Ender（エンダー — 16 種）: 移動・召喚・汎用

| スペル | 効果 |
| :--- | :--- |
| Counterspell | 対象の詠唱を中断させるカウンター |
| Dragon Breath | ドラゴンのブレスを扇状に吐く |
| Evasion | 短時間、あらゆる攻撃を受けると自動ワープで回避 |
| Magic Arrow | 魔法の矢を発射 |
| Magic Missile | 誘導する魔法弾を放つ |
| Starfall | 視点先に星を落とす範囲攻撃 |
| Teleport | 視線先へテレポート |
| Summon Ender Chest | 自分のエンダーチェストを足元に出す |
| Recall | 初期リスポーン地点へ帰還 |
| Portal | 2つのポータルフレームを接続する転移門を開く |
| Echoing Strikes | 付与中、非魔法攻撃が残響し幽体剣/魔法矢で追撃 |
| Black Hole | 敵を引きずり込む重力球を設置 |
| Summon Swords | 自分の周りに回転する剣を召喚（近接自動攻撃） |
| Shadow Slash | 目標地点に瞬間移動しながら斬撃を入れる |
| Arcane Shackle | 対象を魔法の枷で拘束し移動阻止 |
| Gravity Fissure | 地面を割って反重力の裂け目を作り敵を打ち上げる |

### Evocation（召喚/力業 — 17 種）: 設置・召喚・物理魔法

| スペル | 効果 |
| :--- | :--- |
| Chain Creeper | 着弾で連鎖的に敵を連れてくるクリーパーを投げる |
| Fang Strike | 前方にエヴォーカーの牙を一直線に出す |
| Fang Ward | 自分の周囲を円形の牙で囲む防御陣 |
| Fang Swirl | 牙が螺旋状に自分の周りを回る |
| Firecracker | 連続破裂する爆竹を投げる |
| Gust | 風の爆発で敵を吹き飛ばす |
| Invisibility | 完全透明化（防具も消える） |
| Lob Creeper | 生きたクリーパーを放物線で投げる |
| Shield | 魔力の盾で正面を完全防御 |
| Spectral Hammer | 巨大な幽体ハンマーを頭上から振り下ろす |
| Summon Horse | 騎乗できる魔法馬を召喚 |
| Summon Vex | ヴェックスの眷属を複数召喚 |
| Slow | 対象を強力にスロウ |
| Arrow Volley | 上空から矢の雨を降らせる範囲攻撃 |
| Wololo | 敵モブを味方に変える |
| Throw | 対象のブロック/実体を掴んで投げる |
| Scapegoat | 自分の身代わり人形を置いて敵の注意を引く |

### Blood（血 — 10 種）: 体力交換・吸収

| スペル | 効果 |
| :--- | :--- |
| Acupuncture | 触れた対象に針を刺し継続ダメージ+治癒阻害 |
| Blood Needles | 血の針を連射し貫通ダメージ |
| Blood Slash | 血の斬撃を飛ばす飛び道具。命中で自分を治癒 |
| Blood Step | 前方へ瞬間移動し残像で視線を逸らす |
| Devour | 近接対象を食らって体力奪取 |
| Heartstop | 対象の心臓を止める即死級詠唱（対象HP依存） |
| Raise Dead | 死体/地面からスケルトン・ゾンビの眷属を召喚 |
| Ray of Siphoning | 継続ビームで対象の体力を吸い続ける |
| Sacrifice | 自分の召喚物を生贄にして大ダメージ |
| Wither Skull | ウィザースカルを発射（枯死付与） |

### Holy（聖 — 13 種）: 回復・聖属性 ※Healer と共通領域

| スペル | 効果 |
| :--- | :--- |
| Angel Wings | 幽体の翼で滑空（エリトラ状） |
| Blessing of Life | 味方を大回復+持続回復付与 |
| Cloud of Regeneration | 味方を範囲再生する回復雲 |
| Fortify | 自分に吸収ハートを付与 |
| Greater Heal | 大回復（高 Lv 専用） |
| Guiding Bolt | 聖なる誘導光を放ち命中で味方を治癒/敵にダメ |
| Healing Circle | 範囲の味方を継続回復する陣 |
| Heal | 単体即時回復 |
| Sunbeam | 天からの光柱で範囲焼き（アンデッド特効） |
| Wisp | 自分の回りを回る回復ウィスプを召喚 |
| Divine Smite | 聖なる雷で対象を撃つ |
| Haste | 移動・攻撃速度を上げるバフ |
| Cleanse | 自分/味方のデバフを除去 |

### Nature（自然 — 13 種）: 毒・拘束・変身

| スペル | 効果 |
| :--- | :--- |
| Acid Orb | 着弾で酸を撒く弾（防具劣化/継続ダメ） |
| Blight | 対象に Blighted を付与（与ダメ減・被回復減） |
| Poison Arrow | 毒の矢を放つ |
| Poison Breath | 前方へ毒の息を吐き続ける |
| Poison Splash | 毒の飛沫を扇状にばら撒く |
| Root | 対象を根で拘束し動けなくする |
| Spider Aspect | 壁登り・毒耐性など蜘蛛化バフ |
| Firefly Swarm | ホタルの群れを召喚して敵を撹乱/攻撃 |
| Oakskin | 皮膚を樹皮化して被ダメージ軽減 |
| Earthquake | 周囲に地震波を起こして敵を吹き飛ばす |
| Stomp | 地団駄で足元の敵を範囲打撃 |
| Gluttony | 食事でマナを回復する状態にする |
| Touch Dig | 触れたブロックを採掘する魔法 |

### Eldritch（深淵 — 7 種）: 禁忌・最上位

| スペル | 効果 |
| :--- | :--- |
| Abyssal Shroud | 付与中あらゆる攻撃を回避（Shroud 効果） |
| Sculk Tentacles | 周囲からスカルクの触手を生やして敵を貫く |
| Sonic Boom | 正面へ Warden 風の衝撃波を放つ |
| Planar Sight | 壁越しに敵の位置を透視 |
| Telekinesis | 対象の実体を掴んで浮かせる/移動させる |
| Eldritch Blast | 深淵エネルギーの貫通弾を発射 |
| Pocket Dimension | 自分だけの小空間へ転移する |

## 立ち回りガイド

- **学派を絞れ**: 全学派を均等に使うより、Fire（殲滅）/ Ice（制御）/ Lightning（連鎖）など主力学派を決めて装備・オーブを集中させる方が強い。
- **制御→殲滅の型**: Ice で足止め（Cone of Cold / Blizzard / Ice Tomb）→ Fire で焼く（Fireball / Blaze Storm / Raise Hell）が基本コンボ。
- **詠唱中は無防備**: long/continuous 詠唱の大技は、Guardian が敵を止めている間に撃つ。単騎では instant/charge 中心に。
- **緊急離脱を常に持て**: Ender の Teleport / Evasion / Blood Step、Holy の Angel Wings のいずれかをホットバーに入れておく。
- **スクロール経済**: Inscription Table で高レベルスクロールを量産すれば、スペルブックのスロットを占有せず大技を持ち運べる。
- **災害防衛**: 襲撃の集団を範囲魔法で一掃する主力。Black Hole で集めて Blaze Storm、Arrow Volley で面制圧。
- **異世界遠征**: 黄昏の森の雑魚集団処理・厄災ボスの周辺掃討に適任。狭い洞窟では Fireball 等の自爆に注意。

## お金の稼ぎ方

- 範囲殲滅力を活かしたダンジョン高速周回・ドロップ素材の量産販売。
- 高レベルスペルスクロール・学派装備・アップグレードオーブの売買。
- 災害防衛の範囲火力担当としての報酬。
- 魔導素材・魔法関連アイテムの売買（科学陣営が扱えない品ほど高値）。

## こんな人に向いている

- 一撃で画面を薙ぎ払う爽快感が好きな人
- リソース管理と位置取りの頭脳プレイが好きな人
- 「紙装甲でも最強火力」というロマンに燃える人
