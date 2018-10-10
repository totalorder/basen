package se.totalorder.basen.model;

import lombok.Data;
import se.deadlock.txman.RowMapper;

@Data
public class User {
  public final int id;
  public final String username;

  public static RowMapper<User> mapper = (resultSet) -> new User(
      resultSet.getInt("id"),
      resultSet.getString("username")
  );
}
