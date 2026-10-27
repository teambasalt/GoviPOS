# GoviPOS
GoviPOS is an Agricultural based POS system that enables multi branch management, product based expiry tracking and numerous tasks convenient while obeying an offline first architecture

## Desktop UI

Requires JDK 21 or newer and Maven 3.9 or newer.

```powershell
mvn clean package
mvn javafx:run
```

The JavaFX frontend reproduces the supplied general and role-specific login references, with a resizable 50/50 desktop layout, role selection, password visibility. Choose a role and click Sign in to preview that role's login presentation. All fields are optional; this is a frontend-only prototype.

Authentication is not connected. No credentials are stored or verified, and the role login does not grant access. The admin dashboard remains pending its design reference; the supplied images show login screens only. Colours and measurements are approximated from the low-resolution screenshots until the Figma source is accessible.
