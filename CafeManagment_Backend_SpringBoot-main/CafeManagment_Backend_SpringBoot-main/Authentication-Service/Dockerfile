adoptopenjdk/openjdk11:jre-11.0.19_7-ubuntu
FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD target/authentication-service.jar authentication-service.jar
ENTRYPOINT ["java","-jar","/authentication-service.jar"]
