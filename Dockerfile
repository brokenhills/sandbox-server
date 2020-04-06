FROM openjdk:8-jdk-alpine
ARG JAR_FILE=https://maven.pkg.github.com/brokenhills/sandbox-server/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
