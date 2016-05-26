FROM java:8

RUN mkdir -p /opt/lite-country-service

ENV JAR_FILE lite-country-service-1.0-SNAPSHOT.jar
ENV SERVICE_DIR /opt/lite-country-service

COPY build/libs/$JAR_FILE $SERVICE_DIR

WORKDIR $SERVICE_DIR

CMD java "-jar" $JAR_FILE "server" $CONFIG_FILE