package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Data;

@Data
public class Response {
  private final okhttp3.Response okResponse;
  private final ObjectMapper objectMapper;

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
