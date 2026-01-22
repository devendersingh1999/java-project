# # -------- Stage 1: Build --------
# FROM maven:3.9.6-eclipse-temurin-21 AS build

# WORKDIR /app
# COPY . .

# # Use system Maven (NOT mvnw)
# RUN mvn clean package -DskipTests

# # -------- Stage 2: Runtime --------
# FROM eclipse-temurin:21-jre

# WORKDIR /app
# COPY --from=build /app/target/*.jar app.jar
# USER ares
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","app.jar"]
# -------- Stage 1: Build --------
FROM maven:3.9.6-openjdk-21 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# -------- Stage 2: Runtime --------
FROM openjdk:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup -S appgroup && adduser -S ares -G appgroup
USER ares

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
