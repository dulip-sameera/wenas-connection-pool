package lk.ijse.dep13.wenasconnectionpool.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        try {
            initializer();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializer() throws SQLException, ClassNotFoundException {
        String database = PROPERTIES.getProperty("app.db.database");
        String host = PROPERTIES.getProperty("app.db.host");
        String port = PROPERTIES.getProperty("app.db.port");
        String user = PROPERTIES.getProperty("app.db.user");
        String password = PROPERTIES.getProperty("app.db.password");

        Class.forName("com.mysql.cj.jdbc.Driver");

        for (int i = 0; i < this.poolSize; i++) {
            Connection connection = DriverManager.getConnection("jdbc:mysql://%s:%s/$s".formatted(host, port, database), user, password);
            MAIN_POOL.put(UUID.randomUUID(), connection);
        }
    }

    public record ConnectionWrapper(UUID id, Connection connection) {
    }

    public synchronized ConnectionWrapper getConnection() {
        while (MAIN_POOL.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        UUID key = MAIN_POOL.keySet().stream().findFirst().get();
        Connection connection = MAIN_POOL.get(key);
        MAIN_POOL.remove(key);
        CONSUMER_POOL.put(key, connection);
        return new ConnectionWrapper(key, connection);
    }

    public synchronized void releaseConnection(UUID id) {
        if (!CONSUMER_POOL.containsKey(id)) throw new RuntimeException("Invalid Connection ID");
        Connection connection = CONSUMER_POOL.get(id);
        CONSUMER_POOL.remove(id);
        MAIN_POOL.put(id, connection);
        notify();
    }

    public synchronized void releaseAllConnections() {
        CONSUMER_POOL.forEach(MAIN_POOL::put);
        CONSUMER_POOL.clear();
        notifyAll();
    }

}
