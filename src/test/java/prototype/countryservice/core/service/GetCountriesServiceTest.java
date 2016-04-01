package prototype.countryservice.core.service;

import prototype.countryservice.api.Country;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetCountriesServiceTest {

    @Mock
    private SpireGetCountriesClient spireGetCountriesClient;

    @Mock
    private CountryListFactory countryListFactory;

    private GetCountriesService getCountriesService;

    @Mock
    private SOAPMessage soapMessage;

    @Before
    public void setUp() throws Exception {
        getCountriesService = new GetCountriesService(spireGetCountriesClient, countryListFactory, 1000);
    }

    @Test
    public void shouldGetCountries() throws Exception {
        when(spireGetCountriesClient.executeRequest("EXPORT_CONTROL")).thenReturn(soapMessage);

        List<Country> countryList = Arrays.asList(createCountry("Finland"), createCountry("Brazil"), createCountry("Albania"));
        when(countryListFactory.create(soapMessage)).thenReturn(countryList);

        List<Country> result = getCountriesService.getCountryList("export-control");

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getCountryName(), is("Albania"));
        assertThat(result.get(1).getCountryName(), is("Brazil"));
        assertThat(result.get(2).getCountryName(), is("Finland"));
    }

    private Country createCountry(String countryName) {
        Country country = new Country();
        country.setCountryName(countryName);
        return country;
    }
}