FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar file
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 3001

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]