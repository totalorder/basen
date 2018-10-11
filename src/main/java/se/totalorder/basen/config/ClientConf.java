package se.totalorder.basen.config;

import lombok.extern.slf4j.Slf4j;
import se.deadlock.okok.Client;
import se.totalorder.basen.util.OutgoingHttpLogger;

@Slf4j
public class ClientConf {

  private static OutgoingHttpLogger loggingInterceptor = new OutgoingHttpLogger(
      log::info, OutgoingHttpLogger.Level.BODY);

  public static Client.Builder createClient(final String env) {
    return new Client.Builder().addInterceptor(loggingInterceptor);
  }
}
