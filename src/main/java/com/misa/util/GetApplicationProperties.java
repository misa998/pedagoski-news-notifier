package com.misa.util;

import com.misa.dao.exceptions.FailedToReadPropertiesException;
import com.misa.dao.exceptions.PropertyNotFoundByKeyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

/**
 * <p>These are main properties application can't function without.</p>
 */
@Log4j
@NoArgsConstructor
public class GetProperties {
    public static final String PROPERTIES_LOCATION = "properties/app.properties";
    private static Properties properties;

    public static void loadFile() {
        try (InputStream input = GetProperties.class
                                         .getClassLoader()
                                         .getResourceAsStream(PROPERTIES_LOCATION)) {

            fetchProperties(input);
        } catch (NullPointerException | IOException e) {
            throw new FailedToReadPropertiesException(
                    String.format("Failed to read the main application properties from file: %s .",
                            PROPERTIES_LOCATION), e);
        }
    }

    private static void fetchProperties(InputStream in) throws IOException {
        properties = new Properties();
        properties.load(in);
    }

    public static String get(@NonNull String name) {
        if (properties == null) {
            loadFile();
        }
        String fetchedProperty = properties.getProperty(name);
        return Optional.ofNullable(fetchedProperty)
                       .orElseThrow(() -> new PropertyNotFoundByKeyException(
                               String.format("Property not found by the key: %s .", name)));
    }
}
