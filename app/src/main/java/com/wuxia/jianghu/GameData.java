package com.wuxia.jianghu;

/**
 * 静态游戏数据：门派、武功、物品（含兵器体系）、敌人、地点。
 * 所有数组的下标必须与 id 一致，并且只能追加，以保证旧存档兼容。
 */
public final class GameData {
    private GameData() {
    }

    // ---------------- 兵器体系 ----------------
    public static final int W_SWORD = 0;
    public static final int W_BLADE = 1;
    public static final int W_STAFF = 2;
    public static final int W_FIST = 3;
    public static final String[] WEAPON_TYPES = {"剑", "刀", "棍", "拳"};

    public static final int SP_NONE = 0;
    public static final int SP_DRAIN = 1;
    public static final int SP_PIERCE = 2;
    public static final int SP_DOUBLE = 3;
    public static final int SP_FROST = 4;
    public static final int SP_POISON = 5;
    public static final String[] SPECIAL_NAMES = {"", "吸血", "破甲", "连击", "寒冰", "剧毒"};
    public static final String[] SPECIAL_DESC = {
            "",
            "命中时回复伤害 15% 的气血",
            "无视敌人 40% 的防御",
            "普通攻击有 25% 几率追加一击",
            "命中时有几率冻结敌人一回合",
            "命中时有 25% 几率令敌人中毒三回合",
    };

    // ---------------- 门派 ----------------
    public static final class Sect {
        public final String name;
        public final String shortName;
        public final String desc;
        public final int hp, mp, atk, def, agi;
        public final int hpGrow, mpGrow, atkGrow, defGrow, agiGrow;
        public final int startSkill;
        public final int startWeapon;
        public final int color;

        Sect(String name, String shortName, String desc, int hp, int mp, int atk, int def, int agi,
             int hpGrow, int mpGrow, int atkGrow, int defGrow, int agiGrow, int startSkill, int startWeapon, int color) {
            this.name = name;
            this.shortName = shortName;
            this.desc = desc;
            this.hp = hp;
            this.mp = mp;
            this.atk = atk;
            this.def = def;
            this.agi = agi;
            this.hpGrow = hpGrow;
            this.mpGrow = mpGrow;
            this.atkGrow = atkGrow;
            this.defGrow = defGrow;
            this.agiGrow = agiGrow;
            this.startSkill = startSkill;
            this.startWeapon = startWeapon;
            this.color = color;
        }
    }

    public static final Sect[] SECTS = {
            new Sect("少林派", "少林", "天下武功出少林。气血深厚，防御惊人，拳掌功夫冠绝天下。",
                    130, 40, 12, 8, 6, 26, 7, 4, 3, 1, 0, 19, 0xFFB7862C),
            new Sect("武当派", "武当", "以柔克刚，四两拨千斤。攻守均衡，内力绵长，拳剑双修。",
                    110, 60, 11, 6, 8, 20, 11, 4, 2, 2, 4, 5, 0xFF2F5A8A),
            new Sect("峨眉派", "峨眉", "剑走轻灵，身法飘逸。出手迅捷，擅长连击与疗伤。",
                    95, 65, 11, 5, 11, 17, 11, 4, 2, 3, 8, 5, 0xFF2F7563),
            new Sect("丐帮", "丐帮", "天下第一大帮。刚猛无俦，攻击最强，掌棒双绝。",
                    115, 45, 14, 5, 8, 22, 8, 5, 2, 2, 12, 16, 0xFFA3291F),
    };

    // ---------------- 武功 ----------------
    public static final int SK_DMG = 0;
    public static final int SK_MULTI = 1;
    public static final int SK_HEAL = 2;
    public static final int SK_GUARD = 3;
    public static final int SK_STUN = 4;
    public static final int SK_DRAIN = 5;
    public static final int SK_DODGE = 6;
    public static final int SK_POISON = 7;

    public static final class Skill {
        public final int id;
        public final String name;
        public final int sect; // -1 为奇遇武学
        public final int type;
        public final int mp;
        public final float power;
        public final int hits;
        public final int learnLv;
        public final int price;
        public final String desc;
        public final int wtype; // -1 表示内功 / 身法，不依赖兵器

        Skill(int id, String name, int sect, int type, int mp, float power, int hits, int learnLv, int price,
              int wtype, String desc) {
            this.id = id;
            this.name = name;
            this.sect = sect;
            this.type = type;
            this.mp = mp;
            this.power = power;
            this.hits = hits;
            this.learnLv = learnLv;
            this.price = price;
            this.wtype = wtype;
            this.desc = desc;
        }
    }

    public static final Skill[] SKILLS = {
            new Skill(0, "罗汉拳", 0, SK_DMG, 4, 1.5f, 1, 1, 0, W_FIST, "少林入门拳法，拳势沉稳。"),
            new Skill(1, "金钟罩", 0, SK_GUARD, 8, 0f, 1, 3, 80, -1, "护体神功，三回合内防御大增。"),
            new Skill(2, "大力金刚掌", 0, SK_DMG, 14, 2.6f, 1, 6, 300, W_FIST, "掌力雄浑，开碑裂石。"),
            new Skill(3, "易筋经", 0, SK_HEAL, 18, 0.45f, 1, 9, 600, -1, "少林至宝，运转周天，大量恢复气血。"),

            new Skill(4, "太极拳", 1, SK_DRAIN, 4, 1.4f, 1, 1, 0, W_FIST, "以柔克刚，伤敌同时回复自身气血。"),
            new Skill(5, "梯云纵", 1, SK_DODGE, 8, 0f, 1, 3, 80, -1, "身轻如燕，三回合内闪避大增。"),
            new Skill(6, "太极剑", 1, SK_MULTI, 14, 1.1f, 3, 6, 300, W_SWORD, "剑意圆转，连绵三击。"),
            new Skill(7, "纯阳无极功", 1, SK_HEAL, 16, 0.4f, 1, 9, 600, -1, "武当内功心法，回复大量气血。"),

            new Skill(8, "峨眉剑法", 2, SK_MULTI, 5, 0.75f, 2, 1, 0, W_SWORD, "轻灵剑法，一招两式。"),
            new Skill(9, "回春术", 2, SK_HEAL, 10, 0.3f, 1, 3, 80, -1, "峨眉疗伤心法。"),
            new Skill(10, "金顶绵掌", 2, SK_STUN, 12, 1.8f, 1, 6, 300, W_FIST, "绵里藏针，有几率点中敌人穴道。"),
            new Skill(11, "灭剑式", 2, SK_DMG, 20, 3.2f, 1, 9, 600, W_SWORD, "峨眉绝学，一剑灭敌。"),

            new Skill(12, "打狗棒法", 3, SK_STUN, 5, 1.3f, 1, 1, 0, W_STAFF, "丐帮镇帮棒法，可缠住敌人令其无法出手。"),
            new Skill(13, "醉拳", 3, SK_DODGE, 8, 0f, 1, 3, 80, -1, "似醉非醉，三回合内闪避大增。"),
            new Skill(14, "降龙十八掌", 3, SK_DMG, 16, 3.0f, 1, 6, 300, W_FIST, "天下至刚的掌法。"),
            new Skill(15, "天下无狗", 3, SK_MULTI, 20, 1.2f, 4, 9, 600, W_STAFF, "打狗棒法最后一招，棒影漫天。"),

            new Skill(16, "凌波微步", -1, SK_DODGE, 10, 0f, 1, 3, 0, -1, "绝顶轻功，闪避极高。"),
            new Skill(17, "吸星大法", -1, SK_DRAIN, 15, 2.2f, 1, 6, 0, -1, "吸人内力为己用，伤敌回血。"),
            new Skill(18, "独孤九剑", -1, SK_MULTI, 25, 1.3f, 5, 8, 0, W_SWORD, "无招胜有招，破尽天下武功。"),

            new Skill(19, "般若金刚掌", 0, SK_DMG, 26, 4.0f, 1, 12, 1200, W_FIST, "少林七十二绝技之首，佛门金刚之力。"),
            new Skill(20, "太极两仪剑", 1, SK_MULTI, 26, 1.3f, 4, 12, 1200, W_SWORD, "两仪化四象，剑光如环。"),
            new Skill(21, "佛光普照", 2, SK_DRAIN, 24, 3.2f, 1, 12, 1200, -1, "峨眉镇派绝学，佛光所至，伤敌愈己。"),
            new Skill(22, "亢龙有悔", 3, SK_DMG, 26, 4.2f, 1, 12, 1200, W_FIST, "掌力留有余地，却是至刚至猛的一击。"),
            new Skill(23, "千蛛万毒手", -1, SK_POISON, 14, 1.8f, 1, 8, 0, W_FIST, "苗疆奇功，以毒攻毒，令敌人剧毒缠身。"),
            new Skill(24, "天山折梅手", -1, SK_MULTI, 20, 1.0f, 6, 10, 0, W_FIST, "天下武学皆可化入此手，连绵六击。"),
            new Skill(25, "狂风刀法", -1, SK_MULTI, 18, 1.15f, 4, 9, 0, W_BLADE, "大漠风沙中悟出的刀法，刀势如狂风。"),
            new Skill(26, "碧海潮生曲", -1, SK_STUN, 28, 2.6f, 1, 14, 0, -1, "以内力催动箫声，如潮水般令敌人心神大乱。"),
    };

    // ---------------- 物品 ----------------
    public static final int IT_HP = 0;
    public static final int IT_MP = 1;
    public static final int IT_ALL = 2;
    public static final int IT_WEAPON = 3;
    public static final int IT_ARMOR = 4;
    public static final int IT_ACC = 5;
    public static final int IT_MAT = 6;
    public static final int IT_CURE = 7;

    public static final class Item {
        public final int id;
        public final String name;
        public final int type;
        public final int value;
        public final int price;
        public final String desc;
        public int wtype = -1;
        public int atk, def, hp, mp, agi, crit;
        public int special = SP_NONE;

        Item(int id, String name, int type, int value, int price, String desc) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.value = value;
            this.price = price;
            this.desc = desc;
        }

        public boolean consumable() {
            return type == IT_HP || type == IT_MP || type == IT_ALL || type == IT_CURE;
        }

        public boolean equipable() {
            return type == IT_WEAPON || type == IT_ARMOR || type == IT_ACC;
        }

        /** 简短的属性说明，例如「剑 攻+14 暴+3 [吸血]」。 */
        public String statText() {
            StringBuilder sb = new StringBuilder();
            if (type == IT_WEAPON) {
                sb.append(WEAPON_TYPES[wtype]).append(" 攻+").append(value);
            } else if (type == IT_ARMOR) {
                sb.append("防+").append(value);
            }
            if (atk != 0) {
                sb.append(" 攻+").append(atk);
            }
            if (def != 0) {
                sb.append(" 防+").append(def);
            }
            if (hp != 0) {
                sb.append(" 血+").append(hp);
            }
            if (mp != 0) {
                sb.append(" 内+").append(mp);
            }
            if (agi != 0) {
                sb.append(" 身").append(agi > 0 ? "+" : "").append(agi);
            }
            if (crit != 0) {
                sb.append(" 暴+").append(crit).append("%");
            }
            if (special != SP_NONE) {
                sb.append(" [").append(SPECIAL_NAMES[special]).append("]");
            }
            if (sb.length() == 0) {
                return desc;
            }
            return sb.toString().trim();
        }
    }

    private static Item med(int id, String name, int type, int value, int price, String desc) {
        return new Item(id, name, type, value, price, desc);
    }

    private static Item weapon(int id, String name, int wtype, int atk, int crit, int agi, int special, int price, String desc) {
        Item it = new Item(id, name, IT_WEAPON, atk, price, desc);
        it.wtype = wtype;
        it.crit = crit;
        it.agi = agi;
        it.special = special;
        return it;
    }

    private static Item armor(int id, String name, int def, int hp, int agi, int price, String desc) {
        Item it = new Item(id, name, IT_ARMOR, def, price, desc);
        it.hp = hp;
        it.agi = agi;
        return it;
    }

    private static Item acc(int id, String name, int atk, int def, int hp, int mp, int agi, int crit, int price, String desc) {
        Item it = new Item(id, name, IT_ACC, 0, price, desc);
        it.atk = atk;
        it.def = def;
        it.hp = hp;
        it.mp = mp;
        it.agi = agi;
        it.crit = crit;
        return it;
    }

    public static final Item[] ITEMS = {
            med(0, "金创药", IT_HP, 80, 20, "回复 80 点气血。"),
            med(1, "小还丹", IT_MP, 40, 25, "回复 40 点内力。"),
            med(2, "大还丹", IT_HP, 300, 80, "回复 300 点气血。"),
            med(3, "回气丹", IT_MP, 120, 90, "回复 120 点内力。"),
            med(4, "天山雪莲", IT_ALL, 9999, 400, "千年灵药，气血内力全满。"),
            weapon(5, "铁剑", W_SWORD, 6, 0, 0, SP_NONE, 60, "寻常铁剑，江湖人人佩得。"),
            weapon(6, "青钢剑", W_SWORD, 14, 3, 0, SP_NONE, 260, "百炼青钢，吹毛断发。"),
            weapon(7, "玄铁重剑", W_SWORD, 28, 0, -2, SP_PIERCE, 900, "重剑无锋，大巧不工。"),
            armor(8, "布甲", 4, 0, 0, 50, "粗布棉甲。"),
            armor(9, "皮甲", 10, 20, 0, 220, "牛皮软甲。"),
            armor(10, "软猬甲", 20, 40, 0, 800, "刀枪不入的宝甲。"),
            weapon(11, "倚天剑", W_SWORD, 45, 8, 0, SP_PIERCE, 0, "倚天不出，谁与争锋。"),
            med(12, "草药", IT_HP, 40, 8, "山间采来的草药，回复 40 点气血。"),
            weapon(13, "朴刀", W_BLADE, 8, 4, 0, SP_NONE, 70, "厚背朴刀，大开大合。"),
            weapon(14, "雁翎刀", W_BLADE, 18, 7, 0, SP_NONE, 300, "刀身狭长如雁翎，出刀极快。"),
            weapon(15, "断岳刀", W_BLADE, 34, 10, 0, SP_DOUBLE, 1000, "一刀可断山岳，刀势连绵。"),
            weapon(16, "齐眉棍", W_STAFF, 7, 0, 2, SP_NONE, 60, "白蜡杆齐眉棍，轻巧趁手。"),
            weapon(17, "青竹杖", W_STAFF, 16, 2, 4, SP_DOUBLE, 280, "百年青竹所制，韧而不折。"),
            weapon(18, "绿玉杖", W_STAFF, 36, 4, 6, SP_FROST, 0, "通体碧绿，触手生寒。"),
            weapon(19, "铁指虎", W_FIST, 5, 5, 0, SP_NONE, 50, "套在指上的铁环，拳拳到肉。"),
            weapon(20, "金丝手套", W_FIST, 15, 8, 0, SP_POISON, 300, "金丝织就，掌心藏毒。"),
            weapon(21, "龙鳞拳套", W_FIST, 32, 10, 0, SP_DRAIN, 1000, "以龙鳞缀成，掌力所及，血气倒流。"),
            weapon(22, "碧血剑", W_SWORD, 38, 6, 0, SP_DRAIN, 0, "剑身隐有血光，饮血而鸣。"),
            weapon(23, "屠魔刀", W_BLADE, 52, 12, 0, SP_PIERCE, 0, "大漠狂刀的佩刀，刀下亡魂无数。"),
            weapon(24, "冰魄剑", W_SWORD, 48, 8, 2, SP_FROST, 0, "万年寒冰铸就，剑气凝霜。"),
            weapon(25, "龙吟剑", W_SWORD, 72, 15, 4, SP_DOUBLE, 0, "东海剑仙的佩剑，出鞘时如龙吟九天。"),
            armor(26, "锁子甲", 16, 30, 0, 500, "铁环相扣，坚固耐用。"),
            armor(27, "金丝软甲", 28, 60, 2, 1600, "金丝织就，轻若无物。"),
            armor(28, "天蚕宝衣", 40, 120, 4, 0, "苏晚晴母亲的遗物，天蚕丝织成，水火不侵。"),
            acc(29, "羊脂玉佩", 0, 0, 0, 0, 4, 3, 300, "温润如脂，佩之心静。"),
            acc(30, "护身符", 0, 2, 60, 0, 0, 0, 200, "寺中求来的平安符。"),
            acc(31, "紫玉簪", 0, 0, 0, 40, 3, 0, 0, "苏晚晴所赠，玉色温润，隐有幽香。"),
            acc(32, "金刚护腕", 0, 6, 40, 0, 0, 0, 600, "精钢护腕，可挡刀剑。"),
            acc(33, "天魔令", 12, 0, 0, 0, 0, 5, 0, "天魔教的黑铁令牌，持之者教众莫敢不从。"),
            acc(34, "青锋令", 8, 4, 0, 0, 0, 0, 0, "武林盟主亲赐的信物，刻有青锋剑印。"),
            med(35, "玄铁", IT_MAT, 0, 150, "铁匠铺强化高阶兵甲所需的材料。"),
            med(36, "九花玉露丸", IT_HP, 600, 200, "以九种奇花炼成，回复 600 点气血。"),
            med(37, "解毒丹", IT_CURE, 100, 30, "解除中毒，并回复 100 点气血。"),
    };

    // ---------------- 敌人 ----------------
    public static final class Enemy {
        public final int id;
        public final String name;
        public final int lv, hp, atk, def, agi, exp, silver;
        public final String skill;
        public final float skillPower;
        public final int drop;
        public final float dropRate;
        public final boolean boss;
        public final int color;
        public final int shape; // 0 人形 1 兽 2 蛇 3 虫 4 蝎
        public final boolean poison;

        Enemy(int id, String name, int lv, int hp, int atk, int def, int agi, int exp, int silver,
              String skill, float skillPower, int drop, float dropRate, boolean boss, int color, int shape, boolean poison) {
            this.id = id;
            this.name = name;
            this.lv = lv;
            this.hp = hp;
            this.atk = atk;
            this.def = def;
            this.agi = agi;
            this.exp = exp;
            this.silver = silver;
            this.skill = skill;
            this.skillPower = skillPower;
            this.drop = drop;
            this.dropRate = dropRate;
            this.boss = boss;
            this.color = color;
            this.shape = shape;
            this.poison = poison;
        }
    }

    public static final Enemy[] ENEMIES = {
            new Enemy(0, "山贼", 1, 45, 11, 3, 6, 12, 8, "乱刀砍", 1.5f, 0, 0.2f, false, 0xFF5A4632, 0, false),
            new Enemy(1, "野狼", 1, 38, 12, 2, 10, 10, 2, "撕咬", 1.6f, 12, 0.25f, false, 0xFF6B6B6B, 1, false),
            new Enemy(2, "毒蛇", 2, 32, 13, 2, 14, 14, 3, "毒牙", 1.6f, 12, 0.3f, false, 0xFF3F6B2F, 2, true),
            new Enemy(3, "黑风喽啰", 3, 85, 19, 6, 10, 25, 15, "连环刀", 1.6f, 0, 0.25f, false, 0xFF2E2E2E, 0, false),
            new Enemy(4, "恶犬", 3, 72, 21, 5, 16, 24, 5, "猛扑", 1.7f, 12, 0.2f, false, 0xFF4A3A2A, 1, false),
            new Enemy(5, "山贼头目", 4, 125, 25, 9, 12, 40, 30, "开山斧", 1.9f, 1, 0.3f, false, 0xFF7A3B22, 0, false),
            new Enemy(6, "黑风寨主", 5, 460, 34, 12, 14, 160, 200, "黑风刀法", 2.2f, 14, 1f, true, 0xFF141414, 0, false),
            new Enemy(7, "玉蜂", 6, 110, 34, 9, 28, 48, 5, "蜂针", 1.8f, 1, 0.3f, false, 0xFFC9A227, 3, true),
            new Enemy(8, "古墓守卫", 7, 240, 42, 16, 16, 62, 25, "玉女剑", 1.8f, 2, 0.3f, false, 0xFF8A8A9A, 0, false),
            new Enemy(9, "盗墓贼", 6, 170, 38, 12, 20, 52, 60, "飞蝗石", 1.7f, 3, 0.25f, false, 0xFF5B4A3A, 0, false),
            new Enemy(10, "赤练魔头", 8, 900, 52, 20, 22, 340, 400, "赤练神掌", 2.3f, 11, 1f, true, 0xFF8B1A1A, 0, true),
            new Enemy(11, "魔教护法", 9, 330, 56, 22, 22, 95, 60, "吸血功", 2.0f, 2, 0.3f, false, 0xFF4B2A5A, 0, false),
            new Enemy(12, "雪豹", 9, 300, 58, 18, 34, 88, 10, "疾扑", 1.8f, 4, 0.08f, false, 0xFFBFBFBF, 1, false),
            new Enemy(13, "魔教长老", 12, 480, 76, 30, 26, 150, 90, "天魔掌", 2.0f, 36, 0.3f, false, 0xFF3A1A4A, 0, false),
            new Enemy(14, "天魔教主", 14, 2600, 92, 36, 36, 1200, 1000, "天魔解体大法", 2.4f, 36, 1f, true, 0xFF6B1E5A, 0, false),
            new Enemy(15, "武林盟弟子", 3, 90, 20, 7, 12, 28, 20, "两仪剑阵", 1.7f, 0, 0.3f, false, 0xFF4A6A8A, 0, false),
            new Enemy(16, "鬼影护法", 10, 1100, 62, 24, 34, 420, 380, "鬼影迷踪", 2.3f, 27, 1f, true, 0xFF2A2A3A, 0, false),
            new Enemy(17, "毒蝎", 7, 160, 44, 18, 18, 58, 6, "尾针", 1.8f, 37, 0.3f, false, 0xFF6B3A1A, 4, true),
            new Enemy(18, "苗疆蛊师", 8, 230, 50, 14, 22, 72, 40, "金蚕蛊", 2.0f, 37, 0.3f, false, 0xFF3A5A3A, 0, true),
            new Enemy(19, "五毒老怪", 9, 1050, 58, 20, 26, 450, 420, "五毒神掌", 2.3f, 22, 1f, true, 0xFF2F4A1F, 0, true),
            new Enemy(20, "水寨喽啰", 5, 130, 30, 9, 14, 36, 22, "分水刺", 1.6f, 0, 0.3f, false, 0xFF2A4A5A, 0, false),
            new Enemy(21, "水鬼", 6, 150, 36, 10, 26, 46, 18, "溺水拖拽", 1.8f, 1, 0.25f, false, 0xFF1F3A3A, 0, false),
            new Enemy(22, "水寨龙王", 7, 760, 46, 16, 20, 300, 360, "翻江倒海", 2.2f, 18, 1f, true, 0xFF1A3A5A, 0, false),
            new Enemy(23, "马贼", 10, 330, 60, 22, 26, 105, 55, "回马枪", 1.9f, 2, 0.3f, false, 0xFF6A4A2A, 0, false),
            new Enemy(24, "沙蝎", 10, 300, 62, 26, 22, 100, 10, "毒尾", 1.9f, 37, 0.3f, false, 0xFFA07A3A, 4, true),
            new Enemy(25, "西域刀客", 11, 400, 68, 24, 28, 125, 70, "弯刀斩", 2.0f, 3, 0.3f, false, 0xFF8A5A2A, 0, false),
            new Enemy(26, "大漠狂刀", 11, 1500, 74, 28, 30, 620, 600, "狂沙刀阵", 2.4f, 23, 1f, true, 0xFF5A3A1A, 0, false),
            new Enemy(27, "雪狼", 11, 360, 70, 22, 34, 115, 12, "寒牙", 1.9f, 4, 0.06f, false, 0xFFD8D8E0, 1, false),
            new Enemy(28, "天山弃徒", 12, 450, 74, 28, 28, 135, 80, "天山六阳掌", 2.0f, 36, 0.2f, false, 0xFF5A7A9A, 0, false),
            new Enemy(29, "冰魄老人", 12, 1800, 80, 32, 32, 800, 700, "冰魄寒光", 2.4f, 24, 1f, true, 0xFFB8C8D8, 0, false),
            new Enemy(30, "魔教精英", 13, 520, 82, 32, 30, 160, 90, "血刃", 2.0f, 36, 0.25f, false, 0xFF4A1A2A, 0, false),
            new Enemy(31, "慕容渊", 15, 3000, 98, 40, 40, 1600, 1500, "慕容剑诀", 2.5f, -1, 0f, true, 0xFF3F6B5A, 0, false),
            new Enemy(32, "萧寒", 13, 1600, 80, 30, 38, 600, 0, "流云剑法", 2.2f, -1, 0f, true, 0xFFD8DEE6, 0, false),
            new Enemy(33, "蓬莱弟子", 15, 600, 92, 36, 36, 200, 100, "碧涛掌", 2.0f, 36, 0.3f, false, 0xFF2F7A8A, 0, false),
            new Enemy(34, "东海鲨", 15, 650, 98, 32, 30, 190, 20, "血口獠牙", 2.0f, 35, 0.3f, false, 0xFF4A5A6A, 1, false),
            new Enemy(35, "剑傀", 16, 720, 102, 44, 28, 220, 60, "万剑归宗", 2.1f, 35, 0.3f, false, 0xFF7A7A6A, 0, false),
            new Enemy(36, "东海剑仙", 18, 5200, 125, 50, 50, 3000, 3000, "天外飞仙", 2.6f, 25, 1f, true, 0xFFE8E8F0, 0, false),
            new Enemy(37, "竹林大盗", 2, 160, 15, 4, 10, 55, 60, "竹影刀", 1.8f, 13, 1f, true, 0xFF3A5A2A, 0, false),
    };

    // ---------------- 地点 ----------------
    public static final class Location {
        public final int id;
        public final String name;
        public final boolean town;
        public final String desc;
        public final float x, y; // 地图坐标（0..1）
        public final int minLv;
        public final int floors;
        public final int[] enemies;
        public final int boss;
        public final boolean storyBoss;
        public final int[] shop;
        public final int hermitSkill;
        public final int[] links;
        public final int variant;

        Location(int id, String name, boolean town, String desc, float x, float y, int minLv, int floors,
                 int[] enemies, int boss, boolean storyBoss, int[] shop, int hermitSkill, int[] links, int variant) {
            this.id = id;
            this.name = name;
            this.town = town;
            this.desc = desc;
            this.x = x;
            this.y = y;
            this.minLv = minLv;
            this.floors = floors;
            this.enemies = enemies;
            this.boss = boss;
            this.storyBoss = storyBoss;
            this.shop = shop;
            this.hermitSkill = hermitSkill;
            this.links = links;
            this.variant = variant;
        }
    }

    private static final int[] NONE = {};

    public static final Location[] LOCATIONS = {
            new Location(0, "临安城", true, "江南繁华之地，酒旗招展，人来人往。初入江湖者多在此落脚。",
                    0.72f, 0.82f, 1, 0, NONE, -1, false, new int[]{0, 1, 12, 37, 5, 13, 16, 19, 8, 29}, -1,
                    new int[]{1, 3, 9, 13}, 0),
            new Location(1, "竹林小径", false, "翠竹万竿，风过如涛。近来常有山贼在此剪径。",
                    0.92f, 0.66f, 1, 3, new int[]{0, 1, 2}, 37, false, NONE, 16, new int[]{0, 2}, 1),
            new Location(2, "黑风寨", false, "山势险恶，寨墙高耸，黑风寨的匪徒盘踞于此。",
                    0.62f, 0.55f, 3, 4, new int[]{3, 4, 5}, 6, false, NONE, -1, new int[]{1, 3}, 2),
            new Location(3, "洛阳城", true, "九朝古都，牡丹盛开。武林盟在此设有分舵，城中药铺兵器铺货色齐全。",
                    0.45f, 0.36f, 3, 0, NONE, -1, false, new int[]{0, 1, 2, 3, 37, 6, 14, 17, 20, 9, 30}, -1,
                    new int[]{0, 2, 4, 5, 10}, 3),
            new Location(4, "终南古墓", false, "终南山下古墓幽深，玉蜂嗡嗡，机关重重。",
                    0.82f, 0.24f, 6, 5, new int[]{7, 8, 9}, 10, false, NONE, 17, new int[]{3, 6}, 4),
            new Location(5, "思过崖", false, "华山绝顶，崖高万仞，石壁上刻满了前人留下的剑痕。",
                    0.45f, 0.17f, 8, 5, new int[]{11, 12}, 16, false, NONE, 18, new int[]{3, 6}, 5),
            new Location(6, "黑木崖", false, "天魔教总坛所在，黑云压顶，杀气腾腾。",
                    0.62f, 0.04f, 13, 6, new int[]{13, 30, 11}, 14, true, NONE, -1, new int[]{4, 5, 11}, 6),
            new Location(7, "大理城", true, "苍山洱海之间的南疆古城，风物秀美，药材与奇珍异宝云集。",
                    0.12f, 0.88f, 6, 0, NONE, -1, false, new int[]{0, 2, 3, 37, 36, 20, 26, 29, 30, 32}, -1,
                    new int[]{8, 10}, 7),
            new Location(8, "苗疆毒林", false, "瘴气弥漫的原始密林，毒虫遍地，苗疆蛊师出没其间。",
                    0.06f, 0.70f, 8, 5, new int[]{17, 18, 2}, 19, false, NONE, 23, new int[]{7, 10}, 8),
            new Location(9, "太湖水寨", false, "烟波浩渺的太湖之上，水寨连营，水匪劫掠过往商船。",
                    0.44f, 0.94f, 5, 4, new int[]{20, 21}, 22, false, NONE, -1, new int[]{0, 10}, 9),
            new Location(10, "襄阳城", true, "铁打的襄阳，北望中原的雄城。武林盟大军在此集结。",
                    0.22f, 0.52f, 8, 0, NONE, -1, false, new int[]{2, 3, 36, 37, 4, 7, 15, 21, 26, 27, 32}, -1,
                    new int[]{3, 7, 8, 9, 12}, 10),
            new Location(11, "天山雪岭", false, "终年积雪的天山之巅，寒风如刀，传说有雪莲生于绝壁。",
                    0.10f, 0.04f, 11, 5, new int[]{27, 28, 12}, 29, false, NONE, 24, new int[]{6, 12}, 11),
            new Location(12, "大漠孤城", false, "玉门关外的废弃古城，黄沙漫天，马贼与西域刀客横行。",
                    0.10f, 0.22f, 10, 5, new int[]{23, 24, 25}, 26, false, NONE, 25, new int[]{10, 11}, 12),
            new Location(13, "蓬莱仙岛", false, "东海深处的仙岛，云雾缭绕，传说岛上隐居着一位剑仙。",
                    0.90f, 0.42f, 15, 6, new int[]{33, 34, 35}, 36, false, NONE, 26, new int[]{0}, 13),
    };

    public static boolean linked(int a, int b) {
        for (int k : LOCATIONS[a].links) {
            if (k == b) {
                return true;
            }
        }
        return false;
    }

    public static final String[] NAMES_FAMILY = {"萧", "林", "沈", "楚", "陆", "叶", "苏", "云", "慕容", "上官", "独孤", "令狐", "燕", "顾", "谢"};
    public static final String[] NAMES_GIVEN = {"逸尘", "长风", "无忌", "青衣", "若雪", "惊鸿", "寒山", "子墨", "听雨", "孤城", "破军", "南星", "飞白", "照影", "千秋"};

    public static final String[] SCENERY = {
            "你在溪边捧水而饮，山风拂面，心中一片澄明。",
            "远处传来一阵箫声，如泣如诉，不知是何方高人。",
            "路旁石碑上刻着前人的剑招，你看了半晌，若有所悟。",
            "一位樵夫挑柴而过，哼着不知名的小曲。",
            "林中惊起一群飞鸟，你屏息凝神，却什么也没发现。",
            "你在古树下打坐片刻，内息运转更加顺畅。",
            "地上有一摊血迹，似乎不久前这里有过一场恶斗。",
            "你在一处废弃的驿站歇脚，墙上歪歪扭扭写着：「某年某月，到此一游」。",
            "夕阳西下，你望着天边的晚霞，忽然想起了师门后山的那片云海。",
            "一队镖车从你身边驶过，镖师们警惕地打量着你。",
    };
}
