FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/product-service-*.jar app.jar
EXPOSE 9012
ENTRYPOINT ["java", "-jar", "app.jar"]

