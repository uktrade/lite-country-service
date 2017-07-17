package uk.gov.bis.lite.countryservice.resource;

import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.exception.CountryRefNotFoundException;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.service.CountryDataService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/country-data")
public class CountryDataResource {

  private final CountryDataService countryDataService;

  @Inject
  public CountryDataResource(CountryDataService countryDataService) {
    this.countryDataService = countryDataService;
  }

  @GET
  @Path("/{countryRef}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCountryData(@PathParam("countryRef") String countryRef) {
    Optional<CountryView> countryView = countryDataService.getCountryData(countryRef);
    if (countryView.isPresent()) {
      return Response.ok(countryView).build();
    } else {
      throw new CountryRefNotFoundException("The following countryRef does not exist: " + countryRef);
    }
  }

  @PUT
  @Path("/{countryRef}")
  public Response updateCountryData(@PathParam("countryRef") String countryRef, CountryData countryData) {
    CountryData updateCountryData = new CountryData(countryRef, countryData.getSynonyms());
    countryDataService.bulkUpdateCountryData(Collections.singletonList(updateCountryData));
    return Response.ok().build();
  }

  @DELETE
  @Path("/{countryRef}")
  public Response deleteCountryData(@PathParam("countryRef") String countryRef) {
    countryDataService.deleteCountryData(countryRef);
    return Response.accepted().build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllCountryData() {
    List<CountryView> countryViews = countryDataService.getCountryData();
    return Response.ok(countryViews).build();
  }

  @PUT
  public Response bulkUpdateCountryData(List<CountryData> countryDataList) {
    countryDataService.bulkUpdateCountryData(countryDataList);
    return Response.ok().build();
  }

  @DELETE
  public Response deleteAllCountryData() {
    countryDataService.deleteAllCountryData();
    return Response.accepted().build();
  }

}
