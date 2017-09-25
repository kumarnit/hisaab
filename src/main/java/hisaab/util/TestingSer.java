package hisaab.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestingSer
 */
public class TestingSer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestingSer() {
        super();
        // TODO Auto-generated constructor stub
    }

	private final String userID = "admin";
	private final String password = "admin";

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		// get request parameters for userID and password
		String user = request.getParameter("username");
		String pwd = request.getParameter("key");
		
		if(userID.equals(user) && password.equals(pwd)){
			Cookie loginCookie = new Cookie("user",user);
			//setting cookie to expiry in 30 mins
			loginCookie.setMaxAge(60*10);
//			loginCookie.setHttpOnly(false);

//			loginCookie.setSecure(true);
//			loginCookie.setComment("FullSecure");
			response.addCookie(loginCookie);
			
			response.sendRedirect("/index.jsp");
		}else{
			PrintWriter out= response.getWriter();
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/main.jsp");
			
			out.println("<font color=red>Either user name or password is wrong.</font>");
			rd.include(request, response);
		}

	}

}