package se.totalorder.basen.confer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.Builder;
import lombok.Singular;

public class Confer {
  @Builder
  static Config typesafe(String directory,
                         String name,
                         String extension,
                         @Singular final ImmutableList<String> envs,
                         ObjectMapper objectMapper) {
    directory = directory != null ? directory : "";
    if (!directory.isEmpty() && !directory.endsWith("/")) {
      directory = directory + "/";
    }
    name = name != null ? name : "app";
    extension = extension != null ? extension : ".yml";
    objectMapper = objectMapper != null ? objectMapper : new ObjectMapper(new YAMLFactory());

    final URL baseFile = Resources.getResource(directory + name + extension);
    try {
      JsonNode jsonNode = objectMapper.readTree(baseFile);
      for (final String env : envs) {
        final URL envFile = Resources.getResource(directory + name + "-" + env + extension);
        final String envFileString = Resources.toString(envFile, StandardCharsets.UTF_8);
        if (envFileString.trim().isEmpty()) {
          continue;
        }
        jsonNode = objectMapper.readerForUpdating(jsonNode).readValue(envFileString);
      }
      final String jsonString = new ObjectMapper().writeValueAsString(jsonNode);
      return ConfigFactory.parseString(jsonString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
