package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CountryRefNotFoundException extends WebApplicationException {

  public CountryRefNotFoundException(String message) {
    super(message, Response.Status.NOT_FOUND);
  }

}
