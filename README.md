# Mini CRM Assignment 

A small, end‑to‑end **mini CRM** that stores and shows customers/orders. It includes:

* **Kotlin Android app** (`android-crm`) — create/update customers & orders, local Room cache, two way sync(Firebase&Room).
* **Flutter app** (`flutter-crm-viewer`) — a read‑only viewer that lists the same data (Firebase or mock API).

> **Heads‑up about the API in the assignment**
>
> The API link suggested in the original assignment wasn’t working when I built this. So for demo/testing I used **JSONPlaceholder** ([https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com)). It’s a public mock API and is perfect for a quick prototype.
>
> **Mapping I used**
>
> * **Customers** ← `GET /users`
> * **Orders/Notes** ← `GET /posts?userId=<id>` (or `GET /todos?userId=<id>` if you prefer tasks)
> * Common fields used: `id`, `name`, `email`, `phone`, `company.name`, `address.city`, `title`, `body`.

---

## Repo layout

```
mini-crm-assignment/
├─ android-crm/           # Kotlin Android app (Room + optional Firebase sync)
└─ flutter-crm-viewer/    # Flutter viewer app (Firebase or JSONPlaceholder)
```

---

## Quick start (fastest path)

If you just want to **run both apps with JSONPlaceholder** (no Firebase needed):

1. **Clone**

```bash
git clone https://github.com/imcoolthanyou/mini-crm-assignment.git
cd mini-crm-assignment
```

2. **Run Android app** (Kotlin)

* Open `android-crm/` in **Android Studio** → *Run*  (choose emulator/device).
* Or CLI (Windows):

  ```bat
  cd android-crm
  .\gradlew clean assembleDebug
  adb install -r app\build\outputs\apk\debug\app-debug.apk
  ```
* Or CLI (macOS/Linux):

  ```bash
  cd android-crm
  ./gradlew clean assembleDebug
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  ```

3. **Run Flutter app** (viewer)

```bash
cd flutter-crm-viewer
flutter pub get
flutter run
```

Pick your device (Android emulator, iOS simulator, Chrome, etc.).

> **Note**: Both apps default to JSONPlaceholder in this demo. If you point them to Firebase later, see the Firebase notes below.

---

## Android (Kotlin) app — detailed

### Requirements

* Android Studio (latest stable)
* Android SDK Platform 34+ (or the version in `android-crm` Gradle files)
* A device/emulator on API 24+ (Android 7.0+) recommended

### Data sources

* **Primary (demo)**: JSONPlaceholder — `https://jsonplaceholder.typicode.com`
* **Optional**: Firebase (Firestore/Realtime DB) for real‑time sync

### Configure base URL (for JSONPlaceholder or your backend)

Most Android projects keep this in one of the following places:

* `app/src/main/res/values/strings.xml` (a `base_url` string), or
* a `Constants.kt` / `ApiConstants.kt` under `app/src/main/java/.../`.

> **What to do:** Use your editor’s search (**Ctrl/Cmd+Shift+F**) for `baseUrl` or `jsonplaceholder` inside `android-crm`. Set it to:

```
https://jsonplaceholder.typicode.com
```

If you switch to your real backend later, replace the URL and keep the same mapping (users ↔ customers, posts/todos ↔ orders/notes).

### Run from Android Studio

1. **Open** the `android-crm` folder in Android Studio.
2. Let Gradle sync; accept any SDK prompts.
3. *Run* ▶ the `app` configuration.

### Run from command line

```bash
cd android-crm
# macOS/Linux
./gradlew clean installDebug
# Windows
gradlew.bat clean installDebug
```

This will build and deploy to the connected device/emulator.

### Firebase (optional)

If you want real‑time sync instead of just JSONPlaceholder:

1. Create a Firebase project.
2. Add an **Android app** and download `google-services.json`.
3. Put `google-services.json` in `android-crm/app/`.
4. Enable Firestore (or Realtime Database) in Firebase Console.
5. Rebuild the app. Data changes should now sync via Firebase in addition to the local Room cache.

> If you don’t add Firebase config, the app still runs using local Room + JSONPlaceholder.

---

## Flutter viewer app — detailed

### Requirements

* Flutter SDK 3.x
* Dart 3.x
* Xcode (for iOS) or Android Studio (for Android), or just run on Chrome for web

### Data sources

* **Demo**: JSONPlaceholder — reads `/users` and `/posts?userId=<id>` to render lists and details
* **Optional**: Firebase — if you want to read the same Firestore data created by the Android app

### Configure API / Firebase

* Look for `baseUrl` or `apiClient` in `lib/` (typical spots: `lib/core/config.dart`, `lib/services/api/`, or `lib/data/remote/`).
* Set the base URL to:

```
https://jsonplaceholder.typicode.com
```

* For Firebase usage instead, run:

```bash
flutterfire configure
```

This will generate `firebase_options.dart`. Also add `google-services.json` (Android) or `GoogleService-Info.plist` (iOS) if you build for mobile.

### Run

```bash
cd flutter-crm-viewer
flutter pub get
flutter run
```

* To run in Chrome (web): `flutter run -d chrome`
* To pick a device interactively: just run `flutter run` and choose from the list.

--

## Why JSONPlaceholder?

The assignment’s API endpoint wasn’t reachable during development. JSONPlaceholder is:

* free, public, stable,
* returns realistic shapes for users/posts,
* great for wiring UI and persistence quickly.
---

## License

MIT (feel free to use/modify for learning and demos).
