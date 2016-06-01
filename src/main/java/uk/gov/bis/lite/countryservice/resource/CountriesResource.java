package uk.gov.bis.lite.countryservice.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.service.GetCountriesService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

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
    Optional<CountryListEntry> countryListEntryOptional = getCountriesService.getCountryList(countrySetName);
    if (!countryListEntryOptional.isPresent()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    CountryListEntry countryListEntry = countryListEntryOptional.get();
    return Response.ok()
        .entity(countryListEntry.getList())
        .cacheControl(getCacheControl(countryListEntry.getTimeStamp()))
        .build();
  }

  private CacheControl getCacheControl(long timestamp) {
    CacheControl cacheControl = new CacheControl();
    int elapsedTime = (int) ((System.currentTimeMillis() - timestamp) / 1000);

    cacheControl.setMaxAge(cacheExpirySeconds - elapsedTime);

    return cacheControl;
  }

}