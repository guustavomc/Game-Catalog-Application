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
   mvn clean package 
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
   docker run -p 8080:8080 -v ${PWD}/data:/data game-api

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

## Step 3: Deploy to Kubernetes with Kind

We use **Kind** to run a local Kubernetes cluster and deploy the API using Kubernetes manifests.

### 3.1 Set Up a Kind Cluster

1. **Create a Kind Cluster**:

   ```bash
   kind create cluster --name game-catalog
   ```

   - This starts a local Kubernetes cluster named `game-catalog`.

2. **Verify the Cluster**:

   ```bash
   kubectl cluster-info --context kind-game-catalog
   ```

   - Ensures `kubectl` is connected to the Kind cluster.

### 3.2 Load the Docker Image into Kind (Optional)

If you prefer not to use Docker Hub, you can load the local `game-catalog` image into Kind:

```bash
kind load docker-image game-api:latest --name game-catalog
```

- This makes the image available to the Kind cluster without needing a registry. Skip this step if you pushed the image to Docker Hub.

### 3.3 Deploy Kubernetes Resources

The application is deployed using a `Deployment` and exposed via a `Service`. Optionally, an `Ingress` can be used for HTTP access.

1. **Create the Deployment Manifest** (`game-catalog-deployment.yaml`):

   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: game-api
     namespace: default
     labels:
       app: game-api
   spec:
     replicas: 2
     selector:
       matchLabels:
         app: game-api
     template:
       metadata:
         labels:
           app: game-api
       spec:
         containers:
         - name: game-api
           image: <your-dockerhub-username>/game-api:latest
           ports:
           - containerPort: 8080
           resources:
             requests:
               memory: "256Mi"
               cpu: "250m"
             limits:
               memory: "512Mi"
               cpu: "500m"
           volumeMounts:
              - name: data-volume
                mountPath: /data
           livenessProbe:
             httpGet:
               path: /api/game
               port: 8080
             initialDelaySeconds: 15
             periodSeconds: 10
           readinessProbe:
             httpGet:
               path: /api/game
               port: 8080
             initialDelaySeconds: 5
             periodSeconds: 5
   ```

   - **Important**: Replace `<your-dockerhub-username>` with your Docker Hub username in the `image` field. If you used the local image with `kind load docker-image`, use `image: game-api:latest` instead.

2. **Create the Service Manifest** (`game-catalog-service.yaml`):

   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: game-api-service
     namespace: default
   spec:
     selector:
       app: game-api
     ports:
       - protocol: TCP
         port: 80
         targetPort: 8080
         nodePort: 30080
     type: NodePort
   ```

3. **Create the Persistent Volume** (`game-catalog-pvc.yaml`):

- Since app writes and reads a /data/Games.json file inside the container,
  we need to attach a Persistent Volume (PVC) to your pods, otherwise when a pod dies and restarts, it will lose all Games.json data.

    ```yaml
      apiVersion: v1
      kind: PersistentVolumeClaim
      metadata:
        name: game-api-pvc
        namespace: default
      spec:
        accessModes:
          - ReadWriteOnce
        resources:
          requests:
            storage: 1Gi
   ```


3. **Apply the Manifests**:

   ```bash
   kubectl apply -f game-catalog-pvc.yaml
   kubectl apply -f game-catalog-deployment.yaml
   kubectl apply -f game-catalog-service.yaml
   ```

4. **Verify the Deployment**:

   ```bash
   kubectl get deployments
   kubectl get pods
   kubectl get services
   ```

   - Ensure the `game-api` Deployment has 2/2 pods ready.
   - Check that `game-api-service` is running with `type: NodePort`.

### 3.4 Access the API

The `NodePort` Service exposes the API on a high port (e.g., `30080`).

1. **Get the Cluster IP**:

   ```bash
   kubectl get nodes -o wide
   ```

   - Note the `INTERNAL-IP` of the Kind control plane node (e.g., `172.18.0.2`).

2. **Test the API**:

   ```bash
   curl http://<INTERNAL-IP>:30080/api/pokemon
   curl http://<INTERNAL-IP>:30080/api/pokemon/id/1
   curl http://<INTERNAL-IP>:30080/api/pokemon/search/Squirtle
   curl http://<INTERNAL-IP>:30080/api/pokemon/type/Grass
   ```

   **Windows Alternative** (PowerShell):
   ```powershell
   Invoke-WebRequest -Uri http://<INTERNAL-IP>:30080/api/pokemon
   ```

   - Replace `<INTERNAL-IP>` with the node’s IP.

3. **Port Forwarding** (Alternative):

   ```bash
   kubectl port-forward service/pokedex-api-service 8080:80
   ```

   - Access the API at `http://localhost:8080/api/pokemon`.

### 3.5 Optional: Set Up Ingress

For HTTP access with a domain (e.g., `pokedex.local`), use an Ingress.

1. **Enable the Ingress Controller in Kind**: Create a Kind cluster with Ingress support by using a config file (`kind-config.yaml`):

   ```yaml
   kind: Cluster
   apiVersion: kind.x-k8s.io/v1alpha4
   nodes:
   - role: control-plane
     kubeadmConfigPatches:
     - |
       kind: InitConfiguration
       nodeRegistration:
         kubeletExtraArgs:
           node-labels: "ingress-ready=true"
     extraPortMappings:
     - containerPort: 80
       hostPort: 80
       protocol: TCP
     - containerPort: 443
       hostPort: 443
       protocol: TCP
   ```

   Create the cluster:

   ```bash
   kind create cluster --name pokedex --config kind-config.yaml
   ```

2. **Install NGINX Ingress Controller**:

   ```bash
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
   ```

3. **Create the Ingress Manifest** (`pokedex-ingress.yaml`):

   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: pokedex-api-ingress
     namespace: default
     annotations:
       nginx.ingress.kubernetes.io/rewrite-target: /
   spec:
     rules:
     - host: pokedex.local
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: pokedex-api-service
               port:
                 number: 80
   ```

4. **Apply the Ingress**:

   ```bash
   kubectl apply -f pokedex-ingress.yaml
   ```

5. **Update Hosts File**:
   - **Windows**: Edit `C:\Windows\System32\drivers\etc\hosts` (requires admin privileges):
     ```powershell
     Add-Content -Path "C:\Windows\System32\drivers\etc\hosts" -Value "127.0.0.1 pokedex.local" -Force
     ```
      - Run PowerShell as Administrator.
   - **macOS/Linux**:
     ```bash
     echo "127.0.0.1 pokedex.local" | sudo tee -a /etc/hosts
     ```

6. **Test the Ingress**:

   ```bash
   curl http://pokedex.local/api/pokemon
   ```

   **Windows Alternative** (PowerShell):
   ```powershell
   Invoke-WebRequest -Uri http://pokedex.local/api/pokemon