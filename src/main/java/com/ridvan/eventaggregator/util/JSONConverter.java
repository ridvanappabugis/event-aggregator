package com.ridvan.eventaggregator.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.Callable;

/**
 * JSON Converter util for converting to and from json.
 */
public class JSONConverter {
    private static final ObjectMapper MAPPER;

    static {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        MAPPER = mapper;
    }

    private JSONConverter() {
        // Hide default CTOR
    }


    public static <T> T fromJSON(final String source, final Class<T> clazz) {
        return wrap(() -> MAPPER.readValue(source, clazz), source, clazz);
    }

    public static String toJSON(final Object source) {
        return wrap(() -> MAPPER.writeValueAsString(source), source);
    }

    /**
     * Wraps {@link Callable} callable with parameter check and exception handling.
     * <p>
     * The {@link Callable} will only be executed if all {@code nonNullParams} are indeed not {@code null}.
     */
    private static <T> T wrap(final Callable<T> callable, final Object... nonNullParams) {
        try {
            for (final Object param : nonNullParams) {
                if (param == null) {
                    return null;
                }
            }

            return callable.call();
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
