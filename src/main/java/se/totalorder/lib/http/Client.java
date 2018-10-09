package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.Delegate;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class Client {
  @Delegate(excludes = DelegateExcludes.class)
  private final OkHttpClient okClient;

  @Nullable
  private final String baseUrl;
  private final ObjectMapper objectMapper;

  public Client() {
    this(null);
  }

  public Client(final String baseUrl) {
    super();
    this.okClient = new OkHttpClient();
    this.baseUrl = baseUrl;
    this.objectMapper = new ObjectMapper();
  }

  public Call newCall(Request request) {
    return new Call(okClient.newCall(request.unwrap()), objectMapper);
  }

  public Response get(final String url) {
    return execute(request().url(url).get().build());
  }

  public Response post(final String url, final String body) {
    return execute(request().url(url).post(body).build());
  }

  public Response put(final String url, final String body) {
    return execute(request().url(url).put(body).build());
  }

  public Response delete(final String url) {
    return execute(request().url(url).delete().build());
  }

  public Response delete(final String url, final String body) {
    return execute(request().url(url).delete(body).build());
  }

  public Response execute(final Request request) {
    if (baseUrl != null && !request.url().toString().startsWith(baseUrl)) {
      throw new RuntimeException("Request url " + request.url().toString() + " does not match baseUrl "
          + baseUrl + ". Consider using a plain OkHttpClient if this is intended.");
    }
    return newCall(request).execute();
  }

  public RequestBuilder request(final String url) {
    return request().url(url);
  }

  public RequestBuilder request() {
    return new RequestBuilder(baseUrl);
  }

  interface DelegateExcludes {
    okhttp3.Call newCall(okhttp3.Request request);
  }
}
