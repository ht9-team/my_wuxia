package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/** 武学页面：dojo=true 时为城镇中的拜师学艺，否则为查看已学武功。 */
public class SkillScene extends Scene {
    private final boolean dojo;

    public SkillScene(Game game, boolean dojo) {
        super(game);
        this.dojo = dojo;
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        ArrayList<GameData.Skill> list = new ArrayList<>();
        if (dojo) {
            for (GameData.Skill s : GameData.SKILLS) {
                if (s.sect == p.sect) {
                    list.add(s);
                }
            }
        } else {
            for (int id : p.skills) {
                list.add(GameData.SKILLS[id]);
            }
        }
        float y = 270;
        for (final GameData.Skill s : list) {
            String label;
            boolean enabled;
            if (dojo) {
                if (p.hasSkill(s.id)) {
                    label = s.name + "    已习得 · 第" + p.skillLevel(s.id) + "重";
                    enabled = false;
                } else if (p.level < s.learnLv) {
                    label = s.name + "    需 Lv." + s.learnLv;
                    enabled = false;
                } else {
                    label = s.name + "    学费 " + s.price + " 两";
                    enabled = p.silver >= s.price;
                }
            } else {
                label = s.name + "  第" + p.skillLevel(s.id) + "重    内力 " + s.mp;
                enabled = true;
            }
            Runnable act = dojo ? () -> learn(s) : null;
            Button b = addButton(36, y, W() - 72, 110, label, act);
            b.textSize = 32;
            b.sub = s.desc + (dojo ? "" : "  威力 " + Math.round(p.skillPower(s.id) * 100) + "%");
            b.enabled = enabled;
            b.color = s.sect < 0 ? UI.PURPLE : GameData.SECTS[s.sect].color;
            y += 124;
        }
        addButton(36, H() - 150, W() - 72, 104, "返回", () -> game.setScene(new LocationScene(game))).color = UI.INK_LIGHT;
    }

    private void learn(GameData.Skill s) {
        Player p = game.player;
        if (p.silver < s.price || p.hasSkill(s.id) || p.level < s.learnLv) {
            return;
        }
        p.silver -= s.price;
        p.learn(s.id);
        game.toast("习得 " + s.name + "！");
        game.save();
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 4, game.time);
        UI.hud(c, game);
        UI.title(c, dojo ? GameData.SECTS[game.player.sect].name + " · 传功长老" : "所习武学", W() / 2, 236, 46);
        drawButtons(c);
        if (!dojo) {
            UI.text(c, "武功每施展一次增加熟练度，最高十重", W() / 2, H() - 176, 24, UI.INK_LIGHT, Paint.Align.CENTER);
        } else {
            UI.text(c, "奇遇武学需在江湖中寻访，无法花钱学得", W() / 2, H() - 176, 24, UI.INK_LIGHT, Paint.Align.CENTER);
        }
    }

    @Override
    public boolean onBack() {
        game.setScene(new LocationScene(game));
        return true;
    }
}
