
FROM maven:3.9.6-eclipse-temurin-21 AS build
# Use Maven with Java 21 (Spring Boot 3)

WORKDIR /app
# Set the working directory inside the container to /app

COPY pom.xml .
# Copy the Maven configuration first (for dependency caching)

COPY src ./src
# Copy the source code into the container

RUN mvn clean package -DskipTests
# Build the JAR file, skipping tests for faster builds

# ---------- Stage 2: Run the application ----------
FROM eclipse-temurin:17-jdk
# Use lightweight Java 17 runtime image

WORKDIR /app
# Set the working directory

COPY --from=build /app/target/*.jar app.jar
# Copy the built JAR from the first stage to this runtime image

EXPOSE 8080
# Expose port 8080 (Spring Boot default)

ENTRYPOINT ["java", "-jar", "app.jar"]
# Command to run the application
