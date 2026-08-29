# Desain Improvement UI/UX NutriGrow

Tanggal: 29 Agustus 2026
Branch: `dev`
Status: menunggu tinjauan

---

## 1. Latar

Aplikasi Android NutriGrow sudah berfungsi dan tersambung penuh ke backend v2,
tetapi tampilannya tumbuh tanpa fondasi bersama. Pengukuran atas kode saat ini:

| Yang diukur | Angka |
|---|---|
| Pemakaian `Color(0x…)` langsung | 477 pemakaian, 48 warna unik |
| Warna yang paling sering ditulis ulang | `0xFF9DA1A6` sebanyak 267 kali |
| Literal `fontSize` yang menimpa skala tipografi | 329 |
| Berkas yang merakit `TopAppBar` sendiri | 27 |
| Pemakaian `Button` | 90, tersebar di 52 berkas |
| Pemakaian `TextField` | 94, tersebar di 23 berkas |
| Keadaan kosong "Tidak Ada Data" yang dirakit tangan | 14 |
| `AlertDialog` | 13 |
| `Toast` yang berdampingan dengan Snackbar | 4 |
| Pemakaian `stringResource` | 8 |

Tiga berkas layar terbesar masing-masing berupa **satu** fungsi `@Composable`:

| Berkas | Baris | Indentasi terdalam |
|---|---|---|
| `ui/screen/growth/detail/DetailGrowthScreen.kt` | 1325 | 108 spasi |
| `ui/screen/child/ChildProfileScreen.kt` | 1303 | 88 spasi |
| `ui/screen/officer/OfficerProfileScreen.kt` | 848 | 60 spasi |

Di `ChildProfileScreen.kt` baris 760–950 terdapat tiga blok `when` berisi teks
anjuran gizi — 11 teks, total 7.474 karakter — yang ditulis langsung di dalam
pohon Compose dan selalu tampil terbuka.

Navigasinya hibrida: `NavHost` dengan 9 rute di dalam `MainActivity`, ditambah
25 Activity terpisah yang diluncurkan lewat `Intent`.

---

## 2. Keputusan yang sudah diambil

Lima keputusan berikut diambil bersama user sebelum desain ini disusun. Semuanya
mengunci cakupan.

| # | Keputusan | Alasan |
|---|---|---|
| 1 | **Navigasi dirapikan, bukan dibongkar.** 25 Activity tetap. | Migrasi ke `NavHost` tunggal menyentuh setiap pemanggil `Intent` dan menuntut pengujian ulang seluruh layar. Alur yang sudah teruji tidak diusik. |
| 2 | **Token dibangun, migrasi sambil jalan.** | Mengganti seluruh 477 warna literal sekaligus menyentuh hampir setiap berkas UI dan berisiko tanpa uji otomatis. |
| 3 | **Detail dibuka dengan perluas di tempat.** | Pembaca tidak kehilangan konteks angka, bisa membandingkan dua kartu sekaligus, dan tidak ada lapisan modal baru. |
| 4 | **Empat tab untuk semua peran.** | Orang Tua kini hanya punya dua tab, sehingga jadwal, artikel, MPASI, dan profil balita semuanya bergantung pada Beranda. |
| 5 | **Fondasi dulu, lalu per alur.** | Komponen yang matang lebih awal membuat setiap layar berikutnya lebih cepat, dan konsistensi datang dari komponen, bukan dari disiplin. |

---

## 3. Prinsip yang menuntun

**Token merekam keadaan sekarang, bukan memaksakan skala baru.** Karena migrasi
dilakukan sambil jalan, layar yang sudah dipindah dan yang belum akan
berdampingan. Kalau token memakai ritme 4dp anjuran Material sementara sisa
aplikasi memakai ritme 5dp, setiap batas antar layar terlihat sebagai sambungan
yang tidak rapi. Karena itu token mengikuti ritme dan ukuran yang sudah dipakai.

**Pemindahan dipisahkan dari perubahan.** Memecah berkas 1300 baris dikerjakan
sebagai pemindahan murni lebih dulu — potong, tempel, tambahkan tanda tangan
fungsi, tanpa satu pun baris tampilan berubah — lalu dibuktikan dengan build.
Penataan ulang menyusul sebagai langkah terpisah. Kalau keduanya digabung dan
hasilnya salah, tidak ada cara membedakan apakah yang rusak pemecahannya atau
penataannya.

**Setiap langkah berhenti di titik yang bisa dikompilasi.** Aturan repo melarang
agent menjalankan build, dan aplikasi ini tidak punya uji otomatis. Satu-satunya
bukti adalah `./gradlew assembleDebug` yang dijalankan user. Karena itu pekerjaan
diserahkan per langkah, bukan ditumpuk.

---

## 4. Fondasi

### 4.1 `ui/theme/Spacing.kt` (baru)

Enam nilai, diambil dari ritme yang sudah mendominasi kode (`10.dp` 422 kali,
`5.dp` 197 kali, `15.dp` 170 kali, `20.dp` 146 kali):

```
xs 5 · sm 10 · md 15 · lg 20 · xl 25 · xxl 45
```

### 4.2 `ui/theme/Type.kt` (baru)

Poppins sudah terpasang di `Theme.kt`, tetapi hanya `fontFamily`-nya; ukurannya
dibiarkan bawaan Material lalu ditimpa 329 literal `fontSize` di layar. Skala
berikut memakai ukuran yang mencerminkan pemakaian nyata, sehingga mengganti
`fontSize = 15.sp` dengan `bodyMedium` tidak mengubah tampilan.

| Token | Ukuran | Tebal | Catatan |
|---|---|---|---|
| `titleLarge` | 20 sp | SemiBold | menggantikan literal 19–20 sp (17 pemakaian) |
| `titleMedium` | **tidak diubah** | Medium | lihat peringatan di bawah |
| `titleSmall` | 15 sp | Medium | 1 pemakaian |
| `bodyLarge` | 16 sp | Regular | sama dengan bawaan Material |
| `bodyMedium` | 15 sp | Regular | dari bawaan 14 sp; 10 pemakaian |
| `bodySmall` | 13 sp | Regular | dari bawaan 12 sp; 13 pemakaian |

**`titleMedium` sengaja tidak disentuh.** Rancangan awal menaikkannya ke 17 sp
SemiBold, mengikuti sebaran literal `fontSize`. Perhitungan ulang atas pemakaian
token — bukan literal — menunjukkan `MaterialTheme.typography.titleMedium`
dipakai **221 kali**, jauh melampaui token lain:

| Token | Pemakaian |
|---|---|
| `titleMedium` | 221 |
| `bodySmall` | 13 |
| `bodyMedium` | 10 |
| `titleLarge` | 5 |
| `labelSmall` | 2 |
| `titleSmall` | 1 |

Di aplikasi ini `titleMedium` sudah berfungsi sebagai gaya teks umum, bukan gaya
judul. Menaikkan ukurannya dan menebalkannya akan mengubah 221 tempat sekaligus,
termasuk seluruh teks pengganti pada kolom isian. Prinsip di bagian 3 — token
merekam keadaan sekarang — justru menuntut token ini dibiarkan pada nilai
bawaannya.

### 4.3 `ui/theme/Color.kt` (dirapikan)

Token bernama menggantikan angka heksadesimal yang berulang:

| Token | Nilai | Pemakaian sekarang |
|---|---|---|
| `BrandGreen` | `0xFF00BF63` | 75 |
| `BrandGreenSoft` | `0xFFE0FFD2` | 51 |
| `FieldOutline` | `0xFF9DA1A6` | 267 |
| `TextPrimary` | `0xFF000000` | — |
| `TextSecondary` | `0xFF575555` | — |

Warna status gizi tetap di `ui/state/GrowthStatus.kt` yang sudah ada. Tempatnya
memang di sana karena terikat makna klinis, bukan identitas merek.

### 4.4 Mode gelap dinyatakan di luar cakupan

Palet gelap di `Color.kt` sekarang berwarna oranye, sisa templat yang tidak
pernah disesuaikan, sementara palet terang berwarna hijau. Memperbaiki paletnya
saja tidak menolong: dengan 477 warna literal — sebagian besar `Color.White`
sebagai latar kartu — mode gelap tetap pecah. Aplikasi dikunci ke mode terang.
Menyediakan mode yang rusak lebih buruk daripada tidak menyediakannya.

---

## 5. Komponen bersama

Delapan komponen baru. Masing-masing dibenarkan oleh pengulangan terukur.

| Komponen | Menyerap |
|---|---|
| `NutriTopBar` | `TopAppBar` yang dirakit ulang di 27 berkas |
| `NutriTextField` | 94 `TextField` di 23 berkas, dan 267 pemakaian `FieldOutline` |
| `NutriButton` | 90 `Button` di 52 berkas |
| `StatusChip` | pil status gizi di `ChildProfileScreen`, `CheckUpCard`, `ChildCard` |
| `ExpandableInfoCard` | pola "Selengkapnya" |
| `MeasurementCard` | blok pengukuran di `ChildProfileScreen` |
| `EmptyState` | 14 keadaan "Tidak Ada Data" yang dirakit tangan |
| `ConfirmDialog` | 13 `AlertDialog` |

Yang sudah ada dipakai ulang, tidak ditulis ulang: `ui/component/respond/`
(`ErrorMessage`, `OriginalLoading`, `LoadingIndicator`) dan
`ui/component/snackbar/CustomSnackBar.kt`.

### 5.1 `MeasurementCard` — inti perubahan

Sekarang satu pengukuran memakai dua kartu, header hijau dan badan hijau muda,
sehingga tiga pengukuran terlihat seperti enam blok. Usulannya satu kartu:

```kotlin
MeasurementCard(
    label = "Berat Badan",
    value = "11,2 kg",
    status = growth.wfa_status,
    statusLabel = wfaLabel(growth.wfa_status),
    zScore = growth.wfa_zscore,
    advice = wfaAdvice(growth.wfa_status),
)
```

Di dalamnya `MeasurementCard` memanggil `StatusChip` dan `ExpandableInfoCard`.
Tiga pengukuran di layar menjadi tiga baris pemanggilan, menggantikan sekitar
400 baris pohon Compose bersarang.

Keadaan tertutup menampilkan label, nilai, status berwarna, z-score, dan dua
baris pertama anjuran dengan `TextOverflow.Ellipsis`, ditutup tombol
"Selengkapnya". Perluasan memakai `AnimatedVisibility`.

### 5.2 `ui/state/GrowthAdvice.kt` (baru)

Sebelas teks anjuran (7.474 karakter) pindah ke sini, bersebelahan dengan
`GrowthStatus.kt`. Bentuknya mengikuti pola yang sudah ada di berkas itu:

```kotlin
fun wfaAdvice(status: String): String
fun hfaAdvice(status: String): String
fun wfhAdvice(status: String): String
```

Tempat yang lebih benar sebenarnya `res/values/strings.xml`. Itu tidak diusulkan
di sini karena aplikasi baru memakai `stringResource` 8 kali; memindahkan 11 teks
ke sana menjadikan berkas sumber daya satu-satunya tempat berbahasa Indonesia
sementara sisanya tetap tertanam di kode — setengah jalan yang tidak menolong.
Pemindahan menyeluruh seluruh teks aplikasi ke `strings.xml` adalah pekerjaan
tersendiri, di luar cakupan desain ini.

---

## 6. Navigasi

### 6.1 Empat tab, tanpa rute baru

`Screen.Home`, `Screen.Children`, `Screen.Event`, dan `Screen.Profile` keempatnya
sudah terdaftar di `NavHost`. Sebagian hanya tidak pernah ditawarkan ke sebagian
peran. Yang berubah adalah daftar item, bukan grafik navigasi.

| Peran | Sekarang | Usulan |
|---|---|---|
| Admin | Beranda · Jadwal · Profil | Beranda · Balita · Jadwal · Profil |
| Kader | Beranda · Balita · Profil | Beranda · Balita · Jadwal · Profil |
| Orang Tua | Beranda · Profil | Beranda · Balita · Jadwal · Profil |

Tab keempat Admin adalah **Balita**, bukan Kader seperti yang sempat diusulkan
saat pengambilan keputusan. Alasannya baru terlihat setelah kode dibaca lebih
teliti: daftar Kader bagi Admin **sudah merupakan isi Beranda** —
`HomeAdminContent` yang menampilkan kartu Kader beserta kolom pencariannya. Tab
Kader tersendiri hanya akan menggandakan Beranda. Tab Balita, sebaliknya, memakai
rute `Screen.Children` yang sudah ada dan sudah mendukung Admin lewat cabang
`getChildrenByName`.

Karena ketiganya menjadi identik, `AdminBottomBar.kt`, `OfficerBottomBar.kt`, dan
`ParentBottomBar.kt` lebur menjadi satu `NutriBottomBar`. Penyesuaian per peran
sudah terjadi di dalam masing-masing layar: `EventScreen` menampilkan tombol
tambah hanya untuk Admin, `ChildrenScreen` menyaring per wilayah untuk Kader.

Perpindahan tab tidak perlu disentuh — `saveState`, `restoreState`, dan
`launchSingleTop` sudah terpasang benar, sehingga isi tab tidak dimuat ulang.

### 6.2 Prasyarat: cacat data Orang Tua

Di `ui/screen/child/list/ChildrenScreen.kt` baris 94–101:

```kotlin
if (role == "Officer") viewModel.getChildrenByNameAndRegion(keyword, region)
else                   viewModel.getChildrenByName(keyword)
```

Cabang `else` mengambil **seluruh balita di desa**. Aman untuk Admin, salah untuk
Orang Tua: mereka akan melihat data anak orang lain. Percabangannya harus menjadi
tiga arah, dengan Orang Tua memakai `getChildrenByParent(auth.id)` yang sudah ada
di `ChildRepository` dan memanggil `GET children/parent/{id}` —
`HomeParentContent` memang sudah memakai jalur itu.

**Ini prasyarat, bukan tambahan.** Tab Balita tidak boleh dibuka untuk Orang Tua
sebelum cabang ini benar.

### 6.3 Kerangka layar

- `Screen` mendapat properti `showsBottomBar`, menggantikan rantai boolean di
  `NutriGrowApp.kt` yang menyebut nama rute satu per satu dan akan terus tumbuh.
- Ke-25 Activity memakai `NutriTopBar` yang sama, sehingga judul, tombol kembali,
  dan warnanya seragam.
- Pola "selesai lalu kembali ke daftar sebelumnya" dibakukan jadi satu pembantu.
  Pola ini sudah ditulis tangan di dua layar hapus akun dan akan muncul lagi di
  setiap layar tambah dan ubah.

---

## 7. Layar, per alur

Pola pemecahan mengikuti yang sudah diterapkan pada `HomeScreen`: satu berkas
orkestrasi, sisanya composable isi yang berdiri sendiri. Batas praktis sekitar
300 baris per berkas.

### 7.1 Alur Orang Tua

**`ChildProfileScreen`** (1303 baris) dipecah menjadi `ChildProfileScreen.kt`
(orkestrasi), `ChildIdentityCard`, dan `ChildGrowthSection`.

Bagian pengukuran menjadi **lima** `MeasurementCard`, bukan tiga. Lingkar kepala
dan lingkar lengan sekarang hanya tampil sebagai angka telanjang tanpa
keterangan, padahal `head_circum_status` dan `muac_status` sudah dikirim backend
dan sudah punya pewarnaan di `GrowthStatus.kt`. Keduanya naik menjadi pengukuran
penuh dengan status.

Di atas kelima kartu dipasang **panji rujukan 2T**: bila `needs_referral` bernilai
benar, muncul panji merah berisi jumlah penimbangan berturut-turut tanpa kenaikan
dan anjuran merujuk ke tenaga kesehatan. Ini sinyal paling penting secara klinis
di seluruh aplikasi dan saat ini tidak muncul di layar yang justru dilihat orang
tua; ia sudah ada di `AssessmentCard`, tetapi kartu itu baru terpasang di
`DetailGrowthScreen`.

**`DetailGrowthScreen`** (1325 baris) isinya sudah benar — empat `ZScoreChart`
dan `AssessmentCard` sudah terpasang. Ini murni pemecahan berkas, tanpa
perubahan perilaku.

**`HomeParentContent`** (644 baris) menyusut. Dengan Balita dan Jadwal punya tab
sendiri, Beranda berhenti menjadi satu-satunya pintu dan fokus pada: status
terkini tiap balita, jadwal Posyandu terdekat, lalu pintasan MPASI dan Artikel.

### 7.2 Alur Kader

- `ChildrenScreen` — percabangan tiga arah dari bagian 6.2, ditambah pencarian
  dan tombol peluluskan yang sudah ada.
- `AddGrowthScreen`, `UpdateGrowthScreen`, `AddChildScreen`, `UpdateChildScreen`
  — beralih ke `NutriTextField` dan `NutriButton`. Di sini pemakaian
  `FieldOutline` paling banyak terserap.
- `ReportScreen` menyusul.

### 7.3 Alur Admin

`HomeAdminContent` sudah dirapikan pada putaran sebelumnya, jadi hanya ikut
memakai komponen bersama.

### 7.4 Sisanya

MPASI, Artikel, Vaksin, dan Jadwal hanya menerima `NutriTopBar` dan token, tanpa
penataan ulang.

---

## 8. Umpan balik dan keadaan

- **Umpan balik disatukan.** Empat pemakaian `Toast` dihapus; semuanya lewat
  Snackbar memakai `CustomSnackBar` yang sudah ada. Karena tiap Activity punya
  `Scaffold` sendiri, ini pembantu per layar, bukan wadah global.
- **Tiga keadaan dibakukan.** Memuat dan galat sudah punya `OriginalLoading` dan
  `ErrorMessage`. Yang belum ada adalah keadaan kosong — 14 pemakaian
  "Tidak Ada Data" dirakit tangan satu per satu. `EmptyState` mengisinya.
- **`UiState.Unauthorized`** sudah punya jalur ke `redirectToWelcome` dan tinggal
  dipakai konsisten.

---

## 9. Verifikasi

Aplikasi ini tidak punya uji otomatis, dan aturan repo melarang agent menjalankan
build. Satu-satunya bukti adalah `./gradlew assembleDebug` yang dijalankan user.

Yang menggantikan uji:

1. Setiap langkah berhenti di titik yang bisa dikompilasi dan diserahkan ke user
   di situ.
2. Pemindahan berkas dipisahkan dari perubahan tampilan (lihat bagian 3).
3. Pemindai simbol dijalankan sebelum tiap serah terima. Ia mengumpulkan setiap
   identifier berhuruf besar yang dipakai sebagai akar ekspresi lalu mencocokkannya
   dengan impor, deklarasi lokal, dan deklarasi satu paket. Ia menangkap rujukan
   tak dikenal — kelas galat yang paling sering muncul saat memindahkan kode
   antarberkas — tetapi **tidak** menangkap ketidakcocokan tipe.

---

## 10. Urutan pengerjaan

Tiap nomor adalah satu titik build.

| # | Langkah | Keluaran |
|---|---|---|
| 1 | Perbaiki cacat data Orang Tua di `ChildrenScreen` | Prasyarat keamanan data |
| 2 | Fondasi | `Spacing.kt`, `Type.kt`, `Color.kt` |
| 3 | Delapan komponen bersama | `ui/component/` + `GrowthAdvice.kt` |
| 4 | `NutriBottomBar` dan empat tab | `Screen.showsBottomBar`, tiga berkas bilah bawah lebur jadi satu |
| 5 | Alur Orang Tua | `ChildProfileScreen` dipindah, lalu ditata; `DetailGrowthScreen` dipecah; `HomeParentContent` disusutkan |
| 6 | Alur Kader | Daftar dan layar isian |
| 7 | Sisanya | `NutriTopBar` dan token pada layar yang belum tersentuh |

---

## 11. Di luar cakupan

Keempatnya dibahas dan dibuang dengan alasan, bukan terlupa.

| Yang tidak dikerjakan | Alasan |
|---|---|
| Mode gelap | Palet gelap masih oranye sisa templat, dan 477 warna literal membuatnya tetap pecah walau paletnya dibetulkan (bagian 4.4). |
| Pemindahan teks ke `strings.xml` | Setengah jalan tidak menolong; pemindahan menyeluruh adalah pekerjaan tersendiri (bagian 5.2). |
| Migrasi ke `NavHost` tunggal | Menyentuh setiap pemanggil `Intent` dan menuntut pengujian ulang seluruh layar (keputusan 1). |
| Penggantian menyeluruh 477 warna literal | Menyentuh hampir setiap berkas UI tanpa uji otomatis sebagai jaring pengaman (keputusan 2). |
