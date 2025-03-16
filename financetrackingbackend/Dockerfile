FROM openjdk:17-jdk-alpine
COPY build/libs/financetrackingbackend-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env
COPY src/main/resources/application-docker.yaml application-docker.yaml
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=docker"]