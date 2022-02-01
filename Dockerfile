FROM eclipse-temurin:17-jre-alpine
COPY build/libs/*-all.jar sus-service.jar
EXPOSE 8080
CMD java ${JAVA_OPTS} -jar sus-service.jar