package lk.ijse.dep13.wenasconnectionpool.listener;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lk.ijse.dep13.wenasconnectionpool.db.ConnectionPool;

import java.util.Set;

public class AppListener implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        ConnectionPool connectionPool = new ConnectionPool();
        servletContext.setAttribute("datasource", connectionPool);

    }
}
