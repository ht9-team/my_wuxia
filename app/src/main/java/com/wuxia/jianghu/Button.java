package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Button {
    public final RectF rect;
    public String label;
    public String sub;
    public Runnable action;
    public boolean enabled = true;
    public boolean visible = true;
    public boolean pressed = false;
    public boolean selected = false;
    public int color = UI.INK;
    public float textSize = 34f;

    public Button(float x, float y, float w, float h, String label, Runnable action) {
        this.rect = new RectF(x, y, x + w, y + h);
        this.label = label;
        this.action = action;
    }

    public boolean contains(float x, float y) {
        return rect.contains(x, y);
    }

    public void draw(Canvas c) {
        if (!visible) {
            return;
        }
        int bg;
        int fg;
        int border;
        if (!enabled) {
            bg = 0xFFCFC2A6;
            fg = 0xFF8C8272;
            border = 0xFFA89C84;
        } else if (pressed || selected) {
            bg = color;
            fg = UI.PAPER;
            border = UI.GOLD;
        } else {
            bg = UI.PAPER_LIGHT;
            fg = color;
            border = color;
        }
        RectF r = pressed ? new RectF(rect.left + 3, rect.top + 3, rect.right - 3, rect.bottom - 3) : rect;
        if (enabled && !pressed) {
            RectF shadow = new RectF(r.left + 4, r.top + 6, r.right + 4, r.bottom + 6);
            c.drawRoundRect(shadow, 14, 14, UI.fill(0x33000000));
        }
        c.drawRoundRect(r, 14, 14, UI.fill(bg));
        c.drawRoundRect(r, 14, 14, UI.stroke(border, 3f));
        float cy = r.centerY();
        if (sub != null) {
            UI.text(c, label, r.centerX(), cy - 4, textSize, fg, Paint.Align.CENTER);
            UI.text(c, sub, r.centerX(), cy + 28, 22, enabled ? (pressed || selected ? 0xFFE8D9B5 : UI.INK_LIGHT) : fg, Paint.Align.CENTER);
        } else {
            UI.text(c, label, r.centerX(), cy + textSize * 0.36f, textSize, fg, Paint.Align.CENTER);
        }
    }
}
