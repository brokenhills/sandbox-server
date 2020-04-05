FROM openjdk:8-jdk-alpine
ARG JAR_FILE=/home/runner/work/sandbox-server/sandbox-server/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]