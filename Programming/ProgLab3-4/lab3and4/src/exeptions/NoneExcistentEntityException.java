package exeptions;

class NoneExistentEntityException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String entityName;

    public NoneExistentEntityException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return "Error: " + super.getMessage() + " Entity: " + entityName;
    }
}