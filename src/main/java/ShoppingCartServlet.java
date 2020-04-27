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
import java.util.ArrayList;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/cs122b/shopping-cart")
public class ShoppingCartServlet extends HttpServlet{
    /**
     * handles POST requests to store session information
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession(true);
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        if(session.getAttribute("items") == null) {
            ArrayList<Item> temp = new ArrayList<Item>();
            temp.add(new Item("12", "test", 12, 100));
            session.setAttribute("items", temp);
        }

        ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
        for (int i = 0; i < items.size(); i++) {
            JsonObject jsonObject = new JsonObject();

            String title = items.get(i).getMovieTitle();
            String id = items.get(i).getMovieId();
            int quantity = items.get(i).getQuantity();
            int price = items.get(i).getPrice();

            jsonObject.addProperty("title", title);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("price", price);

            jsonArray.add(jsonObject);
        }

        out.write(jsonArray.toString());
        response.setStatus(200);
    }
}
