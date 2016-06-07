package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CountrySetNotFoundException extends RuntimeException {
  public CountrySetNotFoundException(String message) {
    super(message);
  }

  public static class NotFoundExceptionMapper implements ExceptionMapper<CountrySetNotFoundException> {

    @Override
    public Response toResponse(CountrySetNotFoundException exception) {
      return Response.status(404)
          .entity(exception.getMessage())
          .build();
    }
  }
}
