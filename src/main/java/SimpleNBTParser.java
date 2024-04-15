import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class SimpleNBTParser {

    public static final byte END = 0;
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

    public static JsonNode readBase64(String string) throws IOException {
        return readBytes(new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(string))).readAllBytes());
    }

    public static JsonNode readBytes(byte[] bytes) {

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte tagId = buffer.get();
        String name = INSTANCE.readName(buffer);

        // If the first tag has a name, wrap the data in a JsonObject
        if (name.length() > 0) {

            ObjectNode object = Json.createObject();

            JsonNode value = INSTANCE.readTag(tagId, buffer);

            object.set(name, value);

            return object;
        }

        return INSTANCE.readTag(tagId, buffer);
    }

    private SimpleNBTParser() {} // Utility class, no instantiation

    private JsonNode readTag(byte tagId, ByteBuffer buffer) {
        return switch (tagId) {
            case END -> null;
            case BYTE -> Json.createNode(buffer.get());
            case SHORT -> Json.createNode(buffer.getShort());
            case INT -> Json.createNode(buffer.getInt());
            case LONG -> Json.createNode(buffer.getLong());
            case FLOAT -> Json.createNode(buffer.getFloat());
            case DOUBLE -> Json.createNode(buffer.getDouble());
            case BYTE_ARRAY -> readByteArray(buffer);
            case STRING -> readString(buffer);
            case TAG_LIST -> readList(buffer);
            case COMPOUND_TAG -> readCompound(buffer);
            case INT_ARRAY -> readIntArray(buffer);
            case LONG_ARRAY -> readLongArray(buffer);
            default -> throw new IllegalArgumentException("Invalid NBT tag ID provided (" + tagId + "). Buffer position: " + buffer.position());
        };
    }

    private String readName(ByteBuffer buffer) {

        int size = buffer.getShort() & 0xFFFF;
        byte[] nameBytes = new byte[size];

        buffer.get(nameBytes);

        return new String(nameBytes, StandardCharsets.UTF_8);
    }

    private ArrayNode readByteArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        ArrayNode array = Json.createArray();

        for (int i = 0; i < size; i++) {
            array.add(buffer.get());
        }

        return array;
    }

    private ArrayNode readIntArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        ArrayNode array = Json.createArray();

        for (int i = 0; i < size; i++) {
            array.add(buffer.getInt());
        }

        return array;
    }

    private ArrayNode readLongArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        ArrayNode array = Json.createArray();

        for (int i = 0; i < size; i++) {
            array.add(buffer.getLong());
        }

        return array;
    }

    private JsonNode readString(ByteBuffer buffer) {

        int length = buffer.getShort() & 0xFFFF;
        byte[] bytes = new byte[length];

        buffer.get(bytes);

        return Json.createTextNode(new String(bytes, StandardCharsets.UTF_8));
    }

    private ArrayNode readList(ByteBuffer buffer) {

        byte tagId = buffer.get();
        int size = buffer.getInt();

        ArrayNode array = Json.createArray();

        for (int i = 0; i < size; i++) {
            array.add(readTag(tagId, buffer));
        }

        return array;
    }

    private ObjectNode readCompound(ByteBuffer buffer) {

        ObjectNode compound = Json.createObject();

        byte tagId = buffer.get();

        while (tagId != 0) {

            String name = readName(buffer);

            JsonNode value;

            try {
                value = readTag(tagId, buffer);
            } catch (Exception ex) {
                try {
                    System.out.println(Json.stringify(compound, true));
                } catch (Exception ex1) {
                    ex.printStackTrace();
                }
                throw ex;
            }

            if (value == null) {
                return compound;
            }

            compound.set(name, value);

            if (!buffer.hasRemaining()) {
                return compound;
            }

            tagId = buffer.get();
        }

        return compound;
    }

}