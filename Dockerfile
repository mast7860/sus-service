FROM eclipse-temurin:17-jre-alpine
COPY sus-service/build/libs/*-all.jar sus-service.jar
EXPOSE 8080
CMD java -Dmicronaut.environments=${ENVIRONMENT} ${JAVA_OPTS} -jar sus-service.jar