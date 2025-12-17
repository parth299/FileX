# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy Maven config first (pom.xml) to leverage caching of dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the project and skip tests for faster build
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
# Use a lightweight JDK image for running the app
FROM eclipse-temurin:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Default command to run your JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
