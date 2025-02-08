# Build Stage
FROM maven:3.8.6 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Final Stage
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/online-examination-0.0.1-SNAPSHOT.jar /app/online-examination.jar

EXPOSE 8080
CMD ["java", "-jar", "online-examination.jar"]

