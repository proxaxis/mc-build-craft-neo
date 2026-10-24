# Iron's Spells 'n Spellbooks — MOD リファレンス

> **原作**: [Iron's Spells 'n Spellbooks (by iron431)](https://iron.wiki/spellbooks/) / [GitHub](https://github.com/iron431/Irons-Spells-n-Spellbooks)
> **対応**: Forge / NeoForge（1.21 系）、クライアント＋サーバ
> **出典**: `1.21` ブランチ `SpellRegistry.java`（全スペル登録一覧）・`en_us.json`・公式 Wiki

## 概要

エルダースクロール/古典 RPG 風の**本格的な魔法システム**を Minecraft に実装する MOD。
MP（Mana）消費・キャスト時間・クールダウン・スペルレベル・10 の魔法学派（School）を持つ
100 以上のスペルと、それを運用するスペルブック/スクロール/武器スロットの一式を提供する。

## 特徴（システム）

- **MP（Mana）**: スペル詠唱で消費、時間経過で自動回復。装具・属性で最大値/回復力を強化
- **詠唱方式**: instant（即発）/ long（溜め詠唱）/ continuous（継続詠唱）/ charge など、スペルごとに `CastType` が違う
- **学派 × スペルパワー**: 装備やポーションで学派ごとの火力を伸ばす。弱点/耐性も学派別
- **スペルレベル**: スペルごとに最大 Lv10 前後。レベルで威力・効果時間・弾数が伸びる
- **スペルブック**: スロット数付きの魔法書にスペルを刻んで切替詠唱。スクロール（単発消費）もある
- **スクロール作成**: Scroll Forge / Inscription Table で紙+インクを刻んでスクロール化
- **武器スロット**: 一部武器にスペルホットバーが内蔵される
- **コンテンツ**: 学派ごとの防具セット、アップグレードオーブ、魔法リング/護符、専用モブ・ボス（Dead King 等）、構造物（法師塔・地下墓地等）、錬金術（Alchemist Cauldron）、Arcane Anvil
- **ステータス効果**: Abyssal Shroud（全攻撃回避）、Evasion（被撃時ワープ）、Guided、Echoing Strikes、Blighted、Chilled、Frostbite 等数十種

## できること（プレイヤー視点）

- スペルブック/武器スロットにスペルを入れ、R キーなどで選択してキャスト
- スクロールを拾い/作って一回限りの詠唱（MP 不要・消費アイテム）
- スクロールをスペルブックへ刻んで複製習得、Inscription Table でレベルアップ
- 学派装備を揃えて特定属性の威力を最大化
- 召喚物・防御結界・移動術・回復・範囲制圧など職業的な役割分担ができる

## 全スペル一覧（学派別）

SpellRegistry の登録順 = 学派分け。**太字カテゴリ**はソースのセクション区切り。
各スペルには Lv1〜最大のレベル帯・MP コスト・CD・詠唱方式があり、ツールチップに数値が出る。

### Blood（血 — 10 種）

| スペル | 効果 |
| :--- | :--- |
| Acupuncture | 触れた対象に針を刺し継続ダメージ+治癒阻害 |
| Blood Needles | 血の針を連射し貫通ダメージ |
| Blood Slash | 血の斬撃を飛ばす飛び道具。命中で自分を治癒 |
| Blood Step | 前方へ瞬間移動し残像で視線を逸らす |
| Devour | 近接対象を食らって体力奪取 |
| Heartstop | 対象の心臓を止める即死級詠唱（対象 HP 依存） |
| Raise Dead | 死体/地面からスケルトン・ゾンビの眷属を召喚 |
| Ray of Siphoning | 継続ビームで対象の体力を吸い続ける |
| Sacrifice | 自分の召喚物を生贄にして大ダメージ |
| Wither Skull | ウィザースカルを発射（枯死付与） |

### Ender（エンダー — 16 種）

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
| Portal | 2 つのポータルフレームを接続する転移門を開く |
| Echoing Strikes | 付与中、非魔法攻撃が残響し幽体剣/魔法矢で追撃 |
| Black Hole | 敵を引きずり込む重力球を設置 |
| Summon Swords | 自分の周りに回転する剣を召喚（近接自動攻撃） |
| Shadow Slash | 目標地点に瞬間移動しながら斬撃を入れる |
| Arcane Shackle | 対象を魔法の枷で拘束し移動阻止 |
| Gravity Fissure | 地面を割って反重力の裂け目を作り敵を打ち上げる |

### Evocation（召喚/力業 — 17 種）

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
| Wololo | 敵モブを味方に変える（AoE 参照ネタ） |
| Throw | 対象のブロック/実体を掴んで投げる |
| Scapegoat | 自分の身代わり人形を置いて敵の注意を引く |

### Fire（火 — 13 種）

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

### Holy（聖 — 13 種）

| スペル | 効果 |
| :--- | :--- |
| Angel Wings | 幽体の翼で滑空（エリトラ状） |
| Blessing of Life | 味方を大回復+持続回復付与 |
| Cloud of Regeneration | 味方を範囲再生する回復雲を発生 |
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

### Ice（氷 — 12 種）

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

### Lightning（雷 — 10 種）

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

### Nature（自然 — 13 種）

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

### Eldritch（深淵 — 7 種）

| スペル | 効果 |
| :--- | :--- |
| Abyssal Shroud | 付与中あらゆる攻撃を回避（Shroud 効果） |
| Sculk Tentacles | 周囲からスカルクの触手を生やして敵を貫く |
| Sonic Boom | 正面へ Warden 風の衝撃波を放つ |
| Planar Sight | 壁越しに敵の位置を透視 |
| Telekinesis | 対象の実体を掴んで浮かせる/移動させる |
| Eldritch Blast | 深淵エネルギーの貫通弾を発射 |
| Pocket Dimension | 自分だけの小空間へ転移する |

## 関連システム

- **ブロック**: Inscription Table、Scroll Forge、Arcane Anvil、Alchemist Cauldron、Portal Frame（Portal スペル連携）、Mithril/Deepslate Mithril 鉱石、Wisewood 本棚など
- **防具**: 学派ごとの法衣セット（Pyromancer/Cryomancer/Electromancer/Cultist/Plagued/Archevoker/Priest/Shadowwalker 等）＋ネザライト系上位装
- **リング/護符**: Mana Ring、Cast Time Ring、Cooldown Ring、Affinity Ring（学派強化）、各種 Ward Ring、Invisibility Ring、Expulsion Ring 等
- **アップグレード**: `*_upgrade_orb` で装備を強化（mana/cooldown/学派別）
- **モブ/ボス**: Dead King、Archevoker、Pyromancer、Cryomancer、Priest、Necromancer、Apothecarist、Keeper、Ice Spider など + 構造物生成

## BCN での近似メモ

- BCN 実装済み: MP 消費・職業ゲート付きの 6 スペル（heal/lightning/blink/holy_smite/smoke_bomb/fortify）+ `spellbook`（7 スペル切替・sneak+右クリ）+ `scroll_*` 4 種
- 原作の本質は **MP/詠唱時間/CD/レベル/学派** の組み合わせ。BCN で近づけるなら spell のデータ駆動化（MP・CD・cast_type をアイテム NBT で持つ）が最初の一歩
- 詠唱モーション・スキルツリー GUI はクライアント専用で RCON 検証不可（AGENTS.md 既記）
