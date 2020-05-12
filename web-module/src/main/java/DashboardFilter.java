package main.java;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "DashboardFilter", urlPatterns = "/*")
public class DashboardFilter implements Filter {
    private final ArrayList<String> notAllowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("DashboardFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in (already in login page)
        if (this.isUrlAllowedWithoutAdmin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession(true).getAttribute("admin") == null || httpRequest.getSession(true).getAttribute("admin").equals(false)) {
            httpResponse.sendRedirect("index.html");
        }
        else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutAdmin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        if(notAllowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith)){
            return false;
        }
        return true;
//        return notAllowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        notAllowedURIs.add("dashboard.html");
        notAllowedURIs.add("dashboard.js");
        notAllowedURIs.add("cs122b/dashboard");
        notAllowedURIs.add("cs122b/add-movie");
        notAllowedURIs.add("cs122b/add-star");
    }

    public void destroy() {
        // ignored.
    }

}
