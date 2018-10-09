package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import javax.annotation.Nullable;
import java.io.IOException;

@AllArgsConstructor
public class Response {
  @Delegate
  private final okhttp3.Response okResponse;

  private final ObjectMapper objectMapper;

  @Nullable
  public JsonNode json() {
    try {
      final String body = okResponse.body().string();
      if (body.isEmpty()) {
        return null;
      }

      return objectMapper.readTree(body);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  public <T> T json(final Class<T> clazz) {
    try {
      final String body = okResponse.body().string();
      if (body.isEmpty()) {
        return null;
      }

      return objectMapper.readValue(body, clazz);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String text() {
    try {
      return okResponse.body().string();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public okhttp3.Response okResponse() {
    return okResponse;
  }
}
