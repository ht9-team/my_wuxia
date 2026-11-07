package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TitleScene extends Scene {
    public TitleScene(Game game) {
        super(game);
    }

    @Override
    public int music() {
        return Audio.BGM_TITLE;
    }

    @Override
    public void layout() {
        buttons.clear();
        float bw = 400;
        float x = (W() - bw) / 2;
        float y = H() * 0.56f;
        final int last = game.lastSlot();
        if (game.hasSave(last)) {
            Player peek = game.peek(last);
            Button b = addButton(x, y, bw, 100, "继续江湖", () -> {
                if (game.load(last)) {
                    Story.toLocation(game);
                } else {
                    game.toast("存档损坏，无法读取");
                }
            });
            b.color = UI.RED;
            if (peek != null) {
                b.sub = "存档" + (last + 1) + " · " + peek.name + " Lv." + peek.level;
            }
            y += 120;
        }
        if (game.anySave()) {
            addButton(x, y, bw, 100, "读取存档", () -> game.setScene(new SlotScene(game, false))).sub = "共 " + Game.SLOTS + " 个存档位";
            y += 120;
        }
        addButton(x, y, bw, 100, "新的江湖", () -> game.setScene(new SlotScene(game, true))).sub = "选择存档位，创建角色";
        y += 120;
        Button s = addButton(x, y, bw, 90, "系统设置", () -> game.setScene(new SystemScene(game, true)));
        s.color = UI.INK_LIGHT;
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 0, game.time);
        float cy = H() * 0.26f;
        UI.text(c, "江湖行", W() / 2 + 5, cy + 5, 150, 0x33000000, Paint.Align.CENTER);
        UI.text(c, "江湖行", W() / 2, cy, 150, UI.INK, Paint.Align.CENTER);
        UI.seal(c, "武侠", W() / 2 + 250, cy - 40, 84);
        UI.text(c, "一剑一酒一江湖", W() / 2, cy + 80, 36, UI.INK_LIGHT, Paint.Align.CENTER);
        UI.text(c, "结局收集 " + game.endingCount() + " / " + Story.ENDING_NAMES.length, W() / 2, cy + 140, 26, UI.RED,
                Paint.Align.CENTER);
        drawButtons(c);
        UI.text(c, "v" + BuildInfo.versionName(game.ctx), W() - 24, H() - 24, 22, UI.PAPER_DARK, Paint.Align.RIGHT);
    }

    @Override
    public boolean onBack() {
        return false;
    }
}
