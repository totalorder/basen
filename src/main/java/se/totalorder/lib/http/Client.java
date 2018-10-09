package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Client {
  private final OkHttpClient okClient;
  private final String baseUrl;
  private final ObjectMapper objectMapper;

  public Client() {
    this(null);
  }

  public Client(final String baseUrl) {
    this(new OkHttpClient(), baseUrl, new ObjectMapper());
  }

  public Client(final OkHttpClient okClient, final String baseUrl, final ObjectMapper objectMapper) {
    this.okClient = okClient;
    this.baseUrl = baseUrl;
    this.objectMapper = objectMapper;
  }

  public Response post(final Request request) {
    return execute(request.okBuilder().post(RequestBody.create(MediaType.parse(request.mimeType), request.body)).build());
  }

  public Response post(final String url, final String body) {
    return post(request(url).body(body).build());
  }

  public Response put(final Request request) {
    return execute(request.okBuilder().put(RequestBody.create(MediaType.parse(request.mimeType), request.body)).build());
  }

  public Response put(final String url, final String body) {
    return put(request(url).body(body).build());
  }

  public Response get(final Request request) {
    return execute(request.okBuilder().get().build());
  }

  public Response get(final String url) {
    return get(request(url).build());
  }

  public Response delete(final Request request) {
    return execute(request.okBuilder().delete().build());
  }

  public Response delete(final String url) {
    return delete(request(url).build());
  }

  public Request.RequestBuilder request(final String url) {
    if (baseUrl != null) {
      return Request.builder().url(baseUrl + url);
    }

    return Request.builder().url(url);
  }

  public Response execute(final okhttp3.Request request) {
    try {
      if (baseUrl != null && !request.url().toString().startsWith(baseUrl)) {
        throw new RuntimeException("Request url " + request.url().toString() + " does not match baseUrl "
            + baseUrl + ". Consider using a plain OkHttpClient if this is intended.");
      }
      return new Response(okClient.newCall(request).execute(), objectMapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
