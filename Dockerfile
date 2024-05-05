FROM --platform=linux/amd64 openjdk:17-alpine
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} msc-user-api.jar
ENTRYPOINT ["java","-jar","msc-user-api.jar"]
EXPOSE 9091