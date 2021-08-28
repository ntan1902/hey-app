cd hey-registry
export TZ="Asia/Ho_Chi_Minh"
mvn clean install -DskipTests
java -jar ./target/hey-registry-0.0.1-SNAPSHOT.jar
