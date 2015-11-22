FROM java:8
VOLUME /tmp

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN bash -c 'chmod +x mvnw'
RUN ./mvnw install -DskipTests

RUN bash -c 'cp /usr/src/app/dick-web/target/dick-web-1.0-SNAPSHOT.jar /app.jar'
RUN bash -c 'touch /app.jar'
RUN bash -c 'rm -rf /usr/src/app'

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]