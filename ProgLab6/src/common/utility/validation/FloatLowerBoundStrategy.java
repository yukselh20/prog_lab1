package common.utility.validation;

public class FloatLowerBoundStrategy implements ValidationStrategy<Float> {
    private final float lowerBound;
    private final String errorMessage;

    public FloatLowerBoundStrategy(float lowerBound, String errorMessage) {
        this.lowerBound = lowerBound;
        this.errorMessage = errorMessage;
    }

    @Override
    public Float validate(String input) throws Exception {
        float value = Float.parseFloat(input);
        if (value <= lowerBound) {
            throw new Exception(errorMessage);
        }
        return value;
    }
}