package org.akuebler.rhino.sql;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.ScriptableObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RhinoSql {
    public interface RowProcessor {
        public void process(Object... arguments);
    }

    private interface LoopCondition {
        public boolean eval(ResultSet resultSet) throws SQLException;
    }

    Connection connection;
    String query;

    public RhinoSql(Connection connection) {
        this.connection = connection;
    }

    public RhinoSql q(String query) {
        this.query = query;
        return this;
    }

    public void eachRow(final Function function) {
        processResults(rowProcessor(function), new LoopCondition() {
            @Override
            public boolean eval(ResultSet resultSet) throws SQLException {
                return resultSet.next();
            }
        });
    }

    private RowProcessor rowProcessor(final Function function) {
        return new RowProcessor() {
            @Override
            public void process(Object... arguments) {
                Context context = Context.enter();
                ScriptableObject scope = context.initStandardObjects();

                function.call(context, scope, context.newObject(scope), arguments);
            }
        };
    }

    private void processResults(RowProcessor processor, LoopCondition condition) {
        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet results = statement.executeQuery()
        ) {
            while (condition.eval(results)) {
                processor.process(getRow(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void firstRow(final Function function) {
        processResults(rowProcessor(function), new LoopCondition() {
            private boolean first = true;

            @Override
            public boolean eval(ResultSet resultSet) throws SQLException {
                if (first && resultSet.next()) {
                    first = false;
                    return true;
                }
                return false;
            }
        });
    }

    private Object[] getRow(ResultSet resultSet) throws SQLException {
        int numColumns = resultSet.getMetaData().getColumnCount();
        Object[] result = new Object[numColumns];

        while (numColumns-- > 0) {
            result[numColumns] = resultSet.getObject(numColumns+1);
        }
        return result;
    }
}
