# Glass TV Home User Guide (Beginner-Friendly) 📺✨

Welcome to **Glass TV Home**! This guide is crafted specifically for everyday users and beginners so you can easily install, debloat your TV for blazing-fast performance without ads, navigate seamlessly with your TV remote, and enjoy all smart ambient features effortlessly.

---

## 🌟 1. What is Glass TV Home?

Glass TV Home is a modern, lightweight home launcher replacement for Android TV and Google TV (compatible with Xiaomi Mi Box S, Mi TV Stick, Sony Bravia, TCL, Sharp, Philips, Mecool, Chromecast with Google TV, and more):

* 🚀 **Zero Ads & Ultralight**: Only ~422 KB in size. Saves over 400 MB of RAM compared to sluggish stock manufacturer launchers.
* 🪟 **Frosted Glass Aesthetic (tvOS Style)**: Modern Apple TV-inspired interface with fluid focus animations and a floating glass dock.
* 🕌 **Islamic Prayer Timetable & Daily Ayat**: Accurate 6-phase prayer timetable (Subuh, Syuruk, Zohor, Asar, Maghrib, Isyak) with active prayer emerald highlighting and daily Quranic verses, shown on a dark frosted pill for readability against any wallpaper.
* ⛅ **Weather & Air Quality (API / AQI) Dashboard**: Live ambient temperature, "feels like", humidity, UV index, wind speed, sunrise/sunset, 12-hour forecast, 7-day outlook, and official Air Quality Index readings (APIMS).
* 🌙 **StandBy Screensaver & Sleep Timer**: Enters ambient black nightstand screensaver after 10 minutes of inactivity (burn-in safe for OLED/LED). A moon-icon **Sleep Timer pill** in the top bar lets you set a countdown (15, 30, 60, or 120 min) after which the TV display turns off automatically.
* 🙈 **Hide Apps**: Long-press any app tile and choose **Hide App** to instantly remove it from the grid. Restore hidden apps anytime from **Launcher Settings → Hidden Apps**.
* 🎨 **App Icon Customisation**: Long-press any tile to change its icon using an installed icon pack, a custom image from your device, or to reset it back to the original.

---

## 🤖 2. The Easiest Way: Let AI Do Everything Automatically! (Noob-Friendly)

You **do not need any coding skills** or technical terminal knowledge. You can simply ask an AI Coding Assistant to connect to your TV over Wi-Fi, remove manufacturer bloatware, block background telemetry and ads, optimize performance, and install Glass TV Home automatically.

### 💡 Supported AI Assistants:
1. **Google Antigravity (Free / Zero Setup)** ⭐ *Highly Recommended*:
   - Official AI coding agent from Google DeepMind with full terminal shell capabilities, automation tools, and script execution for free.
2. **Claude Code (Anthropic Paid Subscription)**:
   - Powerful CLI coding agent by Anthropic that runs inside your terminal.
3. **Cursor / Windsurf IDE (Freemium / Paid)**:
   - AI code editor with an integrated terminal shell.

---

### 📋 Step 1: Turn On Developer Mode & Debugging on Your TV
Before AI can reach your TV over Wi-Fi, enable developer options on your Android TV:
1. On your TV remote, open **Settings ⚙️**.
2. Select **Device Preferences** (or **System** on newer Google TVs).
3. Select **About**.
4. Scroll down until you find **Build** (or **Android TV OS Build**).
5. **Press the OK button on your remote 7 times rapidly** until a toast notification appears: *"You are now a developer!"*.
6. Press the *Back* button once. You will now see a new menu: **Developer options**.
7. Open *Developer options*, locate and **ENABLE**:
   - ✅ **USB debugging** (Turn ON).
   - ✅ **Network debugging / Wireless debugging** (if available on your TV model).
8. Find your TV's local IP address under **Settings $\rightarrow$ Network & Internet** (e.g. `192.168.68.102` or `192.168.1.50`).

---

### 📋 Step 2: Give This Prompt to Your AI Assistant
Open **Google Antigravity**, **Claude Code**, or **Cursor** on your computer/laptop connected to the **same home Wi-Fi network as your TV**. Copy and paste the prompt below:

```text
Please install Glass TV from repository https://github.com/maohisham/glass-tv-home on my Android TV.
My TV model: [Insert your TV brand & model, e.g., Xiaomi Mi Box S]
My TV IP address: [Insert your TV IP, e.g., 192.168.68.102]

Please execute the following tasks automatically:
1. Connect to my TV over Wi-Fi using ADB (port 5555).
2. Create a backup of my TV's current app list and settings into the backup folder.
3. Optimize & Debloat (Remove Ads & Telemetry):
   - Disable manufacturer bloatware, sponsored promotional ads (such as Xiaomi PatchWall or Google TV launcher ads), and background analytics services using 'pm disable-user --user 0'.
   - Do NOT disable critical system services (keep HDMI, Bluetooth remote, Google Play Store, audio, and Wi-Fi intact).
4. Configure AdGuard Private DNS (dns.adguard-dns.com) to block ads and trackers at the system DNS level.
5. Compile/build the Glass TV Home APK and install it as the default home launcher.
6. Enable HomeKeyService so the remote Home button returns to Glass TV and the 2-hour sleep standby timer functions.
7. Capture a live screenshot from the TV and display it to confirm everything succeeded smoothly.
```

---

### 📋 Step 3: Click "Allow" on Your TV Screen
When the AI initiates the connection, a authorization prompt will pop up on your TV screen:
* *"Allow USB debugging from this computer?"*
* Check the box: ☑ **"Always allow from this computer"**
* Select **OK / Allow**.

The AI will handle everything else: clearing sluggish bloatware, enabling the ad blocker, deploying Glass TV Home, and showing you a screenshot of your clean, responsive TV!

---

### ↩️ How to Undo / Restore Original Launcher
All modifications made by the AI are **100% safe and reversible**:
* The AI disables apps rather than permanently deleting system packages.
* An automatic restore script (`restore.sh` / `restore.ps1`) is saved.
* If you ever wish to return to the manufacturer's original launcher, just prompt the AI:
  > *"Please restore my TV back to the original factory launcher and settings."*

---

## 🎮 3. Remote Control Navigation (D-Pad)

Glass TV Home is designed around standard TV remotes:

| Remote Button | Action / Function |
| :--- | :--- |
| **DOUBLE-PRESS HOME** | Opens the system's native **Recent Apps** drawer (same as the recents key on supported remotes). |
| **HOME Button (Single Press)** | Return to home dock from any third-party app. If already on home, engages the ambient screensaver. |
| **RECENTS / APP SWITCH key** | Opens the system's native **Recent Apps** drawer directly. |
| **UP Button (▲)** | Navigates to the top shelf to highlight the **Weather & AQI Pill**, Wi-Fi, Sleep Timer, or Settings buttons. |
| **OK / Center Button** | • Press on an app to launch it.<br>• Press on the weather pill to open the **Full-Screen Weather & Air Quality Dashboard**.<br>• **Long-Press** on an app tile to open the app options menu (Move, Hide App, App Info, Change Icon, Upload Icon). |
| **DOWN Button (▼)** | Returns focus down to the main application dock. |
| **LEFT / RIGHT (◀ / ▶)** | Move smoothly between dock apps or top pills. |
| **BACK Button (↩)** | Closes the Weather Dashboard or screensaver and returns to the home dock. |

---

## 📱 4. Key Feature Walkthrough

### A. Weather & Air Quality (AQI) Hub
* Located under the digital clock (e.g., `☁ 28° Overcast  •  API 151 Unhealthy`).
* **Press UP (▲) then press OK** to launch the full Leanback weather interface:
  1. **Current Conditions**: Temperature, "Feels Like", humidity, wind velocity, UV index, sunrise and sunset times.
  2. **Air Quality Index (APIMS)**: Visual rainbow gradient gauge with official health advisories and comparative monitoring stations.
  3. **12-Hour Hourly Forecast**: Hourly forecast cards with precipitation chance percentages.
  4. **7-Day Outlook**: Daily weather forecast across the upcoming week.

### B. Islamic Prayer Timetable & Daily Ayat
* **6-Phase Prayer Timetable**: Subuh, Syuruk, Zohor, Asar, Maghrib, and Isyak synchronized with your local time zone.
* **Active Window Glow**: The current active prayer window illuminates in emerald green.
* **Daily Quranic Verse**: Rotating daily ayat with Arabic calligraphy and translations.

### C. StandBy Ambient Mode & Smart Power Saving
* **10 Minutes Idle**: Enters an ambient OLED-safe black StandBy screensaver displaying large 110sp numerals, Islamic Hijri date, and the upcoming prayer window.
* **2 Hours Later**: If left untouched, the TV display enters sleep standby (`GLOBAL_ACTION_LOCK_SCREEN`) to save power.
* **Wake Up**: Press any button on the TV remote to wake the TV immediately.

### D. System Recent Apps (Double-Press Home)
* **How to Open**: Press the remote **HOME button twice rapidly** from any app or screen, or press the dedicated **Recents / App Switch** button on supported remotes.
* **What You See**: The standard Android TV recents drawer managed by the system — the same one you get on stock launchers. Switch between your open apps or dismiss them from here.
* **Tip**: Pressing Back while in recents returns you straight to the Glass TV Home screen.

### E. Hide Apps
Keep your launcher clean by hiding apps you rarely use without uninstalling them:
* **How to Hide**: On the home screen, **long-press** any app tile to open the options menu. Select **Hide App** — the app disappears from the grid instantly, with no restart needed.
* **How to Restore**: Open **Launcher Settings** (top-right gear icon) and scroll to **Hidden Apps** at the bottom. You will see each hidden app listed by name. Tap or select **Unhide** next to any app to bring it back.
* **Note**: Only apps actually installed on your device appear in the Hidden Apps list. Default entries for apps that are not installed are automatically filtered out.

### F. App Icon Customisation
Change any app's tile icon to your liking:
* **How to Customise**: **Long-press** any app tile to open the options menu, then choose one of:
  * **Change Icon** — select an icon from any installed icon pack (e.g., Arcticons, Lawnicons).
  * **Upload Icon** — use any image stored on your device as a completely custom tile icon.
  * **Reset Icon** — restore the app's original system icon.
* **Icon Packs**: Install an icon pack from the Play Store, then go to **Launcher Settings → Icons → Icon Pack** to apply it globally across all tiles.

---

## 📲 5. Mobile Phone Setup (Without Any PC / Laptop)

You do **not** need a computer to install Glass TV Home.

### Method 1: Android Phone via 'Bugjaeger' (Install APK + Debloat Directly)
The **Bugjaeger Mobile ADB** app (free on Google Play Store) allows your phone to act as an ADB controller over Wi-Fi:

1. Connect both phone and TV to the **same Wi-Fi**.
2. Open **Bugjaeger**, tap the **Connect 🔌** icon, enter your TV IP (e.g. `192.168.68.102:5555`), and tap **Connect**.
3. Accept the *"Allow USB debugging"* prompt on your TV screen.
4. Go to the **Packages 📦** tab in Bugjaeger, tap **+**, and select the downloaded `app-release.apk` to install it directly.
5. In the **Commands ⚡** tab, run these commands to block ads and debloat:
   ```bash
   settings put global private_dns_specifier dns.adguard-dns.com
   settings put global private_dns_mode hostname
   pm disable-user --user 0 com.mitv.tvhome
   pm disable-user --user 0 com.xiaomi.mitv.advertise
   ```

---

### Method 2: iPhone (iOS) Users: Can You Use an iPhone?
**Yes! You can easily install the APK and block ads on your TV using an iPhone.**

Due to Apple App Store sandbox security policies, Apple does not allow direct low-level ADB tools like Bugjaeger on iOS. However, iPhone users can achieve a clean setup quickly:

#### 1. Transfer & Install the APK using 'LocalSend':
* Download the free **LocalSend** app on your iPhone (App Store) and on your Android TV (Play Store).
* Download `app-release.apk` on your iPhone Safari from this GitHub repository.
* Open LocalSend on both devices (on the same Wi-Fi) and send the APK to your TV in 1 tap.
* On your TV, click the received APK file and select **Install**.

#### 2. Block Ads & Telemetry Directly from Your TV Remote (No ADB Needed):
1. On your TV, navigate to **Settings ⚙️** $\rightarrow$ **Network & Internet**.
2. Select **Private DNS** (switch from *Automatic* to *Private DNS provider hostname*).
3. Type:
   ```text
   dns.adguard-dns.com
   ```
4. Click **Save**. All system-wide banner ads, manufacturer analytics, and tracking domains will be blocked immediately!

#### 3. Deep Debloat Note for iPhone Users:
To disable deep manufacturer system packages (e.g., Xiaomi PatchWall), it is recommended to run the ADB command once from a computer, or borrow an Android phone for 2 minutes. Once disabled, the bloatware remains gone permanently.

---

## 🛠️ 6. Alternative: Manual Installation via USB Flash Drive

If you prefer using a physical USB drive:
1. Download `app-release.apk` onto your USB drive.
2. Plug the USB flash drive into your Android TV or Xiaomi Mi Box.
3. Open any file explorer app on the TV (e.g., *File Commander* or *FX File Explorer* from Google Play Store).
4. Click `app-release.apk` and select **Install** (allow *"Install unknown apps"* if prompted).
5. Press the **Home** button on your remote and choose **Glass TV Home** as *Always*.

---

## ❓ 7. Frequently Asked Questions (FAQ)

### Q1: Can I run Claude Code on a mobile phone (iOS / Android), and does the iOS sandbox block local network access?
* **CLI / Node.js Requirement:** Claude Code (`@anthropic-ai/claude-code`) is an interactive terminal tool that requires a full **Node.js 18+** shell environment. iOS sandbox policies strictly forbid running Node.js engines and local arbitrary shell executables directly on an iPhone.
* **Cloud vs Local Network Barrier:** Claude Code or any cloud-hosted AI (e.g., web Claude or web ChatGPT) executes on remote cloud servers. Cloud servers **CANNOT enter your private home Wi-Fi network** to access your TV's private IP (`192.168.x.x`) due to your home router's NAT firewall.
* **Solution:** ADB commands must be initiated by a local device on your home Wi-Fi (such as a laptop/PC running Google Antigravity or Claude Code, or an Android phone running Bugjaeger).

---

### Q2: Is there an AI that can install and do all tasks directly without the user installing the APK manually?
* **YES, when run on a computer/laptop on the same Wi-Fi (Google Antigravity / Claude Code / Cursor):**
  The AI connects to the TV over wireless ADB (`adb connect 192.168.x.x`), builds the APK, installs it, removes bloatware, and verifies everything with automated screenshots. The user does not need to touch or open any APK file manually!
* **On mobile phones without a PC:** You simply use a helper app (Bugjaeger on Android, or LocalSend on iPhone) to transfer and install the APK.

---

### Q3: If a user only installs the APK file, do they have full access with the TV remote, or do they still need AI access?
* **100% STANDALONE! YOU ONLY NEED YOUR TV REMOTE, ZERO AI REQUIRED!**
* Glass TV Home is a **native Android TV application** (Native Kotlin/AndroidX).
* Once the APK is installed on the TV:
  - **Full Remote Control**: The D-Pad (Up/Down/Left/Right/OK), Back, and Home buttons navigate everything natively.
  - **All smart features run locally**: Clock, Hijri dates, prayer timetable, weather & AQI (APIMS), 10-minute screensaver, and 2-hour auto sleep operate entirely inside the TV without requiring an AI agent, computer, or active subscription.
  - Family members and non-technical users can use the TV just like a standard commercial smart TV.
* **The AI's role is purely as a "mechanic/technician"** during the initial setup to automate installation and debloating. Once installed, Glass TV Home is 100% independent.

---

## 📄 License & Credits
* Built upon [CleanYourTV](https://github.com/Gathin23/cleanyourtv) © Gathin (MIT License).
* Outfit font by SIL Open Font License.
* Weather and AQI data powered by Open-Meteo & Malaysia Department of Environment (APIMS) standards.
