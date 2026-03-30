package com.omnitest.platform.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** JDBC query helpers with resource management. */
public class QueryExecutor {

    private static final Logger LOG = LogManager.getLogger(QueryExecutor.class);

    public Map<String, String> queryForMap(String sql, Object... params) throws SQLException {
        long start = System.nanoTime();
        try (Connection conn = DBManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Map.of();
                }
                ResultSetMetaData md = rs.getMetaData();
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    row.put(md.getColumnLabel(i), toString(rs.getObject(i)));
                }
                logDuration(start, sql);
                return row;
            }
        }
    }

    public String queryForString(String sql, Object... params) throws SQLException {
        Map<String, String> row = queryForMap(sql, params);
        if (row.isEmpty()) {
            return null;
        }
        return row.values().iterator().next();
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        long start = System.nanoTime();
        try (Connection conn = DBManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            int n = ps.executeUpdate();
            logDuration(start, sql);
            return n;
        }
    }

    private static void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private static String toString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private void logDuration(long startNanos, String sql) {
        long ms = (System.nanoTime() - startNanos) / 1_000_000;
        if (ms > 500) {
            LOG.warn("Slow query ({} ms): {}", ms, sql);
        } else {
            LOG.debug("Query took {} ms: {}", ms, sql);
        }
    }
}
