package dev.toma.configuration.internal;

import dev.toma.configuration.api.IConfigWriter;
import dev.toma.configuration.api.IObjectSpec;

public class ObjectSpec implements IObjectSpec {

    private final String id;
    private final String[] desc;
    private final IConfigWriter writer;

    public ObjectSpec(String id, IConfigWriter writer, String... desc) {
        this.id = id;
        this.desc = desc;
        this.writer = writer;
    }

    @Override
    public String getObjectID() {
        return id;
    }

    @Override
    public String[] getObjectDescription() {
        return desc;
    }

    @Override
    public IConfigWriter getWriter() {
        return writer;
    }
}
