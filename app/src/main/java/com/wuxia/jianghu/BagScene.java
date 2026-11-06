package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 行囊：角色属性、装备、物品使用。 */
public class BagScene extends Scene {
    private static final float GRID_TOP = 560;

    public BagScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        float gap = 14;
        float bw = (W() - 72 - gap) / 2f;
        int i = 0;
        for (int id = 0; id < p.items.length && i < 10; id++) {
            if (p.items[id] <= 0) {
                continue;
            }
            final GameData.Item it = GameData.ITEMS[id];
            final int itemId = id;
            float x = 36 + (i % 2) * (bw + gap);
            float y = GRID_TOP + (i / 2) * (92 + gap);
            Button b = addButton(x, y, bw, 92, it.name + " ×" + p.items[id], () -> use(itemId));
            b.textSize = 30;
            b.sub = it.consumable() ? "点击服用" : "点击装备";
            b.color = it.consumable() ? UI.JADE : UI.BLUE;
            i++;
        }
        addButton(36, H() - 150, W() - 72, 104, "合上行囊", () -> game.setScene(new LocationScene(game))).color = UI.INK_LIGHT;
    }

    private void use(int id) {
        Player p = game.player;
        GameData.Item it = GameData.ITEMS[id];
        if (it.consumable()) {
            if ((it.type == GameData.IT_HP && p.hp >= p.maxHp) || (it.type == GameData.IT_MP && p.mp >= p.maxMp)
                    || (it.type == GameData.IT_ALL && p.hp >= p.maxHp && p.mp >= p.maxMp)) {
                game.toast("无需服用");
                return;
            }
            p.items[id]--;
            Inventory.applyConsumable(p, it);
            game.toast("服下" + it.name);
        } else {
            Inventory.equip(p, id);
            game.toast("装备了" + it.name);
        }
        game.save();
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 2, game.time);
        UI.hud(c, game);
        Player p = game.player;
        RectF r = new RectF(36, 190, W() - 36, GRID_TOP - 24);
        UI.panel(c, r);
        int atkBonus = p.weapon >= 0 ? GameData.ITEMS[p.weapon].value : 0;
        int defBonus = p.armor >= 0 ? GameData.ITEMS[p.armor].value : 0;
        String[] k = {"攻击", "防御", "身法", "击败"};
        String[] v = {p.atk() + (atkBonus > 0 ? " (+" + atkBonus + ")" : ""),
                p.def() + (defBonus > 0 ? " (+" + defBonus + ")" : ""),
                String.valueOf(p.agi()), p.kills + " 人"};
        for (int i = 0; i < k.length; i++) {
            float x = 70 + (i % 2) * 310;
            float y = 250 + (i / 2) * 56;
            UI.text(c, k[i], x, y, 28, UI.INK_LIGHT);
            UI.text(c, v[i], x + 80, y, 32, UI.INK);
        }
        UI.text(c, "兵器：" + (p.weapon >= 0 ? GameData.ITEMS[p.weapon].name : "赤手空拳"), 70, 380, 28, UI.BLUE);
        UI.text(c, "护甲：" + (p.armor >= 0 ? GameData.ITEMS[p.armor].name : "粗布衣衫"), 380, 380, 28, UI.BLUE);
        UI.text(c, "门派：" + GameData.SECTS[p.sect].name + "    武学 " + p.skills.size() + " 门", 70, 436, 28, UI.INK);
        UI.text(c, "主线进度：" + Math.min(p.questStage, GameData.QUESTS.length) + "/" + GameData.QUESTS.length
                + (p.finished ? "  (已通关)" : ""), 70, 486, 28, UI.RED);
        if (buttons.size() <= 1) {
            UI.text(c, "行囊空空如也", W() / 2, GRID_TOP + 80, 32, UI.INK_LIGHT, Paint.Align.CENTER);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        game.setScene(new LocationScene(game));
        return true;
    }
}
