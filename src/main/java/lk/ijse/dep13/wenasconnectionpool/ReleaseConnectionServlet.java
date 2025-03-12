package lk.ijse.dep13.wenasconnectionpool;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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

        Matcher matcher = Pattern.compile("^/(?<id>[^/])/?(?<rest>.*)$").matcher(req.getPathInfo());
        if (matcher.find()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String rest = matcher.group("rest");
        if (!rest.isBlank()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String id = matcher.group("id");
        releaseConnection(id.trim(), resp);
    }

    private void releaseConnection(String id, HttpServletResponse res) {
        System.out.println("ReleaseConnectionServlet");
    }

    private void releaseAllConnections(HttpServletResponse resp) {
        System.out.println("ReleaseAllConnectionServlet");
    }
}
