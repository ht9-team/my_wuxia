package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** 节点回溯：列出某个存档位在关键抉择前自动保存的节点，可覆盖重走或复制到空存档位重走。 */
public class NodeScene extends Scene {
    private static final int PER = 5;
    private final int slot;
    private final boolean fromTitle;
    private JSONArray nodes;
    private int page = 0;
    private int selected = -1;
    private boolean confirmOverwrite = false;

    public NodeScene(Game game, int slot, boolean fromTitle) {
        super(game);
        this.slot = slot;
        this.fromTitle = fromTitle;
        this.nodes = game.nodes(slot);
    }

    private int count() {
        return nodes.length();
    }

    /** 列表按时间倒序显示：显示序号 i 对应数组下标 count-1-i。 */
    private int indexOf(int display) {
        return count() - 1 - display;
    }

    @Override
    public void layout() {
        buttons.clear();
        float top = 200;
        float bottom = H() - 330;
        int per = Math.max(1, Math.min(PER, (int) ((bottom - top) / 132)));
        final int pageCount = pages(count(), per);
        if (page >= pageCount) {
            page = 0;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        float y = top;
        for (int d = page * per; d < Math.min(count(), page * per + per); d++) {
            final int idx = indexOf(d);
            JSONObject n = nodes.optJSONObject(idx);
            if (n == null) {
                continue;
            }
            String label = n.optString("title", "节点") + " · Lv." + n.optInt("level", 1);
            String sub = n.optString("chapter", "") + " · 第" + n.optInt("day", 1) + "日 · " + fmt.format(new Date(n.optLong("time", 0)));
            Button b = addButton(36, y, W() - 72, 120, label, () -> {
                selected = idx;
                confirmOverwrite = false;
                layout();
            });
            b.sub = sub;
            b.textSize = 30;
            b.selected = selected == idx;
            b.color = UI.RED;
            y += 132;
        }
        float bw = (W() - 72 - 14) / 2f;
        if (pageCount > 1) {
            addButton(36, H() - 320, bw, 76, "上一页", () -> {
                page = (page + pageCount - 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
            addButton(36 + bw + 14, H() - 320, bw, 76, "下一页 " + (page + 1) + "/" + pageCount, () -> {
                page = (page + 1) % pageCount;
                layout();
            }).color = UI.INK_LIGHT;
        }
        Button over = addButton(36, H() - 232, bw, 92, confirmOverwrite ? "再点一次确认" : "从此重走", () -> replay(false));
        over.sub = "覆盖存档 " + (slot + 1) + " 的进度";
        over.enabled = selected >= 0;
        over.color = UI.RED;
        Button copy = addButton(36 + bw + 14, H() - 232, bw, 92, "复制后重走", () -> replay(true));
        int free = game.firstFreeSlot();
        copy.sub = free >= 0 ? "复制到空存档 " + (free + 1) : "没有空存档位";
        copy.enabled = selected >= 0 && free >= 0;
        copy.color = UI.JADE;
        addButton(36, H() - 130, W() - 72, 96, "返回", this::close).color = UI.INK_LIGHT;
    }

    private void replay(boolean copy) {
        if (selected < 0) {
            return;
        }
        if (!copy && !confirmOverwrite) {
            confirmOverwrite = true;
            layout();
            return;
        }
        int target = copy ? game.firstFreeSlot() : slot;
        if (target < 0) {
            game.toast("没有空存档位了");
            return;
        }
        String id = game.loadNode(slot, selected, target);
        if (id == null) {
            game.toast("节点存档损坏，无法读取");
            nodes = game.nodes(slot);
            selected = -1;
            layout();
            return;
        }
        game.toast(copy ? "已复制到存档 " + (target + 1) + "，从节点重走" : "已回到节点，可以改走其他分支");
        Story.play(game, id);
    }

    private void close() {
        if (fromTitle || game.player == null) {
            game.setScene(new SlotScene(game, false));
        } else {
            game.setScene(new JournalScene(game));
        }
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 6, game.time);
        UI.title(c, "节点回溯 · 存档" + (slot + 1), W() / 2, 100, 46);
        UI.text(c, "关键抉择出现前会自动存档；读取后可改走其他分支，看到不同结局", W() / 2, 160, 22, UI.INK_LIGHT, Paint.Align.CENTER);
        if (count() == 0) {
            UI.text(c, "这个存档位还没有节点存档", W() / 2, 360, 30, UI.INK_LIGHT, Paint.Align.CENTER);
        }
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        close();
        return true;
    }
}
