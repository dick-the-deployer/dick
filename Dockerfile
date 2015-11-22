FROM maven:3.3-jdk-8-onbuild
VOLUME /tmp
RUN bash -c 'cp /usr/src/app/dick-web/target/dick-web-1.0-SNAPSHOT.jar /app.jar'
RUN bash -c 'touch /app.jar'

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]