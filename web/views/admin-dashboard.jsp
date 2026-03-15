<%-- 
    Document   : admin-dashboard
    Created on : Mar 14, 2026, 6:15:31 PM
    Author     : ZunzZunn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    model.User account = (model.User) session.getAttribute("account");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tổng quan - iParking</title>
        <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='22' fill='%230071e3'/><text x='50' y='55' dominant-baseline='middle' text-anchor='middle' font-family='-apple-system, sans-serif' font-size='65' font-weight='bold' fill='white'>P</text></svg>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="dashboard" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Chào buổi sáng, ${account.fullName}!" />
                    <jsp:param name="subtitle" value="Hệ thống đang hoạt động ổn định. Đây là tình hình hôm nay." />
                </jsp:include>

                <section class="stats-grid">
                    <div class="stat-card">
                        <div class="icon-wrapper icon-blue"><i class="fa-solid fa-car-side"></i></div>
                        <div class="stat-info">
                            <h3>${parkedVehicles}</h3>
                            <p>Xe đang trong bãi</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="icon-wrapper icon-green"><i class="fa-solid fa-square-check"></i></div>
                        <div class="stat-info">
                            <h3>${availableSpots}</h3>
                            <p>Chỗ trống khả dụng</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="icon-wrapper icon-orange"><i class="fa-solid fa-clock-rotate-left"></i></div>
                        <div class="stat-info">
                            <h3>${expiringPasses}</h3>
                            <p>Vé tháng sắp hết hạn</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="icon-wrapper icon-purple"><i class="fa-solid fa-wallet"></i></div>
                        <div class="stat-info">
                            <h3>${todayRevenue}đ</h3>
                            <p>Doanh thu hôm nay</p>
                        </div>
                    </div>
                </section>

                <section class="content-area">
                    <div class="content-card">
                        <div class="card-header">
                            <h2>Giao dịch gần đây</h2>
                            <a href="${pageContext.request.contextPath}/transactions" class="btn-text" style="text-decoration: none; display: inline-block;">Xem tất cả</a>
                        </div>

                        <table class="apple-table">
                            <thead>
                                <tr>
                                    <th>Biển số</th>
                                    <th>Loại xe</th>
                                    <th>Khu vực (Ô đỗ)</th>
                                    <th>Thời gian vào</th>
                                    <th>Thời gian ra</th> 
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <c:forEach items="${recentTransactions}" var="tx">
                                    <tr>
                                        <td><strong>${tx.licensePlate}</strong></td>
                                        <td>${tx.typeName}</td>
                                        <td>${tx.slotCode}</td>
                                        <td>${tx.formattedTime}</td>
                                        <td>${tx.formattedOutTime}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tx.status == 'Active'}">
                                                    <span class="badge badge-active">Đang đỗ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-completed">Đã rời đi</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>
    </body>
</html>
