package uk.gov.bis.lite.countryservice.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.cache.CountryListEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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

        List<Country> countryList = Arrays.asList(new Country("1", "Albania"), new Country("4", "Brazil"),
                new Country("3", "Finland"));

        CountryListEntry countryListEntry = new CountryListEntry(countryList);
        when(countryListCache.get(COUNTRY_SET_NAME)).thenReturn(Optional.of(countryListEntry));

        Optional<CountryListEntry> result = getCountriesService.getCountryList(COUNTRY_SET_NAME);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getList(), is(countryList));
    }

}