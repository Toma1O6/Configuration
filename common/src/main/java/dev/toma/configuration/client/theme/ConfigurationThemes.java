package dev.toma.configuration.client.theme;

import java.util.function.Consumer;

public final class ConfigurationThemes {

    public static final Consumer<ConfigTheme> DEFAULT_THEME = DefaultConfigTheme::configure;
}
