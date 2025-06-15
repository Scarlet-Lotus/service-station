# Basic image
FROM openjdk:21-jdk-slim

# Specifying metadata
LABEL maintainer="mamlukbeibarys@gmail.com"

# Working directory inside the container
WORKDIR /app

# Copying the JAR file of the application
COPY build/libs/service-station-0.0.1-SNAPSHOT.jar app.jar

# Port to be used by the application
EXPOSE 8081

# Command to launch the application
ENTRYPOINT ["java", "-jar", "app.jar"]