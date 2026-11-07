package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

/** 系统设置：背景音乐、音效、手动存档、返回标题。 */
public class SystemScene extends Scene {
    private final boolean fromTitle;
    private boolean confirmTitle = false;

    public SystemScene(Game game, boolean fromTitle) {
        super(game);
        this.fromTitle = fromTitle;
    }

    @Override
    public void layout() {
        buttons.clear();
        float y = 300;
        Button m = addButton(60, y, W() - 120, 110, "背景音乐：" + (game.musicOn() ? "开" : "关"), () -> {
            game.setMusicOn(!game.musicOn());
            layout();
        });
        m.sub = "古筝与战鼓，程序实时合成";
        y += 130;
        Button s = addButton(60, y, W() - 120, 110, "音效：" + (game.sfxOn() ? "开" : "关"), () -> {
            game.setSfxOn(!game.sfxOn());
            layout();
        });
        s.sub = "刀剑、拳脚、点击等声音";
        y += 130;
        if (!fromTitle && game.player != null) {
            Button sv = addButton(60, y, W() - 120, 110, "立即存档", () -> {
                game.save();
                game.audio.sfx(Audio.SFX_COIN);
                game.toast("已存入存档" + (game.slot + 1));
            });
            sv.sub = "当前为存档 " + (game.slot + 1) + "（行动后也会自动存档）";
            sv.color = UI.JADE;
            y += 130;
            Button t = addButton(60, y, W() - 120, 110, confirmTitle ? "再点一次确认返回标题" : "返回标题", () -> {
                if (!confirmTitle) {
                    confirmTitle = true;
                    layout();
                    return;
                }
                game.save();
                game.setScene(new TitleScene(game));
            });
            t.color = UI.RED;
        }
        addButton(60, H() - 160, W() - 120, 104, "返回", this::back).color = UI.INK_LIGHT;
    }

    private void back() {
        if (fromTitle || game.player == null) {
            game.setScene(new TitleScene(game));
        } else {
            Story.toLocation(game);
        }
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 9, game.time);
        UI.title(c, "系统", W() / 2, 200, 60);
        drawButtons(c);
        UI.text(c, "音量请使用手机音量键调节", W() / 2, H() - 190, 24, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        back();
        return true;
    }
}
