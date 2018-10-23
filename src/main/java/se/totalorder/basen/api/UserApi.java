package se.totalorder.basen.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import se.deadlock.okok.Client;
import se.totalorder.basen.model.User;
import se.deadlock.txman.TxMan;

public class UserApi {

  private final Client client;
  private final TxMan transactionManager;

  public UserApi(final Client client, final TxMan transactionManager) {
    this.client = client;
    this.transactionManager = transactionManager;
  }

  public User get(final String userIdString) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.beginReadonly(tx ->
        tx.executeOne("SELECT * FROM usr WHERE id = ?", User.mapper, listOf(userId)))
        .orElse(null);
  }

  public List<User> get() {
    return transactionManager.beginReadonly(tx ->
        tx.execute("SELECT * FROM usr", User.mapper));
  }

  public User create(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("INSERT INTO usr (id, username) VALUES (?, ?) RETURNING *;", User.mapper, listOf(userId, username)))
        .orElse(null);
  }

  public User put(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("UPDATE usr SET username = ? WHERE id = ? RETURNING *;", User.mapper, listOf(username, userId)))
        .orElse(null);
  }

  public User delete(final String userIdString) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("DELETE FROM usr WHERE id = ? RETURNING *;", User.mapper, listOf(userId)))
        .orElse(null);
  }

  public User proxyGet(final String userIdString) {
    return client.get("/user/" + userIdString).json(User.class);
  }

  private List<Object> listOf(Object... args) {
    return new ArrayList<>(Arrays.asList(args));
  }
}
