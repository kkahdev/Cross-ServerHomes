# RedisHomes

A cross-server home system for Minecraft networks using Velocity (Proxy) and Paper (Backend).
It utilizes Redis Pub/Sub for instant server switching and coordinate transfer, with MySQL/MariaDB for persistent storage.

## Tech Stack
*   **Java:** 21
*   **Build System:** Gradle 8 (Kotlin DSL)
*   **Proxy:** Velocity 3.3.0+
*   **Backend:** Paper 1.21+ (Folia supported)
*   **Database:** HikariCP (MySQL/MariaDB)
*   **Messaging:** Lettuce (Redis)
*   **Commands:** CommandAPI

## Project Structure

The project is a multi-module Gradle build:

| Module | Description |
| :--- | :--- |
| **`common`** | Shared Data Models (`Home`, `TeleportRequest`) and Database logic. Compiled into both plugins. |
| **`velocity`** | The Proxy plugin. Handles `/home` routing, checks DB, and sends Redis teleport requests. |
| **`paper`** | The Backend plugin. Handles `/sethome`, `/delhome`, and listens for incoming Redis teleport requests. |

## Build Instructions

1.  Clone the repository.
2.  Run the shadow build task:
    ```bash
    ./gradlew clean shadowJar
    ```
3.  Artifacts will be generated in:
    *   `velocity/build/libs/velocity-1.0.0.jar`
    *   `paper/build/libs/paper-1.0.0.jar`

## Setup & Configuration

### Prerequisites
*   Redis Server (Default: `localhost:6379`)
*   MySQL or MariaDB Database (Default: `localhost:3306`)

### Installation
1.  **Velocity:** Place the Velocity JAR in the proxy `plugins/` folder.
2.  **Paper:** Place the Paper JAR in the `plugins/` folder of **every** backend server.

### Database Schema
The plugin will attempt to create the table automatically. If you need to manually create it:

```sql
CREATE TABLE IF NOT EXISTS homes (
    owner_uuid VARCHAR(36) NOT NULL,
    home_name VARCHAR(32) NOT NULL,
    server_id VARCHAR(32) NOT NULL,
    world_name VARCHAR(64) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    yaw FLOAT NOT NULL,
    pitch FLOAT NOT NULL,
    PRIMARY KEY (owner_uuid, home_name)
);
Protocol (Redis)

Channel: redishomes:teleport

The proxy publishes a JSON payload when a player requests a home. The target server listens and processes the teleport if the player is connecting or already online.

Payload Example:

code
JSON
download
content_copy
expand_less
{
  "playerUuid": "550e8400-e29b-41d4-a716-446655440000",
  "serverId": "survival_1",
  "worldName": "world",
  "x": 100.5,
  "y": 64.0,
  "z": -250.5,
  "yaw": 90.0,
  "pitch": 0.0
}
Developer Notes

Hardcoded Credentials: In the current skeleton, database and Redis credentials are located in ProxyStorageService.java (Velocity) and BackendStorageService.java (Paper). You should externalize these to a config.yml before production use.

Server IDs: The backend currently defaults to a hardcoded ID or the server.properties name. Ensure your servers have unique identifiers in the database.
