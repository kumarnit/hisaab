package hisaab.services.notification.webservice;

import hisaab.services.notification.NotificationHelper;
import hisaab.services.notification.webservice.bean.SystemUpdateBean;
import hisaab.util.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NotificationServlet
 */

public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NotificationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String min = request.getParameter("min");
		String max = request.getParameter("max");
		String stop = request.getParameter("stop");
		String features1[] = request.getParameterValues("fields[]");
		System.out.println("==  "+min);
		System.out.println("==  "+max);
		System.out.println("==  "+stop);
		System.out.println("00"+features1.toString());
		List<String> features = new ArrayList<String>();
		for(String str : features1 ){
			features.add(str);
			System.out.println("inner : "+str);
		}
		
		SystemUpdateBean sub = new SystemUpdateBean();
		sub.getUpdate().setMaxAvialableVersion(max);
		sub.getUpdate().setMinStableAppVersion(min);
		sub.getUpdate().setStopSupportForVersion(stop);
		sub.getUpdate().setNewFeatures(features);
		sub.getUpdate().setNotificationType(Constants.NOTIFICATION_SYSTEM_NOTIFICATION);
		NotificationHelper.buildAndSendUpdateSystemNotification(sub.getUpdate(),"New Update Availble");
		PrintWriter out = response.getWriter();
		out.print("<html>sucess</html>");
		response.sendRedirect("updateNotification.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}

}
