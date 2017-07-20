# Country service

Retrieves country data from SPIRE and presents it to LITE applications, effectively acting as a proxy and cache for SPIRE data.

## Running locally

* `git clone git@github.com:uktrade/lite-country-service.git`
* `cd lite-country-service` 
* `cp src/main/resources/sample-config.yaml src/main/resources/config.yaml`
* `./gradlew run`

## Endpoint summary

`/countries`

Retrieves country set or group information, e.g.

* `/countries/set/export-control` - all countries in the "export control" set
* `/countries/group/eu` - all countries in the "EU" group

Known sets and groups are enumerated in `CountrySet` and `CountryGroup` respectively.

A `CountryView` is currently very simple and consists only of a reference and country name.

`/country-data`

Endpoints for additional data maintenance. Currently only allow for updating the string array `synonyms`.

* GET `/country-data/<countryRef>` - view additional data for a single country
* PUT `/country-data/<countryRef>` - update additional data for a single country
* DELETE `/country-data/<countryRef>` - delete additional data for a single country
* GET `/country-data` - bulk view all country data stored in the service
* PUT `/country-data` - bulk update data for multiple countries at once
* DELETE `/country-data` - bulk delete all additional country data

## SPIRE integration

The `SpireCountriesClient` is used to retrieve the country list and group data from a SOAP endpoint. This is populated into 
the `CountryCache`, and refreshed by a daily job (`CountryCacheJob`).