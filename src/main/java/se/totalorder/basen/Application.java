package se.totalorder.basen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
  private final static Logger log = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    System.out.println("Hello!");
    log.info("Logback!");
    final JavalinApp javalinApp = new JavalinApp();
    javalinApp.start();
  }
}
