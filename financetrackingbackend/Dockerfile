FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/financetrackingbackend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY .env .env
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]