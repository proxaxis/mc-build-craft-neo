# 戦士 - Warrior

> 魔法技術陣営 / 近接アタッカー

前線に立ち、重い両手武器の一撃と範囲薙ぎ払いで敵陣を粉砕する物理アタッカー.

## できること

- あらゆる近接武器を装備可能: 剣、斧、槍、鎌、槌、チャクラムまで、魔法陣営の近接武器をすべて扱える. 
- Better Combat の近接戦闘システムで戦う: 武器ごとの攻撃コンボ、範囲判定、二刀流、両手持ち. 
- Simply Swords の武器体系をフルに使う: 素材進行（鉄→金→ダイヤ→ネザライト→Runic）、
  Runic Power の刻印、ジェムソケット、Unique 武器の覚醒. 

## できないこと、制約

- 弓、クロスボウは装備不可. 
- 銃火器は使用不可. 
- 魔法陣営の汎用スペル（杖系）は使えるが、他職専用のスペルブックは発動しない. 

## 戦闘システム（Better Combat）

Warrior の通常攻撃はすべて Better Combat のルールで処理される. 技を覚えるのではなく、"武器を選ぶこと = コンボを選ぶこと" がこの職の本質. 

### 基本ルール

- 武器コンボ: 連続攻撃で武器固有のシーケンスが順に出る. 各段でダメージ倍率、リーチ、判定角度、モーションが個別に違い、最後まで出すと先頭にループ. 一定時間無操作（武器クールダウン ×3）でリセット. 
- アップスイング（溜め）: クリックから実ヒットまで武器速度の一定割合（各技の `upswing` ×0.5）の溜め動作が入る. 重い武器ほど "間" が生まれる. 
- 振り判定: カーソル先ではなく武器の軌跡を OBB+SAT で立体判定. 判定ボックスに入った敵は一振りでまとめて巻き込む. 
- 攻撃中の移動速度: ×0.5（騎乗中は無効）. 連打ほどノックバックも減衰（CD 12.5t 未満の攻撃は比例減少）. 
- 掃討の仕様: 複数ヒット時は追加ターゲットごとにダメージ減衰（最大 -50%）. Sweeping Edge エンチャントで全員フルダメージ. 
- 関係性フィルタ: プレイヤー、村人、ゴーレムなど `NEUTRAL` 指定の相手は直視しないと斬れない（流れ斬り誤爆防止）. 
- 補助操作: 攻撃長押しで自動連打、アップスイング中にキャンセル（フェイント）、草など硬度0ブロックを壊さず攻撃優先. 

### 二刀流と両手武器

- 二刀流: 片手武器を両手に持つと攻撃キーで左右交互に攻撃、攻撃速度 +20%. 条件付きコンボ（`DUAL_WIELDING_SAME_CATEGORY` など）で専用モーションが出る武器もある. 
- 両手武器: `two_handed` の武器はオフハンド完全無効. 盾は持てない - 防御は Guard ではなく間合いで取る. 

### コンボ条件（Condition）

`NOT_DUAL_WIELDING` / `DUAL_WIELDING_ANY` / `DUAL_WIELDING_SAME` / `DUAL_WIELDING_SAME_CATEGORY` /
`NO_OFFHAND_ITEM` / `OFF_HAND_SHIELD` / `MAIN_HAND_ONLY` / `OFF_HAND_ONLY` / `MOUNTED` / `NOT_MOUNTED`

## 武器タイプと Implicit（Simply Swords）

Simply Swords の各武器タイプには固有の攻撃速度、ダメージ補正と、入手時に範囲抽選される
Implicit（恒久ボーナス）がある. 数値はデフォルト（攻速=実効値、ダメ補正=素材値への加算）. 

| タイプ | 攻速 | ダメ補正 | Implicit（抽選範囲） | 傾向 |
| :--- | ---: | ---: | :--- | :--- |
| Longsword | 1.6 | +0 | ダメージ弾き 5–15% | バランス型 |
| Claymore | 1.2 | +2 | ダメージ弾き 5–15% | 低速両手剣 |
| Rapier | 2.2 | -1 | 防御無視 15–35% | 高速突き |
| Spear | 1.3 | +0 | 防御無視 15–35% | 長リーチ突き（投擲可） |
| Cutlass | 2.0 | +0 | 追加戦利品 1–3% | 速い斬撃 |
| Katana | 2.0 | +0 | 2倍ダメージ 5–15% | 高速精密剣 |
| Sai | 2.5 | -3 | バックスタブ +20–40% | 最速、低火力 |
| Twinblade | 2.0 | +0 | 攻速上昇 5–25% | 両刃高速 |
| Chakram | 1.0 | -1 | 攻速上昇 5–25% | 円刃 |
| Warglaive | 1.8 | +0 | 2連撃 5–15% | 双剣 |
| Glaive | 1.4 | +0 | 出血付与 10–60% | 薙ぎ払い長柄 |
| Greataxe | 0.9 | +3 | 出血付与 10–60% | 重い両手斧 |
| Halberd | 1.2 | +3 | 出血付与 10–60% | 重長柄 |
| Scythe | 1.3 | +1 | 低HP処刑 5–15% | 鎌 |
| Greathammer | 0.8 | +4 | 装甲破壊 2–10%/hit | 最遅、最大火力 |

※ Dagger（バックスタブ）と Hammer（装甲破壊）は Unique 専用の型で、標準クラフトは無し. 

## 全技 1 - 武器コンボ（Better Combat プリセット）

`bettercombat:` プリセットのうち近接全種.  "倍率" = ダメージ倍率、 "判定" = 横（HORIZONTAL_PLANE＝薙ぎ払い）/ 縦（VERTICAL_PLANE＝振り下ろし）/ 前（FORWARD_BOX＝直線突き）、リーチはバニラ剣からの増減. 

### 片手武器

| プリセット | リーチ | コンボ（順に出る技） |
| :--- | :--- | :--- |
| `sword` | ±0 | ① 横斬り右 → ② 横斬り左（横、120°、×1.0）→ ③ 突き（前、×1.0） |
| `cutlass` | -0.25 | ① 横斬り右 → ② 横斬り左（横、120°、×1.0） |
| `coral_blade` | -0.25 | ① 横斬り右 → ② 横斬り左（横、120°、×1.0）→ ③ アッパーカット（縦、120°、×1.0） |
| `dagger` | -0.5 | ① 横斬り右 → ② 横斬り左（横、150°、×0.9）→ ③ 両手突き（前、×1.4）※③は同カテゴリ二刀流+メインハンド時のみ |
| `rapier` | ±0 | ① 横斬り左（横、90°、×0.9）→ ② 突き（前、×1.1） |
| `sickle` | -0.25 | ① 横斬り右 → ② 横斬り左（横、120°、×0.9）→ ③ 交差斬りクロス（前、×1.2）→ ④ アンクロス（前、×1.3）※③④は二刀流時のみ |
| `mace` | -0.25 | ① 横打ち右 → ② 横打ち左（横、150°、×0.8）→ ③ 振り下ろし（縦、90°、×1.4） |
| `vanilla_mace` | -0.25 | ① 即座の振り下ろし（前、90°、×1.0、upswing 0.1） |
| `axe` | -0.25 | ① 横斬り右 → ② 横斬り左（横、90°、×1.0、upswing 0.3＝素早い出） |
| `lance` | +1.0 | ① 突き（前、×1.0）- 歩行/騎乗でモーション分岐 |
| `trident` | +0.25 | ① 突き（前、×1.0、片手版） |

### 両手武器（オフハンド封じ）

| プリセット | リーチ | コンボ |
| :--- | :--- | :--- |
| `claymore` | +0.25 | ① 大横薙ぎ（横、150°、×0.75）→ ② 両手突き（前、×1.0）→ ③ 両手振り下ろし（縦、150°、×1.25） |
| `katana` | ±0 | ① 横薙ぎ右（横、120°、×0.8）→ ② 縦斬り左（縦、120°、×1.2）→ ③ 縦斬り右（縦、120°、×1.2）→ ④ 横薙ぎ左（横、120°、×0.8） |
| `twin_blade` | ±0 | ① 剣先切替斬り右 → ② 同左（横、150°、×1.0） |
| `double_axe` | ±0 | ① 大横薙ぎ（横、180°、×1.0）→ ② 360°スピン（横、360°、×1.0） |
| `heavy_axe` | ±0 | double_axe 継承（待機ポーズのみ heavy） |
| `scythe` | +0.5 | ① 横薙ぎ右 → ② 横薙ぎ左（横、150°、×1.0） |
| `glaive` | +0.5 | ① 縦斬り右（縦、150°、×0.8）→ ② 縦斬り左（縦、150°、×0.8）→ ③ 大横薙ぎ（横、180°、×1.4） |
| `halberd` | +0.75 | ① 縦斬り（縦、150°、×0.8）→ ② 突き（前、×1.2） |
| `spear` | +1.0 | ① 両手突き（前、×1.0）- 最長リーチ単発 |
| `staff` | +0.25 | ① 横薙ぎ右（横、160°、×0.8）→ ② 横薙ぎ左（横、160°、×1.0）→ ③ 振り下ろし（縦、120°、×1.2） |
| `battlestaff` | +0.5 | 6連: ① 横薙ぎ右（180°、×0.8）→ ② 同左（180°、×1.0）→ ③ 360°スピン（×1.2）→ ④ 振り下ろし（縦、160°、×1.4）→ ⑤⑥ 突き左右（前、×0.8） |
| `hammer` | ±0 | ① 両手振り下ろし（前、×1.0）- 単発ヘビー |
| `anchor` | ±0 | ① 両手ヘビー振り下ろし（前、×1.0） |
| `soul_knife` | -0.25 | ① 突き（前、×1.0） |

## 全技 2 - Runic Powers（26 種）

ネザライト武器を Runic 化（Runic Tablet + ダイヤの鍛冶）すると武器種、Implicit を引き継いだまま
以下のいずれかが最大強度で付く. Runefused ジェムのソケット装着でも同系統の力が得られる. 
`[受動]`=ヒット時確率 or 常時、`[能動]`=武器アビリティキー/右クリ. 

| パワー | 種別 | 効果 |
| :--- | :--- | :--- |
| Float | 受動 | ヒット時、対象の重力を反転（浮遊） |
| Swiftness | 受動 | ヒット時、自分の移動速度上昇 |
| Slow | 受動 | ヒット時、対象をスロウ |
| Freeze | 受動 | ヒット時、対象を冷却/凍結 |
| Wildfire | 受動 | ヒット時、対象と同種の周囲モブをすべて点火 |
| Zephyr | 受動 | ヒット時、攻撃速度+移動速度上昇 |
| Shielding | 受動 | ヒット時、吸収ハート獲得 |
| Stoneskin | 受動 | ヒット時、速度と引き換えに耐性上昇 |
| Trailblaze | 受動 | ヒット時、速度上昇+自分が燃える |
| Weaken | 受動 | ヒット時、対象をスロウ+弱化 |
| Unstable | 受動 | 定期的にランダムな効果が自分に付く |
| Momentum | 能動 | 強い前進モーメンタムを短時間得る（突進） |
| Ward | 能動 | 現在HPの半分を犠牲に、残HP比例の吸収を6秒間脈動付与 |
| Immolation | 能動 | 自他を周期的に灼くオーラ. 与ダメは自分の現在HP比例 |
| Throwing | 能動 | 武器を投げる（軽い武器ほど遠くへ、滞空時間と角度で威力増）. Loyalty III 内蔵 |
| Active Defence | 受動 | 定期的に近くの敵へ矢を自動発射（インベントリの矢を消費） |
| Frost Ward | 受動 | 定期的に全近接敵へ鈍足スノーボールを発射 |
| Banehead Swarm | 受動 | 浮遊クリーパーヘッドが周回し、敵へ追尾自爆 |
| Dancing Blades | 受動 | ヒット時、武器の幽体複製が周回して敵を斬る |
| Faultline | 受動 | ヒット時、前方の地面を裂いて破裂ダメージを走らせる |
| Runic Slash | 受動 | 振るたび貫通するルーン斬撃の飛び道具を放つ |
| Evocation | 受動 | 定期的に敵の足元へエヴォーカーの牙を召喚（鈍足+武器威力） |
| Sniffer Slam | 受動 | ヒット時、スニッファーが落下し敵を押し潰す（重ダメ+行動不能） |
| Stormlash | 受動 | ヒット時、近接敵間を連鎖する雷を放つ |
| Imbued | 受動 | ヒット時、耐久値比例の追加魔法ダメージ |
| Pincushion | 受動 | 自分に刺さった矢1本ごとに与ダメージ増加 |

一部は `Greater`（強化版）抽選あり. 

## 全技 3 - Nether Powers（11 種）

Netherfused ジェムをソケット装着した武器に付く第2のパワー. 覚醒対応 Unique では覚醒 Lv 比例で強化. 

| パワー | 効果 |
| :--- | :--- |
| Berserk | 装備装甲値 10 未満のとき、攻撃が体力を吸収 |
| Echo | 斬撃が残響し、短い遅延後に軽減不能な追加ダメージ |
| Nullification | ヒット時、味方のデバフ解除＆敵のバフ剥がしを行う戦旗を召喚 |
| Onslaught | スロウ状態の敵を殴ると Onslaught 獲得（脈動するHaste、切れるとWeakness） |
| Radiance | 弱化状態の敵を殴ると Immolation オーラを短時間獲得 |
| Verdant Trail | 武器を持つと花の軌跡が残る. 味方を回復、敵を傷つける |
| Necromantic Arsenal | ヒット時、自分の武器の複製を持つ骸骨の従僕を召喚 |
| Wolf Pack | ヒット時、武器の力を共有した狼の従僕を召喚 |
| Goat Stampede | ヒット時、ヤギの突進群を召喚（進路上の敵にダメ+ノックバック）. 同時1群 |
| Wing Buffet | ヒット時、幽体の竜翼が正面へ破壊的な突風を放つ |
| Dragon's Maw | 敵が近いと頭上に浮遊する竜の頭が出現し、標的へ炎のブレスを吐く |

## 全技 4 - ユニーク武器（55 種）

チェスト極低確率（0.05%、ピティ75回）、ボスドロップ、Contained Remnant で入手. 
拾得時は覚醒 Lv0（威力50%、攻速75%、技は Lv4 で解放、Lv8 = フル性能. Runic Tablet 投入で覚醒）. 
表記: 技名 - 受動効果 / 【右(or 長押し)】能動効果. 

| Unique | 型 | 技 |
| :--- | :--- | :--- |
| Brimstone Claymore | Claymore | Brimstone - ヒット時確率で炸裂噴石 / 【右】対象頭上に大剣召喚、範囲パルス後に突き刺さる |
| The Watcher | Claymore | The Unblinking Hunt - ヒットで Dread 蓄積（最大5）+コウモリ / 【右】コウモリ群が斬撃+残HP比例の一撃. 最大Dreadなら HP25%以下を処刑. 討伐でCDリセット |
| The Devourer | Claymore | Devouring Mass - 弾き成功で攻撃者の足下に深淵の顎を開く（24B先まで反撃）/ 【右】深淵の種を射出し巨大な塊を開花. 触手で捕食、Gloam を撒く |
| Storm's Edge | Twinblade | Stormbreak - ヒットでCD更新の確率 / 【右】雷となって無敵ダッシュ、敵を抜き雷鳴. 速度+採掘速度付与 |
| Stormscale | Glaive | Lightning Rod - 設置グレイヴ周辺で攻撃連動範囲ダメ / 【右】幽体グレイヴ設置（40s）. 攻撃ごとにパルス威力+半径+1%（最大80%） |
| Ionbound Stormscale | Glaive | Ion Containment - 8秒ごとに Ion Cube 生成（最大3）. 大ダメージを Cube で完全無効化+防護 / 【右】Cube消費で12m雷撃回廊、1秒以内の再発動で照準追従ビーム3秒（麻痺5s）※Stormscale を Lv4+ で雷雨時に鍛冶 |
| Stormbringer | Longsword | Shock Deflect - Storm Charge 消費でヒット時連鎖雷 / 【右】短時間完全防御. ジャストタイミングでカウンター+Charge（疑似パリィ） |
| Mjolnir | Hammer | Storm - ヒットで敵を帯電化 / 【右】移動する嵐を召喚し連続落雷+雷鳴. 帯電敵は被撃時に小雷鳴. 投擲可 |
| Thunderbrand | Halberd | Thunder Blitz - ヒットでCD更新の確率 / 【長押し】無敵で溜め、ダッシュで敵を抜き、防いだ分の連鎖雷 |
| Enigma | Claymore | Galeforce - 竜巻の近くでHaste / 【右】敵を追う竜巻召喚. 捕縛時間だけダメージ増大 |
| Ribboncleaver | Claymore | Ribbonwrath - 常時 移動-5%、被ダメ-15% / 【右】前方ダッシュ+数ヒット無効化+ノックバック耐性、次撃が単体+95% |
| Caelestis | Claymore | Astral Breach - 【右】異次元の裂け目を45秒開く（半径20B、破損生物召喚）. 5%で自分を狩る Unbound が漏出 |
| Lichblade（3形態） | Claymore | Soul Anguish I–III - 苦しむ魂が周辺敵を削る / 【長押し】魂を標的へ送り連続ダメ+体力吸収（LvIII で帰還時吸収） |
| Waxweaver | Claymore | Waxweave - 燃えた敵を殴ると力+採掘速度. 致命傷で全回復+耐性（60s CD）/ 【右】敵を蜜蝋に封じ行動不能+他敵の標的化. 終了時爆発 |
| Riftmane | Claymore | Vanguard - 4–16B先の振りで裂け目から幽体戦馬 / 【右】5騎の突撃陣. スプリント中発動で中央の馬に騎乗 |
| Dawnquiver | Claymore | Seraph's Draw - 幽体小弓が最遠の敵を射て Dawn Chorus 蓄積（最大3）/ 【長押し】Chorus消費: 1=Dawn Volley(2射) / 2=Sunlance(4体貫通) / 3=Seraphic Chorus(収束3弓) |
| Twisted Blade | Claymore | Ferocity - ヒットで攻速スタック（最大15）、範囲打撃 Crescendo / 【右】全消費で次撃を Grand Crescendo に |
| Sunfire | Longsword | Righteous Standard - ヒットで確率回復 / 【右】正義の軍旗：敵を点火、ダメ、鈍足、味方へ力+体力付与 |
| Harbinger | Longsword | Abyssal Standard - ヒットで確率弱化 / 【右】深淵の軍旗：敵を引寄せ、ダメ、鈍足、味方へHaste |
| Dormant Relic → Righteous/Tainted | Longsword | 覚醒進行型. Lv4で昼=Righteous / 夜=Tainted、Lv8で Sunfire/Harbinger へ |
| Longsword of the Plague | Longsword | Death Knell - ヒットで fever 蓄積、5で疫鐘（範囲ダメ+伝播、最大6連鎖）. 敵バフを悪疫に変換し鐘を強化 |
| Bramblethorn | Rapier | Grasp of the Wild - 前ターゲットから茨を伸ばし経路ダメ+鈍足. 束縛中の1体へのダメが他へ反射 |
| Shadowsting | Rapier | Shadow Dance - 攻撃を反復する影分身 / 【右】無敵で近接敵間を高速ステップし各敵に武器ダメ |
| Whisperwind | Katana | Fatal Flicker - ヒットでCD更新の確率 / 【右】高速前進ダッシュ（耐性+吸収）、通過敵は遅延ダメ |
| Dreadwhisper | Katana | Shadow Rend - Corrupted Wound 付与、傷敵への攻撃は必殺+破裂 / 【右】闇の壁で敵を抜く無敵突進、削った分回復+Gloam ※Devourer が Whisperwind 捕食で変化 |
| Emberlash | Dagger | Smoulder - Smouldering 蓄積、1スタックごと+0.2ダメ / 【右】後方回避し傷を焼き塞ぎ最大HP15%回復 |
| Soulstealer | Dagger | Soul Steal - 魂の負債を奪う（キルで追加）/ 【右】全負債消費で対象背後へブリンク背刺し（最大5倍） |
| Emberblade | Longsword | Ember Ire / 【長押し】エネルギー充填、解放で灼熱破片（溜めほど強化、ラスト1秒解放で+1）. 解放時 Speed/Haste/Strength 確率 |
| Watching Warglaive | Warglaive | The Unblinking Hunt - Dread 蓄積+コウモリ / 【右】Dread付き敵へコウモリ放ち消費分攻撃+体力吸収 |
| Star's Edge | Twinblade | Astral Reprise - 昼:追加ダメ / 夜:吸血 / 【右】星座軌跡を残すダッシュ、触敵ダメ+消滅時爆発 |
| Magiblade | Twinblade | Magisonic - 接近敵を検出撃退 / 【長押し】Wardenの頭が15秒周回し衝撃波連射 |
| Magiscythe | Scythe | Magistorm - 発動中の攻撃で装備品修理の確率 / 【右】魔法の嵐+落雷. 落雷ごとに確率で延長+激化 |
| Soulrender | Scythe | Soul Rend - 弱化デバフ蓄積 / 【右】全スタック消費、量に応じたダメ+回復 |
| Soulstalker | Scythe | Stygian Stride - 背中から深淵触手が定期的に敵を貫く / 【右】6本の深淵脚で走行（溜め跳躍）、足音ダメ+Gloam ※Soulrender 捕食変化 |
| Wraithfang | Cutlass | Spectral Leap - 内蔵Loyalty I / 【右】投げた刃へ無敵跳躍、到達でHaste |
| Wraithmaw | Cutlass | Spectral Downpour - 回収幽体剣が頭上に浮遊し振りに連動追尾 / 【右】幽体剣を対象地点へ降下、武器ダメ+Gloam ※Wraithfang 捕食変化 |
| Bloodwake | Cutlass | Crimson Revelry - Bleed付与、最大Bleed敵で血爆+Blood Frenzy. 血染め地面=敵鈍足、自回復 / 【右】Frenzy全消費の5段血儀: 波→絶叫(攻速最大+60%)→頭上血剣→血蠅群→Red Tide(毎秒波) |
| Frostfall | Hammer | Frost Fury - 内蔵Loyalty III / 【右】投擲、着弾後5パルスでダメ+鈍足+引寄せ |
| Livyatan | Greataxe | Leviathan's Wake - 振りで波放出、Loyalty III / 【右】投擲、帰還時に引寄せ波+確率落雷 |
| Soulpyre | Greataxe | Soul Tether / 【右】周囲ネザー化+敵撃破で拡大. 発動中ノックバック耐性+被ダメ50%減、倒した魂が誘導弾化 |
| Molten Edge | Greataxe | Furnace Heart - 与/被ダメでHeat蓄積（最大 与+50%/被+100%+自燃）/ 【右】放熱で溶岩衝撃波+振り毎マグマ破裂 |
| Flamewind | Glaive | Emberstorm - 炎の種爆発でHaste / 【右】敵に炎の種（持続ダメ→爆発伝播、最大6連鎖） |
| Gloampiercer | Spear | Phantom Phalanx - 3–12B先の振りで空中分身が爆発槍投擲 / 【右】宙浮き+分身槍隊で範囲爆撃 ※Wickpiercer 捕食変化 |
| Magispear | Spear | Magislam - 追加魔法ダメ、Loyalty III / 【右】槍雨→対象地点へ無敵跳躍叩きつけ |
| Wickpiercer | Spear | Flicker Fury - Loyalty III / 【右】投擲で frenzy スタック（攻速+二連撃、最大5、被弾で喪失） |
| Icewhisper | Halberd | Permafrost - 4B内の敵へ冷気ダメ+鈍足オーラ / 【右】氷彗星の範囲攻撃 |
| Arcanethyst | Halberd | Arcane Assault - 対象浮遊 / 【右】中型敵を持ち上げ地面へ叩きつけ |
| Tempest | Chakram | Vortex - 元素エネルギー付着（継続ダメ+属性デバフ）/ 【右】全元素回収で周囲に成長する渦 |
| Chompolotl | Chakram | Chompocalypse - ウーパールーパー召喚（肩に乗せると範囲バフ）/ 【右】レアな青個体召喚 |
| Hiveheart | Greathammer | Hivemind - ヒットで怒った蜂放出（3s CD）/ 【右】小蜂群で執拗な鈍足+連続ダメ |
| Soulkeeper | Greathammer | Soul Meld - 魂ランタン2個が周回ダメ、攻撃で加速 / 【右】ランタン+2個 |
| Hearthflame | Greathammer | Chains of the Furnace - 点火烙印 / 【右】溶鉱鎖で束縛、打撃が他束縛敵へ火ダメ反射. 圧力最大で全鎖破砕大ダメ |
| Decaying Relic | 未成形 | Deep Dark で形態変化: 衝撃波→Magiscythe / Sculk Sensor上→Magiblade / Darkness→Magispear |
| Sword on a Stick | 木 | 投擲できるだけのジョーク武器 |

### 変化、派生ツリー

```
Dormant Relic ──Lv4── 昼 → Righteous Relic ──Lv8──→ Sunfire
                     └ 夜 → Tainted Relic  ──Lv8──→ Harbinger
Decaying Relic ──Deep Dark で遭遇──→ Magiscythe / Magiblade / Magispear
Slumbering Lichblade ──Lv4──→ Waking Lichblade ──Lv8──→ Awakened Lichblade
The Watcher ──Lv4+ & Deep Dark の鍛冶──→ The Devourer
Stormscale ──Lv4+ & 雷雨時に鍛冶──→ Ionbound Stormscale
Devourer の捕食 ──→ Whisperwind→Dreadwhisper / Wickpiercer→Gloampiercer
                    Soulrender→Soulstalker / Wraithfang→Wraithmaw
```

## 立ち回りガイド

- コンボを覚えて武器を選ぶ: 3段目に大技が来る型（claymore の ×1.25 振り下ろし、mace の ×1.4）と、回転で戦う型（katana の 4 連、twin_blade の 2 連）では立ち回りが変わる.  "何段目を当てるか" を意識して振る. 
- 二刀流 or 両手の二択: 片手×2 なら攻速+20% で手数勝負、両手なら盾を捨てて単発火力と薙ぎ範囲. Guardian とは逆の割り切り. 
- Implicit で武器を見極める: 出血（Glaive/Greataxe/Halberd）は集団戦、装甲破壊（Greathammer）は重装甲、防御無視（Rapier/Spear）は高防御敵に. 拾った瞬間の個体差が強さを左右する. 
- Runic 化が第一目標: ネザライト武器 + Runic Tablet + ダイヤで Runic 化. パワーが最大強度で常時付くため、本命武器を1本育てる価値が高い. 
- Unique は覚醒して初めて本領: 拾ったまま（Lv0）では威力50%で技も出ない. Runic Tablet を集めて Lv4（技解放）→ Lv8（フル性能）を目指す. 
- 災害防衛: 襲撃の雑魚波を薙ぎ払い系コンボ（double_axe の 360° スピン、glaive の 180° 薙ぎ）で掃討する中核. Wildfire/Stormlash 持ち Runic 武器は集団処理に特に強い. 
- 異世界遠征: 黄昏の森、浮島世界では前衛の筆頭. ボス戦では単体倍率の高いコンボ最終段を確実に当てること. 

## お金の稼ぎ方

- ダンジョン、異世界のドロップ素材を、加工手段を持つ科学陣営プレイヤーへ販売. 
- Unique 武器、Runic Tablet の売買（0.05% ドロップは高値が付く）. 
- 災害イベントの防衛報酬（MD、希少素材）. 
- 採取は MineAll/CutAll（スニーク+ツール）で集めて売却するのも手. 

## こんな人に向いている

- 正面から殴り合うのが好きな人
- 武器ごとのコンボを使い分けるアクションが好きな人
- 複雑な管理より戦闘に集中したい人
