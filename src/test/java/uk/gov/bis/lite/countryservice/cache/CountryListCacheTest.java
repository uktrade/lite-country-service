package uk.gov.bis.lite.countryservice.cache;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.exception.CacheLoadingException;
import uk.gov.bis.lite.countryservice.model.Country;
import uk.gov.bis.lite.countryservice.spire.SpireGetCountriesClient;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryListCacheTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private SpireGetCountriesClient spireGetCountriesClient;

  @Mock
  private CountryListFactory countryListFactory;

  @Mock
  private SOAPMessage soapMessage;

  private CountryListCache countryListCache;

  @Before
  public void setUp() throws Exception {
    countryListCache = new CountryListCache(spireGetCountriesClient, countryListFactory);
  }

  @Test
  public void shouldThrowExceptionForSOAPFailure() throws Exception {

    expectedException.expect(CacheLoadingException.class);
    expectedException.expectMessage("Failed to retrieve country list from SPIRE.");

    when(spireGetCountriesClient.executeRequest("EXPORT_CONTROL")).thenThrow(new SOAPException());

    countryListCache.load();
  }

  @Test
  public void shouldGetCountryListFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.get("export-control");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<Country> countryList = countryListEntry.get().getList();
    assertThat(countryList.size()).isEqualTo(3);
    assertThat(countryList.get(0).getCountryName()).isEqualTo("Albania");
    assertThat(countryList.get(1).getCountryName()).isEqualTo("Brazil");
    assertThat(countryList.get(2).getCountryName()).isEqualTo("Finland");
  }

  @Test
  public void shouldGetEmptyListFromCacheWhenKeyNotFound() throws Exception {

    Optional<CountryListEntry> countryListEntry = countryListCache.get("blah");

    assertThat(countryListEntry.isPresent()).isFalse();
  }

  private void setupCache() throws Exception {
    when(spireGetCountriesClient.executeRequest("EXPORT_CONTROL")).thenReturn(soapMessage);

    List<Country> countryList = Arrays.asList(new Country("1", "Finland"), new Country("2", "Brazil"), new Country("3", "Albania"));
    when(countryListFactory.create(soapMessage)).thenReturn(countryList);

    countryListCache.load();
  }

}