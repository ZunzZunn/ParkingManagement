<%-- 
    Document   : sidebar
    Created on : Mar 15, 2026, 6:33:05 PM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Lấy thông tin account từ session để in tên ở góc dưới
    model.User account = (model.User) session.getAttribute("account");
%>

<nav class="sidebar">
    <div class="logo-area">
        <div class="logo-icon">
            <i class="fa-solid fa-square-parking"></i>
        </div>
        <span>iParking</span>
    </div>

    <ul class="nav-links">
        <li class="${param.activePage == 'dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/admin-dashboard"><i class="fa-solid fa-house"></i> Tổng quan</a>
        </li>
        <li class="${param.activePage == 'map' ? 'active' : ''}">
            <a href="#"><i class="fa-solid fa-car"></i> Sơ đồ bãi xe</a>
        </li>
        <li class="${param.activePage == 'monthly_ticket' ? 'active' : ''}">
            <a href="#"><i class="fa-solid fa-ticket"></i> Vé tháng</a>
        </li>
        <li class="${param.activePage == 'transactions' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/transactions"><i class="fa-solid fa-clock-rotate-left"></i> Quản lý Giao dịch</a>
        </li>
        <li class="${param.activePage == 'revenue' ? 'active' : ''}">
            <a href="#"><i class="fa-solid fa-chart-line"></i> Doanh thu</a>
        </li>
        <li class="${param.activePage == 'staff' ? 'active' : ''}">
            <a href="#"><i class="fa-solid fa-users"></i> Nhân viên</a>
        </li>
    </ul>

    <div class="user-profile">
        <div class="avatar" style="overflow: hidden;">
            <c:choose>
                <c:when test="${not empty account.avatar}">
                    <img src="${pageContext.request.contextPath}/${account.avatar}" alt="Avatar" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                </c:when>
                <c:otherwise>
                    <i class="fa-solid fa-user-tie"></i>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="info">
            <a href="${pageContext.request.contextPath}/profile" class="name"><%= account != null ? account.getFullName() : "Khách" %></a>
            <p class="role">Quản trị viên</p>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn" title="Đăng xuất">
            <i class="fa-solid fa-arrow-right-from-bracket"></i>
        </a>
    </div>
</nav>
