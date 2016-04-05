package prototype.countryservice.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import prototype.countryservice.api.Country;
import prototype.countryservice.core.service.GetCountriesService;

import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountriesResourceTest {

    private static final GetCountriesService getCountriesService = mock(GetCountriesService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new CountriesResource(getCountriesService))
            .build();

    @Test
    public void shouldGetCountriesResource() throws Exception {

        String countrySetName = "export-control";
        Country country = new Country("CTRY1434", "France");
        List<Country> countryList = Collections.singletonList(country);
        when(getCountriesService.getCountryList(countrySetName)).thenReturn(countryList);


        List<Country> result = resources.client()
                .target("/countries/set/export-control")
                .request()
                .get(new GenericType<List<Country>>(){});

        assertThat(result, is(countryList));
    }
}