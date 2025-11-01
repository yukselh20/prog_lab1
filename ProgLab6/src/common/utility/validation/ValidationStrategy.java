package common.utility.validation;
public interface ValidationStrategy<T> {
    T validate(String input) throws Exception;
}