/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.SlotDTO;
import model.TransactionDTO;

/**
 *
 * @author myniy
 */
public class TransactionController extends HttpServlet {

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
            out.println("<title>Servlet TransactionController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TransactionController at " + request.getContextPath() + "</h1>");
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
        // 1. Kiểm tra bảo mật (Phải đăng nhập mới được xem)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login");
            return;
        }

        // --- BỘ LỌC VÀ PHÂN TRANG ---
        TicketDAO dao = new TicketDAO();
        int pageSize = 13;
        int page = 1;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e) {
            }
        }

        // 1. Nhận các tham số lọc từ Giao diện
        String licensePlate = request.getParameter("licensePlate");
        String typeId = request.getParameter("typeId");
        String zone = request.getParameter("zone");
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        // 2. Gọi DAO kèm theo tham số lọc
        int totalTransactions = dao.getTotalTransactionsCount(licensePlate, typeId, zone, status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
        List<TransactionDTO> allTransactions = dao.getTransactionsByPage(page, pageSize, licensePlate, typeId, zone, status, fromDate, toDate);

        // 3. Đẩy sang JSP
        request.setAttribute("allTransactions", allTransactions);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalTransactions", totalTransactions);

        // Lấy danh sách chỗ đỗ còn trống gửi sang JSP
        List<SlotDTO> availableSlots = dao.getAvailableSlots();
        request.setAttribute("availableSlots", availableSlots);

        // 3. Đẩy sang JSP
        request.setAttribute("allTransactions", allTransactions);
        request.getRequestDispatcher("views/transactions.jsp").forward(request, response);
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
        // Nhận diện hành động (checkin hay checkout)
        String action = request.getParameter("action");
        TicketDAO dao = new TicketDAO();

        // Lấy ID nhân viên đang trực
        HttpSession session = request.getSession();
        model.User account = (model.User) session.getAttribute("account");
        int staffId = account.getUserID();

        if ("checkin".equals(action)) {
            // Xử lý XE VÀO
            String licensePlate = request.getParameter("licensePlate");
            int typeId = Integer.parseInt(request.getParameter("typeId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            dao.checkInVehicle(licensePlate, typeId, slotId, staffId);

        } else if ("checkout".equals(action)) {
            // Xử lý XE RA
            String licensePlate = request.getParameter("licensePlate");
            dao.checkOutVehicle(licensePlate, staffId);
        }

        // Dù vào hay ra xong cũng tải lại trang để thấy kết quả mới nhất
        response.sendRedirect("transactions");
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
