# App Cloner (Android)

Pick an installed app -> set a custom name and icon -> get a pinned home-screen shortcut.

By default the "clone" is a renamed/re-iconed shortcut to the original app (no data isolation).
For a real independent copy, plug in a virtualization engine (see below).

## Setup in VS Code

1. Install: JDK 17, Android SDK (command-line tools or Android Studio), and Gradle.
2. Set `ANDROID_HOME` to your SDK folder and add `platform-tools` to PATH.
3. Copy `local.properties.example` -> `local.properties` and fill in `sdk.dir`.
4. The Gradle wrapper is included - no need to install Gradle. Just use gradlew / gradlew.bat.
5. Open the folder in VS Code; install the recommended extensions.
6. Enable USB debugging on your phone, connect it, check with `adb devices`.
7. Run tasks (Ctrl+Shift+P -> "Tasks: Run Task"):
   - "Build debug APK"  -> app/build/outputs/apk/debug/app-debug.apk
   - "Install on device"

## Release APK for web hosting

    keytool -genkey -v -keystore my-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias mykey

Create `keystore.properties` in the project root:

    storeFile=../my-key.jks
    storePassword=YOUR_PASSWORD
    keyAlias=mykey
    keyPassword=YOUR_PASSWORD

Then run "Build release APK" -> app/build/outputs/apk/release/app-release.apk.
Upload it to your host, link it with `<a href="app-release.apk" download>`, serve over HTTPS.
Back up the keystore: you need the same key for every future update.

## Plugging in a real engine

1. Add a maintained VirtualApp-style library as a module/dependency (check its README).
2. Create `VirtualEngine : CloneEngine` in `CloneEngine.kt`'s package:
   - `init` -> the library's startup call
   - `install` -> install the package into a virtual user
   - `launch` -> start the app inside the virtual space
3. In `CloneEngine.kt` set `Engine.impl = VirtualEngine`.
4. Test on real devices; Google login, push, banking and DRM apps often fail.
