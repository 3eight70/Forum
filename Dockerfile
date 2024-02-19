FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/FileSystem-0.0.1.jar /app
CMD ["java", "-jar", "FileSystem-0.0.1.jar"]