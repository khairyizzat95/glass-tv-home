# Glass TV Home — 4 Feature Extensions Design Spec

**Date:** 2026-10-23  
**Repo:** `launcher/` inside `glass-tv-home`  
**Base package:** `com.gathin.tvhome`  
**Target device:** NVIDIA Shield TV 2019 (Android 11, API 30)

---

## Overview

Four features are added to the Glass TV Home launcher APK. All are activated via the existing
`config.json` overlay mechanism (`/sdcard/Android/data/com.gathin.tvhome/files/`) and the new
in-launcher Settings UI, which writes back to the same file. No ADB required after initial install.

---

## Feature 1 — Icon Pack Support

### Goal
Replace per-app system icons with icons from a user-selected ADW/Nova-format icon pack app already
installed on the TV.

### Config key
`"icon_pack"`: string — package name of the icon pack, e.g. `"tv.corebuilds.iconpack"`. Empty or
absent means system icons (no change from current behaviour).

### Mechanism
New `IconPackLoader.java` loads `Resources` from the icon pack package via
`PackageManager.getResourcesForApplication(iconPackPkg)`. For each app tile whose banner is null,
it resolves the drawable resource named `ic_<package_name_with_dots_and_slashes_as_underscores>` (e.g.
`ic_com_netflix_ninja`) at XHIGH density. Falls back to `ri.loadIcon(pm)` when not found or when the
pack is unavailable. Results are cached by component name for the launcher session.

### Installed icon packs detected
Query `PackageManager.queryIntentActivities` with action `org.adw.launcher.THEMES` to list all
packs. The Settings UI (Feature 4) shows this list as a dropdown.

### Files
- Create: `launcher/app/src/main/java/com/gathin/tvhome/IconPackLoader.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/Config.java` — add `iconPack` field
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/AppAdapter.java` — use `IconPackLoader`

---

## Feature 2 — Sleep Timer with System-wide Overlay

### Goal
Allow the user to set a countdown timer that puts the TV to sleep when it expires. The countdown is
visible on top of any running app (YouTube, video players, etc.).

### Timer button
A timer button is added to the top bar in `activity_main.xml`, right of the settings gear icon.
Pressing it cycles presets: **Off → 15 min → 30 min → 1 h → 2 h → Off**, showing the active preset
as a badge. While active, the timer cycles are still navigable to change or cancel.

### Launcher clock area
When the timer is active AND the launcher is in the foreground, the clock/date block (top-left) is
replaced with a large countdown display (`MM:SS` below 1 hour, `H:MM:SS` above). When the timer is
cancelled or expires, the clock block returns.

### System-wide overlay (on top of any app)
`HomeKeyService` (the running accessibility service) draws a small `TYPE_ACCESSIBILITY_OVERLAY`
floating view (top-left, semi-transparent pill, 160 dp wide) via `WindowManager`. This view shows
the countdown and is visible over all apps. It is created when the timer starts and removed when it
cancels or fires. No `SYSTEM_ALERT_WINDOW` permission needed — accessibility overlays are permitted
by the service.

### Sleep action
When the timer reaches zero:
1. Overlay is removed.
2. `HomeKeyService.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)` opens the Shield's power menu.
3. After 1 second, it simulates D-pad DOWN + DPAD_CENTER to select "Sleep" (the first option on Shield).

### Config key
`"sleep_timer_minutes"`: int — last-used preset (0 = off). Saved on preset change for memory across
restarts.

### Files
- Create: `launcher/app/src/main/java/com/gathin/tvhome/SleepTimer.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/HomeKeyService.java` — overlay + sleep action
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java` — clock swap + button
- Modify: `launcher/app/src/main/res/layout/activity_main.xml` — timer button in top bar

---

## Feature 3 — Aerial Views Video Wallpaper

### Goal
Play aerial videos from the installed `com.neilturner.aerialviews` app as the launcher's live
wallpaper background, using the Projectivity wallpaper-provider plugin API.

### Config key
`"aerial_wallpaper"`: boolean — `true` activates video wallpaper mode. Falls back silently to the
existing static-image wallpaper path when Aerial Views is not installed or binding fails.

### Binding
Bind to `com.neilturner.aerialviews/.services.projectivy.WallpaperProviderService` using intent
action `"tv.projectivy.plugin.WALLPAPER_PROVIDER"`. The service is exported and requires no
special permission.

### AIDL interface (replicated stubs)
Package `tv.projectivy.plugin.wallpaperprovider.api` must exist in Glass TV Home with the exact
same class names and Parcel write order so the Binder marshalling matches:

**`Wallpaper` (Parcelable)** — constructor `(String uri, int type, int duration, String title,
String subtitle, String location, String thumbnailUri)`. `type` 0 = VIDEO, 1 = IMAGE.

**`Event` (Parcelable sealed class)** — subtypes: `TimeElapsed(long ms)`, `CardFocused`,
`LauncherIdleModeChanged`, `NowPlayingChanged`, `ProgramCardFocused`. Only `TimeElapsed` is sent
by Glass TV Home.

**`IWallpaperProviderService` (AIDL)** — single method:
`List<Wallpaper> getWallpapers(in Event event)`.

### Playback
New `AerialWallpaperPlayer.java` wraps a `TextureView` + `MediaPlayer`. On bind success it calls
`getWallpapers(new Event.TimeElapsed(0))`, picks a random VIDEO entry, and begins looped muted
playback. It advances to the next video on completion. A static frame is extracted via
`MediaMetadataRetriever` at 1000 ms and sent to `Backdrop` for the frosted-glass blur. Lifecycle:
pauses on `onStop`, resumes on `onStart`.

### Manifest additions
- `<queries>` entry for `com.neilturner.aerialviews`
- `<queries>` entry for icon pack action `org.adw.launcher.THEMES`

### Files
- Create: `launcher/app/src/main/java/com/gathin/tvhome/AerialWallpaperPlayer.java`
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/IWallpaperProviderService.aidl`
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.aidl`
- Create: `launcher/app/src/main/aidl/tv/projectivy/plugin/wallpaperprovider/api/Event.aidl`
- Create: `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Wallpaper.java`
- Create: `launcher/app/src/main/java/tv/projectivy/plugin/wallpaperprovider/api/Event.java`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/Config.java` — add `aerialWallpaper` field
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java` — branch to aerial path
- Modify: `launcher/app/src/main/AndroidManifest.xml` — queries

---

## Feature 4 — In-Launcher Settings UI

### Goal
A full D-pad-navigable settings screen accessible from the gear icon, covering all launcher options.
Writes changes back to `config.json` so they survive restarts without ADB.

### Access
The existing gear icon in the top bar launches `LauncherSettingsActivity` (replaces the current
`ACTION_HOME_SETTINGS` redirect).

### Settings sections and keys

| Section | Setting | Type | Config key |
|---|---|---|---|
| Wallpaper | Source | Enum (Aerial/Places/Custom/Solid) | `aerial_wallpaper`, `places`, `wallpaper` |
| Wallpaper | Rotation interval | Int seconds | `wallpaper_seconds` |
| Wallpaper | Background colour | Color string | `background_color` |
| Icon Pack | Active pack | String (package) | `icon_pack` |
| Layout | Columns | Int 1–8 | `columns` |
| Layout | Rows below dock | Int 0–3 | `rows_below_dock` |
| Layout | Reduced motion | Bool | `reduced_motion` |
| Sound | Nav tick | Bool | `sound_nav_enabled` |
| Sound | Select chime | Bool | `sound_select_enabled` |
| Sound | Volume | Int 0–100 | `sound_volume` |
| Sleep Timer | Default preset | Int minutes | `sleep_timer_minutes` |
| Apps | Dock apps | String list | `dock_apps` |
| Apps | Hidden apps | String list | `hidden_apps` |

### `Config.save()` method
Reads the current `config.json` (or starts from `{}`), merges the changed key, writes back
atomically (write to `.tmp`, rename). Called from `LauncherSettingsFragment` after each preference
change. A "Restart Home" button in the settings screen calls `recreate()` on `MainActivity` to
reload config.

### Implementation
`LeanbackPreferenceFragment` (AndroidX Leanback 1.2.x) with `PreferenceScreen` XML. Icon pack
picker uses `ListPreference` populated at runtime from installed packs. Colour picker is a custom
`DialogPreference` with 8 preset swatches + hex input. Sound volume is a `SeekBarPreference`.
Dock/hidden app editors open a secondary list screen.

### Files
- Create: `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsActivity.java`
- Create: `launcher/app/src/main/java/com/gathin/tvhome/LauncherSettingsFragment.java`
- Create: `launcher/app/src/main/java/com/gathin/tvhome/ColorPickerPreference.java`
- Create: `launcher/app/src/main/res/xml/settings_main.xml`
- Create: `launcher/app/src/main/res/xml/settings_wallpaper.xml`
- Create: `launcher/app/src/main/res/xml/settings_layout.xml`
- Create: `launcher/app/src/main/res/xml/settings_sound.xml`
- Create: `launcher/app/src/main/res/xml/settings_apps.xml`
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/Config.java` — `save()` method + new keys
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/SoundEffects.java` — honour volume/enabled flags
- Modify: `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java` — gear opens settings
- Modify: `launcher/app/src/main/AndroidManifest.xml` — register `LauncherSettingsActivity`
- Modify: `launcher/app/build.gradle` — add `leanback` preference dependency

---

## Non-goals
- Dock app reordering via drag on the settings screen (still via remote hold-OK on the tile)
- Per-app sound effects (current PS5 nav/select is global)
- Multiple icon packs active simultaneously
