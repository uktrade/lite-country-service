package uk.gov.bis.lite.countryservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.core.service.GetCountriesService;
import io.dropwizard.jersey.caching.CacheControl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/countries")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CountriesResource {

    private final GetCountriesService getCountriesService;

    @Inject
    public CountriesResource(GetCountriesService getCountriesService) {
        this.getCountriesService = getCountriesService;
    }

    @GET
    @Path("set/{countrySetName}")
    @Timed // measures the duration of requests to a resource
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public List<Country> getCountryList(@PathParam("countrySetName") String countrySetName) throws CountryServiceException {
        return getCountriesService.getCountryList(countrySetName);
    }

}
