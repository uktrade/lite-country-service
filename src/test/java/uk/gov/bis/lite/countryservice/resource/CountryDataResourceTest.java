package uk.gov.bis.lite.countryservice.resource;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.service.CountryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class CountryDataResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryDataResourceTest.class);

  private static final String URL = "/country-data";

  private final CountryService countryService = mock(CountryService.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new CountryDataResource(countryService))
      .build();

  @Test
  public void shouldGetCountryData() {
    CountryView germany = new CountryView("1", "Germany", new String[]{"Deutschland", "BRD"});
    when(countryService.getCountryView("1")).thenReturn(Optional.of(germany));

    Response response = resources.client()
        .target(URL + "/1")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    String expected = "{'countryRef':'1','countryName':'Germany','synonyms':['Deutschland','BRD']}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturn404ForGetCountryIfCountryRefNotFound() {
    when(countryService.getCountryView("1")).thenReturn(Optional.empty());

    Response response = resources.client()
        .target(URL + "/1")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);

    String expected = "{'code':404,'message':'The following countryRef does not exist: 1'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturn404ForUpdateCountryIfCountryRefNotFound() {
    when(countryService.getUnmatchedCountryRefs(anyListOf(CountryData.class))).thenReturn(Collections.singletonList("1"));

    CountryView germany = new CountryView("1", "Germany", new String[]{});
    Response response = resources.client()
        .target(URL + "/1")
        .request()
        .put(Entity.json(germany));

    assertThat(response.getStatus()).isEqualTo(404);

    String expected = "{'code':404,'message':'The following countryRef does not exist: 1'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldUpdateCountry() {

    CountryView germany = new CountryView("1", "Germany", new String[]{});
    Response response = resources.client()
        .target(URL + "/1")
        .request()
        .put(Entity.json(germany));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.hasEntity()).isFalse();

    verify(countryService).bulkUpdateCountryData(anyListOf(CountryData.class));
  }

  @Test
  public void shouldDeleteCountry() {
    Response response = resources.client()
        .target(URL + "/1")
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(202);
    assertThat(response.hasEntity()).isFalse();

    verify(countryService).deleteCountryData("1");
  }

  @Test
  public void shouldGetAllCountries() {
    CountryView germany = new CountryView("1", "Germany", new String[]{"Deutschland", "BRD"});
    CountryView france = new CountryView("2", "France", new String[]{});
    when(countryService.getCountryViews()).thenReturn(Arrays.asList(germany, france));

    Response response = resources.client()
        .target(URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    String expected = "[{'countryRef':'1','countryName':'Germany','synonyms':['Deutschland','BRD']},{'countryRef':'2','countryName':'France','synonyms':[]}]";
    String actual = response.readEntity(String.class);
    assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturn404ForBulkUpdateIfCountryRefNotFound() {
    when(countryService.getUnmatchedCountryRefs(anyListOf(CountryData.class))).thenReturn(Arrays.asList("1", "2"));
    when(countryService.getDuplicates(anyListOf(CountryData.class))).thenReturn(new HashSet<>());

    CountryData germany = new CountryData("1", new String[]{});
    CountryData france = new CountryData("2", new String[]{});
    List<CountryData> countryDataList = Arrays.asList(germany, france);

    Response response = resources.client()
        .target(URL)
        .request()
        .put(Entity.json(countryDataList));

    assertThat(response.getStatus()).isEqualTo(404);

    String expected = "{'code':404,'message':'The following countryRef do not exist: 1, 2'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturn400ForBulkUpdateIfDuplicateRefFound() {
    when(countryService.getUnmatchedCountryRefs(anyListOf(CountryData.class))).thenReturn(new ArrayList<>());
    when(countryService.getDuplicates(anyListOf(CountryData.class))).thenReturn(new HashSet<>(Arrays.asList("1", "2")));

    CountryData germany = new CountryData("1", new String[]{});
    CountryData france = new CountryData("2", new String[]{});
    List<CountryData> countryDataList = Arrays.asList(germany, france, germany, france);

    Response response = resources.client()
        .target(URL)
        .request()
        .put(Entity.json(countryDataList));

    String expected = "{'code':400,'message':'The following countryRef are duplicate: 1, 2'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldBulkUpdate() {

    CountryData germany = new CountryData("1", new String[]{});
    CountryData france = new CountryData("2", new String[]{});
    List<CountryData> countryDataList = Arrays.asList(germany, france);

    Response response = resources.client()
        .target(URL)
        .request()
        .put(Entity.json(countryDataList));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.hasEntity()).isFalse();

    verify(countryService).bulkUpdateCountryData(anyListOf(CountryData.class));
  }

  @Test
  public void shouldDeleteAllCountries() {
    Response response = resources.client()
        .target(URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(202);
    assertThat(response.hasEntity()).isFalse();

    verify(countryService).deleteAllCountryData();
  }

  private String toJson(String str) {
    return str.replace("'", "\"");
  }

}
