package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/** 剧情指引：流程图与抉择记录、十二种结局的达成条件、证物与影阁线索的获取方法、支线一览。可上下拖动。 */
public class GuideScene extends Scene {
    private static final String[] TABS = {"流程", "结局", "线索", "支线"};
    private static final float SIZE = 24f;

    private static final class Line {
        final String text;
        final int color;
        final float size;
        final float gap;

        Line(String text, int color, float size, float gap) {
            this.text = text;
            this.color = color;
            this.size = size;
            this.gap = gap;
        }
    }

    private int tab;
    private final ArrayList<Line> lines = new ArrayList<>();
    private float viewTop;
    private float viewBottom;
    private float scroll = 0f;
    private float contentH = 0f;
    private float downY;
    private float lastY;
    private boolean dragging = false;

    public GuideScene(Game game, int tab) {
        super(game);
        this.tab = tab;
    }

    @Override
    public void layout() {
        buttons.clear();
        float tw = (W() - 72 - 3 * 10) / 4f;
        for (int i = 0; i < TABS.length; i++) {
            final int t = i;
            Button b = addButton(36 + i * (tw + 10), 112, tw, 70, TABS[i], () -> {
                tab = t;
                scroll = 0;
                layout();
            });
            b.selected = tab == i;
            b.textSize = 30;
        }
        addButton(36, H() - 130, W() - 72, 96, "返回", this::close).color = UI.INK_LIGHT;
        viewTop = 200;
        viewBottom = H() - 150;
        build();
    }

    private void close() {
        if (game.player != null) {
            game.setScene(new JournalScene(game));
        } else {
            game.setScene(new TitleScene(game));
        }
    }

    private void head(String s) {
        lines.add(new Line(s, UI.RED, 30, 50));
    }

    private void text(String s, int color) {
        for (String l : UI.split(s, W() - 130, SIZE)) {
            lines.add(new Line(l, color, SIZE, 36));
        }
    }

    private void gap() {
        lines.add(new Line("", UI.INK, SIZE, 16));
    }

    private String mark(Player p, int stage) {
        if (p.stage == stage) {
            return "→ ";
        }
        return p.stage > stage ? "√ " : "○ ";
    }

    private void build() {
        lines.clear();
        Player p = game.player;
        switch (tab) {
            case 0:
                buildFlow(p);
                break;
            case 1:
                buildEndings(p);
                break;
            case 2:
                buildClues(p);
                break;
            default:
                buildSides(p);
                break;
        }
        float h = 0;
        for (Line l : lines) {
            h += l.gap;
        }
        contentH = h + 40;
        scroll = Math.max(0, Math.min(maxScroll(), scroll));
    }

    private void stageLine(Player p, int stage) {
        Story.Stage s = Story.STAGES[stage];
        int color = p.stage == stage ? UI.RED : (p.stage > stage ? UI.JADE : UI.INK_LIGHT);
        text(mark(p, stage) + s.title + "：" + s.desc, color);
    }

    private void routeLine(Player p, String flag, String name, String cond) {
        boolean taken = p.has(flag);
        boolean decided = p.has("route_truth") || p.has("route_alliance") || p.has("route_demon");
        String state = taken ? "【已选】" : (decided ? "【未选】" : "");
        text(state + name + "——" + cond, taken ? UI.RED : UI.INK);
    }

    private void buildFlow(Player p) {
        if (p == null) {
            text("开始游戏后可查看流程。", UI.INK_LIGHT);
            return;
        }
        head("当前位置");
        text(Story.current(p).chapter + " · " + Story.current(p).title, UI.RED);
        text(Story.hint(p), UI.GOLD);
        gap();
        head("序章 · 师门风波（四派各不相同）");
        stageLine(p, Story.S_P_TRIAL);
        stageLine(p, Story.S_P_TRAITOR);
        head("第一章 · 初出茅庐");
        stageLine(p, Story.S_BAMBOO);
        text("  分支：竹林中「相助苏晚晴 / 旁观 / 协助武林盟」，决定苏晚晴的初始好感。", UI.INK_LIGHT);
        stageLine(p, Story.S_HEIFENG);
        text("  分支：密信「收好（证物）/ 交官府 / 烧掉」。", UI.INK_LIGHT);
        stageLine(p, Story.S_LANTERN);
        text("  分支：灯会「护住知府 / 追击刺客（影阁线索）/ 陪苏晚晴看灯」。", UI.INK_LIGHT);
        head("第二章 · 英雄大会");
        stageLine(p, Story.S_LUOYANG);
        text("  分支：与萧寒「结拜 / 同饮 / 谢绝」，决定萧寒日后能否同心。", UI.INK_LIGHT);
        stageLine(p, Story.S_ARENA);
        stageLine(p, Story.S_TOMB);
        head("第三章 · 歧路（三选一）");
        routeLine(p, "route_truth", "A 追查真相", "大理 → 苗疆五毒老怪 → 无量山白无常；可得血书、亲证与影阁线索。");
        routeLine(p, "route_alliance", "B 效忠盟主", "思过崖 → 太湖水寨 → 盟主寿宴；可得书信、账本、寿宴密谈与影阁线索。");
        routeLine(p, "route_demon", "C 投身魔教", "黑木崖盟誓 → 苗疆夺令 → 太湖截粮；需侠义 ≤ -15 或苏晚晴好感 ≥ 30。");
        head("第四章 · 大漠孤烟");
        stageLine(p, Story.S_XIANGYANG);
        stageLine(p, Story.S_DESERT);
        text("  关键：把血书交给萧寒（好感 ≥ 20），或劝他提防慕容渊（好感 ≥ 30），萧寒才会同心。", UI.INK_LIGHT);
        stageLine(p, Story.S_LOULAN);
        text("  可选：大漠之后选择「西出阳关」进入楼兰，可得影阁线索。", UI.INK_LIGHT);
        head("第五章 · 风雨欲来（两难）");
        text("师门告急与苏晚晴中毒同时发生：赶回师门 / 远赴天山 / 直上黑木崖。", UI.INK);
        text("  两全之法：随身携带「天山雪莲」（襄阳有售）可托人送去；萧寒同心时，他会替你去另一处。", UI.GOLD);
        stageLine(p, Story.S_SECT);
        stageLine(p, Story.S_TIANSHAN);
        head("终章 · 黑木崖之巅");
        stageLine(p, Story.S_FINAL);
        text("  抉择：听命盟主 / 揭穿慕容渊（证物 ≥ 2）/ 投身魔教 / 携苏晚晴离去 / 置身事外。", UI.INK_LIGHT);
        head("隐章 · 归墟（影阁线索 ≥ " + Story.SHADOW_NEED + "）");
        stageLine(p, Story.S_SHADOW);
        text("  影阁之主出自你的师门，四派各不相同；最终首领也因门派而异。", UI.INK_LIGHT);
        gap();
        head("关键抉择记录（" + p.choices.size() + "）");
        if (p.choices.isEmpty()) {
            text("尚未作出关键抉择。", UI.INK_LIGHT);
        }
        for (int i = p.choices.size() - 1; i >= 0; i--) {
            text("· " + p.choices.get(i), UI.INK);
        }
        gap();
        text("每个关键抉择出现前都会自动存档，可在「江湖志 → 节点回溯」中读取，改走另一条分支。", UI.GOLD);
    }

    private void buildEndings(Player p) {
        head("结局收集 " + game.endingCount() + " / " + Story.ENDING_NAMES.length);
        text("以下为全部结局的达成条件，可配合「节点回溯」逐一解锁。", UI.GOLD);
        gap();
        for (int i = 0; i < Story.ENDING_NAMES.length; i++) {
            boolean got = game.endingUnlocked(i);
            String cur = p != null && p.ending == i ? "（本存档）" : "";
            lines.add(new Line((got ? "√ " : "○ ") + (i + 1) + ". " + Story.ENDING_NAMES[i] + cur, got ? UI.GOLD : UI.INK, 28, 44));
            text(Story.ENDING_CONDITIONS[i], got ? UI.INK_LIGHT : UI.INK);
            gap();
        }
    }

    private void buildClues(Player p) {
        if (p == null) {
            text("开始游戏后可查看线索。", UI.INK_LIGHT);
            return;
        }
        head("影阁线索 " + Story.clues(p) + " / " + Story.CLUES.length + "（≥ " + Story.SHADOW_NEED + " 开启隐藏终章）");
        for (int i = 0; i < Story.CLUES.length; i++) {
            boolean got = p.has(Story.CLUES[i]);
            text((got ? "√ " : "○ ") + Story.CLUE_NAMES[i] + "——" + Story.CLUE_HINTS[i], got ? UI.JADE : UI.INK);
        }
        gap();
        head("证物 " + Story.evidence(p) + " / " + Story.EVIDENCE.length + "（≥ 2 可在黑木崖揭穿慕容渊）");
        for (int i = 0; i < Story.EVIDENCE.length; i++) {
            boolean got = p.has(Story.EVIDENCE[i]);
            text((got ? "√ " : "○ ") + Story.EVIDENCE_NAMES[i] + "——" + Story.EVIDENCE_HINTS[i], got ? UI.JADE : UI.INK);
        }
        gap();
        head("人物与师门");
        text("侠义值：" + p.justice + "（" + JournalScene.justiceLabel(p.justice) + "）", UI.INK);
        text("苏晚晴：" + (p.has("met_su") ? JournalScene.suLabel(p) + "，好感 " + p.affSu : "尚未相识")
                + (Story.suSaved(p) ? "，已获救" : ""), UI.INK);
        text("萧寒：" + (p.has("met_xiao") ? JournalScene.xiaoLabel(p) + "，好感 " + p.affXiao : "尚未相识"), UI.INK);
        text("师门：" + (p.has("sect_saved") ? "得保" : (p.has("sect_fallen") ? "陷落" : "安好")), UI.INK);
    }

    private void buildSides(Player p) {
        if (p == null) {
            text("开始游戏后可查看支线。", UI.INK_LIGHT);
            return;
        }
        head("支线 " + Story.sidesDone(p) + " / " + Story.SIDES.length);
        for (Story.Side q : Story.SIDES) {
            int st = p.side[q.id];
            String state;
            int color;
            if (st < 0) {
                state = "未开启";
                color = UI.INK_LIGHT;
            } else if (st >= q.steps.length) {
                state = "已完成";
                color = UI.JADE;
            } else {
                state = "进行中 " + (st + 1) + "/" + q.steps.length;
                color = UI.RED;
            }
            lines.add(new Line(q.name + "【" + state + "】", color, 28, 46));
            text(q.intro, UI.INK);
            text("开启条件：" + q.unlock, UI.INK_LIGHT);
            if (st >= 0 && st < q.steps.length) {
                text("当前：" + q.steps[st].desc, UI.GOLD);
            }
            gap();
        }
        head("其他玩法");
        text("客栈悬赏：城镇客栈可揭榜接取按等级生成的悬赏，可重复完成。", UI.INK);
        text("奇遇武学：竹林、古墓、思过崖、苗疆、大漠、天山、蓬莱，探索两层以上后有几率触发。", UI.INK);
        text("隐世高手：通关后可前往东海蓬莱仙岛挑战剑仙，夺取龙吟剑。", UI.INK);
    }

    private float maxScroll() {
        return Math.max(0f, contentH - (viewBottom - viewTop));
    }

    @Override
    public void touchDown(float x, float y) {
        downY = y;
        lastY = y;
        dragging = false;
        super.touchDown(x, y);
    }

    @Override
    public void touchMove(float x, float y) {
        if (!dragging && Math.abs(y - downY) > 16 && downY > viewTop && downY < viewBottom) {
            dragging = true;
            cancelPress();
        }
        if (dragging) {
            scroll = Math.max(0f, Math.min(maxScroll(), scroll + (lastY - y)));
            lastY = y;
            return;
        }
        super.touchMove(x, y);
    }

    @Override
    public void touchUp(float x, float y) {
        if (dragging) {
            dragging = false;
            return;
        }
        super.touchUp(x, y);
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 8, game.time);
        UI.title(c, "剧情指引", W() / 2, 82, 50);
        RectF r = new RectF(26, viewTop - 6, W() - 26, viewBottom + 6);
        UI.panel(c, r);
        c.save();
        c.clipRect(r.left + 4, viewTop, r.right - 4, viewBottom);
        float y = viewTop - scroll;
        for (Line l : lines) {
            y += l.gap;
            if (y > viewTop - 40 && y < viewBottom + 40 && !l.text.isEmpty()) {
                UI.text(c, l.text, 56, y, l.size, l.color);
            }
        }
        c.restore();
        float ms = maxScroll();
        if (ms > 0) {
            float trackH = viewBottom - viewTop;
            float barH = Math.max(60, trackH * trackH / (trackH + ms));
            float barY = viewTop + (trackH - barH) * (scroll / ms);
            c.drawRoundRect(new RectF(W() - 40, barY, W() - 34, barY + barH), 3, 3, UI.fill(0x88231E19));
            UI.text(c, "上下拖动查看", W() - 50, viewBottom + 34, 20, UI.INK_LIGHT, Paint.Align.RIGHT);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        close();
        return true;
    }
}
