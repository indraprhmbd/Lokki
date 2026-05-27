# Class Diagram - Lokki

## 1. Kelas per Package

### package `model`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `Credential` | `id: int, siteName: String, siteUrl: String, username: String, encryptedPassword: String, categoryId: int, categoryName: String, notes: String, createdAt: String, updatedAt: String` | getter/setter tiap field, 2 konstruktor |
| `Category` | `id: int, name: String, createdAt: String` | getter/setter, konstruktor(id, name) |
| `MasterConfig` | `id: int, passwordHash: String, saltMaster: String, saltRecovery: String, encryptedVaultKeyByMaster: String, encryptedVaultKeyByRecovery: String, createdAt: String` | getter/setter |

### package `dao`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `DatabaseManager` | (static) `BASE_URL: String, DB_URL: String, USER: String, PASSWORD: String, connection: Connection` | (static) `getConnection(): Connection`, `ensureDatabaseExists(): void` |
| `MasterConfigDAO` | — | `insert(MasterConfig): void`, `get(): MasterConfig`, `count(): int`, `updateMasterFields(String, String, String): void` |
| `CredentialDAO` | — | `findAll(): List<Credential>`, `findByCategory(int): List<Credential>`, `search(String): List<Credential>`, `insert(Credential): void`, `update(Credential): void`, `delete(int): void`, `mapRow(ResultSet): Credential` |
| `CategoryDAO` | — | `findAll(): List<Category>` |

### package `service`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `AuthService` | `masterConfigDAO: MasterConfigDAO, secureRandom: SecureRandom, activeVaultKey: byte[]` | `isFirstRun(): boolean`, `setupVault(char[], char[]): byte[]`, `login(char[]): byte[]`, `recoverWithKey(char[], char[]): byte[]`, `getActiveVaultKey(): byte[]`, `clearSession(): void` |
| `VaultService` | `credentialDAO: CredentialDAO, categoryDAO: CategoryDAO` | `getAllCredentials(byte[]): List<Credential>`, `getCredentialsByCategory(byte[], int): List<Credential>`, `searchCredentials(byte[], String): List<Credential>`, `addCredential(byte[], Credential, String): void`, `updateCredential(byte[], Credential, String): void`, `deleteCredential(int): void`, `getAllCategories(): List<Category>`, `decryptPasswords(byte[], List<Credential>): void` |
| `EncryptionService` | (static) `ALGORITHM: String, IV_LENGTH: int, GCM_TAG_LENGTH: int, SECURE_RANDOM: SecureRandom` | (static) `encryptWithAES(byte[], String): String`, `decryptWithAES(byte[], String): String` |
| `KeyDerivationService` | (static) `ALGORITHM: String, ITERATIONS: int, KEY_LENGTH: int, SALT_LENGTH: int, SECURE_RANDOM: SecureRandom` | (static) `deriveKey(char[], byte[]): byte[]`, `generateSalt(): byte[]`, `hashPassword(char[], byte[]): String` |
| `PasswordGeneratorService` | (static) `UPPERCASE: String, LOWERCASE: String, DIGITS: String, SYMBOLS: String, RANDOM: SecureRandom` | (static) `generate(int, boolean, boolean, boolean, boolean): String` |
| `RecoveryKeyService` | (static) `CHARSET: String, KEY_LENGTH: int, RANDOM: SecureRandom` | (static) `generate(): char[]`, `formatForDisplay(char[]): String` |

### package `controller`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `AuthController` | `authService: AuthService, vaultKey: byte[], setupFrame: SetupFrame, loginFrame: LoginFrame, recoveryFrame: RecoveryFrame, parentFrame: JFrame, callback: AuthCallback` | `startAuthFlow(): void`, `clearSession(): void`, `getVaultKey(): byte[]`, `setCallback(AuthCallback): void`, `setParentFrame(JFrame): void` — private: `showSetupFrame(): void`, `showLoginFrame(): void`, `showRecoveryFrame(): void` |
| `VaultController` | `vaultService: VaultService, vaultKey: byte[], mainFrame: MainFrame, authController: AuthController` | `openMainFrame(): void`, `setAuthController(AuthController): void` — private: `showAddCredentialDialog(): void`, `showEditCredentialDialog(Credential): void`, `refreshCredentials(): void`, `loadCategories(): void`, `createPasswordGenerator(): Generator` |

### package `view`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `LoginFrame` (JFrame) | `passwordField: JPasswordField, unlockButton: JButton, statusLabel: JLabel, attemptsLabel: JLabel, callback: LoginCallback, remainingAttempts: int, cooldownTimer: Timer` | `setCallback(LoginCallback): void`, `onLoginFailed(): void`, `onLoginSuccess(): void` — private: `initComponents(): void`, `startCooldown(): void` |
| `SetupFrame` (JFrame) | `passwordField: JPasswordField, confirmField: JPasswordField, strengthBar: PasswordStrengthBar, setupButton: JButton, showPasswordCheckbox: JCheckBox, callback: SetupCallback` | `setCallback(SetupCallback): void`, `showRecoveryKey(String): void` — private: `initComponents(): void`, `updateStrengthAndValidate(): void` |
| `RecoveryFrame` (JFrame) | `recoveryFields: JTextField[6], newPasswordField: JPasswordField, confirmField: JPasswordField, recoverButton: JButton, statusLabel: JLabel, callback: RecoveryCallback` | `setCallback(RecoveryCallback): void` — private: `initComponents(): void`, `updateValidation(): void`, `handlePaste(): void`, `getClipboardText(): String`, `showPasteMenu(MouseEvent): void`, `clearAllFields(): void` |
| `MainFrame` (JFrame) | `credentialTable: JTable, tableModel: CredentialTableModel, searchField: JTextField, categoryFilter: JComboBox<Category>, statusLabel: JLabel, clipboardLabel: JLabel, autoLockLabel: JLabel, clipboardTimer: ClipboardTimer, autoLockManager: AutoLockManager, menuBar: MainMenuBar, callback: MainFrameCallback, currentCredentials: List<Credential>` | `setCallback(MainFrameCallback): void`, `initComponents(): void`, `refreshTable(List<Credential>): void`, `setCategories(List<Category>): void`, `startClipboardCountdown(): void`, `copyToClipboard(String): void`, `showError(String): void`, `confirmDelete(Credential): boolean` — private: `createToolBar(): JToolBar`, `createMainPanel(): JPanel`, `createStatusBar(): JPanel`, `initClipboardTimer(): void`, `search(): void` |
| `AddEditCredentialDialog` (JDialog) | `siteNameField: JTextField, siteUrlField: JTextField, usernameField: JTextField, passwordField: JPasswordField, showPasswordCheckbox: JCheckBox, categoryCombo: JComboBox<Category>, notesArea: JTextArea, saveButton: JButton, cancelButton: JButton, generateButton: JButton, passwordGenerator: Generator, callback: CredentialCallback, editingCredential: Credential` | `setCallback(CredentialCallback): void` — private: `initComponents(List<Category>): void`, `populateFields(Credential): void`, `updateValidation(): void` |
| `PasswordGeneratorDialog` (JDialog) | `generator: Generator, lengthSpinner: JSpinner, uppercaseCheckbox: JCheckBox, lowercaseCheckbox: JCheckBox, digitsCheckbox: JCheckBox, symbolsCheckbox: JCheckBox, previewField: JTextField, useButton: JButton, selectedPassword: String, callback: PasswordSelectionCallback` | `setCallback(PasswordSelectionCallback): void` — private: `initComponents(): void`, `generatePassword(): void` |

### package `view.component`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `AutoLockManager` | (static) `AUTO_LOCK_DELAY_MS: int, TICK_INTERVAL_MS: int` — (instance) `timer: Timer, statusLabel: JLabel, onLock: Runnable, countdownSeconds: int` | `reset(): void` |
| `ClipboardTimer` | (static) `CLEAR_INTERVAL_MS: int, TOTAL_SECONDS: int` — (instance) `timer: Timer, remainingSeconds: int, onTick: Consumer<Integer>, onComplete: Runnable` | `start(): void`, `stop(): void` |
| `CredentialTableModel` (AbstractTableModel) | (static) `COLUMN_NAMES: String[]` — (instance) `credentials: List<Credential>` | `setCredentials(List<Credential>): void`, `getCredentialAt(int): Credential`, `getRowCount(): int`, `getColumnCount(): int`, `getColumnName(int): String`, `getValueAt(int, int): Object` |
| `MainMenuBar` (JMenuBar) | `callback: MainFrame.MainFrameCallback` | `setCallback(MainFrameCallback): void` — private: `initMenus(): void` |
| `PasswordStrengthBar` (JPanel) | (static inner enum) `Strength{WEAK, MEDIUM, STRONG, VERY_STRONG}` — (instance) `currentStrength: Strength, fillRatio: float` | (static) `evaluateStrength(char[]): Strength` — (instance) `updateStrength(char[]): void` — (override) `paintComponent(Graphics): void` |

### package `util`

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `SecureMemoryUtil` | — | (static) `clearByteArray(byte[]): void`, `clearCharArray(char[]): void` |
| `RecoveryKeyFormatter` | (static) `GROUP_SIZE: int, GROUP_COUNT: int, SEPARATOR: char` | (static) `format(char[]): String`, `strip(String): char[]`, `getExpectedLength(): int`, `getFormattedLength(): int` |
| `AppIcon` | (static) `SIZE: int` | (static) `getIcon(): ImageIcon` |

### Entry Point

| Kelas | Attribute | Method |
|-------|-----------|--------|
| `App` | — | `main(String[]): void` — private: `initLookAndFeel(): void` |

---

## 2. Relasi

### Dependency (A dependency B — A menggunakan B sebagai parameter lokal atau static call)

1. `App` dependency `AuthController`
2. `App` dependency `VaultController`
3. `AuthController` dependency `AuthService`
4. `AuthController` dependency `RecoveryKeyService`
5. `AuthController` dependency `RecoveryKeyFormatter`
6. `VaultController` dependency `VaultService`
7. `VaultController` dependency `PasswordGeneratorService`
8. `VaultController` dependency `AddEditCredentialDialog`
9. `VaultController` dependency `PasswordGeneratorDialog`
10. `AuthService` dependency `MasterConfigDAO`
11. `AuthService` dependency `KeyDerivationService`
12. `AuthService` dependency `EncryptionService`
13. `AuthService` dependency `SecureMemoryUtil`
14. `VaultService` dependency `CredentialDAO`
15. `VaultService` dependency `CategoryDAO`
16. `VaultService` dependency `EncryptionService`
17. `MasterConfigDAO` dependency `DatabaseManager`
18. `CredentialDAO` dependency `DatabaseManager`
19. `CategoryDAO` dependency `DatabaseManager`
20. `EncryptionService` dependency `SecureRandom`
21. `KeyDerivationService` dependency `SecureMemoryUtil`
22. `PasswordStrengthBar` dependency `SecureRandom`
23. `RecoveryKeyService` dependency `RecoveryKeyFormatter`
24. `RecoveryKeyFormatter` dependency `SecureMemoryUtil`
25. `SetupFrame` dependency `AppIcon`
26. `LoginFrame` dependency `AppIcon`
27. `RecoveryFrame` dependency `AppIcon`
28. `AddEditCredentialDialog` dependency `PasswordGeneratorDialog.Generator` (interface)
29. `PasswordGeneratorDialog` dependency `PasswordGeneratorDialog.Generator` (interface)

### Association (A association B — A memiliki field referensi ke B)

30. `AuthController` association `SetupFrame`
31. `AuthController` association `LoginFrame`
32. `AuthController` association `RecoveryFrame`
33. `VaultController` association `MainFrame`
34. `VaultController` association `AuthController`
35. `MainFrame` association `AutoLockManager`
36. `MainFrame` association `ClipboardTimer`
37. `MainFrame` association `CredentialTableModel`
38. `MainFrame` association `MainMenuBar`
39. `MainMenuBar` association `MainFrame.MainFrameCallback` (interface)

### Inheritance (A inheritance B — A extends B)

40. `LoginFrame` inheritance `JFrame`
41. `SetupFrame` inheritance `JFrame`
42. `RecoveryFrame` inheritance `JFrame`
43. `MainFrame` inheritance `JFrame`
44. `AddEditCredentialDialog` inheritance `JDialog`
45. `PasswordGeneratorDialog` inheritance `JDialog`
46. `CredentialTableModel` inheritance `AbstractTableModel`
47. `MainMenuBar` inheritance `JMenuBar`
48. `PasswordStrengthBar` inheritance `JPanel`

### Realization (A realization B — A implements B, baik class anonymous maupun inner interface)

49. `App` realization `AuthController.AuthCallback`
50. `VaultController` realization `MainFrame.MainFrameCallback`
51. `VaultController` realization `PasswordGeneratorDialog.Generator`
52. anonymous class di `LoginFrame` realization `LoginFrame.LoginCallback`
53. anonymous class di `SetupFrame` realization `SetupFrame.SetupCallback`
54. anonymous class di `RecoveryFrame` realization `RecoveryFrame.RecoveryCallback`
55. anonymous class di `AddEditCredentialDialog` realization `AddEditCredentialDialog.CredentialCallback`
56. anonymous class di `MainFrame` realization `MainFrame.MainFrameCallback`
