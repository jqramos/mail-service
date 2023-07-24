FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/mail-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "mail-service-0.0.1-SNAPSHOT.jar"]
