package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.io.IOException;

@AllArgsConstructor
public class Call {
  @Delegate(excludes = DelegateExcludes.class)
  private final okhttp3.Call okCall;
  private final ObjectMapper objectMapper;

  public Response execute() {
    try {
      return new Response(okCall.execute(), objectMapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Call clone() {
    return new Call(okCall.clone(), objectMapper);
  }

  interface DelegateExcludes {
    okhttp3.Response execute() throws IOException;
    okhttp3.Call clone();
  }
}
