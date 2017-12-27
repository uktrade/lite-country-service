package uk.gov.bis.lite.countryservice.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.integration.BaseIntegrationTest;
import uk.gov.bis.lite.countryservice.util.AuthUtil;

import javax.ws.rs.core.Response;

public class CountryAuthTest extends BaseIntegrationTest {

  private static final String URL = "http://localhost:8090/countries";
  private static final String COUNTRIES_SET_EXPORT_CONTROL_URL = URL + "/set/export-control";
  private static final String COUNTRIES_GROUP_EU = URL + "/group/eu";

  // GET /countries/set/{countrySetName}

  @Test
  public void getCountriesBySetShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRIES_SET_EXPORT_CONTROL_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getCountriesBySetShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRIES_SET_EXPORT_CONTROL_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getCountriesBySetShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRIES_SET_EXPORT_CONTROL_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // GET /countries/group/{groupName}

  @Test
  public void getCountriesByGroupShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRIES_GROUP_EU)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getCountriesByGroupShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(COUNTRIES_GROUP_EU)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getCountriesByGroupShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(COUNTRIES_GROUP_EU)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
