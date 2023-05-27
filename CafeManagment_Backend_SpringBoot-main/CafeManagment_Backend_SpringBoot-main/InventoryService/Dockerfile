# Docker Build Maven Stage
FROM maven:3-jdk-8-alpine AS build
# Copy folder in docker
WORKDIR /opt/app
COPY ./ /opt/app
RUN mvn clean install -DskipTests
# Run spring boot in Docker
FROM openjdk:8-jdk-alpine
COPY --from=build /opt/app/target/*.jar InventoryService-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/InventoryService-0.0.1-SNAPSHOT.jar"]
