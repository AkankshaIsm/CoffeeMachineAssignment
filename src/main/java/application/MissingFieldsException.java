package application;

public class MissingFieldsException extends Exception {
    public MissingFieldsException(String errorMessage) {
        super(errorMessage);
    }
}
