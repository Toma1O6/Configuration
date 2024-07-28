package dev.toma.configuration.config.value;

import dev.toma.configuration.config.adapter.TypeAdapter;
import dev.toma.configuration.config.exception.ConfigValueMissingException;
import dev.toma.configuration.config.format.IConfigFormat;
import net.minecraft.network.FriendlyByteBuf;

public class CharArrayValue extends AbstractArrayValue<Character> {

    public CharArrayValue(ValueData<Character[]> value) {
        super(value);
    }

    @Override
    public Character createElementInstance() {
        return 'a';
    }

    @Override
    protected void serialize(IConfigFormat format) {
        format.writeCharArray(this.getId(), this.get(Mode.SAVED));
    }

    @Override
    protected void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.setValue(format.readCharArray(this.getId()));
    }

    public static final class Adapter extends TypeAdapter<Character[]> {

        @Override
        public ConfigValue<Character[]> serialize(TypeAttributes<Character[]> attributes, Object instance, TypeSerializer serializer) throws IllegalAccessException {
            return new CharArrayValue(ValueData.of(attributes));
        }

        @Override
        public void encodeToBuffer(ConfigValue<Character[]> value, FriendlyByteBuf buffer) {
            Character[] chars = value.get();
            buffer.writeInt(chars.length);
            for (Character c : chars) {
                buffer.writeChar(c);
            }
        }

        @Override
        public Character[] decodeFromBuffer(ConfigValue<Character[]> value, FriendlyByteBuf buffer) {
            Character[] characters = new Character[buffer.readInt()];
            for (int i = 0; i < characters.length; i++) {
                characters[i] = buffer.readChar();
            }
            return characters;
        }
    }
}
