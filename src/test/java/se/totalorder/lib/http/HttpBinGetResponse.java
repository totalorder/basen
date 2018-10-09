package se.totalorder.lib.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class HttpBinGetResponse {
  final Map<String, String> args;
}
