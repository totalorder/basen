package se.totalorder.lib.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;

public class ClientTest {

  Client client = new Client();

  @Test
  void testPlainOkHttp() {
    okhttp3.Request okRequest = Request.builder()
        .url("http://httpbin.org/get")
        .build()
        .getOkBuilder()
        .build();

    Response response = client.execute(okRequest);
    assertThat(response.okResponse().code(), is(200));
  }
}
