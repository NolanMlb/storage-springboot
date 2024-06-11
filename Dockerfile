FROM eclipse-temurin:21
WORKDIR /opt
COPY target/storage-0.0.7-SNAPSHOT.jar /opt/app.jar
ENV MONGO_HOST="kstoragemongo-svc"
ENV MONGO_PORT="27017"
ENV REACT_HOST="app-react"
ENV REACT_PORT="8080"
ENV SYMFONY_HOST="app-symfony"
ENV SYMFONY_PORT="8000"
EXPOSE 8088
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar