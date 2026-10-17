# ==============================================================
# Multi-stage Dockerfile from Repository Root (Spring Boot + Frontend)
# ==============================================================

# Stage 1: Build the Spring Boot backend with Maven
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

# Copy backend pom and cache dependencies
COPY backend/pom.xml ./backend/
RUN cd backend && mvn dependency:go-offline -B

# Copy backend source and compile
COPY backend/src ./backend/src
RUN cd backend && mvn clean package -DskipTests

# Stage 2: Minimal JRE runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy compiled JAR artifact
COPY --from=builder /workspace/backend/target/employee-management-system-1.0.0.jar app.jar

# Copy frontend static assets so Spring Boot serves them seamlessly
COPY frontend/ ./frontend/

USER spring:spring

# Render assigns PORT dynamically
ENV PORT=8080
EXPOSE ${PORT}

# Run with container memory limits and dynamic port binding
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
