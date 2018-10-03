package se.totalorder.lib.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Client {
  private final OkHttpClient client;
  private final String baseUrl;
  private final ObjectMapper objectMapper;

  public Client(final String baseUrl) {
    this(new OkHttpClient(), baseUrl, new ObjectMapper());
  }

  public Client(final OkHttpClient client, final String baseUrl, final ObjectMapper objectMapper) {
    this.client = client;
    this.baseUrl = baseUrl;
    this.objectMapper = objectMapper;
  }

  public Response post(final Request request) {
    return execute(build(request).post(RequestBody.create(MediaType.parse(request.mimeType), request.body)).build());
  }

  public Response post(final String url, final String body) {
    return post(Request.builder().url(url).body(body).build());
  }

  public Response put(final Request request) {
    return execute(build(request).put(RequestBody.create(MediaType.parse(request.mimeType), request.body)).build());
  }

  public Response put(final String url, final String body) {
    return put(Request.builder().url(url).body(body).build());
  }

  public Response get(final Request request) {
    return execute(build(request).get().build());
  }

  public Response get(final String url) {
    return get(Request.builder().url(url).build());
  }

  public Response delete(final Request request) {
    return execute(build(request).delete().build());
  }

  public Response delete(final String url) {
    return delete(Request.builder().url(url).build());
  }

  okhttp3.Request.Builder build(final Request request) {
    return new okhttp3.Request.Builder().url(baseUrl + request.url);
  }

  private Response execute(final okhttp3.Request request) {
    try {
      return new Response(client.newCall(request).execute(), objectMapper);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
