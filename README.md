# Lokki - Local Password Manager

Lokki adalah aplikasi password manager berbasis desktop yang berjalan sepenuhnya lokal. Semua data disimpan di mesin pengguna dan tidak ada panggilan jaringan sama sekali. Aplikasi ini dikembangkan menggunakan Java Swing dengan arsitektur MVC dan MySQL sebagai basis data.

---

## Fitur

- Enkripsi envelope: vault key 256-bit melindungi semua credential
- Setup master password dengan indikator kekuatan password real-time
- Login dengan percobaan terbatas (3x) dan cooldown 5 detik
- Recovery key 24 karakter untuk pemulihan akses
- CRUD credential (site name, URL, username, password, kategori, catatan)
- Pencarian dan filter credential berdasarkan kategori
- Password generator dengan opsi karakter (upper, lower, digit, symbol)
- Copy username/password ke clipboard dengan auto-clear 30 detik
- Auto-lock setelah 5 menit tidak ada aktivitas
- Password strength bar dengan 4 level (Weak/Medium/Strong/Very Strong)
- Tampilkan/sembunyikan password pada field input

---

## Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Bahasa | Java 17 |
| GUI | Java Swing (Nimbus Look and Feel) |
| Database | MySQL 8.x |
| Konektor DB | mysql-connector-j 8.3.0 |
| Build Tool | Maven 3.8+ |
| Kriptografi | javax.crypto (standar library Java, tanpa pustaka pihak ketiga) |

---

## Prasyarat

1. Java 17 atau lebih tinggi
2. MySQL 8.x berjalan secara lokal
3. Maven 3.8+ (atau gunakan `mvnw.cmd` yang sudah disediakan)

---

## Cara Install dan Menjalankan

### 1. Pastikan MySQL berjalan

Aplikasi akan otomatis membuat database `lokki_db` dan seluruh tabel saat pertama kali dijalankan. Tidak perlu menjalankan file SQL secara manual.

### 2. Sesuaikan koneksi database (jika perlu)

Buka file `src/main/java/com/lokki/dao/DatabaseManager.java`. Ubah nilai konstanta berikut sesuai konfigurasi MySQL anda:

```java
private static final String USER = "root";
private static final String PASSWORD = "";
```

Default menggunakan user root tanpa password. Jika MySQL anda menggunakan user/password berbeda, ubah di sini.

### 3. Jalankan aplikasi

```bash
mvnw compile
mvnw exec:java
```

Atau jika Maven sudah terinstall secara global:

```bash
mvn compile
mvn exec:java
```

### 4. Setup awal

Saat pertama kali dijalankan, aplikasi akan mendeteksi bahwa vault belum di-setup dan menampilkan halaman setup. Anda akan diminta membuat master password, kemudian aplikasi akan menampilkan recovery key satu kali. Simpan recovery key tersebut di tempat aman.

---

## Standalone Release (Tanpa MySQL / Java)

Tidak ingin repot install MySQL, Java, atau Maven? Gunakan **Lokki_app** - SQLite + Java runtime sudah dibundle. Cukup download, extract, dan jalankan.

### Perbedaan dengan versi utama

| Aspek | Versi Utama | Standalone (Lokki_app) |
|-------|-------------|------------------------|
| Database | MySQL 8.x | SQLite (file `lokki.db`) |
| Runtime | Perlu Java 17+ | Sudah termasuk Java runtime |
| Build | Perlu Maven | Langsung pakai `.exe` / `.bat` |
| File DB | Di MySQL server | `lokki.db` di folder aplikasi |

### Cara pakai

1. Download `Lokki_app.zip` dari [halaman Releases](https://github.com/indraprhmbd/Lokki/releases/)
2. Extract ke folder mana saja
3. Jalankan `Lokki_app.exe` (atau `Lokki_app.bat` jika SmartScreen muncul)
4. Database `lokki.db` dibuat otomatis saat setup pertama

Kedua versi menggunakan format enkripsi dan skema vault yang identik. Data dapat dipindahkan antar versi dengan migrasi manual tabel `master_config` dan `credentials`.

---

```
lokki/
  pom.xml
  mvnw.cmd
  src/main/java/com/lokki/
    model/           -- POJO classes (Credential, MasterConfig, Category)
    dao/             -- Akses database (DatabaseManager, MasterConfigDAO, CredentialDAO, CategoryDAO)
    service/         -- Logika bisnis (Auth, Vault, Encryption, KeyDerivation, PasswordGenerator, RecoveryKey)
    controller/      -- Penghubung view dan service (AuthController, VaultController)
    view/            -- GUI (LoginFrame, SetupFrame, MainFrame, RecoveryFrame, AddEditCredentialDialog, PasswordGeneratorDialog)
    view/component/  -- Komponen GUI kustom (CredentialTableModel, PasswordStrengthBar, ClipboardTimer)
    util/            -- Utility (SecureMemoryUtil, RecoveryKeyFormatter)
  src/main/resources/
    db/schema.sql    -- Referensi skema database
```

---

## Arsitektur MVC 4 Layer

Aplikasi menggunakan arsitektur MVC dengan 4 layer yang ketat:

```
View (Swing frames dan dialog)
  |
Controller (menghubungkan input ke service, memperbarui view)
  |
Service (logika bisnis, enkripsi, manajemen key)
  |
DAO (query SQL saja, tidak ada logika)
  |
MySQL (database lokal)
```

Aturan:
- View tidak boleh mengakses DAO secara langsung
- DAO tidak boleh mengakses Service
- Controller adalah satu-satunya kelas yang mengimpor dari package View dan Service

---

## Model Keamanan

### Envelope Encryption

Lokki menggunakan skema enkripsi envelope:

1. **Vault Key** (256-bit) dibuat secara acak saat setup menggunakan SecureRandom
2. Vault Key digunakan untuk mengenkripsi semua password credential (AES-256-GCM)
3. Vault Key sendiri disimpan dalam database dalam dua bentuk terenkripsi:
   - Dienkripsi oleh key turunan dari master password
   - Dienkripsi oleh key turunan dari recovery key
4. Vault Key hanya berada di memori selama sesi aktif

### Key Derivation

- Algoritma: PBKDF2WithHmacSHA256
- Iterasi: 310.000 (rekomendasi OWASP 2023)
- Panjang key: 256 bit
- Panjang salt: 128 bit (16 byte), dibuat dengan SecureRandom

### Enkripsi Credential

- Algoritma: AES-256-GCM
- IV: 12 byte, dibuat baru setiap enkripsi
- Format penyimpanan: base64(IV + ciphertext + GCM_tag)
- GCM memberikan kerahasiaan dan integritas data

### Recovery Key

- 24 karakter acak dari charset A-Z dan 2-9 (tanpa 0, O, 1, I, L)
- Format tampilan: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
- Ditampilkan SATU KALI saat setup
- Tidak pernah disimpan oleh aplikasi

### Keamanan Memori

- Semua input password menggunakan char[], bukan String
- char[] dihapus (diisi null) segera setelah tidak diperlukan
- Dilakukan di dalam blok finally untuk menjamin eksekusi
- Vault key byte[] dihapus saat aplikasi ditutup atau auto-lock

---

## Skema Database

### Table: master_config

Menyimpan satu baris konfigurasi vault.

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| id | INT (PK) | Auto increment |
| password_hash | VARCHAR(512) | Hash PBKDF2 untuk verifikasi login |
| salt_master | VARCHAR(128) | Salt untuk derivasi key dari master password |
| salt_recovery | VARCHAR(128) | Salt untuk derivasi key dari recovery key |
| encrypted_vault_key_by_master | TEXT | Vault key dienkripsi dengan key turunan master |
| encrypted_vault_key_by_recovery | TEXT | Vault key dienkripsi dengan key turunan recovery |

### Table: categories

Kategori untuk mengorganisir credential. Default: General, Social, Work, Finance, Developer.

### Table: credentials

Menyimpan data login. Hanya kolom `encrypted_password` yang dienkripsi.

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| site_name | VARCHAR(255) | Nama situs (plaintext) |
| site_url | VARCHAR(500) | URL situs (plaintext) |
| username | VARCHAR(255) | Username (plaintext) |
| encrypted_password | TEXT | Password dienkripsi AES-256-GCM (base64) |
| category_id | INT | Foreign key ke categories |
| notes | TEXT | Catatan (plaintext, beri label peringatan di UI) |

---

## Alur Aplikasi

### Setup

1. User memasukkan master password di SetupFrame
2. AuthService membuat vault key 32 byte acak
3. Key turunan dibuat dari master password menggunakan PBKDF2
4. Recovery key 24 karakter dibuat
5. Key turunan dibuat dari recovery key menggunakan PBKDF2
6. Vault key dienkripsi dengan kedua key turunan
7. Semua data disimpan ke tabel master_config
8. Recovery key ditampilkan ke user (hanya sekali)

### Login

1. User memasukkan master password di LoginFrame
2. AuthService membaca konfigurasi dari master_config
3. Key diturunkan dari input password menggunakan salt yang tersimpan
4. Hash password diverifikasi
5. Jika cocok, vault key didekripsi
6. Vault key digunakan untuk sesi aktif

### Recovery

1. User memasukkan recovery key dan master password baru
2. AuthService membaca konfigurasi dari master_config
3. Key diturunkan dari recovery key
4. Vault key didekripsi menggunakan recovery derived key
5. Master password baru diproses untuk menghasilkan key turunan baru
6. Vault key dienkripsi ulang dengan key turunan yang baru
7. Data master_config diperbarui

---

## Daftar Class per Package

### Model
- `Credential.java` - POJO untuk data login
- `MasterConfig.java` - POJO untuk konfigurasi vault
- `Category.java` - POJO untuk kategori

### DAO
- `DatabaseManager.java` - Mengelola koneksi JDBC dan inisialisasi database
- `MasterConfigDAO.java` - CRUD tabel master_config
- `CredentialDAO.java` - CRUD tabel credentials
- `CategoryDAO.java` - Query tabel categories

### Service
- `AuthService.java` - Setup vault, login, logout, recovery
- `VaultService.java` - CRUD credential dengan enkripsi/dekripsi
- `EncryptionService.java` - Enkripsi/dekripsi AES-256-GCM (static methods)
- `KeyDerivationService.java` - Derivasi key PBKDF2 (static methods)
- `PasswordGeneratorService.java` - Generator password acak
- `RecoveryKeyService.java` - Generator dan formatter recovery key

### Controller
- `AuthController.java` - Mengelola alur autentikasi (setup/login/recovery)
- `VaultController.java` - Mengelola tampilan vault utama

### View
- `LoginFrame.java` - Halaman login (entry point aplikasi)
- `SetupFrame.java` - Halaman setup awal
- `RecoveryFrame.java` - Halaman pemulihan akses
- `MainFrame.java` - Tampilan utama vault
- `AddEditCredentialDialog.java` - Dialog tambah/edit credential
- `PasswordGeneratorDialog.java` - Dialog generator password

### Component
- `CredentialTableModel.java` - Model tabel untuk JTable credential
- `PasswordStrengthBar.java` - Komponen visual indikator kekuatan password
- `ClipboardTimer.java` - Timer countdown penghapusan clipboard

### Utility
- `SecureMemoryUtil.java` - Utility penghapusan char[] dan byte[]
- `RecoveryKeyFormatter.java` - Formatter recovery key (format XXXX-XXXX-XXXX-XXXX-XXXX-XXXX)

---

## Catatan Penting

- Hanya kolom password yang dienkripsi. Nama situs, URL, username, dan catatan disimpan sebagai plaintext.
- Catatan (notes) menampilkan peringatan "disimpan sebagai plaintext" di UI.
- Aplikasi tidak membuat koneksi internet sama sekali.
- Tidak ada dependensi kriptografi pihak ketiga. Semua menggunakan javax.crypto dan java.security.
