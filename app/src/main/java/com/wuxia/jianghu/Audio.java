package com.wuxia.jianghu;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 程序合成的音乐与音效，无需任何音频资源文件：
 * 古筝拨弦（Karplus-Strong 算法）+ 五声音阶旋律生成 + 战鼓 / 木鱼节奏。
 * 混音在独立线程中进行，通过 AudioTrack 流式输出。
 */
public final class Audio implements Runnable {
    public static final int RATE = 22050;
    private static final int CHUNK = 512;
    private static final int REST = -999;
    private static final float MUSIC_VOL = 0.32f;
    private static final float SFX_VOL = 0.75f;

    public static final int KEEP = -2;
    public static final int BGM_NONE = -1;
    public static final int BGM_TITLE = 0;
    public static final int BGM_TOWN = 1;
    public static final int BGM_WILD = 2;
    public static final int BGM_BATTLE = 3;
    public static final int BGM_BOSS = 4;
    public static final int BGM_SAD = 5;
    public static final int BGM_ENDING = 6;

    public static final int SFX_CLICK = 0;
    public static final int SFX_HIT = 1;
    public static final int SFX_CRIT = 2;
    public static final int SFX_HEAL = 3;
    public static final int SFX_MISS = 4;
    public static final int SFX_WIN = 5;
    public static final int SFX_LOSE = 6;
    public static final int SFX_LEVEL = 7;
    public static final int SFX_SKILL = 8;
    public static final int SFX_COIN = 9;
    public static final int SFX_HURT = 10;
    public static final int SFX_PAGE = 11;
    public static final int SFX_EQUIP = 12;
    public static final int SFX_GONG = 13;
    public static final int SFX_FAIL = 14;
    private static final int SFX_COUNT = 15;

    private static final class Voice {
        final float[] data;
        final float vol;
        int pos;

        Voice(float[] data, int delay, float vol) {
            this.data = data;
            this.pos = -delay;
            this.vol = vol;
        }
    }

    private final Object lock = new Object();
    private final ArrayList<Voice> sfxVoices = new ArrayList<>();
    private final ArrayList<Voice> musicVoices = new ArrayList<>();
    private final Random rnd = new Random();

    private float[][] sfx;
    private float[][] bank;
    private int[] bankMidi;
    private float[] drum;
    private float[] block;

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private volatile boolean ready = false;
    private volatile boolean musicOn = true;
    private volatile boolean sfxOn = true;
    private volatile int wantTheme = BGM_NONE;
    private Thread thread;

    // ---- 音乐状态：仅音频线程访问 ----
    private int theme = Integer.MIN_VALUE;
    private int stepLen = RATE / 4;
    private int untilStep = 1;
    private int step = 0;
    private int loops = 0;
    private int rootIdx = 0;
    private int bassEvery = 8;
    private int drumStyle = 0;
    private float density = 0.6f;
    private float melodyVol = 0.5f;
    private final int[] phrase = new int[32];

    public void start() {
        if (thread != null) {
            return;
        }
        thread = new Thread(this, "jianghu-audio");
        thread.setDaemon(true);
        thread.start();
    }

    public void sfx(int id) {
        if (!ready || !sfxOn) {
            return;
        }
        float[][] s = sfx;
        if (s == null || id < 0 || id >= s.length || s[id] == null) {
            return;
        }
        synchronized (lock) {
            if (sfxVoices.size() >= 10) {
                sfxVoices.remove(0);
            }
            sfxVoices.add(new Voice(s[id], 0, 1f));
        }
    }

    public void music(int t) {
        if (t != KEEP) {
            wantTheme = t;
        }
    }

    public void setMusicOn(boolean on) {
        musicOn = on;
    }

    public void setSfxOn(boolean on) {
        sfxOn = on;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void release() {
        running = false;
        Thread t = thread;
        thread = null;
        if (t != null) {
            t.interrupt();
            try {
                t.join(400);
            } catch (InterruptedException ignored) {
                // 退出时不必等待
            }
        }
    }

    // ---------------- 线程主循环 ----------------

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        AudioTrack track = null;
        try {
            build();
            int minBuf = AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int bufBytes = Math.max(minBuf, CHUNK * 2 * 4);
            track = new AudioTrack(AudioManager.STREAM_MUSIC, RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufBytes, AudioTrack.MODE_STREAM);
            if (track.getState() != AudioTrack.STATE_INITIALIZED) {
                return;
            }
            ready = true;
            track.play();
            boolean playing = true;
            float[] mix = new float[CHUNK];
            short[] out = new short[CHUNK];
            while (running) {
                if (paused) {
                    if (playing) {
                        track.pause();
                        track.flush();
                        playing = false;
                    }
                    Thread.sleep(60);
                    continue;
                }
                if (!playing) {
                    track.play();
                    playing = true;
                }
                Arrays.fill(mix, 0f);
                renderMusic(mix);
                synchronized (lock) {
                    mixVoices(sfxVoices, mix, SFX_VOL);
                }
                for (int i = 0; i < CHUNK; i++) {
                    float v = mix[i];
                    if (v > 1f) {
                        v = 1f;
                    } else if (v < -1f) {
                        v = -1f;
                    }
                    out[i] = (short) (v * 32000f);
                }
                track.write(out, 0, CHUNK);
            }
        } catch (Throwable ignored) {
            // 声音出错绝不能影响游戏本身
        } finally {
            ready = false;
            if (track != null) {
                try {
                    track.stop();
                } catch (Throwable ignored) {
                    // ignore
                }
                track.release();
            }
        }
    }

    private static void mixVoices(ArrayList<Voice> list, float[] mix, float master) {
        for (int v = list.size() - 1; v >= 0; v--) {
            Voice vc = list.get(v);
            float[] d = vc.data;
            float g = vc.vol * master;
            int p = vc.pos;
            for (int i = 0; i < CHUNK; i++, p++) {
                if (p < 0) {
                    continue;
                }
                if (p >= d.length) {
                    break;
                }
                mix[i] += d[p] * g;
            }
            vc.pos += CHUNK;
            if (vc.pos >= d.length) {
                list.remove(v);
            }
        }
    }

    // ---------------- 音乐 ----------------

    private void renderMusic(float[] mix) {
        int want = wantTheme;
        if (want != theme) {
            theme = want;
            musicVoices.clear();
            if (theme >= 0) {
                startTheme(theme);
            }
        }
        if (theme < 0) {
            return;
        }
        if (!musicOn) {
            musicVoices.clear();
            return;
        }
        for (int i = 0; i < CHUNK; i++) {
            untilStep--;
            if (untilStep <= 0) {
                untilStep = stepLen;
                triggerStep(i);
                step++;
            }
        }
        while (musicVoices.size() > 28) {
            musicVoices.remove(0);
        }
        mixVoices(musicVoices, mix, MUSIC_VOL);
    }

    private void startTheme(int t) {
        step = 0;
        untilStep = 1;
        loops = 0;
        float stepSec;
        int rootMidi;
        switch (t) {
            case BGM_TITLE:
                stepSec = 0.30f;
                rootMidi = 57;
                bassEvery = 8;
                drumStyle = 0;
                density = 0.55f;
                melodyVol = 0.55f;
                break;
            case BGM_TOWN:
                stepSec = 0.22f;
                rootMidi = 60;
                bassEvery = 4;
                drumStyle = 1;
                density = 0.7f;
                melodyVol = 0.5f;
                break;
            case BGM_WILD:
                stepSec = 0.27f;
                rootMidi = 62;
                bassEvery = 8;
                drumStyle = 0;
                density = 0.5f;
                melodyVol = 0.5f;
                break;
            case BGM_BATTLE:
                stepSec = 0.145f;
                rootMidi = 57;
                bassEvery = 4;
                drumStyle = 2;
                density = 0.75f;
                melodyVol = 0.45f;
                break;
            case BGM_BOSS:
                stepSec = 0.13f;
                rootMidi = 52;
                bassEvery = 2;
                drumStyle = 3;
                density = 0.8f;
                melodyVol = 0.45f;
                break;
            case BGM_SAD:
                stepSec = 0.40f;
                rootMidi = 57;
                bassEvery = 16;
                drumStyle = 0;
                density = 0.4f;
                melodyVol = 0.55f;
                break;
            default:
                stepSec = 0.28f;
                rootMidi = 60;
                bassEvery = 8;
                drumStyle = 0;
                density = 0.65f;
                melodyVol = 0.5f;
                break;
        }
        stepLen = Math.max(1, (int) (stepSec * RATE));
        rootIdx = idxOf(rootMidi);
        makePhrase();
    }

    private int[] motif(int startDeg) {
        int[] m = new int[8];
        int deg = startDeg;
        for (int i = 0; i < 8; i++) {
            if (i > 0 && rnd.nextFloat() > density) {
                m[i] = REST;
                continue;
            }
            deg = Math.max(-2, Math.min(7, deg + rnd.nextInt(5) - 2));
            m[i] = deg;
        }
        return m;
    }

    /** 生成一段 32 步的乐句：A A' B A''，结尾回到主音。 */
    private void makePhrase() {
        int[] a = motif(0);
        int[] b = motif(2 + rnd.nextInt(3));
        for (int i = 0; i < 8; i++) {
            phrase[i] = a[i];
            phrase[8 + i] = a[i];
            phrase[16 + i] = b[i];
            phrase[24 + i] = a[i];
        }
        phrase[14] = rnd.nextBoolean() ? REST : Math.min(7, Math.max(-2, (a[6] == REST ? 1 : a[6] + 1)));
        phrase[15] = REST;
        phrase[30] = 0;
        phrase[31] = REST;
    }

    private void triggerStep(int offset) {
        int s = step % 32;
        if (s == 0 && step > 0) {
            loops++;
            if (loops % 4 == 0) {
                makePhrase();
            }
        }
        int d = phrase[s];
        if (d != REST) {
            addNote(rootIdx + d, offset, melodyVol);
        }
        if (step % bassEvery == 0) {
            int bassDeg = ((step / bassEvery) % 2 == 0) ? 0 : 3;
            addNote(rootIdx + bassDeg - 5, offset, 0.55f);
        }
        int s16 = step % 16;
        switch (drumStyle) {
            case 1:
                if (s16 == 4 || s16 == 12) {
                    addSample(block, offset, 0.25f);
                }
                break;
            case 2:
                if (s16 == 0 || s16 == 6 || s16 == 8 || s16 == 11) {
                    addSample(drum, offset, 0.8f);
                }
                if (s16 == 4 || s16 == 12 || s16 == 14) {
                    addSample(block, offset, 0.35f);
                }
                break;
            case 3:
                if (s16 % 3 == 0 || s16 == 8 || s16 == 15) {
                    addSample(drum, offset, 0.9f);
                }
                if (s16 % 4 == 2) {
                    addSample(block, offset, 0.3f);
                }
                break;
            default:
                break;
        }
    }

    private void addNote(int idx, int offset, float vol) {
        while (idx < 0) {
            idx += 5;
        }
        while (idx >= bank.length) {
            idx -= 5;
        }
        musicVoices.add(new Voice(bank[idx], offset, vol));
    }

    private void addSample(float[] d, int offset, float vol) {
        musicVoices.add(new Voice(d, offset, vol));
    }

    private int idxOf(int midi) {
        int best = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < bankMidi.length; i++) {
            int dist = Math.abs(bankMidi[i] - midi);
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return best;
    }

    // ---------------- 采样合成 ----------------

    private void build() {
        Random r = new Random(7);
        ArrayList<Integer> midi = new ArrayList<>();
        for (int m = 40; m <= 88; m++) {
            int pc = m % 12;
            if (pc == 0 || pc == 2 || pc == 4 || pc == 7 || pc == 9) {
                midi.add(m);
            }
        }
        bankMidi = new int[midi.size()];
        bank = new float[midi.size()][];
        for (int i = 0; i < bankMidi.length; i++) {
            bankMidi[i] = midi.get(i);
            float f = (float) (440.0 * Math.pow(2.0, (bankMidi[i] - 69) / 12.0));
            boolean low = bankMidi[i] < 52;
            bank[i] = pluck(f, low ? 1.6f : 1.1f, low ? 0.998f : 0.996f, r);
        }
        drum = makeDrum(r);
        block = makeBlock();

        float[][] s = new float[SFX_COUNT][];
        int c4 = idxOf(60);
        int c5 = idxOf(72);
        int a3 = idxOf(57);
        float[] b;

        b = buf(0.07f);
        sine(b, 0f, 1500, 1100, 0.6f, 60, 0.07f);
        sine(b, 0f, 3000, 2600, 0.15f, 90, 0.05f);
        s[SFX_CLICK] = norm(b, 0.5f);

        b = buf(0.25f);
        noise(b, r, 0f, 0.9f, 28, 0.2f, 0.35f, false);
        sine(b, 0f, 170, 60, 1f, 16, 0.25f);
        s[SFX_HIT] = norm(b, 0.85f);

        b = buf(0.7f);
        noise(b, r, 0f, 1f, 22, 0.25f, 0.45f, false);
        sine(b, 0f, 200, 55, 1f, 12, 0.3f);
        sine(b, 0.02f, 2100, 2050, 0.35f, 5, 0.65f);
        sine(b, 0.02f, 3170, 3100, 0.22f, 6, 0.6f);
        noise(b, r, 0.07f, 0.6f, 30, 0.15f, 0.4f, false);
        s[SFX_CRIT] = norm(b, 0.95f);

        b = buf(1.0f);
        for (int k = 0; k < 5; k++) {
            note(b, k * 0.08f, c5 + k, 0.5f);
        }
        s[SFX_HEAL] = norm(b, 0.6f);

        b = buf(0.28f);
        noise(b, r, 0f, 0.7f, 0, 0.28f, 0.12f, true);
        s[SFX_MISS] = norm(b, 0.45f);

        b = buf(1.6f);
        int[] up = {0, 1, 2, 3, 5};
        for (int k = 0; k < up.length; k++) {
            note(b, k * 0.11f, c4 + up[k], 0.5f);
        }
        note(b, 0.62f, c4, 0.35f);
        note(b, 0.62f, c4 + 2, 0.35f);
        note(b, 0.62f, c4 + 5, 0.35f);
        s[SFX_WIN] = norm(b, 0.7f);

        b = buf(1.8f);
        int[] dn = {4, 2, 1, 0, -2};
        for (int k = 0; k < dn.length; k++) {
            note(b, k * 0.24f, a3 + dn[k], 0.5f);
        }
        s[SFX_LOSE] = norm(b, 0.6f);

        b = buf(1.5f);
        for (int k = 0; k < 7; k++) {
            note(b, k * 0.055f, c4 + k, 0.45f);
        }
        sine(b, 0.38f, 1046, 1040, 0.25f, 2.5f, 1.1f);
        sine(b, 0.38f, 1568, 1560, 0.15f, 3f, 1.0f);
        s[SFX_LEVEL] = norm(b, 0.7f);

        b = buf(0.55f);
        noise(b, r, 0f, 0.8f, 0, 0.3f, 0.2f, true);
        sine(b, 0.14f, 1700, 1500, 0.3f, 7, 0.4f);
        noise(b, r, 0.16f, 0.6f, 30, 0.15f, 0.4f, false);
        s[SFX_SKILL] = norm(b, 0.75f);

        b = buf(0.4f);
        sine(b, 0f, 1900, 1900, 0.4f, 18, 0.2f);
        sine(b, 0.08f, 2550, 2550, 0.4f, 14, 0.3f);
        s[SFX_COIN] = norm(b, 0.5f);

        b = buf(0.32f);
        sine(b, 0f, 130, 45, 1f, 13, 0.32f);
        noise(b, r, 0f, 0.5f, 22, 0.25f, 0.2f, false);
        s[SFX_HURT] = norm(b, 0.8f);

        b = buf(0.16f);
        noise(b, r, 0f, 0.35f, 0, 0.16f, 0.18f, true);
        s[SFX_PAGE] = norm(b, 0.3f);

        b = buf(0.7f);
        float[] fr = {523f, 1347f, 2211f, 3020f};
        for (int k = 0; k < fr.length; k++) {
            sine(b, 0f, fr[k], fr[k] * 0.995f, 0.25f, 5 + k * 2, 0.7f);
        }
        noise(b, r, 0f, 0.4f, 60, 0.05f, 0.6f, false);
        s[SFX_EQUIP] = norm(b, 0.6f);

        b = buf(2.6f);
        float[] gf = {98f, 148.5f, 220.7f, 311f, 457f};
        for (int k = 0; k < gf.length; k++) {
            sine(b, 0f, gf[k], gf[k] * 0.985f, 0.3f, 1.1f + k * 0.5f, 2.6f);
        }
        noise(b, r, 0f, 0.3f, 30, 0.1f, 0.3f, false);
        s[SFX_GONG] = norm(b, 0.8f);

        b = buf(0.5f);
        sine(b, 0f, 420, 330, 0.5f, 6, 0.18f);
        sine(b, 0.2f, 330, 220, 0.5f, 6, 0.28f);
        s[SFX_FAIL] = norm(b, 0.5f);

        sfx = s;
    }

    private static float[] pluck(float freq, float sec, float decay, Random r) {
        int n = (int) (RATE * sec);
        float[] out = new float[n];
        int period = Math.max(2, Math.round(RATE / freq));
        float[] buf = new float[period];
        float prev = 0f;
        for (int i = 0; i < period; i++) {
            float x = r.nextFloat() * 2f - 1f;
            prev = prev * 0.5f + x * 0.5f;
            buf[i] = prev;
        }
        int idx = 0;
        float peak = 1e-6f;
        for (int i = 0; i < n; i++) {
            int nx = idx + 1 == period ? 0 : idx + 1;
            float v = buf[idx];
            out[i] = v;
            buf[idx] = decay * 0.5f * (v + buf[nx]);
            idx = nx;
            float a = Math.abs(v);
            if (a > peak) {
                peak = a;
            }
        }
        float g = 0.8f / peak;
        int fade = Math.max(1, n / 6);
        for (int i = 0; i < n; i++) {
            float e = 1f;
            if (i < 32) {
                e = i / 32f;
            }
            if (i > n - fade) {
                e *= (n - i) / (float) fade;
            }
            out[i] *= g * e;
        }
        return out;
    }

    private static float[] makeDrum(Random r) {
        int n = (int) (RATE * 0.45f);
        float[] d = new float[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            double f = 55 + 95 * Math.exp(-t * 25);
            ph += 2 * Math.PI * f / RATE;
            double env = Math.exp(-t * 8);
            double nz = (r.nextFloat() * 2 - 1) * Math.exp(-t * 60) * 0.35;
            d[i] = (float) (Math.sin(ph) * env * 0.95 + nz);
        }
        return d;
    }

    private static float[] makeBlock() {
        int n = (int) (RATE * 0.08f);
        float[] d = new float[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            d[i] = (float) (Math.sin(2 * Math.PI * 950 * t) * Math.exp(-t * 70) * 0.6
                    + Math.sin(2 * Math.PI * 1900 * t) * Math.exp(-t * 90) * 0.2);
        }
        return d;
    }

    private static float[] buf(float sec) {
        return new float[Math.max(1, (int) (RATE * sec))];
    }

    private static void sine(float[] b, float startSec, float f0, float f1, float amp, float decay, float len) {
        int s = (int) (startSec * RATE);
        int n = (int) (len * RATE);
        double ph = 0;
        for (int i = 0; i < n && s + i < b.length; i++) {
            double t = i / (double) RATE;
            double k = n > 1 ? i / (double) (n - 1) : 0;
            double f = f0 + (f1 - f0) * k;
            ph += 2 * Math.PI * f / RATE;
            double env = Math.exp(-t * decay);
            if (i < 40) {
                env *= i / 40.0;
            }
            b[s + i] += (float) (Math.sin(ph) * env * amp);
        }
    }

    private static void noise(float[] b, Random r, float startSec, float amp, float decay, float len, float lp, boolean swell) {
        int s = (int) (startSec * RATE);
        int n = Math.max(1, (int) (len * RATE));
        float y = 0f;
        for (int i = 0; i < n && s + i < b.length; i++) {
            double t = i / (double) RATE;
            double env = swell ? Math.sin(Math.PI * i / n) : Math.exp(-t * decay);
            float x = r.nextFloat() * 2f - 1f;
            y += lp * (x - y);
            b[s + i] += (float) (y * env * amp);
        }
    }

    private void note(float[] b, float startSec, int idx, float amp) {
        if (idx < 0 || idx >= bank.length) {
            return;
        }
        float[] src = bank[idx];
        int s = (int) (startSec * RATE);
        for (int i = 0; i < src.length && s + i < b.length; i++) {
            b[s + i] += src[i] * amp;
        }
    }

    private static float[] norm(float[] b, float target) {
        float peak = 1e-6f;
        for (float v : b) {
            float a = Math.abs(v);
            if (a > peak) {
                peak = a;
            }
        }
        float g = target / peak;
        for (int i = 0; i < b.length; i++) {
            b[i] *= g;
        }
        return b;
    }
}
