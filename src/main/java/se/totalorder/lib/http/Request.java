package se.totalorder.lib.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
  public final String url;
  public final String mimeType;
  public final String body;

  public static class RequestBuilder {
    public String url;
    public String mimeType = "text/plain";
    public String body;
  }
}