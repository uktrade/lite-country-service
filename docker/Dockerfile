FROM java:8

ENV JAR_FILE lite-country-service-1.0-SNAPSHOT.jar
ENV SERVICE_DIR /opt/lite-country-service

COPY docker/$JAR_FILE SERVICE_DIR

COPY docker/service.yaml SERVICE_DIR

EXPOSE 9090

WORKDIR SERVICE_DIR

CMD java "-jar" $JAR_FILE "server" service.yaml



