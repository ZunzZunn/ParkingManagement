/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ZunzZunn
 */
public class AuthenticationFilter implements Filter {

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // Ép kiểu để sử dụng được các hàm của HTTP
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Ngoại lệ: Cho phép truy cập thẳng vào trang login.jsp (để Controller còn có thể forward tới)
        String uri = req.getRequestURI();
        if (uri.endsWith("login.jsp")) {
            chain.doFilter(request, response); // Cho qua
            return;
        }

        // Lấy session hiện tại (truyền false để không tự động tạo mới nếu chưa có)
        HttpSession session = req.getSession(false);

        // Kiểm tra xem đã đăng nhập chưa (session tồn tại và có chứa đối tượng "account")
        boolean isLoggedIn = (session != null && session.getAttribute("account") != null);

        if (isLoggedIn) {
            // Đã đăng nhập hợp lệ -> Cho phép đi tiếp vào trang báo cáo/dashboard
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập -> "Đá" văng về trang đăng nhập
            // Dùng req.getContextPath() + "/login" để đảm bảo đường dẫn luôn chuẩn xác
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
