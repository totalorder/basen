package se.totalorder.basen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Tx {
  private final Connection connection;
  public final boolean readOnly;

  public Tx(final Connection connection, final boolean readOnly) {
    this.readOnly = readOnly;
    try {
      connection.setAutoCommit(readOnly);
      connection.setReadOnly(readOnly);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    this.connection = connection;
  }

  public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
    try {
      final Statement statement = connection.createStatement();
      final ResultSet resultSet = statement.executeQuery(sql);
      final List<T> results = new ArrayList<>();
      while (resultSet.next()) {
        results.add(rowMapper.mapRow(resultSet));
      }
      return results;
    } catch (SQLException e) {
      try {
        rollbackIfNeeded();
      } catch (SQLException e1) {
        throw new RuntimeException(e1);
      }
      throw new RuntimeException(e);
    }
  }

  public int update(final String sql) {
    try {
      final Statement statement = connection.createStatement();
      return statement.executeUpdate(sql);
    } catch (SQLException e) {
      try {
        rollbackIfNeeded();
      } catch (SQLException e1) {
        throw new RuntimeException(e1);
      }
      throw new RuntimeException(e);
    }
  }

  public void commit() {
    try {
      if (!readOnly) {
        connection.commit();
      }
    } catch (SQLException e) {
      try {
        rollbackIfNeeded();
      } catch (SQLException e1) {
        throw new RuntimeException(e1);
      }
      throw new RuntimeException(e);
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void rollbackIfNeeded() throws SQLException {
    if (!readOnly) {
      connection.rollback();
    }

    connection.close();
  }

  public void rollback() {
    try {
      rollbackIfNeeded();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
