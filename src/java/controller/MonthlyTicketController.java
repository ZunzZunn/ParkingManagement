/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.MonthlyPassDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.MonthlyPassDTO;
import model.RenewalHistory;

/**
 *
 * @author myniy
 */
public class MonthlyTicketController extends HttpServlet {

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
            out.println("<title>Servlet MonthlyTicketController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MonthlyTicketController at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        MonthlyPassDAO dao = new MonthlyPassDAO();

        // 1. XỬ LÝ AJAX LẤY LỊCH SỬ GIA HẠN
        if ("getHistory".equals(action)) {
            String passIdStr = request.getParameter("passId");

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                // Kiểm tra an toàn: Nếu chuỗi rỗng hoặc null thì báo lỗi ra bảng luôn chứ không cho sập web
                if (passIdStr == null || passIdStr.trim().isEmpty()) {
                    out.print("<tr><td colspan='3' style='text-align:center; color:red;'>Lỗi: Không nhận được mã vé từ giao diện</td></tr>");
                    return;
                }

                int passId = Integer.parseInt(passIdStr);
                List<RenewalHistory> history = dao.getRenewalHistory(passId);

                if (history == null || history.isEmpty()) {
                    out.print("<tr><td colspan='3' style='text-align:center;'>Khách hàng này chưa từng gia hạn</td></tr>");
                } else {
                    java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    java.text.SimpleDateFormat sdfEnd = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    for (RenewalHistory h : history) {
                        out.print("<tr>");
                        out.print("<td>" + sdfDate.format(h.getRenewDate()) + "</td>");
                        out.print("<td>" + h.getDurationMonths() + " Tháng</td>");
                        out.print("<td><strong style='color: var(--apple-text-dark);'>" + sdfEnd.format(h.getNewEndDate()) + "</strong></td>");

                        // Cột mới in tên nhân viên
                        String staffName = h.getRenewedByName() != null ? h.getRenewedByName() : "Hệ thống";
                        out.print("<td><span style='color: #8e8e93;'><i class='fa-solid fa-user-pen'></i> " + staffName + "</span></td>");

                        out.print("</tr>");
                    }
                }
            }
            return; // Dừng hàm doGet ở đây để không load lại HTML của toàn trang
        }

        // 2. LOAD TRANG VÉ THÁNG (CÓ PHÂN TRANG BẰNG JAVA)
        String licensePlate = request.getParameter("licensePlate");
        String customerInfo = request.getParameter("customerInfo");
        String status = request.getParameter("status");

        // Nhận trang hiện tại
        int page = 1;
        int pageSize = 10; // 10 vé / trang
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e) {
            }
        }

        // Lấy TOÀN BỘ danh sách vé theo hàm DAO CŨ CỦA BẠN
        List<MonthlyPassDTO> allPasses = dao.getAllPasses(licensePlate, customerInfo, status);

        // --- BẮT ĐẦU XỬ LÝ PHÂN TRANG BẰNG JAVA ---
        int totalPasses = allPasses.size();
        int totalPages = (int) Math.ceil((double) totalPasses / pageSize);

        // Đảm bảo page không bị lỗi vượt quá giới hạn
        if (page > totalPages) {
            page = totalPages;
        }
        if (page < 1) {
            page = 1;
        }

        // Cắt subList để lấy đúng số vé của trang hiện tại
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalPasses);
        List<MonthlyPassDTO> paginatedPasses = (totalPasses > 0) ? allPasses.subList(start, end) : new java.util.ArrayList<>();

        // Đẩy dữ liệu lên JSP (Vẫn dùng tên biến "passes" để không phải sửa vòng lặp trên JSP của bạn)
        request.setAttribute("passes", paginatedPasses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalPasses", totalPasses);

        // Lấy danh sách ô đỗ trống truyền sang Form Đăng ký mới (Giữ nguyên của bạn)
        request.setAttribute("emptySlots", dao.getAvailableSlots());

        request.getRequestDispatcher("/views/monthly-ticket.jsp").forward(request, response);
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
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        MonthlyPassDAO dao = new MonthlyPassDAO();

        try {
            if ("register".equals(action)) {
                String customerName = request.getParameter("customerName");
                String phoneNumber = request.getParameter("phoneNumber");
                String licensePlate = request.getParameter("licensePlate");

                // --- 1. KIỂM TRA XE ĐÃ CÓ VÉ THÁNG CHƯA ---
                if (dao.hasActiveMonthlyPass(licensePlate)) {
                    // Nếu đã có, lưu lỗi vào session và load lại trang
                    request.getSession().setAttribute("errorMessage", "Biển số xe " + licensePlate + " đã có vé tháng đang hoạt động!");
                    response.sendRedirect("monthly-ticket");
                    return; // Dừng tại đây, không cho đăng ký
                }

                int duration = Integer.parseInt(request.getParameter("duration"));
                int slotId = Integer.parseInt(request.getParameter("slotId"));
                int typeId = Integer.parseInt(request.getParameter("typeId"));

                // Lấy thông tin tài khoản nhân viên
                model.User account = (model.User) request.getSession().getAttribute("account");
                int staffId = (account != null) ? account.getUserID() : 1;

                // Đăng ký mới
                boolean success = dao.addMonthlyPass(customerName, phoneNumber, slotId, licensePlate, typeId, duration, staffId);

                if (success) {
                    dao.updateSlotStatus(slotId, "Reserved");
                    request.getSession().setAttribute("successMessage", "Đăng ký vé tháng thành công cho xe " + licensePlate);
                } else {
                    request.getSession().setAttribute("errorMessage", "Lỗi hệ thống khi đăng ký vé!");
                }

            } else if ("renew".equals(action)) {
                int passId = Integer.parseInt(request.getParameter("passID"));
                int duration = Integer.parseInt(request.getParameter("duration"));

                model.User account = (model.User) request.getSession().getAttribute("account");
                int staffId = (account != null) ? account.getUserID() : 1;

                if (dao.renewMonthlyPass(passId, duration, staffId)) {
                    request.getSession().setAttribute("successMessage", "Gia hạn vé tháng thành công!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Lỗi hệ thống khi gia hạn vé!");
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi POST MonthlyTicket: " + e.getMessage());
        }

        // Sau khi xử lý DB xong thì load lại trang danh sách
        response.sendRedirect("monthly-ticket");
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
