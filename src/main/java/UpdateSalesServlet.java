package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalDate;


@WebServlet(name = "UpdateSalesServlet", urlPatterns = "/cs122b/update")
public class UpdateSalesServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        response.setContentType("application/json"); // Response mime type

        PrintWriter out = response.getWriter();

        try {
            Connection dbCon = dataSource.getConnection();
            Statement update = dbCon.createStatement();
            String cid = session.getAttribute("cid").toString();


            // Update Sales Table
            String values = "INSERT INTO sales (customerId, movieId, saleDate) VALUES";
            String saleDate = LocalDate.now().toString();
            ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
            for (int i = 0; i < items.size(); i++) {
                int quantity = items.get(i).getQuantity();
                String movieId = items.get(i).getMovieId();
                for (int j = 0; j < quantity; j++){
                    values += " (" + cid + "," + "'" + movieId + "', '" + saleDate + "'),";
                }
            }
            values = values.substring(0,values.length()-1);
            int retID = update.executeUpdate(values);

            // Get Sales ID
            int items_num = 0;
            for (int i = 0; i < items.size(); i++) {
                items_num += items.get(i).getQuantity();
            }

            Statement get = dbCon.createStatement();
            String select = "SELECT id from sales order by id DESC limit " + items_num;
            ResultSet saleIds = get.executeQuery(select);

            JsonArray jsonArray = new JsonArray();
            int total = 0;

            ArrayList<String> salesID_array = new ArrayList<String>();
            while (saleIds.next()){
                String saleID = saleIds.getString("id"); // last -> first
                salesID_array.add(saleID);
            }
//
            ArrayList<String> revSIds = new ArrayList<String>();
            for (int i = salesID_array.size() - 1; i >= 0; i--) {
                revSIds.add(salesID_array.get(i));
            }

            // populating confirmation items table
            int s = 0;
            for (int i = 0; i < items.size(); i++) {
                JsonObject jsonObject = new JsonObject();

                String saleID = revSIds.get(s);
                String title = items.get(i).getMovieTitle();
                String Movieid = items.get(i).getMovieId();
                int quantity = items.get(i).getQuantity();
                for (int k = 1; k < quantity; k++){
                    s++;
                    saleID = saleID + "," + revSIds.get(s);
                }
                int price = items.get(i).getPrice();
                total+=price;
                s++;

                jsonObject.addProperty("saleID", saleID);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("quantity", quantity);
                jsonObject.addProperty("id", Movieid);
                jsonObject.addProperty("price", price);
                jsonObject.addProperty("total", total);

                jsonArray.add(jsonObject);
            }

            session.setAttribute("items", null);

            out.write(jsonArray.toString());
            update.close();

            response.setStatus(200);


        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(500);
        }

        out.close();
        response.getWriter().write("success");
    }
}
