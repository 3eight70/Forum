FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/service-registry-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "service-registry-0.0.1-SNAPSHOT.jar"]