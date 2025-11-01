package client.commands;

import common.dto.Request;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.exceptions.WrongAmountOfElementsException;

public interface ICommand {
    Request build(String args) throws IncorrectInputInScriptException, InvalidFormException, WrongAmountOfElementsException;
}