package common.utility.validation;

public class DiscountStrategy implements ValidationStrategy<Long> {
    @Override
    public Long validate(String input) throws Exception {
        long value = Long.parseLong(input);
        if (value <= 0 || value > 100) {
            throw new Exception("The discount must be greater than 0 and not exceed 100");
        }
        return value;
    }
}