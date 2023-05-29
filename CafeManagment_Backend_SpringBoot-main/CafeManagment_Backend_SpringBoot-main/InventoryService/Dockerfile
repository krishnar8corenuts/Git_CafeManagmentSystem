
FROM adoptopenjdk/openjdk11:jre-11.0.19_7-ubuntu
VOLUME /tmp
ADD target/inventory-service.jar inventory-service.jar


ENTRYPOINT ["java","-jar","/inventory-service.jar"]

