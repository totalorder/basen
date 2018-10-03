package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Data;

@Data
public class Response {
  private final okhttp3.Response response;
  private final ObjectMapper objectMapper;

  public <T> T json(final Class<T> clazz) {
    try {
      final String body = response.body().string();
      if (body.isEmpty()) {
        return null;
      }

      return objectMapper.readValue(body, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
