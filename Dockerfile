FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/pdf-extractor-tools-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]