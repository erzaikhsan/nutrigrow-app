# Merilis APK NutriGrow

Distribusi memakai **GitHub Releases**: gratis, tanpa kartu kredit, tanpa masa
tinjau, dan tiap rilis punya URL permanen yang bisa dikutip di naskah skripsi.

Play Store sengaja tidak dipakai. Biaya pendaftarannya $25 menuntut kartu, dan
akun developer perorangan diwajibkan mengumpulkan 12 penguji aktif selama 14
hari berturut-turut sebelum aplikasi boleh naik ke produksi.

---

## Sekali seumur proyek: membuat keystore

Semua APK yang dipasang orang lain wajib ditandatangani. Tanda tangan itu juga
yang membuat Android mengenali sebuah APK baru sebagai *pembaruan* dari yang
sudah terpasang, bukan aplikasi lain.

```bash
mkdir -p ~/.android-keys
keytool -genkeypair -v \
  -keystore ~/.android-keys/nutrigrow-release.jks \
  -alias nutrigrow \
  -keyalg RSA -keysize 2048 -validity 10000
```

`keytool` akan menanyakan kata sandi keystore, kata sandi kunci, lalu identitas
pemilik. Isian identitas boleh apa saja; yang penting kata sandinya diingat.

> **Cadangkan berkas `.jks` itu ke tempat lain.** Kalau hilang, kamu tidak bisa
> lagi merilis pembaruan yang menimpa pemasangan lama — pengguna harus mencopot
> aplikasinya dulu, dan semua data lokalnya ikut hilang. Tidak ada cara
> memulihkan keystore yang hilang.

Lalu buat `keystore.properties` di akar repo:

```properties
storeFile=/home/<user>/.android-keys/nutrigrow-release.jks
storePassword=<kata sandi keystore>
keyAlias=nutrigrow
keyPassword=<kata sandi kunci>
```

Berkas itu dan seluruh `*.jks` sudah masuk `.gitignore`; **jangan pernah
di-commit.** `app/build.gradle.kts` membacanya secara opsional — bila berkasnya
tidak ada, `assembleDebug` tetap jalan dan hanya `assembleRelease` yang
menghasilkan APK tanpa tanda tangan.

---

## Tiap rilis

### 1. Naikkan versi

Di `app/build.gradle.kts`:

```kotlin
versionCode = 2
versionName = "1.1"
```

`versionCode` **wajib** naik tiap rilis — Android menolak memasang APK dengan
`versionCode` yang sama atau lebih rendah di atas yang sudah terpasang.
`versionName` yang tampil di kartu halaman "Tentang NutriGrow".

### 2. Bangun

```bash
./gradlew clean assembleRelease
```

Hasilnya ada di `app/build/outputs/apk/release/`. Periksa isi folder itu
karena nama berkasnya bisa berbeda antar konfigurasi. Pastikan **APK**, bukan
AAB — AAB hanya bisa dipasang lewat Play Store.

Verifikasi tanda tangannya sebelum dibagikan:

```bash
$ANDROID_HOME/build-tools/35.0.0/apksigner verify --print-certs \
  app/build/outputs/apk/release/app-release.apk
```

### 3. Unggah

Lewat antarmuka web GitHub: **Releases → Draft a new release**, isi tag
(`v1.1`), pilih target branch, tulis judul dan catatan rilis, lalu seret berkas
APK-nya ke kotak lampiran. Beri nama berkas yang memuat versinya, misalnya
`NutriGrow-V1.1.apk`, supaya penerima tahu versi mana yang mereka unduh.

Kalau `gh` terpasang, hal yang sama bisa lewat terminal:

```bash
gh release create v1.1 app/build/outputs/apk/release/NutriGrow-V1.1.apk \
  --target dev \
  --title "NutriGrow 1.1" \
  --notes "Ringkasan perubahan."
```

---

## Petunjuk untuk penerima

Sertakan ini di catatan rilis, karena hampir semua orang tersandung di langkah
kedua:

1. Unduh berkas `.apk` lewat peramban di HP.
2. Saat muncul peringatan, buka **Setelan → Aplikasi → Akses aplikasi khusus →
   Instal aplikasi tidak dikenal**, lalu izinkan peramban atau pengelola berkas
   yang dipakai membuka berkas tadi. Letak menunya sedikit berbeda antar merek.
3. Buka berkasnya lagi, lalu pasang.

Minimal Android 8.0 (API 26). Aplikasi menghubungi
`https://nutrigrow-rest-api.vercel.app/api/v1/`, jadi butuh koneksi internet.
