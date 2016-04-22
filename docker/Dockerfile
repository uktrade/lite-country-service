FROM java:8

RUN mkdir -p /opt/lite-country-service/

WORKDIR /opt/lite-country-service

COPY lite-country-service-1.0-SNAPSHOT-all.jar /opt/lite-country-service/

COPY service.yaml /opt/lite-country-service/

EXPOSE 8090

CMD ["java", "-jar", "lite-country-service-1.0-SNAPSHOT-all.jar", "server", "service.yaml"]


