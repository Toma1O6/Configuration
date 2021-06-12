package dev.toma.configuration.api;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Restriction is used to restrict user input which is not valid for
 * use in config.
 * @param <I> Restricted type
 */
public interface IRestriction<I> {

    /**
     * Validates whether input is valid or not
     * @param input Input to validate
     * @return Validation result
     */
    boolean isInputValid(I input);

    /**
     * Allows this restriction to add additional information into config types
     * @param descriptionLines List of already added description lines
     */
    void addDescription(List<String> descriptionLines);

    /**
     * @return Message which will be shown into UI when input validation fails
     */
    String[] getUserErrorMessage();
}
