FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/order-service-*.jar app.jar
EXPOSE 9014
ENTRYPOINT ["java", "-jar", "app.jar"]

