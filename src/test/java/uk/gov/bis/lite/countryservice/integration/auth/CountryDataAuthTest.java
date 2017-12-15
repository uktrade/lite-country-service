package uk.gov.bis.lite.countryservice.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.integration.BaseIntegrationTest;
import uk.gov.bis.lite.countryservice.util.AuthUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class CountryDataAuthTest extends BaseIntegrationTest {

  private static final String COUNTRY_DATA_URL = "http://localhost:8090/country-data";
  private static final String COUNTRY_DATA_CTRY3_URL = "http://localhost:8090/country-data/CTRY3";

  // GET /country-data/{countryRef}

  @Test
  public void getByCountryRefShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getByCountryRefShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getByCountryRefShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_CTRY3_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // PUT /country-data/{countryRef}

  @Test
  public void putByCountryRefShouldReturnForbiddenForServiceUser() {
    CountryData updateCountryData = new CountryData(null, Arrays.asList("Emirates", "Dubai"));
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .put(Entity.json(updateCountryData));

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void putByCountryRefShouldReturnUnauthorisedForNoAuthHeader() {
    CountryData updateCountryData = new CountryData(null, Arrays.asList("Emirates", "Dubai"));
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .put(Entity.json(updateCountryData));

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void putByCountryRefShouldReturnUnauthorisedForUnknownUsers() {
    CountryData updateCountryData = new CountryData(null, Arrays.asList("Emirates", "Dubai"));
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_CTRY3_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .put(Entity.json(updateCountryData));

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // DELETE /country-data/{countryRef}

  @Test
  public void deleteByCountryRefShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .delete();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void deleteByCountryRefShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_CTRY3_URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void deleteByCountryRefShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_CTRY3_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .delete();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // GET /country-data

  @Test
  public void getCountryDataShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getCountryDataShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getCountryDataShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // PUT /country-data

  @Test
  public void putCountryDataShouldReturnForbiddenForServiceUser() {
    List<CountryData> countryDataList = Collections.singletonList(new CountryData("CTRY3", null));

    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .put(Entity.json(countryDataList));

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void putCountryDataShouldReturnUnauthorisedForNoAuthHeader() {
    List<CountryData> countryDataList = Collections.singletonList(new CountryData("CTRY3", null));

    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .put(Entity.json(countryDataList));

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void putCountryDataShouldReturnUnauthorisedForUnknownUsers() {
    List<CountryData> countryDataList = Collections.singletonList(new CountryData("CTRY3", null));

    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .put(Entity.json(countryDataList));

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // DELETE /country-data

  @Test
  public void deleteCountryDataShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .delete();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void deleteCountryDataShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRY_DATA_URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void deleteCountryDataShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRY_DATA_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .delete();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
