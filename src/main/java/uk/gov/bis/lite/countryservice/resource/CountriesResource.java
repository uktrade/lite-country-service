package uk.gov.bis.lite.countryservice.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.ErrorResponse;
import uk.gov.bis.lite.countryservice.service.CountriesService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  static final String NEGATIVE_COUNTRY_ID_PREFIX = "CTRY-";
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
    Optional<CountryListEntry> countryListEntry = countriesService.getCountrySet(countrySetName);
    if (countryListEntry.isPresent()) {
      return buildResponse(countryListEntry.get());
    } else {
      return ErrorResponse.buildResponse("Country set does not exist - " + countrySetName, 404);
    }
  }

  @GET
  @Path("group/{groupName}")
  @Timed // measures the duration of requests to a resource
  public Response getCountryGroups(@PathParam("groupName") @NotEmpty String groupName) {
    Optional<CountryListEntry> countryListEntry = countriesService.getCountryGroup(groupName);
    if (countryListEntry.isPresent()) {
      return buildResponse(countryListEntry.get());
    } else {
      return ErrorResponse.buildResponse("Country group does not exist - " + groupName, 404);
    }
  }

  private Response buildResponse(CountryListEntry countryListEntry) {
    //Filter "negative" country IDs
    List<CountryView> spireCountryList = countryListEntry.getList()
      .stream()
      .filter(e -> !e.getCountryRef().startsWith(NEGATIVE_COUNTRY_ID_PREFIX))
      .collect(Collectors.toList());

    return Response.ok()
      .entity(spireCountryList)
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