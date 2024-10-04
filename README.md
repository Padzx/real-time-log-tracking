# Real Time Log Tracking 

## Summary

1. [Introduction](#introduction)
2. [Technologies](#technologies)
3. [Microservices](#microservices)
4. [Monitoring](#monitoring)
5. [How to Run](#how-to-run)


## Introduction

This project aims to build a robust and scalable system for real time log ingestion, processing, and monitoring. Designed for large-scale production environments, the system emphasizes resilience, scalability, and automation. Using modern technologies such as Apache Kafka and Spring Boot, the platform ensures efficient log processing, secure storage, and real-time monitoring, and is easy to extend and deploy.

Key technologies include Apache Kafka for log streaming, Prometheus and Grafana for monitoring and visualization, and Docker for orchestrating containerized services.


## Technologies

This project leverages modern technologies to ensure scalability, flexibility and ease of maintenance. Below is a list of the main technologies and their respective versions used in the system:

- **Spring Boot** (v3.3.3): The core framework used for building microservices in this project. Spring Boot provides a production-ready environment with embedded servers and simplifies application configuration.

- **Gradle** (v8.9): A modern build automation tool used for dependency management and building the project. The project is structured as a multi-module application, with each microservice having its own `build.gradle.kts` file.

- **Kotlin Script (KTS)**: Used for the Gradle build scripts, providing a type-safe way to configure and manage the project dependencies, especially in a multi-module setup. Each microservice in the project has its individual build configuration using Kotlin DSL (`build.gradle.kts`).

### Docker Compose Stack

The docker-compose.yml file orchestrates a stack of essential services for the real-time log ingestion, processing and monitoring system. It defines the configuration of each service, including volumes, networks, and dependencies between them. Below is a brief summary of each service defined in the stack:

**Services**

**Zookeeper**

zookeeper: ``3.8.4 version``
Responsible for managing and coordinating Apache Kafka.

- Exposes the necessary ports for communication and monitoring.
- Includes JMX Exporter configuration to expose metrics to Prometheus.
- Volumes: Maps configuration and data files for persistence.

**Kafka**

confluentinc/``cp-kafka:7.5.0 version``
Main service for streaming logs.

- Connected to Zookeeper for coordination.
- Exposes metrics with JMX Exporter for monitoring via Prometheus.
- Volumes: Includes property settings and persistent logs.

**Logstash**

docker.elastic.co/logstash/``logstash:8.5.0 version``

It acts as a log ingestion pipeline, processing data from various sources.

- Receives logs via Kafka and processes according to the pipeline configuration.
- Exposed on port 5044 for data entry and 9600 for monitoring.

**Postgres**

postgres:``15 version``
Database used to store processed logs and metadata.

- Exposes port 5432 and includes a persistent volume for data.
- Includes an initialization SQL script.
- Healthcheck configured to check if the bank is ready to receive connections.

**Prometheus**

prom/``prometheus:v2.48.0 version``
Monitoring tool used to collect metrics from services including Kafka and Zookeeper.
- Configured with a prometheus.yml file that defines the sources of metrics and alerts.
- Exposed on port 9090 for viewing metrics and configuration.

**Grafana**

grafana/``grafana:10.1.1 version``
Used to create dashboards and graphical visualizations of metrics collected by Prometheus.

- Exposed on port 3000 for access to the dashboard interface.
- Includes automatic provisioning of dashboards configured for Kafka and Zookeeper monitoring.

## Microservices

### Log Ingestion

The Log Ingestion Service is responsible for receiving logs from various sources, such as monitoring systems, Logstash or custom applications, and publishing them to topics in Apache Kafka.
The service ensures the resilience and scalability of the data flow by acting as an intermediate bridge for subsequent processing.

**Interaction with Kafka:**

Consume data from different external sources, applying transformations when necessary, such as filtering out irrelevant information or enriching logs with additional metadata.
Publish the processed logs in Kafka topics, organizing them according to criteria such as log type, origin or severity.

### Log Processor

The Log Processor Service consumes logs from Kafka topics and performs processing operations such as analysis, filtering, aggregation, and data transformation.
The service prepares logs for storage or indexing, implementing logic to identify patterns, anomalies, or critical events that require special handling, such as immediate alerts.
Interaction with Kafka:

Consumes Kafka topic logs, enabling large-scale and efficient processing.
You can publish the processed data back to new Kafka topics or send it directly to final storage, depending on the chosen architecture.

**Database:**

Stores processed logs in PostgreSQL, ensuring that data is organized and indexed to facilitate future queries.
It supports both real-time processing (streaming) and batch processing, according to operational needs.

## Monitoring

The monitoring of the system is handled using **Prometheus** for metric collection and **Grafana** for visualization. These tools help ensure that the system remains resilient and scalable, providing insights into the health and performance of each service in the stack.

### Prometheus

- **Prometheus** is configured to scrape metrics from services like Kafka and Zookeeper using the **JMX Exporter**.
- Prometheus is also set up to monitor custom alerts defined in the configuration files.

### Grafana

- **Grafana** is used to visualize the metrics collected by Prometheus. It comes pre-configured with dashboards to display critical information regarding Kafka, Zookeeper, and other services.

### Ports Overview

Below is a summary of the ports exposed by each service for monitoring purposes:

| Service       | Port  | Description                       |
|---------------|-------|-----------------------------------|
| Zookeeper     | 7000  | JMX Exporter for Zookeeper metrics |
| Kafka         | 7071  | JMX Exporter for Kafka metrics     |
| Prometheus    | 9090  | Prometheus metrics and UI         |
| Grafana       | 3000  | Grafana UI                        |
| Logstash      | 9600  | Logstash monitoring API           |

### Accessing Monitoring Tools

- **Prometheus** UI: [http://localhost:9090](http://localhost:9090)
- **Grafana** UI: [http://localhost:3000](http://localhost:3000)

Grafana is pre-configured with the default credentials:
- **Username**: `admin`
- **Password**: `admin`


## How to Run

To execute the project it is necessary to understand some points:

The project was developed in multi modules, each module has its own build.gradle, so we can run it in isolation and provision dependencies in an organized way within each module, the existing modules are:

``./gradlew :log-processor:``
``./gradlew :log-ingestion:``

The project has unit tests and integration tests, for efficient development and familiarizes with good development practices:

````.
├── java
│   └── org
│       └── example
│           ├── integration
│           │   └── service
│           │       └── LogConsumerIntegrationTest.java
│           ├── LogProcessorApplicationTest.java
│           └── unit
│               ├── dto
│               │   └── LogRecordTest.java
│               └── services
│                   ├── LogConsumerServiceTest.java
│                   ├── LogProcessingServiceTest.java
│                   ├── LogProducerServiceTest.java
│                   └── LogServiceTest.java
└── resources
    ├── application-test-integration.yml
    └── application-test.yml
````
To run the integration tests and generate reports, follow these steps:

``./gradlew :log-processor:UnitTest --info`` for unit tests.

``./gradlew :log-processor:IntegrationTest --info`` for integration tests.

The unit tests are also being executed in a [CI Workflow](#https://github.com/Padzx/real-time-log-tracking/actions) for each change made explicitly, the integration tests are being executed locally.

### Running the application locally

To be running locally and interacting with the services:

``./gradlew bootRun`` for run application.

You can be interacting with de application sending an example log for endpoint of Log Ingestion:

Interacting with the Application:

```
POST http://localhost:8080/api/logs/json
Content-Type: application/json

{
  "timestamp": "2024-10-01T15:30:00Z",
  "level": "INFO",
  "message": "Log message example",
  "source": "my-application",
  "thread": "main-thread",
  "logger": "com.example.MyClass"
}
```

``Note:`` To view the results of persisting Log Processor log data using PostgreSQL.

```
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container_name_or_id>
```

```
psql -h <container_ip> -U admin -d logdb
```

Accessing the database, you may be viewing the logs in the processed_logs table, the result will be similar to this:

```
 id |       timestamp        | level |      message       | source | thread |      logger       |      processed_timestamp      |  category   |  status   
----+------------------------+-------+--------------------+--------+--------+-------------------+-------------------------------+-------------+-----------
  1 | 2024-09-19 10:00:00+00 | INFO  | This is a test log | my-app | main   | com.example.MyApp | 2024-09-24 02:21:03.811127+00 | Information | processed
  2 | 2024-09-19 10:00:00+00 | INFO  | This is a test log | my-app | main   | com.example.MyApp | 2024-09-24 02:21:30.153044+00 | Information | processed

```
