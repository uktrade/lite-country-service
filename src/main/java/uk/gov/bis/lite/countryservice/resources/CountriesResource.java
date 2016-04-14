package uk.gov.bis.lite.countryservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCacheEntry;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.core.service.GetCountriesService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Path("/countries")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CountriesResource {

    private final GetCountriesService getCountriesService;
    private final Integer cacheExpirySeconds;

    @Inject
    public CountriesResource(GetCountriesService getCountriesService, @Named("cacheExpirySeconds") Integer cacheExpirySeconds) {
        this.getCountriesService = getCountriesService;
        this.cacheExpirySeconds = cacheExpirySeconds;
    }

    @GET
    @Path("set/{countrySetName}")
    @Timed // measures the duration of requests to a resource
    public Response getCountryList(@PathParam("countrySetName") @NotEmpty String countrySetName) throws CountryServiceException {
        Optional<CountryListCacheEntry> countryList = getCountriesService.getCountryList(countrySetName);
        if (!countryList.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        CountryListCacheEntry countryListCacheEntry = countryList.get();
        return Response.ok()
                .entity(countryListCacheEntry.getCountryList())
                .cacheControl(getCacheControl(countryListCacheEntry.getTimeStamp()))
                .build();
    }

    private CacheControl getCacheControl(long timestamp) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge((int)(timestamp /1000) + cacheExpirySeconds);
        return cacheControl;
    }

}