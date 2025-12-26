FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/payment-service-*.jar app.jar
EXPOSE 9015
ENTRYPOINT ["java", "-jar", "app.jar"]
