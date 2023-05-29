FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/inventory-service.jar inventory-service.jar


ENTRYPOINT ["java","-jar","/inventory-service.jar"]

