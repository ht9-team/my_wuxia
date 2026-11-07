package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** 江湖志：章节目标、侠义值、人物好感、证物、结局收集与江湖纪事。 */
public class JournalScene extends Scene {
    public JournalScene(Game game) {
        super(game);
    }

    @Override
    public void layout() {
        buttons.clear();
        addButton(36, H() - 130, W() - 72, 96, "合上江湖志", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private static String justiceLabel(int j) {
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

    private static String suLabel(int a) {
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

    private static String xiaoLabel(int a) {
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

        // 章节与人物
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
            UI.text(c, suLabel(p.affSu), W() - 56, y + 22, 24, UI.INK, Paint.Align.RIGHT);
        } else {
            UI.text(c, "？？？", 58, y + 22, 26, UI.INK_LIGHT);
        }
        y += 52;
        if (p.has("met_xiao")) {
            UI.text(c, "萧寒", 58, y + 22, 26, UI.BLUE);
            UI.centerBar(c, 150, y, W() - 360, 28, p.affXiao, UI.INK_LIGHT, UI.BLUE);
            UI.text(c, xiaoLabel(p.affXiao), W() - 56, y + 22, 24, UI.INK, Paint.Align.RIGHT);
        } else {
            UI.text(c, "？？？", 58, y + 22, 26, UI.INK_LIGHT);
        }

        // 证物与结局
        RectF r2 = new RectF(30, 484, W() - 30, 742);
        UI.panel(c, r2);
        StringBuilder ev = new StringBuilder();
        for (int i = 0; i < Story.EVIDENCE.length; i++) {
            if (p.has(Story.EVIDENCE[i])) {
                if (ev.length() > 0) {
                    ev.append("、");
                }
                ev.append(Story.EVIDENCE_NAMES[i]);
            }
        }
        UI.text(c, "证物（" + Story.evidence(p) + "）", 56, 526, 26, UI.INK);
        UI.wrap(c, ev.length() == 0 ? "尚无" : ev.toString(), 200, 526, W() - 260, 24, UI.RED, 32);
        UI.text(c, "结局收集 " + game.endingCount() + " / " + Story.ENDING_NAMES.length, 56, 604, 26, UI.INK);
        for (int i = 0; i < Story.ENDING_NAMES.length; i++) {
            float x = 56 + (i % 3) * 206;
            float yy = 652 + (i / 3) * 52;
            boolean got = game.endingUnlocked(i);
            UI.text(c, got ? Story.ENDING_NAMES[i] : "？？？？", x, yy, 26, got ? UI.GOLD : UI.INK_LIGHT);
            if (p.ending == i) {
                UI.text(c, "◆", x - 22, yy, 20, UI.RED);
            }
        }

        // 江湖纪事
        RectF r3 = new RectF(30, 756, W() - 30, H() - 146);
        UI.panel(c, r3);
        UI.text(c, "江湖纪事", 56, 796, 26, UI.INK);
        UI.text(c, "悬赏：" + p.bountyText(), W() - 56, 796, 22, UI.RED, Paint.Align.RIGHT);
        int lines = Math.max(1, (int) ((r3.bottom - 830) / 36));
        int start = Math.max(0, p.journal.size() - lines);
        float jy = 836;
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
