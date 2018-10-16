package max.dirscan.input;

import max.dirscan.exceptions.ValidationParamsException;

public class InputParamsLengthValidator implements InputParamsValidator {

    @Override
    public void validate(String... inputParams) throws ValidationParamsException {
        if(inputParams.length == 0) {
            throw new ValidationParamsException("Input params validation error. Params has invalid length = 0");
        }
    }
}
