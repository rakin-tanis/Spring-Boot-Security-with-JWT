FROM eclipse-temurin:17-jdk-alpine
LABEL authors="akin"
VOLUME /tmp
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar","$ARGS"]