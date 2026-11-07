package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

/** 结局卡片：显示结局名与收集进度。 */
public class EndingScene extends Scene {
    private final int id;
    private float t = 0f;

    public EndingScene(Game game, int id) {
        super(game);
        this.id = id;
    }

    @Override
    public int music() {
        return Audio.BGM_ENDING;
    }

    @Override
    public void update(float dt) {
        t += dt;
    }

    @Override
    protected void onTap(float x, float y) {
        if (t > 1.2f) {
            Story.toLocation(game);
        }
    }

    @Override
    public void draw(Canvas c) {
        UI.background(c, W(), H(), 6, 0xFF1E1A16, 0xFF4A3C2C, game.time);
        c.drawRect(0, 0, W(), H(), UI.fill(0x88000000));
        float k = Math.min(1f, t / 1.2f);
        int a = (int) (k * 255);
        UI.seal(c, "终", W() / 2, H() * 0.28f, 180);
        UI.text(c, "结局", W() / 2, H() * 0.45f, 40, (a << 24) | 0xB7862C, Paint.Align.CENTER);
        UI.text(c, Story.ENDING_NAMES[id], W() / 2, H() * 0.45f + 110, 96, (a << 24) | 0xF2E6CC, Paint.Align.CENTER);
        UI.text(c, Story.ENDING_HINTS[id], W() / 2, H() * 0.45f + 180, 30, (a << 24) | 0xC0B090, Paint.Align.CENTER);
        UI.text(c, "已收集结局 " + game.endingCount() + " / " + Story.ENDING_NAMES.length, W() / 2, H() * 0.70f, 32,
                (a << 24) | 0xE0D2B0, Paint.Align.CENTER);
        UI.text(c, "不同的抉择，会走向不同的江湖。", W() / 2, H() * 0.70f + 56, 26, (a << 24) | 0x9A8C70, Paint.Align.CENTER);
        if (t > 1.2f) {
            int b = (int) (160 + 90 * Math.sin(game.time * 4));
            UI.text(c, "— 点击继续游历江湖 —", W() / 2, H() - 120, 30, (b << 24) | 0xF2E6CC, Paint.Align.CENTER);
        }
    }

    @Override
    public boolean onBack() {
        onTap(0, 0);
        return true;
    }
}
