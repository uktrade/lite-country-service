package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CountriesNotFoundException extends RuntimeException {
  public CountriesNotFoundException(String message) {
    super(message);
  }

  public static class NotFoundExceptionMapper implements ExceptionMapper<CountriesNotFoundException> {

    @Override
    public Response toResponse(CountriesNotFoundException exception) {
      return Response.status(404)
          .entity(exception.getMessage())
          .build();
    }
  }
}
