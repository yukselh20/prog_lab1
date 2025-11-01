package common.dto;

import java.io.Serializable; // Enumlar zaten serializable ama belirtmek zarar vermez

public enum CommandType implements Serializable {
    HELP,
    INFO,
    SHOW,
    INSERT,
    UPDATE,
    REMOVE_KEY,
    CLEAR,
    EXECUTE_SCRIPT,
    REMOVE_LOWER,
    REPLACE_IF_LOWER,
    COUNT_GREATER_THAN_DISCOUNT,
    FILTER_STARTS_WITH_NAME,
    PRINT_DESCENDING,
    REGISTER
    // EXIT istemci tarafında işlenir,
    // SAVE sunucuya özeldir (veya kaldırılır)
}