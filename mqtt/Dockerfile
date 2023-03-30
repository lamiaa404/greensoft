FROM maven:3-openjdk-17-slim as dev
# java version 17 als lts
RUN mkdir /mqtt
WORKDIR /mqtt

FROM dev as build
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package

FROM dev as prod
# pom.xml artifactId-version
COPY --from=build /mqtt/target/mqtt-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "mqtt-0.0.1-SNAPSHOT.jar"]
