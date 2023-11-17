import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {

        String b64data = "H4sIAAAAAAAAAO3Pu0rEQBgF4D/ZZE2iqAgWdmMtFlaKWBjjrgpmA7GwlNEZswO5yGSWqC+U98iDicmKsI2CjdVpD//lfAGRT5YKiOjQJlsJ690iN6oWpXEDGhmejci/VkJOc57V/dRHQGtC1S85f/PJua209PrUpt2uPbnQslFlxm7KTEuhZGloo2ufuzaPkjhOZh45M15I2uzDmGcFZ5GWvKCAtiavRvPQGK0eF0bW3lCF1uPwKg4fonQSxsOP8SUveCaX/4aq4++qZ3+o6tB21x5/XTplXft0cNSne339O8NNzbgQUrBmLktWzyuzTzurBBamaXK/CpnmqjQs1LpqfoS4y61fCecggAACCCCAAAIIIIAAAggggAACCCCAAAIIIIAAAggggAACCCCAAAII/06gT/oVR3M8IgAA";

        Map<String, Object> data = SimpleNBTParser.readBase64(b64data);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof HashMap<?, ?> map) {
                for (Map.Entry<?, ?> entry2 : map.entrySet()) {
                    if (entry2.getValue() instanceof ArrayList<?> list) {
                        for (Object object : list) {
                            if (object instanceof HashMap<?,?> map1) {
                                System.out.println(map1.get("tag"));
                            }
                        }
                    }
                }
            }
        }

    }

}
