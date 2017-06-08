package uk.gov.bis.lite.countryservice.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ErrorResponse {
  public static Response buildResponse(String message, int status) {
    Map<String,Object> errorMessage = new HashMap<>();
    errorMessage.put("code", status);
    errorMessage.put("message", message);

    return Response.status(status)
        .entity(errorMessage)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .build();
  }

}
