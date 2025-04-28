# Game Catalog Application REST API

This project is a REST API built with Spring Boot to serve Game data from a JSON file (`Games.json`). It allows users to query Games by Name, Publisher, or Available Platforms. This README provides a step-by-step guide to build, containerize, and deploy the application using Maven, Docker, and Kubernetes (with Kind for local development). Instructions are provided for both Windows and macOS/Linux users.

## Prerequisites

Before starting, ensure you have the following installed:

- **Java 17**: For building the Spring Boot application.
- **Maven**: For dependency management and building the JAR.
- **Docker**: For containerizing the application.
- **Kind**: For running a local Kubernetes cluster.
- **kubectl**: For interacting with Kubernetes.
- **curl** or **Postman**: For testing API endpoints.

### Installation (Windows)

For Windows, use **Chocolatey** (a package manager) or manual installation:

1. **Install Chocolatey** (optional, run in Admin PowerShell):
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
   ```

2. **Install Tools with Chocolatey**:
   ```powershell
   choco install openjdk17
   choco install maven
   choco install docker-desktop
   choco install kind
   choco install kubernetes-cli
   choco install curl
   ```

3. **Manual Installation**:
    - **Java 17**: Download from [Adoptium](https://adoptium.net/), set `JAVA_HOME` environment variable.
    - **Maven**: Download from [Apache Maven](https://maven.apache.org/download.cgi), add `bin` to `PATH`.
    - **Docker**: Install [Docker Desktop](https://www.docker.com/products/docker-desktop/), enable WSL 2 backend.
    - **Kind**: Download from [Kind releases](https://github.com/kubernetes-sigs/kind/releases), place in `C:\bin`.
    - **kubectl**: Download from [Kubernetes releases](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/), place in `C:\bin`.

4. **Set Up WSL 2** (recommended for Kind):
   ```powershell
   wsl --install
   wsl --update
   wsl --set-default-version 2
   ```

### Installation (macOS/Linux)

```bash
# Install Homebrew (macOS)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install tools
brew install openjdk@17
brew install maven
brew install docker
brew install kind
brew install kubectl
```
## Project Structure

```
Game-Catalog-Application/
├── src/
│   ├── main/
│   │   ├── java/com/application/
│   │   │   ├── Main.java
│   │   │   ├── GamePokemonService.java
│   │   │   ├── GameController.java
│   │   │   ├── Game.java
├── pom.xml
├── Dockerfile
├── game-deployment.yaml
├── game-service.yaml
├── game-ingress.yaml
├── README.md
├── Games.json
```

- `Games.json`: Contains Pokémon data.
- `Dockerfile`: Defines the Docker image build process.
- Kubernetes manifests (`game-deployment.yaml`, `game-service.yaml`): Define the Kubernetes resources.

## Step 1: Build the JAR

The application is built using Maven to create an executable JAR file.

1. **Build the JAR**:

   ```bash
   mvn clean package -DskipTests
   ```

   - This compiles the code, runs the build, and generates `target/Game-Catalog-Application-1.0-SNAPSHOT.jar`.
   - The `-DskipTests` flag skips tests for faster builds (ensure tests pass if you have them).
   

2. **Run the Application Locally** (Optional):

   ```bash
   mvn spring-boot:run
   ```

   - This starts the Spring Boot application on `http://localhost:8080`.


3. **Test API Endpoints Locally**: Use `curl` or a browser to test the API:

   ```bash
   # Get all Games
   http://localhost:8080/api/game
   
   # Get Game with name The Legend of Zelda: Breath of the Wild
   http://localhost:8080/api/game/name/The Legend of Zelda: Breath of the Wild
   
   # Get Games from publisher Nintendo
   http://localhost:8080/api/game/publisher/Nintendo
 
   # Post Game
   http://localhost:8080/api/game
   {
   "name" : "The Witcher 3: Wild Hunt",
   "publisher" : "CD Projekt",
   "releaseDate" : "2015-05-19",
   "availablePlatforms" : [ "PC", "PlayStation 4", "Xbox One", "Nintendo Switch" ]
   }

   # Put Game with updated information
   http://localhost:8080/api/game/The Witcher 3: Wild Hunt
   {
   "name" : "The Witcher 3: Wild Hunt",
   "publisher" : "CD Projekt",
   "releaseDate" : "2015-05-19",
   "availablePlatforms" : [ "PC", "PlayStation 4", "PlayStation 5", "Xbox One", "Nintendo Switch" ]
   }
   # Delete Game 
   http://localhost:8080/api/game/The Witcher 3: Wild Hunt

   ```

## Step 2: Containerize the Application with Docker
The application is packaged into a Docker container for deployment.

1. **Build the Docker Image**:

   ```bash
   docker build -t game-api .
   ```

    - This creates a Docker image named `game-api` based on the `Dockerfile`.
    - The `Dockerfile` uses a multi-stage build: Maven builds the JAR, and an Alpine JRE runs it.

2. **Test the Docker Container**:

   ```bash
   docker run -p 8080:8080 -v $(pwd)/data:/data game-api
   ```

    - Maps port `8080` on the host to `8080` in the container.
    - Test the API at `http://localhost:8080/api/game`.

3. **Push the Image to Docker Hub**: To make the image available to Kubernetes, push it to Docker Hub:

   ```bash
   # Log in to Docker Hub (create an account at https://hub.docker.com if needed)
   docker login
   
   # Tag the image with your Docker Hub username
   docker tag game-api <your-dockerhub-username>/game-api:latest
   
   # Push the image to Docker Hub
   docker push <your-dockerhub-username>/game-api:latest
   ```

    - Replace `<your-dockerhub-username>` with your Docker Hub username.
    - This makes the image accessible to Kubernetes clusters, including Kind.