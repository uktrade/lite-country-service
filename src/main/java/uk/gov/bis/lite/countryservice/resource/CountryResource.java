package uk.gov.bis.lite.countryservice.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.exception.CountryListNotFoundException;
import uk.gov.bis.lite.countryservice.service.CountryService;

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
public class CountryResource {

  static final String NEGATIVE_COUNTRY_ID_PREFIX = "CTRY-";
  private final CountryService countryService;
  private final Integer cacheExpirySeconds;

  @Inject
  public CountryResource(CountryService countryService, @Named("cacheExpirySeconds") Integer cacheExpirySeconds) {
    this.countryService = countryService;
    this.cacheExpirySeconds = cacheExpirySeconds;
  }

  @GET
  @Path("set/{countrySetName}")
  @Timed // measures the duration of requests to a resource
  public Response getCountryList(@PathParam("countrySetName") @NotEmpty String countrySetName) {
    Optional<List<CountryView>> countryViews = countryService.getCountrySet(countrySetName);
    if (countryViews.isPresent()) {
      return buildResponse(countryViews.get());
    } else {
      throw new CountryListNotFoundException("Country set does not exist - " + countrySetName);
    }
  }

  @GET
  @Path("group/{groupName}")
  @Timed // measures the duration of requests to a resource
  public Response getCountryGroups(@PathParam("groupName") @NotEmpty String groupName) {
    Optional<List<CountryView>> countryViews = countryService.getCountryGroup(groupName);
    if (countryViews.isPresent()) {
      return buildResponse(countryViews.get());
    } else {
      throw new CountryListNotFoundException("Country group does not exist - " + groupName);
    }
  }

  private Response buildResponse(List<CountryView> countryViews) {
    //Filter "negative" country IDs
    List<CountryView> spireCountryList = countryViews
        .stream()
        .filter(e -> !e.getCountryRef().startsWith(NEGATIVE_COUNTRY_ID_PREFIX))
        .collect(Collectors.toList());

    return Response.ok()
        .entity(spireCountryList)
        .cacheControl(getCacheControl())
        .build();
  }

  private CacheControl getCacheControl() {
    long lastCached = countryService.getLastCached();
    CacheControl cacheControl = new CacheControl();
    int elapsedTime = (int) ((System.currentTimeMillis() - lastCached) / 1000);

    cacheControl.setMaxAge(cacheExpirySeconds - elapsedTime);

    return cacheControl;
  }

}