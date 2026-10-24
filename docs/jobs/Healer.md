# Healer — 聖職者

> **陣営**: 魔法技術陣営 / **役割**: 治癒・支援（パーティの生命線）
> **源流MOD**: Paladins & Priests（RPG Series）+ Iron's Spells 'n Spellbooks（Holy 学派）
> **MOD詳細**: [Paladins.md](../mods/Paladins.md) / [IronsSpells.md](../mods/IronsSpells.md)

聖なる祈祷で味方の傷を癒やし、戦線を維持するパーティの心臓部。
源流 MOD の設計では、聖職者は**Spell Engine ベースの 2 クラス制** ——
前線で殴りながら癒やす **Paladin** と、後衛で範囲回復・結界を張る **Priest**。
Healer は両系統の聖属性スペルを束ねる、需要の絶えない支援職。

## できること

- **聖属性スペル体系**: 回復・バフ・デバフ・結界・範囲制圧まで網羅する 14 スペル。
- **スペルブック運用**: Paladin Libram（パラディン呪文）と Holy Book（プリースト呪文）にスペルを刻んで切替発動。
- **杖/ワンドの直接発動**: `holy_wand`/`holy_staff` 系武器にスペルを内在させて使える。
- **スクロール**: Paladin/Holy Spell Scroll でスペルの単発使用も可能。

## できないこと・制約

- **弓・クロスボウは装備不可**。
- **攻撃手段が乏しい**: 単独での敵処理は時間がかかり危険。常にパーティと行動すること。
- **スペルはコスト付き**: ほぼ全スペルが `healing_stone` ルーンを消費 + 飢餓 exhaust + 個別クールダウン。回復の連打は資源との相談。

## スペルシステム（Paladins & Priests）

- **クラス構成**: Paladin = メイス/大槌/カイトシールドを持つ**近接ヒーラー**。Priest = 杖/ワンドの**後衛回復**。
- **スペルコスト**: ほぼ全スペルが `runes:healing_stone` ルーン消費 + 飢餓 exhaust + 個別 CD。
- **スペルブック**: Paladin Libram / Holy Book でセットが分かれる。スクロールを刻んで習得。
- **装備進行**: Mace / Great Hammer / Claymore / Kite Shield が Stone → Iron/Golden → Diamond → Ruby → Netherite → Aeternium と段階式。Holy Wand / Holy Staff も同様。
- **専用防具**: Crusader（パラディン）/ Priest・Prior（プリースト）セット + ネザライト版。
- **固有武器**: Sun's Mace、Silver Staff of the Valkyrie、Valkyrie Bulwark、Valkyrie Great Hammer、Holy Claymore など。

## 全技① — Priest スペル（Holy Book の 6 種）＋ 共通

後衛ヒーラーとしての中核セット。`{...}` は装備で伸びる変数。CD は秒。

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Holy Light（Holy Beam） | 10 | 光ビームを継続照射: 味方を毎秒 `{heal}` 回復、敵へ `{damage}` 魔法ダメージ。**攻守一体の主力チャネル** |
| Circle of Healing | 10 | 自分と周囲の味方を `{heal}` 範囲回復。**集団回復の柱** |
| Barrier | 40 | 円形バリアを召喚。飛び道具・魔法・敵の侵入を遮断。**防衛戦の要** |
| Lightwell | 45 | 光の井戸を召喚し、近くの負傷した味方を継続回復（`{summon_duration}` 秒持続）。設置型の回復源 |
| Levitate | 24 | 詠唱中は聖光で浮上し、止めるとふわりと降下。高所回避・位置取り |
| Penance | 12 | 聖なる 3 連ボルトを敵へ照射。各弾は `{damage}`+命中点周辺 `{impact_range}` の味方に吸収シールド |

### 共通スペル（両クラス利用可）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Heal | 4 | 単体回復（詠唱 1 秒）。**短 CD の基本回復技** |
| Holy Shock | 3 | 味方なら回復 `{heal}`、敵ならスマイト `{damage}`。攻守一体の最短 CD 技 |

## 全技② — Paladin スペル（Paladin Libram の 6 種）

前線に立ちながら癒やす近接ヒーラーのセット。タンク兼ヒーラーの立ち回りが可能。

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Flash Heal | 6 | 自分または味方対象を即時回復 `{heal}`。**最短の緊急回復** |
| Blessed Strikes | 12 | 武器に聖光を込め最大 5 祝福。近接ごとに 1 消費して追加 `{damage}` 魔法ダメージ（保持 15s）。殴りながら回復資源を稼ぐ |
| Divine Protection | 30 | 以後の被攻撃を最大 `{effect_amplifier_cap}` 回まで防ぐ（8 秒）。**前線での保険** |
| Judgement | 15 | 対象と周囲 `{impact_range}` B の敵に最大 `{damage}` ダメージ + 3 秒スタン（隕石式着弾）。パラディン唯一の範囲攻撃 |
| Battle Banner | 45 | 軍旗を立て、半径内の味方の攻撃速度+ノックバック耐性を `{cloud_duration}` 秒間上げる。**集団戦のバフ旗** |
| Immolation | 12 | 聖炎で爆発: 周辺敵に `{damage}`+点火、自分と味方を `{heal}` 回復。攻撃と回復を同時に行う |

## 全技③ — Holy 学派スペル（Iron's Spells 'n Spellbooks）

Iron's Spells の Holy（聖）学派も Healer の領域。より本格的な魔法システムで使える 13 種:

| スペル | 効果 |
| :--- | :--- |
| Heal | 単体即時回復 |
| Greater Heal | 大回復（高 Lv 専用） |
| Blessing of Life | 味方を大回復+持続回復付与 |
| Healing Circle | 範囲の味方を継続回復する陣 |
| Cloud of Regeneration | 味方を範囲再生する回復雲 |
| Wisp | 自分の回りを回る回復ウィスプを召喚 |
| Fortify | 自分に吸収ハートを付与 |
| Cleanse | 自分/味方のデバフを除去 |
| Haste | 移動・攻撃速度を上げるバフ |
| Guiding Bolt | 聖なる誘導光。命中で味方を治癒/敵にダメ |
| Sunbeam | 天からの光柱で範囲焼き（アンデッド特効） |
| Divine Smite | 聖なる雷で対象を撃つ |
| Angel Wings | 幽体の翼で滑空（緊急離脱） |

※ Iron's Spells 側は MP（Mana）消費 + 詠唱方式（instant/long/continuous）+ スペルレベル（最大 Lv10 前後）のシステム。

## 立ち回りガイド

- **位置取りは「中央より少し後ろ」**: Circle of Healing / Holy Light の射程を全員に届けられる中衛ポジションが理想。前に出過ぎると狙われ、下がり過ぎると届かない。
- **回復の階層を使い分け**: 単体緊急は Heal(4s)/Flash Heal(6s)、集団は Circle of Healing(10s)、継続は Lightwell/Holy Light、総合攻撃+回復は Immolation。「どれを撃つか」の判断がヒーラーの技術。
- **Barrier は防衛の切り札**: 飛び道具・魔法・敵の侵入を遮断する円形結界。入口防衛・壊滅寸前の立て直しに。
- **Paladin 型なら前線へ**: Divine Protection + Blessed Strikes で殴りながら戦線を維持。Judgement の 3 秒スタンは味方の逃げ時間を作る。
- **ルーン（healing_stone）管理**: 全スペルのコスト。切らすと回復が止まる — 遠征前の補充は必須。
- **災害防衛**: 入口で耐える Guardian/前衛組の背後に控え、Barrier で入口を塞ぎながら回復し続けるだけで防衛戦が成立する。
- **異世界遠征**: 黄昏の森の長期ダンジョン・厄災級ボス戦では消耗戦必至。Healer の有無で帰還率が変わる。

## お金の稼ぎ方

- **同行需要No.1の職業**: あらゆる攻略パーティ・遠征の必須枠。遠征同行・ダンジョン介護の対価を受け取ろう。
- 災害防衛での回復役としての報酬。
- healing_stone ルーン・Holy スクロール・固有武器（Sun's Mace / Valkyrie 系）の売買。
- 高難度ボス攻略の報酬分配（Healer なしではクリアできないコンテンツが多い）。

## こんな人に向いている

- キルより「味方を生かす」ことにやりがいを感じる人
- 戦況を見渡して判断するサポート脳の人
- どんなパーティにも必要とされる存在でありたい人
