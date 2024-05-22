FROM eclipse-temurin:21
RUN apt-get update && apt-get install -y curl
COPY target/storage-0.0.1-SNAPSHOT.jar /app.jar
ENV MONGO_HOST="mongodb"
ENV MONGO_PORT="27017"
ENV REACT_HOST="app-react"
ENV REACT_PORT="80"
ENV SYMFONY_HOST="app-symfony"
ENV SYMFONY_PORT="8000"
EXPOSE 8088
ENTRYPOINT [ "java","-jar","/app.jar" ]
