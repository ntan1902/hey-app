#FROM maven:3.8.1-jdk-11-slim
## copy the project files
#COPY ./pom.xml ./pom.xml
#
## build all dependencies for offline use
##RUN mvn dependency:go-offline -B
#
## copy your other files
#COPY ./src ./src
#COPY ./settings.xml ./settings.xml
#
## build for release
#RUN mvn package -DskipTests
#
## set the startup command to run your binary
#CMD ["java", "-jar", "./target/hey-auth-0.0.1-SNAPSHOT.jar"]


FROM openjdk:11-jdk-slim-buster


ADD ./target/hey-auth-0.0.1-SNAPSHOT.jar ./target/hey-auth-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "./target/hey-auth-0.0.1-SNAPSHOT.jar"]