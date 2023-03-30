# das ist das image in dem unser spring code laufen wird
FROM maven:3-openjdk-17-slim as dev
# java version 17 als lts
# -slim image heißt das es ein wenig kleiner ist
# das macht die downloads in der pipelin schneller
# ordner anlegen
RUN mkdir /app
# den ordner als arbeitsverzeichnis nutzen
WORKDIR /app

# multi stage build, siehe dazu die frontend Dockerfile
FROM dev as build
# das manifest unserer spring anwendung in den container kopieren
COPY ./pom.xml ./pom.xml
# das downloadet erstmal *alle* dependencies
RUN mvn dependency:go-offline -B
# im src/ ordner ist der tatsächliche quellcode
# in den container kopieren
COPY ./src ./src
# maven kompiliert unseren code in eine .jar datei
RUN mvn package

# wieder multi stage build um die ganzen build dateien
# nicht im finalen image zu haben
FROM dev as prod
# die kompilierte executable aus dem vorigen schritt kopieren
# schema: aus der pom.xml artifactId-version
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar .
# spring anwendung starten
ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
