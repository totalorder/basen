package se.totalorder.lib.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

public class ClientTest {

  Client client = new Client("http://httpbin.org");

  @Test
  void testPlainOkHttp() {
    okhttp3.Request okRequest = Request.builder()
        .url("/get")
        .build()
        .getOkBuilder()
        .build();

    Response response = client.execute(okRequest);
    assertThat(response.okResponse().code(), is(200));
  }

  @Test
  void testGet() {
    Response response = client.get("/get");
    assertThat(response.statusCode(), is(200));
  }

  @Test
  void testPost() {
    Response response = client.post("/post", "post-body");
    assertThat(response.statusCode(), is(200));
    assertThat(response.json().get("data").textValue(), is("post-body"));
  }

  @Test
  void testPut() {
    Response response = client.put("/put", "put-body");
    assertThat(response.statusCode(), is(200));
    assertThat(response.json().get("data").textValue(), is("put-body"));
  }

  @Test
  void testDelete() {
    Response response = client.delete("/delete");
    assertThat(response.statusCode(), is(200));
  }

  @Test
  void testGetJson() {
    Response response = client.get("/get?q=value");
    assertThat(response.statusCode(), is(200));
    assertThat(
        response.json(HttpBinGetResponse.class),
        is(new HttpBinGetResponse(ImmutableMap.<String, String>builder().put("q", "value").build())));
  }

  @Test
  void testPostContentType() {
    Response response = client.post(
        client.request("/post").mimeType("application/json").body("{}").build());
    assertThat(response.statusCode(), is(200));
    assertThat(response.json().get("headers").get("Content-Type").textValue(), is("application/json; charset=utf-8"));
  }
}
