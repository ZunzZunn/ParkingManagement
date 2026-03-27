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
                int staffId = Integer.parseInt(request.getParameter("staffId"));
                String fullName = request.getParameter("fullName");
                String phoneNumber = request.getParameter("phoneNumber");
                String email = request.getParameter("email");
                int roleId = Integer.parseInt(request.getParameter("roleId"));

                // Gọi hàm cập nhật DB của bạn
                boolean isUpdated = dao.updateStaff(staffId, fullName, phoneNumber, email, roleId);

                if (isUpdated) {
                    session.setAttribute("successMessage", "Cập nhật thông tin thành công!");

                    // ========================================================
                    // ĐOẠN CODE THÊM VÀO ĐỂ FIX LỖI SIDEBAR KHÔNG ĐỔI TÊN
                    // ========================================================
                    model.User currentAccount = (model.User) session.getAttribute("account");

                    // Kiểm tra xem ID của người vừa bị sửa có trùng với ID của người đang đăng nhập không
                    if (currentAccount != null && currentAccount.getUserID() == staffId) {
                        // Nếu đúng là tự sửa chính mình -> Cập nhật lại ngay Session trong RAM
                        currentAccount.setFullName(fullName);
                        currentAccount.setPhoneNumber(phoneNumber);
                        currentAccount.setEmail(email);
                        currentAccount.setRoleID(roleId);

                        // Ghi đè lại Session
                        session.setAttribute("account", currentAccount);
                    }
                    // ========================================================

                } else {
                    session.setAttribute("errorMessage", "Cập nhật thất bại!");
                }
                response.sendRedirect("staff");
                return;

            } else if ("toggleStatus".equals(action)) {
                int staffId = Integer.parseInt(request.getParameter("staffId"));
                boolean currentStatus = Boolean.parseBoolean(request.getParameter("currentStatus"));

                dao.toggleStaffStatus(staffId, !currentStatus);
                session.setAttribute("dbMessage", "Đã thay đổi trạng thái tài khoản!");

            } else if ("delete".equals(action)) {
                int staffId = Integer.parseInt(request.getParameter("staffId"));

                // --- 1. LOGIC BẢO VỆ ADMIN DUY NHẤT ---
                int targetRole = dao.getUserRole(staffId);
                if (targetRole == 1) { // 1 là RoleID của Admin
                    int currentAdmins = dao.countActiveAdmins();
                    if (currentAdmins <= 1) {
                        // Bị chặn: Đẩy thông báo lỗi cho Popup Apple
                        session.setAttribute("errorMessage", "Cảnh báo: Không thể xóa Quản trị viên duy nhất của hệ thống!");
                        response.sendRedirect("staff");
                        return; // Dừng thực thi lệnh xóa bên dưới
                    }
                }

                // --- 2. THỰC HIỆN XÓA NẾU AN TOÀN ---
                // Gọi hàm xóa và kiểm tra kết quả
                boolean isDeleted = dao.deleteStaff(staffId);

                if (isDeleted) {
                    // Thành công: Đẩy thông báo xanh cho Popup Apple
                    session.setAttribute("successMessage", "Đã xóa nhân viên thành công!");
                } else {
                    // Vướng khóa ngoại (Đã có giao dịch): Đẩy thông báo đỏ cho Popup Apple
                    session.setAttribute("errorMessage", "Không thể xóa! Nhân viên này đã có dữ liệu giao dịch. Vui lòng dùng chức năng Khóa tài khoản.");
                }

                // Load lại trang để hiển thị kết quả
                response.sendRedirect("staff");
                return;
            }
        } catch (Exception e) {
            session.setAttribute("dbMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        // Sau khi xử lý xong, load lại trang staff. Thông báo sẽ được alert ra màn hình!
        response.sendRedirect("staff");
    }
}
