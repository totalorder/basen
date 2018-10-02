package se.totalorder.basen.api;

import java.util.List;
import se.totalorder.basen.model.User;
import se.totalorder.basen.tx.TxMan;

public class UserApi {

  private final TxMan transactionManager;

  public UserApi(final TxMan transactionManager) {
    this.transactionManager = transactionManager;
  }

  public User get(final String userIdString) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.beginReadonly(tx ->
        tx.executeOne("SELECT * FROM usr WHERE id = ?", User.mapper, userId))
        .orElse(null);
  }

  public List<User> get() {
    return transactionManager.beginReadonly(tx ->
        tx.execute("SELECT * FROM usr", User.mapper));
  }

  public User create(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("INSERT INTO usr (id, username) VALUES (?, ?) RETURNING *;", User.mapper, userId, username))
        .orElse(null);
  }

  public User put(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("UPDATE usr SET username = ? WHERE id = ? RETURNING *;", User.mapper, username, userId))
        .orElse(null);
  }

  public User delete(final String userIdString) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("DELETE FROM usr WHERE id = ? RETURNING *;", User.mapper, userId))
        .orElse(null);
  }
}
