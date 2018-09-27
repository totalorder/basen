package se.totalorder.basen.model;

import lombok.Data;
import se.totalorder.basen.tx.RowMapper;

@Data
public class User {
  public final int id;
  public final String username;

  public static RowMapper<User> mapper = (resultSet) -> new User(
      resultSet.getInt("id"),
      resultSet.getString("username")
  );
}
