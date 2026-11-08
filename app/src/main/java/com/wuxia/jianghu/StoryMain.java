package com.wuxia.jianghu;

/** 主线剧情：序章（四派各异）、第一章、第二章、第三章三条线路、第四章。 */
final class StoryMain {
    private StoryMain() {
    }

    static Ev build(Game g, String id) {
        switch (id) {
            case "intro":
                return intro(g);
            case "trial_done":
                return trialDone(g);
            case "traitor":
                return traitor(g);
            case "su_meet":
                return suMeet(g);
            case "heifeng":
                return heifeng(g);
            case "lantern":
                return lantern(g);
            case "luoyang":
                return luoyang(g);
            case "arena":
                return arena(g);
            case "arena_win":
                return arenaWin(g);
            case "chilian":
                return chilian(g);
            case "report":
                return report(g);
            case "dali":
                return dali(g);
            case "witness":
                return witness(g);
            case "wuliang":
                return wuliang(g);
            case "huashan":
                return huashan(g);
            case "ledger":
                return ledger(g);
            case "banquet":
                return banquet(g);
            case "oath":
                return oath(g);
            case "c_miao":
                return cMiao(g);
            case "c_taihu":
                return cTaihu(g);
            case "xiangyang":
                return xiangyang(g);
            case "desert":
                return desert(g);
            case "west":
                return west(g);
            case "loulan":
                return loulan(g);
            default:
                return null;
        }
    }

    // ================= 序章 · 四派各异 =================

    private static Ev intro(Game g) {
        final Player p = g.player;
        Ev e = new Ev(Story.STAGES[Story.S_P_TRIAL].chapter).bg(14 + p.sect).bgm(Audio.BGM_TITLE);
        switch (p.sect) {
            case 0:
                e.narr("嵩山少室山，晨钟三响。藏经阁的方向，忽然腾起一缕黑烟。")
                        .say("师父", p.name + "，昨夜有贼人潜入藏经阁，盗走了一部《易筋经》抄本。达摩院已经封山，可山中盗贼尚未肃清。")
                        .say("师父", "你去后山，将那些夜行盗贼拿下。出家人慈悲为怀，能不伤人性命，便莫伤人性命。");
                break;
            case 1:
                e.narr("武当山紫霄宫，钟鼓齐鸣。镇观之宝「真武剑」昨夜失窃，宫中上下一片肃杀。")
                        .say("师父", p.name + "，盗剑之人还在山中。后山出现了许多来历不明的黑衣剑客，你去探个究竟。")
                        .say("师父", "武当弟子，以柔克刚。遇敌莫要逞强，看清了再出手。");
                break;
            case 2:
                e.narr("峨眉金顶，佛光初现。近来山下屡有女弟子失踪，金顶上人心惶惶。")
                        .say("师父", p.name + "，山中出了采花淫贼，还有成群的猿猴受人驱使，袭扰香客。你下山去查查。")
                        .say("师父", "峨眉剑法，轻灵为上。记住，剑是用来护人的，不是用来逞凶的。");
                break;
            default:
                e.narr("洞庭君山，丐帮总舵。污衣、净衣两派积怨已久，最近竟到了动刀子的地步。")
                        .say("师父", p.name + "，净衣派的弟子在后山聚众闹事，还有几个恶丐趁乱打劫。你去把他们镇住。")
                        .say("师父", "都是自家兄弟，教训一顿便是，莫伤了性命。");
                break;
        }
        return e.say("江湖百晓生", "【江湖指南】野外分为若干层，每战胜一场便深入一层；探明全境之后，才能挑战此地的首领。")
                .say("江湖百晓生", "【江湖指南】当前目标写在地点页下方；金色按钮是剧情入口。迷路时，打开「江湖志 → 剧情指引」。")
                .then(() -> {
                    p.log("师门生变，奉命肃清山中贼人");
                    Story.goStage(g, Story.S_P_TRIAL);
                    Story.toLocation(g);
                });
    }

    private static Ev trialDone(Game g) {
        final Player p = g.player;
        Ev e = new Ev(Story.STAGES[Story.S_P_TRIAL].chapter);
        switch (p.sect) {
            case 0:
                e.narr("你擒住最后一名盗贼。那人吓得面如土色，连连磕头。")
                        .say("夜行盗贼", "大师饶命！不关小的事！是……是寺里的慧空大师，许了我们一百两银子……")
                        .me("慧空师兄？他可是达摩院的人……")
                        .say("师父", "（闻讯赶来）阿弥陀佛。慧空已携经书逃往后山塔林。清理门户之事，老衲便交给你了。");
                break;
            case 1:
                e.narr("黑衣剑客倒在松下，嘴角溢血，却仍在狞笑。")
                        .say("黑衣剑客", "真武剑？嘿嘿……早被玄冥子道长带走了……")
                        .me("玄冥子？他不是三年前就被逐出武当的师叔吗？")
                        .say("师父", "孽障，他竟还敢回来！他练的是阴毒的玄冥寒掌，中者寒毒攻心。你去南岩将他拿下，切记小心。");
                break;
            case 2:
                e.narr("采花贼被你点了穴道，瘫在地上抖如筛糠。")
                        .say("采花贼", "饶命！是血手人屠让我们干的……他在舍身崖下抓了好几个师太的弟子……")
                        .say("师父", "血手人屠？那是二十年前被先师打落舍身崖的魔头！他竟没死……你速去舍身崖救人！");
                break;
            default:
                e.narr("闹事的净衣派弟子被你一一放倒，七嘴八舌地嚷了起来。")
                        .say("净衣派弟子", "是彭长老叫我们闹的！他说帮主老糊涂了，要另立新帮主！")
                        .say("师父", "彭长老……他会摄魂术，与人对敌时专看对方的眼睛。你去后山竹林拿他，千万莫与他对视！");
                break;
        }
        return e.narr("（提示：继续探索师门后山，探明全部区域后即可「挑战首领」。）")
                .then(() -> {
                    p.log("查明作乱的幕后主使是门中叛徒" + Sects.TRAITOR_NAME[p.sect]);
                    Story.goStage(g, Story.S_P_TRAITOR);
                    Story.toLocation(g);
                });
    }

    private static Ev traitor(Game g) {
        final Player p = g.player;
        final String tn = Sects.TRAITOR_NAME[p.sect];
        Ev e = new Ev("序章 · 清理门户");
        switch (p.sect) {
            case 0:
                e.narr("慧空跪倒在塔林之中，怀中那部《易筋经》抄本散落一地。")
                        .say(tn, "师弟……我也是身不由己。他们答应我，只要拿到经书，便让我做达摩院首座……");
                break;
            case 1:
                e.narr("玄冥子的寒掌被你破去，真武剑「当啷」一声落在南岩的石台上。")
                        .say(tn, "当年他们逐我下山……那人答应我，只要拿到真武剑，便让我回武当做掌门……");
                break;
            case 2:
                e.narr("血手人屠倒在舍身崖下，被掳的女弟子们终于得救。")
                        .say(tn, "嘿嘿……有人给了我解药，让我回来报仇。那人还说，峨眉山上……有他的人……");
                break;
            default:
                e.narr("彭长老的摄魂术被你破去，他双目流血，委顿在地。")
                        .say(tn, "我……我也是被人摄了魂……那人戴着面具，他说丐帮迟早是他的……");
                break;
        }
        return e.choice("逼问幕后主使", "或许能问出些什么", true, () -> Story.show(g, traitorEnd(g, true)))
                .choice("先替他止血疗伤", "侠义 +2", true, () -> {
                    p.justice += 2;
                    Story.show(g, traitorEnd(g, false));
                });
    }

    private static Ev traitorEnd(Game g, boolean pressed) {
        final Player p = g.player;
        String tn = Sects.TRAITOR_NAME[p.sect];
        Ev e = new Ev("序章 · 影");
        if (pressed) {
            e.me("说！那人是谁？");
        } else {
            e.narr("你封住他的穴道，替他止了血。他怔怔地望着你，眼中满是愧色。");
        }
        return e.say(tn, "影……影阁……阁主他……就在……")
                .narr("话未说完，一枚细如牛毛的黑针从暗处射来，正中他的咽喉。你拔下黑针，针尾刻着一个小小的「影」字。")
                .narr("（获得影阁线索：叛徒遗言）")
                .say("师父", "此事牵连甚广，绝非一门一派之事。你已能独当一面，下山去吧。先到江南临安，那里鱼龙混杂，最能历练人。")
                .say("师父", "记住：侠义二字，不在名门正派的招牌上，而在你的本心。")
                .say("江湖百晓生", "【江湖指南】剧情中的关键抉择会自动存为「节点存档」，可在「系统 → 节点回溯」中读取，改走另一条路。")
                .say("江湖百晓生", "【江湖指南】侠义值、人物好感、证物与影阁线索，将决定你最终走向十二种结局中的哪一种。")
                .then(() -> {
                    p.set("clue_traitor");
                    p.log("叛徒临死前吐出「影阁」二字，随后被灭口");
                    p.log("拜别师父，下山前往临安");
                    Story.goStage(g, Story.S_BAMBOO);
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
                .choice("拔剑相助紫衣少女", "苏晚晴好感 ↑↑ · 侠义 ↑", true,
                        () -> Story.fight(g, 15, () -> Story.show(g, suHelp(g, true)), () -> Story.show(g, suHelp(g, false))))
                .choice("袖手旁观", "江湖恩怨，与我无关", true, () -> Story.show(g, suWatch(g)))
                .choice("协助武林盟擒拿妖女", "武林盟青睐 · 苏晚晴好感 ↓↓", true, () -> Story.show(g, suCapture(g)));
    }

    private static Ev suHelp(Game g, boolean won) {
        final Player p = g.player;
        Ev ev = new Ev("第一章 · 竹林相逢");
        if (won) {
            ev.narr("白衣剑客们见势不妙，丢下一句「你给我等着」，狼狈退走。");
        } else {
            ev.narr("你寡不敌众，眼看就要落败，紫衣少女忽然撒出一把紫烟，拉着你冲出了重围。");
        }
        return ev.say("苏晚晴", "……多谢。不过你可知道，帮了我，便是得罪了整个武林盟。")
                .me("我只知道，以多欺少，不是侠义所为。")
                .say("苏晚晴", "侠义？呵……你这人倒有趣。我叫苏晚晴。这支紫玉簪送你，日后有缘，凭它来寻我。")
                .narr("话音未落，她身形一晃，已消失在竹海深处，只余一缕淡淡的幽香。（获得：紫玉簪）")
                .then(() -> {
                    p.affSu += 25;
                    p.justice += 3;
                    p.items[31]++;
                    p.set("met_su");
                    p.log("竹林中出手救下紫衣少女苏晚晴");
                    Story.goStage(g, Story.S_HEIFENG);
                    Story.toLocation(g);
                });
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
                    Story.goStage(g, Story.S_HEIFENG);
                    Story.toLocation(g);
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
                    Story.goStage(g, Story.S_HEIFENG);
                    Story.toLocation(g);
                });
    }

    private static Ev heifeng(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            Story.goStage(g, Story.S_LANTERN);
            Story.show(g, new Ev("第一章 · 归途")
                    .narr("黑风寨既破，山道上又有了往来的商旅。你忽然想起，临安城的上元灯会，就在这几日了。")
                    .narr("（主线：回到临安城）")
                    .then(() -> Story.toLocation(g)));
        };
        return new Ev("第一章 · 黑风密信")
                .narr("黑风寨主轰然倒地。你在他的卧房中搜出一只铁匣，匣中除了金银细软，还有一封火漆密信。")
                .narr("信上只有寥寥数语：「劫来之货，三成照旧送往洛阳。事成之后，盟中自有安排。」")
                .narr("落款处，是一枚小小的青锋剑形印记。")
                .me("青锋剑印……这不是武林盟的标记吗？")
                .choice("收好密信，日后查证", "获得证物：黑风寨密信", true, () -> {
                    p.set("keep_letter");
                    p.log("在黑风寨得到一封盖有青锋剑印的密信");
                    next.run();
                })
                .choice("交给临安府衙", "侠义 +3 · 赏银 50 两 · 灯会时会有后续", true, () -> {
                    p.justice += 3;
                    p.silver += 50;
                    p.set("letter_to_yamen");
                    g.toast("府衙赏银 50 两");
                    p.log("将黑风寨密信交给了临安府衙");
                    next.run();
                })
                .choice("一把火烧了", "侠义 -2 · 免得惹祸上身", true, () -> {
                    p.justice -= 2;
                    p.log("烧毁了黑风寨的密信");
                    next.run();
                });
    }

    private static Ev lantern(Game g) {
        final Player p = g.player;
        final boolean suOk = p.has("met_su") && p.affSu >= 10;
        Ev e = new Ev("第一章 · 上元灯会").bgm(Audio.BGM_TOWN)
                .narr("上元之夜，临安城万家灯火。河上漂满了莲花灯，桥头的糖人摊前挤满了孩童。");
        if (suOk) {
            e.say("苏晚晴", "（拎着一盏兔子灯，从人群里钻出来）喂，你也在这儿？……一起走走？");
        }
        e.narr("忽然，人群中一阵骚乱——一个黑衣人从屋顶掠下，短刃直取正在观灯的临安知府！");
        if (p.has("letter_to_yamen")) {
            e.say("知府", "救命！他们……他们是为了那封密信而来！");
        }
        return e.choice("护住知府", "与刺客交手 · 侠义 +4", true,
                        () -> Story.fight(g, 76, () -> Story.show(g, lanternGuard(g, true)), () -> Story.show(g, lanternGuard(g, false))))
                .choice("追击刺客", "可能获得影阁线索", true,
                        () -> Story.fight(g, 76, () -> Story.show(g, lanternChase(g, true)), () -> Story.show(g, lanternChase(g, false))))
                .choice("陪苏晚晴看灯", "苏晚晴好感 ↑↑ · 需与她相识且好感 ≥ 10（当前 " + p.affSu + "）", suOk,
                        () -> Story.show(g, lanternSu(g)));
    }

    /** 灯会之后收到英雄帖（只追加页面，结束回调由调用方设置）。 */
    private static Ev invitation(Ev e) {
        return e.narr("灯会过后不久，一名身着青衫的武林盟信使找到了你，双手奉上一张烫金请柬。")
                .say("武林盟信使", "盟主慕容渊将于洛阳召开英雄大会，共商讨伐魔教大计。久闻少侠义举，特邀赴会。");
    }

    private static void toLuoyang(Game g) {
        g.player.log("收到武林盟的英雄帖");
        Story.goStage(g, Story.S_LUOYANG);
        Story.toLocation(g);
    }

    private static Ev lanternGuard(Game g, boolean won) {
        final Player p = g.player;
        final int reward = won ? 100 : 50;
        Ev e = new Ev("第一章 · 上元灯会");
        if (won) {
            e.narr("刺客见一击不中，掷下烟丸遁走。知府惊魂未定，命人取来一百两纹银相谢。");
        } else {
            e.narr("你被刺客一掌击退，所幸巡夜的官兵及时赶到，刺客遁走。知府仍执意赠你五十两。");
        }
        e.say("知府", "少侠救命之恩，本府没齿难忘！")
                .narr("（侠义 +4，银两 +" + reward + "）");
        return invitation(e).then(() -> {
            p.justice += 4;
            p.silver += reward;
            p.set("lantern_guard");
            p.log("上元灯会救下临安知府");
            toLuoyang(g);
        });
    }

    private static Ev lanternChase(Game g, boolean won) {
        final Player p = g.player;
        Ev e = new Ev("第一章 · 上元灯会");
        if (won) {
            e.narr("你在城西的废宅里截住了刺客。他见无路可逃，竟咬破藏在齿间的毒丸自尽。")
                    .narr("你从他身上搜出一块黑铁令牌，正面一个「影」字。");
        } else {
            e.narr("刺客的轻功远在你之上，你只追回了他遗落的一块碎布，上面绣着一个残缺的「影」字。");
        }
        e.narr("（获得影阁线索：灯会刺客的影字令）")
                .narr("回到灯市时，临安知府已身负重伤，被衙役们抬回了府中。");
        return invitation(e).then(() -> {
            p.set("clue_lantern");
            p.log("追击灯会刺客，得到一块「影」字令牌");
            toLuoyang(g);
        });
    }

    private static Ev lanternSu(Game g) {
        final Player p = g.player;
        Ev e = new Ev("第一章 · 上元灯会")
                .narr("你拉着苏晚晴挤出人群，身后传来一阵惊呼，你却没有回头。")
                .say("苏晚晴", "这河灯，要许个愿才灵。……不许偷看！")
                .narr("她蹲在河边，把一盏小小的莲花灯推进水里。灯影摇摇晃晃，漂向了远处。")
                .narr("后来你才听说，临安知府在那一夜遇刺身亡。");
        return invitation(e).then(() -> {
            p.affSu += 15;
            p.justice -= 2;
            p.log("上元灯会与苏晚晴同游，未理会刺客");
            toLuoyang(g);
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
            Story.goStage(g, Story.S_ARENA);
            Story.show(g, new Ev("第二章 · 英雄大会")
                    .say("慕容渊", "明日辰时，老夫在城南设擂。连胜三场者，便是讨伐赤练魔头的先锋！")
                    .narr("（主线：在洛阳城点击金色剧情按钮「英雄擂台」）")
                    .then(() -> Story.toLocation(g)));
        };
        Ev ev = new Ev("第二章 · 英雄大会")
                .narr("洛阳城，武林盟分舵。大厅之中群雄云集，觥筹交错，好不热闹。")
                .narr("主位上那人年约五旬，面如冠玉，三缕长须，一袭青袍，正是当今武林盟主——慕容渊。")
                .say("慕容渊", "诸位！二十年前，天魔教血洗萧家庄，屠戮三十余口，此乃我正道之耻！")
                .say("慕容渊", "如今魔教余孽赤练魔头盘踞终南古墓，以活人练功。哪位少年英雄，愿为武林除此大害？")
                .say("慕容渊", "听闻" + GameData.SECTS[p.sect].name + "前些日子也出了叛徒？唉，这江湖，越来越不太平了。");
        if (p.has("alliance_favor")) {
            ev.say("慕容渊", "这位便是在竹林中助我盟弟子的" + p.name + "少侠吧？好，后生可畏！这枚青锋令你且收下。");
        } else if (p.has("keep_letter")) {
            ev.narr("慕容渊的目光扫过众人，在你身上微微一顿。你下意识按住了怀中那封盖着青锋剑印的密信。");
        }
        return ev.narr("一个白衣青年大笑着走到你身边。他眉目疏朗，腰悬长剑，自有一股洒脱之气。")
                .say("萧寒", "在下萧寒，盟主是我义父。看兄台气度不凡，不如你我交个朋友？")
                .choice("与萧寒结为异姓兄弟", "萧寒好感 ↑↑↑（他日后是否同心，取决于此）", true, () -> Story.show(g, new Ev("第二章 · 金兰之义")
                        .narr("你二人当即在关公像前撮土为香，对天八拜。")
                        .say("萧寒", "从今往后，你我便是兄弟！哈哈，我萧寒无父无母，今日总算多了一个亲人。")
                        .then(() -> {
                            p.affXiao += 30;
                            p.set("sworn");
                            p.log("与萧寒义结金兰");
                            next.run();
                        })))
                .choice("举杯同饮，结伴而行", "萧寒好感 ↑", true, () -> {
                    p.affXiao += 15;
                    p.log("结识盟主义子萧寒");
                    next.run();
                })
                .choice("婉言谢绝，独来独往", "江湖险恶，不轻信于人", true, () -> {
                    p.affXiao += 3;
                    p.log("婉拒了萧寒的结交");
                    next.run();
                });
    }

    private static void healPart(Player p) {
        p.heal(p.maxHp() * 2 / 5);
        p.restoreMp(p.maxMp() * 2 / 5);
    }

    private static Ev arena(Game g) {
        final Player p = g.player;
        return new Ev("第二章 · 英雄擂台")
                .narr("洛阳城南，擂台高筑。台下人山人海，武林盟主慕容渊端坐在看台正中。")
                .narr("第一场，铁掌门主雷震。此人一双铁掌开碑裂石，已在台上连胜七场。")
                .choice("登台打擂", "连战三场，场间恢复四成气血内力", true, () -> Story.fight(g, 67, () -> {
                    healPart(p);
                    Story.show(g, new Ev("第二章 · 英雄擂台")
                            .narr("雷震拱手认输。台下一片叫好声中，第二个人飘然跃上擂台。")
                            .narr("点苍剑客柳随风，剑法如风中柳絮，飘忽难测。")
                            .then(() -> Story.fight(g, 68, () -> arenaFinal(g), () -> Story.show(g, arenaLose(g)))));
                }, () -> Story.show(g, arenaLose(g))))
                .choice("在台下观战", "不出风头 · 萧寒将成为先锋", true, () -> {
                    p.affXiao += 5;
                    p.log("在台下观看英雄擂台");
                    Story.show(g, new Ev("第二章 · 英雄擂台")
                            .narr("萧寒连胜三场，被立为先锋。他跳下擂台，冲你挤了挤眼睛。")
                            .say("萧寒", "古墓之行，可少不了你！")
                            .then(() -> {
                                Story.goStage(g, Story.S_TOMB);
                                Story.toLocation(g);
                            }));
                });
    }

    private static void arenaFinal(Game g) {
        final Player p = g.player;
        healPart(p);
        if (p.has("met_xiao")) {
            Story.show(g, new Ev("第二章 · 英雄擂台")
                    .say("萧寒", "（跃上擂台，抱拳一笑）最后一场，就让我来领教你的高招！点到为止，可别留手！")
                    .then(() -> Story.fight(g, 69, () -> Story.play(g, "arena_win"), () -> Story.show(g, arenaLose(g)))));
        } else {
            Story.play(g, "arena_win");
        }
    }

    private static Ev arenaLose(Game g) {
        final Player p = g.player;
        return new Ev("第二章 · 英雄擂台")
                .narr("你惜败一招，拱手下台。台下仍有不少人为你喝彩。")
                .then(() -> {
                    p.set("arena_lost");
                    p.log("英雄擂台惜败");
                    Story.goStage(g, Story.S_TOMB);
                    Story.toLocation(g);
                });
    }

    private static Ev arenaWin(Game g) {
        final Player p = g.player;
        return new Ev("第二章 · 擂台夺魁")
                .narr("三场全胜！台下掌声雷动。慕容渊亲自走下看台，含笑扶住你的双手。")
                .say("慕容渊", "好！后生可畏！老夫赐你青钢宝剑一口，并立你为讨伐赤练魔头的先锋！")
                .choice("谢盟主厚爱", "获得青钢剑 · 武林盟青睐", true, () -> {
                    p.items[6]++;
                    p.set("alliance_favor");
                    p.log("英雄擂台夺魁，受封先锋");
                    g.toast("获得：青钢剑");
                    Story.goStage(g, Story.S_TOMB);
                    Story.toLocation(g);
                })
                .choice("将先锋之位让给萧寒", "萧寒好感 ↑↑", p.has("met_xiao"), () -> {
                    p.affXiao += 20;
                    p.log("将先锋之位让给了萧寒");
                    Story.goStage(g, Story.S_TOMB);
                    Story.toLocation(g);
                })
                .choice("婉拒赏赐，只求查明古墓真相", "侠义 +2 · 盟主心生芥蒂", true, () -> {
                    p.justice += 2;
                    p.set("alliance_suspect");
                    p.log("婉拒了盟主的赏赐");
                    Story.goStage(g, Story.S_TOMB);
                    Story.toLocation(g);
                });
    }

    private static Ev chilian(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第二章 · 古墓遗言").bgm(Audio.BGM_SAD)
                .narr("赤练魔头倒在血泊之中，却忽然仰天大笑，笑声在古墓中回荡，令人毛骨悚然。")
                .say("赤练魔头", "哈哈哈……正道？好一个正道！你们这些蠢材，全都被慕容渊那个伪君子骗了！")
                .say("赤练魔头", "二十年前的萧家庄……根本不是天魔教干的……是……")
                .narr("话音未落，一枚透骨钉从暗处疾射而来，正中她的眉心。你追出古墓，只见一道青影掠过树梢，转瞬无踪。");
        if (p.affXiao >= 15) {
            ev.say("萧寒", "（气喘吁吁地赶到）没事吧？这魔头死到临头还要挑拨离间，你可别听她胡说。");
        }
        ev.narr("片刻之后，一阵幽香袭来。紫衣少女不知何时已站在古墓门口，月光照着她苍白的脸。");
        if (p.affSu >= 10) {
            ev.say("苏晚晴", "又见面了。你方才也听见了吧？二十年前的真相……我需要一个帮手。");
        } else {
            ev.say("苏晚晴", "是你。哼，我本不想求你们名门正派……可这件事，关系到二十年前的真相。");
        }
        boolean cOk = p.justice <= -15 || p.affSu >= 30;
        return ev.say("苏晚晴", "我爹……就是天魔教主厉天行。他背负血洗萧家庄的罪名二十年，可他从未做过此事。")
                .say("苏晚晴", "当年血案唯一的活口是一位药师，据说被囚禁在苗疆。你可愿随我去查？……或者，随我回黑木崖见我爹？")
                .choice("随苏晚晴南下，追查真相", "→ A 线 · 追查真相（大理 → 苗疆 → 无量山）", true, () -> {
                    p.affSu += 15;
                    p.set("route_truth");
                    p.log("第三章：随苏晚晴南下，追查萧家庄血案（A 线）");
                    Story.goStage(g, Story.S_A_DALI);
                    Story.toLocation(g);
                })
                .choice("妖女之言不可信，回洛阳复命", "→ B 线 · 效忠盟主（华山 → 太湖 → 寿宴）", true, () -> {
                    p.affSu -= 10;
                    p.affXiao += 10;
                    p.justice += 2;
                    p.set("route_alliance");
                    p.log("第三章：拒绝了苏晚晴，回洛阳复命（B 线）");
                    Story.play(g, "report");
                })
                .choice("随她前往黑木崖，投身天魔教", "→ C 线 · 需侠义≤-15 或晚晴好感≥30（" + p.justice + "/" + p.affSu + "）", cOk, () -> {
                    p.affSu += 10;
                    p.justice -= 5;
                    p.set("route_demon");
                    p.set("join_demon");
                    p.log("第三章：随苏晚晴前往黑木崖，投身天魔教（C 线）");
                    Story.goStage(g, Story.S_C_OATH);
                    Story.toLocation(g);
                });
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
        return ev.narr("慕容渊命人取来二百两纹银，作为盘缠。")
                .then(() -> {
                    p.silver += 200;
                    Story.goStage(g, Story.S_B_HUASHAN);
                    g.goLocation(3);
                });
    }

    // ================= 第三章 · A 追查真相 =================

    private static Ev dali(Game g) {
        final Player p = g.player;
        return new Ev("第三章 · 苍山洱海")
                .narr("大理城，苍山如黛，洱海如镜。苏晚晴带你走进一家临水的僻静茶馆。")
                .say("苏晚晴", "五毒老怪的巢穴，就在城外苗疆毒林的最深处。那里瘴气弥漫，你须多备些解毒丹。")
                .narr("茶馆里的说书人正讲到「萧家庄血案」，说得唾沫横飞，满堂喝彩。苏晚晴听着，指节捏得发白。")
                .me("你恨武林盟吗？")
                .say("苏晚晴", "我恨的不是谁，是谎言。二十年了，我爹被千夫所指，却从不辩解一句。我只想知道，他为什么不说。")
                .choice("我陪你查清真相", "苏晚晴好感 ↑ · 侠义 +2", true, () -> {
                    p.affSu += 10;
                    p.justice += 2;
                    p.log("在大理答应苏晚晴，一同查清真相");
                    Story.goStage(g, Story.S_A_MIAO);
                    Story.toLocation(g);
                })
                .choice("若真相对你爹不利呢？", "实话实说", true, () -> Story.show(g, new Ev("第三章 · 苍山洱海")
                        .say("苏晚晴", "……那我也认。我要的只是真相，不是一个干干净净的爹。")
                        .narr("她别过脸去，望着窗外的洱海，许久没有说话。")
                        .then(() -> {
                            p.affSu += 5;
                            Story.goStage(g, Story.S_A_MIAO);
                            Story.toLocation(g);
                        })));
    }

    private static Ev witness(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第三章 · 二十年前").bgm(Audio.BGM_SAD)
                .narr("五毒老怪死后，你们在毒林深处的石牢里，找到了一个形容枯槁、双腿尽废的老人。")
                .say("老药王", "你们……是来杀我灭口的吗？二十年了，他们终于不想留我这条老命了？")
                .say("苏晚晴", "老人家别怕，我们是来求真相的。二十年前的萧家庄，究竟发生了什么？")
                .say("老药王", "那一夜，我奉命为萧庄主疗伤。三更时分，一群黑衣人杀入庄中，见人便杀，鸡犬不留……")
                .say("老药王", "为首之人使的是慕容家的独门剑法「流云剑」。萧庄主临死前扯下了他的面巾——正是慕容渊！")
                .say("老药王", "可还有一件事，老夫二十年来想不明白：慕容渊身后，始终站着一个戴青铜面具的人。慕容渊对他……毕恭毕敬。");
        if (p.has("met_xiao")) {
            ev.say("老药王", "慕容渊杀了萧家满门，却抱走了襁褓中的小公子，收为义子……");
            ev.me("襁褓中的小公子……难道是萧寒？！");
        }
        return ev.say("老药王", "老夫写了一份血书，藏在这石牢的墙缝里二十年。你们拿去吧。（获得证物：老药王血书）")
                .narr("话音刚落，林中传来一声尖利的哨响。老药王脸色大变：「是影阁的人……他们来了！」")
                .say("苏晚晴", "我得回黑木崖把这一切告诉爹爹。你带老人家去无量山避一避，那里有他的故人。我们襄阳再会！")
                .then(() -> {
                    p.set("has_testimony");
                    p.justice += 5;
                    p.affSu += 10;
                    p.log("在苗疆救出老药王，得知慕容渊身后还有一个面具人");
                    Story.goStage(g, Story.S_A_WULIANG);
                    Story.toLocation(g);
                });
    }

    private static Ev wuliang(Game g) {
        final Player p = g.player;
        return new Ev("第三章 · 无量玉壁")
                .narr("白无常的勾魂索断成数截，他靠在玉壁之下，惨白的脸上竟露出一丝笑意。")
                .say("白无常", "你以为……杀了我就完了？影阁的眼线遍布天下……阁主他……就在你最亲近的地方……")
                .narr("他身子一软，再无声息。玉壁上的剑影微微晃动，仿佛在嘲笑什么。（获得影阁线索：白无常遗言）")
                .say("老药王", "孩子，多亏了你。老夫这把骨头……你说，接下来该怎么办？")
                .choice("请老人家留在无量山避祸", "侠义 +3 · 老药王安全", true, () -> {
                    p.justice += 3;
                    p.set("clue_wuliang");
                    p.log("在无量山击败白无常，老药王留山避祸");
                    Story.goStage(g, Story.S_XIANGYANG);
                    Story.toLocation(g);
                })
                .choice("请老人家随我去襄阳当众作证", "获得证物：老药王亲证", true, () -> {
                    p.set("witness_alive");
                    p.set("clue_wuliang");
                    p.log("在无量山击败白无常，老药王愿随你去襄阳作证");
                    Story.goStage(g, Story.S_XIANGYANG);
                    Story.toLocation(g);
                });
    }

    // ================= 第三章 · B 效忠盟主 =================

    private static Ev huashan(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            Story.goStage(g, Story.S_B_TAIHU);
            Story.show(g, new Ev("第三章 · 盟主密令")
                    .narr("你刚下华山，武林盟的飞鸽传书便到了。")
                    .say("慕容渊", "（信中）太湖水寨龙王勾结魔教，劫掠商船，罪大恶极。望少侠速往剿灭，务必……不留活口。")
                    .then(() -> Story.toLocation(g)));
        };
        Ev ev = new Ev("第三章 · 华山剑痕")
                .narr("思过崖上，最后一名魔教护法倒下，怀中掉出一卷残破的剑谱和一封书信。")
                .narr("你展开书信，不禁大吃一惊——信中竟是慕容渊的笔迹：「护法辛苦。剑谱到手，照旧送往总坛，酬金加倍。」")
                .me("盟主……竟与魔教护法暗中往来？");
        if (p.has("met_xiao")) {
            ev.say("萧寒", "不可能！义父一生光明磊落，这定是魔教伪造书信，故意陷害他！");
        }
        return ev.choice("呈交盟主，当面问个明白", "失去证物 · 武林盟青睐", true, () -> Story.show(g, new Ev("第三章 · 华山剑痕")
                        .narr("慕容渊看完书信，面不改色，随手将它投入了烛火之中。")
                        .say("慕容渊", "魔教惯用离间之计，少侠莫要上当。此事，就不必再提了。")
                        .then(() -> {
                            p.justice += 1;
                            p.affXiao += 5;
                            p.set("alliance_favor");
                            p.log("将思过崖的书信交给了慕容渊");
                            next.run();
                        })))
                .choice("暗中收起书信", "获得证物：思过崖书信", true, () -> {
                    p.set("huashan_letter");
                    p.log("暗中收起了慕容渊写给魔教护法的书信");
                    next.run();
                })
                .choice("撕毁书信，此事与我无关", "侠义 -3", true, () -> {
                    p.justice -= 3;
                    p.log("撕毁了思过崖的书信");
                    next.run();
                });
    }

    private static Ev ledger(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            Story.goStage(g, Story.S_B_BANQUET);
            Story.show(g, new Ev("第三章 · 寿宴请柬")
                    .narr("慕容渊传书：五十寿辰将至，请你回洛阳赴宴。")
                    .narr("（主线：在洛阳城点击金色剧情按钮「盟主寿宴」）")
                    .then(() -> Story.toLocation(g)));
        };
        return new Ev("第三章 · 太湖账本")
                .narr("水寨龙王跪倒在地，连连叩首，额头磕得鲜血直流。")
                .say("水寨龙王", "少侠饶命！我不过是替人管账的……这些年水寨劫来的银子，九成都送进了武林盟的金库啊！")
                .say("水寨龙王", "慕容渊要灭我的口，才诬我勾结魔教！账本……账本就藏在龙王庙的神像底下！")
                .choice("饶他一命，取走账本", "获得证物：太湖账本 · 侠义 +3", true, () -> {
                    p.set("has_ledger");
                    p.justice += 3;
                    g.toast("获得证物：太湖账本");
                    p.log("饶过水寨龙王，取得武林盟的黑账");
                    next.run();
                })
                .choice("斩草除根，再取账本", "获得证物：太湖账本 · 侠义 -5", true, () -> {
                    p.set("has_ledger");
                    p.justice -= 5;
                    g.toast("获得证物：太湖账本");
                    p.log("斩杀水寨龙王，取得武林盟的黑账");
                    next.run();
                })
                .choice("收下金银，替他保密", "银两 +500 · 侠义 -8（需侠义 ≤ -15）", p.justice <= -15, () -> {
                    p.silver += 500;
                    p.justice -= 8;
                    p.log("收下水寨龙王的五百两封口银");
                    next.run();
                });
    }

    private static Runnable afterBanquet(Game g) {
        return () -> {
            Story.goStage(g, Story.S_XIANGYANG);
            Story.show(g, new Ev("第三章 · 风起襄阳")
                    .narr("寿宴之后，慕容渊传令天下：各派精英齐聚襄阳，择日讨伐黑木崖。")
                    .then(() -> Story.toLocation(g)));
        };
    }

    private static Ev banquet(Game g) {
        final Player p = g.player;
        final Runnable next = afterBanquet(g);
        return new Ev("第三章 · 盟主寿宴")
                .narr("盟主府张灯结彩，贺客如云。酒过三巡，慕容渊将你请入后堂，屏退了左右。")
                .say("慕容渊", "明日，天魔教的使者要来洛阳议和。来的人……是厉天行的女儿，苏晚晴。")
                .say("慕容渊", "议和是假，探听虚实是真。老夫要你在明日的和谈席上，一剑取她性命。事成之后，你便是武林盟的副盟主。")
                .choice("奉命行事", "与苏晚晴交手 · 侠义 ↓↓", true, () -> Story.fight(g, 71,
                        () -> Story.show(g, banquetKill(g)), () -> Story.show(g, new Ev("第三章 · 盟主寿宴")
                                .narr("你败了。苏晚晴收剑入鞘，深深看了你一眼，转身离去。")
                                .then(() -> {
                                    p.affSu -= 20;
                                    p.log("奉命刺杀苏晚晴，却败在她的剑下");
                                    next.run();
                                }))))
                .choice("暗中提醒苏晚晴", "苏晚晴好感 ↑↑ · 盟主起疑（需与她相识）", p.has("met_su"), () -> Story.show(g, new Ev("第三章 · 盟主寿宴")
                        .narr("当夜，你托人给苏晚晴送去一张字条：「洛阳有诈，勿来。」")
                        .narr("第二天，天魔教的使者没有出现。慕容渊什么也没说，只是看你的眼神，多了几分寒意。")
                        .then(() -> {
                            p.affSu += 25;
                            p.justice += 5;
                            p.set("warned_su");
                            p.set("alliance_suspect");
                            p.log("暗中提醒苏晚晴，使她免于一死");
                            next.run();
                        })))
                .choice("夜探盟主密室", "可能获得证物与影阁线索", true, () -> Story.show(g, new Ev("第三章 · 密室")
                        .narr("三更时分，你潜入盟主府的密室。烛影摇红，慕容渊竟跪在地上，对着一个戴青铜面具的人连连叩首。")
                        .say("面具人", "那个丫头若死在你手里，厉天行必定倾巢而出。正邪拼个两败俱伤……才是本座想看的。")
                        .narr("面具人似有所觉，目光扫向你藏身的横梁。你屏住呼吸，许久，他才拂袖而去。")
                        .narr("（获得证物：寿宴密谈；获得影阁线索：密室中的面具人）")
                        .then(() -> {
                            p.set("banquet_secret");
                            p.set("clue_banquet");
                            p.log("夜探密室，看见慕容渊跪拜一个青铜面具人");
                            next.run();
                        })));
    }

    private static Ev banquetKill(Game g) {
        final Player p = g.player;
        final Runnable next = afterBanquet(g);
        return new Ev("第三章 · 盟主寿宴").bgm(Audio.BGM_SAD)
                .narr("苏晚晴的短剑被你挑飞，她踉跄着跌坐在地，抬头望着你，眼中没有恐惧，只有失望。")
                .say("苏晚晴", "原来……你也是他们的人。")
                .choice("一剑刺下", "苏晚晴将会死去 · 侠义 -15", true, () -> {
                    p.set("su_dead");
                    p.justice -= 15;
                    p.set("alliance_favor");
                    p.log("在议和席上刺杀了苏晚晴");
                    next.run();
                })
                .choice("终究下不了手", "放她离去 · 苏晚晴好感 ↓↓", true, () -> {
                    p.affSu -= 30;
                    p.log("刺杀苏晚晴时终究下不了手");
                    next.run();
                });
    }

    // ================= 第三章 · C 投身魔教 =================

    private static Ev oath(Game g) {
        final Player p = g.player;
        Ev e = new Ev("第三章 · 黑木崖盟誓").bgm(Audio.BGM_BOSS)
                .narr("黑木崖顶，天魔殿。殿中教众分列两旁，杀气森森。苏晚晴领着你，一步一步走上殿前的石阶。")
                .say("厉天行", "（打量着你）名门正派的弟子，来我天魔教做什么？");
        if (p.affSu >= 30) {
            e.say("苏晚晴", "爹，他是我带来的人。古墓里那魔头临死前说的话，他也听见了。");
        }
        final Runnable next = () -> {
            p.set("clue_oath");
            Story.goStage(g, Story.S_C_MIAO);
            Story.show(g, new Ev("第三章 · 天魔令")
                    .say("厉天行", "好。教中叛徒五毒老怪盗走了天魔令，逃往苗疆毒林。你替本座把它夺回来。")
                    .then(() -> Story.toLocation(g)));
        };
        return e.say("厉天行", "二十年前那一夜，我也在萧家庄。我赶去救萧庄主，却看见火光里站着一个戴青铜面具的人……那人，不是慕容渊。")
                .say("厉天行", "这些年我不辩解，是因为我在等，等那个面具人自己露出马脚。（获得影阁线索：厉天行的回忆）")
                .say("厉天行", "你若真心入教，便当着众人的面立誓吧。")
                .choice("歃血为盟，誓死效忠", "侠义 -10 · 苏晚晴好感 ↑", true, () -> {
                    p.justice -= 10;
                    p.affSu += 10;
                    p.set("demon_loyal");
                    p.log("在黑木崖歃血为盟，誓死效忠天魔教");
                    next.run();
                })
                .choice("假意投诚，暗中收集情报", "成为双面卧底", true, () -> {
                    p.set("double_agent");
                    p.log("假意投诚天魔教，暗中收集情报");
                    next.run();
                })
                .choice("我只为晚晴而来", "苏晚晴好感 ↑↑（需好感 ≥ 30）", p.affSu >= 30, () -> {
                    p.affSu += 20;
                    p.set("for_su");
                    p.log("在天魔殿上当众说：只为苏晚晴而来");
                    next.run();
                });
    }

    private static Ev cMiao(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            p.items[33]++;
            Story.goStage(g, Story.S_C_TAIHU);
            Story.show(g, new Ev("第三章 · 截粮令")
                    .say("厉天行", "（传书）天魔令既已夺回，你记一大功。武林盟的粮船正经太湖北上，去把它截了。")
                    .then(() -> Story.toLocation(g)));
        };
        return new Ev("第三章 · 夺回天魔令")
                .narr("五毒老怪倒在毒潭边，怀中滚出一块黑铁令牌——正是天魔令。（获得：天魔令）")
                .narr("毒林深处的石牢里，还关着一个形容枯槁的老人。他看见你腰间的令牌，浑身一颤。")
                .say("老药王", "天魔教的人……你们终于来灭口了吗？二十年前的事，老夫一个字都没有说出去啊！")
                .choice("放了他，听他说完", "获得证物：老药王血书 · 侠义 +3", true, () -> Story.show(g, new Ev("第三章 · 二十年前")
                        .say("老药王", "你……你不杀我？好，好……那一夜，杀进萧家庄的人，为首的是慕容渊。")
                        .say("老药王", "可慕容渊身后，还站着一个戴青铜面具的人……这血书，你拿去吧。")
                        .then(() -> {
                            p.set("has_testimony");
                            p.justice += 3;
                            p.log("在苗疆放走老药王，得到他的血书");
                            next.run();
                        })))
                .choice("奉教主之命，杀人灭口", "侠义 -8", true, () -> {
                    p.justice -= 8;
                    p.log("在苗疆杀了老药王");
                    next.run();
                })
                .choice("不予理会，带走天魔令", "无", true, () -> {
                    p.log("夺回天魔令，未理会石牢中的老人");
                    next.run();
                });
    }

    private static Ev cTaihu(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            Story.goStage(g, Story.S_XIANGYANG);
            Story.show(g, new Ev("第三章 · 潜入襄阳")
                    .say("厉天行", "（传书）武林盟大军正在襄阳集结。你潜入城中，替本座看看慕容渊在打什么主意。")
                    .then(() -> Story.toLocation(g)));
        };
        return new Ev("第三章 · 太湖截粮")
                .narr("太湖之上，武林盟的粮船燃起冲天大火。被俘的押运弟子跪成一排，瑟瑟发抖。")
                .say("天魔教弟子", "右使大人，这些正道狗贼怎么处置？")
                .choice("尽数斩杀，以儆效尤", "侠义 -10", true, () -> {
                    p.justice -= 10;
                    p.log("太湖截粮，斩杀了全部俘虏");
                    next.run();
                })
                .choice("放他们回去报信", "侠义 +5", true, () -> {
                    p.justice += 5;
                    p.log("太湖截粮，放走了俘虏");
                    next.run();
                })
                .choice("审问押运头目", "获得证物：太湖密令", true, () -> Story.show(g, new Ev("第三章 · 太湖密令")
                        .say("押运头目", "别杀我！我说……这批粮草是幌子，船舱底下藏着盟主的密令！")
                        .narr("密令上写着：「屠灭黑木崖下三村，嫁祸魔教，以激天下公愤。」落款，正是慕容渊。（获得证物：太湖密令）")
                        .then(() -> {
                            p.set("taihu_orders");
                            p.log("太湖截粮，搜出慕容渊嫁祸魔教的密令");
                            next.run();
                        })));
    }

    // ================= 第四章 =================

    private static Ev xiangyang(Game g) {
        final Player p = g.player;
        Ev ev = new Ev("第四章 · 襄阳烽火");
        if (p.has("route_demon")) {
            ev.narr("你乔装混入襄阳城。城头旌旗猎猎，武林盟各派人马已在此集结。");
        } else {
            ev.narr("襄阳城头旌旗猎猎，武林盟各派人马已在此集结。慕容渊立于城楼之上，意气风发。");
        }
        ev.say("慕容渊", "诸位，天魔教覆灭在即！只是……我儿萧寒三日前独自追查魔教踪迹，深入大漠，至今未归。");
        if (!p.has("route_demon")) {
            ev.say("慕容渊", p.name + "少侠武艺高强，可否替老夫走一趟大漠孤城，寻回寒儿？");
        }
        ev.narr("他说这话时神情关切，可不知为何，你总觉得那目光深处藏着一丝冷意。");
        if (p.has("has_testimony")) {
            ev.narr("你摸了摸怀中的血书，没有作声。");
        } else if (Story.evidence(p) > 0) {
            ev.narr("你想起怀中的证物，心中疑云更重。");
        }
        return ev.choice("前往大漠寻找萧寒", "→ 大漠孤城", true, () -> {
                    p.affXiao += 5;
                    p.log("前往大漠寻找萧寒");
                    Story.goStage(g, Story.S_DESERT);
                    Story.toLocation(g);
                })
                .choice("要我去也行，先付五百两", "银两 +500 · 侠义 -3（需侠义 ≤ -10）", p.justice <= -10 && !p.has("route_demon"), () -> {
                    p.silver += 500;
                    p.justice -= 3;
                    p.log("向慕容渊索要了五百两酬金");
                    Story.goStage(g, Story.S_DESERT);
                    Story.toLocation(g);
                })
                .choice("向教主密报大军动向", "侠义 -3 · C 线专属", p.has("route_demon"), () -> {
                    p.justice -= 3;
                    p.set("demon_favor");
                    p.log("向厉天行密报了武林盟大军的动向");
                    Story.goStage(g, Story.S_DESERT);
                    Story.toLocation(g);
                });
    }

    private static Ev desert(Game g) {
        final Player p = g.player;
        final Runnable next = () -> {
            p.set("blade_confess");
            Story.play(g, "west");
        };
        Ev ev = new Ev("第四章 · 大漠孤烟")
                .narr("大漠狂刀倒在黄沙之中。你在孤城的地牢深处，找到了遍体鳞伤的萧寒。")
                .say("萧寒", "咳……你来了……我查到，这伙马贼背后……竟然……")
                .say("大漠狂刀", "嘿嘿……小子，你以为老子是魔教的人？老子是拿了慕容渊的银子，来取你萧寒的命！")
                .say("大漠狂刀", "谁让你到处打听自己的身世……慕容渊……可不想留着你……")
                .narr("大漠狂刀头一歪，再没了声息。萧寒脸色惨白，久久说不出一句话来。（获得证物：大漠狂刀遗言）");
        if (p.has("has_testimony")) {
            ev.choice("将老药王的血书交给萧寒", p.affXiao >= 20 ? "他会相信你 · 萧寒同心" : "他未必信你（萧寒好感 < 20）", true, () -> {
                Ev r = new Ev("第四章 · 血书");
                if (p.affXiao >= 20) {
                    r.narr("萧寒读完血书，双手剧烈颤抖，一口鲜血喷在了黄沙之上。")
                            .say("萧寒", "二十年……我竟认贼作父二十年！他养我、教我，原来只是为了……心安？！")
                            .say("萧寒", "多谢你告诉我。黑木崖上，我要亲口问他一句：为什么。")
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
                Story.show(g, r);
            });
            ev.choice("此事关系重大，暂且瞒着他", "等待更好的时机", true, () -> {
                p.log("在大漠救出萧寒，没有告诉他真相");
                next.run();
            });
        } else {
            ev.choice("劝他提防慕容渊", p.affXiao >= 30 ? "他会听进去 · 萧寒同心" : "他未必信你（萧寒好感 < 30）", true, () -> {
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
            ev.choice("死人之言，不必当真", "萧寒好感 ↑", true, () -> {
                p.affXiao += 5;
                p.log("在大漠救出萧寒");
                next.run();
            });
        }
        return ev;
    }

    private static Ev west(Game g) {
        final Player p = g.player;
        Ev e = new Ev("第四章 · 西出阳关")
                .narr("你在大漠狂刀的尸身上搜出一张羊皮地图。地图上用朱砂圈着一处地方——西域楼兰古城，旁边画着一个小小的「影」字。");
        if (p.has("met_xiao")) {
            e.say("萧寒", "楼兰……听说那里早已被黄沙掩埋，怎会有人在那里落脚？");
        }
        return e.choice("西出阳关，追查影阁", "→ 楼兰追影（可获得影阁线索）", true, () -> {
                    p.log("决定西出阳关，前往楼兰古城");
                    Story.goStage(g, Story.S_LOULAN);
                    Story.toLocation(g);
                })
                .choice("先护送萧寒回襄阳", "→ 直接进入第五章", true, () -> {
                    p.log("护送萧寒回到襄阳");
                    g.player.location = 10;
                    Story.play(g, "storm");
                });
    }

    private static Ev loulan(Game g) {
        final Player p = g.player;
        return new Ev("第四章 · 楼兰追影")
                .narr("黑无常的锁魂链寸寸断裂。他倚着残破的佛塔，咳出一口黑血。")
                .say("黑无常", "白无常那个蠢货……没告诉过你吗？阁主早就在你身边了……你们师门的人，一个比一个蠢。")
                .narr("他狂笑着，咬碎了藏在牙中的毒丸。（获得影阁线索：黑无常遗言）")
                .me("我们师门……难道影阁之主，竟是本门中人？")
                .narr("你带着满腹疑云，日夜兼程赶回了襄阳。")
                .then(() -> {
                    p.set("clue_loulan");
                    p.set("knows_sect_traitor");
                    p.log("在楼兰击败黑无常，得知影阁之主竟出自师门");
                    p.location = 10;
                    p.set("v10");
                    Story.play(g, "storm");
                });
    }
}
