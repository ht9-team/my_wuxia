package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

/** 城镇商铺：购买丹药和兵器护甲，更好的装备会自动换上。 */
public class ShopScene extends Scene {
    public ShopScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        GameData.Location l = GameData.LOCATIONS[p.location];
        float y = 270;
        for (int id : l.shop) {
            final GameData.Item it = GameData.ITEMS[id];
            String owned = it.consumable() ? "  持有 " + p.items[id] : (p.weapon == id || p.armor == id ? "  已装备" : "");
            Button b = addButton(36, y, W() - 72, 96, it.name + "    " + it.price + " 两" + owned, () -> buy(it));
            b.textSize = 30;
            b.sub = it.desc;
            b.enabled = p.silver >= it.price;
            b.color = it.consumable() ? UI.JADE : UI.BLUE;
            y += 108;
        }
        addButton(36, H() - 150, W() - 72, 104, "离开商铺", () -> game.setScene(new LocationScene(game))).color = UI.INK_LIGHT;
    }

    private void buy(GameData.Item it) {
        Player p = game.player;
        if (p.silver < it.price) {
            game.toast("银两不足");
            return;
        }
        p.silver -= it.price;
        p.items[it.id]++;
        String msg = "购得 " + it.name;
        if (it.type == GameData.IT_WEAPON) {
            int cur = p.weapon >= 0 ? GameData.ITEMS[p.weapon].value : 0;
            if (it.value > cur) {
                Inventory.equip(p, it.id);
                msg += "，已装备";
            }
        } else if (it.type == GameData.IT_ARMOR) {
            int cur = p.armor >= 0 ? GameData.ITEMS[p.armor].value : 0;
            if (it.value > cur) {
                Inventory.equip(p, it.id);
                msg += "，已装备";
            }
        }
        game.toast(msg);
        game.save();
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 3, game.time);
        UI.hud(c, game);
        UI.title(c, GameData.LOCATIONS[game.player.location].name + " · 集市", W() / 2, 236, 46);
        drawButtons(c);
        Player p = game.player;
        String eq = "兵器：" + (p.weapon >= 0 ? GameData.ITEMS[p.weapon].name : "无") + "    护甲：" + (p.armor >= 0 ? GameData.ITEMS[p.armor].name : "无");
        UI.text(c, eq, W() / 2, H() - 170, 26, UI.INK, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        game.setScene(new LocationScene(game));
        return true;
    }
}
