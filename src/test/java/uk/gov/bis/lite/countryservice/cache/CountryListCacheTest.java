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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryListCacheTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private SpireGetCountriesClient spireGetCountriesClient;

  @Mock
  private CountryListFactory countryListFactory;

  private CountryListCache countryListCache;

  @Before
  public void setUp() throws Exception {
    countryListCache = new CountryListCache(spireGetCountriesClient, countryListFactory);
  }

  @Test
  public void shouldThrowCacheLoadingExceptionForCountriesByCountrySetError() throws Exception {

    expectedException.expect(CacheLoadingException.class);
    expectedException.expectMessage("Failed to retrieve country list from SPIRE {countrySetName=export-control}");

    when(spireGetCountriesClient.countriesByCountrySetId("EXPORT_CONTROL")).thenThrow(new SOAPException());

    countryListCache.load();
  }

  @Test
  public void shouldGetCountrySetFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesBySetName("export-control");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<Country> countryList = countryListEntry.get().getList();
    assertThat(countryList.size()).isEqualTo(3);
    assertThat(countryList.get(0).getCountryName()).isEqualTo("Albania");
    assertThat(countryList.get(1).getCountryName()).isEqualTo("Brazil");
    assertThat(countryList.get(2).getCountryName()).isEqualTo("Finland");
  }

  @Test
  public void shouldGetEmptyListWhenCountrySetNotFoundInCache() throws Exception {

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesBySetName("blah");

    assertThat(countryListEntry.isPresent()).isFalse();
  }

  @Test
  public void shouldThrowCacheLoadingExceptionForCountriesByCountryGroupIdError() throws Exception {

    expectedException.expect(CacheLoadingException.class);
    expectedException.expectMessage("Failed to retrieve country list from SPIRE {countryGroupName=eu}");

    when(spireGetCountriesClient.countriesByCountryGroupId("EU")).thenThrow(new SOAPException());

    countryListCache.load();
  }

  @Test
  public void shouldGetCountryGroupFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesByGroupName("eu");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<Country> countryList = countryListEntry.get().getList();
    assertThat(countryList.size()).isEqualTo(3);
    assertThat(countryList.get(0).getCountryName()).isEqualTo("France");
    assertThat(countryList.get(1).getCountryName()).isEqualTo("Germany");
    assertThat(countryList.get(2).getCountryName()).isEqualTo("Sweden");
  }

  @Test
  public void shouldGetEmptyListWhenCountryGroupNotFoundInCache() throws Exception {

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesByGroupName("blah");

    assertThat(countryListEntry.isPresent()).isFalse();
  }

  private void setupCache() throws Exception {
    SOAPMessage countrySetSoapMessage = mock(SOAPMessage.class);
    SOAPMessage countryGroupSoapMessage = mock(SOAPMessage.class);

    when(spireGetCountriesClient.countriesByCountrySetId("EXPORT_CONTROL")).thenReturn(countrySetSoapMessage);
    when(spireGetCountriesClient.countriesByCountryGroupId("EU")).thenReturn(countryGroupSoapMessage);

    when(countryListFactory.create(countrySetSoapMessage))
      .thenReturn(Arrays.asList(new Country("1", "Finland"), new Country("2", "Brazil"), new Country("3", "Albania")));

    when(countryListFactory.create(countryGroupSoapMessage))
      .thenReturn(Arrays.asList(new Country("1", "Sweden"), new Country("2", "France"), new Country("3", "Germany")));

    countryListCache.load();
  }

}