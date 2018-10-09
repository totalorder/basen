package se.totalorder.lib.http;

import lombok.Builder;
import lombok.Data;

@Data
public class Request {
  public final String url;
  public final String mimeType;
  public final String body;
  private final okhttp3.Request.Builder okBuilder;

  @Builder
  public Request(final String url, final String mimeType, final String body) {
    this.url = url;
    this.mimeType = mimeType != null ? mimeType : "text/plain";
    this.body = body;
    this.okBuilder = new okhttp3.Request.Builder().url(url);
  }

  public okhttp3.Request.Builder okBuilder() {
    return okBuilder;
  }
}