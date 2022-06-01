FROM openjdk:17
ADD ./target/TaskManager-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]