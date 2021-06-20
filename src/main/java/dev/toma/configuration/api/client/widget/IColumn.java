package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.IClientSettings;

import javax.annotation.Nullable;

public interface IColumn {

    boolean isAbsolute();

    int getColumnWidth(int totalWidth);

    int getMargin();

    IColumn setMargin(int px);

    Widget init(IConfigType<?> configType, IClientSettings settings, int x, int y, int width, int height);

    static IColumn absolute(int px, @Nullable WidgetType<?> type) {
        return new Absolute(px, type);
    }

    static IColumn relative(double displayPart, @Nullable WidgetType<?> type) {
        return new Relative(displayPart, type);
    }

    abstract class AbstractColumn implements IColumn {

        private int margin;
        private final WidgetType<?> type;

        public AbstractColumn(WidgetType<?> type) {
            this.type = type;
        }

        @Override
        public int getMargin() {
            return margin;
        }

        @Override
        public IColumn setMargin(int margin) {
            this.margin = margin;
            return this;
        }

        @Override
        public Widget init(IConfigType<?> configType, IClientSettings settings, int x, int y, int width, int height) {
            if (type == null) {
                return null;
            }
            return type.instantiateWidget(configType, settings, x + margin, y, width - margin, height);
        }
    }

    class Absolute extends AbstractColumn {

        final int absoluteWidth;

        protected Absolute(int absoluteWidth, WidgetType<?> type) {
            super(type);
            this.absoluteWidth = absoluteWidth;
            if (absoluteWidth <= 0)
                throw new IllegalArgumentException("Invalid argument: Number must be bigger than 0");
        }

        @Override
        public boolean isAbsolute() {
            return true;
        }

        @Override
        public int getColumnWidth(int totalWidth) {
            return absoluteWidth;
        }
    }

    class Relative extends AbstractColumn {

        final double part;

        protected Relative(double part, WidgetType<?> type) {
            super(type);
            this.part = part;
            if (part < 0 || part > 1)
                throw new IllegalArgumentException("Invalid argument: Number must be in range from 0 to 1");
        }

        @Override
        public boolean isAbsolute() {
            return false;
        }

        @Override
        public int getColumnWidth(int totalWidth) {
            return (int) (totalWidth * part);
        }
    }
}
