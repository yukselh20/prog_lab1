package common.utility.validation;

public class YesNoStrategy implements ValidationStrategy<String> {
    @Override
    public String validate(String input) throws Exception {
        String lowerInput = input.toLowerCase();
        if (!lowerInput.equals("yes") && !lowerInput.equals("no")) {
            throw new Exception("Please enter 'yes' or 'no'");
        }
        return lowerInput;
    }
}