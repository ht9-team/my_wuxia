package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/** 行囊：角色属性、三件装备（点击卸下）、物品分栏（点击使用 / 装备，出售模式下点击出售）。 */
public class BagScene extends Scene {
    private static final float GRID_TOP = 580;
    private int tab = 0;
    private int page = 0;
    private boolean sellMode = false;

    public BagScene(Game game) {
        super(game);
    }

    private boolean inTab(GameData.Item it) {
        switch (tab) {
            case 0:
                return it.consumable();
            case 1:
                return it.equipable();
            default:
                return !it.consumable() && !it.equipable();
        }
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        // 装备栏
        float ew = (W() - 72 - 28) / 3f;
        String[] what = {"兵器", "护甲", "饰品"};
        int[] ids = {p.weapon, p.armor, p.accessory};
        for (int i = 0; i < 3; i++) {
            final int slot = i;
            int id = ids[i];
            String name = id >= 0 ? GameData.ITEMS[id].name + (p.enhance[id] > 0 ? "+" + p.enhance[id] : "") : "（空）";
            Button b = addButton(36 + i * (ew + 14), 384, ew, 92, name, () -> {
                Inventory.unequip(game.player, slot);
                game.audio.sfx(Audio.SFX_EQUIP);
                game.save();
                layout();
            });
            b.textSize = 26;
            b.sub = what[i] + (id >= 0 ? " · 点击卸下" : "");
            b.enabled = id >= 0;
            b.color = UI.BLUE;
        }
        // 分栏
        String[] tabs = {"丹药", "装备", "杂物"};
        float tw = (W() - 72 - 42) / 4f;
        for (int i = 0; i < 3; i++) {
            final int t = i;
            Button b = addButton(36 + i * (tw + 14), 494, tw, 66, tabs[i], () -> {
                tab = t;
                page = 0;
                layout();
            });
            b.textSize = 28;
            b.selected = tab == i;
        }
        Button sell = addButton(36 + 3 * (tw + 14), 494, tw, 66, sellMode ? "出售中" : "出售", () -> {
            sellMode = !sellMode;
            layout();
        });
        sell.textSize = 28;
        sell.selected = sellMode;
        sell.color = UI.GOLD;

        // 物品列表
        final ArrayList<Integer> list = new ArrayList<>();
        for (int id = 0; id < p.items.length; id++) {
            if (p.items[id] > 0 && inTab(GameData.ITEMS[id])) {
                list.add(id);
            }
        }
        float gap = 14;
        float bw = (W() - 72 - gap) / 2f;
        float bottom = H() - 270;
        int rows = Math.max(1, (int) ((bottom - GRID_TOP) / (88 + 12)));
        final int per = rows * 2;
        final int pageCount = pages(list.size(), per);
        if (page >= pageCount) {
            page = 0;
        }
        int i = 0;
        for (int k = page * per; k < Math.min(list.size(), page * per + per); k++) {
            final int id = list.get(k);
            GameData.Item it = GameData.ITEMS[id];
            float x = 36 + (i % 2) * (bw + gap);
            float y = GRID_TOP + (i / 2) * (88 + 12);
            String plus = it.equipable() && p.enhance[id] > 0 ? "+" + p.enhance[id] : "";
            Button b = addButton(x, y, bw, 88, it.name + plus + " ×" + p.items[id], () -> use(id));
            b.textSize = 28;
            if (sellMode) {
                b.sub = Inventory.sellable(it) ? "出售 " + Inventory.sellPrice(it) + " 两" : "不可出售";
                b.color = UI.GOLD;
            } else {
                b.sub = it.equipable() ? it.statText() : (it.consumable() ? "点击服用" : it.desc);
                b.color = it.consumable() ? UI.JADE : (it.equipable() ? UI.BLUE : UI.INK_LIGHT);
            }
            i++;
        }
        if (pageCount > 1) {
            addButton(36, H() - 254, bw, 84, "上一页", () -> {
                page = (page + pageCount - 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
            addButton(36 + bw + gap, H() - 254, bw, 84, "下一页", () -> {
                page = (page + 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
        }
        addButton(36, H() - 150, W() - 72, 104, "合上行囊", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private void use(int id) {
        Player p = game.player;
        GameData.Item it = GameData.ITEMS[id];
        if (p.items[id] <= 0) {
            return;
        }
        if (sellMode) {
            if (!Inventory.sellable(it)) {
                game.toast("此物意义非凡，不可出售");
                return;
            }
            int price = Inventory.sellPrice(it);
            p.items[id]--;
            p.silver += price;
            game.audio.sfx(Audio.SFX_COIN);
            game.toast("卖出 " + it.name + "，得银 " + price + " 两");
        } else if (it.consumable()) {
            if ((it.type == GameData.IT_HP && p.hp >= p.maxHp()) || (it.type == GameData.IT_MP && p.mp >= p.maxMp())
                    || (it.type == GameData.IT_ALL && p.hp >= p.maxHp() && p.mp >= p.maxMp())) {
                game.toast("无需服用");
                return;
            }
            p.items[id]--;
            String eff = Inventory.applyConsumable(p, it);
            game.audio.sfx(Audio.SFX_HEAL);
            game.toast("服下" + it.name + "，" + eff);
        } else if (it.equipable()) {
            Inventory.equip(p, id);
            game.audio.sfx(Audio.SFX_EQUIP);
            game.toast("装备了" + it.name);
        } else {
            game.toast(it.desc);
            return;
        }
        game.save();
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 2, game.time);
        UI.hud(c, game);
        Player p = game.player;
        RectF r = new RectF(36, 182, W() - 36, 372);
        UI.panel(c, r);
        String[] k = {"攻击", "防御", "身法", "暴击", "气血", "内力"};
        String[] v = {String.valueOf(p.atk()), String.valueOf(p.def()), String.valueOf(p.agi()), p.crit() + "%",
                String.valueOf(p.maxHp()), String.valueOf(p.maxMp())};
        for (int i = 0; i < k.length; i++) {
            float x = 64 + (i % 3) * 212;
            float y = 232 + (i / 3) * 52;
            UI.text(c, k[i], x, y, 24, UI.INK_LIGHT);
            UI.text(c, v[i], x + 64, y, 30, UI.INK);
        }
        String wt = GameData.WEAPON_TYPES[p.weaponType()];
        String sp = GameData.SPECIAL_NAMES[p.weaponSpecial()];
        UI.text(c, "兵器类别：" + wt + (sp.isEmpty() ? "" : "   兵器特效：" + sp), 64, 338, 24, UI.BLUE);
        UI.text(c, "击败 " + p.kills, W() - 64, 338, 24, UI.INK_LIGHT, Paint.Align.RIGHT);
        int count = 0;
        for (Button b : buttons) {
            if (b.rect.top >= GRID_TOP && b.rect.top < H() - 270) {
                count++;
            }
        }
        if (count == 0) {
            UI.text(c, "空空如也", W() / 2, GRID_TOP + 90, 32, UI.INK_LIGHT, Paint.Align.CENTER);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
