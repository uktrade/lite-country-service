package uk.gov.bis.lite.countryservice.resources;

import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.service.GetCountriesService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountriesResourceTest {

    private static final GetCountriesService getCountriesService = mock(GetCountriesService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new CountriesResource(getCountriesService, 1000))
            .build();

    @Test
    public void shouldGetCountriesResource() throws Exception {

        String countrySetName = "export-control";
        Country country = new Country();
        country.setCountryName("CTRY1434");
        country.setCountryRef("France");
        List<Country> countryList = Arrays.asList(country);
        when(getCountriesService.getCountryList(countrySetName)).thenReturn(countryList);


        List<Country> result = resources.client()
                .target("/countries/set/export-control")
                .request()
                .get(new GenericType<List<Country>>(){});

        assertThat(result, is(countryList));
    }
}