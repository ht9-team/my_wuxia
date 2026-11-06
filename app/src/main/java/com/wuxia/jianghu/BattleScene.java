package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/** 回合制战斗。 */
public class BattleScene extends Scene {
    private static final int ST_PLAYER = 0;
    private static final int ST_BUSY = 1;
    private static final int ST_WIN = 2;
    private static final int ST_LOSE = 3;

    private static final int MODE_MAIN = 0;
    private static final int MODE_SKILL = 1;
    private static final int MODE_ITEM = 2;

    private final GameData.Enemy e;
    private int ehp;
    private boolean enemyStunned = false;
    private int guardTurns = 0;
    private int dodgeTurns = 0;
    private int state = ST_BUSY;
    private int mode = MODE_MAIN;
    private float wait = 0f;
    private Runnable next;
    private final ArrayList<String> log = new ArrayList<>();
    private final ArrayList<FloatText> floats = new ArrayList<>();
    private float enemyShake = 0f;
    private float heroShake = 0f;
    private float slash = 0f;
    private int slashColor = UI.INK;
    private float heroLunge = 0f;
    private float enemyLunge = 0f;
    private String banner;
    private float bannerT = 0f;
    private float enemyFade = 1f;
    private String resultText = "";
    private Runnable afterResult;

    private float fieldTop;
    private float logTop;
    private float logBottom;
    private float buttonTop;

    private static final class FloatText {
        final String s;
        final float x;
        float y;
        float t;
        final int color;
        final float size;
        float delay;

        FloatText(String s, float x, float y, int color, float size, float delay) {
            this.s = s;
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
            this.t = 1.1f;
            this.delay = delay;
        }
    }

    public BattleScene(Game game, int enemyId) {
        super(game);
        e = GameData.ENEMIES[enemyId];
        ehp = e.hp;
        addLog((e.boss ? "首领 " : "") + e.name + " 拦住了你的去路！");
        boolean enemyFirst = e.agi > game.player.agi() + 6 && !e.boss;
        if (enemyFirst) {
            addLog(e.name + " 身法极快，抢先出手！");
            schedule(0.9f, this::enemyTurn);
        } else {
            schedule(0.6f, this::startPlayerTurn);
        }
    }

    private void schedule(float t, Runnable r) {
        state = ST_BUSY;
        wait = t;
        next = r;
        buttons.clear();
    }

    private void addLog(String s) {
        log.add(s);
        while (log.size() > 4) {
            log.remove(0);
        }
    }

    private float heroX() {
        return W() * 0.27f;
    }

    private float heroY() {
        return logTop - 170;
    }

    private float enemyX() {
        return W() * 0.7f;
    }

    private float enemyY() {
        return fieldTop + 170;
    }

    @Override
    public void layout() {
        buttonTop = H() - 36 - 4 * 96 - 3 * 14;
        logBottom = buttonTop - 16;
        logTop = logBottom - 176;
        fieldTop = 170;
        buttons.clear();
        if (state == ST_PLAYER) {
            buildMenu();
        } else if (state == ST_WIN || state == ST_LOSE) {
            Button b = addButton(36, H() - 150, W() - 72, 110, state == ST_WIN ? "收功离去" : "回城疗伤", () -> afterResult.run());
            b.color = state == ST_WIN ? UI.JADE : UI.RED;
        }
    }

    private void buildMenu() {
        buttons.clear();
        Player p = game.player;
        float gap = 14;
        float bw = (W() - 72 - gap) / 2f;
        if (mode == MODE_MAIN) {
            float bh = 150;
            float y = H() - 36 - bh * 2 - gap;
            Button a = addButton(36, y, bw, bh, "攻击", this::playerAttack);
            a.sub = "普通一击，回复内力";
            a.textSize = 44;
            Button s = addButton(36 + bw + gap, y, bw, bh, "武功", () -> {
                mode = MODE_SKILL;
                buildMenu();
            });
            s.sub = "施展武学";
            s.textSize = 44;
            s.color = UI.RED;
            Button it = addButton(36, y + bh + gap, bw, bh, "物品", () -> {
                mode = MODE_ITEM;
                buildMenu();
            });
            it.sub = "丹药 " + p.itemCount() + " 份";
            it.textSize = 44;
            it.color = UI.JADE;
            Button f = addButton(36 + bw + gap, y + bh + gap, bw, bh, "逃跑", this::flee);
            f.sub = e.boss ? "首领战无法脱身" : "三十六计走为上";
            f.textSize = 44;
            f.color = UI.INK_LIGHT;
            f.enabled = !e.boss;
        } else if (mode == MODE_SKILL) {
            List<Integer> list = p.skills;
            int i = 0;
            for (int id : list) {
                if (i >= 7) {
                    break;
                }
                final GameData.Skill sk = GameData.SKILLS[id];
                float x = 36 + (i % 2) * (bw + gap);
                float y = buttonTop + (i / 2) * (96 + gap);
                Button b = addButton(x, y, bw, 96, sk.name + " " + p.skillLevel(id) + "重", () -> useSkill(sk));
                b.textSize = 30;
                b.sub = "内力 " + sk.mp + " · " + typeName(sk.type);
                b.enabled = p.mp >= sk.mp;
                b.color = UI.RED;
                i++;
            }
            float x = 36 + (i % 2) * (bw + gap);
            float y = buttonTop + (i / 2) * (96 + gap);
            addButton(x, y, bw, 96, "返回", () -> {
                mode = MODE_MAIN;
                buildMenu();
            }).color = UI.INK_LIGHT;
        } else {
            int i = 0;
            for (int id = 0; id < p.items.length; id++) {
                GameData.Item it = GameData.ITEMS[id];
                if (!it.consumable() || p.items[id] <= 0 || i >= 7) {
                    continue;
                }
                final int itemId = id;
                float x = 36 + (i % 2) * (bw + gap);
                float y = buttonTop + (i / 2) * (96 + gap);
                Button b = addButton(x, y, bw, 96, it.name + " ×" + p.items[id], () -> useItem(itemId));
                b.textSize = 30;
                b.sub = it.desc;
                b.color = UI.JADE;
                i++;
            }
            float x = 36 + (i % 2) * (bw + gap);
            float y = buttonTop + (i / 2) * (96 + gap);
            addButton(x, y, bw, 96, i == 0 ? "行囊空空 · 返回" : "返回", () -> {
                mode = MODE_MAIN;
                buildMenu();
            }).color = UI.INK_LIGHT;
        }
    }

    private static String typeName(int type) {
        switch (type) {
            case GameData.SK_MULTI:
                return "连击";
            case GameData.SK_HEAL:
                return "疗伤";
            case GameData.SK_GUARD:
                return "护体";
            case GameData.SK_STUN:
                return "点穴";
            case GameData.SK_DRAIN:
                return "吸血";
            case GameData.SK_DODGE:
                return "身法";
            default:
                return "重击";
        }
    }

    private void startPlayerTurn() {
        state = ST_PLAYER;
        mode = MODE_MAIN;
        buildMenu();
    }

    // ---------------- 伤害计算 ----------------

    private boolean rollDodge(int atkAgi, int defAgi, boolean buff) {
        float chance = 5 + (defAgi - atkAgi) * 1.2f + (buff ? 40 : 0);
        chance = Math.max(3, Math.min(buff ? 75 : 35, chance));
        return game.rnd.nextFloat() * 100 < chance;
    }

    private int damage(int atk, int def, float power, boolean[] crit, int agi) {
        float base = atk * power * (0.9f + game.rnd.nextFloat() * 0.2f) * 100f / (100f + def * 3f);
        crit[0] = game.rnd.nextFloat() * 100 < 8 + agi * 0.3f;
        if (crit[0]) {
            base *= 1.6f;
        }
        return Math.max(1, Math.round(base));
    }

    /** 玩家对敌人造成一击，返回造成的伤害（闪避为 0）。 */
    private int hitEnemy(float power, float delay) {
        Player p = game.player;
        if (rollDodge(p.agi(), e.agi, false)) {
            floats.add(new FloatText("闪避", enemyX(), enemyY() - 140, UI.INK_LIGHT, 40, delay));
            return 0;
        }
        boolean[] crit = new boolean[1];
        int d = damage(p.atk(), e.def, power, crit, p.agi());
        ehp = Math.max(0, ehp - d);
        floats.add(new FloatText((crit[0] ? "暴击 " : "-") + d, enemyX() + (game.rnd.nextFloat() - 0.5f) * 80,
                enemyY() - 140, crit[0] ? UI.GOLD : UI.RED, crit[0] ? 58 : 48, delay));
        enemyShake = 0.35f;
        return d;
    }

    private void showBanner(String s) {
        banner = s;
        bannerT = 1.0f;
    }

    private void playerAttack() {
        Player p = game.player;
        heroLunge = 0.4f;
        slash = 0.4f;
        slashColor = UI.INK;
        int d = hitEnemy(1f, 0f);
        int mpGain = 3 + p.level / 2;
        p.restoreMp(mpGain);
        addLog(d > 0 ? "你挥手一击，造成 " + d + " 点伤害。" : e.name + " 闪开了你的攻击。");
        afterPlayerAction();
    }

    private void useSkill(GameData.Skill sk) {
        Player p = game.player;
        if (p.mp < sk.mp) {
            return;
        }
        p.mp -= sk.mp;
        if (p.practice(sk.id)) {
            game.toast(sk.name + " 精进至第 " + p.skillLevel(sk.id) + " 重！");
        }
        float pow = p.skillPower(sk.id);
        showBanner(sk.name);
        switch (sk.type) {
            case GameData.SK_HEAL: {
                int h = Math.round(p.maxHp * pow);
                p.heal(h);
                floats.add(new FloatText("+" + h, heroX(), heroY() - 160, UI.JADE, 50, 0));
                addLog("你运起" + sk.name + "，回复 " + h + " 点气血。");
                break;
            }
            case GameData.SK_GUARD:
                guardTurns = 3;
                addLog("你运起" + sk.name + "，周身真气鼓荡，刀枪难入。");
                break;
            case GameData.SK_DODGE:
                dodgeTurns = 3;
                addLog("你施展" + sk.name + "，身形飘忽不定。");
                break;
            case GameData.SK_MULTI: {
                heroLunge = 0.5f;
                slash = 0.6f;
                slashColor = UI.BLUE;
                int total = 0;
                for (int i = 0; i < sk.hits; i++) {
                    total += hitEnemy(pow, i * 0.15f);
                    if (ehp <= 0) {
                        break;
                    }
                }
                addLog("你使出" + sk.name + "，连环 " + sk.hits + " 击，共造成 " + total + " 点伤害！");
                break;
            }
            case GameData.SK_STUN: {
                heroLunge = 0.4f;
                slash = 0.5f;
                slashColor = UI.PURPLE;
                int d = hitEnemy(pow, 0);
                String msg = "你使出" + sk.name + "，造成 " + d + " 点伤害";
                float chance = (e.boss ? 25 : 45) + p.skillLevel(sk.id) * 3;
                if (d > 0 && ehp > 0 && game.rnd.nextFloat() * 100 < chance) {
                    enemyStunned = true;
                    msg += "，并封住了对方穴道！";
                    floats.add(new FloatText("定身", enemyX(), enemyY() - 200, UI.PURPLE, 44, 0.3f));
                } else {
                    msg += "。";
                }
                addLog(msg);
                break;
            }
            case GameData.SK_DRAIN: {
                heroLunge = 0.4f;
                slash = 0.5f;
                slashColor = UI.JADE;
                int d = hitEnemy(pow, 0);
                int h = d / 2;
                p.heal(h);
                if (h > 0) {
                    floats.add(new FloatText("+" + h, heroX(), heroY() - 160, UI.JADE, 44, 0.2f));
                }
                addLog("你使出" + sk.name + "，造成 " + d + " 点伤害，回复 " + h + " 点气血。");
                break;
            }
            default: {
                heroLunge = 0.5f;
                slash = 0.6f;
                slashColor = UI.RED;
                int d = hitEnemy(pow, 0);
                addLog(d > 0 ? "你使出" + sk.name + "，造成 " + d + " 点伤害！" : "你使出" + sk.name + "，却被对方避开了。");
                break;
            }
        }
        afterPlayerAction();
    }

    private void useItem(int id) {
        Player p = game.player;
        GameData.Item it = GameData.ITEMS[id];
        if (p.items[id] <= 0) {
            return;
        }
        p.items[id]--;
        Inventory.applyConsumable(p, it);
        floats.add(new FloatText(it.name, heroX(), heroY() - 160, UI.JADE, 40, 0));
        addLog("你服下" + it.name + "。" + it.desc);
        afterPlayerAction();
    }

    private void flee() {
        Player p = game.player;
        float chance = Math.max(20, Math.min(90, 50 + (p.agi() - e.agi) * 2));
        if (game.rnd.nextFloat() * 100 < chance) {
            game.toast("你脚底抹油，溜之大吉");
            game.save();
            game.setScene(new LocationScene(game));
        } else {
            addLog("你想要逃走，却被 " + e.name + " 拦了回来！");
            afterPlayerAction();
        }
    }

    private void afterPlayerAction() {
        if (ehp <= 0) {
            schedule(0.9f, this::win);
        } else {
            schedule(0.9f, this::enemyTurn);
        }
    }

    private void enemyTurn() {
        Player p = game.player;
        if (enemyStunned) {
            enemyStunned = false;
            addLog(e.name + " 穴道被封，动弹不得！");
        } else {
            boolean special = game.rnd.nextFloat() < (e.boss ? 0.4f : 0.28f);
            float power = special ? e.skillPower : 1f;
            enemyLunge = 0.4f;
            if (special) {
                showBanner(e.skill);
            }
            if (rollDodge(e.agi, p.agi(), dodgeTurns > 0)) {
                floats.add(new FloatText("闪避", heroX(), heroY() - 160, UI.INK_LIGHT, 40, 0.1f));
                addLog(e.name + (special ? "使出「" + e.skill + "」" : "出手攻击") + "，被你闪身避过。");
            } else {
                int def = p.def();
                if (guardTurns > 0) {
                    def = def * 2 + 10;
                }
                boolean[] crit = new boolean[1];
                int d = damage(e.atk, def, power, crit, e.agi / 2);
                p.hp = Math.max(0, p.hp - d);
                heroShake = 0.35f;
                floats.add(new FloatText((crit[0] ? "暴击 " : "-") + d, heroX() + (game.rnd.nextFloat() - 0.5f) * 60,
                        heroY() - 160, crit[0] ? UI.GOLD : UI.RED, crit[0] ? 56 : 46, 0.1f));
                addLog(e.name + (special ? "使出「" + e.skill + "」" : "出手攻击") + "，你受到 " + d + " 点伤害。");
            }
        }
        if (guardTurns > 0) {
            guardTurns--;
        }
        if (dodgeTurns > 0) {
            dodgeTurns--;
        }
        if (p.hp <= 0) {
            schedule(0.9f, this::lose);
        } else {
            schedule(0.7f, this::startPlayerTurn);
        }
    }

    private void win() {
        Player p = game.player;
        state = ST_WIN;
        StringBuilder sb = new StringBuilder();
        int silver = Math.max(1, Math.round(e.silver * (0.8f + game.rnd.nextFloat() * 0.4f)));
        p.silver += silver;
        p.kills++;
        sb.append("击败 ").append(e.name).append("！\n修为 +").append(e.exp).append("   银两 +").append(silver);
        if (e.drop >= 0 && game.rnd.nextFloat() < e.dropRate) {
            p.items[e.drop]++;
            sb.append("\n获得 ").append(GameData.ITEMS[e.drop].name);
            if (!GameData.ITEMS[e.drop].consumable()) {
                sb.append("（可在行囊中装备）");
            }
        }
        for (String s : p.gainExp(e.exp)) {
            sb.append("\n").append(s);
        }
        boolean questDone = false;
        boolean finale = false;
        GameData.Quest q = p.currentQuest();
        if (q != null && q.target == e.id) {
            p.questProgress++;
            if (p.questProgress >= q.count) {
                questDone = true;
                p.questStage++;
                p.questProgress = 0;
                p.silver += q.silver;
                sb.append("\n\n任务完成【").append(q.title).append("】\n奖励 修为 +").append(q.exp).append("  银两 +").append(q.silver);
                for (String s : p.gainExp(q.exp)) {
                    sb.append("\n").append(s);
                }
                if (p.questStage >= GameData.QUESTS.length) {
                    p.finished = true;
                    finale = true;
                }
            } else {
                sb.append("\n任务进度 ").append(p.questProgress).append("/").append(q.count);
            }
        }
        resultText = sb.toString();
        game.save();
        final boolean done = questDone;
        final boolean fin = finale;
        final int stage = p.questStage;
        afterResult = () -> {
            if (fin) {
                game.setScene(new StoryScene(game, "终章 · 笑傲江湖",
                        "天魔教主轰然倒地，黑木崖上乌云散尽，一缕阳光洒落。\n\n"
                                + "正道群雄齐聚，尊你为武林盟主。你却只是淡淡一笑，将盟主令牌交还。\n\n"
                                + "「江湖路远，我只想做个快意恩仇的侠客。」\n\n"
                                + "此后江湖之上，常有人说起一位" + GameData.SECTS[p.sect].name + "的少年侠客——"
                                + p.name + "。\n\n（全剧终 · 你可以继续游历江湖）",
                        () -> game.goLocation(p.lastTown)));
            } else if (done && stage < GameData.QUESTS.length) {
                GameData.Quest nq = GameData.QUESTS[stage];
                game.setScene(new StoryScene(game, "新的委托 · " + nq.title, nq.desc
                        + "\n\n目的地：" + GameData.LOCATIONS[nq.location].name,
                        () -> game.goLocation(p.location)));
            } else {
                game.goLocation(p.location);
            }
        };
        layout();
    }

    private void lose() {
        Player p = game.player;
        state = ST_LOSE;
        int lost = p.silver / 2;
        p.silver -= lost;
        p.fullRestore();
        p.day += 2;
        p.location = p.lastTown;
        resultText = "你力竭倒地，眼前一黑……\n\n醒来时已被好心人送回" + GameData.LOCATIONS[p.lastTown].name
                + "，丢失了 " + lost + " 两银子。\n胜败乃兵家常事，大侠请重新来过。";
        game.save();
        afterResult = () -> game.goLocation(p.lastTown);
        layout();
    }

    // ---------------- 更新 & 绘制 ----------------

    @Override
    public void update(float dt) {
        if (wait > 0) {
            wait -= dt;
            if (wait <= 0 && next != null) {
                Runnable r = next;
                next = null;
                r.run();
            }
        }
        enemyShake = Math.max(0, enemyShake - dt);
        heroShake = Math.max(0, heroShake - dt);
        slash = Math.max(0, slash - dt);
        heroLunge = Math.max(0, heroLunge - dt);
        enemyLunge = Math.max(0, enemyLunge - dt);
        bannerT = Math.max(0, bannerT - dt);
        if (state == ST_WIN) {
            enemyFade = Math.max(0, enemyFade - dt * 1.5f);
        }
        for (int i = floats.size() - 1; i >= 0; i--) {
            FloatText f = floats.get(i);
            if (f.delay > 0) {
                f.delay -= dt;
                continue;
            }
            f.t -= dt;
            f.y -= dt * 70;
            if (f.t <= 0) {
                floats.remove(i);
            }
        }
    }

    @Override
    public void draw(Canvas c) {
        GameData.Location l = GameData.LOCATIONS[game.player.location];
        UI.background(c, W(), H(), l.variant, 0xFFEBDDBE, 0xFFB9A57E, game.time);
        Player p = game.player;
        // 敌方信息
        RectF er = new RectF(16, 16, W() - 16, 150);
        UI.panel(c, er, 0xEE231E19, e.boss ? UI.RED : UI.GOLD);
        UI.text(c, (e.boss ? "【首领】" : "") + e.name, 40, 66, 38, e.boss ? 0xFFE06050 : UI.PAPER);
        UI.text(c, "Lv." + e.lv, W() - 40, 66, 30, UI.GOLD, Paint.Align.RIGHT);
        UI.bar(c, 40, 88, W() - 80, 34, ehp / (float) e.hp, UI.RED, ehp + " / " + e.hp);
        if (enemyStunned) {
            UI.text(c, "穴道被封", W() - 40, 140, 22, 0xFFC090E0, Paint.Align.RIGHT);
        }

        // 战场
        float shakeE = enemyShake > 0 ? (float) Math.sin(enemyShake * 80) * 14 : 0;
        float shakeH = heroShake > 0 ? (float) Math.sin(heroShake * 80) * 14 : 0;
        float lungeH = (float) Math.sin(Math.min(1f, heroLunge / 0.4f) * Math.PI) * 90;
        float lungeE = (float) Math.sin(Math.min(1f, enemyLunge / 0.4f) * Math.PI) * 90;
        boolean blinkHidden = state == ST_WIN && ((int) (enemyFade * 12)) % 2 == 1;
        if (enemyFade > 0 && !blinkHidden) {
            Sprites.enemy(c, e, enemyX() + shakeE - lungeE, enemyY() + lungeE * 0.6f, 0.95f, game.time);
        }
        int robe = GameData.SECTS[p.sect].color;
        if (guardTurns > 0) {
            c.drawCircle(heroX(), heroY(), 150, UI.fill(0x33B7862C));
            c.drawCircle(heroX(), heroY(), 150, UI.stroke(0x88B7862C, 4));
        }
        if (dodgeTurns > 0) {
            Sprites.hero(c, heroX() - 30, heroY(), 0.95f, 0x552F5A8A, 0x55FFFFFF, 1, game.time + 0.5f, p.weapon >= 0);
        }
        Sprites.hero(c, heroX() + shakeH + lungeH, heroY() - lungeH * 0.6f, 0.95f, robe, UI.PAPER_LIGHT, 1, game.time, p.weapon >= 0);

        if (slash > 0) {
            float k = slash / 0.6f;
            int a = (int) (k * 220);
            Paint sp = UI.stroke((a << 24) | (slashColor & 0xFFFFFF), 12 * k + 2);
            RectF arc = new RectF(enemyX() - 140, enemyY() - 160, enemyX() + 140, enemyY() + 120);
            c.drawArc(arc, 200 + (1 - k) * 60, 120, false, sp);
            c.drawLine(enemyX() - 130, enemyY() - 120 + (1 - k) * 60, enemyX() + 120, enemyY() + 60 - (1 - k) * 40, sp);
        }

        // 我方状态
        float sy = logTop - 60;
        UI.bar(c, W() * 0.5f, sy - 50, W() * 0.5f - 36, 30, p.hp / (float) p.maxHp, UI.RED, "气血 " + p.hp + "/" + p.maxHp);
        UI.bar(c, W() * 0.5f, sy - 10, W() * 0.5f - 36, 30, p.mp / (float) Math.max(1, p.maxMp), UI.BLUE, "内力 " + p.mp + "/" + p.maxMp);
        String buffs = (guardTurns > 0 ? "护体" + guardTurns + "  " : "") + (dodgeTurns > 0 ? "身法" + dodgeTurns : "");
        if (!buffs.isEmpty()) {
            UI.text(c, buffs, W() - 36, sy - 66, 24, UI.INK, Paint.Align.RIGHT);
        }

        // 招式横幅
        if (bannerT > 0 && banner != null) {
            float k = Math.min(1f, bannerT / 0.3f);
            int a = (int) (k * 255);
            float bs = 84 + (1 - bannerT) * 20;
            UI.text(c, banner, W() / 2 + 4, fieldTop + 260 + 4, bs, (a / 3 << 24), Paint.Align.CENTER);
            UI.text(c, banner, W() / 2, fieldTop + 260, bs, (a << 24) | (UI.RED & 0xFFFFFF), Paint.Align.CENTER);
        }

        for (FloatText f : floats) {
            if (f.delay > 0) {
                continue;
            }
            int a = (int) (Math.min(1f, f.t / 0.3f) * 255);
            UI.text(c, f.s, f.x + 3, f.y + 3, f.size, (a / 2) << 24, Paint.Align.CENTER);
            UI.text(c, f.s, f.x, f.y, f.size, (a << 24) | (f.color & 0xFFFFFF), Paint.Align.CENTER);
        }

        // 战斗记录
        if (state == ST_WIN || state == ST_LOSE) {
            RectF rr = new RectF(36, logTop, W() - 36, H() - 170);
            UI.panel(c, rr);
            UI.text(c, state == ST_WIN ? "胜" : "败", W() - 100, logTop + 90, 90, state == ST_WIN ? UI.JADE : UI.RED, Paint.Align.CENTER);
            UI.wrap(c, resultText, 64, logTop + 60, W() - 220, 30, UI.INK, 44);
        } else {
            RectF lr = new RectF(36, logTop, W() - 36, logBottom);
            UI.panel(c, lr);
            float y = logTop + 46;
            for (int i = 0; i < log.size(); i++) {
                int col = i == log.size() - 1 ? UI.INK : UI.INK_LIGHT;
                UI.wrap(c, log.get(i), 60, y, W() - 120, 24, col, 30);
                y += 34;
            }
            if (state == ST_BUSY) {
                UI.text(c, "……", W() / 2, buttonTop + 120, 48, UI.INK_LIGHT, Paint.Align.CENTER);
            }
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        if (state == ST_PLAYER && mode != MODE_MAIN) {
            mode = MODE_MAIN;
            buildMenu();
        }
        return true;
    }
}
