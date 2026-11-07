package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/** 江湖地图：可前往相邻之地（耗时一日）；在城镇时可乘驿站马车前往去过的其他城镇。 */
public class MapScene extends Scene {
    private RectF area;
    private final Path river = new Path();

    public MapScene(Game game) {
        super(game);
    }

    private float lx(GameData.Location l) {
        return area.left + l.x * area.width();
    }

    private float ly(GameData.Location l) {
        return area.top + l.y * area.height();
    }

    private int fare() {
        return 15 + game.player.level * 3;
    }

    @Override
    public void layout() {
        buttons.clear();
        area = new RectF(110, 270, W() - 110, H() - 240);
        Player p = game.player;
        GameData.Location cur = GameData.LOCATIONS[p.location];
        int questLoc = Story.current(p).loc;
        for (GameData.Location l : GameData.LOCATIONS) {
            final int id = l.id;
            boolean adjacent = GameData.linked(cur.id, id);
            boolean visited = p.has("v" + id);
            final boolean coach = cur.town && l.town && visited && !adjacent && id != p.location;
            float w = 150;
            float h = 78;
            Button b = addButton(lx(l) - w / 2, ly(l) - h / 2, w, h, l.name, () -> travel(id, coach));
            b.textSize = 28;
            if (id == p.location) {
                b.sub = "当前所在";
            } else if (coach) {
                b.sub = "驿站 " + fare() + "两";
            } else if (l.town) {
                b.sub = "城镇";
            } else {
                b.sub = "Lv." + l.minLv;
            }
            b.selected = id == p.location;
            b.enabled = adjacent || coach || id == p.location;
            b.color = l.town ? UI.JADE : (l.boss >= 0 ? UI.RED : UI.INK);
            if (id == questLoc && !p.finished) {
                b.color = UI.GOLD;
            }
        }
        addButton(36, H() - 150, W() - 72, 104, "返回" + cur.name, () -> Story.toLocation(game));
    }

    private void travel(int id, boolean coach) {
        Player p = game.player;
        if (id == p.location) {
            Story.toLocation(game);
            return;
        }
        GameData.Location l = GameData.LOCATIONS[id];
        if (coach) {
            if (p.silver < fare()) {
                game.toast("银两不足，坐不起马车");
                game.audio.sfx(Audio.SFX_FAIL);
                return;
            }
            p.silver -= fare();
            p.day += 2;
            game.toast("乘驿站马车，两日后抵达" + l.name);
        } else {
            p.day++;
        }
        if (p.level < l.minLv) {
            game.toast("此地凶险，建议 Lv." + l.minLv + " 再来");
        }
        game.goLocation(id);
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 5, game.time);
        UI.hud(c, game);
        RectF frame = new RectF(24, area.top - 70, W() - 24, area.bottom + 64);
        UI.panel(c, frame, 0xE8EFE2C4, UI.INK_LIGHT);
        // 长江
        river.reset();
        river.moveTo(frame.left, area.top + area.height() * 0.62f);
        river.cubicTo(area.left + area.width() * 0.3f, area.top + area.height() * 0.5f,
                area.left + area.width() * 0.55f, area.top + area.height() * 0.78f,
                frame.right, area.top + area.height() * 0.66f);
        c.drawPath(river, UI.stroke(0x552F5A8A, 24f));
        // 东海
        c.drawRect(frame.right - 40, frame.top + 8, frame.right - 8, frame.bottom - 8, UI.fill(0x332F5A8A));
        // 连线
        for (GameData.Location l : GameData.LOCATIONS) {
            for (int k : l.links) {
                if (k > l.id) {
                    GameData.Location o = GameData.LOCATIONS[k];
                    c.drawLine(lx(l), ly(l), lx(o), ly(o), UI.stroke(0x88231E19, 5f));
                }
            }
        }
        GameData.Location cur = GameData.LOCATIONS[game.player.location];
        float pulse = (float) (1 + 0.15 * Math.sin(game.time * 4));
        c.drawCircle(lx(cur), ly(cur), 96 * pulse, UI.fill(0x33A3291F));
        UI.text(c, "江湖地图", W() / 2, frame.top + 46, 36, UI.INK, Paint.Align.CENTER);
        drawButtons(c);
        UI.text(c, "金色为主线目的地 · 城镇间可乘驿站马车", W() / 2, frame.bottom - 18, 22, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
