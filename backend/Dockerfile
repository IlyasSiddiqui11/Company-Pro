# ==============================================================
# Multi-stage Dockerfile for Spring Boot Backend on Render
# ==============================================================

# Stage 1: Build the application with Maven
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lightweight JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: Run as non-root spring user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy packaged JAR artifact from builder stage
COPY --from=builder /app/target/employee-management-system-1.0.0.jar app.jar

# Render assigns PORT dynamically (defaults to 10000, 8080 locally)
ENV PORT=8080
EXPOSE ${PORT}

# Run with JVM container support, RAM limit optimization, and dynamic port binding
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
