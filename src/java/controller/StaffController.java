package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dal.UserDAO;
import model.User;
import jakarta.servlet.http.HttpSession;

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

        // Lấy session để cài đặt thông báo (alert) hiển thị lên JSP
        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        if (account == null || account.getRoleID() != 1) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        try {
            if ("add".equals(action)) {
                String fullName = request.getParameter("fullName");
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String phoneNumber = request.getParameter("phoneNumber");
                String email = request.getParameter("email");

                // JSP đang gửi lên biến name="roleId" với value là 1 hoặc 2
                int roleId = Integer.parseInt(request.getParameter("roleId"));

                dao.addStaff(fullName, username, password, phoneNumber, email, roleId);
                session.setAttribute("dbMessage", "Thêm nhân viên thành công!");

            } else if ("edit".equals(action)) {
                // Bổ sung logic Sửa (Edit) vì trong JSP của bạn có form này
                int staffId = Integer.parseInt(request.getParameter("staffId"));
                String fullName = request.getParameter("fullName");
                String phoneNumber = request.getParameter("phoneNumber");
                String email = request.getParameter("email");
                int roleId = Integer.parseInt(request.getParameter("roleId"));

                dao.updateStaff(staffId, fullName, phoneNumber, email, roleId);
                session.setAttribute("dbMessage", "Cập nhật thông tin thành công!");

            } else if ("toggleStatus".equals(action)) {
                int staffId = Integer.parseInt(request.getParameter("staffId"));
                boolean currentStatus = Boolean.parseBoolean(request.getParameter("currentStatus"));

                dao.toggleStaffStatus(staffId, !currentStatus);
                session.setAttribute("dbMessage", "Đã thay đổi trạng thái tài khoản!");

            } else if ("delete".equals(action)) {
                int staffId = Integer.parseInt(request.getParameter("staffId"));

                // Gọi hàm xóa và kiểm tra kết quả
                boolean isDeleted = dao.deleteStaff(staffId);

                if (isDeleted) {
                    session.setAttribute("dbMessage", "Đã xóa nhân viên thành công!");
                } else {
                    // Nếu trả về false -> Bị vướng khóa ngoại do nhân viên này đã có lịch sử giao dịch
                    session.setAttribute("dbMessage", "LỖI: Không thể xóa! Nhân viên này đã có dữ liệu tạo vé hoặc thu tiền trên hệ thống. Vui lòng dùng chức năng Khóa tài khoản.");
                }
            }
        } catch (Exception e) {
            session.setAttribute("dbMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        // Sau khi xử lý xong, load lại trang staff. Thông báo sẽ được alert ra màn hình!
        response.sendRedirect("staff");
    }
}
