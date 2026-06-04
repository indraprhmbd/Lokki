# Lokki - Belajar OOP dari Codebase

Dokumen ini memetakan konsep Pemrograman Berorientasi Objek (PBO) yang diajarkan di kelas ke dalam kode nyata pada proyek Lokki. Setiap konsep dilengkapi referensi file dan baris agar kamu bisa langsung membuka dan mempelajarinya.

---

## Daftar Isi

1. [Class dan Object](#1-class-dan-object)
2. [Constructor](#2-constructor)
3. [Encapsulation (Getter & Setter)](#3-encapsulation-getter--setter)
4. [Inheritance (extends)](#4-inheritance-extends)
5. [Polymorphism - Method Overriding](#5-polymorphism--method-overriding)
6. [Polymorphism - Interface](#6-polymorphism--interface)
7. [Abstract Class](#7-abstract-class)
8. [Static vs Instance](#8-static-vs-instance)
9. [`final` Keyword](#9-final-keyword)
10. [Package dan Import](#10-package-dan-import)
11. [Exception Handling](#11-exception-handling)
12. [Collections Framework](#12-collections-framework)
13. [Generics](#13-generics)
14. [Anonymous Class](#14-anonymous-class)
15. [Enum](#15-enum)
16. [Inner Interface](#16-inner-interface)
17. [Try-With-Resources](#17-try-with-resources)
18. [MVC Architecture](#18-mvc-architecture)
19. [Design Patterns di Lokki](#19-design-patterns-di-lokki)

---

## 1. Class dan Object

**Materi:** Class adalah cetak biru (blueprint), Object adalah instance nyata dari class.

### Contoh: Class `Credential`

```java
// File: src/main/java/com/lokki/model/Credential.java
public class Credential {
    private int id;
    private String siteName;
    private String siteUrl;
    private String username;
    private String encryptedPassword;
    // ...
}
```

**Cara membuat object (di VaultService):**

```java
Credential credential = new Credential();
credential.setSiteName("GitHub");
credential.setUsername("user@example.com");
```

Atau dengan constructor:

```java
Credential credential = new Credential("GitHub", "https://github.com",
    "user@example.com", "encrypted...", 1, "notes");
```

**Penjelasan:**
- `Credential` adalah **class** - blueprint untuk data login
- Saat dipanggil `new Credential()`, Java membuat **object** di heap memory
- Satu class bisa membuat banyak object (misal 100 credential = 100 object Credential)

### Class lain:
| File | Class | Merepresentasikan |
|------|-------|-------------------|
| `model/Category.java` | `Category` | Kategori (General, Social, dll) |
| `model/MasterConfig.java` | `MasterConfig` | Konfigurasi vault (satu baris) |
| `controller/AuthController.java` | `AuthController` | Pengatur alur autentikasi |
| `view/MainFrame.java` | `MainFrame` | Jendela utama vault |

---

## 2. Constructor

**Materi:** Constructor adalah method spesial yang dipanggil saat object dibuat. Namanya sama persis dengan nama class, tidak punya return type.

### Contoh: Constructor kosong dan constructor berparameter

```java
// File: src/main/java/com/lokki/model/Credential.java, baris 16-25

// Constructor 1: tanpa parameter (default)
public Credential() {}

// Constructor 2: dengan parameter - inisialisasi langsung
public Credential(String siteName, String siteUrl, String username,
                  String encryptedPassword, int categoryId, String notes) {
    this.siteName = siteName;
    this.siteUrl = siteUrl;
    this.username = username;
    this.encryptedPassword = encryptedPassword;
    this.categoryId = categoryId;
    this.notes = notes;
}
```

**Konsep penting:**
- `this` merujuk ke object saat ini - membedakan field (`this.siteName`) dari parameter (`siteName`)
- **Constructor overloading**: dua constructor dengan parameter berbeda
- Jika tidak menulis constructor sama sekali, Java menyediakan **default constructor** (no-arg) secara implicit

### Contoh lain: Constructor di AuthController

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 31-33
public AuthController() {
    this.authService = new AuthService();  // inisialisasi dependency
}
```

### Constructor di Category

```java
// File: src/main/java/com/lokki/model/Category.java, baris 9-14
public Category() {}

public Category(int id, String name) {
    this.id = id;
    this.name = name;
}
```

---

## 3. Encapsulation (Getter & Setter)

**Materi:** Enkapsulasi menyembunyikan data internal object dengan akses modifier `private`, lalu menyediakan method publik (`getter`/`setter`) untuk mengakses data.

### Contoh: Semua field di `Credential` adalah `private`

```java
// File: src/main/java/com/lokki/model/Credential.java, baris 5-6
private int id;
private String siteName;
```

Tidak bisa diakses langsung dari luar class:

```java
// ❌ ERROR: field is private
credential.id = 5;
credential.siteName = "GitHub";
```

Harus melalui getter/setter publik:

```java
// ✅ Benar
credential.setId(5);
credential.setSiteName("GitHub");
String name = credential.getSiteName();
```

**Mengapa enkapsulasi penting?**
1. **Kontrol akses** - kita bisa validasi di setter (misal menolak `null`)
2. **Read-only** - cukup buat getter saja tanpa setter
3. **Internal representation hiding** - kita bisa mengubah implementasi internal tanpa mengubah kode pemanggil

---

## 4. Inheritance (extends)

**Materi:** Inheritance memungkinkan sebuah class mewarisi field dan method dari class lain menggunakan keyword `extends`.

### Contoh 1: `CredentialTableModel extends AbstractTableModel`

```java
// File: src/main/java/com/lokki/view/component/CredentialTableModel.java, baris 8
public class CredentialTableModel extends AbstractTableModel {
```

`AbstractTableModel` adalah class dari Java Swing (package `javax.swing.table`). Dengan meng-extends-nya, `CredentialTableModel` mewarisi method seperti `fireTableDataChanged()` dan wajib meng-override method abstract:

```java
@Override
public int getRowCount() { ... }

@Override
public int getColumnCount() { ... }

@Override
public Object getValueAt(int rowIndex, int columnIndex) { ... }
```

### Contoh 2: `MainFrame extends JFrame`

```java
// File: src/main/java/com/lokki/view/MainFrame.java
public class MainFrame extends JFrame {
```

Semua method `JFrame` seperti `setTitle()`, `setSize()`, `setVisible()`, `setDefaultCloseOperation()` bisa langsung dipakai.

### Contoh 3: `PasswordStrengthBar extends JComponent`

```java
// File: src/main/java/com/lokki/view/component/PasswordStrengthBar.java, baris 10
public class PasswordStrengthBar extends JComponent {
```

**Class hierarchy:**
```
JComponent
  └── PasswordStrengthBar  (custom component)
```

```
AbstractTableModel
  └── CredentialTableModel
```

```
JFrame
  ├── MainFrame
  ├── LoginFrame
  ├── SetupFrame
  └── RecoveryFrame
```

```
JDialog
  ├── AddEditCredentialDialog
  └── PasswordGeneratorDialog
```

---

## 5. Polymorphism - Method Overriding

**Materi:** Overriding adalah menulis ulang method dari superclass di subclass dengan implementasi yang berbeda. Ditandai dengan anotasi `@Override`.

### Contoh 1: Override `getValueAt` di `CredentialTableModel`

```java
// File: src/main/java/com/lokki/view/component/CredentialTableModel.java, baris 47-58
@Override
public Object getValueAt(int rowIndex, int columnIndex) {
    Credential credential = credentials.get(rowIndex);
    switch (columnIndex) {
        case 0: return credential.getSiteName();
        case 1: return credential.getUsername();
        case 2: return "********";  // password never shown
        case 3: return credential.getCategoryName();
        case 4: return credential.getUpdatedAt() != null ?
                         credential.getUpdatedAt() : credential.getCreatedAt();
        default: return null;
    }
}
```

### Contoh 2: Override `toString` di `Category`

```java
// File: src/main/java/com/lokki/model/Category.java, baris 40-43
@Override
public String toString() {
    return name;  // JComboBox panggil toString() untuk display
}
```

### Contoh 3: Override `paintComponent` di `PasswordStrengthBar`

```java
// File: src/main/java/com/lokki/view/component/PasswordStrengthBar.java, baris 90-116
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    // ... custom drawing code ...
}
```

---

## 6. Polymorphism - Interface

**Materi:** Interface adalah kontrak yang mendefinisikan method apa saja yang harus dimiliki oleh class yang mengimplementasikannya. Interface memungkinkan **polymorphism** - object dari class berbeda bisa diperlakukan sama melalui interface yang sama.

### Contoh 1: Interface `Generator` di `PasswordGeneratorDialog`

```java
// File: src/main/java/com/lokki/view/PasswordGeneratorDialog.java
public interface Generator {
    String generate(int length, boolean includeUppercase, boolean includeLowercase,
                    boolean includeDigits, boolean includeSymbols);
}
```

**Implementasi anonymous di VaultController:**

```java
// VaultController membuat anonymous class yang mengimplementasikan Generator
private PasswordGeneratorDialog.Generator createPasswordGenerator() {
    return new PasswordGeneratorDialog.Generator() {
        @Override
        public String generate(int length, boolean upper, boolean lower,
                               boolean digits, boolean symbols) {
            return PasswordGeneratorService.generate(length, upper, lower, digits, symbols);
        }
    };
}
```

### Contoh 2: Interface `Runnable`

```java
// File: src/main/java/com/lokki/view/component/AutoLockManager.java
private final Runnable onLock;
// Dipanggil: onLock.run();
// Diisi oleh MainFrame dengan anonymous class (callback)
```

### Contoh 3: Interface `ActionListener` dari Swing

```java
// File: src/main/java/com/lokki/view/component/AutoLockManager.java, baris 25-41
this.timer = new Timer(TICK_INTERVAL_MS, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // method interface dipanggil setiap 5 detik
    }
});
```

---

## 7. Abstract Class

**Materi:** Abstract class adalah class yang tidak bisa di-instantiate langsung. Biasanya memiliki method abstract yang harus di-override oleh subclass.

### Contoh: `AbstractTableModel`

```java
// File: src/main/java/com/lokki/view/component/CredentialTableModel.java, baris 8
public class CredentialTableModel extends AbstractTableModel {
```

`AbstractTableModel` adalah abstract class. Method abstract yang wajib di-override:

```java
public abstract int getRowCount();     // wajib di-override
public abstract int getColumnCount();  // wajib di-override
public abstract Object getValueAt(int row, int column);  // wajib di-override
```

Method konkret (sudah ada implementasi) yang bisa dipakai langsung:

```java
fireTableDataChanged();  // memberitahu JTable untuk refresh
```

---

## 8. Static vs Instance

**Materi:** `static` berarti milik **class** (bersama untuk semua object), non-static berarti milik **instance** (masing-masing object punya salinan sendiri).

### Contoh Static pada Utility Class: `EncryptionService`

```java
// File: src/main/java/com/lokki/service/EncryptionService.java, baris 9, 22, 44
public final class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";  // static field
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private EncryptionService() {}  // private constructor agar tidak bisa dibuat object

    public static String encryptWithAES(byte[] key, String plaintext) { ... }
    public static String decryptWithAES(byte[] key, String ciphertextBase64) { ... }
}
```

**Dipanggil tanpa membuat object:**

```java
// Tidak perlu: EncryptionService es = new EncryptionService();
String encrypted = EncryptionService.encryptWithAES(vaultKey, "password123");
String decrypted = EncryptionService.decryptWithAES(vaultKey, encrypted);
```

### Contoh Instance - setiap object punya state sendiri

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 17-18
private final AuthService authService;  // instance field
private byte[] vaultKey;                // instance field - berbeda untuk setiap object

// File: src/main/java/com/lokki/dao/DatabaseManager.java, baris 15
private static Connection connection;   // static - satu koneksi untuk semua pemanggil
```

### Utility class dengan static methods:

| Class | Method Static |
|-------|---------------|
| `EncryptionService` | `encryptWithAES`, `decryptWithAES` |
| `KeyDerivationService` | `deriveKey`, `generateSalt`, `hashPassword` |
| `RecoveryKeyService` | `generate`, `formatForDisplay` |
| `PasswordGeneratorService` | `generate` |
| `SecureMemoryUtil` | `clearCharArray`, `clearByteArray` |
| `RecoveryKeyFormatter` | `format`, `strip`, `getExpectedLength` |
| `AppIcon` | `getIcon` |

Semua class di atas punya **private constructor** - tidak bisa dibuat object-nya.

---

## 9. `final` Keyword

**Materi:** `final` punya 3 arti tergantung konteks: class tidak bisa di-extends, method tidak bisa di-override, variabel tidak bisa diubah.

### Contoh di Lokki:

| Penggunaan | File | Baris | Arti |
|-----------|------|-------|------|
| `public final class EncryptionService` | `EncryptionService.java` | 9 | Class tidak bisa di-extends |
| `private static final String ALGORITHM` | `EncryptionService.java` | 11 | Konstanta - tidak bisa diubah |
| `private final AuthService authService` | `AuthController.java` | 17 | Field reference tidak bisa di-reassign |

---

## 10. Package dan Import

**Materi:** Package mengorganisir class ke dalam folder. Import digunakan untuk menggunakan class dari package lain.

### Struktur package Lokki:

```
com.lokki                  → App.java (entry point)
com.lokki.model            → Credential, Category, MasterConfig
com.lokki.dao              → DatabaseManager, MasterConfigDAO, CategoryDAO, CredentialDAO
com.lokki.service          → AuthService, VaultService, EncryptionService, dll
com.lokki.controller       → AuthController, VaultController
com.lokki.view             → MainFrame, LoginFrame, SetupFrame, dll
com.lokki.view.component   → CredentialTableModel, PasswordStrengthBar, dll
com.lokki.util             → AppIcon, SecureMemoryUtil, RecoveryKeyFormatter
```

### Contoh import:

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 1-13
package com.lokki.controller;

import com.lokki.service.AuthService;           // import class dari package lain
import com.lokki.view.LoginFrame;               // import class dari package view
import com.lokki.view.SetupFrame;
import javax.swing.JFrame;                       // import dari library Java standard
import javax.swing.JOptionPane;
import java.util.Arrays;                         // import dari java.util
```

### Aturan di MVC Lokki:
- **View** hanya import dari package `view`, `view.component`, dan `util`
- **Controller** import dari `view` dan `service`
- **Service** import dari `dao`
- **DAO** import dari `model` dan `java.sql`

---

## 11. Exception Handling

**Materi:** Exception adalah mekanisme Java untuk menangani error saat runtime. Blok `try-catch-finally` digunakan untuk menangkap dan menangani exception.

### Contoh 1: Try-Catch di AuthController

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 58-77
try {
    char[] recoveryKey = RecoveryKeyService.generate();
    vaultKey = authService.setupVault(masterPassword, recoveryKey);
    setupFrame.showRecoveryKey(formattedKey);
    setupFrame.dispose();
    if (callback != null) {
        callback.onAuthenticated(vaultKey);
    }
} catch (Exception e) {
    JOptionPane.showMessageDialog(setupFrame,
            "Failed to create vault: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
}
```

### Contoh 2: Exception di EncryptionService

```java
// File: src/main/java/com/lokki/service/EncryptionService.java, baris 22-38
public static String encryptWithAES(byte[] key, String plaintext) {
    try {
        // ... operasi kriptografi ...
        return Base64.getEncoder().encodeToString(combined);
    } catch (Exception e) {
        throw new RuntimeException("Encryption failed", e);
        //          ^^^ unchecked exception - tidak wajib di-declare di throws
    }
}
```

### Contoh 3: Try-With-Resources (auto-close resource)

```java
// File: src/main/java/com/lokki/dao/DatabaseManager.java, baris 28-29
try (Connection tempConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
     Statement stmt = tempConn.createStatement()) {
    // Connection dan Statement akan otomatis close setelah blok try selesai
}
```

### Contoh 4: Try-Finally untuk cleanup

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 62-71
try {
    vaultKey = authService.setupVault(masterPassword, recoveryKey);
    setupFrame.showRecoveryKey(formattedKey);
    setupFrame.dispose();
    // ...
} finally {
    Arrays.fill(recoveryKey, '\u0000');  // tetap dijalankan meskipun ada exception!
}
```

`finally` **selalu** dijalankan - baik ada exception maupun tidak. Cocok untuk cleanup (hapus key dari memory, tutup koneksi, dll).

### Contoh 5: Try-Finally untuk jaminan System.exit

```java
// File: src/main/java/com/lokki/controller/VaultController.java, baris 116-126
@Override
public void onExit() {
    try {
        if (authController != null) {
            authController.clearSession();
        }
    } finally {
        mainFrame.cleanup();   // matikan timer + hapus event listener
        mainFrame.dispose();
        System.exit(0);        // tetap dijalankan meskipun clearSession() throw
    }
}
```

### Contoh 6: Per-item exception handling - satu credential corrupt tidak merusak yang lain

```java
// File: src/main/java/com/lokki/service/VaultService.java, baris 118-127
private void decryptPasswords(byte[] vaultKey, List<Credential> credentials) {
    for (Credential credential : credentials) {
        try {
            String decrypted = EncryptionService.decryptWithAES(
                    vaultKey, credential.getEncryptedPassword());
            credential.setEncryptedPassword(decrypted);
        } catch (Exception e) {
            credential.setEncryptedPassword("[decryption error]");
            // teruskan ke credential berikutnya, jangan crash
        }
    }
}
```

**Pola penting:** Exception di dalam loop ditangkap per-item, bukan per-list. Satu data corrupt tidak menghalangi data lain untuk ditampilkan.

---

## 12. Collections Framework

**Materi:** Collections Framework menyediakan struktur data siap pakai seperti `List`, `ArrayList`, `Map`, `Set`.

### Contoh: `List<Credential>` dan `ArrayList`

```java
// File: src/main/java/com/lokki/view/component/CredentialTableModel.java, baris 11-15
private List<Credential> credentials;  // interface

public CredentialTableModel() {
    this.credentials = new ArrayList<>();  // implementasi konkret
}
```

### Method-method Collections yang dipakai di Lokki:

| Method | File | Penggunaan |
|--------|------|------------|
| `list.size()` | CredentialTableModel:33 | Jumlah baris tabel |
| `list.get(index)` | CredentialTableModel:29 | Ambil credential per baris |
| `Arrays.fill(array, value)` | SecureMemoryUtil:11 | Hapus isi array |

---

## 13. Generics

**Materi:** Generics memungkinkan parameter tipe pada class/method sehingga type-safe.

### Contoh: `List<Credential>`

```java
// File: src/main/java/com/lokki/model/Credential.java
private List<Credential> credentials;
//       ^^^^^^^^^^^^^^^ generic - list hanya bisa berisi object Credential
```

**Tanpa generic (sebelum Java 5):**

```java
List list = new ArrayList();  // bisa diisi apa saja
list.add("string");
list.add(123);
list.add(new Credential());  // tidak ada pemeriksaan tipe
```

**Dengan generic:**

```java
List<Credential> list = new ArrayList<>();
list.add(new Credential());  // ✅ hanya Credential
list.add("string");           // ❌ compile error
```

### Generic di interface callback:

```java
// Consumer<Integer> - generic interface dengan tipe Integer
// File: ClipboardTimer.java
private final Consumer<Integer> onTick;
```

---

## 14. Anonymous Class

**Materi:** Anonymous class adalah class tanpa nama yang langsung di-instantiate, biasanya untuk mengimplementasikan interface atau meng-extends class secara inline.

### Contoh 1: Implementasi `ActionListener` secara anonymous

```java
// File: src/main/java/com/lokki/view/component/AutoLockManager.java, baris 25-41
this.timer = new Timer(TICK_INTERVAL_MS, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        countdownSeconds -= TICK_INTERVAL_MS / 1000;
        // ... update UI ...
    }
});
```

### Contoh 2: Implementasi `AWTEventListener` secara anonymous

```java
// File: src/main/java/com/lokki/view/component/AutoLockManager.java, baris 44-53
this.awtEventListener = new java.awt.event.AWTEventListener() {
    @Override
    public void eventDispatched(java.awt.event.AWTEvent event) {
        if (event.getSource() instanceof javax.swing.JComponent) {
            reset();  // reset auto-lock setiap ada aktivitas mouse/keyboard
        }
    }
};
Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener,
        AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
```

**Penting:** AWTEventListener disimpan di field `awtEventListener` agar bisa di-remove dengan method `stop()`:

```java
// File: src/main/java/com/lokki/view/component/AutoLockManager.java, baris 58-61
public void stop() {
    timer.stop();
    Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
}
```

Tanpa `stop()`, listener akan terus hidup meskipun jendela ditutup - menyebabkan **zombie process**.

### Contoh 3: Callback Setup

```java
// File: src/main/java/com/lokki/controller/AuthController.java, baris 56-78
setupFrame.setCallback(new SetupFrame.SetupCallback() {
    @Override
    public void onSetupComplete(char[] masterPassword) {
        // logic ketika user selesai setup
    }
});
```

Anonymous class digunakan secara ekstensif di semua view callback - ini adalah cara Java Swing menghubungkan event UI dengan logic aplikasi.

---

## 15. Enum

**Materi:** Enum adalah tipe data yang memiliki sekumpulan konstanta tetap. Enum di Java lebih kuat dari bahasa lain - bisa punya field, constructor, dan method.

### Contoh: Enum `Strength` di `PasswordStrengthBar`

```java
// File: src/main/java/com/lokki/view/component/PasswordStrengthBar.java, baris 12-33
public enum Strength {
    WEAK("Weak", new Color(220, 53, 69)),
    MEDIUM("Medium", new Color(255, 193, 7)),
    STRONG("Strong", new Color(40, 167, 69)),
    VERY_STRONG("Very Strong", new Color(0, 123, 255));

    private final String label;
    private final Color color;

    Strength(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() { return label; }
    public Color getColor() { return color; }
}
```

**Cara pakai:**

```java
Strength s = Strength.STRONG;
String label = s.getLabel();  // "Strong"
Color c = s.getColor();       // RGB(40,167,69)
```

**Mengapa enum?** - lebih aman daripada `String` atau `int` karena nilai dibatasi. Tidak mungkin salah tulis "Stroong" atau pakai `int` 99.

---

## 16. Inner Interface

**Materi:** Interface bisa didefinisikan di dalam class (inner interface). Biasanya untuk mendefinisikan callback contract.

### Contoh: Interface di dalam `MainFrame`

```java
// File: src/main/java/com/lokki/view/MainFrame.java
public class MainFrame extends JFrame {

    public interface MainFrameCallback {
        void onAddCredential();
        void onEditCredential(Credential credential);
        void onDeleteCredential(Credential credential);
        void onCopyUsername(Credential credential);
        void onCopyPassword(Credential credential);
        void onSearch(String searchTerm);
        void onCategoryFilter(int categoryId);
        void onShowPasswordGenerator();
        void onLock();
        void onExit();
        void onRefresh();
    }
    // ...
}
```

### Semua inner interface di Lokki:

| Interface | File | Untuk |
|-----------|------|-------|
| `MainFrame.MainFrameCallback` | `view/MainFrame.java` | Semua aksi dari jendela utama |
| `LoginFrame.LoginCallback` | `view/LoginFrame.java` | Login dan minta recovery |
| `SetupFrame.SetupCallback` | `view/SetupFrame.java` | Setup selesai |
| `RecoveryFrame.RecoveryCallback` | `view/RecoveryFrame.java` | Recovery atau batal |
| `AddEditCredentialDialog.CredentialCallback` | `view/AddEditCredentialDialog.java` | Simpan atau batal edit |
| `PasswordGeneratorDialog.Generator` | `view/PasswordGeneratorDialog.java` | Generate password |
| `PasswordGeneratorDialog.PasswordSelectionCallback` | `view/PasswordGeneratorDialog.java` | Password dipilih |
| `AuthController.AuthCallback` | `controller/AuthController.java` | Autentikasi berhasil/sesi dibersihkan |

---

## 17. Try-With-Resources

**Materi:** Sejak Java 7, resource yang mengimplementasikan `AutoCloseable` bisa otomatis ditutup menggunakan try-with-resources.

### Contoh: Di `DatabaseManager`

```java
// File: src/main/java/com/lokki/dao/DatabaseManager.java, baris 28-29
try (Connection tempConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
     Statement stmt = tempConn.createStatement()) {

    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS lokki_db ...");

} catch (SQLException e) {
    throw new SQLException("Failed to initialize database schema", e);
}
// tempConn dan stmt otomatis di-close() tanpa perlu finally blok
```

### Contoh di `CredentialDAO` (pola JDBC):

```java
// Pola yang digunakan di semua method CredentialDAO
try (Connection conn = DatabaseManager.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, value);
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            // mapping ResultSet ke object
        }
    }
}
```

Resource `Connection`, `PreparedStatement`, dan `ResultSet` semuanya `AutoCloseable`.

---

## 18. MVC Architecture

**Materi:** Model-View-Controller memisahkan aplikasi menjadi 3 komponen utama.

### Arsitektur 4 Layer Lokki:

```
┌──────────────────────────────────────────────┐
│ VIEW (tampilan, input user)                  │
│ MainFrame, LoginFrame, SetupFrame, ...       │
│ Dialog, JTable, JMenuBar                     │
├──────────────────────────────────────────────┤
│ CONTROLLER (menghubungkan View ↔ Service)    │
│ AuthController, VaultController              │
├──────────────────────────────────────────────┤
│ SERVICE (logika bisnis, enkripsi)            │
│ AuthService, VaultService, EncryptionService │
├──────────────────────────────────────────────┤
│ DAO (akses database, query SQL)              │
│ DatabaseManager, CredentialDAO, ...          │
├──────────────────────────────────────────────┤
│ MODEL / DATABASE (POJO + MySQL)              │
│ Credential, Category, MasterConfig           │
└──────────────────────────────────────────────┘
```

### Aturan MVC Lokki:

| Layer | Import dari | Tidak boleh import |
|-------|-------------|-------------------|
| **View** | Model, Util | Service, DAO |
| **Controller** | View, Service | DAO |
| **Service** | DAO | View |
| **DAO** | Model | View, Service |

### Aliran data saat user menambah credential:

```
1. User klik "Add" di MainFrame
       │
2. MainFrame panggil callback → VaultController.showAddCredentialDialog()
       │
3. Tampilkan AddEditCredentialDialog (View)
       │
4. User isi form, klik Save
       │
5. Callback → VaultController menerima data
       │
6. VaultController panggil VaultService.addCredential()
       │
7. VaultService panggil EncryptionService.encryptWithAES()
       │
8. VaultService panggil CredentialDAO.insert()
       │
9. CredentialDAO jalankan INSERT INTO credentials ...
       │
10. VaultController.refreshCredentials() → update tabel
```

---

## 19. Design Patterns di Lokki

### 1. Singleton (melalui static utility)

```java
// DatabaseManager - koneksi tunggal
private static Connection connection;

public static Connection getConnection() {
    if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(...);
    }
    return connection;
}

// EncryptionService - private constructor, static methods
private EncryptionService() {}
public static String encryptWithAES(...) { ... }
```

### 2. Utility Class (stateless)

Class seperti `EncryptionService`, `KeyDerivationService`, `PasswordGeneratorService`, `SecureMemoryUtil`, `AppIcon`, dan `RecoveryKeyFormatter` semuanya mengikuti pola yang sama:
- `private` constructor
- Semua method `public static`
- Tidak punya state (tidak ada field instance)

### 3. Callback Pattern

Menggunakan interface untuk komunikasi View → Controller:

```
View                    Controller
  │                        │
  │ setCallback(callback)  │
  │ ───────────────►       │
  │                        │
  │ event terjadi          │
  │ callback.onXxx(data)   │
  │ ───────────────►       │
```

### 4. Strategy Pattern

`PasswordGeneratorDialog.Generator` adalah interface yang memisahkan "cara generate password" dari dialog-nya. `VaultController` menyediakan implementasi yang mendelegasikan ke `PasswordGeneratorService`. Jika suatu saat ingin mengganti algoritma generate, cukup buat implementasi baru tanpa mengubah dialog.

### 5. Factory Method

`VaultController.createPasswordGenerator()` adalah factory method yang membuat object `Generator`:

```java
private PasswordGeneratorDialog.Generator createPasswordGenerator() {
    return new PasswordGeneratorDialog.Generator() {
        @Override
        public String generate(...) {
            return PasswordGeneratorService.generate(length, upper, lower, digits, symbols);
        }
    };
}
```

---

## Ringkasan Konsep per File

| File (package) | Konsep OOP yang bisa dipelajari |
|---------------|--------------------------------|
| `model/Credential.java` | Class, object, constructor overloading, encapsulation |
| `model/Category.java` | Override `toString()`, constructor |
| `model/MasterConfig.java` | POJO, constructor, encapsulation |
| `dao/DatabaseManager.java` | Static field/method, try-with-resources, singleton pattern |
| `dao/CredentialDAO.java` | JDBC, try-with-resources, List, Generics, `findById()` |
| `dao/CategoryDAO.java` | JDBC query, List return type |
| `dao/MasterConfigDAO.java` | JDBC insert/update/select |
| `service/EncryptionService.java` | Static utility, final class, byte array manipulation |
| `service/KeyDerivationService.java` | Static utility, final constants, exception handling |
| `service/AuthService.java` | Envelope encryption, finally block cleanup, instance fields |
| `service/VaultService.java` | Delegation, encrypt/decrypt flow, per-item exception handling (`[decryption error]`), `findById()` to restore ciphertext on unchanged password |
| `service/RecoveryKeyService.java` | Static utility, SecureRandom |
| `service/PasswordGeneratorService.java` | Static utility, Fisher-Yates shuffle |
| `controller/AuthController.java` | Anonymous class callback, inner interface, try-finally, `clearAllFields()` on recovery failure |
| `controller/VaultController.java` | Factory method, anonymous Generator, callback wiring, try-finally cleanup on exit, `cleanup()` before `dispose()` on lock |
| `view/MainFrame.java` | Inheritance (extends JFrame), inner interface, Swing components, `cleanup()` method to stop timers and remove listeners |
| `view/LoginFrame.java` | Inheritance, inner interface, Timer, event handling |
| `view/SetupFrame.java` | Inheritance, inner interface, password strength validation |
| `view/RecoveryFrame.java` | Inheritance, inner interface, paste handling, `clearAllFields()` public untuk reset form dari luar |
| `view/AddEditCredentialDialog.java` | Inheritance (extends JDialog), constructor overloading |
| `view/PasswordGeneratorDialog.java` | Inheritance, inner interface (Generator), callback pattern |
| `view/component/CredentialTableModel.java` | Inheritance (extends AbstractTableModel), List, Generics, Override |
| `view/component/PasswordStrengthBar.java` | Inheritance (extends JComponent), enum, Override paintComponent |
| `view/component/AutoLockManager.java` | Anonymous class (ActionListener, AWTEventListener), Runnable, `stop()` method to remove global listener and prevent zombie processes |
| `view/component/ClipboardTimer.java` | Anonymous class, Consumer interface, Timer |
| `view/component/MainMenuBar.java` | Inheritance (extends JMenuBar), menu bar construction |
| `util/AppIcon.java` | Static utility, Java2D graphics, private constructor |
| `util/SecureMemoryUtil.java` | Static utility, Arrays.fill, null check |
| `util/RecoveryKeyFormatter.java` | Static utility, char array manipulation |
