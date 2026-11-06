package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

/** 用简单几何形体画出的水墨人物与野兽。 */
public final class Sprites {
    private static final Path P = new Path();

    private Sprites() {
    }

    /** 人形侠客。facing=1 面向右，-1 面向左。 */
    public static void hero(Canvas c, float x, float y, float s, int robe, int sash, int facing, float t, boolean weapon) {
        float bob = (float) Math.sin(t * 3) * 4 * s;
        c.save();
        c.translate(x, y + bob);
        c.scale(facing * s, s);
        // 影子
        c.drawOval(new RectF(-70, 140 - bob / s, 70, 165 - bob / s), UI.fill(0x33000000));
        // 腿
        c.drawLine(-18, 80, -30, 150, UI.stroke(UI.INK, 16));
        c.drawLine(18, 80, 34, 150, UI.stroke(UI.INK, 16));
        // 长袍
        P.reset();
        P.moveTo(-30, -40);
        P.lineTo(30, -40);
        P.lineTo(52, 110);
        P.quadTo(0, 124, -52, 110);
        P.close();
        c.drawPath(P, UI.fill(robe));
        c.drawPath(P, UI.stroke(UI.INK, 4));
        // 腰带
        c.drawRect(-38, 22, 38, 36, UI.fill(sash));
        // 飘带
        float wave = (float) Math.sin(t * 5) * 10;
        P.reset();
        P.moveTo(-34, 30);
        P.quadTo(-70, 40 + wave, -92, 66 - wave);
        c.drawPath(P, UI.stroke(sash, 7));
        // 手臂 & 兵器
        c.drawLine(20, -26, 64, 18, UI.stroke(robe, 22));
        c.drawLine(20, -26, 64, 18, UI.stroke(0x55231E19, 3));
        if (weapon) {
            c.drawLine(56, 26, 132, -96, UI.stroke(0xFFCCCCCC, 7));
            c.drawLine(56, 26, 132, -96, UI.stroke(0x88231E19, 2));
            c.drawLine(46, 12, 72, 34, UI.stroke(UI.GOLD, 8));
        }
        c.drawLine(-20, -26, -54, 30, UI.stroke(robe, 22));
        // 头
        c.drawCircle(0, -72, 30, UI.fill(0xFFF0D9B5));
        c.drawCircle(0, -72, 30, UI.stroke(UI.INK, 4));
        // 发髻
        c.drawCircle(-6, -104, 14, UI.fill(UI.INK));
        P.reset();
        P.moveTo(-30, -78);
        P.quadTo(-28, -106, 0, -104);
        P.quadTo(28, -106, 30, -80);
        P.quadTo(10, -92, -30, -78);
        c.drawPath(P, UI.fill(UI.INK));
        // 眼睛
        c.drawLine(10, -72, 20, -72, UI.stroke(UI.INK, 4));
        c.restore();
    }

    public static void enemy(Canvas c, GameData.Enemy e, float x, float y, float s, float t) {
        switch (e.shape) {
            case 1:
                beast(c, x, y, s, e.color, t);
                break;
            case 2:
                snake(c, x, y, s, e.color, t);
                break;
            case 3:
                bee(c, x, y, s, e.color, t);
                break;
            default:
                int sash = e.boss ? UI.GOLD : 0xFF8B1A1A;
                hero(c, x, y, s * (e.boss ? 1.15f : 1f), e.color, sash, -1, t + 1.3f, true);
                if (e.boss) {
                    // 首领的黑气
                    for (int i = 0; i < 5; i++) {
                        float a = (float) (t * 1.5 + i * 1.256);
                        float r = 120 * s + (float) Math.sin(t * 3 + i) * 12;
                        c.drawCircle(x + (float) Math.cos(a) * r, y + (float) Math.sin(a) * r * 0.6f, 14 * s, UI.fill(0x44231E19));
                    }
                }
                break;
        }
    }

    private static void beast(Canvas c, float x, float y, float s, int color, float t) {
        float bob = (float) Math.sin(t * 4) * 5;
        c.save();
        c.translate(x, y + bob);
        c.scale(s, s);
        c.drawOval(new RectF(-90, 130, 90, 155), UI.fill(0x33000000));
        for (int i = 0; i < 4; i++) {
            float lx = -60 + i * 38;
            c.drawLine(lx, 60, lx + (i % 2 == 0 ? -6 : 6), 140, UI.stroke(color, 18));
        }
        c.drawOval(new RectF(-90, -10, 80, 80), UI.fill(color));
        c.drawOval(new RectF(-90, -10, 80, 80), UI.stroke(UI.INK, 4));
        // 尾巴
        P.reset();
        P.moveTo(70, 20);
        P.quadTo(130, -10 + bob, 120, -50);
        c.drawPath(P, UI.stroke(color, 16));
        // 头
        c.drawCircle(-100, -10, 42, UI.fill(color));
        c.drawCircle(-100, -10, 42, UI.stroke(UI.INK, 4));
        P.reset();
        P.moveTo(-126, -40);
        P.lineTo(-118, -82);
        P.lineTo(-96, -48);
        P.close();
        c.drawPath(P, UI.fill(color));
        c.drawPath(P, UI.stroke(UI.INK, 3));
        c.drawOval(new RectF(-160, -6, -118, 20), UI.fill(color));
        c.drawCircle(-112, -18, 6, UI.fill(0xFFC2402F));
        c.restore();
    }

    private static void snake(Canvas c, float x, float y, float s, int color, float t) {
        c.save();
        c.translate(x, y);
        c.scale(s, s);
        c.drawOval(new RectF(-110, 120, 110, 150), UI.fill(0x33000000));
        P.reset();
        P.moveTo(110, 130);
        for (int i = 1; i <= 20; i++) {
            float k = i / 20f;
            float px = 110 - k * 200;
            float py = 130 - k * 150 + (float) Math.sin(k * 9 + t * 4) * 24 * (1 - k * 0.5f);
            P.lineTo(px, py);
        }
        c.drawPath(P, UI.stroke(UI.INK, 34));
        c.drawPath(P, UI.stroke(color, 26));
        float hy = -20 + (float) Math.sin(9 + t * 4) * 12;
        c.drawOval(new RectF(-130, hy - 26, -70, hy + 20), UI.fill(color));
        c.drawOval(new RectF(-130, hy - 26, -70, hy + 20), UI.stroke(UI.INK, 4));
        c.drawCircle(-108, hy - 8, 6, UI.fill(0xFFE0C020));
        float tongue = (float) Math.max(0, Math.sin(t * 7)) * 26;
        c.drawLine(-128, hy, -128 - tongue, hy + 4, UI.stroke(0xFFC2402F, 4));
        c.restore();
    }

    private static void bee(Canvas c, float x, float y, float s, int color, float t) {
        float fy = (float) Math.sin(t * 5) * 18;
        c.save();
        c.translate(x, y + fy - 40);
        c.scale(s, s);
        float wing = (float) Math.abs(Math.sin(t * 30)) * 30 + 20;
        c.drawOval(new RectF(-40, -40 - wing, 20, -10), UI.fill(0x88FFFFFF));
        c.drawOval(new RectF(-10, -40 - wing, 50, -10), UI.fill(0x88FFFFFF));
        c.drawOval(new RectF(-70, -30, 70, 40), UI.fill(color));
        for (int i = 0; i < 3; i++) {
            c.drawRect(-20 + i * 30, -28, -6 + i * 30, 38, UI.fill(UI.INK));
        }
        c.drawOval(new RectF(-70, -30, 70, 40), UI.stroke(UI.INK, 4));
        c.drawCircle(-80, 0, 24, UI.fill(UI.INK));
        P.reset();
        P.moveTo(70, 4);
        P.lineTo(104, 10);
        P.lineTo(70, 18);
        c.drawPath(P, UI.fill(UI.INK));
        c.restore();
    }
}
