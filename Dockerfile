FROM docker.io/library/openjdk:21-slim

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]