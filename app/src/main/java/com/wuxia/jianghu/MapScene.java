package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * 江湖地图（可上下拖动）：可前往相邻之地（耗时一日）；在城镇时可乘驿站马车前往去过的其他城镇。
 * 只显示自己门派的师门；隐藏终章开启后才会出现「归墟」。
 */
public class MapScene extends Scene {
    private static final float MAP_H = 1300f;
    private static final float NODE_W = 150f;
    private static final float NODE_H = 78f;

    private final Path river = new Path();
    private float viewTop;
    private float viewBottom;
    private float scroll = -1f;
    private float downY;
    private float lastY;
    private boolean dragging = false;
    private Button back;

    public MapScene(Game game) {
        super(game);
    }

    private float left() {
        return 110f;
    }

    private float width() {
        return W() - 220f;
    }

    private float lx(GameData.Location l) {
        return left() + l.x * width();
    }

    private float ly(GameData.Location l) {
        return viewTop + 50 + l.y * MAP_H - scroll;
    }

    private float maxScroll() {
        return Math.max(0f, MAP_H + 100 - (viewBottom - viewTop));
    }

    private int fare() {
        return 15 + game.player.level * 3;
    }

    @Override
    public void layout() {
        buttons.clear();
        viewTop = 230;
        viewBottom = H() - 180;
        Player p = game.player;
        GameData.Location cur = GameData.LOCATIONS[p.location];
        if (scroll < 0) {
            scroll = cur.y * MAP_H - (viewBottom - viewTop) / 2f + 50;
        }
        scroll = Math.max(0f, Math.min(maxScroll(), scroll));
        int questLoc = Story.current(p).loc(p);
        for (GameData.Location l : GameData.LOCATIONS) {
            if (!GameData.visible(p, l.id)) {
                continue;
            }
            final int id = l.id;
            boolean adjacent = GameData.linked(cur.id, id);
            boolean visited = p.has("v" + id);
            final boolean coach = cur.town && l.town && visited && !adjacent && id != p.location;
            float cx = lx(l);
            float cy = ly(l);
            Button b = addButton(cx - NODE_W / 2, cy - NODE_H / 2, NODE_W, NODE_H, l.name, () -> travel(id, coach));
            b.textSize = 28;
            if (id == p.location) {
                b.sub = "当前所在";
            } else if (coach) {
                b.sub = "驿站 " + fare() + "两";
            } else if (l.town) {
                b.sub = "城镇";
            } else if (id == Sects.home(p)) {
                b.sub = "师门";
            } else {
                b.sub = "Lv." + l.minLv;
            }
            b.selected = id == p.location;
            b.enabled = adjacent || coach || id == p.location;
            b.visible = cy + NODE_H / 2 > viewTop && cy - NODE_H / 2 < viewBottom;
            b.color = l.town ? UI.JADE : (id == Sects.home(p) ? UI.BLUE : (l.boss >= 0 ? UI.RED : UI.INK));
            if (id == questLoc && !p.finished) {
                b.color = UI.GOLD;
            }
        }
        back = addButton(36, H() - 150, W() - 72, 104, "返回" + cur.name, () -> Story.toLocation(game));
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
    public void touchDown(float x, float y) {
        downY = y;
        lastY = y;
        dragging = false;
        if ((y < viewTop || y > viewBottom) && !back.contains(x, y)) {
            return;
        }
        super.touchDown(x, y);
    }

    @Override
    public void touchMove(float x, float y) {
        if (!dragging && Math.abs(y - downY) > 16 && downY > viewTop && downY < viewBottom) {
            dragging = true;
            cancelPress();
        }
        if (dragging) {
            scroll = Math.max(0f, Math.min(maxScroll(), scroll + (lastY - y)));
            lastY = y;
            layout();
            return;
        }
        super.touchMove(x, y);
    }

    @Override
    public void touchUp(float x, float y) {
        if (dragging) {
            dragging = false;
            return;
        }
        super.touchUp(x, y);
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 5, game.time);
        UI.hud(c, game);
        RectF frame = new RectF(20, viewTop - 50, W() - 20, viewBottom + 10);
        UI.panel(c, frame, 0xE8EFE2C4, UI.INK_LIGHT);
        UI.text(c, "江湖地图（上下拖动）", W() / 2, viewTop - 14, 30, UI.INK, Paint.Align.CENTER);
        c.save();
        c.clipRect(frame.left + 6, viewTop, frame.right - 6, viewBottom);
        float top = viewTop + 50 - scroll;
        // 长江
        river.reset();
        river.moveTo(frame.left, top + MAP_H * 0.40f);
        river.cubicTo(left() + width() * 0.3f, top + MAP_H * 0.34f,
                left() + width() * 0.6f, top + MAP_H * 0.60f,
                frame.right, top + MAP_H * 0.52f);
        c.drawPath(river, UI.stroke(0x552F5A8A, 24f));
        // 东海
        c.drawRect(frame.right - 34, top - 40, frame.right - 6, top + MAP_H, UI.fill(0x332F5A8A));
        Player p = game.player;
        for (GameData.Location l : GameData.LOCATIONS) {
            if (!GameData.visible(p, l.id)) {
                continue;
            }
            for (int k : l.links) {
                if (k > l.id && GameData.visible(p, k)) {
                    GameData.Location o = GameData.LOCATIONS[k];
                    c.drawLine(lx(l), ly(l), lx(o), ly(o), UI.stroke(0x88231E19, 5f));
                }
            }
        }
        GameData.Location cur = GameData.LOCATIONS[p.location];
        float pulse = (float) (1 + 0.15 * Math.sin(game.time * 4));
        c.drawCircle(lx(cur), ly(cur), 96 * pulse, UI.fill(0x33A3291F));
        for (Button b : buttons) {
            if (b != back && b.visible) {
                b.draw(c);
            }
        }
        c.restore();
        // 滚动条
        float ms = maxScroll();
        if (ms > 0) {
            float trackH = viewBottom - viewTop;
            float barH = Math.max(60, trackH * trackH / (trackH + ms));
            float barY = viewTop + (trackH - barH) * (scroll / ms);
            c.drawRoundRect(new RectF(W() - 30, barY, W() - 24, barY + barH), 3, 3, UI.fill(0x88231E19));
        }
        UI.text(c, "金色为主线目的地 · 蓝色为师门 · 城镇间可乘驿站", W() / 2, viewBottom + 44, 22, UI.INK_LIGHT, Paint.Align.CENTER);
        back.draw(c);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
