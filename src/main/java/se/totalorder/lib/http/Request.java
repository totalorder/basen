package se.totalorder.lib.http;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public class Request {
  @Delegate
  private final okhttp3.Request okRequest;

  public okhttp3.Request unwrap() {
    return okRequest;
  }
}
