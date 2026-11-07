package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/** 城镇集市：分「丹药」「兵甲」两栏，可翻页。 */
public class ShopScene extends Scene {
    private int tab = 0;
    private int page = 0;

    public ShopScene(Game game) {
        super(game);
    }

    private ArrayList<Integer> stock() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int id : GameData.LOCATIONS[game.player.location].shop) {
            GameData.Item it = GameData.ITEMS[id];
            boolean gear = it.equipable();
            if ((tab == 1) == gear) {
                list.add(id);
            }
        }
        return list;
    }

    @Override
    public void layout() {
        buttons.clear();
        final Player p = game.player;
        float tw = (W() - 72 - 14) / 2f;
        Button t0 = addButton(36, 196, tw, 70, "丹药材料", () -> {
            tab = 0;
            page = 0;
            layout();
        });
        t0.selected = tab == 0;
        t0.textSize = 30;
        Button t1 = addButton(36 + tw + 14, 196, tw, 70, "兵器护甲", () -> {
            tab = 1;
            page = 0;
            layout();
        });
        t1.selected = tab == 1;
        t1.textSize = 30;

        final ArrayList<Integer> list = stock();
        float top = 286;
        float bottom = H() - 270;
        final int per = Math.max(1, (int) ((bottom - top) / 108));
        final int pageCount = pages(list.size(), per);
        if (page >= pageCount) {
            page = 0;
        }
        float y = top;
        for (int k = page * per; k < Math.min(list.size(), page * per + per); k++) {
            final GameData.Item it = GameData.ITEMS[list.get(k)];
            String owned = " 持有" + p.items[it.id];
            Button b = addButton(36, y, W() - 72, 96, it.name + "  " + it.price + "两 " + owned, () -> buy(it));
            b.textSize = 30;
            b.sub = it.equipable() ? it.statText() : it.desc;
            b.enabled = p.silver >= it.price;
            b.color = it.equipable() ? UI.BLUE : UI.JADE;
            y += 108;
        }
        if (pageCount > 1) {
            float bw = (W() - 72 - 14) / 2f;
            addButton(36, H() - 254, bw, 84, "上一页", () -> {
                page = (page + pageCount - 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
            addButton(36 + bw + 14, H() - 254, bw, 84, "下一页", () -> {
                page = (page + 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
        }
        addButton(36, H() - 150, W() - 72, 104, "离开集市", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private void buy(GameData.Item it) {
        Player p = game.player;
        if (p.silver < it.price) {
            game.toast("银两不足");
            game.audio.sfx(Audio.SFX_FAIL);
            return;
        }
        p.silver -= it.price;
        p.items[it.id]++;
        game.audio.sfx(Audio.SFX_COIN);
        game.toast(it.equipable() ? "购得 " + it.name + "（在行囊中装备）" : "购得 " + it.name);
        game.save();
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 3, game.time);
        UI.hud(c, game);
        drawButtons(c);
        Player p = game.player;
        String eq = "兵器：" + (p.weapon >= 0 ? GameData.ITEMS[p.weapon].name : "空手") + "   护甲："
                + (p.armor >= 0 ? GameData.ITEMS[p.armor].name : "无");
        UI.text(c, eq, W() / 2, H() - 164, 24, UI.INK, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
