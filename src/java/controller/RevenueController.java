package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dal.RevenueDAO; // Import DAO từ package dal
import model.RevenueLog; // Import Model
import model.User;

public class RevenueController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- KIỂM TRA QUYỀN (CHỈ ADMIN) ---
        User account = (User) request.getSession().getAttribute("account");
        if (account == null || account.getRoleID() != 1) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            return;
        }
        // ----------------------------------

        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        RevenueDAO dao = new RevenueDAO();

        double ticketRevenue = dao.getRevenueByType("Ticket", fromDate, toDate);
        double monthlyRevenue = dao.getRevenueByType("Monthly", fromDate, toDate);
        double totalRevenue = ticketRevenue + monthlyRevenue;

        List<RevenueLog> logs = dao.getRevenueLogs(fromDate, toDate);

        request.setAttribute("ticketRevenue", ticketRevenue);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("revenueLogs", logs);

        request.getRequestDispatcher("views/revenue.jsp").forward(request, response);
    }
}
