#!/bin/bash

echo "Building Expense Tracker APK..."
echo

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo
    echo "✅ Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo
    echo "To install on device:"
    echo "adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo
    echo "❌ Build failed!"
    echo "Please check the error messages above."
fi
