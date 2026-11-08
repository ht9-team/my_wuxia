package com.wuxia.jianghu;

import java.util.ArrayList;

/**
 * 剧情引擎：章节目标、支线、线索与证物、节点存档、提示文本与事件分发。
 * 具体剧情文本分布在 StoryMain（序章 ~ 第四章）、StoryEnd（第五章 ~ 结局）、StorySide（支线与奇遇）。
 */
public final class Story {
    private Story() {
    }

    // ---------------- 目标类型 ----------------
    public static final int T_NONE = 0;
    public static final int T_KILL = 1;
    public static final int T_BOSS = 2;
    public static final int T_VISIT = 3;
    public static final int T_FINAL = 4;
    public static final int T_TALK = 5;

    // ---------------- 主线章节 ----------------
    public static final int S_P_TRIAL = 0;
    public static final int S_P_TRAITOR = 1;
    public static final int S_BAMBOO = 2;
    public static final int S_HEIFENG = 3;
    public static final int S_LANTERN = 4;
    public static final int S_LUOYANG = 5;
    public static final int S_ARENA = 6;
    public static final int S_TOMB = 7;
    public static final int S_A_DALI = 8;
    public static final int S_A_MIAO = 9;
    public static final int S_A_WULIANG = 10;
    public static final int S_B_HUASHAN = 11;
    public static final int S_B_TAIHU = 12;
    public static final int S_B_BANQUET = 13;
    public static final int S_C_OATH = 14;
    public static final int S_C_MIAO = 15;
    public static final int S_C_TAIHU = 16;
    public static final int S_XIANGYANG = 17;
    public static final int S_DESERT = 18;
    public static final int S_LOULAN = 19;
    public static final int S_SECT = 20;
    public static final int S_TIANSHAN = 21;
    public static final int S_FINAL = 22;
    public static final int S_SHADOW = 23;
    public static final int S_POST = 24;

    // ---------------- 结局 ----------------
    public static final int END_HERO = 0;
    public static final int END_LOVERS = 1;
    public static final int END_TYRANT = 2;
    public static final int END_HEIR = 3;
    public static final int END_LONE = 4;
    public static final int END_ENVOY = 5;
    public static final int END_DAWN = 6;
    public static final int END_DUSK = 7;
    public static final int END_SHADOW = 8;
    public static final int END_SECT = 9;
    public static final int END_RAIN = 10;
    public static final int END_SACRIFICE = 11;

    public static final String[] ENDING_NAMES = {
            "侠之大者", "神仙眷侣", "魔道至尊", "盟主传人", "孤剑天涯", "天魔右使",
            "影灭天明", "残阳如血", "影阁新主", "师门传人", "江湖夜雨", "玉石俱焚",
    };
    public static final String[] ENDING_HINTS = {
            "揭穿伪君子，留在江湖",
            "与心上人携手归隐",
            "入魔之后，取而代之",
            "听命盟主，诛杀魔头",
            "失去了最重要的人",
            "投身魔教，辅佐教主",
            "诛灭影阁，正邪同归于好",
            "诛灭影阁，却已物是人非",
            "成为新的影阁之主",
            "回归师门，继承衣钵",
            "孑然一身，浪迹天涯",
            "以身为饵，与归墟同沉",
    };
    public static final String[] ENDING_CONDITIONS = {
            "黑木崖：选择「揭穿慕容渊」（需证物 ≥ 2）并取胜；影阁线索不足 4 条；不与苏晚晴归隐。",
            "黑木崖：救下苏晚晴且好感 ≥ 70 时带她离开；或揭穿慕容渊后携她归隐（好感 ≥ 60）；或诛灭影阁后携她归隐。",
            "黑木崖：投身魔教（侠义 ≤ -20 或第三章走 C 线）并诛杀慕容渊，再与厉天行决战、取而代之。",
            "黑木崖：听从盟主号令诛杀天魔教主，且苏晚晴好感 < 40；影阁线索不足 4 条。",
            "黑木崖：听从盟主号令诛杀天魔教主，且苏晚晴好感 ≥ 40（她会为父挡下一掌）；影阁线索不足 4 条。",
            "黑木崖：投身魔教并诛杀慕容渊后，单膝跪下拜见教主。",
            "隐藏终章：影阁线索 ≥ 4 进入归墟，诛灭影阁主后选择执掌武林；需萧寒同心、苏晚晴获救在世、师门得保。",
            "隐藏终章：诛灭影阁主后选择执掌武林，但师门陷落、萧寒未同心或苏晚晴未能获救。",
            "隐藏终章：在归墟中接受影阁主的邀请（需侠义 ≤ 0 或曾入魔教）。",
            "隐藏终章：诛灭影阁主后回归师门（需师门得保且侠义 ≥ 30）。",
            "黑木崖上选择置身事外转身下山；或诛灭影阁主后选择孑然一身。",
            "隐藏终章：在归墟中选择以身为饵，与影阁同归于尽。",
    };

    // ---------------- 证物与影阁线索 ----------------
    public static final String[] EVIDENCE = {
            "keep_letter", "huashan_letter", "has_ledger", "has_testimony",
            "blade_confess", "taihu_orders", "banquet_secret", "witness_alive",
    };
    public static final String[] EVIDENCE_NAMES = {
            "黑风寨密信", "思过崖书信", "太湖账本", "老药王血书", "大漠狂刀遗言", "太湖密令", "寿宴密谈", "老药王亲证",
    };
    public static final String[] EVIDENCE_HINTS = {
            "第一章：击败黑风寨主后选择「收好密信」",
            "B 线：思过崖选择「暗中收起书信」",
            "B 线：太湖水寨取走账本",
            "A 线：苗疆救出老药王；C 线：苗疆选择放了老药王",
            "第四章：击败大漠狂刀后必定获得",
            "C 线：太湖截粮后选择「审问押运头目」",
            "B 线：盟主寿宴选择「夜探盟主密室」",
            "A 线：无量山选择「请老人家随我去襄阳作证」",
    };
    public static final String[] CLUES = {
            "clue_traitor", "clue_lantern", "clue_wuliang", "clue_banquet",
            "clue_oath", "clue_loulan", "clue_sect", "clue_letter",
    };
    public static final String[] CLUE_NAMES = {
            "叛徒遗言", "灯会刺客的影字令", "白无常遗言", "密室中的面具人",
            "厉天行的回忆", "黑无常遗言", "师门之战", "影阁密信",
    };
    public static final String[] CLUE_HINTS = {
            "序章：击败门派叛徒后必定获得",
            "第一章：临安灯会时选择「追击刺客」",
            "A 线：击败无量山白无常",
            "B 线：盟主寿宴时选择「夜探盟主密室」",
            "C 线：黑木崖盟誓时获得",
            "第四章：大漠之后选择「西出阳关」，击败楼兰黑无常",
            "第五章：赶回师门击退影阁；或从师门的噩耗中得知",
            "支线「影阁密信」：截杀无量山的影阁信使并破译密信",
    };
    public static final int SHADOW_NEED = 4;

    // ---------------- 主线章节定义 ----------------
    public static final class Stage {
        public final int id;
        public final String chapter;
        public final String title;
        public final String desc;
        public final int type;
        private final int loc;
        private final int target;
        public final int count;
        public final int exp;
        public final int silver;
        public final String done;
        private final boolean sectLoc;
        private final int[] sectTargets;

        Stage(int id, String chapter, String title, String desc, int type, int loc, int target, int count,
              int exp, int silver, String done) {
            this(id, chapter, title, desc, type, loc, target, count, exp, silver, done, false, null);
        }

        Stage(int id, String chapter, String title, String desc, int type, int loc, int target, int count,
              int exp, int silver, String done, boolean sectLoc, int[] sectTargets) {
            this.id = id;
            this.chapter = chapter;
            this.title = title;
            this.desc = desc;
            this.type = type;
            this.loc = loc;
            this.target = target;
            this.count = count;
            this.exp = exp;
            this.silver = silver;
            this.done = done;
            this.sectLoc = sectLoc;
            this.sectTargets = sectTargets;
        }

        public int loc(Player p) {
            return sectLoc ? Sects.home(p) : loc;
        }

        public int target(Player p) {
            return sectTargets != null ? sectTargets[p.sect] : target;
        }
    }

    private static final String CH_P = "序章 · 师门风波";
    private static final String CH_1 = "第一章 · 初出茅庐";
    private static final String CH_2 = "第二章 · 英雄大会";
    private static final String CH_3A = "第三章 · 歧路（A 追查真相）";
    private static final String CH_3B = "第三章 · 歧路（B 效忠盟主）";
    private static final String CH_3C = "第三章 · 歧路（C 投身魔教）";
    private static final String CH_4 = "第四章 · 大漠孤烟";
    private static final String CH_5 = "第五章 · 风雨欲来";

    public static final Stage[] STAGES = {
            new Stage(S_P_TRIAL, CH_P, "师门试炼", "师门出了乱子，山中有贼人作乱。击败三名作乱的贼人，查明真相。",
                    T_KILL, -1, -1, 3, 30, 30, "trial_done", true, Sects.MINION),
            new Stage(S_P_TRAITOR, CH_P, "清理门户", "幕后黑手竟是门中叛徒。探明师门后山的全部区域，擒下叛徒。",
                    T_BOSS, -1, -1, 1, 80, 60, "traitor", true, Sects.TRAITOR),
            new Stage(S_BAMBOO, CH_1, "竹林剪径", "临安城外的竹林近来山贼横行，过往商旅苦不堪言。前往竹林小径，击败三名山贼。",
                    T_KILL, 1, 0, 3, 40, 60, "su_meet"),
            new Stage(S_HEIFENG, CH_1, "荡平黑风寨", "山贼背后是黑风寨在撑腰。深入黑风寨，击败黑风寨主，为民除害。",
                    T_BOSS, 2, 6, 1, 150, 200, "heifeng"),
            new Stage(S_LANTERN, CH_1, "临安灯会", "上元佳节，临安城张灯结彩。回到临安城，看一看这江南的灯火。",
                    T_VISIT, 0, -1, 1, 60, 0, "lantern"),
            new Stage(S_LUOYANG, CH_2, "洛阳英雄宴", "武林盟主慕容渊在洛阳广发英雄帖，邀天下少年英雄赴会。前往洛阳城。",
                    T_VISIT, 3, -1, 1, 80, 0, "luoyang"),
            new Stage(S_ARENA, CH_2, "英雄擂台", "盟主在洛阳城南设下擂台，选拔讨伐赤练魔头的先锋。在洛阳城中点击剧情按钮登台。",
                    T_TALK, 3, -1, 1, 120, 100, "arena"),
            new Stage(S_TOMB, CH_2, "古墓疑云", "赤练魔头盘踞终南古墓，以活人练功。深入古墓，击败赤练魔头。此后将面临三条线路的抉择。",
                    T_BOSS, 4, 10, 1, 300, 400, "chilian"),
            new Stage(S_A_DALI, CH_3A, "南疆寻踪", "随苏晚晴南下大理，打探当年血案唯一活口的下落。前往大理城。",
                    T_VISIT, 7, -1, 1, 100, 0, "dali"),
            new Stage(S_A_MIAO, CH_3A, "五毒禁地", "当年的药师被五毒老怪囚禁在苗疆毒林深处。击败五毒老怪，救出证人。",
                    T_BOSS, 8, 19, 1, 450, 400, "witness"),
            new Stage(S_A_WULIANG, CH_3A, "无量玉壁", "影阁派出杀手追杀老药王。护送他前往大理以北的无量山，击败追兵首领白无常。",
                    T_BOSS, 19, 56, 1, 480, 400, "wuliang"),
            new Stage(S_B_HUASHAN, CH_3B, "华山剑痕", "魔教护法在思过崖搜寻失传剑谱。击败三名魔教护法，阻止其阴谋。",
                    T_KILL, 5, 11, 3, 400, 300, "huashan"),
            new Stage(S_B_TAIHU, CH_3B, "太湖疑案", "盟主密令：太湖水寨龙王勾结魔教，劫掠商船。剿灭水寨，擒拿龙王。",
                    T_BOSS, 9, 22, 1, 400, 400, "ledger"),
            new Stage(S_B_BANQUET, CH_3B, "盟主寿宴", "慕容渊五十大寿，在洛阳大宴群雄。回到洛阳城，点击剧情按钮赴宴。",
                    T_TALK, 3, -1, 1, 200, 200, "banquet"),
            new Stage(S_C_OATH, CH_3C, "黑木崖盟誓", "随苏晚晴前往天魔教总坛黑木崖（经终南古墓或思过崖），拜见教主厉天行。",
                    T_VISIT, 6, -1, 1, 150, 0, "oath"),
            new Stage(S_C_MIAO, CH_3C, "夺回天魔令", "叛徒五毒老怪盗走教中至宝天魔令，逃往苗疆毒林。击败他，夺回天魔令。",
                    T_BOSS, 8, 19, 1, 450, 300, "c_miao"),
            new Stage(S_C_TAIHU, CH_3C, "太湖截粮", "武林盟的粮船正经太湖北上。前往太湖水寨，击败三名押运的武林盟精英。",
                    T_KILL, 9, 62, 3, 420, 500, "c_taihu"),
            new Stage(S_XIANGYANG, CH_4, "襄阳烽火", "武林盟大军集结襄阳，正邪决战在即。前往襄阳城。",
                    T_VISIT, 10, -1, 1, 150, 0, "xiangyang"),
            new Stage(S_DESERT, CH_4, "大漠寻人", "萧寒孤身追查魔教，深入大漠孤城后音讯全无。击败盘踞孤城的大漠狂刀。",
                    T_BOSS, 12, 26, 1, 650, 600, "desert"),
            new Stage(S_LOULAN, CH_4, "楼兰追影", "大漠狂刀身上的地图指向西域楼兰古城。深入古城，击败影阁的黑无常。",
                    T_BOSS, 20, 57, 1, 760, 600, "loulan"),
            new Stage(S_SECT, CH_5, "师门告急", "影阁大举进犯师门！星夜赶回，探明师门后，击退来犯的影阁高手。",
                    T_BOSS, -1, -1, 1, 820, 400, "sect_saved", true, Sects.LIEUTENANT),
            new Stage(S_TIANSHAN, CH_5, "天山求药", "苏晚晴身中玄冥寒毒，唯天山雪莲可解。击败守护雪莲的冰魄老人。",
                    T_BOSS, 11, 29, 1, 800, 300, "lotus"),
            new Stage(S_FINAL, "终章 · 黑木崖之巅", "决战黑木崖", "正邪大军会战黑木崖。一路杀上崖顶，在天魔殿前做出你的抉择。",
                    T_FINAL, 6, -1, 1, 0, 0, "climax"),
            new Stage(S_SHADOW, "隐章 · 归墟", "影阁归墟", "影阁之主遁入黑木崖下的归墟。从黑木崖进入归墟，一路杀到最深处。",
                    T_FINAL, 18, -1, 1, 0, 0, "shadow_final"),
            new Stage(S_POST, "后记 · 江湖传说", "江湖传说", "尘埃落定，江湖依旧。传闻东海蓬莱仙岛上隐居着一位剑仙，武功深不可测……",
                    T_NONE, 13, -1, 0, 0, 0, null),
    };

    // ---------------- 支线定义 ----------------
    public static final class Step {
        public final int type;
        private final int loc;
        private final int target;
        public final int count;
        public final String desc;
        public final String done;
        private final boolean sectLoc;
        private final int[] sectTargets;

        Step(int type, int loc, int target, int count, String desc, String done) {
            this(type, loc, target, count, desc, done, false, null);
        }

        Step(int type, int loc, int target, int count, String desc, String done, boolean sectLoc, int[] sectTargets) {
            this.type = type;
            this.loc = loc;
            this.target = target;
            this.count = count;
            this.desc = desc;
            this.done = done;
            this.sectLoc = sectLoc;
            this.sectTargets = sectTargets;
        }

        public int loc(Player p) {
            return sectLoc ? Sects.home(p) : loc;
        }

        public int target(Player p) {
            return sectTargets != null ? sectTargets[p.sect] : target;
        }
    }

    public static final class Side {
        public final int id;
        public final String name;
        public final String intro;
        public final String unlock;
        public final Step[] steps;

        Side(int id, String name, String intro, String unlock, Step[] steps) {
            this.id = id;
            this.name = name;
            this.intro = intro;
            this.unlock = unlock;
            this.steps = steps;
        }
    }

    public static final Side[] SIDES = {
            new Side(0, "师门秘传", "回师门接受出师试炼，赢取镇派神兵。", "英雄大会之后，等级 ≥ 6", new Step[]{
                    new Step(T_TALK, -1, -1, 1, "回师门拜见师父（点击金色剧情按钮）", "sq0_a", true, null),
                    new Step(T_BOSS, -1, -1, 1, "在师门挑战首领，闯过出师试炼", "sq0_b", true, Sects.GUARDIAN),
            }),
            new Side(1, "萧家庄旧址", "陪萧寒重返萧家庄旧址，寻找萧家遗物。", "萧寒得知自己的身世之后", new Step[]{
                    new Step(T_TALK, 3, -1, 1, "与萧寒同往洛阳城外的萧家庄旧址", "sq1_a"),
                    new Step(T_KILL, 2, 54, 3, "追击潜伏在黑风寨一带的影阁杀手", "sq1_b"),
                    new Step(T_TALK, 3, -1, 1, "回到萧家庄，打开地窖", "sq1_c"),
            }),
            new Side(2, "晚晴的心事", "苏晚晴似乎有什么心事，约你在大理相见。", "苏晚晴在世且好感 ≥ 40，第四章之后", new Step[]{
                    new Step(T_TALK, 7, -1, 1, "前往大理城赴苏晚晴之约", "sq2_a"),
                    new Step(T_BOSS, 8, 70, 1, "击败盘踞苗疆的天魔教叛徒赤发鬼", "sq2_b"),
            }),
            new Side(3, "镖银失踪", "临安威远镖局的镖银在太湖被劫。", "第一章临安灯会之后", new Step[]{
                    new Step(T_TALK, 0, -1, 1, "前往临安威远镖局", "sq3_a"),
                    new Step(T_KILL, 9, 20, 4, "在太湖水寨击败四名水寨喽啰，夺回镖银", "sq3_b"),
            }),
            new Side(4, "铸剑", "襄阳老铸剑师想以天山陨铁为你铸一件神兵。", "第四章之后，等级 ≥ 9", new Step[]{
                    new Step(T_TALK, 10, -1, 1, "拜访襄阳城中的老铸剑师", "sq4_a"),
                    new Step(T_KILL, 11, 27, 3, "在天山雪岭击败三头雪狼，取回陨铁", "sq4_b"),
                    new Step(T_TALK, 10, -1, 1, "将陨铁交给老铸剑师", "sq4_c"),
            }),
            new Side(5, "影阁密信", "截杀影阁信使，破译他们的密信。", "获得至少一条影阁线索，古墓之后", new Step[]{
                    new Step(T_KILL, 19, 54, 2, "在无量山截杀两名影阁信使", "sq5_a"),
                    new Step(T_TALK, 10, -1, 1, "回襄阳请人破译密信", "sq5_b"),
            }),
    };

    static boolean sideUnlocked(Player p, int id) {
        switch (id) {
            case 0:
                return p.stage >= S_ARENA && p.level >= 6;
            case 1:
                return p.has("xiao_knows") && !p.has("xiao_dead");
            case 2:
                return suAlive(p) && p.affSu >= 40 && p.stage >= S_XIANGYANG;
            case 3:
                return p.stage > S_LANTERN;
            case 4:
                return p.level >= 9 && p.stage >= S_XIANGYANG;
            case 5:
                return clues(p) >= 1 && p.stage > S_TOMB;
            default:
                return false;
        }
    }

    // ---------------- 运行时目标 ----------------
    public static final class Goal {
        public final int side;
        public final String title;
        public final String desc;
        public final int type;
        public final int loc;
        public final int target;
        public final int count;
        public final String done;

        Goal(int side, String title, String desc, int type, int loc, int target, int count, String done) {
            this.side = side;
            this.title = title;
            this.desc = desc;
            this.type = type;
            this.loc = loc;
            this.target = target;
            this.count = count;
            this.done = done;
        }
    }

    public static Stage current(Player p) {
        int s = Math.max(0, Math.min(STAGES.length - 1, p.stage));
        return STAGES[s];
    }

    public static ArrayList<Goal> goals(Player p) {
        ArrayList<Goal> list = new ArrayList<>();
        Stage s = current(p);
        if (s.type != T_NONE) {
            list.add(new Goal(-1, s.title, s.desc, s.type, s.loc(p), s.target(p), s.count, s.done));
        }
        for (Side q : SIDES) {
            int st = p.side[q.id];
            if (st >= 0 && st < q.steps.length) {
                Step k = q.steps[st];
                list.add(new Goal(q.id, q.name, k.desc, k.type, k.loc(p), k.target(p), k.count, k.done));
            }
        }
        return list;
    }

    public static int progress(Player p, Goal goal) {
        return goal.side < 0 ? p.progress : p.sideProg[goal.side];
    }

    public static String goalText(Player p, Goal goal) {
        String where = goal.loc >= 0 ? GameData.LOCATIONS[goal.loc].name : "";
        switch (goal.type) {
            case T_KILL:
                return "地点：" + where + "   击败" + GameData.ENEMIES[goal.target].name + " " + progress(p, goal) + "/" + goal.count;
            case T_BOSS:
                return "地点：" + where + "   探明全境后挑战首领「" + GameData.ENEMIES[goal.target].name + "」";
            case T_VISIT:
                return "前往：" + where;
            case T_TALK:
                return "地点：" + where + "   点击金色「剧情」按钮";
            case T_FINAL:
                return "地点：" + where + "   探索进度 " + p.explore[goal.loc] + "/" + GameData.LOCATIONS[goal.loc].floors;
            default:
                return "东海蓬莱仙岛（临安城以东）上，有隐世高手等候挑战";
        }
    }

    public static String objective(Player p) {
        Stage s = current(p);
        if (s.type == T_NONE) {
            return goalText(p, new Goal(-1, s.title, s.desc, T_NONE, -1, -1, 0, null));
        }
        return goalText(p, new Goal(-1, s.title, s.desc, s.type, s.loc(p), s.target(p), s.count, s.done));
    }

    /** 主线的下一步提示。 */
    public static String hint(Player p) {
        Stage s = current(p);
        switch (s.id) {
            case S_P_TRAITOR:
                return "提示：每战胜一场深入一层，探明全部 3 层后「挑战首领」会亮起。";
            case S_TOMB:
                return "提示：古墓之后将三选一——A 追查真相 / B 效忠盟主 / C 投身魔教（需侠义 ≤ -15 或苏晚晴好感 ≥ 30）。";
            case S_DESERT:
                return "提示：大漠之后可选择「西出阳关」，在楼兰古城获得一条影阁线索。";
            case S_LOULAN:
                return "提示：第五章将面临师门与苏晚晴的两难；随身带一株「天山雪莲」（襄阳有售）可两全。";
            case S_FINAL:
                return clues(p) >= SHADOW_NEED ? "提示：你已集齐 " + clues(p) + " 条影阁线索，黑木崖取胜后将开启隐藏终章。"
                        : "提示：影阁线索 " + clues(p) + "/" + SHADOW_NEED + "，集齐后黑木崖取胜将开启隐藏终章。";
            case S_SHADOW:
                return "提示：萧寒同心、苏晚晴在世、师门得保，三者俱全方能见到真结局「影灭天明」。";
            default:
                break;
        }
        switch (s.type) {
            case T_KILL:
                return "提示：在该地「四处探索」，目标敌人出没得更频繁。";
            case T_BOSS:
                return "提示：探明全境（每胜一场深入一层）后，点击「挑战首领」。";
            case T_VISIT:
                return "提示：打开江湖地图，金色地点即为目的地；城镇之间可乘驿站马车。";
            case T_TALK:
                return "提示：到达后，地点页上方会出现金色剧情按钮。";
            case T_FINAL:
                return "提示：探索至顶层后，点击右上方的剧情按钮。";
            default:
                return "提示：可在「江湖志 → 剧情指引」查看全部结局的达成条件。";
        }
    }

    // ---------------- 统计 ----------------

    public static int evidence(Player p) {
        int n = 0;
        for (String f : EVIDENCE) {
            if (p.has(f)) {
                n++;
            }
        }
        return n;
    }

    public static int clues(Player p) {
        int n = 0;
        for (String f : CLUES) {
            if (p.has(f)) {
                n++;
            }
        }
        return n;
    }

    public static boolean shadowReady(Player p) {
        return clues(p) >= SHADOW_NEED;
    }

    public static boolean xiaoFriend(Player p) {
        return p.has("xiao_knows") && p.affXiao >= 30 && !p.has("xiao_dead");
    }

    public static boolean suAlive(Player p) {
        return p.has("met_su") && !p.has("su_dead");
    }

    public static boolean suSaved(Player p) {
        return p.has("saved_su") && suAlive(p);
    }

    public static boolean allyXiao(Player p, int enemyId) {
        return (enemyId == 31 || Sects.isFinalBoss(enemyId)) && xiaoFriend(p);
    }

    public static boolean allySu(Player p, int enemyId) {
        return (enemyId == 31 || enemyId == 32 || Sects.isFinalBoss(enemyId)) && suSaved(p) && p.affSu >= 40;
    }

    public static int sidesDone(Player p) {
        int n = 0;
        for (Side q : SIDES) {
            if (p.side[q.id] >= q.steps.length) {
                n++;
            }
        }
        return n;
    }

    // ---------------- 进度推进 ----------------

    public static boolean finalAvailable(Player p) {
        Stage s = current(p);
        int loc = s.loc(p);
        return s.type == T_FINAL && p.location == loc && p.explore[loc] >= GameData.LOCATIONS[loc].floors;
    }

    /** 记录击杀；若有目标因此完成，返回该目标。 */
    public static Goal onKill(Game g, int enemyId) {
        Player p = g.player;
        Goal done = null;
        for (Goal goal : goals(p)) {
            if ((goal.type == T_KILL || goal.type == T_BOSS) && goal.target == enemyId && goal.loc == p.location) {
                int n;
                if (goal.side < 0) {
                    p.progress++;
                    n = p.progress;
                } else {
                    p.sideProg[goal.side]++;
                    n = p.sideProg[goal.side];
                }
                if (n >= goal.count && done == null) {
                    done = goal;
                }
            }
        }
        return done;
    }

    public static void completeGoal(Game g, Goal goal) {
        if (goal.side < 0) {
            completeStage(g);
        } else {
            completeSide(g, goal.side);
        }
    }

    public static void completeStage(Game g) {
        Player p = g.player;
        Stage s = current(p);
        p.progress = 0;
        p.silver += s.silver;
        for (String m : p.gainExp(s.exp)) {
            g.toast(m);
        }
        g.toast("完成【" + s.title + "】" + (s.exp > 0 ? " 修为+" + s.exp : "") + (s.silver > 0 ? "  银两+" + s.silver : ""));
        p.log("完成：" + s.title);
        g.save();
        if (s.done != null) {
            play(g, s.done);
        } else {
            toLocation(g);
        }
    }

    static void completeSide(Game g, int id) {
        Player p = g.player;
        Side q = SIDES[id];
        int st = p.side[id];
        if (st < 0 || st >= q.steps.length) {
            toLocation(g);
            return;
        }
        Step k = q.steps[st];
        p.sideProg[id] = 0;
        p.side[id] = st + 1;
        if (p.side[id] >= q.steps.length) {
            g.toast("支线完成【" + q.name + "】");
            p.log("完成支线：" + q.name);
        }
        g.save();
        play(g, k.done);
    }

    private static void checkUnlocks(Game g) {
        Player p = g.player;
        for (Side q : SIDES) {
            if (p.side[q.id] < 0 && sideUnlocked(p, q.id)) {
                p.side[q.id] = 0;
                p.sideProg[q.id] = 0;
                g.toast("新支线【" + q.name + "】");
                p.log("开启支线：" + q.name);
            }
        }
    }

    /** 到达地点或结束剧情时检查：前往类目标、已满足但未结算的击杀类目标。触发剧情时返回 true。 */
    public static boolean onArrive(Game g) {
        Player p = g.player;
        for (Goal goal : goals(p)) {
            if (goal.type == T_VISIT && goal.loc == p.location) {
                completeGoal(g, goal);
                return true;
            }
            if ((goal.type == T_KILL || goal.type == T_BOSS) && progress(p, goal) >= goal.count) {
                completeGoal(g, goal);
                return true;
            }
        }
        return false;
    }

    public static void toLocation(Game g) {
        checkUnlocks(g);
        if (!onArrive(g)) {
            g.setScene(new LocationScene(g));
        }
    }

    /** 当前地点可触发的「剧情」类目标。 */
    public static ArrayList<Goal> talkGoals(Player p) {
        ArrayList<Goal> list = new ArrayList<>();
        for (Goal goal : goals(p)) {
            if (goal.type == T_TALK && goal.loc == p.location) {
                list.add(goal);
            }
        }
        return list;
    }

    /** 该地点当前应挑战的首领：剧情目标优先，否则为地点首领。 */
    public static int bossFor(Player p, GameData.Location l) {
        for (Goal goal : goals(p)) {
            if (goal.type == T_BOSS && goal.loc == l.id) {
                return goal.target;
            }
        }
        return l.boss;
    }

    /** 该地点的击杀目标（用于提高其出没几率），没有则返回 -1。 */
    public static int spawnFor(Player p, GameData.Location l) {
        for (Goal goal : goals(p)) {
            if (goal.type == T_KILL && goal.loc == l.id) {
                return goal.target;
            }
        }
        return -1;
    }

    static void goStage(Game g, int id) {
        g.player.stage = id;
        g.player.progress = 0;
        g.save();
    }

    // ---------------- 事件分发 ----------------

    static void show(Game g, Ev ev) {
        g.setScene(new DialogScene(g, ev));
    }

    static void fight(Game g, int enemy, Runnable win, Runnable lose) {
        g.setScene(new BattleScene(g, enemy, BattleScene.KIND_STORY, win, lose));
    }

    /** 播放剧情事件；带抉择的事件会自动存为节点存档，以便日后读档改走其他分支。 */
    public static void play(Game g, String id) {
        Ev ev = build(g, id);
        if (ev == null) {
            toLocation(g);
            return;
        }
        if (!ev.choices.isEmpty() && g.player != null) {
            ev.node = true;
            g.recordNode(id, ev.title);
        }
        show(g, ev);
    }

    private static Ev build(Game g, String id) {
        Ev ev = StoryMain.build(g, id);
        if (ev == null) {
            ev = StoryEnd.build(g, id);
        }
        if (ev == null) {
            ev = StorySide.build(g, id);
        }
        return ev;
    }

    public static void hermit(Game g, int skill) {
        StorySide.hermit(g, skill);
    }

    public static Ev randomEvent(Game g) {
        return StorySide.randomEvent(g);
    }

    public static Ev tavern(Game g) {
        return StorySide.tavern(g);
    }

    static void ending(Game g, int id) {
        StoryEnd.ending(g, id);
    }
}
