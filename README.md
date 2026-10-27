# GoviPOS
GoviPOS is an Agricultural based POS system that enables multi branch management, product based expiry tracking and numerous tasks convenient while obeying an offline first architecture

## Desktop UI

Requires JDK 21 or newer and Maven 3.9 or newer.

```powershell
mvn clean package
mvn javafx:run
```

The JavaFX frontend reproduces the supplied general and role-specific login references, with a resizable 50/50 desktop layout, role selection, password visibility. Choose a role and click Login to preview that role's login presentation. All fields are optional; this is a frontend-only prototype.

Select Admin and click Login once to open the admin dashboard. Empty credentials are allowed. Use the A account menu to sign out. Other roles retain their login presentations.

The dashboard controls open full Create Branch, Remove Branch and Assign Branch screens. The Assign Branch screen follows the supplied Staff Role & Access Management (RBAC) reference; Add New Staff Account opens Create Account. Back to Inventory Hub returns to the admin dashboard. Escape returns from Create Account to the staff screen, without adding a button absent from the reference.

All actions are frontend only. Form fields are editable; there is no authentication, validation, persistence, network connection or actual branch removal. Save/register/create controls are visual affordances and do not submit data. The destructive removal button remains disabled, matching the screenshot. Clipped audit amounts and statuses use visible reference text with ellipses, as requested. Search fields are visual inputs; they do not filter the static reference tables.

Open the dashboard directly for visual review:

```powershell
mvn javafx:run "-Djavafx.args=--dashboard"
```

Preview any new screen directly:

```powershell
mvn javafx:run "-Djavafx.args=--create-branch"
mvn javafx:run "-Djavafx.args=--remove-branch"
mvn javafx:run "-Djavafx.args=--assign-branch"
mvn javafx:run "-Djavafx.args=--create-account"
```

Export screenshots of all four real JavaFX screens to `target/previews/` and leave the dashboard open:

```powershell
mvn javafx:run "-Djavafx.args=--preview-all"
```

The new scenes share `AdminScreens.java` and `admin-screens.css`. Two-panel management screens stack when the available width drops below 850 pixels, and long forms scroll vertically. Create Account preserves the reference's equal green/white split. The artwork's outer dark canvas, captions and blue Figma selection outlines are not part of the application UI.
