package uk.gov.dwp.jsa.citizen_ui.gateway;

public class BaseRequestJSONBuilder {

    public static final String JSON_RESPONSE = "{\n" +
            "  \"error\": {\n" +
            "    \"code\": \"\",\n" +
            "    \"message\": \"\"\n" +
            "  },\n" +
            "  \"success\": [\n" +
            "    {\n" +
            "      \"path\": \"/path/to/resource\",\n" +
            "      \"data\": \"d41948d0-02ec-4c25-98d5-ffa6dace1db4\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
