package lk.ijse.dep13.wenasconnectionpool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep13.wenasconnectionpool.db.ConnectionPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "release-connection-servlet", urlPatterns = "/connections/*")
public class ReleaseConnectionServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            releaseAllConnections(resp);
            return;
        }

        Matcher matcher = Pattern.compile("^/(?<id>[^/]+)/?(?<rest>.*)$").matcher(req.getPathInfo());
        if (!matcher.find()) {
            System.out.println("find");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String rest = matcher.group("rest");
        if (!rest.isBlank()) {
            System.out.println("rest");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String id = matcher.group("id");
        releaseConnection(id.trim(), resp);
    }

    private void releaseConnection(String id, HttpServletResponse resp) throws IOException {
        ConnectionPool conPool = (ConnectionPool) getServletContext().getAttribute("datasource");
        conPool.releaseConnection(UUID.fromString(id));

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Connection ID: " + id + " released</h1>");
    }

    private void releaseAllConnections(HttpServletResponse resp) throws IOException {
        ConnectionPool conPool = (ConnectionPool) getServletContext().getAttribute("datasource");
        conPool.releaseAllConnections();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>All Connections released</h1>");
    }
}
