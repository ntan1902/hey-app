# base image
FROM maven:3.8.1-jdk-11-slim


# copy the project files
COPY ./pom.xml ./pom.xml

# copy your other files
COPY ./src ./src

RUN mvn package -DskipTests

# set the startup command to run your binary
CMD ["java", "-jar", "./target/hey-chat-0.0.1-SNAPSHOT.jar"]