package com.wuxia.jianghu;

/** 第五章（师门与苏晚晴的两难）、终章黑木崖、隐藏终章归墟，以及十二种结局。 */
final class StoryEnd {
    private StoryEnd() {
    }

    static Ev build(Game g, String id) {
        switch (id) {
            case "storm":
                return storm(g);
            case "sect_saved":
                return sectSaved(g);
            case "lotus":
                return lotus(g);
            case "climax":
                return climax(g);
            case "after_expose":
                return afterExpose(g);
            case "demon_choice":
                return demonChoice(g);
            case "shadow_reveal":
                return shadowReveal(g);
            case "shadow_final":
                return shadowFinal(g);
            case "after_shadow":
                return afterShadow(g);
            default:
                return null;
        }
    }

    // ================= 第五章 · 风雨欲来 =================

    private static Ev storm(Game g) {
        final Player p = g.player;
        final boolean suInDanger = Story.suAlive(p) && p.affSu >= 10 && !p.has("saved_su");
        final boolean hasLotus = p.items[4] > 0;
        final boolean xiao = Story.xiaoFriend(p);
        String sectName = GameData.SECTS[p.sect].name;
        Ev e = new Ev("第五章 · 风雨欲来").bgm(Audio.BGM_SAD)
                .narr("回到襄阳的当夜，急信一封接一封地送到了你的手中。")
                .narr("第一封来自" + sectName + "：「影阁大举进犯，师门危在旦夕，速回！」");
        if (suInDanger) {
            e.narr("第二封来自苏晚晴的侍女：「小姐回教途中遭蒙面人偷袭，身中玄冥寒毒，唯天山雪莲可解。求少侠相救！」")
                    .me("师门……晚晴……天各一方，我只能去一处。");
            if (hasLotus) {
                e.narr("你摸了摸行囊——那里还有一株天山雪莲。或许，可以托人先送过去。");
            } else if (xiao) {
                e.say("萧寒", "别慌！你去一处，另一处交给我。我萧寒说到做到！");
            }
        } else {
            e.me("师门有难，我岂能坐视！可武林盟大军也已开拔，黑木崖之战一触即发……");
        }
        String sectHint;
        if (suInDanger) {
            sectHint = hasLotus ? "托人送去天山雪莲，两全其美" : (xiao ? "萧寒会替你去天山求药" : "苏晚晴将毒发身亡");
        } else {
            sectHint = "→ 师门告急";
        }
        e.choice("星夜赶回师门", sectHint, true, () -> {
            if (suInDanger) {
                saveSuRemotely(g, hasLotus, xiao);
            }
            p.log("第五章：星夜赶回师门");
            Story.goStage(g, Story.S_SECT);
            Story.toLocation(g);
        });
        if (suInDanger) {
            e.choice("远赴天山求药", xiao ? "萧寒会替你守住师门" : "师门将会陷落", true, () -> {
                p.affSu += 10;
                if (xiao) {
                    p.set("xiao_guard_sect");
                }
                p.log("第五章：远赴天山为苏晚晴求药");
                Story.goStage(g, Story.S_TIANSHAN);
                Story.toLocation(g);
            });
        }
        String finalHint = "师门将会陷落" + (suInDanger && !hasLotus ? "，苏晚晴将毒发身亡" : "");
        e.choice("大局为重，直上黑木崖", finalHint, true, () -> {
            p.set("sect_fallen");
            if (suInDanger) {
                if (hasLotus) {
                    saveSuRemotely(g, true, false);
                } else {
                    p.set("su_dead");
                }
            }
            p.log("第五章：以大局为重，直上黑木崖");
            Story.goStage(g, Story.S_FINAL);
            Story.show(g, new Ev("第五章 · 噩耗").bgm(Audio.BGM_SAD)
                    .narr("你随武林盟大军北上。行至半途，" + GameData.SECTS[p.sect].name + "陷落的消息传来，师父战死山门。")
                    .narr(p.has("su_dead") ? "同一日，苏晚晴毒发身亡的消息，也到了。" : "你攥紧了拳头，一言不发。")
                    .then(() -> Story.toLocation(g)));
        });
        return e;
    }

    private static void saveSuRemotely(Game g, boolean hasLotus, boolean xiao) {
        Player p = g.player;
        if (hasLotus) {
            p.items[4]--;
            p.set("saved_su");
            p.affSu += 15;
            g.toast("天山雪莲已托人送往苏晚晴处");
            p.log("托人将天山雪莲送给中毒的苏晚晴");
        } else if (xiao) {
            p.set("saved_su");
            p.set("xiao_saved_su");
            p.affSu += 5;
            p.affXiao += 5;
            p.log("萧寒替你远赴天山，为苏晚晴求来雪莲");
        } else {
            p.set("su_dead");
            p.log("苏晚晴毒发身亡");
        }
    }

    private static Ev sectSaved(Game g) {
        final Player p = g.player;
        String home = Sects.homeName(p);
        String master = Sects.MASTER[p.sect];
        Ev e = new Ev("第五章 · 师门之战").bg(14 + p.sect)
                .narr("影阁来敌伏诛。" + home + "上下一片狼藉，所幸山门未破，" + master + "身负轻伤，并无大碍。")
                .say("影阁死士", "（临死前）阁主……就在你们之中……你们……都会死……")
                .say("师父", "孩子，你回来得正是时候。为师一直怀疑门中有内鬼，只是……一直不敢往那个人身上想。");
        switch (p.sect) {
            case 0:
                e.say("师父", "达摩院这些年……罢了，罢了。黑木崖之战，你多加小心。");
                break;
            case 1:
                e.say("师父", "玄冥寒掌，本门只有一人练过……不，或许是为师多心了。");
                break;
            case 2:
                e.say("师父", "戒律院的人，近来总在夜里出入藏经楼……你记住为师这句话。");
                break;
            default:
                e.say("师父", "执法长老那边……唉，丐帮的事，一言难尽。");
                break;
        }
        e.narr("（获得影阁线索：师门之战）");
        if (p.has("xiao_saved_su")) {
            e.narr("数日后，萧寒风尘仆仆地赶来，告诉你：苏晚晴已服下雪莲，转危为安。");
        } else if (p.has("su_dead")) {
            e.narr("可你还没来得及喘口气，便收到了苏晚晴毒发身亡的死讯。");
        }
        return e.then(() -> {
            p.set("sect_saved");
            p.set("clue_sect");
            p.set("knows_sect_traitor");
            p.justice += 5;
            p.log("赶回师门击退影阁，师父平安");
            Story.goStage(g, Story.S_FINAL);
            Story.toLocation(g);
        });
    }

    private static Ev lotus(Game g) {
        final Player p = g.player;
        return new Ev("第五章 · 天山雪莲")
                .narr("冰魄老人长叹一声，让开了身后的冰崖。崖顶之上，一株雪莲迎风而立，晶莹如玉。")
                .say("冰魄老人", "老夫守这雪莲三十年，为的是等一个值得的人。罢了，你为救人而来，拿去吧。")
                .narr("你日夜兼程，赶到苏晚晴藏身的山村。她服下雪莲，苍白的脸上终于渐渐有了血色。")
                .say("苏晚晴", "傻子……从天山到这里，你几天没合眼了？")
                .say("苏晚晴", "偷袭我的那个人蒙着面，使的是玄冥掌……可他的身法，像是名门正派的路数。")
                .say("苏晚晴", "这件天蚕宝衣，是我娘留下的。你穿上它。黑木崖上……我不想看到你受伤。（获得：天蚕宝衣）")
                .choice("黑木崖上，我与你同在", "苏晚晴好感 ↑↑", true, () -> {
                    p.affSu += 15;
                    finishLotus(g);
                })
                .choice("保重。", "点到即止", true, () -> finishLotus(g));
    }

    private static void finishLotus(Game g) {
        final Player p = g.player;
        p.set("saved_su");
        p.affSu += 20;
        p.items[28]++;
        p.log("以天山雪莲救回苏晚晴");
        Ev news = new Ev("第五章 · 师门消息");
        if (p.has("xiao_guard_sect")) {
            news.narr("下山时，萧寒的飞鸽传书到了：「师门已守住，影阁的人退了。他们临走时喊了一句：阁主就在你们之中。」")
                    .narr("（获得影阁线索：师门之战）");
        } else {
            news.bgm(Audio.BGM_SAD)
                    .narr("下山时，你收到了" + GameData.SECTS[p.sect].name + "陷落的噩耗。" + Sects.MASTER[p.sect] + "战死山门。")
                    .narr("师父留下一封遗书：「影阁之主……就在本门之中……为师无能，你千万小心。」")
                    .narr("（获得影阁线索：师门之战）");
        }
        news.then(() -> {
            if (p.has("xiao_guard_sect")) {
                p.set("sect_saved");
                p.log("萧寒替你守住了师门");
            } else {
                p.set("sect_fallen");
                p.log("师门陷落，师父战死");
            }
            p.set("clue_sect");
            p.set("knows_sect_traitor");
            Story.goStage(g, Story.S_FINAL);
            Story.toLocation(g);
        });
        Story.show(g, news);
    }

    // ================= 终章 · 黑木崖 =================

    private static Ev climax(Game g) {
        final Player p = g.player;
        int n = Story.evidence(p);
        int c = Story.clues(p);
        Ev e = new Ev("终章 · 黑木崖之巅").bgm(Audio.BGM_BOSS)
                .narr("黑木崖顶，天魔殿前。武林盟大军与天魔教众杀声震天，血染石阶。")
                .narr("天魔教主厉天行负手立于殿前，须发如戟，不怒自威。他的对面，慕容渊青袍猎猎，长剑在手。")
                .say("厉天行", "慕容渊！二十年了，你终于敢上我黑木崖了！")
                .say("慕容渊", "魔头！今日我便替萧家庄三十余口冤魂，讨回一个公道！");
        if (Story.suSaved(p)) {
            e.narr("苏晚晴站在父亲身侧，望向你的目光中满是担忧。");
        } else if (Story.suAlive(p)) {
            e.narr("你看见了苏晚晴。她站在天魔殿的阴影里，脸色苍白，没有看你。");
        }
        if (Story.xiaoFriend(p)) {
            e.say("萧寒", "（走到你身边，低声）无论你作何选择，我都站在你这边。");
        } else if (p.has("met_xiao")) {
            e.narr("萧寒站在慕容渊身后，握剑的手微微发抖。");
        }
        if (c >= Story.SHADOW_NEED) {
            e.narr("你隐隐觉得，这满山的厮杀背后，还有一双眼睛在暗处看着。（影阁线索已集齐，取胜后将开启隐藏终章）");
        }
        e.say("慕容渊", p.name + "！还等什么？随我诛杀魔头！");
        boolean suLove = Story.suSaved(p) && p.affSu >= 70;
        return e.choice("听从盟主号令，诛杀天魔教主", "结局：盟主传人 / 孤剑天涯（苏晚晴好感 ≥ 40）", true,
                        () -> Story.fight(g, 14, () -> afterObey(g), null))
                .choice("当众揭穿慕容渊的真面目", "需证物 ≥ 2（现有 " + n + " 件） · 结局：侠之大者", n >= 2,
                        () -> Story.show(g, expose(g)))
                .choice("投身天魔教，诛杀伪君子", "需侠义≤-20 或走过C线（当前 " + p.justice + "）· 魔道",
                        p.justice <= -20 || p.has("join_demon"), () -> Story.show(g, joinDemon(g)))
                .choice("带苏晚晴离开这是非之地", "需救下苏晚晴且好感 ≥ 70（当前 " + p.affSu + "） · 神仙眷侣", suLove,
                        () -> Story.show(g, elope(g)))
                .choice("置身事外，转身下山", "结局：江湖夜雨", true, () -> {
                    p.set("walked_away");
                    ending(g, Story.END_RAIN);
                });
    }

    private static void afterObey(Game g) {
        Player p = g.player;
        boolean suDies = Story.suAlive(p) && p.affSu >= 40;
        if (suDies) {
            p.set("su_dead");
            p.set("su_died_climax");
        }
        if (Story.shadowReady(p)) {
            p.set("path_obey");
            Story.play(g, "shadow_reveal");
        } else {
            ending(g, suDies ? Story.END_LONE : Story.END_HEIR);
        }
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
        if (p.has("taihu_orders")) {
            e.me("太湖粮船里的密令——屠村嫁祸，也是你的手笔！");
        }
        if (p.has("banquet_secret")) {
            e.me("寿宴那夜，我亲眼看见你跪在一个面具人脚下！");
        }
        if (p.has("has_testimony")) {
            e.me("还有老药王的血书！二十年前，萧庄主临死前扯下的，正是你的面巾！");
        }
        if (p.has("witness_alive")) {
            e.say("老药王", "（被人搀扶着走上前）各位英雄，老夫可以作证——那一夜，就是他！");
        }
        if (p.has("blade_confess")) {
            e.me("大漠狂刀临死前亲口承认，是你出钱要取萧寒的性命！");
        }
        e.narr("群雄哗然。无数道目光，齐刷刷地落在了慕容渊身上。");
        if (p.has("met_xiao")) {
            e.say("萧寒", "义父……他说的，是真的吗？");
        }
        return e.say("慕容渊", "……好，好得很。二十年的布局，竟毁在你这个黄口小儿手里。")
                .say("慕容渊", "既然如此——今日在场之人，一个也别想活着下山！")
                .then(() -> Story.fight(g, 31, () -> {
                    if (Story.shadowReady(p)) {
                        p.set("path_expose");
                        Story.play(g, "shadow_reveal");
                    } else if (Story.suSaved(p) && p.affSu >= 60) {
                        Story.play(g, "after_expose");
                    } else {
                        ending(g, Story.END_HERO);
                    }
                }, null));
    }

    private static Ev afterExpose(Game g) {
        return new Ev("终章 · 尘埃落定")
                .narr("慕容渊倒在了天魔殿前的石阶上。二十年的冤案，终于大白于天下。")
                .narr("群雄纷纷向你抱拳致意。人群之外，苏晚晴静静地望着你，嘴角带着一丝浅浅的笑。")
                .say("苏晚晴", "接下来，你打算怎么办？")
                .choice("留下来，重整武林", "结局：侠之大者", true, () -> ending(g, Story.END_HERO))
                .choice("携晚晴归隐山林", "结局：神仙眷侣", true, () -> ending(g, Story.END_LOVERS));
    }

    private static Ev joinDemon(Game g) {
        return new Ev("终章 · 入魔").bgm(Audio.BGM_BOSS)
                .me("慕容渊，你的正道，我看够了。")
                .narr("你转过身，剑尖直指武林盟主。正道群雄一片哗然，厉天行却放声大笑。")
                .say("厉天行", "好！好小子！今日你我联手，先斩了这个伪君子！")
                .then(() -> Story.fight(g, 31, () -> Story.play(g, "demon_choice"), null));
    }

    private static Ev demonChoice(Game g) {
        final Player p = g.player;
        Ev e = new Ev("终章 · 魔尊")
                .narr("慕容渊倒下了。正道群雄四散奔逃，黑木崖上只剩下天魔教的欢呼。")
                .say("厉天行", "哈哈哈！小子，从今日起，你便是我天魔教的光明右使！")
                .narr("你望着他伸出的手，又看了看满地的尸首。手中的长剑，仍在滴血。")
                .choice("单膝跪下，拜见教主", "结局：天魔右使", true, () -> ending(g, Story.END_ENVOY))
                .choice("这江湖，该换个主人了", "与厉天行一决生死 · 结局：魔道至尊", true, () -> Story.show(g, new Ev("终章 · 魔尊").bgm(Audio.BGM_BOSS)
                        .say("厉天行", "……你说什么？哈，好，好！不愧是我看中的人！来吧！")
                        .then(() -> Story.fight(g, 14, () -> ending(g, Story.END_TYRANT), null))));
        if (Story.shadowReady(p)) {
            e.choice("先找出二十年前的面具人", "影阁线索已集齐 · 开启隐藏终章", true, () -> {
                p.set("path_demon");
                Story.play(g, "shadow_reveal");
            });
        }
        return e;
    }

    private static Ev elope(Game g) {
        final Player p = g.player;
        Ev e = new Ev("终章 · 归去")
                .narr("你穿过混战的人群，握住了苏晚晴冰凉的手。")
                .say("苏晚晴", "你……你要做什么？")
                .me("带你走。这江湖的恩怨，让他们自己去了结吧。");
        if (Story.xiaoFriend(p)) {
            return e.say("萧寒", "（横剑挡住追兵）走！这里交给我。照顾好她！")
                    .then(() -> ending(g, Story.END_LOVERS));
        }
        return e.say("萧寒", "站住！" + p.name + "，你要为了一个魔教妖女，背叛整个正道吗？")
                .then(() -> Story.fight(g, 32, () -> ending(g, Story.END_LOVERS), null));
    }

    // ================= 隐藏终章 · 归墟 =================

    private static Ev shadowReveal(Game g) {
        final Player p = g.player;
        String sn = Sects.SHADOW_NAME[p.sect];
        Ev e = new Ev("隐章 · 影").bgm(Audio.BGM_BOSS);
        if (p.has("path_expose")) {
            e.narr("慕容渊伏在石阶上，嘴角溢血。")
                    .say("慕容渊", "你以为……我就是主谋？我不过是一枚棋子……二十年前，真正下令的是……")
                    .narr("一枚黑针破空而来，慕容渊双目圆睁，气绝身亡。");
        } else if (p.has("path_demon")) {
            e.narr("厉天行大笑着走向你，却忽然身形一顿——一只枯瘦的手掌，从背后贯穿了他的胸膛。")
                    .say("厉天行", "是……是你……二十年前……火光里的……");
        } else {
            e.narr("天魔教主轰然倒地。慕容渊仰天大笑，正要开口，一道黑影却从殿顶掠下——");
            if (p.has("su_died_climax")) {
                e.narr("苏晚晴扑在父亲身上，被那黑影一掌震飞，再也没有起来。");
            }
            e.narr("黑影反手一掌，慕容渊的笑声戛然而止。");
        }
        e.narr("殿顶之上，站着一个戴青铜面具的人。他缓缓摘下了面具——");
        switch (p.sect) {
            case 0:
                e.narr("竟是少林达摩院首座，你的师叔——慧明大师！")
                        .say(sn, "阿弥陀佛。师侄，你比老衲想的要聪明。");
                break;
            case 1:
                e.narr("竟是武当掌门大弟子，你的大师兄——清虚道长！")
                        .say(sn, "你从小就是这样，非要把事情查个水落石出。");
                break;
            case 2:
                e.narr("竟是峨眉戒律院首座——静玄师太！")
                        .say(sn, "你啊……总是这般不知进退。");
                break;
            default:
                e.narr("竟是丐帮执法长老——钱不易！")
                        .say(sn, "小叫花子，你坏了老夫二十年的大事。");
                break;
        }
        return e.say(sn, "二十年前，是我给了慕容渊一把刀，也给了厉天行一个仇人。正邪相争，各派凋零，这江湖才会一点一点落进我的手里。")
                .say(sn, "叛徒、灯会的刺客、黑白无常……都是我的人。可惜啊，棋子总是不够听话。")
                .say(sn, "想报仇，就来归墟找我吧。黑木崖下的万丈深渊——那里，才是影阁真正的所在。")
                .narr("他身形一晃，跃入崖后的深渊，消失在翻涌的黑雾之中。（隐藏终章「归墟」已开启，地图上出现了新的地点）")
                .then(() -> {
                    if (p.has("path_demon")) {
                        p.set("li_dead");
                    }
                    p.log("影阁之主现身——竟是" + Sects.SHADOW_TITLE[p.sect]);
                    Story.goStage(g, Story.S_SHADOW);
                    Story.toLocation(g);
                });
    }

    private static Ev shadowFinal(Game g) {
        final Player p = g.player;
        String sn = Sects.SHADOW_NAME[p.sect];
        Ev e = new Ev("隐章 · 归墟").bgm(Audio.BGM_BOSS)
                .narr("归墟最深处，地脉之火映红了石壁。" + Sects.SHADOW_TITLE[p.sect] + "端坐在一张白骨王座之上。")
                .say(sn, "你终于来了。");
        switch (p.sect) {
            case 0:
                e.say(sn, "三十年前，方丈之位传给了了尘，而不是老衲。这口气，老衲咽了三十年。");
                break;
            case 1:
                e.say(sn, "师父说我心术不正，宁可让掌门之位空悬，也不肯传给我。我怎能甘心？");
                break;
            case 2:
                e.say(sn, "峨眉的清规戒律，困了我四十年。我要这天下，再没有人敢对我说一个「不」字。");
                break;
            default:
                e.say(sn, "叫花子们讨了一辈子饭，凭什么不能做一回天下的主人？");
                break;
        }
        e.say(sn, "跟随我吧。这江湖，你我共掌。");
        StringBuilder allies = new StringBuilder();
        if (Story.xiaoFriend(p)) {
            e.say("萧寒", "（拔剑上前）萧家三十余口的血债，今日一并讨回！");
            allies.append("萧寒");
        }
        if (Story.suSaved(p)) {
            e.say("苏晚晴", "（握紧短剑）我也来。我爹背了二十年的罪名，该有个了断了。");
            if (allies.length() > 0) {
                allies.append("、");
            }
            allies.append("苏晚晴");
        }
        String allyHint = allies.length() > 0 ? "同伴助战：" + allies : "孤身一人迎战";
        return e.choice("与同伴并肩，诛灭影阁", allyHint, true,
                        () -> Story.fight(g, Sects.FINAL_BOSS[p.sect], () -> Story.play(g, "after_shadow"), null))
                .choice("接受影阁主的邀请", "需侠义 ≤ 0 或曾入魔教（当前 " + p.justice + "） · 结局：影阁新主",
                        p.justice <= 0 || p.has("join_demon"), () -> ending(g, Story.END_SHADOW))
                .choice("以身为饵，与归墟同沉", "结局：玉石俱焚", true, () -> ending(g, Story.END_SACRIFICE));
    }

    private static Ev afterShadow(Game g) {
        final Player p = g.player;
        final boolean dawn = Story.xiaoFriend(p) && Story.suSaved(p) && p.has("sect_saved");
        String dawnHint = dawn ? "结局：影灭天明（三者俱全）" : "结局：残阳如血（三者未能俱全）";
        return new Ev("隐章 · 天明")
                .narr(Sects.SHADOW_NAME[p.sect] + "倒在白骨王座之下，青铜面具裂成两半。二十年的阴谋，终于烟消云散。")
                .narr("归墟开始崩塌。你一路冲出深渊，回到黑木崖顶时，朝阳正从云海中升起。")
                .choice("执掌武林，重立规矩", dawnHint, true, () -> ending(g, dawn ? Story.END_DAWN : Story.END_DUSK))
                .choice("回归师门，继承衣钵", "需师门得保且侠义 ≥ 30（当前 " + p.justice + "） · 结局：师门传人",
                        p.has("sect_saved") && p.justice >= 30, () -> ending(g, Story.END_SECT))
                .choice("与晚晴归隐山林", "需苏晚晴获救在世且好感 ≥ 70（当前 " + p.affSu + "） · 神仙眷侣",
                        Story.suSaved(p) && p.affSu >= 70, () -> ending(g, Story.END_LOVERS))
                .choice("孑然一身，浪迹天涯", "结局：江湖夜雨", true, () -> ending(g, Story.END_RAIN));
    }

    // ================= 结局 =================

    static void ending(Game g, int id) {
        final Player p = g.player;
        boolean sad = id == Story.END_LONE || id == Story.END_HEIR || id == Story.END_DUSK
                || id == Story.END_RAIN || id == Story.END_SACRIFICE;
        Ev e = new Ev("结局 · " + Story.ENDING_NAMES[id]).bgm(sad ? Audio.BGM_SAD : Audio.BGM_ENDING);
        boolean xiao = Story.xiaoFriend(p);
        boolean shadow = p.stage >= Story.S_SHADOW;
        String sect = GameData.SECTS[p.sect].name;
        switch (id) {
            case Story.END_HERO:
                e.narr("慕容渊伏诛，二十年的冤案终于大白于天下。");
                if (xiao) {
                    e.narr("萧寒在萧家庄旧址前长跪三日，而后接过盟主之位，与天魔教立下百年不战之约。");
                } else if (p.has("met_xiao")) {
                    e.narr("萧寒得知身世之后，一夜白头。有人说他去了关外，从此再未踏足中原。");
                }
                if (Story.suAlive(p) && p.affSu >= 30) {
                    e.say("苏晚晴", "若你哪日累了，黑木崖上，有一壶酒为你温着。");
                }
                e.narr("群雄推举你为武林盟主，你却只接下了一块「侠之大者」的匾额。")
                        .narr("「侠之大者，为国为民。」你留下这句话，转身走入了万丈红尘。")
                        .narr("只是偶尔，你会想起叛徒临死前那个「影」字……这江湖，真的干净了吗？");
                break;
            case Story.END_LOVERS:
                e.narr("那一日之后，江湖上再没有人见过" + p.name + "和苏晚晴。")
                        .narr("有人说，在大理苍山脚下，见过一对夫妻开了间小酒馆。一个擅剑，一个抚琴。");
                if (xiao) {
                    e.narr("每年清明，总有一个白衣剑客路过那间酒馆，一坐便是一整夜。");
                }
                if (shadow) {
                    e.narr("影阁覆灭的消息传遍天下时，他们正在洱海边放河灯。");
                }
                e.narr("偶尔有江湖客慕名而来，想要讨教几招，都被一壶好酒灌醉，糊里糊涂地下了山。")
                        .narr("江湖很大，可他们的江湖，只有彼此。");
                break;
            case Story.END_TYRANT:
                e.narr("厉天行倒在了天魔殿的宝座之前。你踏着他的尸首，一步一步走上了那张黑玉椅。");
                if (Story.suAlive(p)) {
                    e.narr("苏晚晴看着你，眼中再没有一丝光彩。那一夜，她独自离开了黑木崖。");
                }
                e.narr("正道群龙无首，魔教一家独大。不出三年，整个江湖已尽在你的掌握之中。")
                        .narr("人们不再叫你的名字，只叫你——「魔尊」。")
                        .narr("你站在黑木崖之巅，俯瞰万里河山，却忽然觉得……这崖顶的风，冷得刺骨。");
                break;
            case Story.END_HEIR:
                e.narr("天魔教主伏诛，黑木崖上一把大火，烧了三天三夜。")
                        .narr("慕容渊当众宣布，收你为衣钵传人。你名动天下，风光无两。");
                if (Story.suAlive(p)) {
                    e.narr("你再也没有见过苏晚晴。有人说，她在一个雪夜，削发为尼。");
                }
                e.narr("直到某一天，你在盟主书房中，偶然翻出一封二十年前的旧信。")
                        .narr("信纸泛黄，上面只有一行字：「萧家庄之事，务必做得干净。」落款是……慕容渊。")
                        .narr("你握着那封信，在窗前坐了一夜。窗外，新的盟主大旗正猎猎作响。这个江湖，真的变好了吗？");
                break;
            case Story.END_LONE:
                e.narr("厉天行倒下的那一刻，苏晚晴扑了上来，用身体挡住了补上的那一掌。")
                        .say("苏晚晴", "傻子……我不怪你……替我……查清……")
                        .narr("她在你怀中渐渐冷去，手中紧紧攥着那支紫玉簪。")
                        .narr("后来你才知道，二十年前的真相，就藏在慕容渊的书房里。可这一切，都已经太迟了。")
                        .narr("从此江湖上多了一个独行的剑客，一壶酒，一柄剑，一支紫玉簪。他走过很多地方，却再也没有回过黑木崖。");
                break;
            case Story.END_ENVOY:
                e.narr("你单膝跪下，厉天行哈哈大笑，声震九霄。")
                        .narr("慕容渊身死，武林盟土崩瓦解。天魔教重开山门，正邪之争告一段落。");
                if (Story.suAlive(p) && p.affSu >= 30) {
                    e.narr("苏晚晴成了你的妻子。她常说，你是她见过最奇怪的魔教中人——从不滥杀一个无辜。");
                }
                e.narr("江湖人提起天魔教的光明右使，语气复杂：有人骂他是叛徒，也有人说，他是唯一敢对教主说「不」的人。")
                        .narr("正与邪，原本就没有那么分明。");
                break;
            case Story.END_DAWN:
                e.narr("影阁覆灭，二十年的血债终于清偿。黑木崖上，正邪两道的幸存者第一次放下了刀剑。")
                        .narr("萧寒重建萧家庄，又被群雄推为武林盟主。他立下的第一条规矩是：正邪之分，不在门户，而在人心。")
                        .narr("苏晚晴接掌天魔教，改名「天心教」，从此教众不得滥杀一人。")
                        .narr(sect + "重修山门那日，师父拉着你的手，久久没有说话。")
                        .narr("而你，被天下人称为「侠之大者」。每年上元，临安城的河上都会多出三盏莲花灯——那是你们三个人的约定。");
                break;
            case Story.END_DUSK:
                e.narr("影阁覆灭了，可这一战的代价，太过沉重。");
                if (p.has("sect_fallen")) {
                    e.narr(sect + "只剩下断壁残垣。你在师父的坟前跪了一整夜。");
                }
                if (p.has("su_dead")) {
                    e.narr("苏晚晴没能等到这一天。你把那支紫玉簪埋在了洱海边。");
                }
                if (!xiao && p.has("met_xiao")) {
                    e.narr("萧寒没有来。他至死都不知道，自己究竟是谁的儿子。");
                }
                e.narr("你登上了武林盟主之位，雷厉风行地整顿江湖。人人都敬你、怕你，却没有人再与你同饮一壶酒。")
                        .narr("残阳如血，照着空荡荡的盟主府。你终于赢了，却不知道自己赢了什么。");
                break;
            case Story.END_SHADOW:
                e.narr("你单膝跪在白骨王座之前。" + Sects.SHADOW_NAME[p.sect] + "抚掌大笑，亲手为你戴上了那张青铜面具。")
                        .narr("三年后，他死于一场「意外」。影阁的新主人，从此再也没有摘下过面具。");
                if (Story.suAlive(p)) {
                    e.narr("苏晚晴曾在归墟外等了你七天七夜。第八天，她转身离去，再也没有回头。");
                }
                e.narr("江湖依旧纷争不断，正邪依旧斗得你死我活。只是在每一场厮杀的背后，都多了一双看不见的眼睛。")
                        .narr("那双眼睛，是你的。");
                break;
            case Story.END_SECT:
                e.narr("影阁覆灭之后，你谢绝了盟主之位，回到了" + Sects.homeName(p) + "。")
                        .narr("师父将" + Sects.HEIR_TITLE[p.sect] + "的信物交到你手中：「本门清誉，今后便交给你了。」")
                        .narr("你重整门规，收容了许多因战乱失去亲人的孩子。他们之中，有正道子弟的遗孤，也有魔教教众的后人。");
                if (Story.suAlive(p)) {
                    e.narr("苏晚晴偶尔会上山来看你，带一壶黑木崖的酒，和你在山门前坐到天黑。");
                }
                e.narr("多年以后，当年的孩子们行走江湖。人们问起他们的师承，他们总会骄傲地说出你的名字。");
                break;
            case Story.END_RAIN:
                if (p.has("walked_away")) {
                    e.narr("你收剑入鞘，转身走下了黑木崖。身后的厮杀声，渐渐听不见了。");
                } else {
                    e.narr("影阁覆灭之后，你没有接受任何人的挽留，独自离开了黑木崖。");
                }
                e.narr("后来，江湖上有了很多关于你的传说。有人说你死在了黑木崖，有人说你去了西域，也有人说你出家了。")
                        .narr("只有江南的一间小酒馆的掌柜知道：每逢下雨的夜晚，总有一个旅人来喝一壶酒，听一夜的雨。")
                        .narr("江湖夜雨十年灯。你终究，还是一个人。");
                break;
            default:
                e.narr("你没有拔剑。你只是一步一步走向白骨王座，然后紧紧抱住了" + Sects.SHADOW_NAME[p.sect] + "。")
                        .say(Sects.SHADOW_NAME[p.sect], "你……你疯了！归墟一塌，你我都会葬身于此！")
                        .me("我知道。");
                if (Story.xiaoFriend(p) || Story.suSaved(p)) {
                    e.narr("你回头，冲着身后的同伴笑了笑，示意他们快走。");
                }
                e.narr("地脉崩裂，万丈深渊轰然合拢。影阁的阴谋，和你一起，永远埋在了黑木崖下。")
                        .narr("江湖上没有人知道你的名字。只是后来，黑木崖下多了一块无字的石碑，每年都有人来献上一束野花。");
                break;
        }
        e.then(() -> {
            p.ending = id;
            p.finished = true;
            p.log("结局：" + Story.ENDING_NAMES[id]);
            Story.goStage(g, Story.S_POST);
            g.unlockEnding(id);
            g.save();
            g.setScene(new EndingScene(g, id));
        });
        Story.show(g, e);
    }
}
