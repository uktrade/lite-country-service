package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.WebApplicationException;

public class CountryServiceException extends WebApplicationException {

  public CountryServiceException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
