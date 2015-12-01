package no.nb.microservices.catalogsearchindex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nb.microservices.catalogsearchindex.exception.TestDataException;
import no.nb.microservices.catalogsearchindex.rest.controller.ItemResourceBuilder;

public class TestItemResource {
    
    public static ItemResourceBuilder aDefaultBook() {
        return new ItemResourceBuilder()
            .withMediaTypes("bøker")
            .withContentClasses("restricted", "public")
            .isDigital();
    }

    public static String aDefaultBookJson() {
        return objectToJson(aDefaultBook().build());
    }
    
    public static ItemResourceBuilder aDefaultRadio() {
        return new ItemResourceBuilder()
                .withMediaTypes("radio")
                .withContentClasses("restricted")
                .isDigital();
    }

    public static ItemResourceBuilder aDefaultMusic() {
        return new ItemResourceBuilder()
                .withMediaTypes("musikk")
                .withContentClasses("restricted")
                .isDigital();
    }

    public static String aDefaultMusicJson() {
        return objectToJson(aDefaultMusic().build());
    }

    private static String objectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new TestDataException(ex);
        }
    }
}
