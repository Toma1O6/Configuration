package dev.toma.configuration.api;

/**
 * Object containing specification for custom object creation.
 */
public interface IObjectSpec {

    /**
     * @return Object ID
     */
    String getObjectID();

    /**
     * @return Object description
     */
    String[] getObjectDescription();

    /**
     * @return Writer for creating config structure
     */
    IConfigWriter getWriter();
}
