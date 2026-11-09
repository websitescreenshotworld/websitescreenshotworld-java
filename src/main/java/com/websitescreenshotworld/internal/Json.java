package com.websitescreenshotworld.internal;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.websitescreenshotworld.Token;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * The JSON mapping shared by the client and webhook parsing. Not part of the public API.
 *
 * <p>Models are mapped through their fields, named in snake_case, so the public classes carry no
 * mapping annotations. Unknown fields are ignored and unset fields are not written.
 */
public final class Json {
  private static final ObjectMapper MAPPER =
      JsonMapper.builder()
          .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
          .visibility(PropertyAccessor.ALL, Visibility.NONE)
          .visibility(PropertyAccessor.FIELD, Visibility.ANY)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .defaultPropertyInclusion(
              JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS))
          .addModule(new ApiModule())
          .build();

  private Json() {}

  /**
   * Reads a JSON document into {@code type}.
   *
   * @param json the document
   * @param type the class to read into
   * @param <T> the result type
   * @return the value
   * @throws IOException if the document is not valid JSON for {@code type}
   */
  public static <T> T read(byte[] json, Class<T> type) throws IOException {
    return MAPPER.readValue(json, type);
  }

  /**
   * Reads a JSON document as a tree.
   *
   * @param json the document
   * @return the root node
   * @throws IOException if the document is not valid JSON
   */
  public static JsonNode readTree(byte[] json) throws IOException {
    return MAPPER.readTree(json);
  }

  /**
   * Converts a tree into {@code type}.
   *
   * @param node the tree
   * @param type the class to convert into
   * @param <T> the result type
   * @return the value
   * @throws JsonProcessingException if the tree does not fit {@code type}
   */
  public static <T> T convert(JsonNode node, Class<T> type) throws JsonProcessingException {
    return MAPPER.treeToValue(node, type);
  }

  /**
   * Converts a value into a tree, using the API's field names.
   *
   * @param value the value
   * @return the tree
   */
  public static JsonNode toTree(Object value) {
    return MAPPER.valueToTree(value);
  }

  /**
   * Writes a value as UTF-8 JSON.
   *
   * @param value the value
   * @return the document
   */
  public static byte[] write(Object value) {
    try {
      return MAPPER.writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Could not write JSON", e);
    }
  }

  private static final class ApiModule extends Module {
    @Override
    public String getModuleName() {
      return "websitescreenshotworld";
    }

    @Override
    public Version version() {
      return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
      SimpleSerializers serializers = new SimpleSerializers();
      serializers.addSerializer(Token.class, ToStringSerializer.instance);
      serializers.addSerializer(Instant.class, ToStringSerializer.instance);
      context.addSerializers(serializers);
      context.addDeserializers(new ApiDeserializers());
    }
  }

  private static final class ApiDeserializers extends Deserializers.Base {
    @Override
    public JsonDeserializer<?> findBeanDeserializer(
        JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
      Class<?> raw = type.getRawClass();
      if (raw == Instant.class) {
        return new InstantDeserializer();
      }
      if (Token.class.isAssignableFrom(raw)) {
        return new TokenDeserializer(raw);
      }
      return null;
    }
  }

  private static final class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
      String text = parser.getValueAsString();
      if (text == null) {
        return (Instant) context.handleUnexpectedToken(Instant.class, parser);
      }
      try {
        return Timestamps.parse(text);
      } catch (DateTimeParseException e) {
        return (Instant) context.handleWeirdStringValue(Instant.class, text, e.getMessage());
      }
    }
  }

  /** Builds tokens through the subclass's public static {@code of(String)} factory. */
  private static final class TokenDeserializer extends JsonDeserializer<Token> {
    private final Class<?> type;
    private final Method factory;

    TokenDeserializer(Class<?> type) {
      this.type = type;
      try {
        this.factory = type.getMethod("of", String.class);
      } catch (NoSuchMethodException e) {
        throw new IllegalStateException(type.getName() + " has no of(String) factory", e);
      }
    }

    @Override
    public Token deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      String text = parser.getValueAsString();
      if (text == null) {
        return (Token) context.handleUnexpectedToken(type, parser);
      }
      try {
        return (Token) factory.invoke(null, text);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException("Could not create " + type.getName(), e);
      }
    }
  }
}
