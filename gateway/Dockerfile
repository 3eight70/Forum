FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/gateway-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]