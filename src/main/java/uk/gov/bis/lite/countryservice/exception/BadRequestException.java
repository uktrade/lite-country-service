package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

  public BadRequestException(String message) {
    super(message, Response.Status.BAD_REQUEST);
  }

}
