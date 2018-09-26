package se.totalorder.basen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;
import javax.sql.DataSource;

public class TransactionManager {
  private final DataSource dataSource;

  public TransactionManager(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private Tx begin(final boolean readOnly) {
    try {
      final Connection connection = dataSource.getConnection();
      return new Tx(connection, readOnly);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Tx begin() {
    return begin(false);
  }

  public Tx beginReadonly() {
    return begin(true);
  }

  private <T> T begin(final Function<Tx, T> callback, final boolean readOnly) {
    final Tx tx = begin(readOnly);
    try {
      final T result = callback.apply(tx);
      tx.commit();
      return result;
    } catch (final Exception e) {
      if (!tx.readOnly) {
        tx.rollback();
      }
      throw new RuntimeException(e);
    }
  }

  public <T> T begin(final Function<Tx, T> callback) {
    return begin(callback, false);
  }

  public <T> T beginReadonly(final Function<Tx, T> callback) {
    return begin(callback, true);
  }
}
