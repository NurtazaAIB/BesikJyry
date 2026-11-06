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
