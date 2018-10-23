package se.totalorder.basen.api;

import java.util.List;
import se.deadlock.okok.Client;
import se.deadlock.txman.TxMan;
import se.totalorder.basen.model.User;

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
        tx.executeOne("SELECT * FROM usr WHERE id = :id", tx.params()
            .put("id", userId)
            .build(),
            User.mapper))
        .orElse(null);
  }

  public List<User> get() {
    return transactionManager.beginReadonly(tx ->
        tx.execute("SELECT * FROM usr", User.mapper));
  }

  public User create(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("INSERT INTO usr (id, username) VALUES (:id, :username) RETURNING *;", tx.params()
            .put("id", userId)
            .put("username", username)
            .build(),
            User.mapper))
        .orElse(null);
  }

  public User put(final String userIdString, final String username) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("UPDATE usr SET username = :username WHERE id = :id RETURNING *;", tx.params()
            .put("username", username)
            .put("id", userId)
            .build(),
            User.mapper))
        .orElse(null);
  }

  public User delete(final String userIdString) {
    final int userId = Integer.parseInt(userIdString);
    return transactionManager.begin(tx ->
        tx.executeOne("DELETE FROM usr WHERE id = :id RETURNING *;", tx.params()
            .put("id", userId)
            .build(),
            User.mapper))
        .orElse(null);
  }

  public User proxyGet(final String userIdString) {
    return client.get("/user/" + userIdString).json(User.class);
  }
}
