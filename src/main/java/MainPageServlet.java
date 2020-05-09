package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MainPageServlet", urlPatterns = "/cs122b/main")
public class MainPageServlet extends HttpServlet{
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
//        String username = (String) session.getAttribute("user");

        String heading;
//        Integer newUser = (Integer) session.getAttribute("new_user");
        if (session.getAttribute("newUser") == null) {
            // Which means the user is never seen before
//            newUser = 1;
            heading = "Welcome to Fabflix!";
            session.setAttribute("newUser", false);
        } else {
            // Which means the user has requested before, thus user information can be found in the session
            heading = "Welcome back to Fabflix!";
        }

        String admin = "false";
        if(session.getAttribute("admin") != null && session.getAttribute("admin").equals(true)){
            admin = "true";
        }

        // Update the new accessCount to session, replacing the old value if existed
//        session.setAttribute("new_user", newUser);

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("welcomeInfo", heading);
        responseJsonObject.addProperty("admin", admin);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();
        try {
            HttpSession session = request.getSession(true);
            session.setAttribute("login", false);
            if(session.getAttribute("admin") != null){
                session.setAttribute("admin", false);
            }
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "logout successfully");
        } catch (Exception ex) {
            responseJsonObject.addProperty("message", "session error");
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
