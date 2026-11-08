package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/** 回合制战斗：兵器特效、武功相合、中毒、首领暴怒。 */
public class BattleScene extends Scene {
    public static final int KIND_WILD = 0;
    public static final int KIND_BOSS = 1;
    public static final int KIND_STORY = 2;

    private static final int ST_PLAYER = 0;
    private static final int ST_BUSY = 1;
    private static final int ST_WIN = 2;
    private static final int ST_LOSE = 3;

    private static final int MODE_MAIN = 0;
    private static final int MODE_SKILL = 1;
    private static final int MODE_ITEM = 2;

    private final GameData.Enemy e;
    private final int kind;
    private final Runnable onWin;
    private final Runnable onLose;
    private final int emax;
    private int ehp;
    private float eatkMul = 1f;
    private boolean enraged = false;
    private boolean enemyStunned = false;
    private int enemyPoison = 0;
    private int guardTurns = 0;
    private int dodgeTurns = 0;
    private int playerPoison = 0;
    private int state = ST_BUSY;
    private int mode = MODE_MAIN;
    private int page = 0;
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
    private Story.Goal doneGoal;
    private boolean playerStunned = false;
    private final boolean allyXiao;
    private final boolean allySu;

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

    public BattleScene(Game game, int enemyId, int kind, Runnable onWin, Runnable onLose) {
        super(game);
        this.e = GameData.ENEMIES[enemyId];
        this.kind = kind;
        this.onWin = onWin;
        this.onLose = onLose;
        this.emax = e.hp;
        this.ehp = e.hp;
        addLog((e.boss ? "首领 " : "") + e.name + " 拦住了你的去路！");
        Player pl = game.player;
        allyXiao = kind == KIND_STORY && Story.allyXiao(pl, e.id);
        allySu = kind == KIND_STORY && Story.allySu(pl, e.id);
        if (allyXiao) {
            addLog("萧寒拔剑而出，与你并肩而立！");
        }
        if (allySu) {
            addLog("苏晚晴短剑出鞘，守在你的身侧！");
        }
        if (e.boss) {
            game.audio.sfx(Audio.SFX_GONG);
        }
        boolean enemyFirst = !e.boss && e.agi > game.player.agi() + 6;
        if (enemyFirst) {
            addLog(e.name + " 身法极快，抢先出手！");
            schedule(0.9f, this::enemyTurn);
        } else {
            schedule(0.6f, this::startPlayerTurn);
        }
    }

    public BattleScene(Game game, int enemyId, int kind) {
        this(game, enemyId, kind, null, null);
    }

    @Override
    public int music() {
        return e.boss ? Audio.BGM_BOSS : Audio.BGM_BATTLE;
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
            Button b = addButton(36, H() - 150, W() - 72, 110, state == ST_WIN ? "收功离去" : "败走", () -> afterResult.run());
            b.color = state == ST_WIN ? UI.JADE : UI.RED;
        }
    }

    private float slotX(int i, float bw, float gap) {
        return 36 + (i % 2) * (bw + gap);
    }

    private float slotY(int i, float gap) {
        return buttonTop + (i / 2) * (96 + gap);
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
            String sp = GameData.SPECIAL_NAMES[p.weaponSpecial()];
            a.sub = sp.isEmpty() ? "普通一击，回复内力" : "普通一击 · " + sp;
            a.textSize = 44;
            Button s = addButton(36 + bw + gap, y, bw, bh, "武功", () -> {
                mode = MODE_SKILL;
                page = 0;
                buildMenu();
            });
            s.sub = "施展武学";
            s.textSize = 44;
            s.color = UI.RED;
            Button it = addButton(36, y + bh + gap, bw, bh, "物品", () -> {
                mode = MODE_ITEM;
                page = 0;
                buildMenu();
            });
            it.sub = "丹药 " + p.itemCount() + " 份";
            it.textSize = 44;
            it.color = UI.JADE;
            boolean canFlee = kind == KIND_WILD && !e.boss;
            Button f = addButton(36 + bw + gap, y + bh + gap, bw, bh, "逃跑", this::flee);
            f.sub = canFlee ? "三十六计走为上" : "此战无法脱身";
            f.textSize = 44;
            f.color = UI.INK_LIGHT;
            f.enabled = canFlee;
            return;
        }
        ArrayList<Integer> ids = new ArrayList<>();
        if (mode == MODE_SKILL) {
            ids.addAll(p.skills);
        } else {
            for (int id = 0; id < p.items.length; id++) {
                if (GameData.ITEMS[id].consumable() && p.items[id] > 0) {
                    ids.add(id);
                }
            }
        }
        int per = ids.size() > 7 ? 6 : 7;
        int pageCount = pages(ids.size(), per);
        if (page >= pageCount) {
            page = 0;
        }
        int i = 0;
        for (int k = page * per; k < Math.min(ids.size(), page * per + per); k++) {
            final int id = ids.get(k);
            Button b;
            if (mode == MODE_SKILL) {
                final GameData.Skill sk = GameData.SKILLS[id];
                b = addButton(slotX(i, bw, gap), slotY(i, gap), bw, 96, sk.name + " " + p.skillLevel(id) + "重", () -> useSkill(sk));
                b.sub = "内力 " + sk.mp + " · " + typeName(sk.type) + (p.matches(sk) ? " · 相合" : "");
                b.enabled = p.mp >= sk.mp;
                b.color = UI.RED;
            } else {
                GameData.Item it = GameData.ITEMS[id];
                b = addButton(slotX(i, bw, gap), slotY(i, gap), bw, 96, it.name + " ×" + p.items[id], () -> useItem(id));
                b.sub = it.desc;
                b.color = UI.JADE;
            }
            b.textSize = 30;
            i++;
        }
        if (pageCount > 1) {
            Button nb = addButton(slotX(i, bw, gap), slotY(i, gap), bw, 96, "下一页", () -> {
                page = (page + 1) % pages(ids.size(), per);
                buildMenu();
            });
            nb.sub = (page + 1) + " / " + pageCount;
            nb.color = UI.INK_LIGHT;
            i++;
        }
        addButton(slotX(i, bw, gap), slotY(i, gap), bw, 96, ids.isEmpty() ? "空空如也 · 返回" : "返回", () -> {
            mode = MODE_MAIN;
            buildMenu();
        }).color = UI.INK_LIGHT;
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
            case GameData.SK_POISON:
                return "施毒";
            default:
                return "重击";
        }
    }

    private void startPlayerTurn() {
        if (playerStunned) {
            playerStunned = false;
            addLog("你心神恍惚，这一回合无法出手！");
            floats.add(new FloatText("失神", heroX(), heroY() - 180, UI.PURPLE, 42, 0));
            schedule(0.9f, this::enemyTurn);
            return;
        }
        state = ST_PLAYER;
        mode = MODE_MAIN;
        buildMenu();
    }

    // ---------------- 伤害计算 ----------------

    private boolean rollDodge(int atkAgi, int defAgi, boolean buff) {
        float chance = 5 + (defAgi - atkAgi) * 1.0f + (buff ? 40 : 0);
        chance = Math.max(3, Math.min(buff ? 75 : 30, chance));
        return game.rnd.nextFloat() * 100 < chance;
    }

    private int calc(float atk, float def, float power, boolean crit) {
        float base = atk * power * (0.9f + game.rnd.nextFloat() * 0.2f) * 100f / (100f + def * 2f);
        if (crit) {
            base *= 1.6f;
        }
        return Math.max(1, Math.round(base));
    }

    /** 玩家对敌人造成一击，返回造成的伤害（闪避为 0）。 */
    private int hitEnemy(float power, float delay, boolean procs) {
        Player p = game.player;
        if (rollDodge(p.agi(), e.agi, false)) {
            floats.add(new FloatText("闪避", enemyX(), enemyY() - 140, UI.INK_LIGHT, 40, delay));
            game.audio.sfx(Audio.SFX_MISS);
            return 0;
        }
        int sp = p.weaponSpecial();
        float def = e.def * (sp == GameData.SP_PIERCE ? 0.6f : 1f);
        boolean crit = game.rnd.nextFloat() * 100 < p.crit();
        int d = calc(p.atk(), def, power, crit);
        ehp = Math.max(0, ehp - d);
        floats.add(new FloatText((crit ? "暴击 " : "-") + d, enemyX() + (game.rnd.nextFloat() - 0.5f) * 80,
                enemyY() - 140, crit ? UI.GOLD : UI.RED, crit ? 58 : 48, delay));
        enemyShake = 0.35f;
        game.audio.sfx(crit ? Audio.SFX_CRIT : Audio.SFX_HIT);
        if (procs && ehp > 0) {
            switch (sp) {
                case GameData.SP_DRAIN: {
                    int h = Math.max(1, d * 15 / 100);
                    p.heal(h);
                    floats.add(new FloatText("+" + h, heroX(), heroY() - 160, UI.JADE, 40, delay + 0.1f));
                    break;
                }
                case GameData.SP_FROST:
                    if (!enemyStunned && game.rnd.nextFloat() < (e.boss ? 0.08f : 0.15f)) {
                        enemyStunned = true;
                        floats.add(new FloatText("冰封", enemyX(), enemyY() - 200, UI.BLUE, 44, delay + 0.2f));
                    }
                    break;
                case GameData.SP_POISON:
                    if (game.rnd.nextFloat() < 0.25f) {
                        enemyPoison = 3;
                        floats.add(new FloatText("中毒", enemyX(), enemyY() - 200, UI.JADE, 44, delay + 0.2f));
                    }
                    break;
                default:
                    break;
            }
        }
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
        int d = hitEnemy(1f, 0f, true);
        String msg = d > 0 ? "你挥手一击，造成 " + d + " 点伤害。" : e.name + " 闪开了你的攻击。";
        if (d > 0 && ehp > 0 && p.weaponSpecial() == GameData.SP_DOUBLE && game.rnd.nextFloat() < 0.25f) {
            int d2 = hitEnemy(0.6f, 0.18f, false);
            msg += "（连击 " + d2 + "）";
        }
        p.restoreMp(3 + p.level / 2);
        addLog(msg);
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
        boolean match = p.matches(sk);
        showBanner(sk.name);
        game.audio.sfx(Audio.SFX_SKILL);
        String tag = match ? "（兵器相合）" : "";
        switch (sk.type) {
            case GameData.SK_HEAL: {
                int h = Math.round(p.maxHp() * pow);
                p.heal(h);
                playerPoison = 0;
                floats.add(new FloatText("+" + h, heroX(), heroY() - 160, UI.JADE, 50, 0));
                game.audio.sfx(Audio.SFX_HEAL);
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
                    total += hitEnemy(pow, i * 0.15f, i == 0);
                    if (ehp <= 0) {
                        break;
                    }
                }
                addLog("你使出" + sk.name + tag + "，连环 " + sk.hits + " 击，共造成 " + total + " 点伤害！");
                break;
            }
            case GameData.SK_STUN: {
                heroLunge = 0.4f;
                slash = 0.5f;
                slashColor = UI.PURPLE;
                int d = hitEnemy(pow, 0, true);
                String msg = "你使出" + sk.name + tag + "，造成 " + d + " 点伤害";
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
                int d = hitEnemy(pow, 0, true);
                int h = d / 2;
                p.heal(h);
                if (h > 0) {
                    floats.add(new FloatText("+" + h, heroX(), heroY() - 160, UI.JADE, 44, 0.2f));
                }
                addLog("你使出" + sk.name + tag + "，造成 " + d + " 点伤害，回复 " + h + " 点气血。");
                break;
            }
            case GameData.SK_POISON: {
                heroLunge = 0.4f;
                slash = 0.5f;
                slashColor = UI.JADE;
                int d = hitEnemy(pow, 0, true);
                String msg = "你使出" + sk.name + tag + "，造成 " + d + " 点伤害";
                if (d > 0 && ehp > 0) {
                    enemyPoison = 4;
                    msg += "，剧毒入体！";
                    floats.add(new FloatText("剧毒", enemyX(), enemyY() - 200, UI.JADE, 44, 0.3f));
                } else {
                    msg += "。";
                }
                addLog(msg);
                break;
            }
            default: {
                heroLunge = 0.5f;
                slash = 0.6f;
                slashColor = UI.RED;
                int d = hitEnemy(pow, 0, true);
                addLog(d > 0 ? "你使出" + sk.name + tag + "，造成 " + d + " 点伤害！" : "你使出" + sk.name + "，却被对方避开了。");
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
        String eff = Inventory.applyConsumable(p, it);
        if (it.type == GameData.IT_CURE) {
            playerPoison = 0;
        }
        game.audio.sfx(Audio.SFX_HEAL);
        floats.add(new FloatText(it.name, heroX(), heroY() - 160, UI.JADE, 40, 0));
        addLog("你服下" + it.name + "，" + eff + "。");
        afterPlayerAction();
    }

    private void flee() {
        Player p = game.player;
        float chance = Math.max(20, Math.min(90, 50 + (p.agi() - e.agi) * 2));
        if (game.rnd.nextFloat() * 100 < chance) {
            game.toast("你脚底抹油，溜之大吉");
            game.save();
            Story.toLocation(game);
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
        if (enemyPoison > 0) {
            int pd = Math.max(5, Math.round(emax * (e.boss ? 0.02f : 0.05f)));
            ehp = Math.max(0, ehp - pd);
            enemyPoison--;
            floats.add(new FloatText("毒 -" + pd, enemyX(), enemyY() - 120, UI.JADE, 40, 0));
            addLog(e.name + " 毒发，损失 " + pd + " 点气血。");
            if (ehp <= 0) {
                schedule(0.8f, this::win);
                return;
            }
        }
        if (e.boss && !enraged && ehp <= emax / 2) {
            enraged = true;
            eatkMul = 1.25f;
            showBanner("暴怒");
            addLog(e.name + " 怒发冲冠，攻势大涨！");
        }
        if (enemyStunned) {
            enemyStunned = false;
            addLog(e.name + " 动弹不得！");
        } else {
            boolean special = game.rnd.nextFloat() < (e.boss ? 0.4f : 0.28f);
            float power = special ? e.skillPower : 1f;
            enemyLunge = 0.4f;
            if (special) {
                showBanner(e.skill);
            }
            String act = special ? "使出「" + e.skill + "」" : "出手攻击";
            if (rollDodge(e.agi, p.agi(), dodgeTurns > 0)) {
                floats.add(new FloatText("闪避", heroX(), heroY() - 160, UI.INK_LIGHT, 40, 0.1f));
                game.audio.sfx(Audio.SFX_MISS);
                addLog(e.name + act + "，被你闪身避过。");
            } else {
                float def = p.def();
                if (guardTurns > 0) {
                    def = def * 2 + 10;
                }
                boolean crit = game.rnd.nextFloat() * 100 < 5 + e.agi * 0.15f;
                int d = calc(e.atk * eatkMul, def, power, crit);
                p.hp = Math.max(0, p.hp - d);
                heroShake = 0.35f;
                game.audio.sfx(crit ? Audio.SFX_CRIT : Audio.SFX_HURT);
                floats.add(new FloatText((crit ? "暴击 " : "-") + d, heroX() + (game.rnd.nextFloat() - 0.5f) * 60,
                        heroY() - 160, crit ? UI.GOLD : UI.RED, crit ? 56 : 46, 0.1f));
                String msg = e.name + act + "，你受到 " + d + " 点伤害。";
                if (special && p.hp > 0) {
                    if (e.effect == GameData.E_POISON && playerPoison == 0) {
                        playerPoison = 3;
                        msg += "你中毒了！";
                    } else if (e.effect == GameData.E_STUN && !playerStunned && game.rnd.nextFloat() < 0.35f) {
                        playerStunned = true;
                        msg += "你心神被摄！";
                    } else if (e.effect == GameData.E_DRAIN) {
                        int h = Math.max(1, d * 3 / 10);
                        ehp = Math.min(emax, ehp + h);
                        floats.add(new FloatText("+" + h, enemyX(), enemyY() - 120, UI.JADE, 38, 0.3f));
                        msg += e.name + "吸走了 " + h + " 点气血。";
                    }
                }
                addLog(msg);
            }
        }
        if (p.hp > 0 && allyActions()) {
            schedule(0.9f, this::win);
            return;
        }
        if (playerPoison > 0 && p.hp > 0) {
            int pd = Math.max(3, p.maxHp() * 5 / 100);
            p.hp = Math.max(0, p.hp - pd);
            playerPoison--;
            floats.add(new FloatText("毒 -" + pd, heroX(), heroY() - 110, UI.JADE, 36, 0.4f));
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

    /** 同伴助战：返回敌人是否因此倒下。 */
    private boolean allyActions() {
        Player p = game.player;
        if (allyXiao && ehp > 0) {
            boolean crit = game.rnd.nextFloat() < 0.12f;
            float pow = p.has("xiao_sword") ? 1.7f : 1.3f;
            int d = calc(30 + p.level * 5, e.def, pow, crit);
            ehp = Math.max(0, ehp - d);
            enemyShake = 0.3f;
            floats.add(new FloatText("萧寒 -" + d, enemyX() - 40, enemyY() - 90, UI.BLUE, 38, 0.35f));
            addLog("萧寒一招「流云剑」，刺中对方，造成 " + d + " 点伤害。");
        }
        if (allySu && ehp > 0) {
            if (p.hp < p.maxHp() / 2) {
                int h = Math.max(1, p.maxHp() * 12 / 100);
                p.heal(h);
                floats.add(new FloatText("晚晴 +" + h, heroX(), heroY() - 120, UI.JADE, 36, 0.5f));
                addLog("苏晚晴喂你服下一粒药丸，回复 " + h + " 点气血。");
            } else {
                int d = calc(25 + p.level * 4, e.def, 1.1f, false);
                ehp = Math.max(0, ehp - d);
                floats.add(new FloatText("晚晴 -" + d, enemyX() + 40, enemyY() - 60, 0xFFC86A9A, 36, 0.5f));
                addLog("苏晚晴短剑翻飞，造成 " + d + " 点伤害。");
            }
        }
        return ehp <= 0;
    }

    private void win() {
        Player p = game.player;
        state = ST_WIN;
        StringBuilder sb = new StringBuilder();
        int silver = Math.max(1, Math.round(e.silver * (0.8f + game.rnd.nextFloat() * 0.4f)));
        p.silver += silver;
        p.kills++;
        sb.append("击败 ").append(e.name).append("！\n修为 +").append(e.exp).append("   银两 +").append(silver);
        boolean first = e.boss && !p.has("boss_" + e.id);
        if (e.boss) {
            p.set("boss_" + e.id);
        }
        float rate = e.boss ? (first ? 1f : 0.2f) : e.dropRate;
        if (e.drop >= 0 && game.rnd.nextFloat() < rate) {
            p.items[e.drop]++;
            GameData.Item it = GameData.ITEMS[e.drop];
            sb.append("\n获得 ").append(it.name);
            if (it.equipable()) {
                sb.append("（可在行囊中装备）");
            }
        }
        if ((e.boss || e.lv >= 6) && game.rnd.nextFloat() < (e.boss ? 1f : 0.08f)) {
            int n = e.boss ? 1 + game.rnd.nextInt(2) : 1;
            p.items[Inventory.IRON] += n;
            sb.append("\n获得 玄铁 ×").append(n);
        }
        List<String> ups = p.gainExp(e.exp);
        for (String s : ups) {
            sb.append("\n").append(s);
        }
        GameData.Location l = GameData.LOCATIONS[p.location];
        if (kind == KIND_WILD && !l.town && p.explore[l.id] < l.floors) {
            p.explore[l.id]++;
            sb.append("\n探索深入 ").append(p.explore[l.id]).append("/").append(l.floors);
            if (p.explore[l.id] >= l.floors) {
                sb.append("（已探明全境）");
            }
        }
        if (kind != KIND_STORY) {
            doneGoal = Story.onKill(game, e.id);
            for (Story.Goal goal : Story.goals(p)) {
                if (goal.type == Story.T_KILL && goal.target == e.id && goal.loc == p.location
                        && Story.progress(p, goal) < goal.count) {
                    sb.append("\n").append(goal.side < 0 ? "任务" : "支线").append("进度 ")
                            .append(Story.progress(p, goal)).append("/").append(goal.count);
                }
            }
        }
        if (kind != KIND_STORY) {
            ArrayList<String> bountyUps = new ArrayList<>();
            String bm = p.onBountyKill(e.id, bountyUps);
            if (bm != null) {
                sb.append("\n").append(bm);
                for (String s : bountyUps) {
                    sb.append("\n").append(s);
                }
                if (!bountyUps.isEmpty()) {
                    ups = bountyUps;
                }
            }
        }
        resultText = sb.toString();
        game.audio.sfx(ups.isEmpty() ? Audio.SFX_WIN : Audio.SFX_LEVEL);
        game.save();
        afterResult = () -> {
            if (doneGoal != null) {
                Story.completeGoal(game, doneGoal);
            } else if (onWin != null) {
                onWin.run();
            } else {
                Story.toLocation(game);
            }
        };
        layout();
    }

    private void lose() {
        final Player p = game.player;
        state = ST_LOSE;
        game.audio.sfx(Audio.SFX_LOSE);
        if (onLose != null) {
            p.hp = Math.max(1, p.maxHp() * 3 / 10);
            resultText = "你力竭倒地，败下阵来……";
            afterResult = onLose;
        } else {
            int lost = p.silver / 2;
            p.silver -= lost;
            p.fullRestore();
            p.day += 2;
            p.location = p.lastTown;
            resultText = "你力竭倒地，眼前一黑……\n\n醒来时已被好心人送回" + GameData.LOCATIONS[p.lastTown].name
                    + "，丢失了 " + lost + " 两银子。\n胜败乃兵家常事，大侠请重新来过。";
            afterResult = () -> game.goLocation(p.lastTown);
        }
        game.save();
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
        Player p = game.player;
        GameData.Location l = GameData.LOCATIONS[p.location];
        UI.background(c, W(), H(), l.variant, 0xFFEBDDBE, 0xFFB9A57E, game.time);
        // 敌方信息
        RectF er = new RectF(16, 16, W() - 16, 150);
        UI.panel(c, er, 0xEE231E19, e.boss ? UI.RED : UI.GOLD);
        UI.text(c, (e.boss ? "【首领】" : "") + e.name, 40, 66, 38, e.boss ? 0xFFE06050 : UI.PAPER);
        UI.text(c, "Lv." + e.lv, W() - 40, 66, 30, UI.GOLD, Paint.Align.RIGHT);
        UI.bar(c, 40, 88, W() - 80, 34, ehp / (float) emax, UI.RED, ehp + " / " + emax);
        String est = (enemyStunned ? "定身 " : "") + (enemyPoison > 0 ? "中毒" + enemyPoison + " " : "") + (enraged ? "暴怒" : "");
        if (!est.isEmpty()) {
            UI.text(c, est, W() - 40, 145, 22, 0xFFE0A0A0, Paint.Align.RIGHT);
        }

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
            Sprites.hero(c, heroX() - 30, heroY(), 0.95f, 0x552F5A8A, 0x55FFFFFF, 1, game.time + 0.5f, -1, false);
        }
        if (playerPoison > 0) {
            c.drawCircle(heroX(), heroY() + 20, 120, UI.fill(0x223F6B2F));
        }
        if (allyXiao) {
            Sprites.hero(c, heroX() - 110, heroY() + 40, 0.62f, 0xFFD8DEE6, 0xFF2F5A8A, 1, game.time + 0.8f, GameData.W_SWORD, false);
        }
        if (allySu) {
            Sprites.hero(c, heroX() + 120, heroY() + 50, 0.62f, 0xFF6B3E7A, 0xFFE8B0C8, 1, game.time + 1.6f, GameData.W_SWORD, true);
        }
        Sprites.hero(c, heroX() + shakeH + lungeH, heroY() - lungeH * 0.6f, 0.95f, robe, UI.PAPER_LIGHT, 1, game.time,
                p.drawWeapon(), false);

        if (slash > 0) {
            float k = slash / 0.6f;
            int a = (int) (Math.min(1f, k) * 220);
            Paint sp = UI.stroke((a << 24) | (slashColor & 0xFFFFFF), 12 * k + 2);
            RectF arc = new RectF(enemyX() - 140, enemyY() - 160, enemyX() + 140, enemyY() + 120);
            c.drawArc(arc, 200 + (1 - k) * 60, 120, false, sp);
            c.drawLine(enemyX() - 130, enemyY() - 120 + (1 - k) * 60, enemyX() + 120, enemyY() + 60 - (1 - k) * 40, sp);
        }

        // 我方状态
        float sy = logTop - 60;
        UI.bar(c, W() * 0.5f, sy - 50, W() * 0.5f - 36, 30, p.hp / (float) p.maxHp(), UI.RED, "气血 " + p.hp + "/" + p.maxHp());
        UI.bar(c, W() * 0.5f, sy - 10, W() * 0.5f - 36, 30, p.mp / (float) Math.max(1, p.maxMp()), UI.BLUE, "内力 " + p.mp + "/" + p.maxMp());
        String buffs = (guardTurns > 0 ? "护体" + guardTurns + "  " : "") + (dodgeTurns > 0 ? "身法" + dodgeTurns + "  " : "")
                + (playerPoison > 0 ? "中毒" + playerPoison : "");
        if (!buffs.isEmpty()) {
            UI.text(c, buffs, W() - 36, sy - 66, 24, UI.INK, Paint.Align.RIGHT);
        }

        if (bannerT > 0 && banner != null) {
            float k = Math.min(1f, bannerT / 0.3f);
            int a = (int) (k * 255);
            float bs = 84 + (1 - bannerT) * 20;
            UI.text(c, banner, W() / 2 + 4, fieldTop + 260 + 4, bs, (a / 3) << 24, Paint.Align.CENTER);
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

        if (state == ST_WIN || state == ST_LOSE) {
            RectF rr = new RectF(36, logTop - 40, W() - 36, H() - 170);
            UI.panel(c, rr);
            UI.text(c, state == ST_WIN ? "胜" : "败", W() - 100, logTop + 50, 90, state == ST_WIN ? UI.JADE : UI.RED, Paint.Align.CENTER);
            UI.wrap(c, resultText, 64, logTop + 20, W() - 220, 28, UI.INK, 42);
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
