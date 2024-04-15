import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

public final class Json {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectNode createObject() {
        return MAPPER.createObjectNode();
    }

    public static ArrayNode createArray() {
        return MAPPER.createArrayNode();
    }

    public static TextNode createTextNode(String text) {
        return new TextNode(text);
    }

    public static POJONode createNode(Object primitive) {
        return new POJONode(primitive);
    }

    public static JsonNode parse(String json) throws JsonProcessingException {
        return MAPPER.readTree(json);
    }

    public static <T> T fromJson(JsonNode node, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.treeToValue(node, clazz);
    }

    public static JsonNode toJson(Object object) {
        return MAPPER.valueToTree(object);
    }

    public static String stringify(JsonNode node, boolean pretty) throws JsonProcessingException {

        ObjectWriter writer = MAPPER.writer();

        if (pretty) {
            writer = writer.with(SerializationFeature.INDENT_OUTPUT);
        }

        return writer.writeValueAsString(node);
    }

    public String getName() {

        try {
            throw new Exception("name");
        } catch (Exception e) {
            return e.getMessage();
        }

    }

}
