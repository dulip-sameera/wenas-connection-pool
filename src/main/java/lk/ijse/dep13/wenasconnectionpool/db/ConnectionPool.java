package lk.ijse.dep13.wenasconnectionpool.db;

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPool {
    private static final int DEFAULT_POOL_SIZE = 5;
    private final ConcurrentHashMap<UUID, Connection> MAIN_POOL = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Connection> CONSUMER_POOL = new ConcurrentHashMap<>();
    private int poolSize;
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(ConnectionPool.class.getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionPool() {
        this(PROPERTIES.getProperty("app.pool.size") == null
                ? DEFAULT_POOL_SIZE
                : Integer.valueOf(PROPERTIES.getProperty("app.pool.size").trim()));
    }

    public ConnectionPool(int poolSize) {
        this.poolSize = poolSize;
    }

}
