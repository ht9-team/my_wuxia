package com.wuxia.jianghu;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/** 把整个游戏绘制在一块宽度固定为 720 的虚拟画布上，高度随屏幕比例变化。 */
public class GameView extends View {
    public static final float VW = 720f;
    public static final float MIN_VH = 1280f;

    private final Game game;
    private float scale = 1f;
    private float offX = 0f;
    private float offY = 0f;
    private long last = 0L;

    public GameView(Context ctx, Game game) {
        super(ctx);
        this.game = game;
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) {
            return;
        }
        scale = Math.min(w / VW, h / MIN_VH);
        float vh = h / scale;
        offX = (w - VW * scale) / 2f;
        offY = 0f;
        game.resize(vh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = System.nanoTime();
        float dt = last == 0L ? 0.016f : Math.min(0.05f, (now - last) / 1_000_000_000f);
        last = now;
        game.update(dt);
        canvas.drawColor(0xFF15110D);
        canvas.save();
        canvas.translate(offX, offY);
        canvas.scale(scale, scale);
        canvas.clipRect(0, 0, VW, game.H);
        game.draw(canvas);
        canvas.restore();
        postInvalidateOnAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = (e.getX() - offX) / scale;
        float y = (e.getY() - offY) / scale;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                game.touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                game.touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                game.touchUp(x, y);
                performClick();
                break;
            case MotionEvent.ACTION_CANCEL:
                game.touchMove(-9999f, -9999f);
                game.touchUp(-9999f, -9999f);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
