package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dal.UserDAO;
import model.User;
import jakarta.servlet.annotation.WebServlet;

public class StaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User account = (User) request.getSession().getAttribute("account");
        if (account == null || account.getRoleID() != 1) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            return;
        }

        UserDAO dao = new UserDAO();
        List<User> staffList = dao.getAllStaff();
        request.setAttribute("staffList", staffList);
        request.getRequestDispatcher("views/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User account = (User) request.getSession().getAttribute("account");
        if (account == null || account.getRoleID() != 1) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        if ("add".equals(action)) {
            String fullName = request.getParameter("fullName");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String phoneNumber = request.getParameter("phoneNumber");
            String email = request.getParameter("email");
            int roleId = Integer.parseInt(request.getParameter("roleId"));

            String result = dao.addStaff(fullName, username, password, phoneNumber, email, roleId);

            if (!"SUCCESS".equals(result)) {
                // Nếu SQL Server báo lỗi trùng lặp (UNIQUE KEY)
                if (result.contains("Violation of UNIQUE KEY constraint") || result.contains("duplicate key")) {
                    request.getSession().setAttribute("dbMessage", "Lỗi: Tên đăng nhập hoặc Email này đã tồn tại trong hệ thống!");
                } else {
                    request.getSession().setAttribute("dbMessage", "Lỗi hệ thống: " + result);
                }
            } else {
                request.getSession().setAttribute("dbMessage", "Thêm nhân viên thành công!");
            }

        } else if ("edit".equals(action)) {
            // ... (Phần sửa, khóa, xóa mình giữ nguyên cho gọn)
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String email = request.getParameter("email");
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            dao.updateStaff(staffId, fullName, phoneNumber, email, roleId);
            request.getSession().setAttribute("dbMessage", "Cập nhật thông tin thành công!");

        } else if ("toggleStatus".equals(action)) {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            boolean currentStatus = Boolean.parseBoolean(request.getParameter("currentStatus"));
            dao.toggleStaffStatus(staffId, !currentStatus);
            request.getSession().setAttribute("dbMessage", "Đã thay đổi trạng thái tài khoản!");

        } else if ("delete".equals(action)) {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            dao.deleteStaff(staffId);
            request.getSession().setAttribute("dbMessage", "Đã xóa nhân viên thành công!");
        }

        // Làm xong thì quay về trang staff để hiện thông báo
        response.sendRedirect("staff");
    }
}
