package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 剧情文字页，文字逐字浮现，点击加速 / 继续。 */
public class StoryScene extends Scene {
    private final String title;
    private final String body;
    private final Runnable next;
    private float shown = 0f;

    public StoryScene(Game game, String title, String body, Runnable next) {
        super(game);
        this.title = title;
        this.body = body;
        this.next = next;
    }

    @Override
    public void update(float dt) {
        shown += dt * 28f;
    }

    @Override
    protected void onTap(float x, float y) {
        if (shown < body.length()) {
            shown = body.length();
        } else {
            next.run();
        }
    }

    @Override
    public void draw(Canvas c) {
        UI.background(c, W(), H(), 3, 0xFF2A241E, 0xFF5A4C3A, game.time);
        c.drawRect(0, 0, W(), H(), UI.fill(0x66000000));
        UI.text(c, title, W() / 2, 200, 54, UI.GOLD, Paint.Align.CENTER);
        c.drawLine(W() / 2 - 180, 230, W() / 2 + 180, 230, UI.stroke(0x88B7862C, 3f));
        int n = Math.min(body.length(), (int) shown);
        RectF r = new RectF(40, 280, W() - 40, H() - 200);
        c.drawRoundRect(r, 16, 16, UI.fill(0x55000000));
        UI.wrap(c, body.substring(0, n), 72, 350, W() - 144, 34, UI.PAPER, 56);
        if (n >= body.length()) {
            int a = (int) (160 + 90 * Math.sin(game.time * 4));
            UI.text(c, "— 点击继续 —", W() / 2, H() - 120, 30, (a << 24) | 0xF2E6CC, Paint.Align.CENTER);
        }
    }

    @Override
    public boolean onBack() {
        shown = body.length();
        return true;
    }
}
