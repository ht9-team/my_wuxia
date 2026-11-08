package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 江湖志：章节目标、侠义值、人物好感、证物 / 线索 / 结局统计与江湖纪事；可进入剧情指引与节点回溯。 */
public class JournalScene extends Scene {
    public JournalScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        float bw = (W() - 72 - 14) / 2f;
        Button g1 = addButton(36, H() - 244, bw, 92, "剧情指引", () -> game.setScene(new GuideScene(game, 0)));
        g1.sub = "流程 · 结局 · 线索 · 支线";
        g1.color = UI.GOLD;
        Button g2 = addButton(36 + bw + 14, H() - 244, bw, 92, "节点回溯", () -> game.setScene(new NodeScene(game, game.slot, false)));
        g2.sub = "读取关键抉择前的存档";
        g2.color = UI.RED;
        addButton(36, H() - 138, W() - 72, 96, "合上江湖志", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    static String justiceLabel(int j) {
        if (j >= 60) {
            return "大侠";
        } else if (j >= 20) {
            return "侠士";
        } else if (j > -20) {
            return "亦正亦邪";
        } else if (j > -60) {
            return "邪道";
        }
        return "魔头";
    }

    static String suLabel(Player p) {
        if (p.has("su_dead")) {
            return "已故";
        }
        int a = p.affSu;
        if (a >= 70) {
            return "情深意重";
        } else if (a >= 40) {
            return "心生好感";
        } else if (a >= 10) {
            return "萍水相逢";
        } else if (a >= -10) {
            return "形同陌路";
        }
        return "心存芥蒂";
    }

    static String xiaoLabel(Player p) {
        if (Story.xiaoFriend(p)) {
            return "同心";
        }
        int a = p.affXiao;
        if (a >= 50) {
            return "生死之交";
        } else if (a >= 25) {
            return "肝胆相照";
        } else if (a >= 10) {
            return "江湖朋友";
        }
        return "点头之交";
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 7, game.time);
        Player p = game.player;
        UI.title(c, "江湖志", W() / 2, 86, 54);

        RectF r1 = new RectF(30, 122, W() - 30, 470);
        UI.panel(c, r1);
        Story.Stage s = Story.current(p);
        UI.text(c, s.chapter, 56, 166, 26, UI.INK_LIGHT);
        UI.text(c, "【" + s.title + "】", 50, 208, 32, UI.RED);
        UI.wrap(c, Story.objective(p), 58, 248, W() - 116, 24, UI.JADE, 32);

        float y = 314;
        UI.text(c, "侠义", 58, y + 22, 26, UI.INK);
        UI.centerBar(c, 150, y, W() - 360, 28, p.justice, UI.PURPLE, UI.GOLD);
        UI.text(c, justiceLabel(p.justice) + " " + p.justice, W() - 56, y + 22, 24, UI.INK, Paint.Align.RIGHT);
        y += 52;
        if (p.has("met_su")) {
            UI.text(c, "苏晚晴", 58, y + 22, 26, 0xFF6B3E7A);
            UI.centerBar(c, 150, y, W() - 360, 28, p.affSu, UI.INK_LIGHT, 0xFFC86A9A);
            UI.text(c, suLabel(p) + " " + p.affSu, W() - 56, y + 22, 24, UI.INK, Paint.Align.RIGHT);
        } else {
            UI.text(c, "？？？", 58, y + 22, 26, UI.INK_LIGHT);
        }
        y += 52;
        if (p.has("met_xiao")) {
            UI.text(c, "萧寒", 58, y + 22, 26, UI.BLUE);
            UI.centerBar(c, 150, y, W() - 360, 28, p.affXiao, UI.INK_LIGHT, UI.BLUE);
            UI.text(c, xiaoLabel(p) + " " + p.affXiao, W() - 56, y + 22, 24, UI.INK, Paint.Align.RIGHT);
        } else {
            UI.text(c, "？？？", 58, y + 22, 26, UI.INK_LIGHT);
        }

        RectF r2 = new RectF(30, 484, W() - 30, 640);
        UI.panel(c, r2);
        UI.text(c, "证物 " + Story.evidence(p) + "/" + Story.EVIDENCE.length, 56, 528, 26, UI.INK);
        UI.text(c, "影阁线索 " + Story.clues(p) + "/" + Story.CLUES.length, W() / 2 + 10, 528, 26,
                Story.shadowReady(p) ? UI.RED : UI.INK);
        UI.text(c, "结局 " + game.endingCount() + "/" + Story.ENDING_NAMES.length, 56, 576, 26, UI.GOLD);
        UI.text(c, "支线 " + Story.sidesDone(p) + "/" + Story.SIDES.length, W() / 2 + 10, 576, 26, UI.JADE);
        String sect = p.has("sect_saved") ? "师门：得保" : (p.has("sect_fallen") ? "师门：陷落" : "师门：安好");
        UI.text(c, sect + "    悬赏：" + p.bountyText(), 56, 618, 22, UI.INK_LIGHT);

        RectF r3 = new RectF(30, 654, W() - 30, H() - 262);
        UI.panel(c, r3);
        UI.text(c, "江湖纪事", 56, 694, 26, UI.INK);
        int lines = Math.max(1, (int) ((r3.bottom - 726) / 36));
        int start = Math.max(0, p.journal.size() - lines);
        float jy = 734;
        for (int i = p.journal.size() - 1; i >= start; i--) {
            UI.text(c, p.journal.get(i), 58, jy, 22, i == p.journal.size() - 1 ? UI.INK : UI.INK_LIGHT);
            jy += 36;
        }
        if (p.journal.isEmpty()) {
            UI.text(c, "江湖路远，故事才刚刚开始。", 58, jy, 22, UI.INK_LIGHT);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
