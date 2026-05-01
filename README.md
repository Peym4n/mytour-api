# MyTour API

A Spring Boot backend application for the MyTour project.

## Running Locally with Docker Compose

The easiest way to run the project locally is by using Docker Compose. The compose setup starts PostgreSQL, the Spring Boot API, and the Angular frontend.

### Prerequisites
* [Docker](https://docs.docker.com/get-docker/) installed and running.
* [Docker Compose](https://docs.docker.com/compose/install/) (usually included with Docker Desktop).

### Setup

1. **Environment Variables:** 
   Copy the example environment file to create your local configuration:
   ```bash
   cp .env.example .env
   ```
   *(Note: On Windows Command Prompt, use `copy .env.example .env`)*
   
   If needed, open `.env` and adjust the ports or allowed origins:
   * `APP_PORT` exposes the backend API, default `8080`.
   * `UI_PORT` exposes the Angular frontend, default `4200`.
   * `CORS_ALLOWED_ORIGINS` should include the frontend origin, for example `http://localhost:4200`.

2. **Start the Application:**
   Run the following command from the `mytour-api` directory:
   ```bash
   docker compose up --build
   ```
   * The `--build` flag ensures that the backend and frontend images are built with your latest code changes.
   * To run it in the background, you can add the `-d` flag: `docker compose up -d --build`.

3. **Access the Application:**
   Once the database, API, and frontend containers are running and healthy, the app will be accessible at:
   * **Frontend:** `http://localhost:4200`
   * **Base URL:** `http://localhost:8080`
   * **Health Check:** `http://localhost:8080/actuator/health`
   * **Swagger UI / OpenAPI docs:** `http://localhost:8080/swagger-ui.html` (if springdoc is configured)

The frontend service waits for the backend health check before starting.

### Stopping the Application

To stop the containers and remove them, press `Ctrl+C` in your terminal (if running in the foreground), or run:
```bash
docker compose down
```
*(Add `-v` if you also want to delete the local database volume and start fresh next time).*
