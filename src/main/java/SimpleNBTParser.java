import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SimpleNBTParser {

    // Tag IDs, unused for now
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

    public static Map<String, Object> readBase64(String string) throws IOException {
        return readBytes(new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(string))).readAllBytes());
    }

    public static Map<String, Object> readBytes(byte[] bytes) {
        return new SimpleNBTParser().readCompound(ByteBuffer.wrap(bytes));
    }

    private SimpleNBTParser() {} // Utility class, no instantiation

    private Object read(byte tagId, ByteBuffer buffer) {
        return switch (tagId) {
            case BYTE -> buffer.get();
            case SHORT -> buffer.getShort();
            case INT -> buffer.getInt();
            case LONG -> buffer.getLong();
            case FLOAT -> buffer.getFloat();
            case DOUBLE -> buffer.getDouble();
            case BYTE_ARRAY -> readByteArray(buffer);
            case STRING -> readString(buffer);
            case TAG_LIST -> readList(buffer);
            case COMPOUND_TAG -> readCompound(buffer);
            case INT_ARRAY -> readIntArray(buffer);
            case LONG_ARRAY -> readLongArray(buffer);
            default -> throw new IllegalArgumentException("Invalid NBT tag provided");
        };
    }

    private String readName(ByteBuffer buffer) {

        int size = buffer.getShort() & 0xFF;
        byte[] nameBytes = new byte[size];

        buffer.get(nameBytes);

        return new String(nameBytes, StandardCharsets.UTF_8);
    }

    private byte[] readByteArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        byte[] bytes = new byte[size];

        buffer.get(bytes);

        return bytes;
    }

    private int[] readIntArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        int[] ints = new int[size];

        for (int i = 0; i < size; i++) {
            ints[i] = buffer.getInt();
        }

        return ints;
    }

    private long[] readLongArray(ByteBuffer buffer) {

        int size = buffer.getInt();

        long[] longs = new long[size];

        for (int i = 0; i < size; i++) {
            longs[i] = buffer.getLong();
        }

        return longs;
    }

    private String readString(ByteBuffer buffer) {

        int length = buffer.getShort() & 0xFF;

        byte[] bytes = new byte[length];

        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private List<Object> readList(ByteBuffer buffer) {

        byte tagId = buffer.get();
        int size = buffer.getInt();

        List<Object> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(read(tagId, buffer));
        }

        return list;
    }

    private Map<String, Object> readCompound(ByteBuffer buffer) {

        Map<String, Object> compound = new HashMap<>();

        byte tagId = buffer.get();

        while (tagId != 0) {

            String name = readName(buffer);
            Object value = read(tagId, buffer);

            compound.put(name, value);

            // The end has been reached
            if (!buffer.hasRemaining()) {
                return compound;
            }

            tagId = buffer.get();
        }

        return compound;
    }

}
