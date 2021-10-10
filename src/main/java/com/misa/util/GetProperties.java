package com.misa.util;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j
@NoArgsConstructor
public class GetProperties {
    private static Properties properties;

    public static void loadFile() throws IOException {
        try (InputStream input = GetProperties.class
                .getClassLoader().getResourceAsStream("properties/app.properties")) {

            getProperties(input);
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }
    }

    private static void getProperties(InputStream in) throws IOException {
        properties = new Properties();
        properties.load(in);
    }

    public static String get(String name) {
        if(properties == null) {
            try {
                loadFile();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return properties.getProperty(name);
    }
}
