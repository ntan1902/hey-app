## base image
#FROM maven:3.8.1-jdk-11-slim
#
## copy the project files
#COPY ./pom.xml ./pom.xml
#
## copy your other files
#COPY ./src ./src
#
#RUN mvn package -DskipTests
#
## set the startup command to run your binary
#CMD ["java", "-jar", "./target/hey-chat-0.0.1-SNAPSHOT.jar"]

FROM openjdk:11-jdk-slim-buster

COPY ./src ./src
#COPY ./wait-for-it.sh ./wait-for-it.sh
#RUN chmod +x ./wait-for-it.sh

ADD ./target/hey-chat-0.0.1-SNAPSHOT.jar ./target/hey-chat-0.0.1-SNAPSHOT.jar

## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
RUN chmod +x /wait

## Add your application to the docker image
CMD /wait && java -jar ./target/hey-chat-0.0.1-SNAPSHOT.jar
