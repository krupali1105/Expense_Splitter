@echo off
echo Building Expense Tracker APK...
echo.

REM Clean previous builds
echo Cleaning previous builds...
call gradlew clean

REM Build debug APK
echo Building debug APK...
call gradlew assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on device:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ❌ Build failed!
    echo Please check the error messages above.
)

pause
