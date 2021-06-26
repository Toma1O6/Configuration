package dev.toma.configuration.api.client.widget;

import dev.toma.configuration.api.IConfigType;
import dev.toma.configuration.api.client.IClientSettings;

import javax.annotation.Nullable;

/**
 * Columns are used by widget containers for nice placements with different scales.
 * Column instances can be produced easily using either
 * {@link IColumn#absolute(int, WidgetType)} or {@link IColumn#relative(double, WidgetType)}.
 *
 * You can also set custom margin and style for each column
 */
public interface IColumn {

    /**
     * @return If size of this column is always the same (i.e. 20px)
     */
    boolean isAbsolute();

    /**
     * Get width of this column
     * @param totalWidth Container width
     * @return Column width
     */
    int getColumnWidth(int totalWidth);

    /**
     * @return Horizontal offset from previous column
     */
    int getMargin();

    /**
     * Sets margin
     * @param px Pixel offset (different for each Gui scale)
     */
    IColumn setMargin(int px);

    /**
     * Set widget style which will be used in this column
     * @param styleID String key of style, which should be registered using {@link dev.toma.configuration.api.client.IWidgetManager#setStyle(WidgetType, IWidgetStyle, String)}
     */
    IColumn setStyle(String styleID);

    /**
     * Called internally by widget containers, you shouldn't need to worry about this method at all
     */
    Widget init(IConfigType<?> configType, IClientSettings settings, int x, int y, int width, int height);

    /**
     * @return Widget type set for this column
     */
    WidgetType<?> getType();

    /**
     * Creates column with absolute size
     * @param px Column size
     * @param type Widget type for this column
     * @return Column with absolute size
     */
    static IColumn absolute(int px, @Nullable WidgetType<?> type) {
        return new Absolute(px, type);
    }

    /**
     * Creates column with size relative to container size
     * @param displayPart Value of how much space from container size will this column take. Range [0.0 -> 1.0].
     *                    Total sum of relative column sizes should always be equal to 1!
     * @param type Widget type for this column
     * @return Column with size relative to container size
     */
    static IColumn relative(double displayPart, @Nullable WidgetType<?> type) {
        return new Relative(displayPart, type);
    }

    abstract class AbstractColumn implements IColumn {

        private int margin;
        private String styleID;
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
        public IColumn setStyle(String styleID) {
            this.styleID = styleID;
            return this;
        }

        @Override
        public Widget init(IConfigType<?> configType, IClientSettings settings, int x, int y, int width, int height) {
            if (type == null) {
                return null;
            }
            return type.instantiateWidget(configType, settings, x + margin, y, width - margin, height, styleID);
        }

        @Override
        public WidgetType<?> getType() {
            return type;
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

        double part;

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

        public double getPart() {
            return part;
        }

        public void addPart(double part) {
            this.part += part;
        }
    }
}
