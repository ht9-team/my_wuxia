package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 当前所在地点：城镇提供客栈 / 集市 / 铁匠铺 / 学艺；野外分层探索，探明全境后可挑战首领。 */
public class LocationScene extends Scene {
    private static final float BH = 92;
    private static final float GAP = 14;
    private String log;

    public LocationScene(Game game) {
        super(game);
        log = loc().desc;
    }

    private GameData.Location loc() {
        return GameData.LOCATIONS[game.player.location];
    }


    @Override
    public int music() {
        return loc().town ? Audio.BGM_TOWN : Audio.BGM_WILD;
    }

    private float buttonsTop() {
        return H() - 30 - 4 * BH - 3 * GAP;
    }

    @Override
    public void layout() {
        buttons.clear();
        final Player p = game.player;
        final GameData.Location l = loc();
        float bw = (W() - 72 - GAP) / 2f;
        float y0 = buttonsTop();
        float xL = 36;
        float xR = 36 + bw + GAP;
        float r1 = y0 + BH + GAP;
        float r2 = r1 + BH + GAP;
        float r3 = r2 + BH + GAP;
        if (l.town) {
            Button inn = addButton(xL, y0, bw, BH, "客栈", () -> game.setScene(new InnScene(game)));
            inn.sub = p.hasBounty() ? "悬赏 " + p.bountyProgress + "/" + p.bountyCount : "住店 · 悬赏 · 打听";
            addButton(xR, y0, bw, BH, "集市商铺", () -> game.setScene(new ShopScene(game))).sub = "丹药 · 兵器 · 护甲";
            addButton(xL, r1, bw, BH, "铁匠铺", () -> game.setScene(new SmithScene(game))).sub = "强化兵甲";
            addButton(xR, r1, bw, BH, "拜师学艺", () -> game.setScene(new SkillScene(game, true))).sub =
                    GameData.SECTS[p.sect].name + "武学";
        } else {
            int prog = p.explore[l.id];
            Button ex = addButton(xL, y0, bw, BH, "四处探索", this::explore);
            ex.color = UI.RED;
            ex.sub = prog >= l.floors ? "已探明全境" : "探索进度 " + prog + "/" + l.floors;
            Button bb;
            if (l.storyBoss) {
                if (Story.finalAvailable(p)) {
                    bb = addButton(xR, y0, bw, BH, "登临绝顶", () -> Story.play(game, "climax"));
                    bb.sub = "决战天魔殿";
                } else {
                    bb = addButton(xR, y0, bw, BH, "崖顶", null);
                    bb.enabled = false;
                    if (p.finished) {
                        bb.sub = "已归于平静";
                    } else if (Story.current(p).type == Story.T_FINAL) {
                        bb.sub = "探索未尽 " + prog + "/" + l.floors;
                    } else {
                        bb.sub = "时机未到";
                    }
                }
            } else {
                final GameData.Enemy b = GameData.ENEMIES[l.boss];
                bb = addButton(xR, y0, bw, BH, "挑战首领",
                        () -> game.setScene(new BattleScene(game, b.id, BattleScene.KIND_BOSS)));
                bb.enabled = prog >= l.floors;
                bb.sub = bb.enabled ? b.name + " Lv." + b.lv : "探索未尽 " + prog + "/" + l.floors;
            }
            bb.color = UI.PURPLE;
            addButton(xL, r1, bw, BH, "打坐调息", this::meditate).sub = "耗时一日，恢复两成";
            addButton(xR, r1, bw, BH, "武学", () -> game.setScene(new SkillScene(game, false))).sub = "已习武功";
        }
        addButton(xL, r2, bw, BH, "行囊", () -> game.setScene(new BagScene(game))).sub = "物品 · 装备 · 属性";
        addButton(xR, r2, bw, BH, "江湖志", () -> game.setScene(new JournalScene(game))).sub = "剧情 · 人物 · 结局";
        addButton(xL, r3, bw, BH, "江湖地图", game::goMap).sub = "前往他处";
        Button sys = addButton(xR, r3, bw, BH, "系统", () -> game.setScene(new SystemScene(game, false)));
        sys.sub = "存档 · 声音 · 标题";
        sys.color = UI.INK_LIGHT;
    }


    private void meditate() {
        Player p = game.player;
        int h = Math.max(1, p.maxHp() / 5);
        int m = Math.max(1, p.maxMp() / 5);
        p.heal(h);
        p.restoreMp(m);
        p.day++;
        log = "你寻了一块青石盘膝而坐，运功一日。气血 +" + h + "，内力 +" + m + "。";
        game.audio.sfx(Audio.SFX_HEAL);
        game.save();
    }

    private void explore() {
        Player p = game.player;
        GameData.Location l = loc();
        int r = game.rnd.nextInt(100);
        if (l.hermitSkill >= 0 && !p.hasSkill(l.hermitSkill) && p.level >= GameData.SKILLS[l.hermitSkill].learnLv
                && p.explore[l.id] >= 2 && r < 8) {
            Story.hermit(game, l.hermitSkill);
            return;
        }
        if (r < 62) {
            int[] pool = l.enemies;
            int id = pool[game.rnd.nextInt(pool.length)];
            game.setScene(new BattleScene(game, id, BattleScene.KIND_WILD));
            return;
        }
        if (r < 74) {
            game.setScene(new DialogScene(game, Story.randomEvent(game)));
            return;
        }
        if (r < 84) {
            int s = 5 + game.rnd.nextInt(10 + p.level * 6);
            p.silver += s;
            game.audio.sfx(Audio.SFX_COIN);
            log = "你在草丛中发现了一个钱袋，得到 " + s + " 两银子。";
        } else if (r < 92) {
            int item;
            if (l.id == 8 || l.id == 12) {
                item = 37;
            } else if (p.level >= 9) {
                item = 2;
            } else if (p.level >= 5) {
                item = 0;
            } else {
                item = 12;
            }
            p.items[item]++;
            log = "你在路边寻到了一份" + GameData.ITEMS[item].name + "。";
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

    @Override
    public void draw(Canvas c) {
        GameData.Location l = loc();
        Player p = game.player;
        UI.paperBackground(c, W(), H(), l.variant, game.time);
        UI.hud(c, game);
        UI.title(c, l.name, W() / 2, 250, 62);
        String tag;
        int tagColor = UI.INK_LIGHT;
        if (l.town) {
            tag = "城镇";
        } else if (p.level < l.minLv) {
            tag = "此地凶险（建议 Lv." + l.minLv + "）";
            tagColor = UI.RED;
        } else {
            tag = "野外 · 共 " + l.floors + " 层";
        }
        UI.text(c, tag, W() / 2, 300, 24, tagColor, Paint.Align.CENTER);

        float top = 322;
        float bottom = buttonsTop() - 18;
        RectF logR = new RectF(36, top, W() - 36, top + 150);
        UI.panel(c, logR);
        UI.wrap(c, log, 62, top + 48, W() - 124, 27, UI.INK, 38);

        Story.Stage s = Story.current(p);
        float qy = top + 166;
        RectF qr = new RectF(36, qy, W() - 36, bottom);
        UI.panel(c, qr);
        UI.seal(c, p.finished ? "传说" : "主线", qr.right - 60, qr.top + 58, 70);
        UI.text(c, s.chapter, 60, qy + 44, 24, UI.INK_LIGHT);
        UI.text(c, "【" + s.title + "】", 54, qy + 88, 32, UI.RED);
        float yy = UI.wrap(c, s.desc, 62, qy + 132, W() - 210, 25, UI.INK, 36);
        if (qr.bottom - 24 > yy) {
            UI.wrap(c, Story.objective(p), 62, Math.max(yy + 6, qr.bottom - 64), W() - 124, 24, UI.JADE, 34);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        game.goMap();
        return true;
    }
}
