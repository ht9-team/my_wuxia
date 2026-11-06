package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 江湖地图：只能前往与当前地点相连的地方，每次赶路耗时一日。 */
public class MapScene extends Scene {
    private RectF area;

    public MapScene(Game game) {
        super(game);
    }

    private float lx(GameData.Location l) {
        return area.left + l.x * area.width();
    }

    private float ly(GameData.Location l) {
        return area.top + l.y * area.height();
    }

    @Override
    public void layout() {
        buttons.clear();
        area = new RectF(60, 250, W() - 60, H() - 220);
        Player p = game.player;
        GameData.Location cur = GameData.LOCATIONS[p.location];
        for (GameData.Location l : GameData.LOCATIONS) {
            final int id = l.id;
            float w = 172;
            float h = 76;
            Button b = addButton(lx(l) - w / 2, ly(l) - h / 2, w, h, l.name, () -> travel(id));
            b.textSize = 30;
            boolean linked = false;
            for (int k : cur.links) {
                if (k == id) {
                    linked = true;
                }
            }
            b.selected = id == p.location;
            b.enabled = linked || id == p.location;
            b.color = l.town ? UI.JADE : (l.boss >= 0 ? UI.RED : UI.INK);
            GameData.Quest q = p.currentQuest();
            if (q != null && q.location == id) {
                b.color = UI.GOLD;
            }
        }
        addButton(36, H() - 150, W() - 72, 104, "返回" + cur.name, () -> game.setScene(new LocationScene(game)));
    }

    private void travel(int id) {
        Player p = game.player;
        if (id == p.location) {
            game.setScene(new LocationScene(game));
            return;
        }
        GameData.Location l = GameData.LOCATIONS[id];
        p.day++;
        if (p.level < l.minLv) {
            game.toast("此地凶险，建议 Lv." + l.minLv + " 再来");
        }
        game.goLocation(id);
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 5, game.time);
        UI.hud(c, game);
        RectF frame = new RectF(area.left - 30, area.top - 50, area.right + 30, area.bottom + 50);
        UI.panel(c, frame, 0xE8EFE2C4, UI.INK_LIGHT);
        // 河流
        android.graphics.Path river = new android.graphics.Path();
        river.moveTo(frame.left, area.top + area.height() * 0.7f);
        river.cubicTo(area.left + area.width() * 0.3f, area.top + area.height() * 0.6f,
                area.left + area.width() * 0.5f, area.top + area.height() * 0.8f,
                frame.right, area.top + area.height() * 0.55f);
        c.drawPath(river, UI.stroke(0x552F5A8A, 26f));
        // 连线
        for (GameData.Location l : GameData.LOCATIONS) {
            for (int k : l.links) {
                if (k > l.id) {
                    GameData.Location o = GameData.LOCATIONS[k];
                    c.drawLine(lx(l), ly(l), lx(o), ly(o), UI.stroke(0x88231E19, 5f));
                }
            }
        }
        // 当前位置光晕
        GameData.Location cur = GameData.LOCATIONS[game.player.location];
        float pulse = (float) (1 + 0.15 * Math.sin(game.time * 4));
        c.drawCircle(lx(cur), ly(cur), 100 * pulse, UI.fill(0x33A3291F));
        UI.text(c, "江湖地图", W() / 2, area.top - 6, 40, UI.INK, Paint.Align.CENTER);
        drawButtons(c);
        for (GameData.Location l : GameData.LOCATIONS) {
            if (!l.town) {
                UI.text(c, "Lv." + l.minLv, lx(l), ly(l) + 66, 22, UI.INK_LIGHT, Paint.Align.CENTER);
            }
        }
        UI.text(c, "金色为主线目的地 · 仅可前往相邻之地", W() / 2, area.bottom + 40, 24, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        game.setScene(new LocationScene(game));
        return true;
    }
}
