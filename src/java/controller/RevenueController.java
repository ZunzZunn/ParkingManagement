/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.RevenueDAO;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author myniy
 */
public class RevenueController extends HttpServlet {

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
            out.println("<title>Servlet RevenueController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RevenueController at " + request.getContextPath() + "</h1>");
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
        // ==========================================
        // 1. KIỂM TRA BẢO MẬT (CHỈ ADMIN MỚI ĐƯỢC VÀO)
        // ==========================================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login");
            return;
        }
        model.User account = (model.User) session.getAttribute("account");
        if (account.getRoleID() != 1) { // 1 là Admin
            response.sendRedirect("staff-dashboard"); // Nhấn viên cố tình vào sẽ bị đẩy về trang staff
            return;
        }

        RevenueDAO dao = new RevenueDAO();

        // ==========================================
        // 2. XỬ LÝ YÊU CẦU XUẤT EXCEL (ĐỊNH DẠNG CSV)
        // ==========================================
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");
            String sourceParam = request.getParameter("source");
            String search = request.getParameter("search");

            // Lấy TẤT CẢ dữ liệu theo bộ lọc (để pageSize cực lớn)
            List<model.RevenueDTO> exportData = dao.getRevenuesByPage(1, 1000000, fromDate, toDate, sourceParam, search);

            // Cấu hình Header để trình duyệt hiểu đây là file tải về
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"BaoCaoDoanhThu.csv\"");

            try (java.io.PrintWriter out = response.getWriter()) {
                out.write('\ufeff'); // Ký tự BOM giúp Excel đọc tiếng Việt không bị lỗi font
                out.println("Mã GD,Thời gian,Nguồn thu,Chi tiết,Số tiền (VNĐ)");

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (model.RevenueDTO rev : exportData) {
                    out.print(rev.getTransactionId() + ",");
                    out.print(sdf.format(rev.getDate()) + ",");
                    out.print(rev.getSource() + ",");
                    // Bọc chuỗi Chi tiết trong dấu ngoặc kép để tránh lỗi nếu có dấu phẩy
                    out.print("\"" + rev.getDescription().replace("\"", "\"\"") + "\",");
                    out.println(String.format("%.0f", rev.getAmount()));
                }
            }
            return; // Trả file về trình duyệt xong thì dừng luôn, không load HTML nữa
        }

        // ==========================================
        // 2. LẤY DỮ LIỆU THỐNG KÊ (3 THẺ TRÊN CÙNG)
        // ==========================================
        double dailyRevenue = dao.getDailyTicketRevenueToday();
        double monthlyPassRevenue = dao.getMonthlyPassRevenueThisMonth();
        double totalRevenue = dao.getTotalRevenueThisMonth();

        request.setAttribute("dailyRevenue", dailyRevenue);
        request.setAttribute("monthlyPassRevenue", monthlyPassRevenue);
        request.setAttribute("totalRevenue", totalRevenue);

        // ==========================================
        // 3. XỬ LÝ DỮ LIỆU BIỂU ĐỒ (CHART.JS)
        // ==========================================
        Map<String, Double> chartData = dao.getRevenueLast7Days();
        StringBuilder labels = new StringBuilder("[");
        StringBuilder data = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : chartData.entrySet()) {
            labels.append("'").append(entry.getKey()).append("',");
            data.append(entry.getValue()).append(",");
        }
        labels.append("]");
        data.append("]");

        request.setAttribute("chartLabels", labels.toString());
        request.setAttribute("chartData", data.toString());

        // ==========================================
        // 4. XỬ LÝ BỘ LỌC (FILTER) & PHÂN TRANG (PAGINATION)
        // ==========================================
        int pageSize = 10; // Cố định 10 giao dịch / trang
        int page = 1;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e) {
            }
        }

        // Lấy các tham số từ bộ lọc trên form
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String sourceParam = request.getParameter("source");
        String search = request.getParameter("search");

        // Gọi DAO để đếm tổng số bản ghi và lấy dữ liệu phân trang
        int totalTransactions = dao.getTotalRevenueCount(fromDate, toDate, sourceParam, search);
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
        List<model.RevenueDTO> paginatedRevenues = dao.getRevenuesByPage(page, pageSize, fromDate, toDate, sourceParam, search);

        // Đẩy danh sách dữ liệu và thông tin phân trang lên JSP
        request.setAttribute("paginatedRevenues", paginatedRevenues);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Đẩy lại các giá trị lọc lên JSP để giữ nguyên text trên ô input sau khi trang load lại
        request.setAttribute("paramFromDate", fromDate);
        request.setAttribute("paramToDate", toDate);
        request.setAttribute("paramSource", sourceParam);
        request.setAttribute("paramSearch", search);

        // ==========================================
        // 5. HIỂN THỊ GIAO DIỆN
        // ==========================================
        request.getRequestDispatcher("views/revenue.jsp").forward(request, response);
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
