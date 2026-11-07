package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/** 武学页面：dojo=true 时为城镇中的拜师学艺，否则为查看已学武功。 */
public class SkillScene extends Scene {
    private final boolean dojo;
    private int page = 0;

    public SkillScene(Game game, boolean dojo) {
        super(game);
        this.dojo = dojo;
    }

    private ArrayList<GameData.Skill> list() {
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
        return list;
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        ArrayList<GameData.Skill> list = list();
        float top = 270;
        float bottom = H() - 270;
        final int per = Math.max(1, (int) ((bottom - top) / 122));
        final int pageCount = pages(list.size(), per);
        if (page >= pageCount) {
            page = 0;
        }
        float y = top;
        for (int k = page * per; k < Math.min(list.size(), page * per + per); k++) {
            final GameData.Skill s = list.get(k);
            String wt = s.wtype >= 0 ? "[" + GameData.WEAPON_TYPES[s.wtype] + "]" : "[内功]";
            String label;
            boolean enabled;
            Runnable act = null;
            if (dojo) {
                if (p.hasSkill(s.id)) {
                    label = s.name + " " + wt + "  已习得 · 第" + p.skillLevel(s.id) + "重";
                    enabled = false;
                } else if (p.level < s.learnLv) {
                    label = s.name + " " + wt + "  需 Lv." + s.learnLv;
                    enabled = false;
                } else {
                    label = s.name + " " + wt + "  学费 " + s.price + " 两";
                    enabled = p.silver >= s.price;
                    act = () -> learn(s);
                }
            } else {
                label = s.name + " " + wt + "  第" + p.skillLevel(s.id) + "重 · 内力" + s.mp;
                enabled = true;
            }
            Button b = addButton(36, y, W() - 72, 110, label, act);
            b.textSize = 30;
            String extra = dojo ? "" : "  威力" + Math.round(p.skillPower(s.id) * 100) + "%" + (p.matches(s) ? " 兵器相合" : "");
            b.sub = s.desc + extra;
            b.enabled = enabled;
            b.color = s.sect < 0 ? UI.PURPLE : GameData.SECTS[s.sect].color;
            y += 122;
        }
        if (pageCount > 1) {
            float bw = (W() - 72 - 14) / 2f;
            addButton(36, H() - 254, bw, 84, "上一页", () -> {
                page = (page + pageCount - 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
            addButton(36 + bw + 14, H() - 254, bw, 84, "下一页", () -> {
                page = (page + 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
        }
        addButton(36, H() - 150, W() - 72, 104, "返回", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private void learn(GameData.Skill s) {
        Player p = game.player;
        if (p.silver < s.price || p.hasSkill(s.id) || p.level < s.learnLv) {
            return;
        }
        p.silver -= s.price;
        p.learn(s.id);
        p.log("习得" + s.name);
        game.audio.sfx(Audio.SFX_LEVEL);
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
        String tip = dojo ? "奇遇武学需在江湖中寻访；武功与兵器相合时威力 +25%" : "武功每施展一次增加熟练度，最高十重";
        UI.text(c, tip, W() / 2, H() - 166, 22, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
