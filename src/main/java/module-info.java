module se.totalorder.basen {
  requires java.sql;
  requires slf4j.api;
  requires javalin;
  requires jetty.http;
  uses org.eclipse.jetty.http.HttpFieldPreEncoder;
  uses org.eclipse.jetty.http.Http1FieldPreEncoder;

  exports se.totalorder.basen;
}