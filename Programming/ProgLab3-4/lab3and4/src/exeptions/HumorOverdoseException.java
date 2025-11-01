package exeptions;

public class HumorOverdoseException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    public HumorOverdoseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Warning: " + super.getMessage();
    }
}