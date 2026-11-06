package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 创建角色：随机姓名 + 选择门派。 */
public class CreateScene extends Scene {
    private String name;
    private int sect = 0;

    public CreateScene(Game game) {
        super(game);
        rollName();
    }

    private void rollName() {
        String f = GameData.NAMES_FAMILY[game.rnd.nextInt(GameData.NAMES_FAMILY.length)];
        String g = GameData.NAMES_GIVEN[game.rnd.nextInt(GameData.NAMES_GIVEN.length)];
        name = f + g;
    }

    @Override
    public void layout() {
        buttons.clear();
        addButton(W() - 220, 196, 180, 72, "换个名字", this::rollName).textSize = 28;
        float bw = 316;
        float bh = 110;
        for (int i = 0; i < GameData.SECTS.length; i++) {
            final int idx = i;
            float x = 36 + (i % 2) * (bw + 16);
            float y = 320 + (i / 2) * (bh + 18);
            Button b = addButton(x, y, bw, bh, GameData.SECTS[i].name, () -> {
                sect = idx;
                for (Button o : buttons) {
                    o.selected = false;
                }
                buttons.get(idx + 1).selected = true;
            });
            GameData.Sect s = GameData.SECTS[i];
            b.sub = "初始武学：" + GameData.SKILLS[s.startSkill].name;
            b.color = s.color;
            b.selected = i == sect;
        }
        float by = H() - 170;
        addButton(36, by, 200, 100, "返回", () -> game.setScene(new TitleScene(game)));
        addButton(260, by, W() - 296, 100, "踏入江湖", this::start).color = UI.RED;
    }

    private void start() {
        game.deleteSave();
        game.player = Player.create(name, sect);
        game.save();
        game.setScene(new StoryScene(game, "序章 · 初入江湖",
                name + "，你自幼拜入" + GameData.SECTS[sect].name + "门下，苦练十年，今日奉师命下山历练。\n\n"
                        + "师父临行前叮嘱：「如今天魔教势大，江湖风波不息。你此去当行侠仗义，莫堕了本门威名。」\n\n"
                        + "你背起行囊，来到了江南临安城……",
                () -> game.goLocation(0)));
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 1, game.time);
        UI.title(c, "初入江湖", W() / 2, 110, 64);
        UI.text(c, "姓名", 40, 246, 32, UI.INK_LIGHT);
        UI.text(c, name, 130, 250, 48, UI.INK);
        GameData.Sect s = GameData.SECTS[sect];
        float py = 590;
        RectF r = new RectF(36, py, W() - 36, py + 380);
        UI.panel(c, r);
        UI.seal(c, s.shortName, W() - 110, py + 74, 96);
        UI.text(c, s.name, 64, py + 70, 46, s.color);
        UI.wrap(c, s.desc, 64, py + 124, W() - 260, 28, UI.INK, 40);
        String[] k = {"气血", "内力", "攻击", "防御", "身法"};
        int[] v = {s.hp, s.mp, s.atk, s.def, s.agi};
        int[] max = {140, 70, 16, 10, 14};
        for (int i = 0; i < k.length; i++) {
            float y = py + 216 + i * 32;
            UI.text(c, k[i], 64, y + 20, 24, UI.INK_LIGHT);
            UI.bar(c, 140, y, W() - 260, 22, v[i] / (float) max[i], s.color, null);
            UI.text(c, String.valueOf(v[i]), W() - 76, y + 20, 24, UI.INK, Paint.Align.RIGHT);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        game.setScene(new TitleScene(game));
        return true;
    }
}
