# 江湖行 (my_wuxia)

一款手机竖屏的水墨风回合制武侠 RPG，纯 Java + Android Canvas 实现，无第三方依赖。

## 玩法

- **四大门派**：少林（血厚防高）、武当（均衡吸血）、峨眉（迅捷疗伤）、丐帮（刚猛控制），各有 4 门本门武学。
- **江湖地图**：临安城、洛阳城两座城镇与竹林、黑风寨、活死人墓、思过崖、黑木崖五处野外，只能前往相邻地点。
- **回合制战斗**：普攻回内力，武功分重击 / 连击 / 吸血 / 点穴 / 护体 / 身法 / 疗伤七类；暴击、闪避由身法决定。
- **武功熟练度**：每门武功越用越强，最高十重。
- **奇遇**：在特定野外探索可能习得凌波微步、吸星大法、独孤九剑。
- **主线五章**：竹林剪径 → 荡平黑风寨 → 古墓疑云 → 华山剑痕 → 决战黑木崖。
- 城镇可住客栈、逛集市（丹药 / 兵器 / 护甲）、拜师学艺；进度自动存档。

## 构建与发布

本地只做代码开发，不在本地构建。APK 由 GitHub Actions（`.github/workflows/android-apk-release.yml`）构建签名：

```bash
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

推送 `v*.*.*` 标签后，工作流会构建签名 APK，并以 `my_wuxia.apk` 的名字上传到对应 GitHub Release。签名依赖组织级 Actions 配置 `KEY_JKS`、`ALIAS`、`ANDROID_KEY_PASSWORD`、`ANDROID_STORE_PASSWORD`。

## 代码结构

| 文件 | 说明 |
| --- | --- |
| `MainActivity` / `GameView` | 全屏 Activity 与 720 宽虚拟画布 |
| `Game` / `Scene` / `Button` | 场景切换、触控分发、存档 |
| `GameData` | 门派、武功、物品、敌人、地点、主线数据 |
| `Player` / `Inventory` | 角色成长、JSON 存档、物品与装备 |
| `*Scene` | 标题、创建角色、剧情、地点、地图、战斗、商铺、武学、行囊 |
| `UI` / `Sprites` | 水墨风背景、面板、人物与野兽绘制 |
