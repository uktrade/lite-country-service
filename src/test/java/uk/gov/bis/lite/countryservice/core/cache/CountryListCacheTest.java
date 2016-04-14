package uk.gov.bis.lite.countryservice.core.cache;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.core.service.CountryListFactory;
import uk.gov.bis.lite.countryservice.core.service.SpireGetCountriesClient;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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

    @Mock
    private SOAPMessage soapMessage;

    @Before
    public void setUp() throws Exception {
        countryListCache = new CountryListCache(spireGetCountriesClient, countryListFactory);
    }

    @Test
    public void shouldThrowExceptionForSOAPFailure() throws Exception {

        expectedException.expect(CountryServiceException.class);
        expectedException.expectMessage("Failed to retrieve country list from SPIRE.");

        when(spireGetCountriesClient.executeRequest("EXPORT_CONTROL")).thenThrow(new SOAPException());

        List<Country> countryList = Arrays.asList(createCountry("Finland"), createCountry("Brazil"), createCountry("Albania"));
        when(countryListFactory.create(soapMessage)).thenReturn(countryList);

        countryListCache.load();
    }

    @Test
    public void shouldGetCountryListFromCache() throws Exception {

        setupCache();

        List<Country> result = countryListCache.get("export-control");

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getCountryName(), is("Albania"));
        assertThat(result.get(1).getCountryName(), is("Brazil"));
        assertThat(result.get(2).getCountryName(), is("Finland"));
    }

    @Test
    public void shouldGetEmptyListFromCacheWhenKeyNotFound() throws Exception {

        setupCache();

        List<Country> result = countryListCache.get("blah");

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(0));
    }

    private void setupCache() throws Exception {
        when(spireGetCountriesClient.executeRequest("EXPORT_CONTROL")).thenReturn(soapMessage);

        List<Country> countryList = Arrays.asList(createCountry("Finland"), createCountry("Brazil"), createCountry("Albania"));
        when(countryListFactory.create(soapMessage)).thenReturn(countryList);

        countryListCache.load();
    }

    private Country createCountry(String countryName) {
        Country country = new Country();
        country.setCountryName(countryName);
        return country;
    }
}