package com.wuxia.jianghu;

/** 静态游戏数据：门派、武功、物品、敌人、地点、主线任务。 */
public final class GameData {
    private GameData() {
    }

    // ---------------- 门派 ----------------
    public static final class Sect {
        public final String name;
        public final String shortName;
        public final String desc;
        public final int hp, mp, atk, def, agi;
        public final int hpGrow, mpGrow, atkGrow, defGrow, agiGrow;
        public final int startSkill;
        public final int color;

        Sect(String name, String shortName, String desc, int hp, int mp, int atk, int def, int agi,
             int hpGrow, int mpGrow, int atkGrow, int defGrow, int agiGrow, int startSkill, int color) {
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
            this.color = color;
        }
    }

    public static final Sect[] SECTS = {
            new Sect("少林派", "少林", "天下武功出少林。气血深厚，防御惊人，擅长硬功与护体。",
                    130, 40, 12, 8, 6, 26, 7, 4, 3, 1, 0, 0xFFB7862C),
            new Sect("武当派", "武当", "以柔克刚，四两拨千斤。攻守均衡，内力绵长。",
                    110, 60, 11, 6, 8, 20, 11, 4, 2, 2, 4, 0xFF2F5A8A),
            new Sect("峨眉派", "峨眉", "剑走轻灵，身法飘逸。出手迅捷，擅长连击与疗伤。",
                    95, 65, 11, 5, 11, 17, 11, 4, 2, 3, 8, 0xFF2F7563),
            new Sect("丐帮", "丐帮", "天下第一大帮。刚猛无俦，攻击最强，掌棒双绝。",
                    115, 45, 14, 5, 8, 22, 8, 5, 2, 2, 12, 0xFFA3291F),
    };

    // ---------------- 武功 ----------------
    public static final int SK_DMG = 0;
    public static final int SK_MULTI = 1;
    public static final int SK_HEAL = 2;
    public static final int SK_GUARD = 3;
    public static final int SK_STUN = 4;
    public static final int SK_DRAIN = 5;
    public static final int SK_DODGE = 6;

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

        Skill(int id, String name, int sect, int type, int mp, float power, int hits, int learnLv, int price, String desc) {
            this.id = id;
            this.name = name;
            this.sect = sect;
            this.type = type;
            this.mp = mp;
            this.power = power;
            this.hits = hits;
            this.learnLv = learnLv;
            this.price = price;
            this.desc = desc;
        }
    }

    public static final Skill[] SKILLS = {
            new Skill(0, "罗汉拳", 0, SK_DMG, 4, 1.5f, 1, 1, 0, "少林入门拳法，拳势沉稳。"),
            new Skill(1, "金钟罩", 0, SK_GUARD, 8, 0f, 1, 3, 80, "护体神功，三回合内防御大增。"),
            new Skill(2, "大力金刚掌", 0, SK_DMG, 14, 2.6f, 1, 6, 300, "掌力雄浑，开碑裂石。"),
            new Skill(3, "易筋经", 0, SK_HEAL, 18, 0.45f, 1, 9, 600, "少林至宝，运转周天，大量恢复气血。"),

            new Skill(4, "太极拳", 1, SK_DRAIN, 4, 1.4f, 1, 1, 0, "以柔克刚，伤敌同时回复自身气血。"),
            new Skill(5, "梯云纵", 1, SK_DODGE, 8, 0f, 1, 3, 80, "身轻如燕，三回合内闪避大增。"),
            new Skill(6, "太极剑", 1, SK_MULTI, 14, 1.1f, 3, 6, 300, "剑意圆转，连绵三击。"),
            new Skill(7, "纯阳无极功", 1, SK_HEAL, 16, 0.4f, 1, 9, 600, "武当内功心法，回复大量气血。"),

            new Skill(8, "峨眉剑法", 2, SK_MULTI, 5, 0.75f, 2, 1, 0, "轻灵剑法，一招两式。"),
            new Skill(9, "回春术", 2, SK_HEAL, 10, 0.3f, 1, 3, 80, "峨眉疗伤心法。"),
            new Skill(10, "金顶绵掌", 2, SK_STUN, 12, 1.8f, 1, 6, 300, "绵里藏针，有几率点中敌人穴道。"),
            new Skill(11, "灭剑式", 2, SK_DMG, 20, 3.2f, 1, 9, 600, "峨眉绝学，一剑灭敌。"),

            new Skill(12, "打狗棒法", 3, SK_STUN, 5, 1.3f, 1, 1, 0, "丐帮镇帮棒法，可缠住敌人令其无法出手。"),
            new Skill(13, "醉拳", 3, SK_DODGE, 8, 0f, 1, 3, 80, "似醉非醉，三回合内闪避大增。"),
            new Skill(14, "降龙十八掌", 3, SK_DMG, 16, 3.0f, 1, 6, 300, "天下至刚的掌法，亢龙有悔！"),
            new Skill(15, "天下无狗", 3, SK_MULTI, 20, 1.2f, 4, 9, 600, "打狗棒法最后一招，棒影漫天。"),

            new Skill(16, "凌波微步", -1, SK_DODGE, 10, 0f, 1, 3, 0, "逍遥派绝顶轻功，闪避极高。"),
            new Skill(17, "吸星大法", -1, SK_DRAIN, 15, 2.2f, 1, 6, 0, "吸人内力为己用，伤敌回血。"),
            new Skill(18, "独孤九剑", -1, SK_MULTI, 25, 1.3f, 5, 8, 0, "无招胜有招，破尽天下武功。"),
    };

    // ---------------- 物品 ----------------
    public static final int IT_HP = 0;
    public static final int IT_MP = 1;
    public static final int IT_ALL = 2;
    public static final int IT_WEAPON = 3;
    public static final int IT_ARMOR = 4;

    public static final class Item {
        public final int id;
        public final String name;
        public final int type;
        public final int value;
        public final int price;
        public final String desc;

        Item(int id, String name, int type, int value, int price, String desc) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.value = value;
            this.price = price;
            this.desc = desc;
        }

        public boolean consumable() {
            return type == IT_HP || type == IT_MP || type == IT_ALL;
        }
    }

    public static final Item[] ITEMS = {
            new Item(0, "金创药", IT_HP, 80, 20, "回复 80 点气血。"),
            new Item(1, "小还丹", IT_MP, 40, 25, "回复 40 点内力。"),
            new Item(2, "大还丹", IT_HP, 300, 80, "回复 300 点气血。"),
            new Item(3, "回气丹", IT_MP, 120, 90, "回复 120 点内力。"),
            new Item(4, "天山雪莲", IT_ALL, 9999, 400, "千年灵药，气血内力全满。"),
            new Item(5, "铁剑", IT_WEAPON, 6, 60, "寻常铁剑。攻击 +6"),
            new Item(6, "青钢剑", IT_WEAPON, 14, 260, "百炼青钢。攻击 +14"),
            new Item(7, "玄铁重剑", IT_WEAPON, 28, 900, "重剑无锋，大巧不工。攻击 +28"),
            new Item(8, "布甲", IT_ARMOR, 4, 50, "粗布棉甲。防御 +4"),
            new Item(9, "皮甲", IT_ARMOR, 10, 220, "牛皮软甲。防御 +10"),
            new Item(10, "软猬甲", IT_ARMOR, 20, 800, "刀枪不入的宝甲。防御 +20"),
            new Item(11, "倚天剑", IT_WEAPON, 45, 0, "倚天不出，谁与争锋。攻击 +45"),
            new Item(12, "草药", IT_HP, 40, 8, "山间采来的草药，回复 40 点气血。"),
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
        public final int shape; // 0 人形 1 兽 2 蛇 3 虫

        Enemy(int id, String name, int lv, int hp, int atk, int def, int agi, int exp, int silver,
              String skill, float skillPower, int drop, float dropRate, boolean boss, int color, int shape) {
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
        }
    }

    public static final Enemy[] ENEMIES = {
            new Enemy(0, "山贼", 1, 45, 10, 3, 6, 12, 8, "乱刀砍", 1.5f, 0, 0.2f, false, 0xFF5A4632, 0),
            new Enemy(1, "野狼", 1, 38, 12, 2, 10, 10, 2, "撕咬", 1.6f, 12, 0.25f, false, 0xFF6B6B6B, 1),
            new Enemy(2, "毒蛇", 2, 30, 14, 2, 14, 14, 3, "毒牙", 1.8f, 12, 0.3f, false, 0xFF3F6B2F, 2),
            new Enemy(3, "黑风喽啰", 3, 80, 18, 6, 10, 25, 15, "连环刀", 1.6f, 0, 0.25f, false, 0xFF2E2E2E, 0),
            new Enemy(4, "恶犬", 3, 70, 21, 5, 16, 24, 5, "猛扑", 1.7f, 12, 0.2f, false, 0xFF4A3A2A, 1),
            new Enemy(5, "山贼头目", 4, 120, 24, 9, 12, 40, 30, "开山斧", 1.9f, 1, 0.3f, false, 0xFF7A3B22, 0),
            new Enemy(6, "黑风寨主", 5, 340, 30, 12, 14, 150, 200, "黑风刀法", 2.2f, 6, 1f, true, 0xFF141414, 0),
            new Enemy(7, "玉蜂", 6, 90, 30, 8, 28, 45, 5, "蜂针", 1.8f, 1, 0.3f, false, 0xFFC9A227, 3),
            new Enemy(8, "古墓守卫", 7, 220, 36, 16, 16, 60, 25, "玉女剑", 1.8f, 2, 0.3f, false, 0xFF8A8A9A, 0),
            new Enemy(9, "盗墓贼", 6, 160, 33, 12, 20, 50, 60, "飞蝗石", 1.7f, 3, 0.25f, false, 0xFF5B4A3A, 0),
            new Enemy(10, "赤练魔头", 8, 720, 46, 20, 22, 320, 400, "赤练神掌", 2.3f, 11, 1f, true, 0xFF8B1A1A, 0),
            new Enemy(11, "魔教护法", 9, 360, 50, 24, 22, 90, 60, "吸血功", 2.0f, 2, 0.3f, false, 0xFF4B2A5A, 0),
            new Enemy(12, "雪豹", 8, 260, 48, 18, 32, 80, 10, "疾扑", 1.8f, 4, 0.1f, false, 0xFFBFBFBF, 1),
            new Enemy(13, "魔教长老", 10, 480, 56, 28, 24, 120, 90, "天魔掌", 2.0f, 3, 0.3f, false, 0xFF3A1A4A, 0),
            new Enemy(14, "天魔教主", 12, 2400, 78, 32, 36, 1000, 1000, "天魔解体大法", 2.4f, 4, 1f, true, 0xFF6B1E5A, 0),
    };

    // ---------------- 地点 ----------------
    public static final class Location {
        public final int id;
        public final String name;
        public final boolean town;
        public final String desc;
        public final float x, y; // 地图坐标（0..1）
        public final int minLv;
        public final int[] enemies;
        public final int boss;
        public final int[] shop;
        public final int hermitSkill;
        public final int[] links;
        public final int variant;

        Location(int id, String name, boolean town, String desc, float x, float y, int minLv,
                 int[] enemies, int boss, int[] shop, int hermitSkill, int[] links, int variant) {
            this.id = id;
            this.name = name;
            this.town = town;
            this.desc = desc;
            this.x = x;
            this.y = y;
            this.minLv = minLv;
            this.enemies = enemies;
            this.boss = boss;
            this.shop = shop;
            this.hermitSkill = hermitSkill;
            this.links = links;
            this.variant = variant;
        }
    }

    private static final int[] NONE = {};

    public static final Location[] LOCATIONS = {
            new Location(0, "临安城", true, "江南繁华之地，酒旗招展，人来人往。初入江湖者多在此落脚。",
                    0.25f, 0.82f, 1, NONE, -1, new int[]{0, 1, 12, 5, 8}, -1, new int[]{1, 3}, 0),
            new Location(1, "竹林小径", false, "翠竹万竿，风过如涛。近来常有山贼在此剪径。",
                    0.62f, 0.88f, 1, new int[]{0, 1, 2}, -1, NONE, 16, new int[]{0, 2}, 1),
            new Location(2, "黑风寨", false, "山势险恶，寨墙高耸，黑风寨的匪徒盘踞于此。",
                    0.78f, 0.66f, 3, new int[]{3, 4, 5}, 6, NONE, -1, new int[]{1, 3}, 2),
            new Location(3, "洛阳城", true, "九朝古都，牡丹盛开。城中药铺兵器铺货色齐全。",
                    0.32f, 0.52f, 3, NONE, -1, new int[]{0, 2, 3, 6, 9, 7, 10}, -1, new int[]{0, 2, 4, 5}, 3),
            new Location(4, "活死人墓", false, "终南山下古墓幽深，玉蜂嗡嗡，机关重重。",
                    0.74f, 0.42f, 6, new int[]{7, 8, 9}, 10, NONE, 17, new int[]{3, 6}, 4),
            new Location(5, "思过崖", false, "华山绝顶，崖高万仞，石壁上刻满剑痕。",
                    0.2f, 0.26f, 8, new int[]{11, 12}, -1, NONE, 18, new int[]{3, 6}, 5),
            new Location(6, "黑木崖", false, "天魔教总坛所在，黑云压顶，杀气腾腾。",
                    0.64f, 0.14f, 10, new int[]{11, 13}, 14, NONE, -1, new int[]{4, 5}, 6),
    };

    // ---------------- 主线任务 ----------------
    public static final class Quest {
        public final String title;
        public final String desc;
        public final int location;
        public final int target;
        public final int count;
        public final int exp;
        public final int silver;

        Quest(String title, String desc, int location, int target, int count, int exp, int silver) {
            this.title = title;
            this.desc = desc;
            this.location = location;
            this.target = target;
            this.count = count;
            this.exp = exp;
            this.silver = silver;
        }
    }

    public static final Quest[] QUESTS = {
            new Quest("竹林剪径", "临安城外竹林近来山贼横行，过往商旅苦不堪言。前往竹林小径，击败山贼三名。",
                    1, 0, 3, 40, 60),
            new Quest("荡平黑风寨", "山贼背后是黑风寨在撑腰。前往黑风寨，击败黑风寨主，为民除害。",
                    2, 6, 1, 150, 300),
            new Quest("古墓疑云", "洛阳城传言赤练魔头藏身活死人墓，修炼邪功，屡屡害人。前往古墓将其击败。",
                    4, 10, 1, 320, 600),
            new Quest("华山剑痕", "天魔教护法在思过崖寻找失传剑谱。击败魔教护法三名，阻止其阴谋。",
                    5, 11, 3, 400, 600),
            new Quest("决战黑木崖", "天魔教主欲一统江湖，正邪决战在即。登上黑木崖，击败天魔教主！",
                    6, 14, 1, 1000, 1000),
    };

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
    };
}
