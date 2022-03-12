package com.misa.util;

import com.misa.dao.exceptions.FailedToReadPropertiesException;
import com.misa.dao.exceptions.PropertyNotFoundByKeyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

/**
 * <p>These are main properties application can't function without.</p>
 */
@Log4j
@NoArgsConstructor
public class GetApplicationProperties {
    @Getter(AccessLevel.PRIVATE)
    private static final String PROPERTIES_LOCATION_STRING = "properties/app.properties";
    @Getter(AccessLevel.PRIVATE)
    private static Properties properties;

    static void loadFile() {
        try (InputStream input = GetApplicationProperties.class
                                         .getClassLoader()
                                         .getResourceAsStream(getPROPERTIES_LOCATION_STRING())) {

            fetchProperties(input);
        } catch (NullPointerException | IOException e) {
            throw new FailedToReadPropertiesException(
                    String.format("Failed to read the main application properties from file: %s .",
                            getPROPERTIES_LOCATION_STRING()), e);
        }
    }

    static void fetchProperties(InputStream in) throws IOException {
        properties = new Properties();
        properties.load(in);
    }

    public static String get(@NonNull String key) {
        if (getProperties() == null) {
            loadFile();
        }
        String fetchedProperty = getProperties().getProperty(key);
        return Optional.ofNullable(fetchedProperty)
                       .orElseThrow(() -> new PropertyNotFoundByKeyException(
                               String.format("Property not found by the key: %s .", key)));
    }
}
