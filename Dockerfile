FROM openjdk:11
COPY build/libs/*.jar MarsServer.jar
ENTRYPOINT ["java","-jar","/MarsServer.jar"]