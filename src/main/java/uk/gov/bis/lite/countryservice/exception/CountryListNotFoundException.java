package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CountryListNotFoundException extends WebApplicationException {

  public CountryListNotFoundException(String message) {
    super(message, Response.Status.NOT_FOUND);
  }

}
