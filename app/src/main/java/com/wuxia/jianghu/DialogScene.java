package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 对白剧情：人物立绘 + 逐字浮现的对话框 + 抉择按钮。 */
public class DialogScene extends Scene {
    private final Ev ev;
    private int page = 0;
    private float shown = 0f;
    private boolean choosing = false;
    private float boxTop;

    public DialogScene(Game game, Ev ev) {
        super(game);
        this.ev = ev;
        if (ev.pages.isEmpty()) {
            ev.narr("……");
        }
    }

    @Override
    public int music() {
        return ev.bgm;
    }

    private Ev.Page cur() {
        return ev.pages.get(page);
    }

    private String speakerName(Ev.Page pg) {
        if (pg.who == null) {
            return null;
        }
        if (Ev.ME.equals(pg.who)) {
            return game.player != null ? game.player.name : "你";
        }
        return pg.who;
    }

    @Override
    public void layout() {
        buttons.clear();
        boxTop = H() - 440;
        if (!choosing) {
            return;
        }
        int n = ev.choices.size();
        float bh = 100;
        float gap = 14;
        float total = n * bh + (n - 1) * gap;
        float y = Math.max(180, boxTop - 24 - total);
        for (Ev.Choice ch : ev.choices) {
            Button b = addButton(44, y, W() - 88, bh, ch.text, ch.action);
            b.textSize = 30;
            b.enabled = ch.enabled;
            b.color = UI.RED;
            if (ch.hint != null) {
                b.sub = ch.hint;
            }
            y += bh + gap;
        }
    }

    @Override
    public void update(float dt) {
        shown += dt * 32f;
    }

    private void advance() {
        if (choosing) {
            return;
        }
        String t = cur().text;
        if (shown < t.length()) {
            shown = t.length();
            return;
        }
        if (page < ev.pages.size() - 1) {
            page++;
            shown = 0;
            game.audio.sfx(Audio.SFX_PAGE);
            return;
        }
        if (!ev.choices.isEmpty()) {
            choosing = true;
            layout();
            return;
        }
        if (ev.onEnd != null) {
            ev.onEnd.run();
        } else {
            Story.toLocation(game);
        }
    }

    @Override
    protected void onTap(float x, float y) {
        advance();
    }

    @Override
    public void draw(Canvas c) {
        int variant = ev.bg;
        if (variant < 0) {
            variant = game.player != null ? GameData.LOCATIONS[game.player.location].variant : 3;
        }
        UI.background(c, W(), H(), variant, 0xFF3A322A, 0xFF7A6A52, game.time);
        c.drawRect(0, 0, W(), H(), UI.fill(0x66000000));
        UI.text(c, ev.title, W() / 2, 110, 44, UI.GOLD, Paint.Align.CENTER);
        float tw = UI.textWidth(ev.title, 44);
        c.drawLine(W() / 2 - tw / 2 - 30, 134, W() / 2 + tw / 2 + 30, 134, UI.stroke(0x88B7862C, 3f));

        Ev.Page pg = cur();
        String name = speakerName(pg);
        if (pg.who != null) {
            Sprites.portrait(c, game, pg.who, W() / 2, boxTop - 230, 1.5f, game.time);
        }
        if (choosing) {
            c.drawRect(0, 0, W(), boxTop, UI.fill(0x55000000));
        }

        RectF box = new RectF(24, boxTop, W() - 24, H() - 32);
        UI.panel(c, box, 0xEE1E1A16, UI.GOLD);
        if (name != null) {
            float nw = UI.textWidth(name, 32) + 60;
            RectF tag = new RectF(48, boxTop - 30, 48 + nw, boxTop + 30);
            c.drawRoundRect(tag, 10, 10, UI.fill(UI.RED));
            c.drawRoundRect(tag, 10, 10, UI.stroke(UI.GOLD, 3f));
            UI.text(c, name, tag.centerX(), boxTop + 12, 32, UI.PAPER, Paint.Align.CENTER);
        }
        String text = pg.text;
        int n = Math.min(text.length(), (int) shown);
        int color = pg.who == null ? 0xFFE0D2B0 : UI.PAPER;
        UI.wrap(c, text.substring(0, n), 60, boxTop + 90, W() - 120, 31, color, 48);
        UI.text(c, (page + 1) + "/" + ev.pages.size(), W() - 52, H() - 52, 22, UI.INK_LIGHT, Paint.Align.RIGHT);
        if (n >= text.length() && !choosing) {
            int a = (int) (160 + 90 * Math.sin(game.time * 5));
            String hint = page < ev.pages.size() - 1 ? "▼" : (ev.choices.isEmpty() ? "— 点击继续 —" : "— 点击作出抉择 —");
            UI.text(c, hint, W() / 2, H() - 52, 26, (a << 24) | 0xB7862C, Paint.Align.CENTER);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        advance();
        return true;
    }
}
