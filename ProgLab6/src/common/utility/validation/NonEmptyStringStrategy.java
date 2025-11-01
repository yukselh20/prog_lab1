package common.utility.validation;

public class NonEmptyStringStrategy implements ValidationStrategy<String> {
    @Override
    public String validate(String input) throws Exception {
        if (input == null || input.isEmpty()) {
            throw new Exception("The input cannot be empty");
        }
        return input;
    }
}