#!/bin/bash

ZOOKEEPER_DATA_DIR="./zookeeper/data"
ZOOKEEPER_LOG_DIR="./zookeeper/data/log"
ZOOKEEPER_EXTERNAL_LOG_DIR="/var/log/zookeeper"
KAFKA_DATA_DIR="./kafka/data"

# Create internal Zookeeper directories if they don't exist
if [ ! -d "$ZOOKEEPER_DATA_DIR" ]; then
    echo "Creating Zookeeper data directory..."
    mkdir -p "$ZOOKEEPER_DATA_DIR"
fi

if [ ! -d "$ZOOKEEPER_LOG_DIR" ]; then
    echo "Creating Zookeeper log directory..."
    mkdir -p "$ZOOKEEPER_LOG_DIR/version-2"
fi

# Set permissions for Zookeeper directories
echo "Setting permissions for Zookeeper directories..."
sudo chmod -R 777 "$ZOOKEEPER_DATA_DIR"
sudo chmod -R 777 "$ZOOKEEPER_LOG_DIR"

# Create external log directory for Zookeeper logs
if [ ! -d "$ZOOKEEPER_EXTERNAL_LOG_DIR" ]; then
    echo "Creating external Zookeeper log directory..."
    sudo mkdir -p "$ZOOKEEPER_EXTERNAL_LOG_DIR"
fi

# Set permissions for the external log directory
sudo chmod -R 777 "$ZOOKEEPER_EXTERNAL_LOG_DIR"

# Ensure Kafka has permissions to write to its data directory
if [ -d "$KAFKA_DATA_DIR" ]; then
    echo "Setting ownership and permissions for Kafka data directory..."
    sudo chown -R 1000:1000 "$KAFKA_DATA_DIR"
    sudo chmod -R 775 "$KAFKA_DATA_DIR"
else
    echo "Kafka data directory does not exist, creating now..."
    mkdir -p "$KAFKA_DATA_DIR"
    sudo chown -R 1000:1000 "$KAFKA_DATA_DIR"
    sudo chmod -R 775 "$KAFKA_DATA_DIR"
fi

# Clean up old containers and volumes
echo "Cleaning up old containers and volumes..."
docker compose down -v --remove-orphans

# Start Docker containers
echo "Starting Docker containers with Docker Compose..."
docker compose up --build

echo "Docker environment configured and containers started successfully."
