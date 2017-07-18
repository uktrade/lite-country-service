package uk.gov.bis.lite.countryservice.resource;

import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.exception.CountryRefNotFoundException;
import uk.gov.bis.lite.countryservice.service.CountryService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/country-data")
public class CountryDataResource {

  private final CountryService countryService;

  @Inject
  public CountryDataResource(CountryService countryService) {
    this.countryService = countryService;
  }

  @GET
  @Path("/{countryRef}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCountryData(@PathParam("countryRef") String countryRef) {
    Optional<CountryView> countryView = countryService.getCountryView(countryRef);
    if (countryView.isPresent()) {
      return Response.ok(countryView).build();
    } else {
      throw new CountryRefNotFoundException("The following countryRef does not exist: " + countryRef);
    }
  }

  @PUT
  @Path("/{countryRef}")
  public Response updateCountryData(@PathParam("countryRef") String countryRef, @NotNull CountryData countryData) {
    List<String> unmatchedCountryRefs = countryService.getUnmatchedCountryRefs(Collections.singletonList(countryData));
    if (!unmatchedCountryRefs.isEmpty()) {
      throw new CountryRefNotFoundException("The following countryRef does not exist: " + unmatchedCountryRefs.get(0));
    } else {
      CountryData updateCountryData = new CountryData(countryRef, countryData.getSynonyms());
      countryService.bulkUpdateCountryData(Collections.singletonList(updateCountryData));
      return Response.ok().build();
    }
  }

  @DELETE
  @Path("/{countryRef}")
  public Response deleteCountryData(@PathParam("countryRef") String countryRef) {
    countryService.deleteCountryData(countryRef);
    return Response.accepted().build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllCountryData() {
    List<CountryView> countryViews = countryService.getCountryViews();
    return Response.ok(countryViews).build();
  }

  @PUT
  public Response bulkUpdateCountryData(@NotNull List<CountryData> countryDataList) {
    List<String> unmatchedCountryRefs = countryService.getUnmatchedCountryRefs(countryDataList);
    Set<String> duplicateCountryRefs = countryService.getDuplicates(countryDataList);
    if (!unmatchedCountryRefs.isEmpty()) {
      throw new CountryRefNotFoundException("The following countryRef do not exist: " + String.join(", ", unmatchedCountryRefs));
    } else if (!duplicateCountryRefs.isEmpty()) {
      throw new WebApplicationException("The following countryRef are duplicate: " + String.join(", ", duplicateCountryRefs));
    } else {
      countryService.bulkUpdateCountryData(countryDataList);
      return Response.ok().build();
    }
  }

  @DELETE
  public Response deleteAllCountryData() {
    countryService.deleteAllCountryData();
    return Response.accepted().build();
  }

}
