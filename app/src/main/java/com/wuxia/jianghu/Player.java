package com.wuxia.jianghu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player {
    public static final int MAX_SKILL_LV = 10;

    public String name = "无名";
    public int sect = 0;
    public int level = 1;
    public int exp = 0;
    public int silver = 60;
    public int day = 1;
    public int maxHp, hp, maxMp, mp;
    public int baseAtk, baseDef, baseAgi;
    public int weapon = -1;
    public int armor = -1;
    public int location = 0;
    public int lastTown = 0;
    public int questStage = 0;
    public int questProgress = 0;
    public int kills = 0;
    public boolean finished = false;
    public int[] items = new int[GameData.ITEMS.length];
    public final ArrayList<Integer> skills = new ArrayList<>();
    public final HashMap<Integer, Integer> skillLv = new HashMap<>();
    public final HashMap<Integer, Integer> skillExp = new HashMap<>();

    public static Player create(String name, int sect) {
        GameData.Sect s = GameData.SECTS[sect];
        Player p = new Player();
        p.name = name;
        p.sect = sect;
        p.maxHp = s.hp;
        p.maxMp = s.mp;
        p.hp = s.hp;
        p.mp = s.mp;
        p.baseAtk = s.atk;
        p.baseDef = s.def;
        p.baseAgi = s.agi;
        p.learn(s.startSkill);
        p.items[0] = 3;
        p.items[1] = 2;
        return p;
    }

    public int atk() {
        return baseAtk + (weapon >= 0 ? GameData.ITEMS[weapon].value : 0);
    }

    public int def() {
        return baseDef + (armor >= 0 ? GameData.ITEMS[armor].value : 0);
    }

    public int agi() {
        return baseAgi;
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
            maxHp += s.hpGrow;
            maxMp += s.mpGrow;
            baseAtk += s.atkGrow;
            baseDef += s.defGrow;
            baseAgi += s.agiGrow;
            hp = maxHp;
            mp = maxMp;
            msgs.add("功力精进！升至 " + level + " 级");
        }
        return msgs;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void restoreMp(int amount) {
        mp = Math.min(maxMp, mp + amount);
    }

    public void fullRestore() {
        hp = maxHp;
        mp = maxMp;
    }

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
        return GameData.SKILLS[id].power * (1f + 0.08f * (skillLevel(id) - 1));
    }

    public GameData.Quest currentQuest() {
        if (questStage >= 0 && questStage < GameData.QUESTS.length) {
            return GameData.QUESTS[questStage];
        }
        return null;
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

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("name", name);
        o.put("sect", sect);
        o.put("level", level);
        o.put("exp", exp);
        o.put("silver", silver);
        o.put("day", day);
        o.put("maxHp", maxHp);
        o.put("hp", hp);
        o.put("maxMp", maxMp);
        o.put("mp", mp);
        o.put("atk", baseAtk);
        o.put("def", baseDef);
        o.put("agi", baseAgi);
        o.put("weapon", weapon);
        o.put("armor", armor);
        o.put("location", location);
        o.put("lastTown", lastTown);
        o.put("questStage", questStage);
        o.put("questProgress", questProgress);
        o.put("kills", kills);
        o.put("finished", finished);
        JSONArray it = new JSONArray();
        for (int v : items) {
            it.put(v);
        }
        o.put("items", it);
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

    public static Player fromJson(JSONObject o) throws JSONException {
        Player p = new Player();
        p.name = o.getString("name");
        p.sect = o.getInt("sect");
        p.level = o.getInt("level");
        p.exp = o.getInt("exp");
        p.silver = o.getInt("silver");
        p.day = o.optInt("day", 1);
        p.maxHp = o.getInt("maxHp");
        p.hp = o.getInt("hp");
        p.maxMp = o.getInt("maxMp");
        p.mp = o.getInt("mp");
        p.baseAtk = o.getInt("atk");
        p.baseDef = o.getInt("def");
        p.baseAgi = o.getInt("agi");
        p.weapon = o.optInt("weapon", -1);
        p.armor = o.optInt("armor", -1);
        p.location = o.optInt("location", 0);
        p.lastTown = o.optInt("lastTown", 0);
        p.questStage = o.optInt("questStage", 0);
        p.questProgress = o.optInt("questProgress", 0);
        p.kills = o.optInt("kills", 0);
        p.finished = o.optBoolean("finished", false);
        JSONArray it = o.optJSONArray("items");
        if (it != null) {
            for (int i = 0; i < it.length() && i < p.items.length; i++) {
                p.items[i] = it.getInt(i);
            }
        }
        JSONArray sk = o.optJSONArray("skills");
        if (sk != null) {
            for (int i = 0; i < sk.length(); i++) {
                JSONObject so = sk.getJSONObject(i);
                int id = so.getInt("id");
                if (id < 0 || id >= GameData.SKILLS.length) {
                    continue;
                }
                p.skills.add(id);
                p.skillLv.put(id, so.optInt("lv", 1));
                p.skillExp.put(id, so.optInt("exp", 0));
            }
        }
        if (p.location < 0 || p.location >= GameData.LOCATIONS.length) {
            p.location = 0;
        }
        return p;
    }
}
