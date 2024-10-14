# Use the official Maven image to create a build artifact.
FROM maven:3.8.1-jdk-11 as build
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
RUN mvn -f pom.xml clean package

# Use OpenJDK for running the app.
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/football-standings.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/football-standings.jar"]