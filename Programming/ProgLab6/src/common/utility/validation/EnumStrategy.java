package common.utility.validation;

public class EnumStrategy<T extends Enum<T>> implements ValidationStrategy<T> {
    private final Class<T> enumClass;

    public EnumStrategy(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T validate(String input) throws Exception {
        try {
            return Enum.valueOf(enumClass, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid value. Please choose from the available options");
        }
    }
}