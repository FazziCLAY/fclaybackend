FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=build/libs/fclaybackend-0.0.2-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]