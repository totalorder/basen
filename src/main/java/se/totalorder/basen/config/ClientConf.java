package se.totalorder.basen.config;

import se.deadlock.okok.Client;

public class ClientConf {
  static Client createClient(final String env, final String baseUrl) {
    return new Client(baseUrl)
  }
}
