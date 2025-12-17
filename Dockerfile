# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom only first to cache dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
# Use an official Eclipse Temurin JDK image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Default command to run your app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
