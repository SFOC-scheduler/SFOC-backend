FROM amazoncorretto:17-alpine
ARG JAR_PATH=build/libs/*.jar
COPY ${JAR_PATH} app.jar
ENTRYPOINT ["java","-jar","app.jar"]