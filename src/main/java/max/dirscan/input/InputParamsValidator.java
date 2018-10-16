package max.dirscan.input;

import max.dirscan.exceptions.ValidationParamsException;

public interface InputParamsValidator {

    void validate(String... inputParams) throws ValidationParamsException;
}
