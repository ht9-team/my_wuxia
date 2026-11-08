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
        float y = 250;
        float step = 116;
        Button m = addButton(60, y, W() - 120, 100, "背景音乐：" + (game.musicOn() ? "开" : "关"), () -> {
            game.setMusicOn(!game.musicOn());
            layout();
        });
        m.sub = "古筝与战鼓，程序实时合成";
        y += step;
        Button s = addButton(60, y, W() - 120, 100, "音效：" + (game.sfxOn() ? "开" : "关"), () -> {
            game.setSfxOn(!game.sfxOn());
            layout();
        });
        s.sub = "刀剑、拳脚、点击等声音";
        y += step;
        if (!fromTitle && game.player != null) {
            Button gd = addButton(60, y, W() - 120, 100, "剧情指引", () -> game.setScene(new GuideScene(game, 0)));
            gd.sub = "流程、分支条件、线索与十二种结局";
            gd.color = UI.GOLD;
            y += step;
            Button nd = addButton(60, y, W() - 120, 100, "节点回溯", () -> game.setScene(new NodeScene(game, game.slot, false)));
            nd.sub = "读取关键抉择前的自动存档，改走其他分支";
            nd.color = UI.RED;
            y += step;
            Button sv = addButton(60, y, W() - 120, 100, "立即存档", () -> {
                game.save();
                game.audio.sfx(Audio.SFX_COIN);
                game.toast("已存入存档" + (game.slot + 1));
            });
            sv.sub = "当前为存档 " + (game.slot + 1) + "（行动后也会自动存档）";
            sv.color = UI.JADE;
            y += step;
            Button t = addButton(60, y, W() - 120, 100, confirmTitle ? "再点一次确认返回标题" : "返回标题", () -> {
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
        UI.title(c, "系统", W() / 2, 170, 60);
        drawButtons(c);
        UI.text(c, "音量请使用手机音量键调节", W() / 2, H() - 190, 24, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        back();
        return true;
    }
}
