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
    return post(Request.builder().url(url).body(body).build());
  }

  public Response put(final Request request) {
    return execute(request.okBuilder().put(RequestBody.create(MediaType.parse(request.mimeType), request.body)).build());
  }

  public Response put(final String url, final String body) {
    return put(Request.builder().url(url).body(body).build());
  }

  public Response get(final Request request) {
    return execute(request.okBuilder().get().build());
  }

  public Response get(final String url) {
    return get(Request.builder().url(url).build());
  }

  public Response delete(final Request request) {
    return execute(request.okBuilder().delete().build());
  }

  public Response delete(final String url) {
    return delete(Request.builder().url(url).build());
  }

  public Response execute(final okhttp3.Request request) {
    try {
      final okhttp3.Request requestWithBaseUrl = baseUrl != null ?
          request.newBuilder().url(baseUrl + request.url().toString()).build() : request;
      return new Response(okClient.newCall(requestWithBaseUrl).execute(), objectMapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
