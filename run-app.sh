#!/bin/bash

function run_api() {
    CURRENT_DIR=$(pwd)
    cd "$(dirname "$0")/API" || exit 1

    ./run-api.sh
    API_STATUS=$?

    while [ $API_STATUS -ne 0 ]; do
        echo "API failed to start. Retrying..."
        ./run-api.sh
        API_STATUS=$?

        if [ $API_STATUS -ne 0 ]; then
            echo "API failed to start after retrying. Exiting..."
            exit 1
        fi
    done

    echo "API started successfully."
}

run_api
API_STATUS=$?

if [ $API_STATUS -eq 0 ]; then
    echo "Starting the Android app in emulator..."

    # Set Android SDK paths
    export ANDROID_HOME=~/Library/Android/sdk
    export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools

    # Step 1: Start the emulator (adjust emulator name as needed)
    EMULATOR_NAME="Pixel_8_API_28"
    echo "Starting emulator: $EMULATOR_NAME..."
    $ANDROID_HOME/emulator/emulator -avd "$EMULATOR_NAME" -netdelay none -netspeed full &
    EMULATOR_PID=$!

    # Wait for emulator to fully boot up
    echo "Waiting for the emulator to start..."
    BOOTED=0
    RETRIES=30
    while [ $BOOTED -eq 0 ] && [ $RETRIES -gt 0 ]; do
        if adb shell getprop sys.boot_completed | grep -q "1"; then
            BOOTED=1
        else
            echo "Emulator is still booting..."
            sleep 5
            RETRIES=$((RETRIES - 1))
        fi
    done

    if [ $BOOTED -eq 0 ]; then
        echo "Emulator failed to boot. Exiting..."
        kill $EMULATOR_PID
        exit 1
    fi
    echo "Emulator started successfully."

    # Step 2: Build the app
    echo "Building the app..."
    cd app
    ./gradlew assembleDebug

    if [ $? -ne 0 ]; then
        echo "App build failed. Exiting..."
        exit 1
    fi
    echo "App built successfully."

    # Step 3: Install and run the app
    adb install app/build/outputs/apk/debug/app-debug.apk
    adb shell am start -n "bachelorThesis.app/bachelorThesis.app.MainActivity"

    echo "App is running on the emulator."
else
    echo "API failed to start. Exiting..."
    exit 1
fi
