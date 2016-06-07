package uk.gov.bis.lite.countryservice.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.service.CountriesService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/countries")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CountriesResource {

  private final CountriesService countriesService;
  private final Integer cacheExpirySeconds;

  @Inject
  public CountriesResource(CountriesService countriesService, @Named("cacheExpirySeconds") Integer cacheExpirySeconds) {
    this.countriesService = countriesService;
    this.cacheExpirySeconds = cacheExpirySeconds;
  }

  @GET
  @Path("set/{countrySetName}")
  @Timed // measures the duration of requests to a resource
  public Response getCountryList(@PathParam("countrySetName") @NotEmpty String countrySetName) {
    CountryListEntry countryListEntry = countriesService.getCountryList(countrySetName);

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