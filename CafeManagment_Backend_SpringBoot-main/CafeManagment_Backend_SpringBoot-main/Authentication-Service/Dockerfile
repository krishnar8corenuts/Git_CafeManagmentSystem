FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/authentication-service.jar authentication-service.jar


ENTRYPOINT ["java","-jar","/authentication-service.jar"]
