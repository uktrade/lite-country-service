FROM java:8

COPY lite-country-service-1.0-SNAPSHOT-all.jar /opt/lite-country-service/

COPY service.yaml /opt/lite-country-service/

EXPOSE 8090

WORKDIR /opt/lite-country-service

CMD ["java", "-jar", "lite-country-service-1.0-SNAPSHOT-all.jar", "server", "service.yaml"]


