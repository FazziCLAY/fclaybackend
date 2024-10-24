FROM openjdk:17.0.2-jdk-slim-buster
WORKDIR /home/fclaybackend
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]