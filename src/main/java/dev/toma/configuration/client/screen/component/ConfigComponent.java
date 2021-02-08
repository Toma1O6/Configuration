package dev.toma.configuration.client.screen.component;

import dev.toma.configuration.api.type.AbstractConfigType;

public abstract class ConfigComponent<T extends AbstractConfigType<?>> extends Component {

    protected final T configType;

    public ConfigComponent(T configType, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.configType = configType;
    }

    public void onUpdate() {

    }

    public T getConfigElement() {
        return configType;
    }
}
