FROM openjdk:17-jdk-slim

WORKDIR /app

COPY mvnw .
COPY .mvn ./.mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src ./src
COPY watch.sh .

RUN chmod +x watch.sh

RUN apt-get update && apt-get install -y inotify-tools

EXPOSE 8080

CMD ["./watch.sh"]