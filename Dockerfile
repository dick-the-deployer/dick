FROM java:8
VOLUME /tmp
RUN bash -c 'chmod +x mvnw'
RUN bash -c ./mvnw clean install -DskipTests
ADD dick-web/target/dick-web-1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]