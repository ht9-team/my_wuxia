package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TitleScene extends Scene {
    private boolean confirmNew = false;

    public TitleScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        float bw = 360;
        float x = (W() - bw) / 2;
        float y = H() * 0.62f;
        final boolean save = game.hasSave();
        if (save) {
            addButton(x, y, bw, 96, "继续江湖", () -> {
                if (game.load()) {
                    game.goLocation(game.player.location);
                } else {
                    game.toast("存档损坏，请重新开始");
                    game.deleteSave();
                    layout();
                }
            }).color = UI.RED;
            y += 124;
        }
        addButton(x, y, bw, 96, confirmNew ? "确认覆盖存档？" : "初入江湖", () -> {
            if (save && !confirmNew) {
                confirmNew = true;
                layout();
                return;
            }
            game.setScene(new CreateScene(game));
        });
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 0, game.time);
        float cy = H() * 0.3f;
        UI.text(c, "江湖行", W() / 2 + 5, cy + 5, 150, 0x33000000, Paint.Align.CENTER);
        UI.text(c, "江湖行", W() / 2, cy, 150, UI.INK, Paint.Align.CENTER);
        UI.seal(c, "武侠", W() / 2 + 250, cy - 40, 84);
        UI.text(c, "一剑一酒一江湖", W() / 2, cy + 80, 36, UI.INK_LIGHT, Paint.Align.CENTER);
        drawButtons(c);
        UI.text(c, "v" + BuildInfo.versionName(game.ctx), W() - 24, H() - 24, 22, UI.PAPER_DARK, Paint.Align.RIGHT);
    }

    @Override
    public boolean onBack() {
        return false;
    }
}
