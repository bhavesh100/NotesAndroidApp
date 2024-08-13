# Notes Android App

A simple Android app for creating, updating, and managing notes using Kotlin and Firebase.

## Features

- Google Sign-In for user authentication
- Create and update notes with title and content
- Validation for empty title or content fields
- Local storage using SQLite

## Screens

- Google Sign-In Screen
- Notes List Screen
- Add Note Screen
- Update Note Screen

## Setup

### Prerequisites

- Android Studio
- Firebase Project with `google-services.json`

### Dependencies

```gradle
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.firebase.bom)
    implementation(libs.play.services.base)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```
## Installation

  1. Clone the repository.
  2. Open in Android Studio.
  3. Add google-services.json to the app directory.
  4. Sync Gradle and run the app.

## License

For educational purposes only.
