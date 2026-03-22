package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dal.UserDAO;
import model.User;

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
            String roleStr = request.getParameter("role");

            int roleId = "Admin".equals(roleStr) ? 1 : 2;
            String passwordHash = password;

            dao.addStaff(fullName, username, passwordHash, phoneNumber, email, roleId);
        } else if ("toggleStatus".equals(action)) {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            boolean currentStatus = Boolean.parseBoolean(request.getParameter("currentStatus"));
            dao.toggleStaffStatus(staffId, !currentStatus);
        }

        response.sendRedirect("staff");
    }
}
