FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD target/inventory-service.jar inventory-service.jar


ENTRYPOINT ["java","-jar","/inventory-service.jar"]

