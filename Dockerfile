# ---------- Frontend Build stage ----------
FROM node:22-alpine AS frontend-builder

WORKDIR /app

COPY web/package.json .
COPY web/package-lock.json .
RUN npm install

COPY web/ .
RUN npm run build

# ---------- Backend Build stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS backend-builder

WORKDIR /app

# Copy parrent pom.xml file
COPY pom.xml .

# Copy module pom.xml files
COPY froggy-api/pom.xml froggy-api/pom.xml
COPY froggy-jpa/pom.xml froggy-jpa/pom.xml
COPY froggy-app/pom.xml froggy-app/pom.xml

# Download dependencies (cached unless pom changes)
RUN mvn -B dependency:go-offline

# Copy all source files
COPY . .
# Copy built frontend assets into the backend resources
COPY --from=frontend-builder /app/dist froggy-app/src/main/resources/static

# Build the application
RUN mvn -B clean package -DskipTests

# ---------- Runtime stage ----------

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=backend-builder /app/froggy-app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
