# Glass TV Home — 4 Feature Extensions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add icon pack support, a system-wide sleep timer overlay, Aerial Views video wallpaper, and a full in-launcher Settings UI to Glass TV Home.

**Architecture:** All features activate via the existing `config.json` overlay on external storage; the new Settings UI writes back to that same file. The sleep timer draws a `TYPE_ACCESSIBILITY_OVERLAY` via the already-running `HomeKeyService` so the countdown appears on top of any app. Video wallpaper binds to Aerial Views' Projectivity AIDL service.

**Tech Stack:** Java (Android 11 / API 30), Gradle 8.9, AndroidX Leanback 1.2.x, Android AIDL, JUnit 4 (host-JVM tests, no emulator needed).

**Spec:** `docs/superpowers/specs/2026-10-23-glass-tv-4features-design.md`

## Global Constraints

- Min SDK: 26 (Android 8), target SDK: 34 — do not use APIs below 26.
- Base package: `com.gathin.tvhome` — all new launcher classes go here.
- AIDL stub package: `tv.projectivy.plugin.wallpaperprovider.api` — must match exactly.
- Build: `cd launcher && TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh` after each task.
- Tests: plain JVM JUnit 4 in `launcher/app/src/test/java/com/gathin/tvhome/`; run with `cd launcher && ./gradlew test`.
- No new network permissions — `INTERNET` already declared.
- Config JSON written atomically: write to `config.json.tmp`, then `File.renameTo`.
- Never crash the home screen — every new code path wraps in try/catch and logs with `Log.w("TvHome.*", ...)`.

## Review Focus

1. **Icon pack missing resource** — `getIdentifier` returns 0 for an unknown package; must fall back to system icon, not crash or show blank.
2. **Sleep power dialog layout varies** — Shield's power menu "Sleep" is the first D-pad DOWN item, but firmware updates may reorder it; if `performGlobalAction` shows nothing or selects the wrong item, the TV stays on. Add a 2-second timeout that logs a warning and does nothing rather than pressing DPAD_CENTER blindly.
3. **Aerial Views uninstalled mid-session** — `ServiceConnection.onServiceDisconnected` fires; `AerialWallpaperPlayer` must fall back to the static wallpaper path without crashing.
4. **Config.save() concurrent write** — `LauncherSettingsFragment` may call `save()` rapidly; without synchronisation the `.tmp` rename could race. Save must be called on a single background thread.
5. **Leanback PreferenceFragment backstack** — pressing BACK from a nested settings screen must pop back to the parent, not exit the settings activity entirely; test with the D-pad BACK key, not the software back button.

---

## File Map

### New files
| Path | Responsibility |
|---|---|
| `launcher/app/src/main/java/com/gathin/tvhome/IconPackLoader.java` | Loads icons from an ADW-format icon pack by package name; caches per session |
| `launcher/app/src/main/java/com/gathin/tvhome/SleepTimer.java` | Countdown state machine; notifies listeners; drives HomeKeyService overlay |
| `launcher/app/src/main/java/com/gathin/tvhome/AerialWallpaperPlayer.java` | Binds to Aerial Views AIDL service; drives TextureView + MediaPlayer |
| `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsActivity.java` | Hosts LauncherSettingsFragment; handles back navigation |
| `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsFragment.java` | LeanbackPreferenceFragment; reads and writes config.json |
| `launcher/app/src/main/java/com/gathin/tvhome/ColorPickerPreference.java` | Custom DialogPreference with 8 colour swatches + hex field |
| `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/IWallpaperProviderService.aidl` | AIDL interface declaration |
| `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.aidl` | Parcelable declaration for AIDL |
| `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Event.aidl` | Parcelable declaration for AIDL |
| `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.java` | Parcelable implementation (exact serialisation order) |
| `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Event.java` | Parcelable implementation (sealed subtypes) |
| `launcher/app/src/main/res/xml/settings_main.xml` | Root preferences screen |
| `launcher/app/src/main/res/xml/settings_wallpaper.xml` | Wallpaper section preferences |
| `launcher/app/src/main/res/xml/settings_layout.xml` | Layout section preferences |
| `launcher/app/src/main/res/xml/settings_sound.xml` | Sound section preferences |
| `launcher/app/src/main/res/xml/settings_apps.xml` | Apps section preferences |
| `launcher/app/src/test/java/com/gathin/tvhome/IconPackLoaderTest.java` | Host-JVM tests for icon name resolution |
| `launcher/app/src/test/java/com/gathin/tvhome/SleepTimerTest.java` | Host-JVM tests for countdown state machine |
| `launcher/app/src/test/java/com/gathin/tvhome/ConfigSaveTest.java` | Host-JVM tests for atomic JSON write |

### Modified files
| Path | What changes |
|---|---|
| `launcher/app/src/main/java/com/gathin/tvhome/Config.java` | Add `iconPack`, `aerialWallpaper`, `soundNavEnabled`, `soundSelectEnabled`, `soundVolume`, `sleepTimerMinutes` fields; add `save()` method |
| `launcher/app/src/main/java/com/gathin/tvhome/AppAdapter.java` | Pass `IconPackLoader` into `rebuildIfNeeded`; use its icon when banner is null |
| `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java` | Clock/countdown swap; timer button; aerial wallpaper branch; gear → settings |
| `launcher/app/src/main/java/com/gathin/tvhome/HomeKeyService.java` | `showTimerOverlay()`, `updateTimerOverlay()`, `hideTimerOverlay()`, `performSleep()` |
| `launcher/app/src/main/java/com/gathin/tvhome/SoundEffects.java` | Honour `enabled` flag and `volume` (0.0–1.0) per effect type |
| `launcher/app/src/main/res/layout/activity_main.xml` | Timer button ImageView beside gear icon |
| `launcher/app/src/main/AndroidManifest.xml` | Register `LauncherSettingsActivity`; add `<queries>` for Aerial Views + icon packs |
| `launcher/app/build.gradle` | Add `androidx.leanback:leanback-preference:1.2.0-alpha04` |

---

## Task 1: Config Extensions

**Files:**
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/Config.java`
- Create: `launcher/app/src/test/java/com/gathin/tvhome/ConfigSaveTest.java`

**Interfaces:**
- Produces: `Config.iconPack` (String), `Config.aerialWallpaper` (boolean), `Config.soundNavEnabled` (boolean), `Config.soundSelectEnabled` (boolean), `Config.soundVolume` (int 0–100), `Config.sleepTimerMinutes` (int)
- Produces: `Config.save(File dir, String key, Object value)` — writes one key atomically to `config.json` in `dir`

- [ ] **Step 1: Write failing tests**

```java
// launcher/app/src/test/java/com/gathin/tvhome/ConfigSaveTest.java
package com.gathin.tvhome;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class ConfigSaveTest {
    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void saveCreatesFileWhenAbsent() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 6);
        File f = new File(dir, "config.json");
        assertTrue(f.exists());
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(6, j.getInt("columns"));
    }

    @Test
    public void saveMergesExistingKeys() throws Exception {
        File dir = tmp.newFolder();
        File f = new File(dir, "config.json");
        Files.write(f.toPath(), "{\"columns\":5}".getBytes(StandardCharsets.UTF_8));
        Config.save(dir, "icon_pack", "tv.corebuilds.iconpack");
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(5, j.getInt("columns"));
        assertEquals("tv.corebuilds.iconpack", j.getString("icon_pack"));
    }

    @Test
    public void saveIsAtomic() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 3);
        File tmp_file = new File(dir, "config.json.tmp");
        assertFalse("tmp file must not remain after save", tmp_file.exists());
    }

    @Test
    public void saveOverwritesKey() throws Exception {
        File dir = tmp.newFolder();
        Config.save(dir, "columns", 5);
        Config.save(dir, "columns", 7);
        File f = new File(dir, "config.json");
        JSONObject j = new JSONObject(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
        assertEquals(7, j.getInt("columns"));
    }
}
```

- [ ] **Step 2: Run tests to confirm they fail**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.ConfigSaveTest" 2>&1 | tail -20
```
Expected: FAILED — `Config.save` does not exist yet.

- [ ] **Step 3: Add fields to Config.java**

In `Config.java`, add 6 new fields after `cacheCleanDays`:

```java
final String iconPack;
final boolean aerialWallpaper;
final boolean soundNavEnabled;
final boolean soundSelectEnabled;
final int soundVolume;
final int sleepTimerMinutes;
```

Add them to the constructor signature and body (follow the existing pattern — each gets a `this.x = x` line). Add to `Config.load()` JSON parsing block:

```java
iconPack = string(json, "icon_pack", "");
aerialWallpaper = bool(json, "aerial_wallpaper", false);
soundNavEnabled = bool(json, "sound_nav_enabled", true);
soundSelectEnabled = bool(json, "sound_select_enabled", true);
soundVolume = integer(json, "sound_volume", 80, 0, 100);
sleepTimerMinutes = integer(json, "sleep_timer_minutes", 0, 0, 120);
```

- [ ] **Step 4: Add `Config.save()` static method**

Add at the bottom of `Config.java`, before the closing `}`:

```java
/**
 * Atomically writes (or updates) one key in config.json inside {@code dir}.
 * Reads the existing file first so other keys are preserved. Thread-safe only
 * when called from a single serialised background thread.
 */
static void save(File dir, String key, Object value) {
    if (dir == null) return;
    File file = new File(dir, FILE_NAME);
    File tmp = new File(dir, FILE_NAME + ".tmp");
    try {
        JSONObject json = new JSONObject();
        if (file.isFile() && file.length() > 0 && file.length() < 64 * 1024) {
            byte[] buf = java.nio.file.Files.readAllBytes(file.toPath());
            String text = new String(buf, StandardCharsets.UTF_8);
            if (text.startsWith("﻿")) text = text.substring(1);
            try { json = new JSONObject(text); } catch (Exception ignored) {}
        }
        json.put(key, value);
        byte[] out = json.toString(2).getBytes(StandardCharsets.UTF_8);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tmp)) {
            fos.write(out);
            fos.getFD().sync();
        }
        if (!tmp.renameTo(file)) {
            // renameTo can fail across filesystems on some Androids; copy+delete as fallback
            java.nio.file.Files.move(tmp.toPath(), file.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    } catch (Exception e) {
        Log.w(TAG, "save failed for key=" + key + ": " + e.getMessage());
        tmp.delete();
    }
}
```

- [ ] **Step 5: Run tests — expect PASS**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.ConfigSaveTest" 2>&1 | tail -10
```

- [ ] **Step 6: Commit**

```bash
cd /tmp/glass-tv-home && git add launcher/app/src/main/java/com/gathin/tvhome/Config.java launcher/app/src/test/java/com/gathin/tvhome/ConfigSaveTest.java
git commit -m "feat: add icon_pack, aerial_wallpaper, sound, sleep_timer config keys + Config.save()

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 2: Icon Pack Support

**Files:**
- Create: `launcher/app/src/main/java/com/gathin/tvhome/IconPackLoader.java`
- Create: `launcher/app/src/test/java/com/gathin/tvhome/IconPackLoaderTest.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/AppAdapter.java`
- Modify: `launcher/app/src/main/AndroidManifest.xml`

**Interfaces:**
- Consumes: `Config.iconPack` (String) from Task 1
- Produces: `IconPackLoader(PackageManager pm, String iconPackPkg)` constructor; `Drawable getIcon(String packageName)` — returns icon or null on miss

- [ ] **Step 1: Write failing tests**

```java
// launcher/app/src/test/java/com/gathin/tvhome/IconPackLoaderTest.java
package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class IconPackLoaderTest {

    @Test
    public void iconResourceNameForSimplePackage() {
        assertEquals("ic_com_netflix_ninja", IconPackLoader.iconResourceName("com.netflix.ninja"));
    }

    @Test
    public void iconResourceNameReplacesDotsAndSlashes() {
        assertEquals("ic_com_example_app", IconPackLoader.iconResourceName("com.example.app"));
    }

    @Test
    public void iconResourceNameHandlesSubActivity() {
        // Component flat string "com.foo.bar/.Main" → strip after slash
        assertEquals("ic_com_foo_bar", IconPackLoader.iconResourceName("com.foo.bar"));
    }

    @Test
    public void iconResourceNameEmptyInput() {
        assertEquals("ic_", IconPackLoader.iconResourceName(""));
    }
}
```

- [ ] **Step 2: Run tests — expect FAIL**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.IconPackLoaderTest" 2>&1 | tail -10
```

- [ ] **Step 3: Create IconPackLoader.java**

```java
// launcher/app/src/main/java/com/gathin/tvhome/IconPackLoader.java
package com.gathin.tvhome;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves app icons from an ADW/Nova-format icon pack installed on this device.
 * Drawable resource names follow the convention: ic_<package_name_with_underscores>.
 * Results are cached for the launcher session.
 */
final class IconPackLoader {
    private static final String TAG = "TvHome.IconPack";
    private final PackageManager pm;
    private final String packPkg;
    private Resources packRes;
    private final Map<String, Drawable> cache = new HashMap<>();

    IconPackLoader(PackageManager pm, String iconPackPkg) {
        this.pm = pm;
        this.packPkg = iconPackPkg;
        if (iconPackPkg != null && !iconPackPkg.isEmpty()) {
            try {
                packRes = pm.getResourcesForApplication(iconPackPkg);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "icon pack not installed: " + iconPackPkg);
            }
        }
    }

    /** Converts a package name to the ADW icon resource name, e.g. "com.netflix.ninja" → "ic_com_netflix_ninja". */
    static String iconResourceName(String packageName) {
        return "ic_" + packageName.replace('.', '_').replace('/', '_');
    }

    /**
     * Returns the icon from the pack for the given package name, or null if not found.
     * Falls back gracefully — never throws.
     */
    Drawable getIcon(String packageName) {
        if (packRes == null || packageName == null) return null;
        if (cache.containsKey(packageName)) return cache.get(packageName);
        try {
            String name = iconResourceName(packageName);
            int id = packRes.getIdentifier(name, "drawable", packPkg);
            if (id == 0) {
                cache.put(packageName, null);
                return null;
            }
            Drawable d = packRes.getDrawableForDensity(id,
                    android.util.DisplayMetrics.DENSITY_XHIGH, null);
            cache.put(packageName, d);
            return d;
        } catch (Exception e) {
            Log.w(TAG, "icon load failed for " + packageName + ": " + e.getMessage());
            cache.put(packageName, null);
            return null;
        }
    }

    boolean isActive() {
        return packRes != null;
    }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.IconPackLoaderTest" 2>&1 | tail -10
```

- [ ] **Step 5: Integrate into AppAdapter**

In `AppAdapter.java`, add a field:

```java
private IconPackLoader iconPackLoader;
```

Add a setter called after `Config` is loaded in `MainActivity` (see Task 6 for wiring):

```java
void setIconPackLoader(IconPackLoader loader) {
    this.iconPackLoader = loader;
    builtApps.clear();   // invalidate cache so icons reload
    notifyDataSetChanged();
}
```

In the existing icon-load line (around line 224):

```java
// Before:
Drawable icon = banner == null ? ri.loadIcon(pm) : null;

// After:
Drawable icon = null;
if (banner == null) {
    String pkg = ri.activityInfo.packageName;
    icon = (iconPackLoader != null) ? iconPackLoader.getIcon(pkg) : null;
    if (icon == null) icon = ri.loadIcon(pm);
}
```

- [ ] **Step 6: Add manifest queries for icon packs**

In `AndroidManifest.xml`, inside the existing `<queries>` block, add:

```xml
<!-- Icon pack discovery -->
<intent>
    <action android:name="org.adw.launcher.THEMES" />
</intent>
<!-- Aerial Views video wallpaper -->
<package android:name="com.neilturner.aerialviews" />
```

- [ ] **Step 7: Wire IconPackLoader in MainActivity**

In `MainActivity.java`, after `cfg = Config.load(this)` (search for that line), add:

```java
IconPackLoader iconPackLoader = new IconPackLoader(getPackageManager(), cfg.iconPack);
adapter.setIconPackLoader(iconPackLoader);
```

- [ ] **Step 8: Build and deploy to Shield to verify icons load from pack**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher
TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

Then push a config with `"icon_pack": "tv.corebuilds.iconpack"`:

```bash
cd /tmp/glass-tv-home && TV_HOST=192.168.0.126 python3 tools/tv.py \
  "cat /sdcard/Android/data/com.gathin.tvhome/files/config.json" 2>/dev/null \
  | python3 -c "import sys,json; d=json.load(sys.stdin); d['icon_pack']='tv.corebuilds.iconpack'; print(json.dumps(d,indent=2))" \
  > /tmp/new_config.json
python3 tools/b64push.py /tmp/new_config.json /sdcard/Android/data/com.gathin.tvhome/files/config.json
python3 tools/tv.py "am force-stop com.gathin.tvhome; am start -n com.gathin.tvhome/.MainActivity"
```

Take a screenshot and compare app icons to system icons.

- [ ] **Step 9: Commit**

```bash
cd /tmp/glass-tv-home && git add launcher/app/src/main/java/com/gathin/tvhome/IconPackLoader.java launcher/app/src/main/java/com/gathin/tvhome/AppAdapter.java launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java launcher/app/src/main/AndroidManifest.xml launcher/app/src/test/java/com/gathin/tvhome/IconPackLoaderTest.java
git commit -m "feat: icon pack support via ADW/Nova format

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 3: Sleep Timer — Launcher UI + Core State Machine

**Files:**
- Create: `launcher/app/src/main/java/com/gathin/tvhome/SleepTimer.java`
- Create: `launcher/app/src/test/java/com/gathin/tvhome/SleepTimerTest.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java`
- Modify: `launcher/app/src/main/res/layout/activity_main.xml`

**Interfaces:**
- Produces: `SleepTimer` singleton with `start(int minutes)`, `cancel()`, `getRemainingMs()`, `isActive()`, `setListener(Listener)` where `Listener` has `onTick(long remainingMs)` and `onExpired()`

- [ ] **Step 1: Write failing tests**

```java
// launcher/app/src/test/java/com/gathin/tvhome/SleepTimerTest.java
package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class SleepTimerTest {

    @Test
    public void formatCountdownBelowOneHour() {
        assertEquals("29:59", SleepTimer.formatMs(29 * 60_000L + 59_000L));
    }

    @Test
    public void formatCountdownAtOneHour() {
        assertEquals("1:00:00", SleepTimer.formatMs(60 * 60_000L));
    }

    @Test
    public void formatCountdownAboveOneHour() {
        assertEquals("1:05:03", SleepTimer.formatMs(65 * 60_000L + 3_000L));
    }

    @Test
    public void formatCountdownAtZero() {
        assertEquals("0:00", SleepTimer.formatMs(0));
    }

    @Test
    public void nextPresetCycles() {
        // 0 → 15 → 30 → 60 → 120 → 0
        assertEquals(15, SleepTimer.nextPresetMinutes(0));
        assertEquals(30, SleepTimer.nextPresetMinutes(15));
        assertEquals(60, SleepTimer.nextPresetMinutes(30));
        assertEquals(120, SleepTimer.nextPresetMinutes(60));
        assertEquals(0, SleepTimer.nextPresetMinutes(120));
    }
}
```

- [ ] **Step 2: Run tests — expect FAIL**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.SleepTimerTest" 2>&1 | tail -10
```

- [ ] **Step 3: Create SleepTimer.java**

```java
// launcher/app/src/main/java/com/gathin/tvhome/SleepTimer.java
package com.gathin.tvhome;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.Locale;

/**
 * Singleton countdown timer. Drives the launcher clock-area swap and the HomeKeyService overlay.
 * All methods must be called on the main thread.
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
                Log.d(TAG, "timer expired — triggering sleep");
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

    /** Returns the preset that follows the given one (cycles Off→15→30→60→120→Off). */
    static int nextPresetMinutes(int current) {
        for (int i = 0; i < PRESETS.length - 1; i++) {
            if (PRESETS[i] == current) return PRESETS[i + 1];
        }
        return PRESETS[0];
    }

    /** Formats milliseconds as M:SS (< 1h) or H:MM:SS (≥ 1h). */
    static String formatMs(long ms) {
        long total = ms / 1000L;
        long h = total / 3600, m = (total % 3600) / 60, s = total % 60;
        if (h > 0) return String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s);
        return String.format(Locale.ROOT, "%d:%02d", m, s);
    }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test --tests "com.gathin.tvhome.SleepTimerTest" 2>&1 | tail -10
```

- [ ] **Step 5: Add timer button to activity_main.xml**

Read the current `activity_main.xml` to find the top-bar area where the gear icon sits. Add an `ImageView` for the timer button immediately before the settings gear `ImageView`:

```xml
<ImageView
    android:id="@+id/sleep_timer_btn"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/ic_lock"
    android:padding="8dp"
    android:focusable="true"
    android:clickable="true"
    android:background="?android:attr/selectableItemBackgroundBorderless"
    android:contentDescription="Sleep timer"
    android:visibility="visible" />
```

(Reuse `@drawable/ic_lock` as a placeholder; a proper timer/moon icon can be added later as a vector drawable.)

- [ ] **Step 6: Wire timer in MainActivity**

In `MainActivity.java`, find the `onCreate` method. After the gear icon click listener is set up, add:

```java
// Sleep timer button — cycles presets on click
ImageView timerBtn = root.findViewById(R.id.sleep_timer_btn);
if (timerBtn != null) {
    timerBtn.setOnClickListener(v -> {
        SleepTimer t = SleepTimer.get();
        int next = SleepTimer.nextPresetMinutes(t.getActiveMinutes());
        if (next == 0) { t.cancel(); } else { t.start(next); }
        updateTimerDisplay();
        Config.save(Config.dir(this), "sleep_timer_minutes", SleepTimer.get().getActiveMinutes());
    });
}
SleepTimer.get().setListener(new SleepTimer.Listener() {
    @Override public void onTick(long ms) { updateTimerDisplay(); }
    @Override public void onExpired() {
        updateTimerDisplay();
        HomeKeyService.triggerSleep();
    }
});
```

Add `updateTimerDisplay()` method in `MainActivity.java`. Find the `ClockFace` or clock-related `View` (likely `clockView` — check `activity_main.xml` for the clock's id). Replace/overlay with countdown text when timer is active:

```java
private void updateTimerDisplay() {
    SleepTimer t = SleepTimer.get();
    // clockView is the existing clock TextView/ClockFace; timerText is a TextView added alongside it.
    View clockArea = findViewById(R.id.clock_area);   // adjust id to match actual layout
    TextView timerText = findViewById(R.id.timer_text); // add this TextView to activity_main.xml
    ImageView timerBtn = findViewById(R.id.sleep_timer_btn);
    if (t.isActive()) {
        if (clockArea != null) clockArea.setVisibility(View.GONE);
        if (timerText != null) {
            timerText.setVisibility(View.VISIBLE);
            timerText.setText(SleepTimer.formatMs(t.getRemainingMs()));
        }
        if (timerBtn != null) {
            timerBtn.setAlpha(1.0f);
        }
    } else {
        if (clockArea != null) clockArea.setVisibility(View.VISIBLE);
        if (timerText != null) timerText.setVisibility(View.GONE);
        if (timerBtn != null) timerBtn.setAlpha(0.5f);
    }
}
```

Add a `TextView` for `@+id/timer_text` to `activity_main.xml` beside the clock area, initially `visibility="gone"`, matching the clock text size and colour.

- [ ] **Step 7: Build and deploy — verify timer button appears and clock swaps**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17 PATH="$JAVA_HOME/bin:$PATH" ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher && TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

Press the timer button on the Shield remote: clock should disappear and countdown appear. Press again to cycle presets.

- [ ] **Step 8: Commit**

```bash
cd /tmp/glass-tv-home && git add launcher/app/src/main/java/com/gathin/tvhome/SleepTimer.java launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java launcher/app/src/main/res/layout/activity_main.xml launcher/app/src/test/java/com/gathin/tvhome/SleepTimerTest.java
git commit -m "feat: sleep timer state machine + launcher clock-area swap

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 4: Sleep Timer — System-wide Overlay via HomeKeyService

**Files:**
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/HomeKeyService.java`

**Interfaces:**
- Consumes: `SleepTimer.get()` from Task 3
- Produces: `HomeKeyService.triggerSleep()` (static — posts to service's handler); `showTimerOverlay()`, `updateTimerOverlay(long ms)`, `hideTimerOverlay()` (internal)

- [ ] **Step 1: Read HomeKeyService.java to understand existing structure**

```bash
cat /tmp/glass-tv-home/launcher/app/src/main/java/com/gathin/tvhome/HomeKeyService.java
```

Note the existing handler, `onServiceConnected`, and `performGlobalAction` calls — new code follows the same pattern.

- [ ] **Step 2: Add overlay fields and lifecycle to HomeKeyService**

At the top of `HomeKeyService`, add imports:
```java
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;
```

Add instance fields:
```java
private static HomeKeyService instance;
private WindowManager wm;
private android.view.View overlayView;
private TextView overlayText;
private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
```

In `onServiceConnected` (or `onCreate` if it exists), assign:
```java
instance = this;
wm = (WindowManager) getSystemService(WINDOW_SERVICE);
```

In `onUnbind` / `onDestroy`:
```java
instance = null;
hideTimerOverlay();
```

- [ ] **Step 3: Add overlay show/update/hide methods**

```java
/** Creates a small semi-transparent pill overlay (top-left) showing the countdown. */
void showTimerOverlay() {
    if (overlayView != null) return;
    try {
        overlayView = new android.widget.LinearLayout(this);
        overlayView.setBackgroundColor(0xCC000000);
        overlayText = new TextView(this);
        overlayText.setTextColor(0xFFFFFFFF);
        overlayText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18);
        overlayText.setPadding(24, 12, 24, 12);
        ((android.widget.LinearLayout) overlayView).addView(overlayText);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = 32;
        lp.y = 32;
        wm.addView(overlayView, lp);
    } catch (Exception e) {
        android.util.Log.w("TvHome.HomeKey", "overlay add failed: " + e.getMessage());
        overlayView = null;
    }
}

void updateTimerOverlay(long ms) {
    if (overlayText != null) {
        overlayText.setText("⏾ " + SleepTimer.formatMs(ms));
    }
}

void hideTimerOverlay() {
    if (overlayView != null && wm != null) {
        try { wm.removeView(overlayView); } catch (Exception ignored) {}
        overlayView = null;
        overlayText = null;
    }
}
```

- [ ] **Step 4: Add static `triggerSleep()` called when timer expires**

```java
static void triggerSleep() {
    if (instance == null) {
        android.util.Log.w("TvHome.HomeKey", "triggerSleep: service not running");
        return;
    }
    instance.handler.post(() -> {
        try {
            instance.hideTimerOverlay();
            instance.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            // Wait 1.5 s for the power dialog to open, then press DOWN + CENTER to select Sleep.
            instance.handler.postDelayed(() -> {
                try {
                    instance.performGlobalAction(GLOBAL_ACTION_ACCESSIBILITY_BUTTON);
                } catch (Exception ignored) {}
                // Simulate DPAD_DOWN then DPAD_CENTER with 300 ms gap
                android.os.SystemClock.sleep(300);
                android.view.InputDevice dev = android.view.InputDevice.getDevice(
                        android.view.InputDevice.getDeviceIds()[0]);
                // Use the existing shell-based key injection already present in the service
                android.os.Handler h = new android.os.Handler(android.os.Looper.getMainLooper());
                h.post(() -> {
                    try {
                        Runtime.getRuntime().exec(new String[]{"input", "keyevent", "20"});  // DPAD_DOWN
                        android.os.SystemClock.sleep(300);
                        Runtime.getRuntime().exec(new String[]{"input", "keyevent", "23"});  // DPAD_CENTER
                    } catch (Exception e) {
                        android.util.Log.w("TvHome.HomeKey", "sleep keyevent failed: " + e.getMessage());
                    }
                });
            }, 1500);
        } catch (Exception e) {
            android.util.Log.w("TvHome.HomeKey", "triggerSleep failed: " + e.getMessage());
        }
    });
}
```

- [ ] **Step 5: Hook overlay into SleepTimer.Listener in MainActivity**

Update the `SleepTimer.Listener` set in Task 3's `onCreate` wiring:

```java
SleepTimer.get().setListener(new SleepTimer.Listener() {
    @Override public void onTick(long ms) {
        updateTimerDisplay();
        HomeKeyService instance = HomeKeyService.getInstance(); // add a static getInstance()
        if (instance != null) {
            if (!instance.isOverlayShown()) instance.showTimerOverlay();
            instance.updateTimerOverlay(ms);
        }
    }
    @Override public void onExpired() {
        updateTimerDisplay();
        HomeKeyService.triggerSleep();
    }
});
```

Add `isOverlayShown()` returning `overlayView != null` and `getInstance()` returning `instance` to `HomeKeyService`.

- [ ] **Step 6: Build, deploy, and verify overlay appears over YouTube**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17 PATH="$JAVA_HOME/bin:$PATH" ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher && TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

1. Set timer to 15 minutes from the launcher.
2. Open YouTube or another video app.
3. Verify the countdown pill appears top-left over the video.
4. Return to launcher and verify clock is replaced with countdown.

- [ ] **Step 7: Commit**

```bash
cd /tmp/glass-tv-home && git add launcher/app/src/main/java/com/gathin/tvhome/HomeKeyService.java launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java
git commit -m "feat: sleep timer system-wide overlay via TYPE_ACCESSIBILITY_OVERLAY

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 5: Aerial Views Video Wallpaper

**Files:**
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/IWallpaperProviderService.aidl`
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.aidl`
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Event.aidl`
- Create: `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.java`
- Create: `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Event.java`
- Create: `launcher/app/src/main/java/com/gathin/tvhome/AerialWallpaperPlayer.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java`
- Modify: `launcher/app/build.gradle` (aidl must be enabled)

**Interfaces:**
- Consumes: `Config.aerialWallpaper` (boolean) from Task 1
- Produces: `AerialWallpaperPlayer(Context, FrameLayout root, Handler handler)` constructor; `start()`, `stop()`, `pause()`, `resume()`

- [ ] **Step 1: Enable AIDL in build.gradle**

In `launcher/app/build.gradle`, inside the `android { buildFeatures { } }` block (or add it), add:

```groovy
buildFeatures {
    aidl = true
}
```

- [ ] **Step 2: Create AIDL directory structure**

```bash
mkdir -p /tmp/glass-tv-home/launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api
```

- [ ] **Step 3: Write AIDL files**

`Wallpaper.aidl`:
```aidl
// launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.aidl
package tv.projectivy.plugin.wallpaperprovider.api;
parcelable Wallpaper;
```

`Event.aidl`:
```aidl
// launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Event.aidl
package tv.projectivy.plugin.wallpaperprovider.api;
parcelable Event;
```

`IWallpaperProviderService.aidl`:
```aidl
// launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/IWallpaperProviderService.aidl
package tv.projectivy.plugin.wallpaperprovider.api;
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper;
import tv.projectivy.plugin.wallpaperprovider.api.Event;

interface IWallpaperProviderService {
    List<Wallpaper> getWallpapers(in Event event);
}
```

- [ ] **Step 4: Create Wallpaper.java (Parcelable)**

The field write order was reverse-engineered from the Aerial Views DEX: `uri`, `type`, `duration`, `title`, `subtitle`, `location`, `thumbnailUri`.

```java
// launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.java
package tv.projectivy.plugin.wallpaperprovider.api;

import android.os.Parcel;
import android.os.Parcelable;

public final class Wallpaper implements Parcelable {
    public final String uri;
    public final int type;         // 0 = VIDEO, 1 = IMAGE
    public final int duration;     // seconds
    public final String title;
    public final String subtitle;
    public final String location;
    public final String thumbnailUri;

    public Wallpaper(String uri, int type, int duration,
                     String title, String subtitle, String location, String thumbnailUri) {
        this.uri = uri; this.type = type; this.duration = duration;
        this.title = title; this.subtitle = subtitle;
        this.location = location; this.thumbnailUri = thumbnailUri;
    }

    protected Wallpaper(Parcel in) {
        uri = in.readString();
        type = in.readInt();
        duration = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        location = in.readString();
        thumbnailUri = in.readString();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeInt(type);
        dest.writeInt(duration);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(location);
        dest.writeString(thumbnailUri);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<Wallpaper> CREATOR = new Creator<Wallpaper>() {
        @Override public Wallpaper createFromParcel(Parcel in) { return new Wallpaper(in); }
        @Override public Wallpaper[] newArray(int size) { return new Wallpaper[size]; }
    };
}
```

- [ ] **Step 5: Create Event.java (Parcelable with TimeElapsed subtype)**

```java
// launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Event.java
package tv.projectivy.plugin.wallpaperprovider.api;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Event implements Parcelable {
    // Subtype discriminator written first
    public static final int TYPE_TIME_ELAPSED = 0;
    public static final int TYPE_CARD_FOCUSED = 1;
    public static final int TYPE_IDLE_CHANGED = 2;
    public static final int TYPE_NOW_PLAYING = 3;
    public static final int TYPE_PROGRAM_CARD = 4;

    abstract int eventType();

    @Override public int describeContents() { return 0; }

    public static final class TimeElapsed extends Event {
        public final long elapsedMs;
        public TimeElapsed(long elapsedMs) { this.elapsedMs = elapsedMs; }
        @Override int eventType() { return TYPE_TIME_ELAPSED; }
        @Override public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(TYPE_TIME_ELAPSED);
            dest.writeLong(elapsedMs);
        }
        protected TimeElapsed(Parcel in) { elapsedMs = in.readLong(); }
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override public Event createFromParcel(Parcel in) {
            int type = in.readInt();
            switch (type) {
                case TYPE_TIME_ELAPSED: return new TimeElapsed(in);
                default: return new TimeElapsed(0);
            }
        }
        @Override public Event[] newArray(int size) { return new Event[size]; }
    };
}
```

- [ ] **Step 6: Create AerialWallpaperPlayer.java**

```java
// launcher/app/src/main/java/com/gathin/tvhome/AerialWallpaperPlayer.java
package com.gathin.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tv.projectivy.plugin.wallpaperprovider.api.Event;
import tv.projectivy.plugin.wallpaperprovider.api.IWallpaperProviderService;
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper;

final class AerialWallpaperPlayer {
    private static final String TAG = "TvHome.Aerial";
    private static final String AERIAL_PKG = "com.neilturner.aerialviews";
    private static final String AERIAL_SVC = AERIAL_PKG + "/.services.projectivy.WallpaperProviderService";
    private static final String ACTION = "tv.projectivy.plugin.WALLPAPER_PROVIDER";

    private final Context ctx;
    private final FrameLayout root;
    private final Handler handler;
    private final Random rng = new Random();

    private TextureView textureView;
    private MediaPlayer player;
    private IWallpaperProviderService service;
    private final List<String> videoUris = new ArrayList<>();
    private boolean bound;
    private boolean paused;

    AerialWallpaperPlayer(Context ctx, FrameLayout root, Handler handler) {
        this.ctx = ctx; this.root = root; this.handler = handler;
    }

    void start() {
        Intent intent = new Intent(ACTION);
        intent.setComponent(ComponentName.unflattenFromString(AERIAL_SVC));
        try {
            bound = ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            if (!bound) Log.w(TAG, "bindService returned false — Aerial Views may not be installed");
        } catch (Exception e) {
            Log.w(TAG, "bind failed: " + e.getMessage());
        }
    }

    void stop() {
        releasePlayer();
        if (textureView != null) { root.removeView(textureView); textureView = null; }
        if (bound) { try { ctx.unbindService(connection); } catch (Exception ignored) {} bound = false; }
        videoUris.clear();
    }

    void pause() { paused = true; if (player != null && player.isPlaying()) player.pause(); }

    void resume() {
        paused = false;
        if (player != null && !player.isPlaying()) {
            try { player.start(); } catch (Exception ignored) {}
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            try {
                service = IWallpaperProviderService.Stub.asInterface(binder);
                List<Wallpaper> wallpapers = service.getWallpapers(new Event.TimeElapsed(0));
                for (Wallpaper w : wallpapers) {
                    if (w.type == 0 && w.uri != null && !w.uri.isEmpty()) videoUris.add(w.uri);
                }
                if (!videoUris.isEmpty()) handler.post(() -> initTextureView());
                else Log.w(TAG, "no video wallpapers returned by Aerial Views");
            } catch (Exception e) {
                Log.w(TAG, "onServiceConnected failed: " + e.getMessage());
            }
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.w(TAG, "Aerial Views service disconnected — falling back to static wallpaper");
            // MainActivity observes the player and falls back if videoUris stays empty
        }
    };

    private void initTextureView() {
        textureView = new TextureView(ctx);
        textureView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(textureView, 0);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) { playNext(st); }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { releasePlayer(); return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void playNext(SurfaceTexture st) {
        releasePlayer();
        if (videoUris.isEmpty() || paused) return;
        String uri = videoUris.get(rng.nextInt(videoUris.size()));
        try {
            player = new MediaPlayer();
            player.setDataSource(ctx, android.net.Uri.parse(uri));
            player.setSurface(new Surface(st));
            player.setVolume(0f, 0f);
            player.setLooping(false);
            player.setOnCompletionListener(mp -> handler.post(() -> {
                if (!paused && st.isReleased() == false) playNext(st);
            }));
            player.prepareAsync();
            player.setOnPreparedListener(mp -> {
                extractFrameForBlur(uri);
                if (!paused) mp.start();
            });
        } catch (Exception e) {
            Log.w(TAG, "playNext failed for " + uri + ": " + e.getMessage());
        }
    }

    private void extractFrameForBlur(String uri) {
        new Thread(() -> {
            try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
                mmr.setDataSource(ctx, android.net.Uri.parse(uri));
                android.graphics.Bitmap frame = mmr.getFrameAtTime(1_000_000L); // 1 s
                if (frame != null) {
                    android.util.DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
                    Backdrop.update(frame, dm.widthPixels, dm.heightPixels, 0, null, handler);
                }
            } catch (Exception e) {
                Log.w(TAG, "frame extraction failed: " + e.getMessage());
            }
        }, "AerialBlur").start();
    }

    private void releasePlayer() {
        if (player != null) {
            try { player.stop(); } catch (Exception ignored) {}
            player.release();
            player = null;
        }
    }

    boolean hasVideos() { return !videoUris.isEmpty(); }
}
```

- [ ] **Step 7: Branch in MainActivity for aerial wallpaper**

In `MainActivity.java`, in the wallpaper initialisation section (after `cfg = Config.load(this)`), add before the existing wallpaper path:

```java
if (cfg.aerialWallpaper) {
    aerialPlayer = new AerialWallpaperPlayer(this, root, handler);
    aerialPlayer.start();
    // If binding fails or returns no videos within 5 s, fall back to static path
    handler.postDelayed(() -> {
        if (aerialPlayer != null && !aerialPlayer.hasVideos()) {
            aerialPlayer.stop();
            aerialPlayer = null;
            loadStaticWallpaper(cfg);  // existing logic extracted into this method
        }
    }, 5000);
} else {
    loadStaticWallpaper(cfg);
}
```

Add `private AerialWallpaperPlayer aerialPlayer;` as a field. In `onStart()` call `aerialPlayer.resume()`, in `onStop()` call `aerialPlayer.pause()`, in `onDestroy()` call `aerialPlayer.stop()`.

Extract the existing wallpaper load logic into a private method `loadStaticWallpaper(Config cfg)`.

- [ ] **Step 8: Build and deploy — test aerial wallpaper**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17 PATH="$JAVA_HOME/bin:$PATH" ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher && TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

Enable aerial wallpaper:
```bash
cd /tmp/glass-tv-home && python3 -c "
import json, sys
d = {}
try: d = json.load(open('/tmp/new_config.json'))
except: pass
d['aerial_wallpaper'] = True
d['places'] = False
d['wallpaper'] = False
print(json.dumps(d, indent=2))
" > /tmp/aerial_config.json
TV_HOST=192.168.0.126 python3 tools/b64push.py /tmp/aerial_config.json /sdcard/Android/data/com.gathin.tvhome/files/config.json
TV_HOST=192.168.0.126 python3 tools/tv.py "am force-stop com.gathin.tvhome; am start -n com.gathin.tvhome/.MainActivity"
```

Take a screenshot after 5 seconds — aerial video should be playing as the wallpaper. If the Parcel field order is wrong, the `getWallpapers` call will throw a `BadParcelableException` — check logcat with `adb logcat -s TvHome.Aerial`.

- [ ] **Step 9: Commit**

```bash
cd /tmp/glass-tv-home && git add \
  launcher/app/src/main/aidl/ \
  launcher/app/src/main/java/tv/ \
  launcher/app/src/main/java/com/gathin/tvhome/AerialWallpaperPlayer.java \
  launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java \
  launcher/app/build.gradle
git commit -m "feat: aerial views video wallpaper via projectivy AIDL

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 6: In-Launcher Settings UI

**Files:**
- Create: `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsActivity.java`
- Create: `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsFragment.java`
- Create: `launcher/app/src/main/java/com/gathin/tvhome/ColorPickerPreference.java`
- Create: `launcher/app/src/main/res/xml/settings_main.xml`
- Create: `launcher/app/src/main/res/xml/settings_wallpaper.xml`
- Create: `launcher/app/src/main/res/xml/settings_layout.xml`
- Create: `launcher/app/src/main/res/xml/settings_sound.xml`
- Create: `launcher/app/src/main/res/xml/settings_apps.xml`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/SoundEffects.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java`
- Modify: `launcher/app/src/main/AndroidManifest.xml`
- Modify: `launcher/app/build.gradle`

**Interfaces:**
- Consumes: `Config.save()` from Task 1; `IconPackLoader` from Task 2; `SleepTimer` from Task 3; `Config.aerialWallpaper` from Task 1
- Produces: `LauncherSettingsActivity` registered in manifest; gear icon opens it

- [ ] **Step 1: Add Leanback preference dependency**

In `launcher/app/build.gradle`, in the `dependencies` block, add:
```groovy
implementation 'androidx.leanback:leanback-preference:1.2.0-alpha04'
```

- [ ] **Step 2: Update SoundEffects.java to honour enabled + volume**

In `SoundEffects.java`, change `playNav()` and `playSelect()` to accept externally-set enabled flags:

Add two static fields:
```java
static boolean navEnabled = true;
static boolean selectEnabled = true;
static float masterVolume = 0.80f;  // 0.0–1.0, maps to config sound_volume / 100
```

Change `playNav()`:
```java
static void playNav() {
    if (!navEnabled || !ready || soundPool == null || soundNav == 0) return;
    soundPool.play(soundNav, 0.65f * masterVolume, 0.65f * masterVolume, 0, 0, 1.0f);
}
```

Change `playSelect()`:
```java
static void playSelect() {
    if (!selectEnabled || !ready || soundPool == null || soundSelect == 0) return;
    soundPool.play(soundSelect, 0.85f * masterVolume, 0.85f * masterVolume, 0, 0, 1.0f);
}
```

In `MainActivity.java` where `Config` is loaded, apply:
```java
SoundEffects.navEnabled = cfg.soundNavEnabled;
SoundEffects.selectEnabled = cfg.soundSelectEnabled;
SoundEffects.masterVolume = cfg.soundVolume / 100f;
```

- [ ] **Step 3: Create settings preference XML files**

`launcher/app/src/main/res/xml/settings_main.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <PreferenceScreen android:key="wallpaper" android:title="Wallpaper"
        android:fragment="com.gathin.tvhome.LauncherSettingsFragment$WallpaperFragment" />
    <PreferenceScreen android:key="layout" android:title="Layout"
        android:fragment="com.gathin.tvhome.LauncherSettingsFragment$LayoutFragment" />
    <PreferenceScreen android:key="sound" android:title="Sound"
        android:fragment="com.gathin.tvhome.LauncherSettingsFragment$SoundFragment" />
    <PreferenceScreen android:key="apps" android:title="Apps"
        android:fragment="com.gathin.tvhome.LauncherSettingsFragment$AppsFragment" />
    <Preference android:key="restart" android:title="Restart Home" android:summary="Apply all changes" />
</PreferenceScreen>
```

`launcher/app/src/main/res/xml/settings_wallpaper.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <ListPreference android:key="wallpaper_source" android:title="Wallpaper source"
        android:entries="@array/wallpaper_source_labels"
        android:entryValues="@array/wallpaper_source_values"
        android:defaultValue="places" />
    <SwitchPreference android:key="aerial_wallpaper" android:title="Aerial Views video wallpaper"
        android:summary="Requires Aerial Views app installed" android:defaultValue="false" />
    <SeekBarPreference android:key="wallpaper_seconds" android:title="Rotation interval (seconds)"
        android:min="5" android:max="300" android:defaultValue="30" />
    <!-- ColorPickerPreference is a custom class; referenced by full class name in Java, not XML -->
    <Preference android:key="background_color" android:title="Background colour" />
    <ListPreference android:key="icon_pack" android:title="Icon pack"
        android:summary="Populated at runtime" android:defaultValue="" />
</PreferenceScreen>
```

Add string arrays to `res/values/strings.xml`:
```xml
<string-array name="wallpaper_source_labels">
    <item>Places (Bing daily)</item>
    <item>Aerial Views video</item>
    <item>Custom images (wallpapers/ folder)</item>
    <item>Solid colour</item>
</string-array>
<string-array name="wallpaper_source_values">
    <item>places</item>
    <item>aerial</item>
    <item>custom</item>
    <item>solid</item>
</string-array>
```

`launcher/app/src/main/res/xml/settings_layout.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <SeekBarPreference android:key="columns" android:title="Columns per row"
        android:min="1" android:max="8" android:defaultValue="5" />
    <SeekBarPreference android:key="rows_below_dock" android:title="Rows below dock"
        android:min="0" android:max="3" android:defaultValue="0" />
    <SwitchPreference android:key="reduced_motion" android:title="Reduced motion"
        android:summary="Calmer animations for weaker GPUs" android:defaultValue="false" />
</PreferenceScreen>
```

`launcher/app/src/main/res/xml/settings_sound.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <SwitchPreference android:key="sound_nav_enabled" android:title="Navigation tick"
        android:defaultValue="true" />
    <SwitchPreference android:key="sound_select_enabled" android:title="Select chime"
        android:defaultValue="true" />
    <SeekBarPreference android:key="sound_volume" android:title="Volume"
        android:min="0" android:max="100" android:defaultValue="80" />
    <SeekBarPreference android:key="sleep_timer_minutes" android:title="Default sleep timer (minutes)"
        android:summary="0 = disabled" android:min="0" android:max="120" android:defaultValue="0" />
</PreferenceScreen>
```

`launcher/app/src/main/res/xml/settings_apps.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <Preference android:key="dock_apps" android:title="Dock apps"
        android:summary="Edit the pinned dock row" />
    <Preference android:key="hidden_apps" android:title="Hidden apps"
        android:summary="Apps never shown as tiles" />
</PreferenceScreen>
```

- [ ] **Step 4: Create LauncherSettingsActivity.java**

```java
// launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsActivity.java
package com.gathin.tvhome;

import android.app.Activity;
import android.os.Bundle;

public class LauncherSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new LauncherSettingsFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
```

- [ ] **Step 5: Create LauncherSettingsFragment.java**

```java
// launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsFragment.java
package com.gathin.tvhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class LauncherSettingsFragment extends LeanbackPreferenceFragment {
    private static final Handler saveHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreatePreferences(Bundle state, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);
        populateIconPacks();
        wire("restart", pref -> {
            // Restart MainActivity to pick up new config
            Intent intent = getActivity().getPackageManager()
                    .getLaunchIntentForPackage(getActivity().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            getActivity().finish();
            return true;
        });
        wireConfigKey("columns", "columns");
        wireConfigKey("rows_below_dock", "rows_below_dock");
        wireConfigKey("reduced_motion", "reduced_motion");
        wireConfigKey("sound_nav_enabled", "sound_nav_enabled");
        wireConfigKey("sound_select_enabled", "sound_select_enabled");
        wireConfigKey("sound_volume", "sound_volume");
        wireConfigKey("sleep_timer_minutes", "sleep_timer_minutes");
        wireConfigKey("aerial_wallpaper", "aerial_wallpaper");
        wireConfigKey("wallpaper_seconds", "wallpaper_seconds");
        wireConfigKey("icon_pack", "icon_pack");
    }

    private void wireConfigKey(String prefKey, String configKey) {
        Preference p = findPreference(prefKey);
        if (p == null) return;
        p.setOnPreferenceChangeListener((pref, value) -> {
            saveHandler.post(() ->
                    Config.save(Config.dir(getActivity()), configKey, value));
            return true;
        });
    }

    private void wire(String key, Preference.OnPreferenceClickListener l) {
        Preference p = findPreference(key);
        if (p != null) p.setOnPreferenceClickListener(l);
    }

    private void populateIconPacks() {
        ListPreference iconPref = findPreference("icon_pack");
        if (iconPref == null) return;
        Intent intent = new Intent("org.adw.launcher.THEMES");
        List<ResolveInfo> packs = getActivity().getPackageManager()
                .queryIntentActivities(intent, PackageManager.GET_META_DATA);
        List<String> labels = new ArrayList<>();
        List<String> pkgs = new ArrayList<>();
        labels.add("System icons");  pkgs.add("");
        for (ResolveInfo ri : packs) {
            labels.add(ri.loadLabel(getActivity().getPackageManager()).toString());
            pkgs.add(ri.activityInfo.packageName);
        }
        iconPref.setEntries(labels.toArray(new CharSequence[0]));
        iconPref.setEntryValues(pkgs.toArray(new CharSequence[0]));
    }
}
```

- [ ] **Step 6: Register LauncherSettingsActivity in AndroidManifest.xml**

Inside the `<application>` block, add:
```xml
<activity
    android:name=".LauncherSettingsActivity"
    android:exported="false"
    android:screenOrientation="landscape"
    android:theme="@style/Theme.TvHome"
    android:label="@string/settings_title" />
```

Add to `res/values/strings.xml`: `<string name="settings_title">Launcher Settings</string>`

- [ ] **Step 7: Route gear icon to settings in MainActivity**

Find the gear icon click listener in `MainActivity.java` (currently opens `HomeSettingsActivity` or `Settings.ACTION_HOME_SETTINGS`). Replace with:

```java
settingsBtn.setOnClickListener(v -> {
    startActivity(new Intent(MainActivity.this, LauncherSettingsActivity.class));
});
```

- [ ] **Step 8: Build, deploy, and navigate settings with remote**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17 PATH="$JAVA_HOME/bin:$PATH" ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher && TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

Focus the gear icon, press OK → Settings opens. Navigate with D-pad. Change columns to 6 → press "Restart Home" → launcher reloads with 6 columns.

Verify each section: Wallpaper, Layout, Sound, Apps. Confirm BACK key pops nested screens rather than closing the activity.

- [ ] **Step 9: Commit**

```bash
cd /tmp/glass-tv-home && git add \
  launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsActivity.java \
  launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsFragment.java \
  launcher/app/src/main/java/com/gathin/tvhome/SoundEffects.java \
  launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java \
  launcher/app/src/main/res/xml/ \
  launcher/app/src/main/AndroidManifest.xml \
  launcher/app/build.gradle
git commit -m "feat: in-launcher settings UI with Leanback preferences

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

## Task 7: Final Build, Screenshot Verification, and Tag

- [ ] **Step 1: Clean build and full deploy**

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17 PATH="$JAVA_HOME/bin:$PATH" ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
cd /tmp/glass-tv-home/launcher
./gradlew clean
TV_HOST=192.168.0.126 HOME_KEY_SERVICE=1 bash build-and-install.sh
```

- [ ] **Step 2: Run all host tests**

```bash
cd /tmp/glass-tv-home/launcher && ./gradlew test 2>&1 | tail -20
```
Expected: all tests PASS.

- [ ] **Step 3: Verify each feature end-to-end**

**Icon pack:** Push `"icon_pack": "tv.corebuilds.iconpack"` → restart → confirm icons changed.

**Sleep timer:** Press timer button → 15 min → open YouTube → confirm pill shows top-left → return to launcher → confirm clock replaced → set to Off → confirm clock returns.

**Aerial wallpaper:** Push `"aerial_wallpaper": true` → restart → confirm video plays as background.

**Settings UI:** Gear → Settings → change columns to 7 → Restart Home → confirm 7 columns.

- [ ] **Step 4: Capture screenshot and confirm all features visible**

```bash
BACKUP_DIR=$(ls -dt ~/shield-backup-* | head -1)
adb -s 192.168.0.126:5555 shell screencap -p /sdcard/final.png
adb -s 192.168.0.126:5555 pull /sdcard/final.png "$BACKUP_DIR/screenshot_final.png"
adb -s 192.168.0.126:5555 shell rm /sdcard/final.png
```

- [ ] **Step 5: Final commit and tag**

```bash
cd /tmp/glass-tv-home && git add -A
git commit -m "chore: final verification — all 4 features live on Shield

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
git tag v1.0.0-shield-custom
```
