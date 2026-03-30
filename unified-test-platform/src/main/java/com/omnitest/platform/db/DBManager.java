package com.omnitest.platform.db;

import com.omnitest.platform.utils.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;

/**
 * JDBC connection pool (HikariCP) per JVM.
 */
public final class DBManager {

    private static final Logger LOG = LogManager.getLogger(DBManager.class);
    private static volatile HikariDataSource dataSource;

    private DBManager() {
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DBManager.class) {
                if (dataSource == null) {
                    ConfigManager cfg = ConfigManager.getInstance();
                    HikariConfig hc = new HikariConfig();
                    hc.setJdbcUrl(cfg.getJdbcUrl());
                    hc.setUsername(cfg.getDbUsername());
                    hc.setPassword(cfg.getDbPassword());
                    hc.setMaximumPoolSize(cfg.getDbPoolSize());
                    hc.setDriverClassName(cfg.getDbDriverClassName());
                    hc.setPoolName("omnitest-pool");
                    dataSource = new HikariDataSource(hc);
                    LOG.info("HikariCP pool started for {}", cfg.getJdbcUrl());
                }
            }
        }
        return dataSource;
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            LOG.info("HikariCP pool closed");
        }
    }
}
