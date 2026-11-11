# CoolOS v1.0 — Android Launcher Prototype

CoolOS is a dark + blue Android launcher prototype inspired by the broad visual directions of OriginOS, ColorOS and the clean, translucent look of early iOS. It is an independent project and is not affiliated with those brands.

## Target device
- Vivo T4 5G
- Android 16
- Minimum Android: 8.0 (API 26)
- Compile SDK: 36
- Target SDK: 35

## Current features
- Home screen with clock, date and blue glass-style card
- App drawer with search
- Launch installed apps
- Basic CoolOS settings screen
- Android HOME intent so it can be selected as the default launcher
- GitHub Actions workflow to build a debug APK

## Build APK with GitHub Actions
1. Create a new GitHub repository.
2. Upload the contents of this folder to the repository root.
3. Open the **Actions** tab.
4. Select **Build CoolOS APK** and press **Run workflow**, or push to `main`.
5. Wait for the workflow to finish.
6. Open the completed workflow run, find **Artifacts**, and download `CoolOS-v1-debug`.
7. Extract the ZIP and install `app-debug.apk` on your phone.

## Use it on your Vivo
1. Install the APK.
2. Press Home.
3. If Android asks which Home app to use, select **CoolOS**.
4. To switch back, open Settings → Apps → Default apps → Home app and select the Vivo system launcher. The exact menu wording may vary.

## Important limitations
This is a launcher APK, not a replacement firmware or custom ROM. It cannot independently replace the system lock screen, fingerprint interface, notification shade, protected quick settings, system animations, or Android framework. Those require privileged system integration and a device-specific ROM build. The current version does not request Accessibility, notification, contacts, location, or other sensitive permissions.

## Developer notes
The GitHub workflow uses the installed Gradle command on the runner and JDK 17. No Gradle wrapper is included in this starter package. For reproducible local builds, generate and commit a Gradle wrapper using a compatible Gradle 8.13 installation.
