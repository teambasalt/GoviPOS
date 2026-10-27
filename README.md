# GoviPOS
GoviPOS is an Agricultural based POS system that enables multi branch management, product based expiry tracking and numerous tasks convenient while obeying an offline first architecture

## Desktop UI

Requires JDK 21 or newer and Maven 3.9 or newer.

```powershell
mvn clean package
mvn javafx:run
```

The JavaFX frontend reproduces the supplied general and role-specific login references, with a resizable 50/50 desktop layout, role selection, password visibility. Choose a role and click Sign in to preview that role's login presentation. All fields are optional; this is a frontend-only prototype.

Select Admin, click Sign in to open the admin login presentation, then click Sign in again to open the admin dashboard. Empty credentials are allowed. Use the A account menu to sign out. Other roles retain their login presentations.

The admin dashboard includes the green overview cards, operational controls and local-only action dialogs. Dialog fields are illustrative; Done closes the dialog without changing data. Metrics are static reference values. There is no authentication, validation, persistence or network connection. JavaFX source lives in `src/main/java/com/govipos/login.java` and `AdminDashboard.java`, with styles in `src/main/resources/com/govipos/`.

Open the dashboard directly for visual review:

```powershell
mvn javafx:run "-Djavafx.args=--dashboard"
```

The layout and colours follow the supplied screenshot. Its small size prevents verification of tiny labels and the original font; these are approximated. The JavaFX app runs in a native desktop window; the interactive chat preview is an HTML representation of the same frontend flow.
