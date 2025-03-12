package lk.ijse.dep13.wenasconnectionpool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep13.wenasconnectionpool.db.ConnectionPool;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "obtain-connection-servlet", urlPatterns = "/connections/random")
public class ObtainConnectionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ConnectionPool conPool = (ConnectionPool) getServletContext().getAttribute("datasource");
        ConnectionPool.ConnectionWrapper connectionWrapper = conPool.getConnection();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.printf("<h1>Connection ID: %s</h1>", connectionWrapper.id());
        out.printf("<h1>Connection REF: %s</h1>", connectionWrapper.connection());
    }
}
