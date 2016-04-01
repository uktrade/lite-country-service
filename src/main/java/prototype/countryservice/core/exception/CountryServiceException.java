package prototype.countryservice.core.exception;

public class CountryServiceException extends Exception {

    public CountryServiceException(String message) {
        super(message);
    }

    public CountryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CountryServiceException(Throwable cause) {
        super(cause);
    }
}
