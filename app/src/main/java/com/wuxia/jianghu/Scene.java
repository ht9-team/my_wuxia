package com.wuxia.jianghu;

import android.graphics.Canvas;

import java.util.ArrayList;

/** 场景基类：负责按钮的布局、按下态、点击音效和点击分发。 */
public abstract class Scene {
    protected final Game game;
    protected final ArrayList<Button> buttons = new ArrayList<>();
    private Button pressed;

    protected Scene(Game game) {
        this.game = game;
    }

    /** 重新构建按钮；在进入场景和屏幕尺寸变化时调用。 */
    public void layout() {
    }

    /** 进入场景时播放的背景音乐，Audio.KEEP 表示保持当前音乐。 */
    public int music() {
        return Audio.KEEP;
    }

    public void update(float dt) {
    }

    public abstract void draw(Canvas c);

    /** 返回 true 表示已处理返回键。 */
    public boolean onBack() {
        return true;
    }

    protected void onTap(float x, float y) {
    }

    protected Button addButton(float x, float y, float w, float h, String label, Runnable action) {
        Button b = new Button(x, y, w, h, label, action);
        buttons.add(b);
        return b;
    }

    protected void drawButtons(Canvas c) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).draw(c);
        }
    }

    public void touchDown(float x, float y) {
        pressed = null;
        for (int i = buttons.size() - 1; i >= 0; i--) {
            Button b = buttons.get(i);
            if (b.visible && b.enabled && b.contains(x, y)) {
                pressed = b;
                b.pressed = true;
                break;
            }
        }
    }

    public void touchMove(float x, float y) {
        if (pressed != null) {
            pressed.pressed = pressed.contains(x, y);
        }
    }

    public void touchUp(float x, float y) {
        Button b = pressed;
        pressed = null;
        if (b != null) {
            b.pressed = false;
            if (b.contains(x, y) && b.enabled && b.visible && b.action != null) {
                game.audio.sfx(Audio.SFX_CLICK);
                b.action.run();
            }
        } else if (x >= 0 && y >= 0) {
            onTap(x, y);
        }
    }

    protected float W() {
        return game.W;
    }

    protected float H() {
        return game.H;
    }

    /** 分页工具：返回总页数。 */
    protected static int pages(int count, int perPage) {
        return Math.max(1, (count + perPage - 1) / perPage);
    }
}
