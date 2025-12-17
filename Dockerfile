# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the fat/shaded JAR from the build stage
# Replace with the exact name of your shaded JAR if needed
COPY --from=build /app/target/*-shaded.jar app.jar

# Expose the port your app listens on
EXPOSE 8080

# Set default command to run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
