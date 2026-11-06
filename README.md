# BesikJyry — Бесік жыры · Жұлдызды аспан

Android projector lullaby player with a procedural night sky.

- 20 constellation scenes in shuffled order, four minutes each
- Small Kazakh (Russian) label and constellation lines for the first 15 seconds
- Slowly fading background stars; fixed constellation stars
- Choose a folder of MP3 files once; random nonrepeating playback, rescanned between tracks
- D-pad LEFT/RIGHT changes both song and constellation
- Automatically finishes the Activity after 60 minutes, leaving the projector on
- Landscape immersive display; rendering targets 60 fps, subject to hardware

## Build
GitHub Actions > Build APK > Run workflow, or run `gradle assembleDebug` with Android SDK installed. Download the APK from the workflow artifacts.

On first launch, select a folder containing MP3s using the Android system picker. The app remembers permission. Press BACK to exit.

The project uses Android framework Canvas for the initial renderer and MediaPlayer for audio. No network access required.

## Automatic in-app updates from main

The app checks `https://api.github.com/repos/NurtazaAIB/BesikJyry/releases/tags/latest` at startup. If the published version is newer, Android DownloadManager downloads `BesikJyry.apk` **directly** and opens Android's system installer. No GitHub page is opened. Android requires confirmation for APK installation; silent installation is not possible for ordinary third-party apps.

**Important:** In-app updates require every release APK to be signed with the **same permanent signing key**. The workflow only publishes the update APK when these GitHub Actions repository secrets exist:

- `RELEASE_KEYSTORE_BASE64` — base64-encoded JKS keystore (single line)
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

Generate and securely retain the signing keystore yourself. Never commit it or its passwords to this public repository. Add the secrets at Settings → Secrets and variables → Actions. Once configured, each push to `main` builds a signed APK and replaces the asset of the `latest` GitHub Release. The GitHub Actions run number becomes Android `versionCode`, so installed apps can detect a newer build. The first projector installation must use the **signed release APK**, not the debug artifact, or Android will reject subsequent signed updates.

The current GitHub repository is public so the projector can download releases without a login. GitHub Releases must be reachable from the projector's network. Offline music and sky rendering continue if update checking fails.
