package cosmic.eidex.Service;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Unused
 * Klasse um Chat zeitstempel korrekt zu verarbeiten
 */
public class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Formatiert Java OffsetTime in OffsetTime fuer die Datenbank
     * @param src Gegebener OffsetTimestring von Java
     * @param typeOfSrc Type des Eingabewertes Src
     * @param context Kontext
     * @return Json OffsetTimestring
     */
    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(FORMATTER.format(src));
    }

    /**
     * Formatiert OffsetTime fuer die Datenbank in Java OffsetTime
     * @param json Json TimeString
     * @param typeOfT Type des Eingabewertes json
     * @param context Kontext
     * @return Java OffsetTimestring
     * @throws JsonParseException falls json kein richtiges Format hat
     */
    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return OffsetDateTime.parse(json.getAsString(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new JsonParseException("Invalid date format: " + json.getAsString(), e);
        }
    }
}