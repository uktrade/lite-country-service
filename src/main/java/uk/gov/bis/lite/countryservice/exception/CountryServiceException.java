package uk.gov.bis.lite.countryservice.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CountryServiceException extends RuntimeException {

  public CountryServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public static class ServiceExceptionMapper implements ExceptionMapper<CountryServiceException> {

    @Override
    public Response toResponse(CountryServiceException exception) {
      return Response.status(500)
          .entity(exception.getMessage())
          .build();
    }

  }
}
