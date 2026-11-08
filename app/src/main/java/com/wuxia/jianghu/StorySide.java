package com.wuxia.jianghu;

/** 支线剧情、奇遇武学、客栈打听与江湖随机奇遇。 */
final class StorySide {
    private StorySide() {
    }

    static Ev build(Game g, String id) {
        switch (id) {
            case "sq0_a":
                return sq0a(g);
            case "sq0_b":
                return sq0b(g);
            case "sq1_a":
                return sq1a(g);
            case "sq1_b":
                return sq1b(g);
            case "sq1_c":
                return sq1c(g);
            case "sq2_a":
                return sq2a(g);
            case "sq2_b":
                return sq2b(g);
            case "sq3_a":
                return sq3a(g);
            case "sq3_b":
                return sq3b(g);
            case "sq4_a":
                return sq4a(g);
            case "sq4_b":
                return sq4b(g);
            case "sq4_c":
                return sq4c(g);
            case "sq5_a":
                return sq5a(g);
            case "sq5_b":
                return sq5b(g);
            default:
                return null;
        }
    }

    private static Runnable back(Game g) {
        return () -> Story.toLocation(g);
    }

    // ================= 支线：师门秘传 =================

    private static Ev sq0a(Game g) {
        final Player p = g.player;
        Ev e = new Ev("支线 · 师门秘传").bg(14 + p.sect).narr("你回到师门。" + Sects.MASTER[p.sect] + "听你讲完山下的见闻，微微颔首。");
        switch (p.sect) {
            case 0:
                e.say("师父", "少林十八铜人阵，乃本寺弟子出师的最后一关。你若能闯过，老衲便将镇寺之宝金刚指环传你。");
                break;
            case 1:
                e.say("师父", "真武七星剑阵，是本门弟子出师的考验。闯过剑阵，真武剑便归你所用。");
                break;
            case 2:
                e.say("师父", "金顶守山师太的佛光剑，你若能接下，祖师的青霜剑便交给你了。");
                break;
            default:
                e.say("师父", "打狗阵，是历代帮主传人的试炼。闯得过去，镇帮神棒就是你的。");
                break;
        }
        return e.narr("（支线：在师门点击「挑战首领」，闯过" + Sects.GUARDIAN_NAME[p.sect] + "）").then(back(g));
    }

    private static Ev sq0b(Game g) {
        final Player p = g.player;
        final int w = Sects.WEAPON[p.sect];
        return new Ev("支线 · 出师").bg(14 + p.sect)
                .narr(Sects.GUARDIAN_NAME[p.sect] + "缓缓散开，同门师兄弟们爆发出一阵喝彩。")
                .say("师父", "好，好！你已可出师了。这" + GameData.ITEMS[w].name + "，从今日起便交给你。")
                .say("师父", "神兵在手，更须时时自省。持剑之人若失了本心，剑便成了凶器。")
                .narr("（获得：" + GameData.ITEMS[w].name + "）")
                .then(() -> {
                    p.items[w]++;
                    p.log("闯过师门试炼，获得镇派神兵" + GameData.ITEMS[w].name);
                    Story.toLocation(g);
                });
    }

    // ================= 支线：萧家庄旧址 =================

    private static Ev sq1a(Game g) {
        return new Ev("支线 · 萧家庄旧址").bgm(Audio.BGM_SAD)
                .narr("洛阳城外三十里，萧家庄只剩下一片焦黑的断壁残垣。荒草没过了膝盖。")
                .say("萧寒", "这里……就是我出生的地方？我竟一点也不记得了。")
                .narr("萧寒在废墟中跪了很久。你却注意到，地上有几行新鲜的脚印，一路向东延伸。")
                .me("有人比我们先来过。看这脚印的方向……是黑风寨。")
                .say("萧寒", "（猛地站起）是影阁的人！他们在找什么？追！")
                .narr("（支线：在黑风寨击败三名影阁杀手）")
                .then(back(g));
    }

    private static Ev sq1b(Game g) {
        return new Ev("支线 · 萧家庄旧址")
                .narr("最后一名影阁杀手倒下前，怀中掉出一张萧家庄的草图，地窖的位置被朱笔圈了出来。")
                .say("萧寒", "地窖？我从来不知道萧家庄有地窖。走，回去看看！")
                .narr("（支线：回到洛阳，点击金色剧情按钮）")
                .then(back(g));
    }

    private static Ev sq1c(Game g) {
        final Player p = g.player;
        return new Ev("支线 · 寒梅剑谱")
                .narr("地窖里只有一只铁匣。匣中是一卷泛黄的剑谱，封皮上写着四个字：寒梅剑法。")
                .narr("剑谱里夹着一封信，是萧庄主的笔迹：「吾儿若得此谱，当知萧家剑法，只为护人，不为复仇。」")
                .say("萧寒", "（声音发颤）爹……")
                .choice("将剑谱交给萧寒", "萧寒好感 ↑↑ · 决战时萧寒更强", true, () -> {
                    p.affXiao += 20;
                    p.set("xiao_sword");
                    p.log("将寒梅剑谱交还萧寒");
                    Story.show(g, new Ev("支线 · 寒梅剑谱")
                            .say("萧寒", "这份情，我萧寒记一辈子。")
                            .then(back(g)));
                })
                .choice("请萧寒传你剑法", "习得武功：寒梅剑法", true, () -> {
                    p.learn(27);
                    p.affXiao += 5;
                    p.log("萧寒将寒梅剑法传授给你");
                    g.audio.sfx(Audio.SFX_LEVEL);
                    Story.show(g, new Ev("支线 · 寒梅剑谱")
                            .say("萧寒", "萧家的剑法，交到你手里，我放心。")
                            .narr("（习得：寒梅剑法）")
                            .then(back(g)));
                });
    }

    // ================= 支线：晚晴的心事 =================

    private static Ev sq2a(Game g) {
        return new Ev("支线 · 晚晴的心事")
                .narr("大理城外，洱海边的一座小坟前，苏晚晴正蹲着拔草。墓碑上只刻了三个字：段氏女。")
                .say("苏晚晴", "我娘是大理人。她嫁给我爹，被家里逐出了门。临终前，她只想葬在洱海边。")
                .say("苏晚晴", "可前些天，教中的叛徒赤发鬼掘了这座坟，抢走了我娘的陪葬玉佩……他就躲在苗疆。")
                .narr("（支线：在苗疆毒林挑战首领，击败赤发鬼）")
                .then(back(g));
    }

    private static Ev sq2b(Game g) {
        final Player p = g.player;
        return new Ev("支线 · 同心")
                .narr("你把玉佩还给苏晚晴。她捧着它看了很久，忽然从发间取下一支玉簪，放在你的掌心。")
                .say("苏晚晴", "这是用我娘玉佩的边角磨的。你那支紫玉簪……我也替你重新打磨过了。")
                .say("苏晚晴", "簪上的两个字，你自己看。不许笑。")
                .narr("（获得：同心紫玉簪）")
                .choice("郑重收下", "苏晚晴好感 ↑↑", true, () -> {
                    p.items[42]++;
                    p.affSu += 20;
                    p.log("苏晚晴赠你同心紫玉簪");
                    Story.toLocation(g);
                })
                .choice("替她把玉簪戴上", "苏晚晴好感 ↑↑↑", true, () -> {
                    p.items[42]++;
                    p.affSu += 25;
                    p.log("在洱海边为苏晚晴戴上了玉簪");
                    Story.show(g, new Ev("支线 · 同心")
                            .say("苏晚晴", "……傻子。")
                            .narr("她低着头，耳根红透了。")
                            .then(back(g)));
                });
    }

    // ================= 支线：镖银失踪 =================

    private static Ev sq3a(Game g) {
        return new Ev("支线 · 镖银失踪")
                .narr("临安威远镖局的大门紧闭。老镖头满面愁容地把你请进后堂。")
                .say("镖头", "实不相瞒，镖局押送的两千两官银，在太湖被水寨的人劫了。三天之内追不回来，镖局上下都得吃官司。")
                .say("镖头", "少侠若能替我们夺回镖银，必有重谢！")
                .narr("（支线：在太湖水寨击败四名水寨喽啰）")
                .then(back(g));
    }

    private static Ev sq3b(Game g) {
        final Player p = g.player;
        return new Ev("支线 · 镖银失踪")
                .narr("你在水寨的仓房里找到了那几口贴着官府封条的银箱。四下无人。")
                .choice("原数奉还镖局", "侠义 +5 · 赏银 200 两", true, () -> {
                    p.justice += 5;
                    p.silver += 200;
                    p.log("夺回镖银，原数奉还威远镖局");
                    g.toast("镖局酬谢 200 两。侠义 +5");
                    Story.toLocation(g);
                })
                .choice("截下三成再还", "银两 +600 · 侠义 -8", true, () -> {
                    p.justice -= 8;
                    p.silver += 600;
                    p.log("夺回镖银，私吞了三成");
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("银两 +600。侠义 -8");
                    Story.toLocation(g);
                });
    }

    // ================= 支线：铸剑 =================

    private static Ev sq4a(Game g) {
        return new Ev("支线 · 铸剑")
                .narr("襄阳城西的铁匠铺里，一个须发皆白的老人正抡锤打铁。他抬头看了你一眼，又看了看你的兵器。")
                .say("铸剑师", "好苗子，配了一件凡铁，可惜了。天山雪狼窝里，有一块天外陨铁。你去取来，老夫给你铸一件真正的神兵。")
                .narr("（支线：在天山雪岭击败三头雪狼）")
                .then(back(g));
    }

    private static Ev sq4b(Game g) {
        return new Ev("支线 · 铸剑")
                .narr("雪狼窝深处，一块乌黑的陨铁半埋在冰层之中，入手冰凉，沉重异常。")
                .narr("（支线：回襄阳，将陨铁交给老铸剑师）")
                .then(back(g));
    }

    private static Ev sq4c(Game g) {
        final Player p = g.player;
        Ev e = new Ev("支线 · 神兵出炉")
                .narr("老铸剑师把陨铁投入炉中，三天三夜没有合眼。")
                .say("铸剑师", "陨铁只够铸一件。你要什么？");
        int[] ids = {43, 44, 45, 46};
        String[] kinds = {"剑", "刀", "棍", "拳套"};
        for (int i = 0; i < ids.length; i++) {
            final int id = ids[i];
            e.choice("铸一" + (i == 3 ? "副" : (i == 2 ? "根" : "柄")) + kinds[i] + "：" + GameData.ITEMS[id].name,
                    GameData.ITEMS[id].statText(), true, () -> {
                        p.items[id]++;
                        p.log("老铸剑师以天山陨铁为你铸成" + GameData.ITEMS[id].name);
                        g.audio.sfx(Audio.SFX_EQUIP);
                        Story.show(g, new Ev("支线 · 神兵出炉")
                                .narr("淬火的一刹那，铁匠铺里白气冲天。老人捧着新铸的兵器，像捧着自己的孩子。")
                                .say("铸剑师", "拿去吧。别让它沾上无辜者的血。")
                                .narr("（获得：" + GameData.ITEMS[id].name + "）")
                                .then(back(g)));
                    });
        }
        return e;
    }

    // ================= 支线：影阁密信 =================

    private static Ev sq5a(Game g) {
        return new Ev("支线 · 影阁密信")
                .narr("你从影阁信使身上搜出一封蜡封的密信。信上的字歪歪扭扭，像是某种暗语，一个也看不懂。")
                .narr("（支线：回襄阳，找人破译密信）")
                .then(back(g));
    }

    private static Ev sq5b(Game g) {
        final Player p = g.player;
        Ev e = new Ev("支线 · 破译");
        if (p.has("helped_scholar")) {
            e.narr("你在襄阳的书肆里，竟又遇见了那个曾受你资助的书生。他如今在府衙里做了文书。")
                    .say("书生", "恩公！这种暗语我见过，是按《千字文》的顺序换字。小生这就替您解开！");
        } else {
            e.narr("你在襄阳城里四处打听，花了足足五十两银子，才找到一个懂暗语的老账房。");
        }
        return e.narr("密信上写着：「阁主钧令：待黑木崖正邪两败俱伤之时，收网。」")
                .narr("落款处，画着一枚小小的佛珠、一柄拂尘、一串念珠和一根竹杖——四派信物，竟都在上面。（获得影阁线索：影阁密信）")
                .then(() -> {
                    if (!p.has("helped_scholar")) {
                        p.silver = Math.max(0, p.silver - 50);
                    }
                    p.set("clue_letter");
                    p.log("破译影阁密信：待正邪两败俱伤之时收网");
                    Story.toLocation(g);
                });
    }

    // ================= 客栈打听 =================

    static Ev tavern(Game g) {
        final Player p = g.player;
        String tip;
        Story.Stage s = Story.current(p);
        int loc = s.loc(p);
        switch (s.type) {
            case Story.T_KILL:
            case Story.T_BOSS:
                tip = "客官要去" + GameData.LOCATIONS[loc].name + "？那地方得一层一层往里摸，摸到最深处才见得着正主儿。";
                break;
            case Story.T_VISIT:
            case Story.T_TALK:
                tip = "听说" + GameData.LOCATIONS[loc].name + "最近热闹得很，各路人马都往那边赶呢。";
                break;
            case Story.T_FINAL:
                tip = "那地方可是龙潭虎穴，没有九花玉露丸傍身，小的劝您别去。";
                break;
            default:
                tip = "东海蓬莱仙岛，据说岛上的剑仙三十年没出过手了。上一个去挑战的，连剑都没拔出来。";
                break;
        }
        String clueTip = Story.clues(p) < Story.SHADOW_NEED
                ? "还有啊，最近江湖上总有人说起一个叫「影阁」的地方……谁要是能多找几条线索，怕是能揭开天大的秘密。"
                : "您打听的那个「影阁」……小的劝您，别再往下查了。知道得越多，死得越快。";
        return new Ev("客栈 · 打听消息")
                .say("店小二", "（压低声音）客官，您打听的事儿，小的还真知道一些……")
                .say("店小二", tip)
                .say("店小二", clueTip)
                .say("店小二", rumor(g))
                .then(() -> {
                    p.log("在客栈打听到一些江湖消息");
                    g.setScene(new InnScene(g));
                });
    }

    static String rumor(Game g) {
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
        if (s <= Story.S_HEIFENG) {
            return "列位看官！话说那黑风寨主，一口鬼头刀，杀人不眨眼。可他背后有没有人撑腰？嘿嘿，天知道！";
        } else if (s <= Story.S_TOMB) {
            return "要说当今武林，谁人不知慕容盟主？二十年前萧家庄血案之后，盟主收养了一个孤儿，传为美谈呐！";
        } else if (s <= Story.S_C_TAIHU) {
            return "听说苗疆五毒老怪手里关着个老头儿，二十年没见过天日。还有那太湖水寨，银子多得没处花！";
        } else if (s <= Story.S_TIANSHAN) {
            return "天山之巅有雪莲，三十年一开，能解天下奇毒。襄阳的药铺里偶尔也有一两株，贵得吓人！";
        } else if (s <= Story.S_SHADOW) {
            return "正邪决战就在眼前！列位押哪边？我老汉押……嘘，这话可不敢乱说。";
        }
        return "听说东海之上有座蓬莱仙岛，岛上隐居着一位剑仙，手中龙吟剑一出，天下无人能挡！";
    }

    // ================= 奇遇武学 =================

    static void hermit(Game g, int skill) {
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
        e.then(() -> Story.toLocation(g));
        Story.show(g, e);
    }

    // ================= 江湖随机奇遇 =================

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

    static Ev randomEvent(Game g) {
        switch (g.rnd.nextInt(11)) {
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
            case 7:
                return evMasterLetter(g);
            case 8:
                return evAmbush(g);
            case 9:
                return evScholar(g);
            default:
                return evFriend(g);
        }
    }

    private static Ev evBully(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 路见不平")
                .narr("山道旁，几个凶神恶煞的家伙正围着一个卖花的小姑娘推推搡搡，花篮被踢翻在地，花瓣散了一地。")
                .choice("出手教训", "与" + GameData.ENEMIES[foe].name + "交手 · 侠义 +4", true, () -> wildFight(g, foe, () -> {
                    p.justice += 4;
                    g.toast("小姑娘千恩万谢。侠义 +4");
                    p.log("路见不平，救下卖花的小姑娘");
                    Story.toLocation(g);
                }))
                .choice("丢下二十两银子打发他们", "破财消灾 · 侠义 +1", p.silver >= 20, () -> {
                    p.silver -= 20;
                    p.justice += 1;
                    g.toast("恶徒们拿了钱，骂骂咧咧地走了");
                    Story.toLocation(g);
                })
                .choice("视而不见", "侠义 -2", true, () -> {
                    p.justice -= 2;
                    g.toast("身后传来小姑娘的哭声……侠义 -2");
                    Story.toLocation(g);
                });
    }

    private static Ev evWounded(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 魔教伤者")
                .narr("草丛里躺着一个黑衣青年，衣襟上绣着天魔教的火焰纹，腹部一道剑伤还在汩汩渗血。")
                .say("天魔教弟子", "求……求求你……我只是教中的一个伙夫……我不想死……")
                .choice("用金创药救他", "消耗一份金创药 · 苏晚晴好感 +5", p.items[0] > 0, () -> {
                    p.items[0]--;
                    p.affSu += 5;
                    p.justice += 1;
                    g.toast("他千恩万谢地走了，说回教后定会报答");
                    p.log("救治了一名受伤的天魔教弟子");
                    Story.toLocation(g);
                })
                .choice("补上一剑", "侠义 -6", true, () -> {
                    p.justice -= 6;
                    for (String m : p.gainExp(10 + p.level * 3)) {
                        g.toast(m);
                    }
                    g.toast("侠义 -6");
                    Story.toLocation(g);
                })
                .choice("转身离开", "无", true, back(g));
    }

    private static Ev evCaravan(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 商队遇袭")
                .narr("前方传来呼救声，一支商队被强人拦住了去路，车夫倒在血泊中，货物散落一地。")
                .choice("拔刀相助", "与" + GameData.ENEMIES[foe].name + "交手 · 侠义 +3", true, () -> wildFight(g, foe, () -> {
                    int s = 30 + p.level * 10;
                    p.silver += s;
                    p.justice += 3;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("商队酬谢 " + s + " 两。侠义 +3");
                    p.log("救下一支遇袭的商队");
                    Story.toLocation(g);
                }))
                .choice("趁乱顺走几箱货物", "侠义 -6", true, () -> {
                    int s = 40 + p.level * 12;
                    p.silver += s;
                    p.justice -= 6;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("你顺走了价值 " + s + " 两的货物。侠义 -6");
                    Story.toLocation(g);
                })
                .choice("绕道而行", "无", true, back(g));
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
        Story.toLocation(g);
    }

    private static Ev evGamble(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 路边赌局")
                .narr("路边茶棚里，一个独眼汉子摆开了骰盅，吆喝声震天：「押大押小，买定离手！」")
                .choice("押五十两", "五五之数", p.silver >= 50, () -> gamble(g, 50))
                .choice("押两百两", "富贵险中求", p.silver >= 200, () -> gamble(g, 200))
                .choice("不赌", "无", true, back(g));
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
        Story.show(g, new Ev("江湖偶遇 · 奇人")
                .narr("老者吃罢，哈哈一笑，忽然伸手在你背上连拍三掌。你只觉一股暖流游走四肢百骸……")
                .narr("再抬头时，老者已不知去向。（" + gain + "）")
                .then(back(g)));
    }

    private static Ev evOldMan(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 饥饿老者")
                .narr("一位衣衫褴褛的老者坐在路边石头上，有气无力地说：「后生，可有吃的？老头子三天没吃东西了。」")
                .choice("送他一份草药", "消耗一份草药 · 或有奇遇", p.items[12] > 0, () -> {
                    p.items[12]--;
                    oldManReward(g);
                })
                .choice("送他一份金创药", "消耗一份金创药 · 或有奇遇", p.items[0] > 0, () -> {
                    p.items[0]--;
                    oldManReward(g);
                })
                .choice("给他十两银子", "侠义 +1", p.silver >= 10, () -> {
                    p.silver -= 10;
                    p.justice += 1;
                    g.toast("老者道了声谢，蹒跚而去");
                    Story.toLocation(g);
                })
                .choice("不予理会", "无", true, back(g));
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
                    Story.toLocation(g);
                });
    }

    private static Ev evFakeAlliance(Game g) {
        final Player p = g.player;
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 冒名之徒")
                .narr("一伙人打着武林盟的旗号，正在村子里挨家挨户强收「护武钱」，交不出的便砸锅拆屋。")
                .choice("揭穿这些冒牌货", "与" + GameData.ENEMIES[foe].name + "交手 · 侠义 +4", true, () -> wildFight(g, foe, () -> {
                    p.justice += 4;
                    p.silver += 30;
                    g.toast("村民们拍手称快，凑了三十两谢你。侠义 +4");
                    p.log("揭穿了冒充武林盟的恶徒");
                    Story.toLocation(g);
                }))
                .choice("与他们分赃", "银两 +60 · 侠义 -5", true, () -> {
                    p.silver += 60;
                    p.justice -= 5;
                    g.audio.sfx(Audio.SFX_COIN);
                    g.toast("你分到了六十两。侠义 -5");
                    Story.toLocation(g);
                })
                .choice("不管闲事", "无", true, back(g));
    }

    private static Ev evMasterLetter(Game g) {
        final Player p = g.player;
        final String key = "letter_" + p.stage;
        if (p.has(key)) {
            return evBully(g);
        }
        final int item = p.level >= 10 ? 36 : (p.level >= 5 ? 2 : 0);
        final int n = p.level >= 10 ? 1 : 2;
        String advice;
        if (p.justice >= 20) {
            advice = "近来江湖上常有人提起你的侠名，为师甚慰。";
        } else if (p.justice <= -20) {
            advice = "近来听闻你行事乖张，为师寝食难安。望你莫忘下山时的誓言。";
        } else {
            advice = "江湖险恶，凡事三思而后行。";
        }
        return new Ev("江湖偶遇 · 师门来信")
                .narr("一名" + GameData.SECTS[p.sect].name + "的小师弟风尘仆仆地找到了你，递上一封师父的亲笔信和一个包裹。")
                .say("师父", "（信中）" + p.name + "，见字如面。" + advice)
                .say("师父", "（信中）随信附上几样药物，望你保重身体。为师在山上，等你回来。")
                .then(() -> {
                    p.set(key);
                    p.items[item] += n;
                    g.toast("获得：" + GameData.ITEMS[item].name + " ×" + n);
                    p.log("收到师父的来信");
                    Story.toLocation(g);
                });
    }

    private static Ev evAmbush(Game g) {
        final Player p = g.player;
        if (Story.clues(p) == 0 || p.stage < Story.S_TOMB || p.finished) {
            return evCaravan(g);
        }
        final int foe = locEnemy(g);
        return new Ev("江湖偶遇 · 夜半杀机").bgm(Audio.BGM_BATTLE)
                .narr("夜宿荒庙，你忽然惊醒——窗纸上映出几道黑影，刀光一闪，直取你的咽喉！")
                .narr("来人黑巾蒙面，出手狠辣，招招都冲着你怀中的东西而来。")
                .choice("拔剑迎敌", "与蒙面杀手交手", true, () -> wildFight(g, foe, () -> Story.show(g, new Ev("江湖偶遇 · 夜半杀机")
                        .narr("杀手倒地，你从他怀中搜出一块令牌——黑铁所铸，正面一个「影」字。")
                        .me("影阁……看来，有人已经坐不住了。")
                        .then(() -> {
                            p.justice += 1;
                            p.log("夜宿荒庙遭影阁杀手伏击");
                            Story.toLocation(g);
                        }))))
                .choice("破窗而逃", "耗时一日", true, () -> {
                    p.day++;
                    g.toast("你连夜奔逃了数十里，总算甩掉了追兵");
                    Story.toLocation(g);
                });
    }

    private static Ev evScholar(Game g) {
        final Player p = g.player;
        return new Ev("江湖偶遇 · 落难书生")
                .narr("一个书生坐在路边垂泪。原来他进京赶考，盘缠却在半路被人抢了个精光。")
                .say("书生", "十年寒窗，眼看就要……唉，这位侠士，您说读书人的命，怎么就这么苦？")
                .choice("赠他三十两盘缠", "侠义 +3 · 日后或有回报", p.silver >= 30, () -> {
                    p.silver -= 30;
                    p.justice += 3;
                    p.set("helped_scholar");
                    g.toast("书生千恩万谢，说若高中定当报答。侠义 +3");
                    p.log("资助了一位进京赶考的书生");
                    Story.toLocation(g);
                })
                .choice("教他几招防身功夫", "耗时一日 · 修为小增", true, () -> {
                    p.day++;
                    int e = 8 + p.level * 3;
                    for (String m : p.gainExp(e)) {
                        g.toast(m);
                    }
                    g.toast("教学相长，修为 +" + e);
                    Story.toLocation(g);
                })
                .choice("爱莫能助", "无", true, back(g));
    }

    private static Ev evFriend(Game g) {
        final Player p = g.player;
        if (p.has("met_xiao") && p.stage < Story.S_FINAL && g.rnd.nextBoolean()) {
            return new Ev("江湖偶遇 · 故人")
                    .narr("山道转角，一个白衣人正坐在石头上喝酒——竟是萧寒。")
                    .say("萧寒", "来来来，喝一杯！这是我从义父酒窖里偷出来的女儿红，二十年陈酿！")
                    .choice("陪他痛饮", "气血内力全满 · 萧寒好感 +5", true, () -> {
                        p.fullRestore();
                        p.affXiao += 5;
                        g.toast("一醉方休");
                        Story.toLocation(g);
                    })
                    .choice("有事在身，改日再叙", "无", true, back(g));
        }
        if (Story.suAlive(p) && p.affSu >= 0 && p.stage < Story.S_FINAL) {
            return new Ev("江湖偶遇 · 飞鸽传书")
                    .narr("一只通体雪白的信鸽落在你的肩头，脚上绑着一张小小的字条。")
                    .say("苏晚晴", "（字条）听说你最近在" + GameData.LOCATIONS[p.location].name + "一带闯荡。山中多瘴，记得带解毒丹。……才不是担心你。")
                    .then(() -> {
                        p.affSu += 3;
                        p.items[37]++;
                        g.toast("获得：解毒丹");
                        Story.toLocation(g);
                    });
        }
        return evBully(g);
    }
}
