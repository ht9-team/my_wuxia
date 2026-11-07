package com.wuxia.jianghu;

/**
 * 主线剧情：章节目标、分支事件、江湖奇遇与多结局。
 *
 * 线路概览：
 * 第一章 竹林 → 黑风寨；第二章 洛阳英雄会 → 终南古墓；
 * 第三章 分为「追查真相」（大理 → 苗疆）与「效忠盟主」（思过崖 → 太湖）两条线；
 * 第四章 襄阳 → 大漠；第五章 天山（可选）；终章 黑木崖，根据侠义值、好感与证据走向六种结局。
 */
public final class Story {
    private Story() {
    }

    public static final int T_NONE = 0;
    public static final int T_KILL = 1;
    public static final int T_BOSS = 2;
    public static final int T_VISIT = 3;
    public static final int T_FINAL = 4;

    public static final int S_BAMBOO = 0;
    public static final int S_HEIFENG = 1;
    public static final int S_LUOYANG = 2;
    public static final int S_TOMB = 3;
    public static final int S_DALI = 4;
    public static final int S_MIAO = 5;
    public static final int S_HUASHAN = 6;
    public static final int S_TAIHU = 7;
    public static final int S_XIANGYANG = 8;
    public static final int S_DESERT = 9;
    public static final int S_TIANSHAN = 10;
    public static final int S_FINAL = 11;
    public static final int S_POST = 12;

    public static final int END_HERO = 0;
    public static final int END_LOVERS = 1;
    public static final int END_TYRANT = 2;
    public static final int END_HEIR = 3;
    public static final int END_LONE = 4;
    public static final int END_ENVOY = 5;
    public static final String[] ENDING_NAMES = {"侠之大者", "神仙眷侣", "魔道至尊", "盟主传人", "孤剑天涯", "天魔右使"};
    public static final String[] ENDING_HINTS = {
            "揭穿伪君子，留在江湖",
            "与心上人携手归隐",
            "入魔之后，取而代之",
            "听命盟主，诛杀魔头",
            "失去了最重要的人",
            "投身魔教，辅佐教主",
    };

    public static final String[] EVIDENCE = {"keep_letter", "huashan_letter", "has_ledger", "has_testimony", "blade_confess"};
    public static final String[] EVIDENCE_NAMES = {"黑风寨密信", "思过崖书信", "太湖账本", "老药王血书", "大漠狂刀遗言"};

    public static final class Stage {
        public final int id;
        public final String chapter;
        public final String title;
        public final String desc;
        public final int type;
        public final int loc;
        public final int target;
        public final int count;
        public final int exp;
        public final int silver;
        public final String done;

        Stage(int id, String chapter, String title, String desc, int type, int loc, int target, int count,
              int exp, int silver, String done) {
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
        }
    }

    public static final Stage[] STAGES = {
            new Stage(S_BAMBOO, "第一章 · 初出茅庐", "竹林剪径",
                    "临安城外的竹林近来山贼横行，过往商旅苦不堪言。前往竹林小径，击败三名山贼。",
                    T_KILL, 1, 0, 3, 40, 60, "su_meet"),
            new Stage(S_HEIFENG, "第一章 · 初出茅庐", "荡平黑风寨",
                    "山贼背后是黑风寨在撑腰。深入黑风寨，击败黑风寨主，为民除害。",
                    T_BOSS, 2, 6, 1, 150, 200, "heifeng"),
            new Stage(S_LUOYANG, "第二章 · 英雄大会", "洛阳英雄宴",
                    "武林盟主慕容渊在洛阳广发英雄帖，邀天下少年英雄赴会。前往洛阳城。",
                    T_VISIT, 3, -1, 1, 60, 0, "luoyang"),
            new Stage(S_TOMB, "第二章 · 英雄大会", "古墓疑云",
                    "赤练魔头盘踞终南古墓，以活人练功。深入古墓，击败赤练魔头。",
                    T_BOSS, 4, 10, 1, 300, 400, "chilian"),
            new Stage(S_DALI, "第三章 · 迷雾重重", "南疆寻踪",
                    "随苏晚晴南下大理，打探当年血案唯一活口的下落。前往大理城。",
                    T_VISIT, 7, -1, 1, 100, 0, "dali"),
            new Stage(S_MIAO, "第三章 · 迷雾重重", "五毒禁地",
                    "当年的药师被五毒老怪囚禁在苗疆毒林深处。击败五毒老怪，救出证人。",
                    T_BOSS, 8, 19, 1, 450, 400, "witness"),
            new Stage(S_HUASHAN, "第三章 · 迷雾重重", "华山剑痕",
                    "魔教护法在思过崖搜寻失传剑谱。击败三名魔教护法，阻止其阴谋。",
                    T_KILL, 5, 11, 3, 400, 300, "huashan"),
            new Stage(S_TAIHU, "第三章 · 迷雾重重", "太湖疑案",
                    "盟主密令：太湖水寨龙王勾结魔教，劫掠商船。剿灭水寨，擒拿龙王。",
                    T_BOSS, 9, 22, 1, 400, 400, "ledger"),
            new Stage(S_XIANGYANG, "第四章 · 大漠孤烟", "襄阳烽火",
                    "武林盟大军集结襄阳，正邪决战在即。前往襄阳城。",
                    T_VISIT, 10, -1, 1, 150, 0, "xiangyang"),
            new Stage(S_DESERT, "第四章 · 大漠孤烟", "大漠寻人",
                    "萧寒孤身追查魔教，深入大漠孤城后音讯全无。击败盘踞孤城的大漠狂刀。",
                    T_BOSS, 12, 26, 1, 650, 600, "desert"),
            new Stage(S_TIANSHAN, "第五章 · 天山雪莲", "天山求药",
                    "苏晚晴身中玄冥寒毒，唯天山雪莲可解。击败守护雪莲的冰魄老人。",
                    T_BOSS, 11, 29, 1, 800, 300, "lotus"),
            new Stage(S_FINAL, "终章 · 黑木崖之巅", "决战黑木崖",
                    "正邪大军会战黑木崖。一路杀上崖顶，在天魔殿前做出你的抉择。",
                    T_FINAL, 6, -1, 1, 0, 0, "climax"),
            new Stage(S_POST, "后记 · 江湖传说", "江湖传说",
                    "尘埃落定，江湖依旧。传闻东海蓬莱仙岛上隐居着一位剑仙，武功深不可测……",
                    T_NONE, 13, -1, 0, 0, 0, null),
    };

    // ---------------- 进度查询 ----------------

    public static Stage current(Player p) {
        int s = Math.max(0, Math.min(STAGES.length - 1, p.stage));
        return STAGES[s];
    }

    public static String objective(Player p) {
        Stage s = current(p);
        switch (s.type) {
            case T_KILL:
                return "地点：" + GameData.LOCATIONS[s.loc].name + "   击败" + GameData.ENEMIES[s.target].name
                        + " " + p.progress + "/" + s.count;
            case T_BOSS:
                return "地点：" + GameData.LOCATIONS[s.loc].name + "   探索至深处，击败" + GameData.ENEMIES[s.target].name;
            case T_VISIT:
                return "前往：" + GameData.LOCATIONS[s.loc].name;
            case T_FINAL:
                return "地点：黑木崖   登顶进度 " + p.explore[s.loc] + "/" + GameData.LOCATIONS[s.loc].floors;
            default:
                return "东海蓬莱仙岛（临安城以东）上，有隐世高手等候挑战";
        }
    }

    public static int evidence(Player p) {
        int n = 0;
        for (String f : EVIDENCE) {
            if (p.has(f)) {
                n++;
            }
        }
        return n;
    }

    public static boolean finalAvailable(Player p) {
        Stage s = current(p);
        return s.type == T_FINAL && p.location == s.loc && p.explore[s.loc] >= GameData.LOCATIONS[s.loc].floors;
    }

    /** 记录击杀；若当前章节目标因此完成，返回 true。 */
    public static boolean onKill(Game g, int enemyId) {
        Player p = g.player;
        Stage s = current(p);
        if ((s.type == T_KILL || s.type == T_BOSS) && s.target == enemyId && p.location == s.loc) {
            p.progress++;
            return p.progress >= s.count;
        }
        return false;
    }

    public static void completeStage(Game g) {
        Player p = g.player;
        Stage s = current(p);
        p.progress = 0;
        p.silver += s.silver;
        for (String m : p.gainExp(s.exp)) {
            g.toast(m);
        }
        g.toast("完成【" + s.title + "】 修为+" + s.exp + (s.silver > 0 ? "  银两+" + s.silver : ""));
        p.log("完成：" + s.title);
        g.save();
        if (s.done != null) {
            play(g, s.done);
        } else {
            toLocation(g);
        }
    }

    /** 到达地点时检查「前往」类目标；触发剧情时返回 true。 */
    public static boolean onArrive(Game g) {
        Player p = g.player;
        Stage s = current(p);
        if (s.type == T_VISIT && p.location == s.loc) {
            completeStage(g);
            return true;
        }
        return false;
    }

    public static void toLocation(Game g) {
        if (!onArrive(g)) {
            g.setScene(new LocationScene(g));
        }
    }

    static void goStage(Game g, int id) {
        g.player.stage = id;
        g.player.progress = 0;
        g.save();
    }

    private static void show(Game g, Ev ev) {
        g.setScene(new DialogScene(g, ev));
    }

    private static void fight(Game g, int enemy, Runnable win, Runnable lose) {
        g.setScene(new BattleScene(g, enemy, BattleScene.KIND_STORY, win, lose));
    }

    public static void play(Game g, String id) {
        Ev ev = build(g, id);
        if (ev == null) {
            toLocation(g);
            return;
        }
        show(g, ev);
    }

    private static Ev build(Game g, String id) {
        switch (id) {
            case "intro":
                return intro(g);
            case "su_meet":
                return suMeet(g);
            case "heifeng":
                return heifeng(g);
            case "luoyang":
                return luoyang(g);
            case "chilian":
                return chilian(g);
            case "dali":
                return dali(g);
            case "witness":
                return witness(g);
            case "huashan":
                return huashan(g);
            case "ledger":
                return ledger(g);
            case "xiangyang":
                return xiangyang(g);
            case "desert":
                return desert(g);
            case "lotus":
                return lotus(g);
            case "climax":
                return climax(g);
            default:
                return null;
        }
    }

    // ================= 序章 =================

    private static Ev intro(Game g) {
        final Player p = g.player;
        String sect = GameData.SECTS[p.sect].name;
        return new Ev("序章 · 下山").bg(5).bgm(Audio.BGM_TITLE)
                .narr(sect + "后山，晨钟三响，云海翻涌。你在师父门前跪了整整一夜。")
                .say("师父", p.name + "，你入门十年，根基已成。如今江湖风波又起，天魔教死灰复燃，武林盟主慕容渊广发英雄帖……")
                .say("师父", "为师要你下山历练。记住：侠义二字，不在名门正派的招牌上，而在你的本心。")
                .me("弟子谨记师父教诲。")
                .say("师父", "去吧。临安城外的竹林近来有山贼出没，正好拿他们练练手。若遇不平之事……你自己拿主意。")
                .narr("你背起行囊，最后望了一眼山门。从今日起，江湖路远，再无人替你遮风挡雨。")
                .then(() -> {
                    p.log("拜别师父，下山历练");
                    goStage(g, S_BAMBOO);
                    g.goLocation(0);
                });
    }

    // ================= 第一章 =================

    private static Ev suMeet(Game g) {
        return new Ev("第一章 · 竹林相逢")
                .narr("你收拾了最后一个山贼，正欲离去，竹林深处忽然传来一阵兵刃相交之声。")
                .narr("一名紫衣少女被六七个白衣剑客围在中央。她左臂带伤，手中短剑却仍舞得滴水不漏。")
                .say("武林盟弟子", "妖女！你天魔教作恶多端，今日还想逃？乖乖跟我们回总坛，听候盟主发落！")
                .say("苏晚晴", "呸！你们武林盟的人杀了我的老仆，倒还有脸说别人作恶？")
                .narr("少女瞥见了你，眼中闪过一丝倔强，却并未开口求救。")
                .choice("拔剑相助紫衣少女", "以多欺少，岂是侠义所为", true,
                        () -> fight(g, 15, () -> show(g, suHelp(g, true)), () -> show(g, suHelp(g, false))))
                .choice("袖手旁观", "江湖恩怨，与我无关", true, () -> show(g, suWatch(g)))
                .choice("协助武林盟擒拿妖女", "正邪不两立", true, () -> show(g, suCapture(g)));
    }

    private static Ev suHelp(Game g, boolean won) {
        final Player p = g.player;
        Ev ev = new Ev("第一章 · 竹林相逢");
        if (won) {
            ev.narr("白衣剑客们见势不妙，丢下一句「你给我等着」，狼狈退走。");
        } else {
            ev.narr("你寡不敌众，眼看就要落败，紫衣少女忽然撒出一把紫烟，拉着你冲出了重围。");
        }
        ev.say("苏晚晴", "……多谢。不过你可知道，帮了我，便是得罪了整个武林盟。")
                .me("我只知道，以多欺少，不是侠义所为。")
                .say("苏晚晴", "侠义？呵……你这人倒有趣。我叫苏晚晴。这支紫玉簪送你，日后有缘，凭它来寻我。")
                .narr("话音未落，她身形一晃，已消失在竹海深处，只余一缕淡淡的幽香。（获得：紫玉簪）")
                .then(() -> {
                    p.affSu += 25;
                    p.justice += 3;
                    p.items[31]++;
                    p.set("met_su");
                    p.log("竹林中出手救下紫衣少女苏晚晴");
                    goStage(g, S_HEIFENG);
                    toLocation(g);
                });
        return ev;
    }

    private static Ev suWatch(Game g) {
        final Player p = g.player;
        return new Ev("第一章 · 竹林相逢")
                .narr("你抱臂立于一旁。少女咬牙苦战，终于寻得一丝破绽，掷出一枚烟丸，借着烟雾脱身而去。")
                .narr("临走时，她冷冷地望了你一眼，仿佛在说：原来名门子弟，也不过如此。")
                .then(() -> {
                    p.set("met_su");
                    p.affSu -= 5;
                    p.log("竹林中冷眼旁观了一场围攻");
                    goStage(g, S_HEIFENG);
                    toLocation(g);
                });
    }

    private static Ev suCapture(Game g) {
        final Player p = g.player;
        return new Ev("第一章 · 竹林相逢")
                .narr("你挺剑而上，与白衣剑客们合围。少女以一敌八，终于力竭，短剑脱手飞出。")
                .narr("就在剑尖将及她咽喉之际，一道黑影从天而降，卷起少女便走，只留下一阵阴冷的长笑。")
                .say("武林盟弟子", "可惜让那妖女跑了！多谢少侠援手，这三十两银子是一点心意，在下定将少侠义举禀报盟主。")
                .then(() -> {
                    p.set("met_su");
                    p.set("alliance_favor");
                    p.affSu -= 30;
                    p.silver += 30;
                    p.log("协助武林盟围捕魔教妖女");
                    goStage(g, S_HEIFENG);
                    toLocation(g);
                });
    }

    private static Ev heifeng(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            goStage(g, S_LUOYANG);
            show(g, new Ev("第一章 · 英雄帖")
                    .narr("数日后，一名身着青衫的武林盟信使找到了你，双手奉上一张烫金请柬。")
                    .say("武林盟信使", "盟主慕容渊将于洛阳召开英雄大会，共商讨伐魔教大计。盟主久闻少侠剿灭黑风寨的义举，特邀少侠赴会。")
                    .then(() -> toLocation(g)));
        };
        return new Ev("第一章 · 黑风密信")
                .narr("黑风寨主轰然倒地。你在他的卧房中搜出一只铁匣，匣中除了金银细软，还有一封火漆密信。")
                .narr("信上只有寥寥数语：「劫来之货，三成照旧送往洛阳。事成之后，盟中自有安排。」")
                .narr("落款处，是一枚小小的青锋剑形印记。")
                .me("青锋剑印……这不是武林盟的标记吗？")
                .choice("收好密信，日后查证", "或许会是重要的证据", true, () -> {
                    p.set("keep_letter");
                    p.log("在黑风寨得到一封盖有青锋剑印的密信");
                    next.run();
                })
                .choice("交给临安府衙", "交由官府处置", true, () -> {
                    p.justice += 3;
                    p.silver += 50;
                    g.toast("府衙赏银 50 两");
                    p.log("将黑风寨密信交给了临安府衙");
                    next.run();
                })
                .choice("一把火烧了", "免得惹祸上身", true, () -> {
                    p.justice -= 2;
                    p.log("烧毁了黑风寨的密信");
                    next.run();
                });
    }

    // ================= 第二章 =================

    private static Ev luoyang(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            if (p.has("alliance_favor")) {
                p.items[34]++;
                g.toast("获得：青锋令");
            }
            p.set("met_xiao");
            goStage(g, S_TOMB);
            toLocation(g);
        };
        Ev ev = new Ev("第二章 · 英雄大会")
                .narr("洛阳城，武林盟分舵。大厅之中群雄云集，觥筹交错，好不热闹。")
                .narr("主位上那人年约五旬，面如冠玉，三缕长须，一袭青袍，正是当今武林盟主——慕容渊。")
                .say("慕容渊", "诸位！二十年前，天魔教血洗萧家庄，屠戮三十余口，此乃我正道之耻！")
                .say("慕容渊", "如今魔教余孽赤练魔头盘踞终南古墓，以活人练功。哪位少年英雄，愿为武林除此大害？");
        if (p.has("alliance_favor")) {
            ev.say("慕容渊", "这位便是在竹林中助我盟弟子的" + p.name + "少侠吧？好，后生可畏！这枚青锋令你且收下。");
        } else if (p.has("keep_letter")) {
            ev.narr("慕容渊的目光扫过众人，在你身上微微一顿。你下意识按住了怀中那封盖着青锋剑印的密信。");
        }
        ev.narr("一个白衣青年大笑着走到你身边。他眉目疏朗，腰悬长剑，自有一股洒脱之气。")
                .say("萧寒", "在下萧寒，盟主是我义父。看兄台气度不凡，不如你我同去古墓，会一会那魔头？")
                .choice("与萧寒结为异姓兄弟", "义结金兰，生死与共", true, () -> show(g, new Ev("第二章 · 金兰之义")
                        .narr("你二人当即在关公像前撮土为香，对天八拜。")
                        .say("萧寒", "从今往后，你我便是兄弟！哈哈，我萧寒无父无母，今日总算多了一个亲人。")
                        .then(() -> {
                            p.affXiao += 30;
                            p.set("sworn");
                            p.log("与萧寒义结金兰");
                            next.run();
                        })))
                .choice("举杯同饮，结伴而行", "交个朋友也好", true, () -> {
                    p.affXiao += 15;
                    p.log("结识盟主义子萧寒");
                    next.run();
                })
                .choice("婉言谢绝，独来独往", "江湖险恶，不轻信于人", true, () -> {
                    p.affXiao += 3;
                    p.log("婉拒了萧寒的同行之邀");
                    next.run();
                });
        return ev;
    }

    private static Ev chilian(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第二章 · 古墓遗言").bgm(Audio.BGM_SAD)
                .narr("赤练魔头倒在血泊之中，却忽然仰天大笑，笑声在古墓中回荡，令人毛骨悚然。")
                .say("赤练魔头", "哈哈哈……正道？好一个正道！你们这些蠢材，全都被慕容渊那个伪君子骗了！")
                .say("赤练魔头", "二十年前的萧家庄……根本不是天魔教干的……是……")
                .narr("话音未落，一枚透骨钉从暗处疾射而来，正中她的眉心。你追出古墓，只见一道青影掠过树梢，转瞬无踪。");
        if (p.affXiao >= 15) {
            ev.say("萧寒", "（气喘吁吁地赶到）兄弟！没事吧？这魔头死到临头还要挑拨离间，你可别听她胡说。");
        }
        ev.narr("片刻之后，一阵幽香袭来。紫衣少女不知何时已站在古墓门口，月光照着她苍白的脸。");
        if (p.affSu >= 10) {
            ev.say("苏晚晴", "又见面了。你方才也听见了吧？二十年前的真相……我需要一个帮手。");
        } else {
            ev.say("苏晚晴", "是你。哼，我本不想求你们名门正派……可这件事，关系到二十年前的真相。");
        }
        ev.say("苏晚晴", "我爹……就是天魔教主厉天行。他背负血洗萧家庄的罪名二十年，可他从未做过此事。")
                .say("苏晚晴", "当年血案唯一的活口，是一位药师，据说被囚禁在苗疆五毒老怪手中。你……可愿随我南下大理？")
                .choice("随苏晚晴南下，追查真相", "真相不该被埋没", true, () -> {
                    p.affSu += 15;
                    p.set("route_truth");
                    p.log("决定随苏晚晴南下，追查萧家庄血案");
                    goStage(g, S_DALI);
                    toLocation(g);
                })
                .choice("妖女之言不可信，回洛阳复命", "魔教中人，诡计多端", true, () -> {
                    p.affSu -= 10;
                    p.affXiao += 10;
                    p.justice += 2;
                    p.set("route_alliance");
                    p.log("拒绝了苏晚晴，回洛阳复命");
                    show(g, report(g));
                })
                .choice("正道虚伪，不如投身天魔教", "需侠义值 ≤ -15（当前 " + p.justice + "）", p.justice <= -15, () -> {
                    p.affSu += 10;
                    p.justice -= 10;
                    p.set("join_demon");
                    p.set("route_truth");
                    p.log("暗中投身天魔教");
                    goStage(g, S_DALI);
                    toLocation(g);
                });
        return ev;
    }

    private static Ev report(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第三章 · 盟主密令")
                .narr("你回到洛阳复命。慕容渊听罢古墓中的经过，抚须长叹。")
                .say("慕容渊", "魔头临死胡言，意在乱我正道人心。" + p.name + "少侠能明辨是非，难得，难得。")
                .say("慕容渊", "据报，魔教护法正在华山思过崖搜寻一部失传剑谱。少侠可愿替老夫走一趟？");
        if (p.has("met_xiao")) {
            ev.say("萧寒", "义父放心，我与" + p.name + "一同前往！");
        }
        ev.narr("慕容渊命人取来二百两纹银，作为盘缠。")
                .then(() -> {
                    p.silver += 200;
                    goStage(g, S_HUASHAN);
                    g.goLocation(3);
                });
        return ev;
    }

    // ================= 第三章 · 追查真相 =================

    private static Ev dali(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第三章 · 苍山洱海")
                .narr("大理城，苍山如黛，洱海如镜。苏晚晴带你走进一家临水的僻静茶馆。")
                .say("苏晚晴", "五毒老怪的巢穴，就在城外苗疆毒林的最深处。那里瘴气弥漫，毒物横行，你须多备些解毒丹。")
                .narr("茶馆里的说书人正讲到「萧家庄血案」，说得唾沫横飞，满堂喝彩。苏晚晴听着，指节捏得发白。")
                .me("你恨武林盟吗？")
                .say("苏晚晴", "我恨的不是谁，是谎言。二十年了，我爹被千夫所指，却从不辩解一句。我只想知道，他为什么不说。");
        if (p.has("join_demon")) {
            ev.say("苏晚晴", "还有……你说要入我天魔教，是当真的？爹爹若知道有名门弟子来投，不知会有多惊讶。");
        }
        ev.choice("我陪你查清真相", "无论真相如何", true, () -> {
            p.affSu += 10;
            p.justice += 2;
            p.log("在大理答应苏晚晴，一同查清真相");
            goStage(g, S_MIAO);
            toLocation(g);
        }).choice("若真相对你爹不利呢？", "实话实说", true, () -> show(g, new Ev("第三章 · 苍山洱海")
                .say("苏晚晴", "……那我也认。我要的只是真相，不是一个干干净净的爹。")
                .narr("她别过脸去，望着窗外的洱海，许久没有说话。")
                .then(() -> {
                    p.affSu += 5;
                    goStage(g, S_MIAO);
                    toLocation(g);
                })));
        return ev;
    }

    private static Ev witness(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第三章 · 二十年前").bgm(Audio.BGM_SAD)
                .narr("五毒老怪死后，你们在毒林深处的石牢里，找到了一个形容枯槁、双腿尽废的老人。")
                .say("老药王", "你们……是来杀我灭口的吗？二十年了，慕容渊终于不想留我这条老命了？")
                .say("苏晚晴", "老人家别怕，我们是来求真相的。二十年前的萧家庄，究竟发生了什么？")
                .say("老药王", "那一夜，我奉命为萧庄主疗伤。三更时分，一群黑衣人杀入庄中，见人便杀，鸡犬不留……")
                .say("老药王", "为首之人蒙着面，可他使的是慕容家的独门剑法「流云剑」。萧庄主临死前，扯下了他的面巾……")
                .say("老药王", "正是慕容渊！他杀了萧家满门，却抱走了襁褓中的小公子，收为义子，又把罪名推给了天魔教……");
        if (p.has("met_xiao")) {
            ev.me("襁褓中的小公子……难道是萧寒？！");
        }
        ev.say("老药王", "老夫写了一份血书，藏在这石牢的墙缝里二十年。你们拿去吧……老夫，也算死而无憾了。")
                .narr("（获得：老药王的血书）")
                .say("苏晚晴", "我要回黑木崖，把这一切告诉爹爹。你去襄阳等我，好吗？武林盟正在那里集结，听说要大举进攻黑木崖。")
                .then(() -> {
                    p.set("has_testimony");
                    p.justice += 5;
                    p.affSu += 10;
                    p.log("在苗疆救出老药王，得知萧家庄血案的真相");
                    goStage(g, S_XIANGYANG);
                    toLocation(g);
                });
        return ev;
    }

    // ================= 第三章 · 效忠盟主 =================

    private static Ev huashan(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            goStage(g, S_TAIHU);
            show(g, new Ev("第三章 · 盟主密令")
                    .narr("你刚下华山，武林盟的飞鸽传书便到了。")
                    .say("慕容渊", "（信中）太湖水寨龙王勾结魔教，劫掠商船，罪大恶极。望少侠速往剿灭，务必……不留活口。")
                    .then(() -> toLocation(g)));
        };
        Ev ev = new Ev("第三章 · 华山剑痕")
                .narr("思过崖上，最后一名魔教护法倒下，怀中掉出一卷残破的剑谱和一封书信。")
                .narr("你展开书信，不禁大吃一惊——信中竟是慕容渊的笔迹：「护法辛苦。剑谱到手，照旧送往总坛，酬金加倍。」")
                .me("盟主……竟与魔教护法暗中往来？");
        if (p.has("met_xiao")) {
            ev.say("萧寒", "不可能！义父一生光明磊落，这定是魔教伪造书信，故意陷害他！");
        }
        ev.choice("呈交盟主，当面问个明白", "相信盟主的解释", true, () -> show(g, new Ev("第三章 · 华山剑痕")
                        .narr("慕容渊看完书信，面不改色，随手将它投入了烛火之中。")
                        .say("慕容渊", "魔教惯用离间之计，少侠莫要上当。此事，就不必再提了。")
                        .then(() -> {
                            p.justice += 1;
                            p.affXiao += 5;
                            p.set("alliance_favor");
                            p.log("将思过崖的书信交给了慕容渊");
                            next.run();
                        })))
                .choice("暗中收起书信", "事有蹊跷，留个后手", true, () -> {
                    p.set("huashan_letter");
                    p.log("暗中收起了慕容渊写给魔教护法的书信");
                    next.run();
                })
                .choice("撕毁书信，此事与我无关", "多一事不如少一事", true, () -> {
                    p.justice -= 3;
                    p.log("撕毁了思过崖的书信");
                    next.run();
                });
        return ev;
    }

    private static Ev ledger(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            goStage(g, S_XIANGYANG);
            show(g, new Ev("第三章 · 风起襄阳")
                    .narr("江湖传言，武林盟主已传令天下：各派精英齐聚襄阳，择日讨伐黑木崖。")
                    .then(() -> toLocation(g)));
        };
        return new Ev("第三章 · 太湖账本")
                .narr("水寨龙王跪倒在地，连连叩首，额头磕得鲜血直流。")
                .say("水寨龙王", "少侠饶命！我不过是替人管账的……这些年水寨劫来的银子，九成都送进了武林盟的金库啊！")
                .say("水寨龙王", "慕容渊要灭我的口，才诬我勾结魔教！账本……账本就藏在龙王庙的神像底下！")
                .choice("饶他一命，取走账本", "留下活口与证据", true, () -> {
                    p.set("has_ledger");
                    p.justice += 3;
                    g.toast("获得：太湖账本");
                    p.log("饶过水寨龙王，取得武林盟的黑账");
                    next.run();
                })
                .choice("斩草除根，再取账本", "盟主有令，不留活口", true, () -> {
                    p.set("has_ledger");
                    p.justice -= 5;
                    g.toast("获得：太湖账本");
                    p.log("斩杀水寨龙王，取得武林盟的黑账");
                    next.run();
                })
                .choice("收下金银，替他保密", "需侠义值 ≤ -15（当前 " + p.justice + "）", p.justice <= -15, () -> {
                    p.silver += 500;
                    p.justice -= 8;
                    p.log("收下水寨龙王的五百两封口银");
                    next.run();
                });
    }

    // ================= 第四章 =================

    private static Ev xiangyang(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第四章 · 襄阳烽火")
                .narr("襄阳城头旌旗猎猎，武林盟各派人马已在此集结。慕容渊立于城楼之上，意气风发。")
                .say("慕容渊", "诸位，天魔教覆灭在即！只是……我儿萧寒三日前独自追查魔教踪迹，深入大漠，至今未归。")
                .say("慕容渊", p.name + "少侠武艺高强，可否替老夫走一趟大漠孤城，寻回寒儿？")
                .narr("他说这话时神情关切，可不知为何，你总觉得那目光深处藏着一丝冷意。");
        if (p.has("has_testimony")) {
            ev.narr("你摸了摸怀中的血书，没有作声。");
        } else if (p.has("huashan_letter") || p.has("has_ledger")) {
            ev.narr("你想起怀中的那件证物，心中疑云更重。");
        }
        ev.choice("义不容辞", "即刻动身前往大漠", true, () -> {
            p.affXiao += 5;
            p.log("受慕容渊之托，前往大漠寻找萧寒");
            goStage(g, S_DESERT);
            toLocation(g);
        }).choice("要我去也行，先付五百两", "需侠义值 ≤ -10（当前 " + p.justice + "）", p.justice <= -10, () -> {
            p.silver += 500;
            p.justice -= 3;
            p.log("向慕容渊索要了五百两酬金");
            goStage(g, S_DESERT);
            toLocation(g);
        });
        return ev;
    }

    private static Ev desert(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            p.set("blade_confess");
            show(g, suPoison(g));
        };
        Ev ev = new Ev("第四章 · 大漠孤烟")
                .narr("大漠狂刀倒在黄沙之中。你在孤城的地牢深处，找到了遍体鳞伤的萧寒。")
                .say("萧寒", "咳……你来了……我查到，这伙马贼背后……竟然……")
                .say("大漠狂刀", "嘿嘿……小子，你以为老子是魔教的人？老子是拿了慕容渊的银子，来取你萧寒的命！")
                .say("大漠狂刀", "谁让你到处打听自己的身世……慕容渊……可不想留着你……")
                .narr("大漠狂刀头一歪，再没了声息。萧寒脸色惨白，久久说不出一句话来。");
        if (p.has("has_testimony")) {
            ev.choice("将老药王的血书交给萧寒", p.affXiao >= 20 ? "他会相信你" : "他未必信你", true, () -> {
                Ev r = new Ev("第四章 · 血书");
                if (p.affXiao >= 20) {
                    r.narr("萧寒读完血书，双手剧烈颤抖，一口鲜血喷在了黄沙之上。")
                            .say("萧寒", "二十年……我竟认贼作父二十年！他养我、教我，原来只是为了……心安？！")
                            .say("萧寒", "兄弟，多谢你告诉我。黑木崖上，我要亲口问他一句：为什么。")
                            .then(() -> {
                                p.set("xiao_knows");
                                p.affXiao += 20;
                                p.log("将血书交给萧寒，他得知了自己的身世");
                                next.run();
                            });
                } else {
                    r.narr("萧寒扫了一眼血书，冷笑一声，将它掷还给你。")
                            .say("萧寒", "一个将死之人的话，一封来历不明的血书，你就想让我怀疑养我二十年的义父？")
                            .then(() -> {
                                p.affXiao -= 5;
                                p.log("萧寒不肯相信血书");
                                next.run();
                            });
                }
                show(g, r);
            });
            ev.choice("此事关系重大，暂且瞒着他", "等待更好的时机", true, () -> {
                p.log("在大漠救出萧寒，没有告诉他真相");
                next.run();
            });
        } else {
            ev.choice("劝他提防慕容渊", p.affXiao >= 30 ? "他会听进去" : "他未必信你", true, () -> {
                if (p.affXiao >= 30) {
                    p.set("xiao_knows");
                    p.affXiao += 10;
                    g.toast("萧寒默默点头，眼中多了几分疑虑");
                    p.log("萧寒开始怀疑自己的义父");
                } else {
                    p.affXiao -= 3;
                    g.toast("萧寒摇了摇头：「义父不会害我。」");
                }
                next.run();
            });
            ev.choice("死人之言，不必当真", "先救人要紧", true, () -> {
                p.affXiao += 5;
                p.log("在大漠救出萧寒");
                next.run();
            });
        }
        return ev;
    }

    private static Ev suPoison(Game g) {
        final Player p = g.player;
        if (!p.has("met_su") || p.affSu < 10) {
            return new Ev("第四章 · 大军北上")
                    .narr("你护送萧寒回到襄阳。数日后，武林盟大军开拔，旌旗蔽日，直指黑木崖。")
                    .narr("出征前夜，你独自站在城头，望着北方的夜空，心中隐隐不安。")
                    .then(() -> {
                        goStage(g, S_FINAL);
                        g.goLocation(10);
                    });
        }
        return new Ev("第五章 · 玄冥寒毒")
                .narr("回到襄阳的当夜，一只信鸽落在你的窗前。信是苏晚晴的侍女写的，字迹潦草，泪痕斑斑：")
                .narr("「小姐回教途中遭蒙面高手偷袭，身中玄冥寒毒，命在旦夕。唯天山雪莲可解。求少侠相救！」")
                .me("玄冥寒毒……当今武林，会这门阴毒功夫的人屈指可数……")
                .choice("立刻启程，远赴天山", "天山雪岭凶险异常", true, () -> {
                    p.affSu += 10;
                    p.log("得知苏晚晴中毒，远赴天山求药");
                    goStage(g, S_TIANSHAN);
                    g.goLocation(10);
                })
                .choice("大战在即，直上黑木崖", "以大局为重", true, () -> {
                    p.affSu -= 20;
                    p.set("abandoned_su");
                    p.log("没有去救中毒的苏晚晴");
                    goStage(g, S_FINAL);
                    g.goLocation(10);
                });
    }

    // ================= 第五章 =================

    private static Ev lotus(Game g) {
        final Player p = g.player;
        return new Ev("第五章 · 天山雪莲")
                .narr("冰魄老人长叹一声，让开了身后的冰崖。崖顶之上，一株雪莲迎风而立，晶莹如玉。")
                .say("冰魄老人", "老夫守这雪莲三十年，为的是等一个值得的人。罢了，你为救人而来，拿去吧。")
                .narr("你日夜兼程，赶到苏晚晴藏身的山村。她服下雪莲，苍白的脸上终于渐渐有了血色。")
                .say("苏晚晴", "傻子……从天山到这里，你几天没合眼了？")
                .say("苏晚晴", "偷袭我的那个人蒙着面，可他用的是玄冥掌，还有……流云剑法。")
                .say("苏晚晴", "这件天蚕宝衣，是我娘留下的。你穿上它。黑木崖上……我不想看到你受伤。")
                .narr("（获得：天蚕宝衣）")
                .choice("黑木崖上，我与你同在", "生死相随", true, () -> {
                    p.affSu += 15;
                    finishLotus(g);
                })
                .choice("保重。", "点到即止", true, () -> finishLotus(g));
    }

    private static void finishLotus(Game g) {
        Player p = g.player;
        p.set("saved_su");
        p.affSu += 20;
        p.items[28]++;
        p.log("以天山雪莲救回苏晚晴");
        goStage(g, S_FINAL);
        toLocation(g);
    }

    // ================= 终章 =================

    private static Ev climax(Game g) {
        final Player p = g.player;
        int n = evidence(p);
        Ev e = new Ev("终章 · 黑木崖之巅").bgm(Audio.BGM_BOSS)
                .narr("黑木崖顶，天魔殿前。武林盟大军与天魔教众杀声震天，血染石阶。")
                .narr("天魔教主厉天行负手立于殿前，须发如戟，不怒自威。他的对面，慕容渊青袍猎猎，长剑在手。")
                .say("厉天行", "慕容渊！二十年了，你终于敢上我黑木崖了！")
                .say("慕容渊", "魔头！今日我便替萧家庄三十余口冤魂，讨回一个公道！");
        if (p.has("saved_su")) {
            e.narr("苏晚晴站在父亲身侧，望向你的目光中满是担忧。");
        } else if (p.has("met_su")) {
            e.narr("你看见了苏晚晴。她站在天魔殿的阴影里，脸色苍白，没有看你。");
        }
        if (p.has("xiao_knows") && p.affXiao >= 30) {
            e.say("萧寒", "（走到你身边，低声）兄弟，无论你作何选择，我都站在你这边。");
        } else if (p.has("met_xiao")) {
            e.narr("萧寒站在慕容渊身后，握剑的手微微发抖。");
        }
        e.say("慕容渊", p.name + "！还等什么？随我诛杀魔头！");
        boolean suLove = p.has("saved_su") && p.affSu >= 70;
        e.choice("听从盟主号令，诛杀天魔教主", "正邪不两立", true, () -> fight(g, 14,
                        () -> ending(g, p.has("met_su") && p.affSu >= 40 ? END_LONE : END_HEIR), null))
                .choice("当众揭穿慕容渊的真面目", "需要至少两件证据（现有 " + n + " 件）", n >= 2, () -> show(g, expose(g)))
                .choice("投身天魔教，诛杀伪君子", "需侠义值 ≤ -20 或已入魔教（当前 " + p.justice + "）",
                        p.justice <= -20 || p.has("join_demon"), () -> show(g, joinDemon(g)))
                .choice("带苏晚晴离开这是非之地", "需救下苏晚晴且好感 ≥ 70（当前 " + p.affSu + "）", suLove,
                        () -> show(g, elope(g)));
        return e;
    }

    private static Ev expose(Game g) {
        final Player p = g.player;
        Ev e = new Ev("终章 · 真相大白").bgm(Audio.BGM_BOSS)
                .me("慢着！慕容渊，二十年前血洗萧家庄的，根本不是天魔教——是你！");
        if (p.has("keep_letter")) {
            e.me("黑风寨的密信，盖着你的青锋剑印——劫来的赃银，三成送进了你的洛阳！");
        }
        if (p.has("huashan_letter")) {
            e.me("思过崖的书信，是你亲笔写给魔教护法的！");
        }
        if (p.has("has_ledger")) {
            e.me("太湖水寨的账本，记着你武林盟这些年的每一笔黑钱！");
        }
        if (p.has("has_testimony")) {
            e.me("还有老药王的血书！二十年前，萧庄主临死前扯下的，正是你的面巾！");
        }
        if (p.has("blade_confess")) {
            e.me("大漠狂刀临死前亲口承认，是你出钱要取萧寒的性命！");
        }
        e.narr("群雄哗然。无数道目光，齐刷刷地落在了慕容渊身上。");
        if (p.has("met_xiao")) {
            e.say("萧寒", "义父……他说的，是真的吗？");
        }
        e.say("慕容渊", "……好，好得很。二十年的布局，竟毁在你这个黄口小儿手里。")
                .say("慕容渊", "既然如此——今日在场之人，一个也别想活着下山！")
                .then(() -> fight(g, 31, () -> {
                    if (p.has("saved_su") && p.affSu >= 60) {
                        show(g, afterExpose(g));
                    } else {
                        ending(g, END_HERO);
                    }
                }, null));
        return e;
    }

    private static Ev afterExpose(Game g) {
        return new Ev("终章 · 尘埃落定")
                .narr("慕容渊倒在了天魔殿前的石阶上。二十年的冤案，终于大白于天下。")
                .narr("群雄纷纷向你抱拳致意。人群之外，苏晚晴静静地望着你，嘴角带着一丝浅浅的笑。")
                .say("苏晚晴", "接下来，你打算怎么办？")
                .choice("留下来，重整武林", "侠之大者，为国为民", true, () -> ending(g, END_HERO))
                .choice("携晚晴归隐山林", "江湖再大，不如一人", true, () -> ending(g, END_LOVERS));
    }

    private static Ev joinDemon(Game g) {
        return new Ev("终章 · 入魔").bgm(Audio.BGM_BOSS)
                .me("慕容渊，你的正道，我看够了。")
                .narr("你转过身，剑尖直指武林盟主。正道群雄一片哗然，厉天行却放声大笑。")
                .say("厉天行", "好！好小子！今日你我联手，先斩了这个伪君子！")
                .then(() -> fight(g, 31, () -> show(g, demonChoice(g)), null));
    }

    private static Ev demonChoice(Game g) {
        return new Ev("终章 · 魔尊")
                .narr("慕容渊倒下了。正道群雄四散奔逃，黑木崖上只剩下天魔教的欢呼。")
                .say("厉天行", "哈哈哈！小子，从今日起，你便是我天魔教的光明右使！")
                .narr("你望着他伸出的手，又看了看满地的尸首。手中的长剑，仍在滴血。")
                .choice("单膝跪下，拜见教主", "共掌江湖", true, () -> ending(g, END_ENVOY))
                .choice("这江湖，该换个主人了", "与厉天行一决生死", true, () -> show(g, new Ev("终章 · 魔尊").bgm(Audio.BGM_BOSS)
                        .say("厉天行", "……你说什么？哈，好，好！不愧是我看中的人！来吧！")
                        .then(() -> fight(g, 14, () -> ending(g, END_TYRANT), null))));
    }

    private static Ev elope(Game g) {
        final Player p = g.player;
        Ev e = new Ev("终章 · 归去")
                .narr("你穿过混战的人群，握住了苏晚晴冰凉的手。")
                .say("苏晚晴", "你……你要做什么？")
                .me("带你走。这江湖的恩怨，让他们自己去了结吧。");
        if (p.has("xiao_knows") && p.affXiao >= 30) {
            e.say("萧寒", "（横剑挡住追兵）走！兄弟，这里交给我。照顾好她！")
                    .then(() -> ending(g, END_LOVERS));
        } else {
            e.say("萧寒", "站住！" + p.name + "，你要为了一个魔教妖女，背叛整个正道吗？")
                    .then(() -> fight(g, 32, () -> ending(g, END_LOVERS), null));
        }
        return e;
    }

    // ================= 结局 =================

    static void ending(Game g, int id) {
        final Player p = g.player;
        boolean sad = id == END_LONE || id == END_HEIR;
        Ev e = new Ev("结局 · " + ENDING_NAMES[id]).bgm(sad ? Audio.BGM_SAD : Audio.BGM_ENDING);
        boolean xiaoFriend = p.has("xiao_knows") && p.affXiao >= 30;
        switch (id) {
            case END_HERO:
                e.narr("慕容渊伏诛，二十年的冤案终于大白于天下。");
                if (xiaoFriend) {
                    e.narr("萧寒在萧家庄旧址前长跪三日，而后接过盟主之位，与天魔教立下百年不战之约。");
                } else if (p.has("met_xiao")) {
                    e.narr("萧寒得知身世之后，一夜白头。有人说他去了关外，从此再未踏足中原。");
                }
                if (p.has("met_su") && p.affSu >= 30) {
                    e.say("苏晚晴", "若你哪日累了，黑木崖上，有一壶酒为你温着。");
                }
                e.narr("群雄推举你为武林盟主，你却只接下了一块「侠之大者」的匾额。")
                        .narr("「侠之大者，为国为民。」你留下这句话，转身走入了万丈红尘。");
                break;
            case END_LOVERS:
                e.narr("那一日之后，江湖上再没有人见过" + p.name + "和苏晚晴。")
                        .narr("有人说，在大理苍山脚下，见过一对夫妻开了间小酒馆。男的擅剑，女的抚琴。");
                if (xiaoFriend) {
                    e.narr("每年清明，总有一个白衣剑客路过那间酒馆，一坐便是一整夜。");
                }
                e.narr("偶尔有江湖客慕名而来，想要讨教几招，都被那男人一壶好酒灌醉，糊里糊涂地下了山。")
                        .narr("江湖很大，可他们的江湖，只有彼此。");
                break;
            case END_TYRANT:
                e.narr("厉天行倒在了天魔殿的宝座之前。你踏着他的尸首，一步一步走上了那张黑玉椅。");
                if (p.has("met_su")) {
                    e.narr("苏晚晴看着你，眼中再没有一丝光彩。那一夜，她独自离开了黑木崖。");
                }
                e.narr("正道群龙无首，魔教一家独大。不出三年，整个江湖已尽在你的掌握之中。")
                        .narr("人们不再叫你的名字，只叫你——「魔尊」。")
                        .narr("你站在黑木崖之巅，俯瞰万里河山，却忽然觉得……这崖顶的风，冷得刺骨。");
                break;
            case END_HEIR:
                e.narr("天魔教主伏诛，黑木崖上一把大火，烧了三天三夜。")
                        .narr("慕容渊当众宣布，收你为衣钵传人。你名动天下，风光无两。");
                if (p.has("met_su")) {
                    e.narr("你再也没有见过苏晚晴。有人说，她在一个雪夜，削发为尼。");
                }
                e.narr("直到某一天，你在盟主书房中，偶然翻出一封二十年前的旧信。")
                        .narr("信纸泛黄，上面只有一行字：「萧家庄之事，务必做得干净。」落款是……慕容渊。")
                        .narr("你握着那封信，在窗前坐了一夜。窗外，新的盟主大旗正猎猎作响。这个江湖，真的变好了吗？");
                break;
            case END_LONE:
                e.narr("厉天行倒下的那一刻，苏晚晴扑了上来，用身体挡住了慕容渊补上的那一掌。")
                        .say("苏晚晴", "傻子……我不怪你……替我……查清……")
                        .narr("她在你怀中渐渐冷去，手中紧紧攥着那支紫玉簪的另一半。")
                        .narr("后来你才知道，二十年前的真相，就藏在慕容渊的书房里。可这一切，都已经太迟了。")
                        .narr("从此江湖上多了一个独行的剑客，一壶酒，一柄剑，一支紫玉簪。他走过很多地方，却再也没有回过黑木崖。");
                break;
            default:
                e.narr("你单膝跪下，厉天行哈哈大笑，声震九霄。")
                        .narr("慕容渊身死，武林盟土崩瓦解。天魔教重开山门，正邪之争告一段落。");
                if (p.has("met_su") && p.affSu >= 30) {
                    e.narr("苏晚晴成了你的妻子。她常说，你是她见过最奇怪的魔教中人——从不滥杀一个无辜。");
                }
                e.narr("江湖人提起天魔教的光明右使，语气复杂：有人骂他是叛徒，也有人说，他是唯一敢对教主说「不」的人。")
                        .narr("正与邪，原本就没有那么分明。");
                break;
        }
        e.then(() -> {
            p.ending = id;
            p.finished = true;
            p.log("结局：" + ENDING_NAMES[id]);
            goStage(g, S_POST);
            g.unlockEnding(id);
            g.save();
            g.setScene(new EndingScene(g, id));
        });
        show(g, e);
    }

    // ================= 奇遇武学 =================

    public static void hermit(Game g, int skill) {
        Player p = g.player;
        GameData.Skill s = GameData.SKILLS[skill];
        Ev e = new Ev("奇遇 · " + s.name).bgm(Audio.BGM_TITLE);
        switch (skill) {
            case 16:
                e.narr("你在竹林深处迷了路，误入一处山洞。洞中石壁上画着一位女子的步法图谱，旁书「凌波微步」四字。")
                        .narr("你依图而行，起初跌跌撞撞，渐渐地竟觉身轻如羽，脚下如踏碧波。");
                break;
            case 17:
                e.narr("古墓深处，你发现一具枯骨，身旁一册羊皮卷上写着「吸星大法」。")
                        .narr("你一时好奇，依法运功，只觉丹田中一股吸力油然而生，四周的内息竟向你汇聚而来。");
                break;
            case 18:
                e.narr("思过崖后洞中，一位青袍老者负手而立。他看了你半晌，淡淡道：「你根骨不错，老夫传你几招剑法。」")
                        .say("神秘老者", "独孤九剑，有进无退！招招都是进攻，攻敌之不得不守。无招，方能胜有招。");
                break;
            case 23:
                e.narr("毒林深处，你误入一座废弃的苗寨。祭坛上供着一卷蛛丝织成的秘籍，封面绣着「千蛛万毒手」。")
                        .narr("你忍着恶心翻开……原来以毒攻毒，也是一门大学问。");
                break;
            case 24:
                e.narr("风雪中，你在一处冰洞里避寒。洞壁上刻满小人图形，一招一式，竟是一套精妙绝伦的掌法。")
                        .narr("刻痕旁有一行小字：「天下武学，皆可化入此手。」——天山折梅手。");
                break;
            case 25:
                e.narr("一场沙暴过后，黄沙下露出一具骸骨，手中仍紧握着一柄断刀。刀鞘内藏着一卷羊皮：「狂风刀法」。")
                        .narr("你迎着漫天风沙挥刀，只觉刀势如狂风骤起，飞沙走石。");
                break;
            default:
                e.narr("海边礁石上，一位白发渔翁正在吹箫。箫声时而如潮水汹涌，时而如细浪温柔。")
                        .say("神秘老者", "一曲碧海潮生，你听懂了几分？")
                        .narr("你闭目回味，内息竟随着潮声起伏，生生不息。");
                break;
        }
        e.narr("（习得奇功：" + s.name + "）");
        p.learn(skill);
        p.log("奇遇：习得" + s.name);
        g.audio.sfx(Audio.SFX_LEVEL);
        g.save();
        e.then(() -> toLocation(g));
        show(g, e);
    }

    // ================= 江湖奇遇（随机事件） =================

    private static int locEnemy(Game g) {
        int[] pool = GameData.LOCATIONS[g.player.location].enemies;
        if (pool.length == 0) {
            return 0;
        }
        return pool[g.rnd.nextInt(pool.length)];
    }

    private static void wildFight(Game g, int foe, Runnable win) {
        g.setScene(new BattleScene(g, foe, BattleScene.KIND_WILD, win, null));
    }

    public static Ev randomEvent(Game g) {
        switch (g.rnd.nextInt(8)) {
            case 0:
                return evBully(g);
            case 1:
                return evWounded(g);
            case 2:
                return evCaravan(g);
            case 3:
                return evGamble(g);
            case 4:
                return evOldMan(g);
            case 5:
                return evStoryteller(g);
            case 6:
                return evFakeAlliance(g);
            default:
                return evFriend(g);
        }
    }

    private static Ev evBully(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 路见不平")
                .narr("山道旁，几个凶神恶煞的家伙正围着一个卖花的小姑娘推推搡搡，花篮被踢翻在地，花瓣散了一地。")
                .choice("出手教训", "与" + GameData.ENEMIES[foe].name + "交手", true, () -> wildFight(g, foe, () -> {
                    p.justice += 4;
                    g.toast("小姑娘千恩万谢。侠义 +4");
                    p.log("路见不平，救下卖花的小姑娘");
                    toLocation(g);
                }))
                .choice("丢下二十两银子打发他们", "破财消灾", p.silver >= 20, () -> {
                    p.silver -= 20;
                    p.justice += 1;
                    g.toast("恶徒们拿了钱，骂骂咧咧地走了");
                    toLocation(g);
                })
                .choice("视而不见", null, true, () -> {
                    p.justice -= 2;
                    g.toast("身后传来小姑娘的哭声……侠义 -2");
                    toLocation(g);
                });
    }

    private static Ev evWounded(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 魔教伤者")
                .narr("草丛里躺着一个黑衣青年，衣襟上绣着天魔教的火焰纹，腹部一道剑伤还在汩汩渗血。")
                .say("天魔教弟子", "求……求求你……我只是教中的一个伙夫……我不想死……")
                .choice("用金创药救他", "消耗一份金创药", p.items[0] > 0, () -> {
                    p.items[0]--;
                    p.affSu += 5;
                    p.justice += 1;
                    g.toast("他千恩万谢地走了，说回教后定会报答");
                    p.log("救治了一名受伤的天魔教弟子");
                    toLocation(g);
                })
                .choice("补上一剑", "魔教中人，死有余辜", true, () -> {
                    p.justice -= 6;
                    for (String m : p.gainExp(10 + p.level * 3)) {
                        g.toast(m);
                    }
                    g.toast("侠义 -6");
                    toLocation(g);
                })
                .choice("转身离开", null, true, () -> toLocation(g));
    }

    private static Ev evCaravan(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 商队遇袭")
                .narr("前方传来呼救声，一支商队被强人拦住了去路，车夫倒在血泊中，货物散落一地。")
                .choice("拔刀相助", "与" + GameData.ENEMIES[foe].name + "交手", true, () -> wildFight(g, foe, () -> {
                    int s = 30 + p.level * 10;
                    p.silver += s;
                    p.justice += 3;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("商队酬谢 " + s + " 两。侠义 +3");
                    p.log("救下一支遇袭的商队");
                    toLocation(g);
                }))
                .choice("趁乱顺走几箱货物", "侠义 -6", true, () -> {
                    int s = 40 + p.level * 12;
                    p.silver += s;
                    p.justice -= 6;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("你顺走了价值 " + s + " 两的货物。侠义 -6");
                    toLocation(g);
                })
                .choice("绕道而行", null, true, () -> toLocation(g));
    }

    private static void gamble(Game g, int bet) {
        Player p = g.player;
        if (g.rnd.nextBoolean()) {
            p.silver += bet;
            g.audio.sfx(Audio.SFX_COIN);
            g.toast("开！是大！你赢了 " + bet + " 两");
        } else {
            p.silver -= bet;
            g.audio.sfx(Audio.SFX_FAIL);
            g.toast("开……是小。你输了 " + bet + " 两");
        }
        g.save();
        toLocation(g);
    }

    private static Ev evGamble(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 路边赌局")
                .narr("路边茶棚里，一个独眼汉子摆开了骰盅，吆喝声震天：「押大押小，买定离手！」")
                .choice("押五十两", "五五之数", p.silver >= 50, () -> gamble(g, 50))
                .choice("押两百两", "富贵险中求", p.silver >= 200, () -> gamble(g, 200))
                .choice("不赌", null, true, () -> toLocation(g));
    }

    private static void oldManReward(Game g) {
        Player p = g.player;
        p.justice += 2;
        String gain;
        switch (g.rnd.nextInt(4)) {
            case 0:
                p.baseAtk += 1;
                gain = "攻击 +1";
                break;
            case 1:
                p.baseDef += 1;
                gain = "防御 +1";
                break;
            case 2:
                p.baseAgi += 1;
                gain = "身法 +1";
                break;
            default:
                p.baseHp += 15;
                p.hp += 15;
                gain = "气血上限 +15";
                break;
        }
        p.log("得奇人指点，" + gain);
        g.audio.sfx(Audio.SFX_LEVEL);
        show(g, new Ev("江湖偶遇 · 奇人")
                .narr("老者吃罢，哈哈一笑，忽然伸手在你背上连拍三掌。你只觉一股暖流游走四肢百骸……")
                .narr("再抬头时，老者已不知去向。（" + gain + "）")
                .then(() -> toLocation(g)));
    }

    private static Ev evOldMan(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 饥饿老者")
                .narr("一位衣衫褴褛的老者坐在路边石头上，有气无力地说：「后生，可有吃的？老头子三天没吃东西了。」")
                .choice("送他一份草药", "消耗一份草药", p.items[12] > 0, () -> {
                    p.items[12]--;
                    oldManReward(g);
                })
                .choice("送他一份金创药", "消耗一份金创药", p.items[0] > 0, () -> {
                    p.items[0]--;
                    oldManReward(g);
                })
                .choice("给他十两银子", null, p.silver >= 10, () -> {
                    p.silver -= 10;
                    p.justice += 1;
                    g.toast("老者道了声谢，蹒跚而去");
                    toLocation(g);
                })
                .choice("不予理会", null, true, () -> toLocation(g));
    }

    private static String rumor(Game g) {
        Player p = g.player;
        if (g.rnd.nextInt(3) == 0) {
            String[] hermits = {
                    "听说竹林深处有个山洞，洞里画着仙女的步法，谁学会了就能踏水而行！",
                    "古墓里埋着一部邪门功夫，能吸人内力。啧啧，正道人士可千万别碰。",
                    "华山思过崖后洞里，住着一位不肯露面的老剑客，脾气古怪得很。",
                    "苗疆的蛊师说，毒林里有座废寨，供着一卷蛛丝秘籍。",
                    "天山上的冰洞里，刻着一套能化尽天下武学的掌法。",
                    "大漠的沙暴过后，常有前人的骸骨和兵刃被风吹出来。",
                    "东海边有个吹箫的老渔翁，箫声能让海浪倒流！",
            };
            return hermits[g.rnd.nextInt(hermits.length)];
        }
        int s = p.stage;
        if (s <= S_HEIFENG) {
            return "列位看官！话说那黑风寨主，一口鬼头刀，杀人不眨眼。可他背后有没有人撑腰？嘿嘿，天知道！";
        } else if (s <= S_TOMB) {
            return "要说当今武林，谁人不知慕容盟主？二十年前萧家庄血案之后，盟主收养了一个孤儿，传为美谈呐！";
        } else if (s <= S_TAIHU) {
            return "听说苗疆五毒老怪手里关着个老头儿，二十年没见过天日，也不知犯了什么事。还有那太湖水寨，银子多得没处花！";
        } else if (s <= S_TIANSHAN) {
            return "天山之巅有雪莲，三十年一开，能解天下奇毒。只可惜有个冰魄老人守着，谁去谁死！";
        } else if (s == S_FINAL) {
            return "正邪决战就在眼前！列位押哪边？我老汉押……嘘，这话可不敢乱说。";
        }
        return "听说东海之上有座蓬莱仙岛，岛上隐居着一位剑仙，手中龙吟剑一出，天下无人能挡！";
    }

    private static Ev evStoryteller(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 茶棚说书")
                .narr("林间茶棚里，一个说书人正讲得眉飞色舞，四下围了一圈听客。你要了一碗粗茶，也坐下来听。")
                .say("说书人", rumor(g))
                .then(() -> {
                    int e = 5 + p.level * 2;
                    for (String m : p.gainExp(e)) {
                        g.toast(m);
                    }
                    g.toast("听得入神，修为 +" + e);
                    toLocation(g);
                });
    }

    private static Ev evFakeAlliance(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 冒名之徒")
                .narr("一伙人打着武林盟的旗号，正在村子里挨家挨户强收「护武钱」，交不出的便砸锅拆屋。")
                .choice("揭穿这些冒牌货", "与" + GameData.ENEMIES[foe].name + "交手", true, () -> wildFight(g, foe, () -> {
                    p.justice += 4;
                    p.silver += 30;
                    g.toast("村民们拍手称快，凑了三十两谢你。侠义 +4");
                    p.log("揭穿了冒充武林盟的恶徒");
                    toLocation(g);
                }))
                .choice("与他们分赃", "侠义 -5", true, () -> {
                    p.silver += 60;
                    p.justice -= 5;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("你分到了六十两。侠义 -5");
                    toLocation(g);
                })
                .choice("不管闲事", null, true, () -> toLocation(g));
    }

    private static Ev evFriend(Game g) {
        final Player p = g.player;
        if (p.has("met_xiao") && p.stage < S_FINAL && g.rnd.nextBoolean()) {
            return new Ev("江湖偶遇 · 故人")
                    .narr("山道转角，一个白衣人正坐在石头上喝酒——竟是萧寒。")
                    .say("萧寒", "兄弟！来来来，喝一杯！这是我从义父酒窖里偷出来的女儿红，二十年陈酿！")
                    .choice("陪他痛饮", "气血内力全满，萧寒好感 +5", true, () -> {
                        p.fullRestore();
                        p.affXiao += 5;
                        g.toast("一醉方休");
                        toLocation(g);
                    })
                    .choice("有事在身，改日再叙", null, true, () -> toLocation(g));
        }
        if (p.has("met_su") && p.affSu >= 0 && p.stage < S_FINAL) {
            return new Ev("江湖偶遇 · 飞鸽传书")
                    .narr("一只通体雪白的信鸽落在你的肩头，脚上绑着一张小小的字条。")
                    .say("苏晚晴", "（字条）听说你最近在" + GameData.LOCATIONS[p.location].name + "一带闯荡。山中多瘴，记得带解毒丹。……才不是担心你。")
                    .then(() -> {
                        p.affSu += 3;
                        p.items[37]++;
                        g.toast("获得：解毒丹");
                        toLocation(g);
                    });
        }
        return evBully(g);
    }
}
