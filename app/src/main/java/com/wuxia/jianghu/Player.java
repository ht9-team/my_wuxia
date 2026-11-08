package com.wuxia.jianghu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Player {
    public static final int MAX_SKILL_LV = 10;
    public static final int SAVE_VER = 3;
    private static final int CHOICES_MAX = 80;
    private static final int JOURNAL_MAX = 60;

    public String name = "无名";
    public int sect = 0;
    public int level = 1;
    public int exp = 0;
    public int silver = 60;
    public int day = 1;
    public int baseHp, hp, baseMp, mp;
    public int baseAtk, baseDef, baseAgi;
    public int weapon = -1;
    public int armor = -1;
    public int accessory = -1;
    public int location = 0;
    public int lastTown = 0;
    public int stage = 0;
    public int progress = 0;
    public int justice = 0;
    public int affSu = 0;
    public int affXiao = 0;
    public int kills = 0;
    public boolean finished = false;
    public int ending = -1;
    /** 悬赏任务：目标敌人（-1 表示没有）、地点、数量、进度与奖励。 */
    public int bountyEnemy = -1;
    public int bountyLoc = -1;
    public int bountyCount = 0;
    public int bountyProgress = 0;
    public int bountySilver = 0;
    public int bountyExp = 0;
    public int bountiesDone = 0;
    public final int[] items = new int[GameData.ITEMS.length];
    public final int[] enhance = new int[GameData.ITEMS.length];
    public final int[] explore = new int[GameData.LOCATIONS.length];
    public final HashSet<String> flags = new HashSet<>();
    public final ArrayList<String> journal = new ArrayList<>();
    /** 关键抉择记录（流程记录）。 */
    public final ArrayList<String> choices = new ArrayList<>();
    /** 支线进度：-1 未开启，0..n-1 当前步骤，n 已完成。 */
    public final int[] side = new int[Story.SIDES.length];
    public final int[] sideProg = new int[Story.SIDES.length];
    public final ArrayList<Integer> skills = new ArrayList<>();
    public final HashMap<Integer, Integer> skillLv = new HashMap<>();
    public final HashMap<Integer, Integer> skillExp = new HashMap<>();

    public Player() {
        Arrays.fill(side, -1);
    }

    public static Player create(String name, int sect) {
        GameData.Sect s = GameData.SECTS[sect];
        Player p = new Player();
        p.name = name;
        p.sect = sect;
        p.baseHp = s.hp;
        p.baseMp = s.mp;
        p.hp = s.hp;
        p.mp = s.mp;
        p.baseAtk = s.atk;
        p.baseDef = s.def;
        p.baseAgi = s.agi;
        p.learn(s.startSkill);
        p.weapon = s.startWeapon;
        p.items[0] = 3;
        p.items[1] = 2;
        p.location = 14 + sect;
        p.lastTown = 3;
        p.flags.add("v" + p.location);
        p.flags.add("v3");
        p.stage = Story.S_P_TRIAL;
        p.clampVitals();
        return p;
    }

    // ---------------- 属性 ----------------

    /** 兵甲经强化后的主属性：每强化一级 +10%。 */
    public int equipValue(int id) {
        if (id < 0) {
            return 0;
        }
        return Math.round(GameData.ITEMS[id].value * (1f + 0.1f * enhance[id]));
    }

    private static int bonus(int id, int k) {
        if (id < 0) {
            return 0;
        }
        GameData.Item it = GameData.ITEMS[id];
        switch (k) {
            case 0:
                return it.atk;
            case 1:
                return it.def;
            case 2:
                return it.hp;
            case 3:
                return it.mp;
            case 4:
                return it.agi;
            default:
                return it.crit;
        }
    }

    private int sumBonus(int k) {
        return bonus(weapon, k) + bonus(armor, k) + bonus(accessory, k);
    }

    public int atk() {
        return baseAtk + (weapon >= 0 ? equipValue(weapon) : 0) + sumBonus(0);
    }

    public int def() {
        return baseDef + (armor >= 0 ? equipValue(armor) : 0) + sumBonus(1);
    }

    public int maxHp() {
        return baseHp + sumBonus(2);
    }

    public int maxMp() {
        return baseMp + sumBonus(3);
    }

    public int agi() {
        return Math.max(1, baseAgi + sumBonus(4));
    }

    /** 暴击率（百分比）。 */
    public int crit() {
        return 5 + agi() / 4 + sumBonus(5);
    }

    /** 当前兵器类别；空手视为拳。 */
    public int weaponType() {
        return weapon >= 0 ? GameData.ITEMS[weapon].wtype : GameData.W_FIST;
    }

    /** 用于绘制的兵器类别；空手返回 -1。 */
    public int drawWeapon() {
        return weapon >= 0 ? GameData.ITEMS[weapon].wtype : -1;
    }

    public int weaponSpecial() {
        return weapon >= 0 ? GameData.ITEMS[weapon].special : GameData.SP_NONE;
    }

    public boolean matches(GameData.Skill s) {
        return s.wtype >= 0 && s.wtype == weaponType();
    }

    public void clampVitals() {
        hp = Math.min(hp, maxHp());
        mp = Math.min(mp, maxMp());
    }

    public int expNeed() {
        return 20 + level * level * 15;
    }

    /** 获得修为，返回升级提示文本（可能为空列表）。 */
    public List<String> gainExp(int amount) {
        ArrayList<String> msgs = new ArrayList<>();
        exp += amount;
        GameData.Sect s = GameData.SECTS[sect];
        while (exp >= expNeed()) {
            exp -= expNeed();
            level++;
            baseHp += s.hpGrow;
            baseMp += s.mpGrow;
            baseAtk += s.atkGrow;
            baseDef += s.defGrow;
            baseAgi += s.agiGrow;
            hp = maxHp();
            mp = maxMp();
            msgs.add("功力精进！升至 " + level + " 级");
        }
        return msgs;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp(), hp + amount);
    }

    public void restoreMp(int amount) {
        mp = Math.min(maxMp(), mp + amount);
    }

    public void fullRestore() {
        hp = maxHp();
        mp = maxMp();
    }

    // ---------------- 武学 ----------------

    public boolean hasSkill(int id) {
        return skills.contains(id);
    }

    public void learn(int id) {
        if (!skills.contains(id)) {
            skills.add(id);
            skillLv.put(id, 1);
            skillExp.put(id, 0);
        }
    }

    public int skillLevel(int id) {
        Integer v = skillLv.get(id);
        return v == null ? 1 : v;
    }

    /** 武功使用一次增加熟练度，返回是否升级。 */
    public boolean practice(int id) {
        int lv = skillLevel(id);
        if (lv >= MAX_SKILL_LV) {
            return false;
        }
        Integer e = skillExp.get(id);
        int ne = (e == null ? 0 : e) + 1;
        if (ne >= lv * 3) {
            skillLv.put(id, lv + 1);
            skillExp.put(id, 0);
            return true;
        }
        skillExp.put(id, ne);
        return false;
    }

    public float skillPower(int id) {
        GameData.Skill s = GameData.SKILLS[id];
        float p = s.power * (1f + 0.08f * (skillLevel(id) - 1));
        if (matches(s)) {
            p *= 1.25f;
        }
        return p;
    }

    // ---------------- 剧情 ----------------

    public boolean has(String flag) {
        return flags.contains(flag);
    }

    public void set(String flag) {
        flags.add(flag);
    }

    public void choose(String s) {
        choices.add(s);
        while (choices.size() > CHOICES_MAX) {
            choices.remove(0);
        }
    }

    public void log(String s) {
        journal.add("第" + day + "日  " + s);
        while (journal.size() > JOURNAL_MAX) {
            journal.remove(0);
        }
    }

    public boolean hasBounty() {
        return bountyEnemy >= 0;
    }

    public String bountyText() {
        if (!hasBounty()) {
            return "暂无悬赏";
        }
        return "击败" + GameData.ENEMIES[bountyEnemy].name + " " + bountyProgress + "/" + bountyCount
                + " · " + GameData.LOCATIONS[bountyLoc].name;
    }

    /** 记录悬赏击杀；完成时发放奖励并返回提示文本，否则返回进度文本或 null。 */
    public String onBountyKill(int enemyId, List<String> levelUps) {
        if (!hasBounty() || enemyId != bountyEnemy || location != bountyLoc) {
            return null;
        }
        bountyProgress++;
        if (bountyProgress < bountyCount) {
            return "悬赏进度 " + bountyProgress + "/" + bountyCount;
        }
        silver += bountySilver;
        levelUps.addAll(gainExp(bountyExp));
        String msg = "悬赏完成！赏银 +" + bountySilver + "  修为 +" + bountyExp;
        log("完成悬赏：击败" + GameData.ENEMIES[bountyEnemy].name + "×" + bountyCount);
        bountiesDone++;
        bountyEnemy = -1;
        bountyProgress = 0;
        return msg;
    }

    public int itemCount() {
        int n = 0;
        for (int i = 0; i < items.length; i++) {
            if (GameData.ITEMS[i].consumable()) {
                n += items[i];
            }
        }
        return n;
    }

    // ---------------- 存档 ----------------

    private static JSONArray intArray(int[] a) {
        JSONArray arr = new JSONArray();
        for (int v : a) {
            arr.put(v);
        }
        return arr;
    }

    private static void readInts(JSONArray arr, int[] out) throws JSONException {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length() && i < out.length; i++) {
            out[i] = arr.getInt(i);
        }
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("ver", SAVE_VER);
        o.put("name", name);
        o.put("sect", sect);
        o.put("level", level);
        o.put("exp", exp);
        o.put("silver", silver);
        o.put("day", day);
        o.put("baseHp", baseHp);
        o.put("hp", hp);
        o.put("baseMp", baseMp);
        o.put("mp", mp);
        o.put("atk", baseAtk);
        o.put("def", baseDef);
        o.put("agi", baseAgi);
        o.put("weapon", weapon);
        o.put("armor", armor);
        o.put("accessory", accessory);
        o.put("location", location);
        o.put("lastTown", lastTown);
        o.put("stage", stage);
        o.put("progress", progress);
        o.put("justice", justice);
        o.put("affSu", affSu);
        o.put("affXiao", affXiao);
        o.put("kills", kills);
        o.put("finished", finished);
        o.put("ending", ending);
        o.put("bountyEnemy", bountyEnemy);
        o.put("bountyLoc", bountyLoc);
        o.put("bountyCount", bountyCount);
        o.put("bountyProgress", bountyProgress);
        o.put("bountySilver", bountySilver);
        o.put("bountyExp", bountyExp);
        o.put("bountiesDone", bountiesDone);
        o.put("items", intArray(items));
        o.put("enhance", intArray(enhance));
        o.put("explore", intArray(explore));
        JSONArray fl = new JSONArray();
        for (String f : flags) {
            fl.put(f);
        }
        o.put("flags", fl);
        JSONArray jr = new JSONArray();
        for (String j : journal) {
            jr.put(j);
        }
        o.put("journal", jr);
        JSONArray ch = new JSONArray();
        for (String c : choices) {
            ch.put(c);
        }
        o.put("choices", ch);
        o.put("side", intArray(side));
        o.put("sideProg", intArray(sideProg));
        JSONArray sk = new JSONArray();
        for (int id : skills) {
            JSONObject so = new JSONObject();
            so.put("id", id);
            so.put("lv", skillLevel(id));
            Integer e = skillExp.get(id);
            so.put("exp", e == null ? 0 : e);
            sk.put(so);
        }
        o.put("skills", sk);
        return o;
    }

    private static int validItem(int id) {
        return id >= 0 && id < GameData.ITEMS.length ? id : -1;
    }

    /** 旧版（v1）主线进度映射到新版章节。 */
    private static int mapOldStage(int qs, Player p) {
        switch (qs) {
            case 0:
                return Story.S_BAMBOO;
            case 1:
                return Story.S_HEIFENG;
            case 2:
                return Story.S_TOMB;
            case 3:
                p.flags.add("route_alliance");
                return Story.S_B_HUASHAN;
            case 4:
                p.flags.add("route_alliance");
                return Story.S_XIANGYANG;
            default:
                return Story.S_POST;
        }
    }

    /** v2 的 13 个章节编号映射到 v3。 */
    private static final int[] V2_STAGES = {
            Story.S_BAMBOO, Story.S_HEIFENG, Story.S_LUOYANG, Story.S_TOMB, Story.S_A_DALI, Story.S_A_MIAO,
            Story.S_B_HUASHAN, Story.S_B_TAIHU, Story.S_XIANGYANG, Story.S_DESERT, Story.S_TIANSHAN,
            Story.S_FINAL, Story.S_POST,
    };

    public static Player fromJson(JSONObject o) throws JSONException {
        Player p = new Player();
        int ver = o.optInt("ver", 1);
        p.name = o.optString("name", "无名");
        p.sect = Math.max(0, Math.min(GameData.SECTS.length - 1, o.optInt("sect", 0)));
        p.level = Math.max(1, o.optInt("level", 1));
        p.exp = o.optInt("exp", 0);
        p.silver = o.optInt("silver", 0);
        p.day = o.optInt("day", 1);
        p.hp = o.optInt("hp", 100);
        p.mp = o.optInt("mp", 40);
        p.baseAtk = o.optInt("atk", 10);
        p.baseDef = o.optInt("def", 5);
        p.baseAgi = o.optInt("agi", 5);
        p.weapon = validItem(o.optInt("weapon", -1));
        p.armor = validItem(o.optInt("armor", -1));
        p.accessory = validItem(o.optInt("accessory", -1));
        p.location = o.optInt("location", 0);
        p.lastTown = o.optInt("lastTown", 0);
        p.kills = o.optInt("kills", 0);
        p.finished = o.optBoolean("finished", false);
        readInts(o.optJSONArray("items"), p.items);
        JSONArray sk = o.optJSONArray("skills");
        if (sk != null) {
            for (int i = 0; i < sk.length(); i++) {
                JSONObject so = sk.getJSONObject(i);
                int id = so.getInt("id");
                if (id < 0 || id >= GameData.SKILLS.length || p.skills.contains(id)) {
                    continue;
                }
                p.skills.add(id);
                p.skillLv.put(id, so.optInt("lv", 1));
                p.skillExp.put(id, so.optInt("exp", 0));
            }
        }
        if (ver < 2) {
            p.baseHp = o.optInt("maxHp", 100);
            p.baseMp = o.optInt("maxMp", 40);
            p.stage = p.finished ? Story.S_POST : mapOldStage(o.optInt("questStage", 0), p);
            p.progress = 0;
            p.flags.add("v0");
        } else {
            p.baseHp = o.optInt("baseHp", 100);
            p.baseMp = o.optInt("baseMp", 40);
            int st = o.optInt("stage", 0);
            if (ver == 2) {
                st = st >= 0 && st < V2_STAGES.length ? V2_STAGES[st] : Story.S_BAMBOO;
                p.progress = 0;
            } else {
                p.progress = o.optInt("progress", 0);
            }
            p.stage = st;
            p.justice = o.optInt("justice", 0);
            p.affSu = o.optInt("affSu", 0);
            p.affXiao = o.optInt("affXiao", 0);
            p.ending = o.optInt("ending", -1);
            p.bountyEnemy = o.optInt("bountyEnemy", -1);
            p.bountyLoc = o.optInt("bountyLoc", -1);
            p.bountyCount = o.optInt("bountyCount", 0);
            p.bountyProgress = o.optInt("bountyProgress", 0);
            p.bountySilver = o.optInt("bountySilver", 0);
            p.bountyExp = o.optInt("bountyExp", 0);
            p.bountiesDone = o.optInt("bountiesDone", 0);
            if (p.bountyEnemy >= GameData.ENEMIES.length || p.bountyLoc < 0 || p.bountyLoc >= GameData.LOCATIONS.length) {
                p.bountyEnemy = -1;
            }
            readInts(o.optJSONArray("enhance"), p.enhance);
            readInts(o.optJSONArray("explore"), p.explore);
            JSONArray fl = o.optJSONArray("flags");
            if (fl != null) {
                for (int i = 0; i < fl.length(); i++) {
                    p.flags.add(fl.getString(i));
                }
            }
            JSONArray jr = o.optJSONArray("journal");
            if (jr != null) {
                for (int i = 0; i < jr.length(); i++) {
                    p.journal.add(jr.getString(i));
                }
            }
            JSONArray ch = o.optJSONArray("choices");
            if (ch != null) {
                for (int i = 0; i < ch.length(); i++) {
                    p.choices.add(ch.getString(i));
                }
            }
            if (o.has("side")) {
                readInts(o.optJSONArray("side"), p.side);
                readInts(o.optJSONArray("sideProg"), p.sideProg);
            }
        }
        if (p.stage < 0 || p.stage >= Story.STAGES.length) {
            p.stage = 0;
        }
        if (p.location < 0 || p.location >= GameData.LOCATIONS.length) {
            p.location = 0;
        }
        if (ver < 3) {
            p.log("从旧版江湖中归来（v" + ver + " 存档已升级）");
        }
        if (p.lastTown < 0 || p.lastTown >= GameData.LOCATIONS.length || !GameData.LOCATIONS[p.lastTown].town) {
            p.lastTown = 0;
        }
        p.flags.add("v" + p.location);
        p.flags.add("v" + p.lastTown);
        p.clampVitals();
        if (p.hp <= 0) {
            p.hp = 1;
        }
        return p;
    }
}
