package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 当前所在地点：城镇提供客栈 / 商铺 / 学艺，野外提供探索与首领战。 */
public class LocationScene extends Scene {
    private String log = "";
    private boolean confirmMenu = false;

    public LocationScene(Game game) {
        super(game);
        log = loc().desc;
    }

    private GameData.Location loc() {
        return GameData.LOCATIONS[game.player.location];
    }

    private int innPrice() {
        return 5 + game.player.level * 3;
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        GameData.Location l = loc();
        float bw = (W() - 36 * 2 - 20) / 2f;
        float bh = 104;
        float y0 = H() - 36 - bh * 3 - 20 * 2;
        float xL = 36;
        float xR = 36 + bw + 20;
        if (l.town) {
            Button inn = addButton(xL, y0, bw, bh, "客栈歇息", () -> {
                if (p.silver < innPrice()) {
                    game.toast("银两不足，掌柜把你赶了出来");
                    return;
                }
                p.silver -= innPrice();
                p.fullRestore();
                p.day++;
                log = "你在客栈要了一间上房，美美睡了一觉。气血与内力尽数恢复。";
                game.save();
                layout();
            });
            inn.sub = innPrice() + " 两";
            addButton(xR, y0, bw, bh, "集市商铺", () -> game.setScene(new ShopScene(game))).sub = "药材 · 兵器 · 护甲";
            addButton(xL, y0 + bh + 20, bw, bh, "拜师学艺", () -> game.setScene(new SkillScene(game, true))).sub = GameData.SECTS[p.sect].name + "武学";
        } else {
            Button ex = addButton(xL, y0, bw, bh, "四处探索", this::explore);
            ex.color = UI.RED;
            ex.sub = "可能遭遇敌人";
            GameData.Quest q = p.currentQuest();
            if (l.boss >= 0 && q != null && q.target == l.boss) {
                GameData.Enemy b = GameData.ENEMIES[l.boss];
                Button bb = addButton(xR, y0, bw, bh, "挑战首领", () -> game.setScene(new BattleScene(game, b.id)));
                bb.color = UI.PURPLE;
                bb.sub = b.name + " Lv." + b.lv;
            } else {
                Button med = addButton(xR, y0, bw, bh, "打坐调息", () -> {
                    int h = Math.max(1, p.maxHp / 5);
                    int m = Math.max(1, p.maxMp / 5);
                    p.heal(h);
                    p.restoreMp(m);
                    p.day++;
                    log = "你寻了一块青石盘膝而坐，运功一日。气血 +" + h + "，内力 +" + m + "。";
                    game.save();
                });
                med.sub = "耗时一日，恢复少许";
            }
            addButton(xL, y0 + bh + 20, bw, bh, "武学", () -> game.setScene(new SkillScene(game, false))).sub = "查看已习武功";
        }
        addButton(xR, y0 + bh + 20, bw, bh, "行囊", () -> game.setScene(new BagScene(game))).sub = "物品 · 装备 · 属性";
        addButton(xL, y0 + (bh + 20) * 2, bw, bh, "江湖地图", game::goMap).sub = "前往他处";
        addButton(xR, y0 + (bh + 20) * 2, bw, bh, confirmMenu ? "确认返回标题？" : "存档 · 标题", () -> {
            game.save();
            if (!confirmMenu) {
                confirmMenu = true;
                game.toast("已存档");
                layout();
                return;
            }
            game.setScene(new TitleScene(game));
        }).color = UI.INK_LIGHT;
    }

    private void explore() {
        Player p = game.player;
        GameData.Location l = loc();
        int r = game.rnd.nextInt(100);
        if (l.hermitSkill >= 0 && !p.hasSkill(l.hermitSkill)
                && p.level >= GameData.SKILLS[l.hermitSkill].learnLv && r < 8) {
            hermit(l.hermitSkill);
            return;
        }
        if (r < 68) {
            int[] pool = l.enemies;
            int id = pool[game.rnd.nextInt(pool.length)];
            game.setScene(new BattleScene(game, id));
        } else if (r < 80) {
            int s = 5 + game.rnd.nextInt(10 + p.level * 6);
            p.silver += s;
            log = "你在草丛中发现了一个钱袋，得到 " + s + " 两银子。";
        } else if (r < 90) {
            int item = p.level >= 6 ? 2 : 12;
            p.items[item]++;
            log = "你在路边采到了一份" + GameData.ITEMS[item].name + "。";
        } else {
            log = GameData.SCENERY[game.rnd.nextInt(GameData.SCENERY.length)];
            if (game.rnd.nextBoolean()) {
                int e = 3 + p.level * 2;
                log += "（修为 +" + e + "）";
                for (String s : p.gainExp(e)) {
                    game.toast(s);
                }
            }
        }
        game.save();
    }

    private void hermit(int skill) {
        Player p = game.player;
        GameData.Skill s = GameData.SKILLS[skill];
        String text;
        switch (skill) {
            case 16:
                text = "你在竹林深处迷了路，误入一处山洞。洞中石壁上画着一位女子的步法图谱，旁书「凌波微步」四字。\n\n你依图而行，竟觉身轻如羽……";
                break;
            case 17:
                text = "古墓深处，你发现一具枯骨，身旁一册羊皮卷上写着「吸星大法」。\n\n你一时好奇，依法运功，只觉丹田中一股吸力油然而生……";
                break;
            default:
                text = "思过崖后洞中，一位青袍老者负手而立。他看了你半晌，淡淡道：「你根骨不错，老夫传你几招剑法。」\n\n「独孤九剑，有进无退！招招都是进攻……」";
                break;
        }
        p.learn(skill);
        game.save();
        game.setScene(new StoryScene(game, "奇遇 · " + s.name, text + "\n\n（习得奇功：" + s.name + "）",
                () -> game.setScene(new LocationScene(game))));
    }

    @Override
    public void draw(Canvas c) {
        GameData.Location l = loc();
        UI.paperBackground(c, W(), H(), l.variant, game.time);
        UI.hud(c, game);
        Player p = game.player;
        UI.title(c, l.name, W() / 2, 260, 66);
        if (!l.town) {
            String warn = p.level < l.minLv ? "此地凶险（建议 Lv." + l.minLv + "）" : "野外";
            UI.text(c, warn, W() / 2, 316, 26, p.level < l.minLv ? UI.RED : UI.INK_LIGHT, Paint.Align.CENTER);
        } else {
            UI.text(c, "城镇", W() / 2, 316, 26, UI.INK_LIGHT, Paint.Align.CENTER);
        }
        float top = 350;
        float bottomButtons = H() - 36 - 104 * 3 - 40;
        RectF logR = new RectF(36, top, W() - 36, top + 200);
        UI.panel(c, logR);
        UI.wrap(c, log, 64, top + 56, W() - 128, 30, UI.INK, 44);

        GameData.Quest q = p.currentQuest();
        float qy = top + 220;
        RectF qr = new RectF(36, qy, W() - 36, Math.min(bottomButtons - 24, qy + 260));
        UI.panel(c, qr);
        UI.seal(c, "主线", qr.right - 64, qr.top + 60, 72);
        if (q != null) {
            UI.text(c, "【" + q.title + "】", 60, qy + 56, 34, UI.RED);
            UI.wrap(c, q.desc, 64, qy + 104, W() - 220, 26, UI.INK, 38);
            String prog = "地点：" + GameData.LOCATIONS[q.location].name + "    进度：" + p.questProgress + "/" + q.count;
            UI.text(c, prog, 64, qr.bottom - 26, 26, UI.JADE);
        } else {
            UI.text(c, "【江湖传说】", 60, qy + 56, 34, UI.RED);
            UI.wrap(c, "天魔教已灭，你的名字传遍了大江南北。江湖依旧，你仍可四处游历，磨砺武功。", 64, qy + 104, W() - 220, 26, UI.INK, 38);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        game.goMap();
        return true;
    }
}
