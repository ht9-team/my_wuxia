package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 铁匠铺：强化当前装备的兵器与护甲（最高 +10），购买玄铁。 */
public class SmithScene extends Scene {
    private static final int IRON_PRICE = 150;

    public SmithScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        float y = 640;
        for (int slot = 0; slot < 2; slot++) {
            final int s = slot;
            int id = slot == 0 ? p.weapon : p.armor;
            String what = slot == 0 ? "兵器" : "护甲";
            Button b;
            if (id < 0) {
                b = addButton(36, y, W() - 72, 104, "强化" + what, null);
                b.sub = "尚未装备" + what;
                b.enabled = false;
            } else if (p.enhance[id] >= Inventory.MAX_PLUS) {
                b = addButton(36, y, W() - 72, 104, "强化" + what, null);
                b.sub = "已臻化境（+" + Inventory.MAX_PLUS + "）";
                b.enabled = false;
            } else {
                int cost = Inventory.enhanceCost(p, id);
                int iron = Inventory.enhanceIron(p, id);
                b = addButton(36, y, W() - 72, 104, "强化" + what + "  +" + p.enhance[id] + " → +" + (p.enhance[id] + 1),
                        () -> enhance(s));
                b.sub = "银两 " + cost + (iron > 0 ? " · 玄铁 " + iron : "") + " · 成功率 " + Inventory.enhanceRate(p, id) + "%";
                b.enabled = p.silver >= cost && p.items[Inventory.IRON] >= iron;
            }
            b.color = UI.RED;
            y += 120;
        }
        Button buy = addButton(36, y, W() - 72, 104, "购买玄铁  " + IRON_PRICE + " 两", () -> {
            Player pl = game.player;
            if (pl.silver < IRON_PRICE) {
                game.toast("银两不足");
                return;
            }
            pl.silver -= IRON_PRICE;
            pl.items[Inventory.IRON]++;
            game.audio.sfx(Audio.SFX_COIN);
            game.save();
            layout();
        });
        buy.sub = "持有玄铁 " + p.items[Inventory.IRON] + " 块";
        buy.enabled = p.silver >= IRON_PRICE;
        addButton(36, H() - 150, W() - 72, 104, "离开铁匠铺", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private void enhance(int slot) {
        Player p = game.player;
        int id = slot == 0 ? p.weapon : p.armor;
        if (id < 0 || p.enhance[id] >= Inventory.MAX_PLUS) {
            return;
        }
        int cost = Inventory.enhanceCost(p, id);
        int iron = Inventory.enhanceIron(p, id);
        if (p.silver < cost || p.items[Inventory.IRON] < iron) {
            game.toast("银两或玄铁不足");
            return;
        }
        p.silver -= cost;
        p.items[Inventory.IRON] -= iron;
        if (game.rnd.nextInt(100) < Inventory.enhanceRate(p, id)) {
            p.enhance[id]++;
            game.audio.sfx(Audio.SFX_EQUIP);
            game.toast("叮！" + GameData.ITEMS[id].name + " 强化至 +" + p.enhance[id]);
        } else {
            game.audio.sfx(Audio.SFX_FAIL);
            game.toast("火候差了一分……强化失败");
        }
        game.save();
        layout();
    }

    private void drawSlot(Canvas c, float y, String what, int id) {
        Player p = game.player;
        RectF r = new RectF(36, y, W() - 36, y + 170);
        UI.panel(c, r);
        UI.text(c, what, 64, y + 50, 26, UI.INK_LIGHT);
        if (id < 0) {
            UI.text(c, "空", 64, y + 104, 40, UI.INK_LIGHT);
            return;
        }
        GameData.Item it = GameData.ITEMS[id];
        String plus = p.enhance[id] > 0 ? " +" + p.enhance[id] : "";
        UI.text(c, it.name + plus, 64, y + 104, 40, UI.RED);
        String main = it.type == GameData.IT_WEAPON ? "攻击 " : "防御 ";
        UI.text(c, main + p.equipValue(id) + "（基础 " + it.value + "）", 64, y + 148, 24, UI.INK);
        UI.text(c, it.statText(), W() - 64, y + 50, 22, UI.BLUE, Paint.Align.RIGHT);
        if (it.special != GameData.SP_NONE) {
            UI.text(c, GameData.SPECIAL_DESC[it.special], W() - 64, y + 148, 20, UI.INK_LIGHT, Paint.Align.RIGHT);
        }
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 2, game.time);
        UI.hud(c, game);
        UI.title(c, "铁匠铺", W() / 2, 236, 46);
        Player p = game.player;
        drawSlot(c, 270, "兵器", p.weapon);
        drawSlot(c, 452, "护甲", p.armor);
        drawButtons(c);
        UI.text(c, "每强化一级，主属性提升一成；+4 起需要玄铁", W() / 2, H() - 166, 22, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
