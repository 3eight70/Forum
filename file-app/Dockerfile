FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/file-app-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "file-app-0.0.1-SNAPSHOT.jar"]