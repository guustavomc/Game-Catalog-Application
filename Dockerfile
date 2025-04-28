# 1. Use a lightweight Java 17 runtime
FROM eclipse-temurin:17-jre-jammy

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy your packaged jar into the container
COPY target/Game-Catalog-Application-1.0-SNAPSHOT.jar app.jar

# 4. Create the external data directory inside container
RUN mkdir /data

# 5. Expose port 8080
EXPOSE 8080

# 6. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
