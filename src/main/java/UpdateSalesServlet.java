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
import java.time.LocalTime;


@WebServlet(name = "UpdateSalesServlet", urlPatterns = "/cs122b/update")
public class UpdateSalesServlet extends HttpServlet {
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
//        for (int i = 0; i < items.size(); i++){
//            total+=items.get(i).getPrice();
//        }

        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        if(session.getAttribute("items") == null) {
            ArrayList<Item> temp = new ArrayList<Item>();
            session.setAttribute("items", temp);
        }

//        ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
        for (int i = 0; i < items.size(); i++) {
            JsonObject jsonObject = new JsonObject();

            String title = items.get(i).getMovieTitle();
            String id = items.get(i).getMovieId();
            int quantity = items.get(i).getQuantity();
            int price = items.get(i).getPrice();
            total+=price;

            jsonObject.addProperty("title", title);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("price", price);
            jsonObject.addProperty("total", total);

            jsonArray.add(jsonObject);
        }

        out.write(jsonArray.toString());
        out.close();
        response.setStatus(200);

//        JsonObject responseJsonObject = new JsonObject();
//        responseJsonObject.addProperty("total_price", total);

        // write all the data into the jsonObject
//        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        response.setContentType("application/json"); // Response mime type

        PrintWriter out = response.getWriter();

        try {
            Connection dbCon = dataSource.getConnection();
//
//            // Declare a new statement
            Statement update = dbCon.createStatement();
            String cid = session.getAttribute("cid").toString();
//
            String values = "INSERT INTO sales (customerId, movieId, saleDate) VALUES";
            String saleDate = LocalTime.now().toString();
            ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
//            String values = "";
            for (int i = 0; i < items.size(); i++) {
                int quantity = items.get(i).getQuantity();
                String movieId = items.get(i).getMovieId();
                for (int j = 0; j < quantity; j++){
                    values += " (" + cid + "," + movieId + "," + saleDate + "),";
                }
            }
            values = values.substring(0,values.length()-1);
//
            int retID = update.executeUpdate(values);
            JsonArray jsonArray = new JsonArray();

            out.write(jsonArray.toString());
            response.setStatus(200);


        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }



        response.getWriter().write("success");
    }
}
