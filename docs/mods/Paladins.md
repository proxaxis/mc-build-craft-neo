# Paladins & Priests（RPG Series）— MOD リファレンス

> **原作**: [Paladins & Priests (by ZsoltMolnarrr)](https://github.com/ZsoltMolnarrr/Paladins)
> **対応**: Fabric / Forge / NeoForge（RPG Series）、クライアント＋サーバ。**要 Spell Engine**
> **出典**: `1.21.1` ブランチ `data/paladins/spell/*.json` + `en_us.json` + spell book タグ

## 概要

Spell Engine ベースの職業 MOD。**Paladin（聖騎士/前衛ヒーラー）**と **Priest（聖職者/純ヒーラー）**
の 2 クラスを追加し、それぞれ専用スペルブック（**Paladin Libram** / **Holy Book**）に
クラス固有スペルを刻んで運用する。スペルは消耗品ルーン（`runes:healing_stone`）をコストとして使い、
聖属性の攻撃・回復・補助で構成される。

## 特徴

- **2 クラス制**: Paladin = メイス/大槌/カイトシールドを持つ近接ヒーラー。Priest = 杖/ワンドの後衛回復
- **スペルコスト**: ほぼ全スペルが `healing_stone` ルーン消費 + 飢餓 exhaust + 個別クールダウン
- **スペルブック**: Paladin Libram（パラディン呪文）と Holy Book（プリースト呪文）でセットが分かれる
- **装備進行**: Mace/Great Hammer/Claymore/Kite Shield が Stone→Iron/Golden→Diamond→Ruby→Netherite→Aeternium と段階式、Holy Wand/Holy Staff も同様
- **専用防具**: Crusader（パラディン）/ Priest・Prior（プリースト）セット + ネザライト版
- **固有武器**: Sun's Mace、Silver Staff of the Valkyrie、Valkyrie Bulwark、Valkyrie Great Hammer、Holy Claymore など

## できること

- 聖属性スペルで**回復・バフ・デバフ・範囲制圧**を行う
- スクロール（Paladin/Holy Spell Scroll）で単発使用も可
- 杖/ワンドにスペルを内在させて直接発動（`holy_wand`/`holy_staff` タグ）
- Paladin は前線でタンク兼回復、Priest は後衛で範囲回復・バリア・リフトを担当

## 全スペル（全 14 種、クラス別）

※ 全スペル `runes:healing_stone` を消費。CD は秒。`{...}` は装備で伸びる変数。

### Paladin（Paladin Libram の 6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Flash Heal | 6 | 自分または味方対象を即時回復 `{heal}` |
| Blessed Strikes | 12 | 武器に聖光を込め最大 5 祝福。近接ごとに 1 消費して追加 `{damage}` 魔法ダメージ（保持 15s） |
| Divine Protection | 30 | 以後の被攻撃を最大 `{effect_amplifier_cap}` 回まで防ぐ（8 秒） |
| Judgement | 15 | 対象と周囲 `{impact_range}` B の敵に最大 `{damage}` ダメージ + 3 秒スタン（隕石式着弾） |
| Battle Banner | 45 | 軍旗を立て、半径内の味方の攻撃速度+ノックバック耐性を `{cloud_duration}` 秒間上げる |
| Immolation | 12 | 聖炎で爆発: 周辺敵に `{damage}`+点火、自分と味方を `{heal}` 回復 |

### Priest（Holy Book の 6 スペル）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Holy Light（Holy Beam） | 10 | 光ビームを継続照射: 味方を毎秒 `{heal}` 回復、敵へ `{damage}` 魔法ダメージ |
| Circle of Healing | 10 | 自分と周囲の味方を `{heal}` 範囲回復 |
| Barrier | 40 | 円形バリアを召喚。飛び道具・魔法・敵の侵入を遮断 |
| Lightwell | 45 | 光の井戸を召喚し、近くの負傷した味方を継続回復（`{summon_duration}` 秒持続） |
| Levitate | 24 | 詠唱中は聖光で浮上し、止めるとふわりと漂いながら降下 |
| Penance | 12 | 聖なる 3 連ボルトを敵へ照射。各弾は `{damage}`+命中点周辺 `{impact_range}` の味方に吸収シールド |

### 共通（両クラス利用可）

| スペル | CD | 効果 |
| :--- | ---: | :--- |
| Heal | 4 | 単体回復（詠唱 1 秒）。基本回復技 |
| Holy Shock | 3 | 味方なら回復 `{heal}`、敵ならスマイト `{damage}`。攻守一体の短 CD 技 |

## BCN での近似メモ

- BCN 実装済み: `paladin`/`priest` 職業、`ability_barrier`/`ability_consecrate`、`paladin_mace`/`holy_staff`、Priest の healer オーラ共有、Paladin 常時 Absorption
- 原作の 2 ブック制・ルーン消費は BCN の MP 消費 + 職業ゲートで代替可能
- 詠唱チャネル系（Holy Light/Penance/Levitate）はクライアント側演出が主で、サーバ側なら tick 継続処理として実装可
