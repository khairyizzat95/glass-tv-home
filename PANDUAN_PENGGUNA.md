# Panduan Pengguna Glass TV Home (Untuk Beginner) 📺✨

Selamat datang ke **Glass TV Home**! Panduan ini khas untuk pengguna baru (*beginner-friendly*) supaya anda boleh memasang, mengoptimumkan TV agar laju tanpa iklan, menggunakan remote TV, dan menikmati semua ciri menarik dengan mudah tanpa pening kepala.

---

## 🌟 1. Apa Itu Glass TV Home?

Glass TV Home ialah pengganti skrin utama (*launcher*) untuk Android TV dan Google TV (seperti Xiaomi Mi Box S, Mi TV Stick, Android TV Sharp, Sony, TCL, Philips, Mecool dsb):
* 🚀 **Tiada Iklan & Sangat Ringan**: Hanya bersaiz ~422 KB, lancar dan tidak membebankan TV (menjimatkan lebih 400 MB RAM berbanding skrin asal).
* 🪟 **Gaya Kaca Frosted (tvOS)**: Rekaan moden seperti Apple TV dengan bayang kaca terapung dan susunan aplikasi yang kemas.
* 🕌 **Waktu Solat & Ayat Harian**: Jadual 6 waktu solat Malaysia secara automatik dengan ayat al-Quran pilihan harian, dipaparkan di atas kapsul gelap *frosted* agar mudah dibaca pada mana-mana latar belakang.
* ⛅ **Dashboard Cuaca & Jerebu (API)**: Semakan suhu semasa, ramalan 12 jam, 7 hari, serta indeks pencemaran udara rasmi (APIMS Jabatan Alam Sekitar).
* 🌙 **Screensaver Pintar & Pemasa Tidur**: Masuk screensaver selepas 10 minit rehat. Kapsul **Pemasa Tidur (🌙)** di bar atas membolehkan anda tetapkan kiraan undur (15, 30, 60, atau 120 minit) sebelum TV padam automatik.
* 🙈 **Sembunyikan Aplikasi**: Tekan lama mana-mana ikon aplikasi dan pilih **Sembunyikan** — ikon terus hilang dari grid seketika. Pulihkan semula dari **Tetapan Launcher → Aplikasi Tersembunyi** bila-bila masa.
* 🎨 **Ubah Suai Ikon Aplikasi**: Tekan lama mana-mana ikon untuk menukar gambar menggunakan pek ikon yang dipasang, gambar sendiri dari peranti, atau kembali ke ikon asal.

---

## 🤖 2. Cara Paling Mudah: Arahkan AI Buat Semua Automatik! (Noob-Friendly)

Anda **tidak perlu tahu koding** atau menaip perintah teknikal yang rumit. Anda boleh mengarahkan pembantu AI (*AI Coding Assistant*) untuk menyambung ke TV melalui Wi-Fi, membuang iklan pengeluar, menyekat penjejak data (*tracking*), mengoptimumkan TV agar laju, dan memasang Glass TV Home terus ke TV anda.

### 💡 Pilihan AI Yang Boleh Digunakan:
1. **Google Antigravity (Percuma / Free)** ⭐ *Paling Disyorkan*:
   - AI agent rasmi daripada Google DeepMind dengan akses terminal shell penuh, alatan automasi, dan keupayaan menjalankan skrip secara percuma.
2. **Claude Code (Berbayar / Subscription Anthropic)**:
   - CLI coding agent berkuasa daripada Anthropic yang berjalan terus dalam terminal komputer anda.
3. **Cursor / Windsurf IDE (Freemium / Berbayar)**:
   - Editor kod pintar berasaskan AI yang mempunyai terminal shell bersepadu.

---

### 📋 Langkah 1: Hidupkan Developer Mode & Debugging di TV Anda
Sebelum AI boleh menyambung ke TV melalui Wi-Fi, aktifkan mod pembangun pada Android TV anda:
1. Pada remote TV anda, buka **Settings (Tetapan / ikon gear ⚙️)**.
2. Pilih **Device Preferences** (atau **System** pada Google TV baru).
3. Pilih **About (Mengenai)**.
4. Skrol ke bawah hingga jumpa baris bertulis **Build** (atau **Android TV OS Build**).
5. **Tekan butang OK pada remote sebanyak 7 kali berturut-turut** sehingga muncul mesej: *"You are now a developer!"*.
6. Tekan butang *Back* sekali. Anda akan nampak menu baharu iaitu **Developer options (Pilihan pembangun)**.
7. Masuk ke dalam *Developer options*, cari dan **HIDUPKAN**:
   - ✅ **USB debugging** (Tukar kepada ON/Aktif).
   - ✅ **Network debugging / Wireless debugging** (jika ada pada model TV anda).
8. Dapatkan alamat IP TV anda di menu **Settings $\rightarrow$ Network & Internet** (contoh: `192.168.68.102` atau `192.168.1.50`).

---

### 📋 Langkah 2: Berikan Arahan (Prompt) Ini Kepada AI
Buka **Google Antigravity**, **Claude Code**, atau **Cursor** di laptop/komputer anda yang bersambung pada **rangkaian Wi-Fi yang sama dengan TV anda**. Salin dan hantarkan arahan di bawah kepada AI:

```text
Tolong pasangkan Glass TV dari repo https://github.com/aqmangge/glass-tv-home pada Android TV saya.
Model TV saya: [Masukkan jenama & model TV anda, contoh: Xiaomi Mi Box S]
Alamat IP TV: [Masukkan IP TV anda, contoh: 192.168.68.102]

Tolong laksanakan perkara berikut secara automatik:
1. Sambung ke TV saya melalui Wi-Fi menggunakan ADB (port 5555).
2. Buat salinan sandaran (backup) senarai app dan tetapan asal TV ke folder backup terlebih dahulu.
3. Optimumkan & Buang Iklan/Tracking (Debloat):
   - Matikan servis bloatware pengeluar, iklan promosi tajaan (seperti Xiaomi PatchWall/promosi Google), dan servis tracking telemetri latar belakang menggunakan perintah 'pm disable-user --user 0'.
   - Jangan padam sistem asas yang penting (seperti HDMI, Bluetooth remote, Google Play Store, audio, dan Wi-Fi).
4. Aktifkan AdGuard Private DNS (dns.adguard-dns.com) untuk menyekat iklan dan penjejak data sistem secara automatik.
5. Compile/build fail APK Glass TV Home dan pasang sebagai skrin utama (default home launcher).
6. Aktifkan HomeKeyService untuk membolehkan butang Home kembali ke launcher dan fungsi auto-sleep 2 jam berfungsi.
7. Ambil screenshot dari TV dan tunjukkan kepada saya untuk mengesahkan semuanya berjaya dan lancar.
```

---

### 📋 Langkah 3: Tekan "Allow" Pada Skrin TV
Apabila AI mula menyambung ke TV, satu tetingkap pengesahan akan muncul di skrin TV anda:
* *"Allow USB debugging from this computer?"*
* Tandakan kotak: ☑ **"Always allow from this computer"**
* Pilih dan tekan butang **OK / Allow**.

AI akan menguruskan selebihnya: membuang bloatware yang melambatkan TV, mengaktifkan penyekat iklan, memasang Glass TV Home, dan memaparkan gambar tangkap layar (*screenshot*) TV anda yang kini sangat laju dan bersih!

---

### ↩️ Cara Kembali Ke Asal (Undo / Restore)
Semua perubahan yang dibuat oleh AI adalah **100% selamat dan boleh dikembalikan**:
* AI tidak memadam fail sistem secara kekal, hanya menyahaktifkannya.
* AI secara automatik menjana skrip pemulihan (`restore.sh` / `restore.ps1`).
* Jika anda ingin kembali ke skrin asal pengeluar pada masa akan datang, anda hanya perlu beritahu AI:
  > *"Tolong restore semula TV saya kepada tetapan dan launcher asal kilang."*
  Semua aplikasi asal akan dipulihkan serta-merta seperti sedia kala.

---

## 🎮 3. Cara Mengawal Guna Remote TV (D-Pad)

Sangat mudah dan intuitif! Anda hanya perlukan butang arah pada remote TV anda:

| Butang Remote | Tindakan / Fungsi |
| :--- | :--- |
| **Tekan Dua Kali HOME (Double-Press)** | Buka laci **Recent Apps** sistem Android TV (sama seperti menekan butang Recents pada remote yang menyokongnya). |
| **Butang HOME (Sekali Tekan)** | Kembali ke skrin utama dari mana-mana aplikasi. Jika di skrin utama, buka screensaver StandBy. |
| **Butang RECENTS / APP SWITCH** | Buka laci **Recent Apps** sistem Android TV secara terus. |
| **Butang ATAS (▲)** | Naik ke atas untuk pilih **Kapsul Cuaca & Jerebu**, Wi-Fi, Pemasa Tidur, atau butang Tetapan. |
| **Butang TENGAH (OK)** | • Tekan pada aplikasi untuk buka aplikasi tersebut.<br>• Tekan pada kapsul cuaca untuk buka **Dashboard Cuaca & Kualiti Udara penuh**.<br>• **Tekan & Tahan (Long-press)** pada ikon aplikasi untuk buka menu pilihan (Pindah, Sembunyikan, Maklumat App, Tukar Ikon, Muat Naik Ikon). |
| **Butang BAWAH (▼)** | Turun semula ke barisan aplikasi utama (*Dock*). |
| **Butang KIRI / KANAN (◀ / ▶)** | Bergerak antara aplikasi atau beralih antara menu atas. |
| **Butang BACK (↩)** | Keluar dari Dashboard Cuaca atau Screensaver dan kembali ke skrin utama. |

---

## 📱 4. Penerangan Ciri-Ciri Utama

### A. Kapsul Cuaca & Indeks Jerebu (Bawah Jam)
* Terletak betul-betul di bawah jam utama (contoh: `☁ 28° Overcast  •  API 151 Unhealthy`).
* **Tekan butang Atas (▲) lalu tekan OK** untuk membuka skrin cuaca penuh:
  1. **Suhu Semasa & Angin**: Suhu, suhu rasa (*feels like*), kelembapan, kelajuan angin, indeks UV, waktu matahari terbit & terbenam.
  2. **Kualiti Udara (API / IPU)**: Menggunakan bacaan Jabatan Alam Sekitar (APIMS) dengan bar warna pelangi dinamik, penunjuk nilai, stesen perbandingan, dan nasihat kesihatan rasmi.
  3. **Ramalan 12 Jam**: Barisan kad jam demi jam dengan suhu dan peratus kebarangkalian hujan (`↑ %`).
  4. **Ramalan 7 Hari**: Pandangan cuaca sepanjang minggu dengan suhu maksimum/minimum.

### B. Waktu Solat & Ayat Al-Quran Harian
* **Jadual Solat 6 Waktu**: Subuh, Syuruk, Zohor, Asar, Maghrib, Isyak mengikut zon waktu Malaysia secara tepat.
* **Warna Hijau Zamrud**: Waktu solat yang sedang berlangsung akan automatik bertukar warna hijau terang supaya anda sentiasa peka waktu ibadah.
* **Ayat Al-Quran Pilihan**: Ayat al-Quran harian berserta teks Arab yang indah dan terjemahan maksud.

### C. Gambar Latar Belakang Alam Semula Jadi (Bing Nature)
* Gambar landskap definisi tinggi bertukar secara automatik dengan cerita tempat menarik (nama lokasi, negeri/negara, dan penerangan ringkas).

### D. Screensaver StandBy & Pemasa Tidur (Jimat Elektrik)
* **10 Minit Dibiarkan Tanpa Butang**: TV akan automatik masuk mod *StandBy Screensaver* berlatar hitam pekat (selamat untuk mengelakkan *screen burn-in* pada TV OLED/LED), memaparkan jam besar 110sp, tarikh Hijriah (cth: `🌙 12 Jamadilawal 1448H`), dan solat seterusnya.
* **Pemasa Tidur (🌙)**: Tekan kapsul bulan di bar atas untuk tetapkan kiraan undur — 15, 30, 60, atau 120 minit. TV akan padam automatik apabila masa tamat.
* **Untuk Hidupkan Semula**: Tekan mana-mana butang pada remote untuk bangunkan TV.

### E. Recent Apps Sistem (Tekan 2 Kali Butang Home)
* **Cara Buka**: Tekan butang **HOME dua kali berturut-turut** dari mana-mana aplikasi atau skrin utama, atau tekan butang **Recents / App Switch** pada remote yang menyokongnya.
* **Apa Yang Anda Nampak**: Laci Recent Apps asal Android TV — tukar antara aplikasi yang dibuka atau halau mana-mana yang tidak diperlukan terus dari sini.
* **Tip**: Tekan **Back** semasa berada di laci Recents untuk kembali ke skrin utama Glass TV.

### F. Sembunyikan Aplikasi
Bersihkan paparan grid dari aplikasi yang jarang digunakan tanpa memadamkannya:
* **Cara Sembunyikan**: Di skrin utama, **tekan lama** mana-mana ikon aplikasi untuk buka menu pilihan. Pilih **Sembunyikan App** — ikon terus hilang dari grid serta-merta, tanpa perlu mulakan semula launcher.
* **Cara Pulihkan**: Buka **Tetapan Launcher** (ikon gear di sudut atas kanan) dan tatal ke bahagian **Aplikasi Tersembunyi** di bawah. Anda akan nampak senarai aplikasi yang disembunyikan dengan nama sebenar. Tekan **Nyahsembunyi** (*Unhide*) untuk paparkan semula aplikasi tersebut.
* **Nota**: Hanya aplikasi yang betul-betul terpasang pada peranti anda yang dipaparkan dalam senarai ini. Entri lalai untuk aplikasi yang tidak dipasang ditapis secara automatik.

### G. Ubah Suai Ikon Aplikasi
Tukar ikon mana-mana aplikasi mengikut citarasa anda:
* **Cara Ubah**: **Tekan lama** mana-mana ikon aplikasi untuk buka menu pilihan, kemudian pilih salah satu:
  * **Tukar Ikon** — pilih ikon daripada pek ikon yang dipasang (cth: Arcticons, Lawnicons).
  * **Muat Naik Ikon** — gunakan sebarang gambar dari peranti anda sebagai ikon khas.
  * **Tetapkan Semula Ikon** — kembalikan ikon asal aplikasi tersebut.
* **Pek Ikon**: Pasang pek ikon dari Play Store, kemudian pergi ke **Tetapan Launcher → Ikon → Pek Ikon** untuk guna secara menyeluruh pada semua ikon.

---

## 📱 5. Cara Buat Guna Telefon Pintar (Tanpa Komputer / Laptop Langsung!)

**Boleh sangat!** Jika anda tiada komputer atau laptop, anda boleh gunakan telefon pintar Android anda untuk pasang, lajukan TV, dan buang iklan.

---

### Kaedah 1: Guna Aplikasi 'Bugjaeger' (Paling Berkuasa - Pasang APK + Buang Iklan dari Telefon)
Aplikasi **Bugjaeger Mobile ADB** (percuma di Google Play Store telefon) membolehkan telefon anda bertindak seperti komputer untuk mengawal dan mengoptimumkan TV.

1. **Sambung Wi-Fi**: Pastikan telefon dan TV berada di atas Wi-Fi yang sama.
2. **Pasang Bugjaeger**: Muat turun aplikasi **Bugjaeger** di telefon anda dari Play Store.
3. **Sambung ke TV**:
   - Buka Bugjaeger, tekan ikon palam (**Connect 🔌**).
   - Masukkan Alamat IP TV anda (cth: `192.168.68.102`) dan port `5555`.
   - Tekan **Connect**.
   - Pada skrin TV, tekan **Allow** apabila keluar tetingkap *"Allow USB debugging"*.
4. **Pasang Glass TV Terus dari Telefon**:
   - Masuk tab **Packages (ikon kotak 📦)** di Bugjaeger.
   - Tekan butang tambah (**+**) di atas, pilih fail `app-release.apk` yang telah dimuat turun ke telefon anda.
   - Bugjaeger akan memasang Glass TV terus ke TV dalam beberapa saat!
5. **Buang Iklan & Sekat Tracking dari Telefon**:
   - Masuk tab **Commands (ikon terminal ⚡)** di Bugjaeger.
   - Salin dan tampal perintah ini untuk sekat iklan sistem:
     ```bash
     settings put global private_dns_specifier dns.adguard-dns.com
     settings put global private_dns_mode hostname
     ```
   - Masukkan perintah untuk matikan iklan pengeluar (cth Xiaomi PatchWall):
     ```bash
     pm disable-user --user 0 com.mitv.tvhome
     pm disable-user --user 0 com.xiaomi.mitv.advertise
     ```
   - Selesai! TV anda kini bebas iklan dan sangat laju.

---

### Kaedah 2: Paling Ringkas (Hanya Pasang APK Guna 'Send Files to TV')
Jika anda hanya mahu pasang Glass TV Home tanpa buat debloat teknikal:
1. Pasang aplikasi percuma **Send Files to TV** di telefon pintar dan di Android TV anda (dari Google Play Store).
2. Muat turun `app-release.apk` ke telefon anda (dari pautan fail GitHub).
3. Buka aplikasi pada kedua-dua peranti, hantar APK dari telefon ke TV melalui Wi-Fi.
4. Di TV anda, buka aplikasi pengurus fail (seperti *File Commander* atau *FX File Explorer*), klik pada fail APK dan pilih **Install**.
5. Tekan butang **Home** pada remote TV dan pilih **Glass TV Home** sebagai *Always / Sentiasa*.

---

### 🤖 Bolehkah Tanya AI dari Telefon?
Boleh! Anda boleh buka aplikasi AI di telefon (seperti Claude, ChatGPT, atau pelayar web):
1. Beritahu AI jenama TV anda:
   > *"Saya guna telefon dan app Bugjaeger. TV saya model Xiaomi Mi Box S. Berikan saya senarai arahan debloat untuk matikan iklan dan tracking."*
2. AI akan berikan senarai perintah `pm disable-user`.
3. Anda hanya perlu salin dan tampal perintah tersebut ke dalam aplikasi **Bugjaeger** di telefon anda!

---

### 🍏 Khusus untuk Pengguna iPhone (iOS): Boleh Ke Guna iPhone?

**Jawapan jujur: Boleh untuk pasang APK & sekat iklan, tetapi ada sedikit perbezaan berbanding Android.**

Kerana polisi Apple (iOS) yang ketat, Apple tidak membenarkan aplikasi seperti *Bugjaeger / ADB* berada di App Store. Walau bagaimanapun, pengguna iPhone tetap boleh melakukannya dengan sangat mudah:

#### 1. Cara Hantar & Pasang APK dari iPhone ke TV (Guna 'LocalSend'):
* Muat turun aplikasi percuma **LocalSend** di App Store iPhone dan di Google Play Store TV anda.
* Muat turun fail `app-release.apk` di iPhone anda dari pautan GitHub ini.
* Buka LocalSend pada iPhone dan TV (pastikan kedua-duanya bersambung pada Wi-Fi yang sama).
* Hantar APK dari iPhone ke TV dengan 1 klik sahaja!
* Di TV, klik fail APK tersebut dan tekan **Install**.

#### 2. Cara Sekat Iklan & Tracking Jika Guna iPhone (Tanpa Perlu ADB):
Anda tidak perlukan aplikasi ADB pun untuk sekat iklan. Anda boleh lakukannya terus melalui remote TV:
1. Pada remote TV, pergi ke **Settings ⚙️** $\rightarrow$ **Network & Internet**.
2. Pilih **Private DNS** (tukar daripada *Off/Automatic* kepada *Private DNS provider hostname*).
3. Masukkan alamat ini menggunakan papan kekunci di TV:
   ```text
   dns.adguard-dns.com
   ```
4. Tekan **Save / Simpan**.
5. Serta-merta semua iklan sepanduk, penjejak data pengeluar, dan analitik disekat di peringkat sistem TV anda secara automatik!

#### 3. Ingin Buang Bloatware Pengeluar Secara Penuh?
Jika anda ingin membuang aplikasi bawaan pengeluar (seperti Xiaomi PatchWall) sehingga 100% bersih, anda disyorkan menggunakan **laptop/komputer** (atau meminjam telefon Android kawan sekali sahaja selama 2 minit). Selepas dipadam sekali, ia akan kekal bersih selamanya pada TV anda!

---

## 🛠️ 6. Cara Pasang Manual Guna Pendrive (Alternatif)

Jika anda lebih suka menggunakan pendrive USB:
1. Muat turun fail `app-release.apk` (dalam folder `launcher/app/build/outputs/apk/release/`).
2. Salin fail APK tersebut ke dalam pendrive USB.
3. Cucuk pendrive pada Android TV atau Xiaomi Mi Box anda.
4. Buka aplikasi pengurus fail di TV (cth: *File Commander* atau *FX File Explorer* dari Google Play Store TV).
5. Klik pada `app-release.apk` dan tekan **Install**.
   *(Jika TV meminta izin "Install unknown apps", pilih **Allow/Benarkan**)*.
6. Selepas siap dipasang, tekan butang **Home** pada remote TV dan pilih **Glass TV Home** sebagai *Always / Sentiasa*.

---

## ❓ 7. Soalan Lazim (FAQ) Mengenai AI, Telefon & Remote TV

### S1: Bolehkah jalankan Claude Code di telefon (iOS / Android) dan adakah masalah iOS Sandbox menghalang capaian network?
* **Realiti Claude Code di Telefon:** Claude Code ialah perisian Terminal/CLI (`@anthropic-ai/claude-code`) yang memerlukan enjin **Node.js 18+**. Di iPhone (iOS), sistem keselamatan Sandbox Apple tidak membenarkan pelaksanaan enjin Node.js atau shell secara terus di dalam telefon.
* **Isu Capaian Rangkaian (Cloud vs Local Network):** Claude Code (atau mana-mana AI awan seperti Claude.ai / ChatGPT) berjalan di server awan. Server awan di internet **TIDAK BOLEH menembusi Wi-Fi rumah anda** untuk mencapai IP peribadi TV (`192.168.x.x`) kerana disekat oleh *firewall router* rumah anda.
* **Penyelesaian:** Arahan ADB mesti dilancarkan dari peranti yang berada di dalam rangkaian Wi-Fi rumah yang sama:
  - **Paling senang:** Guna laptop / komputer (Google Antigravity percuma atau Claude Code di terminal laptop).
  - **Guna telefon pintar:** Pengguna Android boleh guna aplikasi tempatan seperti **Bugjaeger Mobile ADB**.

---

### S2: Adakah AI yang boleh pasang dan buat semua tugasan secara terus tanpa pengguna perlu pasang APK sendiri?
* **YA, jika menggunakan Komputer / Laptop (Google Antigravity / Claude Code / Cursor):**
  AI pada komputer boleh bersambung terus ke TV melalui Wi-Fi rumah (`adb connect 192.168.x.x`). AI akan membina APK $\rightarrow$ menghantar ke TV $\rightarrow$ memasang APK $\rightarrow$ memadam iklan pengeluar $\rightarrow$ dan menguji skrin secara automatik 100%. Anda tidak perlu muat turun atau sentuh fail APK langsung!
* **Jika Hanya Menggunakan Telefon:** Anda perlu bantuan aplikasi perantara tempatan (Android guna *Bugjaeger*, iPhone hantar fail melalui *LocalSend*).

---

### S3: Jika pengguna hanya pasang fail APK, adakah boleh akses penuh menggunakan remote TV atau perlukan akses AI juga?
* **100% BOLEH GUNA REMOTE SAHAJA, TIDAK PERLUKAN AI LAGI!**
* Glass TV Home dibina sebagai aplikasi Android TV asli (*Native Android TV app*).
* Sebaik sahaja fail APK terpasang pada TV:
  - **Remote TV berfungsi sepenuhnya**: Butang D-Pad (Atas/Bawah/Kiri/Kanan/OK) untuk kawal aplikasi dan cuaca, serta butang Home/Back berfungsi seperti biasa.
  - **Semua ciri automatik berjalan sendiri**: Jam, jadual waktu solat, cuaca & kualiti udara (APIMS), mod screensaver 10 minit, dan auto-sleep 2 jam dijana sepenuhnya oleh cip pemproses TV tanpa bergantung kepada mana-mana AI atau internet luar.
  - Ahli keluarga dan pengguna biasa boleh menikmati TV dengan lancar hanya menggunakan remote TV biasa.
* **Peranan AI hanyalah seperti "jurutera"** yang membantu memasang dan mencuci bloatware pada peringkat permulaan sahaja.

---

## 📄 Lesen & Penghargaan
* Dibina berasaskan [CleanYourTV](https://github.com/Gathin23/cleanyourtv) © Gathin (Lesen MIT).
* Fon Outfit oleh SIL Open Font License.
* Data cuaca oleh Open-Meteo & data kualiti udara berasaskan piawaian APIMS Jabatan Alam Sekitar Malaysia.
