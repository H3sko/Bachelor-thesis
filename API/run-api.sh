#!/bin/bash

# Lib

function is_docker_running() {
    if docker info > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

function is_docker_container_running() {
    echo "Starting Docker container postgresdb..."

    if ! docker info > /dev/null 2>&1; then
        echo "Docker is not running. Please start Docker first."
        return 1
    fi

    if docker ps --filter "name=postgresdb" --filter "status=running" --format "{{.Names}}" | grep -q '^postgresdb$'; then
        echo "Docker container 'postgresdb' is already running."
        return 0
    fi

    if [ -f "./db/start-db.sh" ]; then
        echo "Executing db/start-db.sh to start the database..."
        ./db/start-db.sh
        if [ $? -ne 0 ]; then
            echo "Failed to execute start-db.sh. Exiting..."
            return 1
        fi
    else
        echo "start-db.sh not found. Starting container directly using Docker..."
        docker run --name postgresdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -d postgres
        if [ $? -ne 0 ]; then
            echo "Failed to start Docker container 'postgresdb'. Exiting..."
            return 1
        fi
    fi

    echo "Docker container 'postgresdb' started successfully."
}

function is_api_running() {
    echo "Starting ./gradlew run..."
    ./gradlew run &
    GRADLE_PID=$!

    sleep 5

    if ps -p $GRADLE_PID > /dev/null; then
        return 0
    else
        return 1
    fi
}

function shutdown_docker() {
    echo "Shutting down Docker..."

    docker ps -q | xargs -r docker stop
    pkill -f "Docker Desktop"

    echo "Docker has been shut down."
}


# Step 1: Start Docker

echo "Starting the application..."
echo "Starting Docker..."

open -ga Docker

echo "Waiting for Docker to start..."
MAX_RETRIES=20
RETRY_INTERVAL=6
retries=0
while is_docker_running && [ $retries -lt $MAX_RETRIES ]; do
    echo "Waiting for Docker to initialize..."
    sleep $RETRY_INTERVAL
    retries=$((retries + 1))
done

if [ $retries -ge $MAX_RETRIES ]; then
    echo "Docker failed to start after $MAX_RETRIES retries. Exiting..."
    shutdown_docker
    exit 1
fi
echo "Docker is running."
retries=0

# Step 2: Start the database

echo "Starting the database..."
while is_docker_container_running && [ $retries -lt $MAX_RETRIES ]; do
    echo "Waiting for the containers to start..."
    sleep 1
done

if [ $retries -ge $MAX_RETRIES ]; then
    echo "Containers failed to start after $MAX_RETRIES retries. Exiting..."
    shutdown_docker
    exit 1
fi
echo "Database started successfully."
retries=0

# Step 3: Start the API

echo "Starting the API"
sleep 7

if is_api_running; then
    echo "Backend is running successfully."
    return 0
else
    echo "Failed to start the backend. Exiting..."
    exit 1
fi
echo "API started successfully."