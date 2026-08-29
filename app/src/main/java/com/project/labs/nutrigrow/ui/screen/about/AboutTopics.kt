package com.project.labs.nutrigrow.ui.screen.about

import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Bullets
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Formula
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Heading
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Note
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Paragraph
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Reference
import com.project.labs.nutrigrow.ui.screen.about.AboutBlock.Table

val ABOUT_TOPICS: List<AboutTopic> = listOf(

    AboutTopic(
        id = "tentang",
        group = ABOUT_GROUP_APP,
        title = "Tentang NutriGrow",
        summary = "Untuk siapa aplikasi ini dibuat dan apa yang dipantaunya",
        blocks = listOf(
            Paragraph(
                "NutriGrow adalah aplikasi pemantauan gizi balita untuk Posyandu di Desa Jipang, " +
                    "Kecamatan Karanglewas, Kabupaten Banyumas. Aplikasi ini melayani lima posyandu, " +
                    "yaitu Pamuji 1 sampai Pamuji 5, yang masing-masing menaungi satu RW."
            ),
            Paragraph(
                "Yang dipantau adalah balita berumur 0 sampai 60 bulan. Setelah melewati 60 bulan, " +
                    "balita dinyatakan lulus dari pemantauan dan datanya tetap tersimpan sebagai riwayat."
            ),
            Heading("Yang dicatat setiap penimbangan"),
            Bullets(
                listOf(
                    "Berat badan, dalam kilogram",
                    "Tinggi atau panjang badan, dalam sentimeter",
                    "Lingkar kepala, dalam sentimeter",
                    "Lingkar lengan atas (LiLA), dalam sentimeter",
                )
            ),
            Paragraph(
                "Dari empat ukuran itu aplikasi menghitung enam penilaian: BB/U, TB/U, BB/TB, LK/U, " +
                    "status LiLA, dan status kenaikan berat badan. Cara menghitung setiap penilaian " +
                    "dijelaskan pada bagian Cara Aplikasi Menghitung."
            ),
            Note(
                "Aplikasi ini disusun sebagai bahan penelitian skripsi. Seluruh angka rujukan yang " +
                    "dipakai berasal dari peraturan resmi, bukan ditulis berdasarkan perkiraan."
            ),
        ),
    ),

    AboutTopic(
        id = "peran",
        group = ABOUT_GROUP_APP,
        title = "Peran dan Hak Akses",
        summary = "Siapa yang bisa melihat dan mengubah apa",
        blocks = listOf(
            Paragraph(
                "Setiap akun memiliki satu peran, dan peran itu menentukan menu yang muncul serta " +
                    "data yang boleh dibuka."
            ),
            Table(
                headers = listOf("Peran", "Yang bisa dilakukan"),
                rows = listOf(
                    listOf(
                        "Orang Tua",
                        "Melihat data balitanya sendiri: riwayat penimbangan, grafik pertumbuhan, " +
                            "riwayat imunisasi, jadwal kegiatan, artikel, dan resep MPASI."
                    ),
                    listOf(
                        "Kader",
                        "Semua yang bisa dilakukan Orang Tua, ditambah mencatat penimbangan, " +
                            "mendaftarkan balita dan orang tua, mencatat imunisasi, membuat kegiatan " +
                            "posyandu, dan mengunduh laporan. Terbatas pada wilayah RW-nya sendiri."
                    ),
                    listOf(
                        "Admin",
                        "Seluruh wilayah, ditambah pendaftaran akun Kader, laporan lintas posyandu, " +
                            "dan menu Uji Validasi Z-Score."
                    ),
                ),
            ),
            Heading("Pembatasan wilayah"),
            Paragraph(
                "Kader hanya dapat membuka data balita di RW tempat ia bertugas. Pembatasan ini " +
                    "dijalankan di sisi server, bukan sekadar disembunyikan di layar, sehingga tidak " +
                    "bisa dilewati dengan cara apa pun dari aplikasi."
            ),
            Note(
                "Pendaftaran akun Kader wajib memakai token Admin. Pada versi lama aplikasi ini, " +
                    "siapa pun dapat membuat akun Kader tanpa persetujuan."
            ),
        ),
    ),

    AboutTopic(
        id = "fitur",
        group = ABOUT_GROUP_FEATURE,
        title = "Peta Fitur",
        summary = "Isi setiap menu dan siapa yang dapat memakainya",
        blocks = listOf(
            Table(
                headers = listOf("Menu", "Isi", "Untuk"),
                rows = listOf(
                    listOf("Beranda", "Ringkasan balita, kegiatan terdekat, dan pintasan ke menu lain", "Semua"),
                    listOf("Data Balita", "Daftar balita beserta identitas dan ukuran saat lahir", "Kader, Admin"),
                    listOf("Pertumbuhan", "Riwayat penimbangan, z-score, grafik, dan saran gizi", "Semua"),
                    listOf("Imunisasi", "Riwayat imunisasi per balita", "Semua"),
                    listOf("Kegiatan", "Jadwal kegiatan posyandu beserta pengingatnya", "Semua"),
                    listOf("Artikel", "Bacaan seputar gizi dan tumbuh kembang balita", "Semua"),
                    listOf("Resep MPASI", "Resep makanan pendamping ASI menurut kelompok umur", "Semua"),
                    listOf("Laporan", "Laporan UPGK dan rekap data dalam bentuk PDF", "Kader, Admin"),
                    listOf("Uji Validasi", "Pengujian perhitungan z-score terhadap data uji", "Admin"),
                ),
            ),
            Heading("Pengingat Kegiatan"),
            Paragraph(
                "Bila dinyalakan dari halaman Profil, aplikasi mengirim notifikasi setiap pagi pada " +
                    "hari yang ada kegiatan posyandu. Pengingat ini berjalan di perangkat, jadi tetap " +
                    "muncul walaupun aplikasi sedang tidak dibuka."
            ),
        ),
    ),

    AboutTopic(
        id = "grafik",
        group = ABOUT_GROUP_FEATURE,
        title = "Cara Membaca Grafik",
        summary = "Beda grafik Z-Score dan Grafik WHO, serta cara menyimpannya",
        blocks = listOf(
            Paragraph(
                "Halaman pertumbuhan menyediakan dua bentuk grafik. Tombol pemilih di bagian atas " +
                    "memindahkan tampilan dari satu bentuk ke bentuk lainnya."
            ),
            Heading("Grafik Z-Score"),
            Paragraph(
                "Menampilkan hasil perhitungan, bukan kurva rujukan. Sumbu mendatar adalah umur " +
                    "dalam bulan, sumbu tegak adalah nilai z-score. Garis nol berarti tepat di median " +
                    "WHO. Semakin jauh garis balita dari nol, semakin jauh ukurannya dari median. " +
                    "Angka di setiap titik adalah nilai z-score pada penimbangan itu."
            ),
            Paragraph(
                "Grafik ini paling berguna untuk melihat arah: apakah keadaan balita membaik, " +
                    "bertahan, atau memburuk dari bulan ke bulan."
            ),
            Heading("Grafik WHO"),
            Paragraph(
                "Bentuk kurva yang lazim dipakai pada Buku KIA. Garis-garis lengkung yang tercetak " +
                    "adalah batas SD terbitan WHO, dan titik balita digambar di atasnya. Membaca " +
                    "grafik ini sama dengan membaca KMS: yang penting adalah di antara dua garis " +
                    "mana titiknya berada, dan ke arah mana garis penghubungnya bergerak."
            ),
            Table(
                headers = listOf("Warna garis", "Artinya"),
                rows = listOf(
                    listOf("Hijau (0)", "Median, yaitu nilai tengah balita sehat seumurnya"),
                    listOf("Jingga (-1 dan +1)", "Masih dalam rentang normal"),
                    listOf("Merah (-2 dan +2)", "Batas kategori; di luar garis ini perlu perhatian"),
                    listOf("Hitam (-3 dan +3)", "Batas kategori berat"),
                ),
            ),
            Heading("Menyimpan grafik"),
            Paragraph(
                "Setiap grafik punya tombol Simpan grafik di bawahnya. Tombol itu memotret grafik " +
                    "menjadi berkas PNG dan membuka pilihan aplikasi untuk membagikan atau " +
                    "menyimpannya, misalnya untuk dilampirkan pada laporan."
            ),
        ),
    ),

    AboutTopic(
        id = "laporan",
        group = ABOUT_GROUP_FEATURE,
        title = "Laporan Posyandu",
        summary = "Isi laporan UPGK dan asal setiap angkanya",
        blocks = listOf(
            Paragraph(
                "Laporan UPGK merekap keadaan seluruh balita pada satu bulan dan satu posyandu. " +
                    "Setiap baris dihitung langsung dari data penimbangan bulan itu, bukan diisi " +
                    "manual, sehingga tidak bisa berbeda dari data yang tersimpan."
            ),
            Heading("Kelompok umur"),
            Paragraph(
                "Kolom laporan dibagi menurut kelompok umur dan jenis kelamin: 0 sampai 4 bulan, " +
                    "5 bulan, 6 sampai 11 bulan, 12 sampai 23 bulan, dan 24 sampai 59 bulan."
            ),
            Heading("Baris yang dihitung"),
            Bullets(
                listOf(
                    "Jumlah balita yang ada, terdaftar, dan ditimbang bulan ini",
                    "Jumlah balita yang naik berat badannya (N)",
                    "Jumlah balita yang tidak naik satu kali (T) dan dua kali berturut-turut (2T)",
                    "Jumlah balita yang bulan sebelumnya tidak menimbang",
                    "Jumlah bayi baru pada bulan itu",
                    "Jumlah balita per kategori BB/U, TB/U, dan BB/TB",
                )
            ),
            Note(
                "Laporan hanya dapat diunduh oleh Kader dan Admin, dan Kader hanya memperoleh " +
                    "laporan untuk wilayahnya sendiri."
            ),
        ),
    ),

    AboutTopic(
        id = "validasi",
        group = ABOUT_GROUP_FEATURE,
        title = "Uji Validasi Z-Score",
        summary = "Menguji perhitungan aplikasi terhadap data uji dari luar",
        adminOnly = true,
        blocks = listOf(
            Paragraph(
                "Menu khusus Admin untuk memeriksa apakah perhitungan aplikasi sama dengan " +
                    "perhitungan acuan. Menu ini tidak pernah menyimpan data balita, jadi aman " +
                    "dijalankan berulang kali."
            ),
            Heading("Tab Hitung Satu Data"),
            Paragraph(
                "Masukkan jenis kelamin, umur dalam bulan, berat, dan tinggi badan. Aplikasi " +
                    "menampilkan z-score dan kategori untuk ketiga indikator, beserta tujuh titik SD " +
                    "WHO yang dipakai sebagai dasarnya. Perhitungannya memakai jalur kode yang sama " +
                    "persis dengan yang dipakai kader saat menyimpan penimbangan."
            ),
            Heading("Tab Uji Berkas"),
            Paragraph(
                "Unggah berkas Excel atau CSV berisi sampel uji, lalu jalankan seluruhnya sekaligus. " +
                    "Hasilnya dibandingkan dengan z-score rujukan pada berkas, dan ringkasan " +
                    "kecocokannya ditampilkan dalam persen."
            ),
            Table(
                headers = listOf("Kolom yang dicari", "Contoh judul yang dikenali"),
                rows = listOf(
                    listOf("Jenis kelamin", "Jenis Kelamin, JK, Gender"),
                    listOf("Umur", "Umur (bulan), Usia"),
                    listOf("Berat badan", "BB, Berat Badan (kg)"),
                    listOf("Tinggi badan", "TB, PB, Tinggi Badan (cm)"),
                    listOf("Indeks", "Indeks, Indikator"),
                    listOf("Z-score rujukan", "Z-Score Referensi, Z-Score Manual"),
                ),
            ),
            Note(
                "Judul kolom dicocokkan secara longgar, jadi variasi penulisan tetap terbaca. " +
                    "Kolom rujukan harus memuat kata referensi, rujukan, atau manual, dan tidak boleh " +
                    "memuat kata aplikasi, supaya kolom hasil aplikasi tidak salah dibaca sebagai " +
                    "kolom rujukan."
            ),
        ),
    ),

    AboutTopic(
        id = "umur",
        group = ABOUT_GROUP_METHOD,
        title = "Umur dalam Bulan Penuh",
        summary = "Aturan umur yang menentukan baris tabel WHO mana yang dipakai",
        blocks = listOf(
            Paragraph(
                "Umur balita dihitung dalam bulan penuh yang sudah terlampaui, bukan selisih bulan " +
                    "pada kalender. Aturan ini menentukan baris tabel WHO mana yang dipakai, sehingga " +
                    "harus dipahami lebih dulu sebelum membaca cara menghitung indikator."
            ),
            Heading("Contoh"),
            Table(
                headers = listOf("Tanggal lahir", "Tanggal timbang", "Umur"),
                rows = listOf(
                    listOf("28 Juli", "1 Agustus", "0 bulan"),
                    listOf("28 Juli", "27 Agustus", "0 bulan"),
                    listOf("28 Juli", "28 Agustus", "1 bulan"),
                    listOf("28 Juli", "27 September", "1 bulan"),
                ),
            ),
            Paragraph(
                "Balita baru berumur 1 bulan pada tanggal yang sama dengan tanggal lahirnya di bulan " +
                    "berikutnya. Cara ini mengikuti kaidah WHO. Implementasi lama memakai selisih " +
                    "bulan kalender, sehingga baris tabel yang dipakai bisa bergeser satu bulan dan " +
                    "hasil kategorinya ikut meleset."
            ),
            Note(
                "Tanggal lahir dan tanggal penimbangan diperlakukan sebagai tanggal kalender, bukan " +
                    "sebagai waktu. Zona waktu perangkat tidak pernah menggeser umur balita."
            ),
        ),
    ),

    AboutTopic(
        id = "zscore",
        group = ABOUT_GROUP_METHOD,
        title = "Rumus Z-Score",
        summary = "Cara aplikasi mengubah ukuran badan menjadi angka z",
        blocks = listOf(
            Paragraph(
                "Z-score menyatakan seberapa jauh ukuran seorang balita dari median balita sehat " +
                    "seumur dan sejenis kelaminnya, diukur dalam satuan simpangan baku. Nilai 0 " +
                    "berarti tepat di median, nilai -2 berarti dua simpangan baku di bawah median."
            ),
            Heading("Cara menghitungnya"),
            Paragraph(
                "Tabel standar antropometri memuat tujuh garis, yaitu -3, -2, -1, median, +1, +2, " +
                    "dan +3 SD. Aplikasi mencari di antara dua garis mana ukuran balita berada, lalu " +
                    "menghitung posisinya secara linear:"
            ),
            Formula(
                listOf(
                    "z = z1 + (X - V1) / (V2 - V1) x (z2 - z1)",
                    "",
                    "X  = ukuran balita",
                    "V1 = nilai garis SD di bawahnya, z1 = z garis itu",
                    "V2 = nilai garis SD di atasnya,  z2 = z garis itu",
                )
            ),
            Paragraph(
                "Bila ukuran balita berada di luar -3 SD atau +3 SD, tabel tidak lagi memuat " +
                    "garisnya. Aplikasi meneruskan perhitungan memakai lebar pita SD terluar:"
            ),
            Formula(
                listOf(
                    "di bawah -3 SD:  z = -3 - (V(-3) - X) / (V(-2) - V(-3))",
                    "di atas  +3 SD:  z = +3 + (X - V(+3)) / (V(+3) - V(+2))",
                )
            ),
            Paragraph("Hasilnya dibulatkan menjadi dua angka di belakang koma."),
            Heading("Mengapa bukan rumus LMS"),
            Paragraph(
                "WHO sendiri menghitung z-score dengan rumus LMS, yaitu " +
                    "Z = [(X/M)^L - 1] / (L x S), memakai koefisien L, M, dan S untuk setiap umur. " +
                    "Aplikasi ini memakai interpolasi tabel karena yang berlaku di Posyandu adalah " +
                    "tabel cetak pada Permenkes, bukan koefisien LMS. Dengan begitu hasil aplikasi " +
                    "sama dengan hasil kader yang menghitung manual memakai tabel yang sama."
            ),
            Heading("Seberapa berbeda hasilnya"),
            Table(
                headers = listOf("Rentang", "Selisih rata-rata", "Selisih terbesar"),
                rows = listOf(
                    listOf("Dalam -3 sampai +3 SD", "0,015 sampai 0,020 SD", "0,10 SD"),
                    listOf("Di luar -3 sampai +3 SD", "0,03 sampai 0,07 SD", "0,29 SD"),
                ),
            ),
            Note(
                "Angka di atas diperoleh dengan membandingkan hasil aplikasi terhadap rumus LMS pada " +
                    "244.061 titik uji untuk setiap indikator. Untuk kasus yang sangat ekstrem, di " +
                    "luar -3 atau +3 SD, angka z yang ditampilkan adalah perkiraan yang baik, bukan " +
                    "nilai LMS yang tepat."
            ),
        ),
    ),

    AboutTopic(
        id = "bbu",
        group = ABOUT_GROUP_METHOD,
        title = "BB/U, Berat Badan menurut Umur",
        summary = "Menilai berat badan terhadap umur, untuk mendeteksi berat badan kurang",
        blocks = listOf(
            Paragraph(
                "Indeks BB/U membandingkan berat badan balita dengan berat badan balita sehat yang " +
                    "seumur dan sejenis kelamin. Indeks ini menggambarkan keadaan gizi secara umum, " +
                    "tetapi tidak dapat membedakan balita yang kurus dari balita yang pendek."
            ),
            Table(
                headers = listOf("Kategori", "Ambang z-score"),
                rows = listOf(
                    listOf("Berat badan sangat kurang", "kurang dari -3 SD"),
                    listOf("Berat badan kurang", "-3 SD sampai kurang dari -2 SD"),
                    listOf("Berat badan normal", "-2 SD sampai +1 SD"),
                    listOf("Risiko berat badan lebih", "lebih dari +1 SD"),
                ),
            ),
            Paragraph(
                "Tabel rujukannya memuat umur 0 sampai 60 bulan, terpisah untuk laki-laki dan " +
                    "perempuan."
            ),
            Note(
                "Balita berkategori risiko berat badan lebih belum tentu bermasalah. Permenkes " +
                    "meminta hasil itu dikonfirmasi dengan indeks BB/TB, karena BB/U tidak dapat " +
                    "membedakan berat badan lebih karena kelebihan lemak dari berat badan lebih " +
                    "karena tubuh yang memang tinggi."
            ),
        ),
    ),

    AboutTopic(
        id = "tbu",
        group = ABOUT_GROUP_METHOD,
        title = "TB/U, Tinggi Badan menurut Umur",
        summary = "Menilai tinggi badan terhadap umur, indeks untuk mendeteksi stunting",
        blocks = listOf(
            Paragraph(
                "Indeks PB/U atau TB/U membandingkan panjang atau tinggi badan balita dengan balita " +
                    "sehat seumurnya. Inilah indeks yang dipakai untuk menentukan stunting, karena " +
                    "tinggi badan mencerminkan keadaan gizi jangka panjang, bukan keadaan sesaat."
            ),
            Table(
                headers = listOf("Kategori", "Ambang z-score"),
                rows = listOf(
                    listOf("Sangat pendek (severely stunted)", "kurang dari -3 SD"),
                    listOf("Pendek (stunted)", "-3 SD sampai kurang dari -2 SD"),
                    listOf("Normal", "-2 SD sampai +3 SD"),
                    listOf("Tinggi", "lebih dari +3 SD"),
                ),
            ),
            Heading("Panjang badan atau tinggi badan"),
            Paragraph(
                "Balita berumur sampai 24 bulan diukur telentang, dan angkanya disebut panjang " +
                    "badan. Balita berumur lebih dari 24 bulan diukur berdiri, dan angkanya disebut " +
                    "tinggi badan. Kedua cara itu punya tabel rujukan sendiri, karena tubuh terukur " +
                    "sekitar 0,7 cm lebih panjang saat telentang."
            ),
            Note(
                "Aplikasi memakai tabel telentang untuk umur sampai dengan 24 bulan, lalu beralih ke " +
                    "tabel berdiri mulai umur 25 bulan. Balita berumur tepat 24 bulan yang diukur " +
                    "berdiri karena itu akan tampak sedikit lebih pendek daripada semestinya."
            ),
        ),
    ),

    AboutTopic(
        id = "bbtb",
        group = ABOUT_GROUP_METHOD,
        title = "BB/TB, Berat Badan menurut Tinggi Badan",
        summary = "Menilai proporsi tubuh, indeks untuk gizi buruk dan gizi lebih",
        blocks = listOf(
            Paragraph(
                "Indeks BB/PB atau BB/TB membandingkan berat badan balita dengan berat badan balita " +
                    "sehat yang tingginya sama, tanpa memandang umur. Indeks inilah yang dipakai " +
                    "untuk menentukan gizi buruk dan gizi lebih, karena ia menilai proporsi tubuh."
            ),
            Table(
                headers = listOf("Kategori", "Ambang z-score"),
                rows = listOf(
                    listOf("Gizi buruk (severely wasted)", "kurang dari -3 SD"),
                    listOf("Gizi kurang (wasted)", "-3 SD sampai kurang dari -2 SD"),
                    listOf("Gizi baik (normal)", "-2 SD sampai +1 SD"),
                    listOf("Berisiko gizi lebih", "lebih dari +1 SD sampai +2 SD"),
                    listOf("Gizi lebih (overweight)", "lebih dari +2 SD sampai +3 SD"),
                    listOf("Obesitas (obese)", "lebih dari +3 SD"),
                ),
            ),
            Heading("Tabel yang dipakai"),
            Table(
                headers = listOf("Umur", "Tabel", "Rentang"),
                rows = listOf(
                    listOf("0 sampai 24 bulan", "BB/PB, diukur telentang", "45,0 sampai 110,0 cm"),
                    listOf("25 sampai 60 bulan", "BB/TB, diukur berdiri", "65,0 sampai 120,0 cm"),
                ),
            ),
            Paragraph(
                "Tinggi badan dibulatkan ke kelipatan 0,5 cm terdekat sebelum dicari di tabel, " +
                    "karena tabel rujukan memang disusun per 0,5 cm."
            ),
            Note(
                "Bila tinggi badan berada di luar rentang tabel, misalnya balita berumur 30 bulan " +
                    "yang tingginya belum 65 cm, statusnya menjadi Tidak Diketahui. Ini bukan galat, " +
                    "melainkan batas yang memang ada pada tabel resminya."
            ),
        ),
    ),

    AboutTopic(
        id = "lku",
        group = ABOUT_GROUP_METHOD,
        title = "LK/U, Lingkar Kepala menurut Umur",
        summary = "Menilai lingkar kepala, penanda perkembangan otak",
        blocks = listOf(
            Paragraph(
                "Lingkar kepala dibandingkan dengan lingkar kepala balita sehat seumurnya. " +
                    "Ukuran ini terkait dengan pertumbuhan otak, sehingga penyimpangan yang besar " +
                    "perlu diperiksa lebih lanjut oleh tenaga kesehatan."
            ),
            Table(
                headers = listOf("Kategori", "Ambang z-score"),
                rows = listOf(
                    listOf("Mikrosefali", "kurang dari -2 SD"),
                    listOf("Normal", "-2 SD sampai +2 SD"),
                    listOf("Makrosefali", "lebih dari +2 SD"),
                ),
            ),
            Note(
                "Indeks ini tidak termasuk empat indeks Standar Antropometri Anak pada Permenkes " +
                    "2/2020. Tabel rujukannya diambil langsung dari WHO Child Growth Standards, " +
                    "head circumference-for-age, dan sudah dicocokkan baris per baris dengan " +
                    "terbitan WHO."
            ),
        ),
    ),

    AboutTopic(
        id = "lila",
        group = ABOUT_GROUP_METHOD,
        title = "LiLA, Lingkar Lengan Atas",
        summary = "Penapisan cepat gizi buruk akut tanpa perlu menimbang",
        blocks = listOf(
            Paragraph(
                "Lingkar lengan atas dipakai sebagai penapisan cepat gizi buruk akut. Berbeda dari " +
                    "indikator lain, LiLA tidak memakai z-score maupun tabel: angkanya dibandingkan " +
                    "langsung dengan ambang tetap."
            ),
            Table(
                headers = listOf("Kategori", "Ambang"),
                rows = listOf(
                    listOf("Gizi buruk akut", "kurang dari 11,5 cm"),
                    listOf("Gizi kurang akut", "11,5 cm sampai kurang dari 12,5 cm"),
                    listOf("Normal", "12,5 cm atau lebih"),
                ),
            ),
            Paragraph(
                "Ambang ini hanya berlaku untuk balita berumur 6 sampai 59 bulan. Di luar rentang " +
                    "umur itu LiLA tetap dicatat tetapi statusnya Tidak Berlaku, dan itu dibedakan " +
                    "dari status Normal supaya tidak keliru dibaca sebagai hasil pemeriksaan."
            ),
        ),
    ),

    AboutTopic(
        id = "kbm",
        group = ABOUT_GROUP_METHOD,
        title = "KBM dan Status Naik atau Tidak Naik",
        summary = "Tabel kenaikan berat badan minimum, status N dan T, serta 2T",
        blocks = listOf(
            Paragraph(
                "Selain status gizi, aplikasi menilai apakah berat badan balita naik cukup " +
                    "dibandingkan bulan lalu. Kenaikan itu dibandingkan dengan Kenaikan Berat Badan " +
                    "Minimum (KBM) menurut umurnya."
            ),
            Table(
                headers = listOf("Umur", "KBM"),
                rows = listOf(
                    listOf("1 bulan", "800 gram"),
                    listOf("2 bulan", "900 gram"),
                    listOf("3 bulan", "800 gram"),
                    listOf("4 bulan", "600 gram"),
                    listOf("5 bulan", "500 gram"),
                    listOf("6 bulan", "400 gram"),
                    listOf("7 sampai 10 bulan", "300 gram"),
                    listOf("11 sampai 60 bulan", "200 gram"),
                ),
            ),
            Heading("Empat kemungkinan status"),
            Table(
                headers = listOf("Kode", "Artinya"),
                rows = listOf(
                    listOf("N", "Naik. Kenaikan berat badan mencapai KBM atau lebih."),
                    listOf("T", "Tidak naik. Kenaikan kurang dari KBM, berat tetap, atau turun."),
                    listOf("O", "Bulan lalu tidak ditimbang, sehingga kenaikan tidak dapat dinilai."),
                    listOf("B", "Penimbangan pertama balita ini."),
                ),
            ),
            Heading("2T dan rujukan"),
            Paragraph(
                "Bila status T terjadi dua kali berturut-turut, balita ditandai 2T dan perlu " +
                    "dirujuk ke tenaga kesehatan. Penghitungnya kembali ke nol begitu berat badan " +
                    "naik mencapai KBM. Status O tidak memutus dan tidak menambah hitungan 2T, " +
                    "karena bulan yang terlewat bukan bukti berat badan tidak naik."
            ),
            Note(
                "Petunjuk teknis KMS Kemenkes mengutamakan arah kurva pertumbuhan untuk menentukan " +
                    "naik atau tidak naik, dan menempatkan KBM sebagai pembanding bila arah kurva " +
                    "diragukan. Aplikasi memakai KBM sebagai satu-satunya penentu, karena arah kurva " +
                    "tidak dapat dinilai otomatis tanpa keraguan."
            ),
        ),
    ),

    AboutTopic(
        id = "penandaan",
        group = ABOUT_GROUP_METHOD,
        title = "Penandaan Data Meragukan",
        summary = "Bagaimana aplikasi memperlakukan angka yang tampak tidak wajar",
        blocks = listOf(
            Paragraph(
                "Kesalahan ketik saat mencatat penimbangan tidak dapat dihindari sepenuhnya. " +
                    "Aplikasi tidak menolak angka yang tampak janggal, melainkan menyimpannya " +
                    "sambil memberi tanda supaya diperiksa ulang."
            ),
            Paragraph(
                "Penimbangan dengan nilai z-score lebih besar dari 6 atau lebih kecil dari -6 pada " +
                    "salah satu indikator ditandai untuk diverifikasi. Cara ini mengikuti praktik " +
                    "perangkat lunak WHO Anthro."
            ),
            Heading("Rentang kewajaran pengukuran"),
            Table(
                headers = listOf("Ukuran", "Rentang wajar"),
                rows = listOf(
                    listOf("Berat badan", "0,5 sampai 40 kg"),
                    listOf("Tinggi atau panjang badan", "30 sampai 140 cm"),
                    listOf("Lingkar kepala", "25 sampai 65 cm"),
                    listOf("Lingkar lengan atas", "5 sampai 30 cm"),
                ),
            ),
            Note(
                "Menolak nilai ekstrem justru berisiko membuang kasus gizi buruk yang paling perlu " +
                    "tercatat. Karena itu data tetap disimpan, dan keputusan akhir diserahkan kepada " +
                    "kader yang memeriksanya."
            ),
        ),
    ),

    AboutTopic(
        id = "rantai",
        group = ABOUT_GROUP_METHOD,
        title = "Rantai Penimbangan",
        summary = "Mengapa mengubah data lama ikut mengubah bulan-bulan sesudahnya",
        blocks = listOf(
            Paragraph(
                "Status N, T, dan 2T tidak berdiri sendiri. Ketiganya bergantung pada penimbangan " +
                    "bulan sebelumnya, sehingga seluruh riwayat balita membentuk satu rantai."
            ),
            Paragraph(
                "Karena itu, setiap kali penimbangan ditambahkan, disunting, atau dihapus, aplikasi " +
                    "menghitung ulang seluruh baris sejak tanggal itu sampai penimbangan terbaru. " +
                    "Tanpa itu, memasukkan data mundur akan meninggalkan status N dan T yang salah " +
                    "pada baris-baris sesudahnya."
            ),
            Heading("Penghapusan bersifat lunak"),
            Paragraph(
                "Penimbangan yang dihapus tidak benar-benar dibuang dari basis data, melainkan " +
                    "ditandai terhapus. Slot bulannya dilepas supaya bulan itu bisa diisi ulang, " +
                    "sementara datanya tetap tersimpan untuk keperluan penelusuran."
            ),
        ),
    ),

    AboutTopic(
        id = "rujukan",
        group = ABOUT_GROUP_SOURCE,
        title = "Daftar Rujukan",
        summary = "Peraturan dan terbitan yang menjadi dasar setiap angka",
        blocks = listOf(
            Paragraph(
                "Setiap angka rujukan pada aplikasi ini berasal dari salah satu terbitan berikut. " +
                    "Tidak ada angka yang ditulis berdasarkan perkiraan."
            ),
            Reference(
                title = "Peraturan Menteri Kesehatan Republik Indonesia Nomor 2 Tahun 2020 " +
                    "tentang Standar Antropometri Anak",
                usedFor = "Tabel BB/U, PB/U, TB/U, BB/PB, dan BB/TB untuk umur 0 sampai 60 bulan, " +
                    "serta seluruh kategori dan ambang batas status gizi.",
            ),
            Reference(
                title = "WHO Child Growth Standards, head circumference-for-age, birth to 5 years " +
                    "(z-scores)",
                usedFor = "Tabel rujukan lingkar kepala menurut umur, yang tidak termasuk dalam " +
                    "Permenkes 2/2020.",
            ),
            Reference(
                title = "Kementerian Kesehatan Republik Indonesia, Petunjuk Teknis Penggunaan " +
                    "Kartu Menuju Sehat (KMS) Balita",
                usedFor = "Tabel Kenaikan Berat Badan Minimum, definisi status N, T, O, dan B, " +
                    "serta penanganan lanjutan untuk balita 2T.",
            ),
            Reference(
                title = "Ambang lingkar lengan atas WHO untuk gizi buruk dan gizi kurang akut " +
                    "pada anak umur 6 sampai 59 bulan",
                usedFor = "Ambang 11,5 cm dan 12,5 cm beserta batas umur penerapannya.",
            ),
            Heading("Hasil pencocokan"),
            Paragraph(
                "Seluruh tabel rujukan di dalam aplikasi sudah dicocokkan baris per baris dengan " +
                    "terbitan aslinya, bukan diperiksa sebagian."
            ),
            Table(
                headers = listOf("Tabel", "Jumlah baris", "Selisih"),
                rows = listOf(
                    listOf("BB/U, PB/U, TB/U, BB/PB, BB/TB", "730", "0"),
                    listOf("LK/U", "122", "0"),
                ),
            ),
            Note(
                "Satu-satunya baris yang perlu dijelaskan adalah umur 24 bulan pada indeks TB/U. " +
                    "Permenkes memuat umur itu pada dua tabel sekaligus, telentang dan berdiri, dan " +
                    "aplikasi memilih tabel telentang. Nilai yang dipakai tetap nilai Permenkes, " +
                    "hanya saja diambil dari tabel yang satunya."
            ),
        ),
    ),

    AboutTopic(
        id = "glosarium",
        group = ABOUT_GROUP_SOURCE,
        title = "Glosarium",
        summary = "Arti istilah yang muncul di layar",
        blocks = listOf(
            Table(
                headers = listOf("Istilah", "Arti"),
                rows = listOf(
                    listOf("Z-score", "Jarak ukuran balita dari median, dalam satuan simpangan baku"),
                    listOf("SD", "Simpangan baku, satuan yang dipakai pada z-score"),
                    listOf("Median", "Nilai tengah balita sehat seumur dan sejenis kelamin"),
                    listOf("BB/U", "Berat badan menurut umur"),
                    listOf("TB/U", "Tinggi badan menurut umur"),
                    listOf("PB/U", "Panjang badan menurut umur, untuk balita yang diukur telentang"),
                    listOf("BB/TB", "Berat badan menurut tinggi badan"),
                    listOf("LK/U", "Lingkar kepala menurut umur"),
                    listOf("LiLA", "Lingkar lengan atas"),
                    listOf("Stunting", "Pendek menurut umur, akibat kekurangan gizi jangka panjang"),
                    listOf("Wasting", "Kurus menurut tinggi badan, akibat kekurangan gizi akut"),
                    listOf("Underweight", "Berat badan kurang menurut umur"),
                    listOf("Mikrosefali", "Lingkar kepala jauh lebih kecil dari seharusnya"),
                    listOf("Makrosefali", "Lingkar kepala jauh lebih besar dari seharusnya"),
                    listOf("KBM", "Kenaikan Berat Badan Minimum"),
                    listOf("N, T, O, B", "Naik, Tidak naik, tidak ditimbang bulan lalu, penimbangan pertama"),
                    listOf("2T", "Tidak naik dua kali berturut-turut"),
                    listOf("KMS", "Kartu Menuju Sehat"),
                    listOf("UPGK", "Usaha Perbaikan Gizi Keluarga"),
                    listOf("MPASI", "Makanan Pendamping ASI"),
                ),
            ),
        ),
    ),

    AboutTopic(
        id = "penafian",
        group = ABOUT_GROUP_LIMIT,
        title = "Penafian",
        summary = "Batas kewenangan hasil yang ditampilkan aplikasi",
        blocks = listOf(
            Paragraph(
                "Hasil yang ditampilkan aplikasi ini adalah penapisan, bukan diagnosis. Aplikasi " +
                    "membantu kader dan orang tua mengenali balita yang perlu perhatian lebih, " +
                    "tetapi tidak menentukan penyakit maupun penyebabnya."
            ),
            Paragraph(
                "Balita yang berkategori sangat kurang, sangat pendek, gizi buruk, obesitas, " +
                    "mikrosefali, makrosefali, atau tercatat 2T harus dibawa ke tenaga kesehatan " +
                    "untuk pemeriksaan lebih lanjut. Jangan menerapkan pembatasan makanan pada " +
                    "balita tanpa pengawasan tenaga kesehatan."
            ),
            Note(
                "Ketepatan hasil bergantung sepenuhnya pada ketepatan pengukuran. Timbangan yang " +
                    "belum ditera, pengukuran tinggi yang tidak tegak, atau tanggal lahir yang salah " +
                    "akan menghasilkan kategori yang salah pula, sekalipun perhitungannya benar."
            ),
        ),
    ),

    AboutTopic(
        id = "batasan",
        group = ABOUT_GROUP_LIMIT,
        title = "Keterbatasan yang Diketahui",
        summary = "Hal-hal yang sengaja disederhanakan, beserta akibatnya",
        blocks = listOf(
            Paragraph(
                "Empat penyederhanaan berikut diambil dengan sadar. Semuanya dicatat di sini supaya " +
                    "dapat dipertimbangkan saat membaca hasil."
            ),
            Heading("Interpolasi tabel, bukan rumus LMS"),
            Paragraph(
                "Z-score dihitung dengan menginterpolasi tabel cetak, bukan dengan rumus LMS milik " +
                    "WHO. Selisihnya rata-rata 0,015 sampai 0,020 SD di dalam rentang -3 sampai " +
                    "+3 SD, dan membesar menjadi rata-rata 0,03 sampai 0,07 SD di luar rentang itu."
            ),
            Heading("Umur 24 bulan memakai tabel telentang"),
            Paragraph(
                "Aplikasi hanya menyimpan satu kolom tinggi tanpa penanda cara pengukuran, sehingga " +
                    "harus memilih salah satu tabel untuk umur 24 bulan. Balita 24 bulan yang diukur " +
                    "berdiri akan tampak sekitar 0,7 cm lebih pendek daripada semestinya."
            ),
            Heading("KBM sebagai penentu tunggal"),
            Paragraph(
                "Status naik atau tidak naik ditentukan hanya dari perbandingan terhadap KBM, " +
                    "sedangkan petunjuk teknis KMS mengutamakan arah kurva pertumbuhan."
            ),
            Heading("Grafik WHO berupa gambar"),
            Paragraph(
                "Kurva rujukan pada Grafik WHO adalah berkas gambar, bukan kurva yang digambar dari " +
                    "data. Titik balita digambar di atasnya. Angka z-score tidak terpengaruh oleh " +
                    "hal ini, karena angka itu selalu dihitung dari tabel, bukan dibaca dari gambar."
            ),
        ),
    ),

    AboutTopic(
        id = "privasi",
        group = ABOUT_GROUP_LIMIT,
        title = "Privasi Data",
        summary = "Data apa yang disimpan dan siapa yang dapat melihatnya",
        blocks = listOf(
            Heading("Yang disimpan"),
            Bullets(
                listOf(
                    "Identitas balita: nama, tanggal dan tempat lahir, jenis kelamin, anak ke berapa",
                    "Ukuran saat lahir dan seluruh riwayat penimbangan",
                    "Identitas orang tua: nama, alamat, nomor telepon, dan wilayah RW",
                    "Riwayat imunisasi",
                )
            ),
            Heading("Siapa yang dapat melihat"),
            Paragraph(
                "Orang tua hanya dapat melihat data balitanya sendiri. Kader hanya dapat melihat " +
                    "data balita di wilayah RW tempatnya bertugas. Admin dapat melihat seluruh " +
                    "wilayah. Pembatasan ini dijalankan di sisi server."
            ),
            Heading("Jejak perubahan"),
            Paragraph(
                "Setiap penambahan, penyuntingan, dan penghapusan data dicatat beserta siapa yang " +
                    "melakukannya, sehingga perubahan yang keliru dapat ditelusuri kembali."
            ),
            Note(
                "Data balita adalah data pribadi anak. Jangan membagikan tangkapan layar yang " +
                    "memuat nama, alamat, atau nomor telepon ke luar lingkungan Posyandu."
            ),
        ),
    ),

    AboutTopic(
        id = "pengembang",
        group = ABOUT_GROUP_LIMIT,
        title = "Pengembang",
        summary = "Siapa yang membuat aplikasi ini dan dalam rangka apa",
        blocks = listOf(
            Paragraph(
                "NutriGrow disusun sebagai bahan penelitian skripsi, bekerja sama dengan Posyandu " +
                    "Desa Jipang, Kecamatan Karanglewas, Kabupaten Banyumas."
            ),
            Paragraph(
                "Aplikasi terdiri atas dua bagian: aplikasi Android yang dipakai kader dan orang " +
                    "tua, serta layanan REST API yang menyimpan data dan menjalankan seluruh " +
                    "perhitungan gizi. Seluruh perhitungan dilakukan di sisi server, sehingga hasil " +
                    "yang dilihat semua pengguna dipastikan sama."
            ),
            Note(
                "Saran perbaikan, terutama koreksi terhadap angka rujukan atau ambang batas, sangat " +
                    "diharapkan dan dapat disampaikan melalui pengurus Posyandu."
            ),
        ),
    ),
)
