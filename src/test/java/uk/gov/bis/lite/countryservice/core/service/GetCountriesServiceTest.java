package uk.gov.bis.lite.countryservice.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCacheEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetCountriesServiceTest {

    private static final String COUNTRY_SET_NAME = "export-control";

    @Mock
    private CountryListCache countryListCache;

    @InjectMocks
    private GetCountriesService getCountriesService;

    @Test
    public void shouldGetCountries() throws Exception {

        List<Country> countryList = Arrays.asList(createCountry("Albania"), createCountry("Brazil"), createCountry("Finland"));
        CountryListCacheEntry countryListCacheEntry = new CountryListCacheEntry(countryList);
        when(countryListCache.get(COUNTRY_SET_NAME)).thenReturn(Optional.of(countryListCacheEntry));

        Optional<CountryListCacheEntry> result = getCountriesService.getCountryList(COUNTRY_SET_NAME);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getCountryList(), is(countryList));
//        List<Country> countryList = result.get().getCountryList();
//        assertThat(countryList, is(3));
//        assertThat(countryList.get(0).getCountryName(), is("Albania"));
//        assertThat(countryList.get(1).getCountryName(), is("Brazil"));
//        assertThat(countryList.get(2).getCountryName(), is("Finland"));
    }

    private Country createCountry(String countryName) {
        Country country = new Country();
        country.setCountryName(countryName);
        return country;
    }
}