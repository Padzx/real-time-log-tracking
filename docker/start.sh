#!/bin/bash
# sh script required for docker compose startup with Kafka and Zookeeper

ZOOKEEPER_DATA_DIR="./zookeeper/data"
ZOOKEEPER_LOG_DIR="./zookeeper/data/log"
ZOOKEEPER_EXTERNAL_LOG_DIR="/var/log/zookeeper"
KAFKA_DATA_DIR="./kafka/data"
PERMISSION_LEVEL=775
PERMISSION_OWNER=1000:1000

check_error() {
  if [ $? -ne 0 ]; then
    echo "Error executing the previous command. Ending the script."
    exit 1
  fi
}

if [ ! -d "$ZOOKEEPER_DATA_DIR" ]; then
    echo "Creating Zookeeper data directory..."
    mkdir -p "$ZOOKEEPER_DATA_DIR"
    check_error
fi

if [ ! -d "$ZOOKEEPER_LOG_DIR/version-2" ]; then
    echo "Creating Zookeeper log directory (version-2)..."
    mkdir -p "$ZOOKEEPER_LOG_DIR/version-2"
    check_error
fi

echo "Configuring permissions for Zookeeper directories..."
chmod -R $PERMISSION_LEVEL "$ZOOKEEPER_DATA_DIR"
check_error
chmod -R $PERMISSION_LEVEL "$ZOOKEEPER_LOG_DIR"
check_error

if [ ! -d "$ZOOKEEPER_EXTERNAL_LOG_DIR" ]; then
    echo "Creating external Zookeeper log directory..."
    mkdir -p "$ZOOKEEPER_EXTERNAL_LOG_DIR"
    check_error
fi
chmod -R $PERMISSION_LEVEL "$ZOOKEEPER_EXTERNAL_LOG_DIR"
check_error

if [ -d "$KAFKA_DATA_DIR" ]; then
    echo "Configuring Kafka Data Directory Permissions and Ownership..."
else
    echo "Kafka data directory does not exist, creating now..."
    mkdir -p "$KAFKA_DATA_DIR"
    check_error
fi

chown -R $PERMISSION_OWNER "$KAFKA_DATA_DIR"
check_error
chmod -R $PERMISSION_LEVEL "$KAFKA_DATA_DIR"
check_error

echo "Cleaning old containers and volumes..."
docker compose down -v --remove-orphans
check_error

echo "Starting Docker containers with Docker Compose..."
docker compose up --build
check_error

echo "Docker environment configured and containers started successfully."
cle