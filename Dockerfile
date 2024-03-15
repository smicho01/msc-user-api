FROM openjdk:17-alpine
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} students-api.jar
ENTRYPOINT ["java","-jar","students-api"]
EXPOSE 9091