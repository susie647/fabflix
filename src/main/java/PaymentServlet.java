package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalTime;
import java.sql.PreparedStatement;


@WebServlet(name = "PaymentServelet", urlPatterns = "/cs122b/payment")
public class PaymentServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
        int total = 0;
        for (int i = 0; i < items.size(); i++){
            total+=items.get(i).getPrice();
        }

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("total_price", total);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String creditCardNum = request.getParameter("creditCardNum");    //need to check for space
            String year = request.getParameter("year");
            String month = request.getParameter("month");
            String day = request.getParameter("day");
            String expiration = year + "-" + month + "-" + day;

            // Generate a SQL query
            String query = "Select * from creditcards where creditcards.id = ?";

            PreparedStatement statement = dbCon.prepareStatement(query);

            statement.setString(1, creditCardNum);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs and create a table row <tr>
            if (rs.next()) {
                if(firstName.equals(rs.getString("firstName")) && lastName.equals(rs.getString("lastName"))&&
                        expiration.equals(rs.getString("expiration"))){

                    HttpSession session = request.getSession(true);
                    //session.setAttribute("user", new User(email));
                    session.setAttribute("placeOrder", true);// used to check login status

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "invalid information");
                }
            }
            else{ // resultSet is empty
                responseJsonObject.addProperty("message", "card is invalid");
            }

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
            responseJsonObject.addProperty("message", "Sql error");
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
