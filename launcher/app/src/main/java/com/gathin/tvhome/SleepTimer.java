package com.gathin.tvhome;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.Locale;

/**
 * Singleton countdown timer that drives the launcher clock-area swap and the HomeKeyService overlay.
 * All mutating methods must be called on the main thread.
 * Preset cycle: Off(0) → 15 → 30 → 60 → 120 → Off(0)
 */
final class SleepTimer {
    private static final String TAG = "TvHome.SleepTimer";
    private static final int[] PRESETS = {0, 15, 30, 60, 120};

    interface Listener {
        void onTick(long remainingMs);
        void onExpired();
    }

    private static SleepTimer instance;

    static SleepTimer get() {
        if (instance == null) instance = new SleepTimer();
        return instance;
    }

    private CountDownTimer timer;
    private long remainingMs;
    private int activeMinutes;
    private Listener listener;

    private SleepTimer() {}

    void setListener(Listener l) { this.listener = l; }

    boolean isActive() { return timer != null; }

    int getActiveMinutes() { return activeMinutes; }

    long getRemainingMs() { return remainingMs; }

    void start(int minutes) {
        cancel();
        if (minutes <= 0) return;
        activeMinutes = minutes;
        remainingMs = minutes * 60_000L;
        timer = new CountDownTimer(remainingMs, 1000L) {
            @Override public void onTick(long ms) {
                remainingMs = ms;
                if (listener != null) listener.onTick(ms);
            }
            @Override public void onFinish() {
                timer = null;
                remainingMs = 0;
                activeMinutes = 0;
                Log.d(TAG, "sleep timer expired");
                if (listener != null) listener.onExpired();
            }
        }.start();
        Log.d(TAG, "sleep timer started: " + minutes + " min");
    }

    void cancel() {
        if (timer != null) { timer.cancel(); timer = null; }
        remainingMs = 0;
        activeMinutes = 0;
    }

    /** Returns the next preset after the current one, cycling Off→15→30→60→120→Off. */
    static int nextPresetMinutes(int current) {
        for (int i = 0; i < PRESETS.length; i++) {
            if (PRESETS[i] == current) return PRESETS[(i + 1) % PRESETS.length];
        }
        return PRESETS[1]; // unknown value → start from first non-zero preset
    }

    /** Formats milliseconds as M:SS (< 1 h) or H:MM:SS (≥ 1 h). */
    static String formatMs(long ms) {
        long total = ms / 1000L;
        long h = total / 3600, m = (total % 3600) / 60, s = total % 60;
        if (h > 0) return String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s);
        return String.format(Locale.ROOT, "%d:%02d", m, s);
    }
}
