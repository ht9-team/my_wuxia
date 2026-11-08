package com.wuxia.jianghu;

import java.util.ArrayList;

/** 一段剧情：若干页对白 + 可选的抉择。 */
public final class Ev {
    /** 说话人为玩家自己时使用的占位符。 */
    public static final String ME = "@me";

    public static final class Page {
        public final String who;
        public final String text;

        Page(String who, String text) {
            this.who = who;
            this.text = text;
        }
    }

    public static final class Choice {
        public final String text;
        public final String hint;
        public final boolean enabled;
        public final Runnable action;

        Choice(String text, String hint, boolean enabled, Runnable action) {
            this.text = text;
            this.hint = hint;
            this.enabled = enabled;
            this.action = action;
        }
    }

    public final String title;
    public int bg = -1;
    public int bgm = Audio.KEEP;
    public final ArrayList<Page> pages = new ArrayList<>();
    public final ArrayList<Choice> choices = new ArrayList<>();
    public Runnable onEnd;
    /** 是否为关键节点（已自动存档）。 */
    public boolean node;

    public Ev(String title) {
        this.title = title;
    }

    public Ev say(String who, String text) {
        pages.add(new Page(who, text));
        return this;
    }

    public Ev narr(String text) {
        return say(null, text);
    }

    public Ev me(String text) {
        return say(ME, text);
    }

    public Ev choice(String text, Runnable action) {
        choices.add(new Choice(text, null, true, action));
        return this;
    }

    public Ev choice(String text, String hint, boolean enabled, Runnable action) {
        choices.add(new Choice(text, hint, enabled, action));
        return this;
    }

    public Ev then(Runnable r) {
        onEnd = r;
        return this;
    }

    public Ev bg(int variant) {
        bg = variant;
        return this;
    }

    public Ev bgm(int theme) {
        bgm = theme;
        return this;
    }
}
