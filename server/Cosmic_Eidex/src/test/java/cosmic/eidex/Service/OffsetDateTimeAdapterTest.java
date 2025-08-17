package cosmic.eidex.Service;

import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class OffsetDateTimeAdapterTest {

    private final OffsetDateTimeAdapter adapter = new OffsetDateTimeAdapter();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Test
    void testSerialize() {
        OffsetDateTime now = OffsetDateTime.now();
        JsonElement jsonElement = adapter.serialize(now, OffsetDateTime.class, null);

        assertTrue(jsonElement.isJsonPrimitive());
        assertEquals(formatter.format(now), jsonElement.getAsString());
    }

    @Test
    void testDeserialize() {
        String dateTimeString = "2025-06-24T15:30:00+02:00";
        JsonPrimitive jsonPrimitive = new JsonPrimitive(dateTimeString);

        OffsetDateTime result = adapter.deserialize(jsonPrimitive, OffsetDateTime.class, null);

        assertEquals(OffsetDateTime.parse(dateTimeString, formatter), result);
    }

    @Test
    void testDeserializeInvalidFormat() {
        JsonPrimitive invalidJson = new JsonPrimitive("invalid-date-time");

        assertThrows(JsonParseException.class, () -> {
            adapter.deserialize(invalidJson, OffsetDateTime.class, null);
        });
    }
}

