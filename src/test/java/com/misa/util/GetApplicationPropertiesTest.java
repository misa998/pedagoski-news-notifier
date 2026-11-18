package com.misa.util;

import com.misa.dao.exceptions.PropertyNotFoundByKeyException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetApplicationPropertiesTest {

    @Test
    void whenExceptionThrown_thenAssertionSucceeds() {
        String key = "123";
        Exception exception = assertThrows(PropertyNotFoundByKeyException.class, () -> {
            GetApplicationProperties.get(key);
        });

        String expectedMsg = "Property not found";
        String actualMsg = exception.getMessage();
        assertTrue(actualMsg.contains(expectedMsg));
    }
}