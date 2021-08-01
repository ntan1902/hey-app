cd hey-chat
mvn clean install -DskipTests
export env=dev
java -jar ./target/hey-chat-0.0.1-SNAPSHOT.jar
