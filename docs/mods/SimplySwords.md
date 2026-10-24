# Simply Swords — MOD リファレンス

> **原作**: [Simply Swords (by Sweenus)](https://moddedmc.wiki/en/project/simplyswords/latest/docs/introduction)
> **リポジトリ**: https://github.com/Sweenus/SimplySwords （Architectury / Fabric・Forge・NeoForge、クライアント＋サーバ）
> **出典**: 公式 Wiki（`docs/*.mdx`）+ `en_us.json` + `ItemsRegistry.java` + `WeaponAttributesConfig.java`（Architectury-1.21 ブランチ）

## 概要

バニラの戦闘を拡張する大型武器 MOD。**15 種の武器タイプ**（×鉄/金/ダイヤ、ネザライト鍛冶で強化）、
武器種ごとの抽選ボーナス **Implicit**、ネザライト武器に刻む **Runic Power**、
ジェム装着による第 2 の力（**Runefused / Netherfused gem**）、
そして固有技を持つ **Unique 武器 55 種**で構成される。

- Better Combat とは**公式連携**（同梱の `weapon_attributes/*.json` で全武器に専用プリセットを割当。入れるとコンボモーションが出る）
- 武器ツールチップで **Alt キー**を押すと拡張情報（Implicit 範囲・ソケット・覚醒度）が見える
- アビリティ発動は専用キー **Use Mainhand/Offhand Weapon Ability**（未割当なら右クリック使用で代替）

## 特徴（システム）

- **武器タイプ別 Implicit**: 種類ごとに決まった効果が範囲抽選される恒久的ボーナス（下表）
- **進行**: Iron/Gold/Diamond → ネザライト鍛冶 → **Runic 武器**（Runic Tablet を鍛冶型 + ダイヤ追加）。Runic はパワー常に最大強度
- **Runic Tablet**: チェスト戦利品（60 回連続不発で確定のピティ）。Runic 武器のリロール・Unique 修繕・覚醒素材に使う
- **Runic Forge**（作業台: 黒曜石+アメジスト+鍛冶台+タブレット）: Unique へのタブレット投入（覚醒）とジェム着脱の専用 UI
- **ジェム**: Runefused（Runic 系）/ Netherfused（Nether 系）を武器のソケットに装着。不要ジェムは精錬で素材回収
- **Unique の覚醒**: 拾得時は覚醒 Lv0（威力 50%、攻速 75%、技は Lv4 まで封印）。タブレットを最大 8 個投入で Lv8 = フル性能
- **Contained Remnant**: 対応ブロックで使うと確定で特定 Unique を入手（例: 鉄ブロック→Mjolnir、魂火→Lichblade）
- **戦利品ピティ**: Unique 基本確率 0.05%、25 ミスから +0.2%/ミス、75 ミスで確定（プレイヤー単位）
- **フレンドリーファイア**: バニラチームを尊重。`abilityIgnoredEntities` でアビリティ対象外を指定（既定: passive/armor_stand/villager）

## 武器タイプと Implicit（全 15 種 + Unique 用 2 種）

数値はデフォルト。攻速は実効値（4+補正）、ダメージ補正は素材値への加算。

| タイプ | 攻速 | ダメ補正 | Implicit（抽選範囲） | 傾向 |
| :--- | ---: | ---: | :--- | :--- |
| Longsword | 1.6 | +0 | ダメージ弾き 5–15% | バランス型 |
| Claymore | 1.2 | +2 | ダメージ弾き 5–15% | 低速両手剣 |
| Rapier | 2.2 | -1 | 防御無視 15–35% | 高速突き |
| Spear | 1.3 | +0 | 防御無視 15–35% | 長リーチ突き（投擲可） |
| Cutlass | 2.0 | +0 | 追加戦利品 1–3% | 速い斬撃 |
| Katana | 2.0 | +0 | 2 倍ダメージ 5–15% | 高速精密剣 |
| Sai | 2.5 | -3 | バックスタブ +20–40% | 最速・低火力 |
| Dagger※ | — | — | バックスタブ +20–40% | Unique 専用 |
| Twinblade | 2.0 | +0 | 攻速上昇 5–25% | 両刃高速 |
| Chakram | 1.0 | -1 | 攻速上昇 5–25% | 円刃 |
| Warglaive | 1.8 | +0 | 2 連撃 5–15% | 双剣 |
| Glaive | 1.4 | +0 | 出血付与 10–60% | 薙ぎ払い長柄 |
| Greataxe | 0.9 | +3 | 出血付与 10–60% | 重い両手斧 |
| Halberd | 1.2 | +3 | 出血付与 10–60% | 重長柄 |
| Scythe | 1.3 | +1 | 低 HP 処刑 5–15% | 鎌 |
| Greathammer | 0.8 | +4 | 装甲破壊 2–10%/hit | 最速最遅・装甲破壊 |
| Hammer※ | — | — | 装甲破壊 2–6%/hit | Unique 専用 |

※ Dagger/Hammer は標準クラフト種なし。Unique の型としてのみ存在。

## 全技 — Runic Powers（26 種）

Runic 武器・Runefused ジェムに付くパワー。`[受動]`=ヒット時確率 or 常時、`[能動]`=武器アビリティキー/右クリ。

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
| Ward | 能動 | 現在 HP の半分を犠牲に、残 HP 比例の吸収を 6 秒間脈動付与 |
| Immolation | 能動 | 自他を周期的に灼くオーラ。与ダメは自分の現在 HP 比例 |
| Throwing | 能動 | 武器を投げる（軽い武器ほど遠くへ、滞空時間と角度で威力増）。Loyalty III 内蔵 |
| Active Defence | 受動 | 定期的に近くの敵へ矢を自動発射（インベントリの矢を消費） |
| Frost Ward | 受動 | 定期的に全近接敵へ鈍足スノーボールを発射 |
| Banehead Swarm | 受動 | 浮遊クリーパーヘッドが周回し、敵へ追尾自爆（範囲ダメージ） |
| Dancing Blades | 受動 | ヒット時、武器の幽体複製が周回して敵を斬る |
| Faultline | 受動 | ヒット時、前方の地面を裂いて破裂ダメージを走らせる |
| Runic Slash | 受動 | 振るたび貫通するルーン斬撃の飛び道具を放つ |
| Evocation | 受動 | 定期的に敵の足元へエヴォーカーの牙を召喚（鈍足+武器威力） |
| Sniffer Slam | 受動 | ヒット時、スニッファーが落下し敵を押し潰す（重武器ダメ+短時間行動不能） |
| Stormlash | 受動 | ヒット時、近接敵間を連鎖する雷を放つ |
| Imbued | 受動 | ヒット時、耐久値比例の追加魔法ダメージ |
| Pincushion | 受動 | 自分に刺さった矢 1 本ごとに与ダメージ増加 |

一部は `Greater`（強化版）抽選あり。

## 全技 — Nether Powers（11 種）

Netherfused ジェム（ソケット装着）のパワー。覚醒対応 Unique では覚醒 Lv に比例強化。

| パワー | 効果 |
| :--- | :--- |
| Berserk | 装備装甲値 10 未満のとき、攻撃が体力を吸収 |
| Echo | 斬撃が残響し、短い遅延後に軽減不能な追加ダメージ |
| Nullification | ヒット時、味方のデバフ解除＆敵のバフ剥がしを行う戦旗を召喚 |
| Onslaught | スロウ状態の敵を殴ると Onslaught 獲得（脈動する Haste、切れると Weakness） |
| Radiance | 弱化状態の敵を殴ると Immolation オーラを短時間獲得 |
| Verdant Trail | 武器を持つと花の軌跡が残る。味方を回復・敵を傷つける |
| Necromantic Arsenal | ヒット時、自分の武器の複製を持つ骸骨の従僕を召喚 |
| Wolf Pack | ヒット時、武器の力を共有した狼の従僕を召喚（武器アビリティを多用） |
| Goat Stampede | ヒット時、ヤギの突進群を召喚（進路上の敵にダメ+ノックバック）。同時 1 群 |
| Wing Buffet | ヒット時、幽体の竜翼が正面へ破壊的な突風を放つ |
| Dragon's Maw | 敵が近いと頭上に浮遊する竜の頭が出現し、標的へ炎のブレスを吐く |

## 全技 — Unique 武器（55 種）

チェスト極低確率 or ボスドロップ or Contained Remnant で入手。修繕は Runic Tablet。
表記: **技名** — 受動効果 / 【右クリック(or 長押し)】能動効果。

| Unique | 型 | 技 |
| :--- | :--- | :--- |
| Brimstone Claymore | Claymore | **Brimstone** — ヒット時確率で炸裂する噴石を噴出 / 【右】対象頭上に大剣を召喚、範囲パルス後に地面へ突き刺さる |
| The Watcher | Claymore | **The Unblinking Hunt** — ヒットで対象に Dread 蓄積（最大5）・コウモリが出る / 【右】コウモリ群が敵を切り裂き最後に残 HP 比例の一撃。最大 Dread なら HP25% 以下を処刑。討伐で CD リセット |
| The Devourer | Claymore | **Devouring Mass** — 弾き成功で攻撃者の足下に深淵の顎を開き引きずり込む（24 ブロック先まで反撃）/ 【右】深淵の種を射出し浮遊する巨大な塊を開花。触手で敵を捕らえ捕食、Gloam を撒く |
| Storm's Edge | Twinblade | **Stormbreak** — ヒットで CD 更新の確率 / 【右】雷となってダッシュ（無敵）し敵を切り抜け雷鳴を放つ。残存する速度+採掘速度 |
| Stormscale | Glaive | **Lightning Rod** — 設置中のグレイヴ周辺に近接攻撃連動の範囲ダメージ / 【右】幽体グレイヴを設置（40s）。攻撃ごとにパルス威力+半径 +1%（最大 80%） |
| Ionbound Stormscale | Glaive | **Ion Containment** — 8 秒ごとに Ion Cube 生成（最大3）。大ダメージを受けると Cube で完全無効化+2 秒の防護フィールド / 【右】Cube 消費で 12m の雷撃回廊を召喚（引寄せ→閉門大ダメ）。1 秒以内の再発動で照準追従ビーム 3 秒（敵は麻痺 5s）※Stormscale を Lv4+ で雷雨時に鍛冶 |
| Stormbringer | Longsword | **Shock Deflect** — Storm Charge 消費でヒット時連鎖雷 / 【右】短時間の完全防御。ジャストタイミングでカウンター+Charge 獲得（疑似パリィ） |
| Mjolnir | Hammer | **Storm** — ヒットで敵を帯電（Conductive）化 / 【右】移動する嵐を召喚し周辺へ連続落雷、最後に強力な雷鳴。帯電敵は被撃時に小雷鳴を放つ。投擲可 |
| Thunderbrand | Halberd | **Thunder Blitz** — ヒットで CD 更新の確率 / 【長押し】無敵で溜め、完了後ダッシュで敵を抜き、溜め中に受けたはずのダメ回数分の連鎖雷を放つ |
| Enigma | Claymore | **Galeforce** — 竜巻の近くで Haste 獲得 / 【右】敵を追う竜巻を召喚。捕らえた時間だけダメージ増大 |
| Ribboncleaver | Claymore | **Ribbonwrath** — 常時 移動 -5%・被ダメ -15% / 【右】前方ダッシュ+以後数ヒットを無効化+ノックバック耐性、次の一撃が単体 +95% |
| Caelestis | Claymore | **Astral Breach** — / 【右】異次元の裂け目を 45 秒開く（半径 20B、破損生物を召喚）。終了時 5 秒で収縮。5% で自分を狩る Unbound が漏出 |
| Lichblade（3 形態） | Claymore | **Soul Anguish I–III** — 苦しむ魂が放射され周辺敵にダメージ / 【長押し】魂を遠方の標的へ送り連続ダメ+確率で体力吸収。LvIII で帰還時に吸収付与（Slumbering Lv0–3 / Waking Lv4–7 / Awakened Lv8） |
| Waxweaver | Claymore | **Waxweave** — 燃えた敵を殴ると力+採掘速度。致命傷を受けると代わりに全回復+耐性（60s CD） / 【右】敵を蜜蝋に封じ行動不能化+他敵の標的化。終了/死亡時に爆発 |
| Riftmane | Claymore | **Vanguard** — 4–16B 先への振りで裂け目から幽体戦馬を放つ / 【右】5 騎の突撃陣を放ち進路上を蹂躙。スプリント中発動で中央の馬に騎乗 |
| Dawnquiver | Claymore | **Seraph's Draw** — 幽体の小弓が定期的に最遠の敵を射て Dawn Chorus を蓄積（最大3） / 【長押し】光の大弓。Chorus 消費で Dawn Volley（1: 2 射）/Sunlance（2: 4 体貫通）/Seraphic Chorus（3: 収束する 3 弓召喚） |
| Twisted Blade | Claymore | **Ferocity** — ヒットで攻速スタック（最大15）、スタック中は範囲打撃 Crescendo 発動 / 【右】全スタック消費で次の命中を Grand Crescendo に強化 |
| Sunfire | Longsword | **Righteous Standard** — ヒットで確率回復 / 【右】正義の軍旗を召喚：敵を点火・ダメージ・鈍足し、周囲の味方へ力+体力を定期的付与 |
| Harbinger | Longsword | **Abyssal Standard** — ヒットで確率弱化 / 【右】深淵の軍旗を召喚：敵を引寄せ・ダメージ・鈍足し、味方へ Haste 付与 |
| Dormant Relic → Righteous / Tainted Relic | Longsword | 覚醒進行型。Lv4 で昼=Righteous（Righteous Standard）/ 夜=Tainted（Abyssal Standard）に変化、Lv8 で Sunfire / Harbinger へ |
| Longsword of the Plague | Longsword | **Death Knell** — ヒットで fever 蓄積、5 で疫鐘が鳴り範囲ダメ+fever 伝播（最大 6 体連鎖）。ヒットで敵のバフを悪疫に変換し次の鐘を強化 |
| Bramblethorn | Rapier | **Grasp of the Wild** — 前ターゲットから現ターゲットへ茨を伸ばし経路ダメ+鈍足。複数敵を束縛し、束縛中の 1 体へのダメが他にも反射 |
| Shadowsting | Rapier | **Shadow Dance** — ヒット時自分の攻撃を反復する影分身を召喚 / 【右】短い無敵で近接敵間を高速シャドーステップし各敵に武器ダメージ |
| Whisperwind | Katana | **Fatal Flicker** — ヒットで CD 更新の確率 / 【右】高速前進ダッシュ（耐性+吸収）、通過した敵は遅延後にダメージ |
| Dreadwhisper | Katana | **Shadow Rend** — 命中で Corrupted Wound 付与、傷ついた敵への攻撃は必殺化し傷を破裂 / 【右】闇の壁となって敵を抜く突進（無敵）。経路の敵に武器ヒット+傷+削った分だけ回復、Gloam を撒く ※Devourer が Whisperwind を捕食すると変化 |
| Emberlash | Dagger | **Smoulder** — 攻撃で Smouldering 蓄積、付与中の敵へ 1 スタックごと +0.2 ダメ / 【右】後方回避しながら傷を焼いて塞ぎ最大 HP15% 回復 |
| Soulstealer | Dagger | **Soul Steal** — ヒットで魂の負債を奪う（キルで追加） / 【右】全負債を消費し対象背後へブリンクして背刺し（最大 5 倍武器ダメ） |
| Emberblade | Longsword | **Ember Ire** / 【長押し】ブレードにエネルギー充填、解放で照準先へ灼熱の破片射出（溜めほど強化）。ラスト 1 秒の解放で破片+1。解放時に溜め比例で Speed/Haste/Strength の確率 |
| Watching Warglaive | Warglaive | **The Unblinking Hunt** — Dread 蓄積+コウモリ出現（最大5） / 【右】Dread 付きの近接敵へコウモリを放ち、消費スタック分だけ攻撃+体力吸収 |
| Star's Edge | Twinblade | **Astral Reprise** — 昼: 追加ダメージ / 夜: 吸血 / 【右】前方ダッシュして星座の軌跡を残す。触れた敵にダメージ、消滅時に各星が爆発 |
| Magiblade | Twinblade | **Magisonic** — 接近する敵を時々検出して撃退 / 【長押し】溜め後、Warden の頭が 15 秒周回して周期的に衝撃波を発射（再使用で延長） |
| Magiscythe | Scythe | **Magistorm** — 発動中の攻撃で装備品修理の確率 / 【右】頭上に魔法の嵐を形成し周辺に落雷。落雷ごとに確率で延長+激化 |
| Soulrender | Scythe | **Soul Rend** — ヒットで弱化デバフを蓄積付与 / 【右】周辺敵の全スタックを消費し、量に応じたダメージ+回復 |
| Soulstalker | Scythe | **Stygian Stride** — 深淵の触手が定期的に背中から出て近接敵を貫く / 【右】6 本の深淵脚に乗って走行（溜めジャンプで跳躍）。足音がダメ+Gloam、振ると影の裂撃 ※Devourer が Soulrender を捕食すると変化 |
| Wraithfang | Cutlass | **Spectral Leap** — 内蔵 Loyalty I / 【右】投げた Wraithfang へ跳躍（無敵）、到達で Haste |
| Wraithmaw | Cutlass | **Spectral Downpour** — 回収した幽体カットラスが頭上に浮遊し、振りに合わせて標的を追尾 / 【右】幽体剣を対象地点に降らせ、着弾で武器ダメ+Gloam ※Devourer が Wraithfang を捕食すると変化 |
| Bloodwake | Cutlass | **Crimson Revelry** — ヒットで Bleed。最大 Bleed の敵を殴ると血の爆発+Blood Frenzy 獲得。血染めの地面は敵を鈍足・自分を回復 / 【右】Frenzy 全消費で 5 種の血儀: 1=血の波、2=絶叫（出血敵数だけ攻速+、最大60%）、3=出血敵全員の頭上に血剣降下、4=血蠅の群れ、5=Red Tide（5 秒間毎秒血の波） |
| Frostfall | Hammer | **Frost Fury** — 内蔵 Loyalty III / 【右】前方に投擲（滞空+角度で威力）。着弾後 5 回パルスしダメ+鈍足+引寄せ |
| Livyatan | Greataxe | **Leviathan's Wake** — 振ると波を放つ、内蔵 Loyalty III / 【右】前方投擲。帰還時に敵を引寄せる波+確率落雷 |
| Soulpyre | Greataxe | **Soul Tether** / 【右】周囲をネザー化し、内部で敵を倒すほど拡大。発動中ノックバック耐性+被ダメ50%減。倒した敵の魂が誘導弾として飛ぶ |
| Molten Edge | Greataxe | **Furnace Heart** — 与/被ダメで Heat 蓄積（双方ダメ増）。最大で与+50%/被+100%+自燃、持ち替えで喪失 / 【右】放熱して溶岩衝撃波。放熱中は振りごとにマグマ破裂 |
| Flamewind | Glaive | **Emberstorm** — 炎の種の爆発で Haste / 【右】最寄りの敵に炎の種を植え付け持続ダメ。終了時に爆発して周囲へ伝播（最大 6 連鎖） |
| Gloampiercer | Spear | **Phantom Phalanx** — 3–12B 先への振りで空中の影分身が爆発槍を投擲 / 【右】宙に浮き分身の槍隊で範囲爆撃（各槍は武器ヒット+Gloam 残留） ※Devourer が Wickpiercer を捕食すると変化 |
| Magispear | Spear | **Magislam** — ヒットで追加魔法ダメ、内蔵 Loyalty III / 【右】槍を上空へ打ち上げ魔法の槍雨を降らせ、終了時に対象地点へ跳躍して叩きつけ（跳躍中無敵） |
| Wickpiercer | Spear | **Flicker Fury** — 内蔵 Loyalty III / 【右】投擲で frenzy スタック（攻速+二連撃 4 秒）。最大 5、被弾で喪失 |
| Icewhisper | Halberd | **Permafrost** — 常時 4B 内の敵へダメ+鈍足の冷気オーラ / 【右】周囲へ氷の彗星を降らせ着弾範囲ダメージ |
| Arcanethyst | Halberd | **Arcane Assault** — ヒットで対象を浮遊 / 【右】周辺の中型敵を魔力で持ち上げ地面へ叩きつける |
| Tempest | Chakram | **Vortex** — 攻撃で対象に元素エネルギー付着（継続ダメ+属性デバフ、蓄積可） / 【右】全元素を回収して自分の周囲に成長する元素の渦を形成 |
| Chompolotl | Chakram | **Chompocalypse** — ヒットでウーパールーパー召喚（敵を攻撃、肩に乗せると範囲バフ） / 【右】レアな青ウーパールーパーを召喚 |
| Hiveheart | Greathammer | **Hivemind** — ヒットで怒った蜂を放つ（3 秒 CD） / 【右】小蜂の群れで周辺敵を執拗に鈍足+連続ダメージ |
| Soulkeeper | Greathammer | **Soul Meld** — 2 つの魂ランタンが周回し触れた敵にダメ、攻撃で回転加速 / 【右】一定時間ランタン +2 個 |
| Hearthflame | Greathammer | **Chains of the Furnace** — ヒットで点火の烙印（縛りやすくなる） / 【右】近接敵を溶鉱の鎖で束縛。束縛敵への打撃は他の束縛敵へ火ダメ反射。圧力最大で次撃が全鎖を砕き範囲大ダメ |
| Decaying Relic | 未成形 | 変化型武器。Deep Dark で保持していると形態変化: Warden の衝撃波→**Magiscythe**、Sculk Sensor 上→**Magiblade**、Darkness 付与→**Magispear** |
| Sword on a Stick | 木 | 投擲可能なだけのジョーク武器 |

### 変化・派生ツリー

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

## BCN での近似メモ

- BCN 実装済: `PowerWeaponItem` 右クリック発動で katana→ダッシュ斬撃(Whisperwind 相当), greatsword→衝撃波, spear→ラウンジ, glaive→旋風, rapier→連撃, claymore→範囲打撃+弱体, war_dagger→毒, scythe→ドレイン（クールダウン 60–100t）。
- 原作はパッシブ（ヒット確率発動）+ 能動（右クリ/長押し）の 2 層構造。BCN は能動のみ近似。
- Runic/Nether powers の「確率オンヒット」系はサーバ側で再現可能（範囲召喚・DoT・バフ）。アニメーション/ブリンク/騎乗演出系は実クライアント必須。
