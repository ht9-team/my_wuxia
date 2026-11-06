package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

/** 水墨风格的绘制工具。所有方法只在 UI 线程调用。 */
public final class UI {
    public static final int PAPER = 0xFFF2E6CC;
    public static final int PAPER_LIGHT = 0xFFF8F0DE;
    public static final int PAPER_DARK = 0xFFE0CFA8;
    public static final int INK = 0xFF231E19;
    public static final int INK_LIGHT = 0xFF6A5E50;
    public static final int RED = 0xFFA3291F;
    public static final int GOLD = 0xFFB7862C;
    public static final int JADE = 0xFF2F7563;
    public static final int BLUE = 0xFF2F5A8A;
    public static final int PURPLE = 0xFF6B3E7A;

    private static final Paint FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint STROKE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint GRAD = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path PATH = new Path();

    static {
        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setStrokeCap(Paint.Cap.ROUND);
        STROKE.setStrokeJoin(Paint.Join.ROUND);
        TEXT.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
    }

    private UI() {
    }

    public static Paint fill(int color) {
        FILL.setColor(color);
        return FILL;
    }

    public static Paint stroke(int color, float width) {
        STROKE.setColor(color);
        STROKE.setStrokeWidth(width);
        return STROKE;
    }

    public static void text(Canvas c, String s, float x, float y, float size, int color, Paint.Align align) {
        TEXT.setTextSize(size);
        TEXT.setColor(color);
        TEXT.setTextAlign(align);
        c.drawText(s, x, y, TEXT);
    }

    public static void text(Canvas c, String s, float x, float y, float size, int color) {
        text(c, s, x, y, size, color, Paint.Align.LEFT);
    }

    public static float textWidth(String s, float size) {
        TEXT.setTextSize(size);
        return TEXT.measureText(s);
    }

    /** 逐字换行（适合中文），返回最后一行之后的 y。 */
    public static float wrap(Canvas c, String s, float x, float y, float width, float size, int color, float lineH) {
        TEXT.setTextSize(size);
        TEXT.setColor(color);
        TEXT.setTextAlign(Paint.Align.LEFT);
        String[] paragraphs = s.split("\n", -1);
        for (String p : paragraphs) {
            int start = 0;
            if (p.isEmpty()) {
                y += lineH;
                continue;
            }
            while (start < p.length()) {
                int n = TEXT.breakText(p, start, p.length(), true, width, null);
                if (n <= 0) {
                    n = 1;
                }
                c.drawText(p, start, start + n, x, y, TEXT);
                start += n;
                y += lineH;
            }
        }
        return y;
    }

    public static void panel(Canvas c, RectF r, int bg, int border) {
        c.drawRoundRect(r, 16, 16, fill(bg));
        c.drawRoundRect(r, 16, 16, stroke(border, 3f));
    }

    public static void panel(Canvas c, RectF r) {
        RectF shadow = new RectF(r.left + 5, r.top + 7, r.right + 5, r.bottom + 7);
        c.drawRoundRect(shadow, 16, 16, fill(0x2A000000));
        panel(c, r, 0xF0F8F0DE, INK_LIGHT);
        RectF inner = new RectF(r.left + 8, r.top + 8, r.right - 8, r.bottom - 8);
        c.drawRoundRect(inner, 10, 10, stroke(0x55B7862C, 2f));
    }

    public static void bar(Canvas c, float x, float y, float w, float h, float ratio, int color, String label) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        RectF r = new RectF(x, y, x + w, y + h);
        c.drawRoundRect(r, h / 2, h / 2, fill(0xFF3A322A));
        if (ratio > 0) {
            RectF f = new RectF(x, y, x + Math.max(h, w * ratio), y + h);
            c.drawRoundRect(f, h / 2, h / 2, fill(color));
            RectF gloss = new RectF(x + 4, y + 2, f.right - 4, y + h * 0.42f);
            c.drawRoundRect(gloss, h / 4, h / 4, fill(0x33FFFFFF));
        }
        if (label != null) {
            text(c, label, x + w / 2, y + h * 0.78f, h * 0.82f, 0xFFFFFFFF, Paint.Align.CENTER);
        }
    }

    /** 朱红印章。 */
    public static void seal(Canvas c, String s, float cx, float cy, float size) {
        RectF r = new RectF(cx - size / 2, cy - size / 2, cx + size / 2, cy + size / 2);
        c.drawRoundRect(r, size * 0.12f, size * 0.12f, fill(RED));
        RectF in = new RectF(r.left + size * 0.07f, r.top + size * 0.07f, r.right - size * 0.07f, r.bottom - size * 0.07f);
        c.drawRoundRect(in, size * 0.08f, size * 0.08f, stroke(PAPER, size * 0.04f));
        if (s.length() <= 1) {
            text(c, s, cx, cy + size * 0.22f, size * 0.62f, PAPER, Paint.Align.CENTER);
        } else {
            float fs = size * 0.36f;
            text(c, s.substring(0, 1), cx + fs * 0.55f, cy - fs * 0.1f, fs, PAPER, Paint.Align.CENTER);
            text(c, s.substring(1, 2), cx + fs * 0.55f, cy + fs * 0.95f, fs, PAPER, Paint.Align.CENTER);
            if (s.length() > 2) {
                text(c, s.substring(2, 3), cx - fs * 0.55f, cy - fs * 0.1f, fs, PAPER, Paint.Align.CENTER);
            }
            if (s.length() > 3) {
                text(c, s.substring(3, 4), cx - fs * 0.55f, cy + fs * 0.95f, fs, PAPER, Paint.Align.CENTER);
            }
        }
    }

    /**
     * 宣纸底色 + 三层水墨远山。variant 决定山形，tint 决定天色。
     */
    public static void background(Canvas c, float w, float h, int variant, int skyTop, int skyBottom, float time) {
        GRAD.setShader(new LinearGradient(0, 0, 0, h, skyTop, skyBottom, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, GRAD);
        GRAD.setShader(null);
        // 太阳 / 月亮
        c.drawCircle(w * 0.78f, h * 0.16f, 70, fill(0x55C2402F));
        c.drawCircle(w * 0.78f, h * 0.16f, 52, fill(0x88C2402F));
        int[] colors = {0x40433A30, 0x70362E26, 0xC0231E19};
        for (int layer = 0; layer < 3; layer++) {
            float base = h * (0.52f + layer * 0.12f);
            float amp = 90f - layer * 18f;
            float drift = time * (2f + layer * 3f);
            PATH.reset();
            PATH.moveTo(0, h);
            for (int i = 0; i <= 48; i++) {
                float x = w * i / 48f;
                float t = (x + drift) / w;
                double v = Math.sin(t * 6.1 + variant * 1.7 + layer * 2.3) * 0.55
                        + Math.sin(t * 13.7 + variant * 0.9 + layer) * 0.3
                        + Math.sin(t * 27.3 + layer * 4.1) * 0.15;
                PATH.lineTo(x, (float) (base - Math.abs(v) * amp - amp * 0.3f));
            }
            PATH.lineTo(w, h);
            PATH.close();
            c.drawPath(PATH, fill(colors[layer]));
            // 山间雾气
            c.drawRect(0, base - 10, w, base + 40, fill(0x18FFFFFF));
        }
    }

    /** 统一的纸张底 + 远山背景。 */
    public static void paperBackground(Canvas c, float w, float h, int variant, float time) {
        background(c, w, h, variant, 0xFFF5ECD8, 0xFFDCC9A0, time);
    }

    /** 顶部角色信息栏。 */
    public static void hud(Canvas c, Game g) {
        Player p = g.player;
        if (p == null) {
            return;
        }
        RectF r = new RectF(16, 16, g.W - 16, 166);
        panel(c, r, 0xEE231E19, GOLD);
        seal(c, GameData.SECTS[p.sect].shortName, 76, 91, 92);
        text(c, p.name, 140, 66, 36, PAPER);
        text(c, "Lv." + p.level + "  " + GameData.SECTS[p.sect].name, 140 + textWidth(p.name, 36) + 16, 66, 26, GOLD);
        text(c, "银两 " + p.silver + "   第" + p.day + "日", g.W - 36, 66, 26, PAPER_DARK, Paint.Align.RIGHT);
        bar(c, 140, 84, 260, 28, p.hp / (float) p.maxHp, RED, "气血 " + p.hp + "/" + p.maxHp);
        bar(c, 416, 84, 268, 28, p.mp / (float) Math.max(1, p.maxMp), BLUE, "内力 " + p.mp + "/" + p.maxMp);
        bar(c, 140, 124, 544, 18, p.exp / (float) p.expNeed(), GOLD, "修为 " + p.exp + "/" + p.expNeed());
    }

    /** 场景标题，竖排的毛笔风格感觉用大字 + 下划墨线表现。 */
    public static void title(Canvas c, String s, float x, float y, float size) {
        text(c, s, x + 3, y + 3, size, 0x33000000, Paint.Align.CENTER);
        text(c, s, x, y, size, INK, Paint.Align.CENTER);
        float tw = textWidth(s, size);
        c.drawLine(x - tw / 2 - 20, y + size * 0.3f, x + tw / 2 + 20, y + size * 0.3f, stroke(0x99231E19, 4f));
    }
}
