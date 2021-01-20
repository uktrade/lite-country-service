# Country service

Retrieves country data from SPIRE and presents it to LITE applications, effectively acting as a proxy and cache for SPIRE data.

## Running locally

* `git clone git@github.com:uktrade/lite-country-service.git`
* `cd lite-country-service`
* `cp src/main/resources/sample-config.yaml src/main/resources/config.yaml`
* `./gradlew run`

## Endpoint summary

### Countries

Retrieves country set or group information, e.g.

* `/countries/set/export-control` - all countries in the "export control" set
* `/countries/group/eu` - all countries in the "EU" group

Known sets and groups are enumerated in `CountrySet` and `CountryGroup` respectively.

A `CountryView` is currently very simple and consists only of a reference and country name.

### Country Data

Endpoints for additional data maintenance. Currently only allow for updating the string array `synonyms`.

* GET `/country-data` - bulk view all country data stored in the service
* PUT `/country-data` - bulk update data for multiple countries at once
* DELETE `/country-data` - bulk delete all additional country data
* GET `/country-data/<countryRef>` - view additional data for a single country
* PUT `/country-data/<countryRef>` - update additional data for a single country
* DELETE `/country-data/<countryRef>` - delete additional data for a single country

## SPIRE integration

The `SpireCountriesClient` is used to retrieve the country list and group data from a SOAP endpoint. This is populated into
the `CountryCache`, and refreshed by a daily job (`CountryCacheJob`).

### GDS PaaS Deployment

This repo contains a pre-packed deployment file, lite-country-service-xxxx.jar.  This can be used to deploy this service manually from the CF cli.  Using the following command:

* cf push [app_name] -p lite-country-service-xxxx.jar

For this application to work the following dependencies need to be met:

* Bound PG DB (all services share the same backend db)
* Bound REDIS
* Env VARs will need to be set

### Archive state

This repo is now archived. If you need to deploy this application, you can find a copy of the DB and VARs in the DIT AWS account.
