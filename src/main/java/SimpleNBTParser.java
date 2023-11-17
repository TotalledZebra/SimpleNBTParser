import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class SimpleNBTParser {

    public static final byte BYTE = 1;
    public static final byte SHORT = 2;
    public static final byte INT = 3;
    public static final byte LONG = 4;
    public static final byte FLOAT = 5;
    public static final byte DOUBLE = 6;
    public static final byte BYTE_ARRAY = 7;
    public static final byte STRING = 8;
    public static final byte TAG_LIST = 9;
    public static final byte COMPOUND_TAG = 10;
    public static final byte INT_ARRAY = 11;
    public static final byte LONG_ARRAY = 12;

    private static final SimpleNBTParser INSTANCE = new SimpleNBTParser();

    public static JsonObject readBase64(String string) throws IOException {
        return readBytes(new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(string))).readAllBytes());
    }

    public static JsonObject readBytes(byte[] bytes) {
        return INSTANCE.readCompound(ByteBuffer.wrap(bytes));
    }

    private SimpleNBTParser() {} // Utility class, no instantiation

    private JsonElement read(byte tagId, ByteBuffer buffer) {
        return switch (tagId) {
            case BYTE -> new JsonPrimitive(buffer.get());
            case SHORT -> new JsonPrimitive(buffer.getShort());
            case INT -> new JsonPrimitive(buffer.getInt());
            case LONG -> new JsonPrimitive(buffer.getLong());
            case FLOAT -> new JsonPrimitive(buffer.getFloat());
            case DOUBLE -> new JsonPrimitive(buffer.getDouble());
            case BYTE_ARRAY -> readByteArray(buffer);
            case STRING -> readString(buffer);
            case TAG_LIST -> readList(buffer);
            case COMPOUND_TAG -> readCompound(buffer);
            case INT_ARRAY -> readIntArray(buffer);
            case LONG_ARRAY -> readLongArray(buffer);
            default -> throw new IllegalArgumentException("Invalid NBT tag ID provided");
        };
    }

    private String readName(ByteBuffer buffer) {

        int size = buffer.getShort() & 0xFF;
        byte[] nameBytes = new byte[size];

        buffer.get(nameBytes);

        return new String(nameBytes);
    }

    private JsonArray readByteArray(ByteBuffer buffer) {

        JsonArray array = new JsonArray();

        int size = buffer.getInt();

        for (int i = 0; i < size; i++) {
            array.add(buffer.get());
        }

        return array;
    }

    private JsonArray readIntArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        JsonArray array = new JsonArray();

        for (int i = 0; i < size; i++) {
            array.add(buffer.getInt());
        }

        return array;
    }

    private JsonArray readLongArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        JsonArray array = new JsonArray();

        for (int i = 0; i < size; i++) {
            array.add(buffer.getLong());
        }

        return array;
    }

    private JsonPrimitive readString(ByteBuffer buffer) {

        int length = buffer.getShort() & 0xFF;

        byte[] bytes = new byte[length];

        buffer.get(bytes);

        return new JsonPrimitive(new String(bytes));
    }

    private JsonArray readList(ByteBuffer buffer) {

        byte tagId = buffer.get();
        int size = buffer.getInt();

        JsonArray array = new JsonArray();

        for (int i = 0; i < size; i++) {
            array.add(read(tagId, buffer));
        }

        return array;
    }

    private JsonObject readCompound(ByteBuffer buffer) {

        JsonObject compound = new JsonObject();

        byte tagId = buffer.get();

        while (tagId != 0) {

            String name = readName(buffer);
            JsonElement value = read(tagId, buffer);

            compound.add(name, value);

            if (!buffer.hasRemaining()) {
                return compound;
            }

            tagId = buffer.get();
        }

        return compound;
    }

}