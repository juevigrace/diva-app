# Diva KMP Project

A Kotlin Multiplatform project template that demonstrates the default way of building cross-platform applications using the Diva framework.

## Overview

This is a template project that shows how to structure and build a Kotlin Multiplatform application with:
- Android support
- iOS support  
- Desktop support
- Web support

## Building the Project

### Prerequisites
- JDK 8 or higher
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Build Commands

```bash
# Build all platforms
./gradlew build

# Build for Android
./gradlew assembleDebug

# Build for Desktop
./gradlew createDistributable

# Run tests
./gradlew test
```

## Default Structure

This project follows the standard Kotlin Multiplatform conventions with shared code organized by platform targets.

## Getting Started

1. Clone this template
2. Customize package names and dependencies as needed
3. Build and run for your target platforms

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.