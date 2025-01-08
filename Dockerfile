# Use JDK as the base image for building the app
FROM openjdk:17-jdk-slim AS builder

# Set working directory for build
WORKDIR /app

# Copy Gradle files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Copy source code
COPY src src

# Grant execute permissions to Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Stage 2: Use the built JAR to run the application
FROM openjdk:17-jdk-slim

# Set working directory for runtime
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the Spring Boot application with the 'prod' profile
ENTRYPOINT ["java", "-jar", "app.jar"]
