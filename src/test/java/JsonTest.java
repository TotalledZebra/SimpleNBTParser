import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class JsonTest {

    String b64data = "H4sIAAAAAAAAAO3Pu0rEQBgF4D/ZZE2iqAgWdmMtFlaKWBjjrgpmA7GwlNEZswO5yGSWqC+U98iDicmKsI2CjdVpD//lfAGRT5YKiOjQJlsJ690iN6oWpXEDGhmejci/VkJOc57V/dRHQGtC1S85f/PJua209PrUpt2uPbnQslFlxm7KTEuhZGloo2ufuzaPkjhOZh45M15I2uzDmGcFZ5GWvKCAtiavRvPQGK0eF0bW3lCF1uPwKg4fonQSxsOP8SUveCaX/4aq4++qZ3+o6tB21x5/XTplXft0cNSne339O8NNzbgQUrBmLktWzyuzTzurBBamaXK/CpnmqjQs1LpqfoS4y61fCecggAACCCCAAAIIIIAAAggggAACCCCAAAIIIIAAAggggAACCCCAAAII/06gT/oVR3M8IgAA";
    String b64data2 = "H4sIAAAAAAAAAF2US28iRxCAy/Z6A2QlK5e8pESTyI6MvNjzZMA3DCyMl4ftwcZwsZqZGhiYB5npWS++5QfkGCmXSLnxB3LMiV8S5YdEqQHbuwlC6u6qr17dNZUDyMKWmwOArW3Ydu2tn7dgtxomAd/KwQ5n4yy8wMCaQPrbgWzTtfGNx8YxHf/JQc6cJZ7XvQ8wysC2YcO+WnYsZ6RgwWY6FhTHZoWR6pQLim47GrNKzshWyO4iCucYcRfjLGQ4vudJhPE6jQzs3jAvQfgDF+fi8HYi2rfnnrUwinTumaLXNaZz3QhuFqOqUTR80jcrxdai/BGrcdbXvIFyPhkGl8nIvxFbypWHzSvJ8q/fdWverF2zxEH/UhtMZ1rnoa52HjxvMG0vOv3z2bAx9NsPA6XTHyjtqSF2a9fywL+Uh7R2+8Npt3FJss603Ws/DGvWQ7tWkYbV87JzK76lCnLwie3Gc48t6O5aYYQZEn4O366WegNZJJgWyU6F1dKWyxotpUNNl/NwSPpq5HKhOmGBtQasI+2AlvLhkXyQX5MkyMPrJ7LGfDZ+JJUnVH1CdflYIvqE6LMwSGKhwjmzZoI5R7Q3Rur/bWizTqSJzOOTFGJHkqRsKEl7pGRFPtZLefiByBo6GMS4QYvio79HUJKV42IevifuOSo7Uv8LlY5lNU9XJJG0EYX3fCLc0PZ1SlGbcLS4GwYfZL1JGAWxYBgGGe2vlsU31IKCiVxYl3kqmDP0kIcRwfqheKJuEmhELODxumzp799+EkweYTCmYKRjgZ160sk7KX/9XfjoelO9Q87wHUYL+HpNjaS0UP050Mz1vPSLOCBhnVkTYe6ihcJ4E5KlVUupQYxWSKG+oa0Vhp4d3gdChHayqZD+ozBA+JTU1D7cDcbfkVNKXu5NXPIT+RRr45qOSWAjs4W//vwlpb5aLbXV0qtfGFWhdt1p1LsdoVlvteu9DLzoMB/hSyJa7njCP1xQEz0fOXXsXv09jxg1SOSOEo5xBj6zk2CMYXAXp8XdRfgjvKpWepVqt31mnkrqDnwxYjGanPGzMIz5BUYWBjQwkJLJZiDjh7bruBjBrpcGzaTTBfbMt/VWvde9utvkloNX6XSha/LJON6Bl3z9uumw2YHc/Pn5SbBL2vG6P9IDUFlJQi73Ra2kOdKoWNCKolhQSxIWyo6tFxzGtKJdLmlqyaLJ5XL073iaEFlnIMtdH2PO/DnsSdJJ6URWhPKpVhIqbYBteLl5fBp58C+BOkCyJAUAAA\u003d\u003d";
    String b64data3 = "H4sIAAAAAAAAAB2NQQqCQBhGv1ErHaKu0KoLtGtnarRIhTpA/OGfDIwZ4wxUF/IeHiyyto/3eBKIIJQEIDx4qsJaYJK07m6FhG+p9hEdVMV7TXU3Wh+JWaW6h6ZXhODYGg5/LeZDfxt6nZR5XhYhgoIaxmKE8dsZXu20YwuJZfa0hmJrjbo6y134f8pTll5O5TnbbgAP05Qaqhk+8AVIrd2eoAAAAA==";

    String b64data4_crimson_legs = "H4sIAAAAAAAAAF1TX2sTQRCfNKltolL/gApKWaGCkqa9/I99siZpGkijJFoRkbJ3N7ksvbuNt5vWfgNBEEERkfbJhzz6DXzIJ9E8+DHEuUttqw83tzP7m9/8dnY2BZCEmEgBQGwGZoQdW47BbFUOfR1LQVxzJwbnn/pmgHyXmy7G4pDcFDZuuNxRlPQ7BXO2UAOXHyQh0ZIBzlP0Flwdj8pdHaDv6P4aG4+sdN6A6xSsBkKzGve4g9N4zrgDV2hjE7k7xXKKFaNYDXvoK5wGi0W4RrGmr9F1hYO+9XcDFulfeUHf5MtHsi//c0nRwnhkbnGfs8dSuqy5DUtE1Qi4rxWBzXTFmBy+Y2e5aX8FLpPCbdRIQEpik8NP/ybm0/nJ52+sKj2Ta/ZMKFt6USLAIlV+IjBAmz2U/lCtsZr0BC3YXWO1cA8qhNuQAcM9DA6Yhy4i2xWuyxwu/LAzWTJlpbm1y2SP1qXjfEjR+ufR+6hOnCyUydS51T8DYgQ4zXf+6sW0sZKdfHjDuvvCd+ACRTrcd/CUK0dmXUf1jRMCxbirJLm22hcDDOOkkTOipQPDKvm2CNDSQvqsL7Qm8uODoY/eASO07iMbcN2nw8BFSgj9iO24cIlMSypkWTYVzXvU91BIIVReph74UkfdCdn5FBV2OtwNm90XigmNHrPotkxkAfZk4KB9G5YjRGFy9DXk6+CrIakNWfnxzXWjzufo8NYK3KA2jkduq96ot2vrneeMVo1mu9GNw6wlXRnALz47D4k29zAc9BLNtKfo5C10HNJGNwQL9dc64OtaB8IcalQpSPETJw5ze9OhIvUJelEeTebOgCYz9GE+fIdwqdppbnUftXdOqydNqdSOpqEi3AwpGA4JuGTb2RzPl+5neKVSzhQsg2d4qVTIGEWrUrCNEjexl4CkFh5Sz7wBPfW333/cfEAccG76FIkP/gAGdapaCwQAAA==";

    String invalid = "H4sIAAAAAAAAAFWT346aQBTGR3fbVW/atOk9TZvsRWODiLv1ohcs/hvXgVVQhJtmhFFHBzD8WcR36Dv0zvfwwZqOuE3aMAnJOd98v+8cQg2AKijRGgCgVAZl6pV+lsArNUyDpFQDVwleXYHqgHqkx/Aq5qrfNVAztiljehaQqALK0AOfW3dNudXGXl3G8rLexHK7vsCtdv1u6Yr3pOW5916D33uKwh2JEkriKqgkZJ+kEYkLdAW8mmGWktIvkoUrqA5FbDWY25ysF3OFwk64Qua0hTbdDHWUA+KPlmePUFWoOxg+Oz6LnSnbQqrcQRVKyIQyMhUZbfgdaSzq/bGkW/CAfNi0N2yLDnCP+tM9ojBWqbKCwUO+kJzdoj/Tbc69+AxTbGm83trZBzdH5laC9IUpsWDh90RvPmRTf7b3LJY71rjI6Q2GDce46Lz+TPYGs9yZo6J3mY3zmDYypvL/tbN+Poxf2KE3mGQ6/fb8j0e6sFjq8Ey25Ygjv8U8tS0683WRYyTxeTooczb2wT48MNQfZ5o02/Cd5Xz+XDPXVDN7a8fqthxrmtv+NHPM9cbZrDe21N3b5/0elEaxG1/zNWvCdAM+FtkGIn8/tNVA/M6/Vg3ceDTeMZxXwfUojEiFF2/Ap9Pxnh/OiQVEAxoGQrylgeCucbAisZCHaQRuucIvmrexgHc7giMcuERIQgELpyPp0XjNJV+545eLnx2mgosDgfPOurM7TYgvcGcc5MLFDLzjynBZMDgwpC75yC0+nI7L05GpOkK6Jqi6gbomVCvgWsM+Ae9598z7m9bgaflwb7r7JMJKkkR0kSYkrpx/C/C2B43Bj6fuxNA1ZQRNG4AyeN3BPl4RcAXAH5XAt0NJAwAA";

    @Test
    public void parsesCorrectly() {

        assertDoesNotThrow(() -> {

            JsonNode data = SimpleNBTParser.readBase64(b64data3);

            String stringified = Json.stringify(data, true);

            System.out.println(stringified);
        });



    }

    @Test
    public void printThing() throws IOException {

        String thing = "H4sIAAAAAAAAAONiYOBkYMzkYmBg0GUgCQAA4gDYkzoAAAA=";

        byte[] bytes = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(invalid))).readAllBytes();

        bytes = Arrays.copyOfRange(bytes, 370, bytes.length);

        System.out.println(Arrays.toString(bytes));

        System.out.println(Json.stringify(SimpleNBTParser.readBase64(invalid), true));

    }

}
