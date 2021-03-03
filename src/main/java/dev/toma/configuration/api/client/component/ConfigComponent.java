package dev.toma.configuration.api.client.component;

import dev.toma.configuration.api.type.AbstractConfigType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class ConfigComponent<T extends AbstractConfigType<?>> extends Component {

    protected final T configType;
    private final List<IComponentListener<T>> listeners = new ArrayList<>();

    public ConfigComponent(T configType, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.configType = configType;
    }

    public void addListener(IComponentListener<T> listener) {
        this.listeners.add(listener);
    }

    public void updateListeners() {
        listeners.forEach(listener -> listener.onChange(configType));
    }

    public T getConfigElement() {
        return configType;
    }
}
