FROM java:8

ARG PORT

COPY docker/lite-country-service-1.0-SNAPSHOT.jar /opt/lite-country-service/

COPY docker/service.yaml /opt/lite-country-service/

EXPOSE $PORT

WORKDIR /opt/lite-country-service

CMD ["java", "-jar", "lite-country-service-1.0-SNAPSHOT.jar", "server", "service.yaml"]


