# 🎈 Endless Balloon Protector

[![Android APK CI](https://github.com/VenZ9/antigravity-ballon/actions/workflows/android.yml/badge.svg)](https://github.com/VenZ9/antigravity-ballon/actions/workflows/android.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.10.01-green.svg)](https://developer.android.com/jetpack/compose)

A complete, polished, and charming endless arcade sky runner built natively for Android using Jetpack Compose and custom Canvas rendering. Guide your high-flying hot air balloon through dynamic skies, dodge obstacles, collect sparkling coins, and unlock original balloon skins through a fair, in-game gacha machine!

---

## 🎮 Core Gameplay & Features

- **Continuous Upward Flight**: Navigate through an endless sky featuring multi-layer parallax clouds, atmospheric sun glare, and dynamic altitude markers.
- **Natural Fluid Drag Steering**: Full-screen freeform touch dragging with responsive delta-time movement and realistic horizontal banking tilt (with smooth upright recovery when idle).
- **Varied Hazard Ecosystem**:
  - ☁️ **Storm Clouds**: Puffy hazards with darkened undersides and storm eyes.
  - 🐦 **Sky Sparrows**: Migrating birds with animated flapping wings and horizontal wave patterns.
  - 🔮 **Crystal Spikes**: Rotating geometric spike mines with sharp crystalline tips.
  - ⭐ **Spun Stars**: Rapidly spinning star hazards trailing ember particles.
  - ☄️ **Solar Comets**: Fiery swooping meteors with radiant color gradient tails.
  - ⚡ **Electric Lightning**: Crackling electrical arcs traversing the upper troposphere.
  - 🛸 **Sky Drones**: Twin-rotor aerial drones with spinning blades and blinking LED sensors.
- **Fair Hitboxes & Heart Economy**: 3 lives represented as heart icons. Taking damage triggers 1.4 seconds of invulnerability with visual flashing, halo shields, and collision audio feedback.
- **Coin Trails & Collection Sparks**: Collect spinning 3D gold coins arranged in strategic flight corridors, triggering satisfying collection chimes and gold particle bursts.
- **Power-Up Boosts**:
  - **Star Shield**: Grants temporary hazard invincibility absorbing collisions without life loss.
  - **2× Multiplier**: Doubles every coin collected for 30 seconds.

---

## 🎨 Balloon Wardrobe & Skin Catalog

Collect **12 original, distinct balloon skins** with unique patterns, gradients, and custom visual rendering:

| Skin Name | Rarity | Pattern Style | Primary Colors | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Ruby Classic** *(Default)* | Common | Classic Swirl | Coral / Ruby Red | The timeless high-flying balloon known across every sky. |
| **Mint Breeze** | Common | Vertical Stripes | Mint Green / Aqua | Light, fresh, and aerodynamic. |
| **Citrus Burst** | Common | Polka Dots | Sunshine Yellow / Lime | Zesty sunshine energy in the high clouds. |
| **Sky Azure** | Common | Clean Stripes | Cerulean / Cloud White | Blends harmoniously into the endless azure sky. |
| **Sunset Glow** | Rare | Twilight Swirl | Tangerine / Warm Coral | Bathed in the warm twilight of golden hour. |
| **Violet Dream** | Rare | Starlight | Lavender / Amethyst | Speckled with twinkling sky dust. |
| **Arctic Frost** | Rare | Alpine Stripes | Ice Blue / Silver White | Cooled by alpine winds with crystal reflections. |
| **Emerald Gem** | Rare | Polka Dots | Jade / Forest Emerald | Deep forest vitality and good fortune. |
| **Cosmic Nebula** | Epic | Starlight Flecks | Deep Indigo / Violet | Forged in the outer stratosphere with galactic dust. |
| **Solar Flare** | Epic | Flame Crest | Crimson / Amber Gold | Surging with fiery currents and blazing updrafts. |
| **Cyber Neon** | Epic | Cyber Grid | Neon Cyan / Obsidian | High-tech synthwave aesthetic with pulsing neon wireframes. |
| **Royal Celestial** | Legendary | Celestial Rings | Imperial Gold / Velvet | Adorned with pure gold and orbiting cosmic rings. |

---

## 🎰 Sky Gacha Machine (100% In-Game Economy)

The lucky capsule wheel lets players spend in-game coins earned strictly through flight:
- **Pull Cost**: `50 coins` per spin.
- **Transparent Probabilities**:
  - **Common**: 50%
  - **Rare**: 35%
  - **Epic**: 12%
  - **Legendary**: 3%
- **Fair Duplicate Protection**: Duplicates are never wasted! Rolling an already-owned skin grants a generous coin refund based on rarity (up to 50 coins, ensuring zero coin loss on Legendary duplicates).
- **Zero Real-Money / Microtransactions**: No external dependencies, ads, paid currency, or loot-box monetization.

---

## 🎵 Original Audio Design

- **Background Music**: Original, catchy, lightweight 8-bar looping soundtrack (`bgm_playful.wav`) with walking bass and bright lead flute synth.
- **Low-Latency SFX**:
  - Coin pickup chime (`coin.wav`)
  - Hazard impact bump (`bump.wav`)
  - Snappy button clicks (`click.wav`)
  - Celebratory skin unlock fanfare (`unlock.wav`)
  - Gentle game over sequence (`gameover.wav`)
- **Independent Controls**: Separate music and sound-effect toggles in Settings.
- **Lifecycle Safety**: Audio automatically pauses on app pause or backgrounding, resuming on return. Missing audio cannot crash the game.

---

## 📁 Project Architecture

The project is structured into a clean modular architecture:

```
antigravity-ballon/
├── .github/workflows/
│   └── android.yml                # CI workflow building Debug APK & running tests
├── core/                          # Pure Kotlin JVM module (no Android dependencies)
│   ├── src/main/kotlin/
│   │   └── com/venz29/antigravityballon/core/
│   │       ├── models/            # Domain models (Obstacle, Coin, Hitbox, Skin, Rarity)
│   │       ├── engine/            # GameEngine, CollisionDetector, ObstacleSpawner, GachaEngine
│   │       └── storage/           # SaveData domain model and validation rules
│   └── src/test/kotlin/           # Comprehensive Unit Test Suite (100% pass)
└── app/                           # Android Jetpack Compose application module
    ├── src/main/java/
    │   └── com/venz29/antigravityballon/
    │       ├── MainActivity.kt    # Entry activity & window insets configuration
    │       ├── audio/             # SoundPool & MediaPlayer controller
    │       ├── storage/           # Android SharedPreferences persistence layer
    │       └── ui/
    │           ├── BalloonGameApp.kt
    │           ├── canvas/        # High-performance 2D Canvas renderer
    │           └── screens/       # MainMenu, GamePlayHUD, Wardrobe, Gacha, Pause, GameOver, Settings
    └── src/main/res/              # Drawables, launcher icons, colors, strings, raw audio
```

---

## 🧪 Testing

The `:core` module contains comprehensive JUnit test suites verifying:
1. `GachaEngineTest`: Tests insufficient coin rejection, valid rolls, deterministic seed repeatability, statistical rarity distribution, and duplicate refund calculations.
2. `CollisionDetectorTest`: Circle overlap math, generous coin pickup radius, and fair hazard collision thresholds.
3. `GameEngineTest`: Start state, drag and tilt dynamics, delta-time updates, invulnerability periods, shield absorption, and game-over state transitions.
4. `SaveDataTest`: Corrupted data restoration, coin transaction math, and skin inventory validation.

To run tests locally:
```bash
./gradlew test
```

---

## 📦 How to Download the Android APK

1. Go to the [GitHub Actions tab](https://github.com/VenZ9/antigravity-ballon/actions).
2. Select the latest completed run of the **Build Endless Balloon Protector APK** workflow.
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Download the `antigravity-ballon-debug-apk` zip archive.
5. Extract `app-debug.apk` and transfer it to your Android device to install and play!

---

## 🛠 Local Development & Building

### Requirements
- JDK 17
- Android SDK 35 (Platform 35, Build Tools 35.0.0)
- Gradle 8.11+ (included via Gradle Wrapper)

### Build Debug APK locally
```bash
# Clone the repository
git clone https://github.com/VenZ9/antigravity-ballon.git
cd antigravity-ballon

# Build debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

This project is licensed under the permissive [MIT License](LICENSE).
