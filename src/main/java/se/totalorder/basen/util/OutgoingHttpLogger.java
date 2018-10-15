/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.totalorder.basen.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Stolen from: com.squareup.okhttp3:logging-interceptor:3.11.0 okhttp3.logging.HttpLoggingInterceptor
 */
public final class OutgoingHttpLogger implements Interceptor {
  private static final Charset UTF8 = Charset.forName("UTF-8");

  public enum Level {
    NONE,
    HEADERS,
    BODY,
    HEADERS_BODY
  }

  public interface Logger {
    void log(String message);
  }

  public OutgoingHttpLogger(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  private final Logger logger;

  private final Level level;

  public Level getLevel() {
    return level;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Level level = this.level;

    Request request = chain.request();
    if (level == Level.NONE) {
      return chain.proceed(request);
    }

    boolean logBody = level == Level.BODY;
    boolean logHeaders = level == Level.HEADERS_BODY || level == Level.HEADERS;

    RequestBody requestBody = request.body();
    boolean hasRequestBody = requestBody != null;

    Connection connection = chain.connection();
    String requestStartMessage = "HTTP >> "
        + request.method()
        + ' ' + request.url()
        + (connection != null ? " " + connection.protocol() : "");
    logger.log(requestStartMessage);

    if (hasRequestBody) {
      // Request body headers are only present when installed as a network interceptor. Force
      // them to be included (when available) so there values are known.
      if (requestBody.contentType() != null) {
        logger.log("Content-Type: " + requestBody.contentType());
      }
      if (requestBody.contentLength() != -1) {
        logger.log("Content-Length: " + requestBody.contentLength());
      }
    }

    if (logHeaders) {
      Headers headers = request.headers();
      for (int i = 0, count = headers.size(); i < count; i++) {
        String name = headers.name(i);
        // Skip headers from the request body as they are explicitly logged above.
        if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
          logger.log(name + ": " + headers.value(i));
        }
      }
    }

    if (logBody && hasRequestBody) {
      if (bodyHasUnknownEncoding(request.headers())) {
        logger.log("(encoded body omitted)");
      } else {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);

        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
          charset = contentType.charset(UTF8);
        }

        if (isPlaintext(buffer)) {
          logger.log(buffer.readString(charset));
        } else {
          logger.log(" (binary " + requestBody.contentLength() + "-byte body omitted)");
        }
      }
    }

    long startNs = System.nanoTime();
    Response response;
    try {
      response = chain.proceed(request);
    } catch (Exception e) {
      logger.log(requestStartMessage + " [FAILED]: " + e);
      throw e;
    }
    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

    ResponseBody responseBody = response.body();
    logger.log(requestStartMessage + " ["
        + response.code() + " " + HttpStatus.getMessage(response.code())
        + "] "
        + tookMs + "ms");

    Headers headers = response.headers();
    if (logHeaders) {
      for (int i = 0, count = headers.size(); i < count; i++) {
        logger.log(headers.name(i) + ": " + headers.value(i));
      }
    }

    if (logBody && HttpHeaders.hasBody(response)) {
      if (bodyHasUnknownEncoding(response.headers())) {
        logger.log("(encoded body omitted)");
      } else {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
          GzipSource gzippedResponseBody = null;
          try {
            gzippedResponseBody = new GzipSource(buffer.clone());
            buffer = new Buffer();
            buffer.writeAll(gzippedResponseBody);
          } finally {
            if (gzippedResponseBody != null) {
              gzippedResponseBody.close();
            }
          }
        }

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
          charset = contentType.charset(UTF8);
        }

        if (!isPlaintext(buffer)) {
          logger.log("(binary " + buffer.size() + "-byte body omitted)");
          return response;
        }

        if (responseBody.contentLength() != 0) {
          logger.log(buffer.clone().readString(charset));
        }
      }
    }

    return response;
  }

  /**
   * Returns true if the body in question probably contains human readable text. Uses a small sample
   * of code points to detect unicode control characters commonly used in binary file signatures.
   */
  static boolean isPlaintext(Buffer buffer) {
    try {
      Buffer prefix = new Buffer();
      long byteCount = buffer.size() < 64 ? buffer.size() : 64;
      buffer.copyTo(prefix, 0, byteCount);
      for (int i = 0; i < 16; i++) {
        if (prefix.exhausted()) {
          break;
        }
        int codePoint = prefix.readUtf8CodePoint();
        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
          return false;
        }
      }
      return true;
    } catch (EOFException e) {
      return false; // Truncated UTF-8 sequence.
    }
  }

  private boolean bodyHasUnknownEncoding(Headers headers) {
    String contentEncoding = headers.get("Content-Encoding");
    return contentEncoding != null
        && !contentEncoding.equalsIgnoreCase("identity")
        && !contentEncoding.equalsIgnoreCase("gzip");
  }
}
