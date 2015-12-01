package no.nb.microservices.catalogsearchindex.exception;

public class TestDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestDataException(Throwable cause) {
        super(cause);
    }

    public TestDataException(String message, Throwable cause) {
       super(message, cause);
   }
}
