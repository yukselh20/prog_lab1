package common.utility.validation;

public class PositiveIntegerStrategy implements ValidationStrategy<Integer> {
    @Override
    public Integer validate(String input) throws Exception {
        int value = Integer.parseInt(input);
        if (value <= 0) {
            throw new Exception("The number must be greater than 0");
        }
        return value;
    }
}