/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.MonthlyPassDAO;
import dal.ParkingSlotDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.TransactionDTO;

/**
 *
 * @author myniy
 */
public class StaffDashboardController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StaffDashboardController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffDashboardController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login");
            return;
        }

        TicketDAO ticketDAO = new TicketDAO();
        ParkingSlotDAO slotDAO = new ParkingSlotDAO();
        MonthlyPassDAO passDAO = new MonthlyPassDAO();

        int parkedVehicles = ticketDAO.getTotalParkedVehicles();
        int availableSpots = slotDAO.getAvailableSpots();
        int expiringPasses = passDAO.getExpiringMonthlyPasses();

        // Đã bỏ phần lấy doanh thu (todayRevenue) vì Staff không có quyền xem
        request.setAttribute("parkedVehicles", parkedVehicles);
        request.setAttribute("availableSpots", availableSpots);
        request.setAttribute("expiringPasses", expiringPasses);

        List<TransactionDTO> recentTransactions = ticketDAO.getRecentTransactions();
        request.setAttribute("recentTransactions", recentTransactions);

        // Forward sang trang giao diện dashboard của staff (tên file tùy thuộc vào cách bạn lưu, có thể là staff-dashboard.jsp hoặc staff.jsp)
        request.getRequestDispatcher("views/staff-dashboard.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
