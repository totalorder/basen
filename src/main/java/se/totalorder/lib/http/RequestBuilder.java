package se.totalorder.lib.http;

import okhttp3.*;

import javax.annotation.Nullable;
import java.net.URL;

public class RequestBuilder {
  private okhttp3.Request.Builder okBuilder;

  @Nullable
  private final String baseUrl;

  public RequestBuilder(@Nullable final String baseUrl) {
    this.okBuilder = new okhttp3.Request.Builder();
    this.baseUrl = baseUrl;
  }

  public RequestBuilder url(String url) {
    if (baseUrl != null) {
      okBuilder = okBuilder.url(baseUrl + url);
    } else {
      okBuilder = okBuilder.url(url);
    }
    return this;
  }

  public RequestBuilder url(HttpUrl url) {
    okBuilder = okBuilder.url(url);
    return this;
  }

  public RequestBuilder url(URL url) {
    okBuilder = okBuilder.url(url);
    return this;
  }

  public RequestBuilder header(String name, String value) {
    okBuilder = okBuilder.header(name, value);
    return this;
  }

  public RequestBuilder addHeader(String name, String value) {
    okBuilder = okBuilder.addHeader(name, value);
    return this;
  }

  public RequestBuilder removeHeader(String name) {
    okBuilder = okBuilder.removeHeader(name);
    return this;
  }

  public RequestBuilder headers(Headers headers) {
    okBuilder = okBuilder.headers(headers);
    return this;
  }

  public RequestBuilder cacheControl(CacheControl cacheControl) {
    okBuilder = okBuilder.cacheControl(cacheControl);
    return this;
  }

  public RequestBuilder get() {
    okBuilder = okBuilder.get();
    return this;
  }

  public RequestBuilder head() {
    okBuilder = okBuilder.head();
    return this;
  }

  public RequestBuilder post(RequestBody body) {
    okBuilder = okBuilder.post(body);
    return this;
  }

  public RequestBuilder delete(@Nullable RequestBody body) {
    okBuilder = okBuilder.delete(body);
    return this;
  }

  public RequestBuilder delete() {
    okBuilder = okBuilder.delete();
    return this;
  }

  public RequestBuilder put(RequestBody body) {
    okBuilder = okBuilder.post(body);
    return this;
  }

  public RequestBuilder patch(RequestBody body) {
    okBuilder = okBuilder.patch(body);
    return this;
  }

  public RequestBuilder method(String method, @Nullable RequestBody body) {
    okBuilder = okBuilder.method(method, body);
    return this;
  }

  public RequestBuilder tag(@Nullable Object tag) {
    okBuilder = okBuilder.tag(tag);
    return this;
  }

  public <T> RequestBuilder tag(Class<? super T> type, @Nullable T tag) {
    okBuilder = okBuilder.tag(type, tag);
    return this;
  }

  public Request build() {
    return new Request(okBuilder.build());
  }

  // Add-ons
  public RequestBuilder post(String body) {
    return post(body, null);
  }

  public RequestBuilder delete(String body, @Nullable String contentType) {
    okBuilder = okBuilder.delete(RequestBody.create(contentType != null ? MediaType.parse(contentType) : null, body));
    return this;
  }

  public RequestBuilder delete(String body) {
    return delete(body, null);
  }


  public RequestBuilder post(String body, @Nullable String contentType) {
    okBuilder = okBuilder.post(RequestBody.create(contentType != null ? MediaType.parse(contentType) : null, body));
    return this;
  }

  public RequestBuilder put(String body) {
    return put(body, null);
  }

  public RequestBuilder put(String body, @Nullable String contentType) {
    okBuilder = okBuilder.put(RequestBody.create(contentType != null ? MediaType.parse(contentType) : null, body));
    return this;
  }

  public RequestBuilder patch(String body) {
    return patch(body, null);
  }

  public RequestBuilder patch(String body, @Nullable String contentType) {
    okBuilder = okBuilder.patch(RequestBody.create(contentType != null ? MediaType.parse(contentType) : null, body));
    return this;
  }

  public RequestBuilder method(String method, String body) {
    return method(method, body, null);
  }

  public RequestBuilder method(String method, String body, @Nullable String contentType) {
    okBuilder = okBuilder.put(RequestBody.create(contentType != null ? MediaType.parse(contentType) : null, body));
    return this;
  }

  public okhttp3.Request.Builder okBuilder() {
    return okBuilder;
  }
}
