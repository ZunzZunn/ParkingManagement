<%-- 
    Document   : revenue
    Created on : Mar 22, 2026, 10:41:25 AM
    Author     : Dell
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% model.User account = (model.User) session.getAttribute("account"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Doanh thu - iParking</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="revenue" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Báo cáo Doanh thu" />
                    <jsp:param name="subtitle" value="Thống kê thu nhập từ vé lượt và vé tháng." />
                </jsp:include>

                <section class="stats-grid">
                    <div class="stat-card">
                        <div class="icon-wrapper icon-blue"><i class="fa-solid fa-wallet"></i></div>
                        <div class="stat-info">
                            <h3><fmt:formatNumber value="${totalRevenue}" pattern="#,###"/>đ</h3>
                            <p>Tổng doanh thu kỳ này</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="icon-wrapper icon-green"><i class="fa-solid fa-money-bill-wave"></i></div>
                        <div class="stat-info">
                            <h3><fmt:formatNumber value="${ticketRevenue}" pattern="#,###"/>đ</h3>
                            <p>Từ Vé lượt</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="icon-wrapper icon-orange"><i class="fa-solid fa-id-card"></i></div>
                        <div class="stat-info">
                            <h3><fmt:formatNumber value="${monthlyRevenue}" pattern="#,###"/>đ</h3>
                            <p>Từ Vé tháng</p>
                        </div>
                    </div>
                </section>

                <section class="content-area">
                    <div class="filter-card">
                        <form action="revenue" method="GET" class="filter-grid">
                            <div class="form-group" style="margin: 0;">
                                <label>Từ ngày</label>
                                <input type="date" name="fromDate" value="${param.fromDate}" class="form-control">
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Đến ngày</label>
                                <input type="date" name="toDate" value="${param.toDate}" class="form-control">
                            </div>
                            <button type="submit" class="btn-add" style="margin-top: 22px; width: fit-content; padding: 0 20px;"><i class="fa-solid fa-filter"></i> Lọc dữ liệu</button>
                        </form>
                    </div>

                    <div class="content-card">
                        <div class="card-header">
                            <h2>Chi tiết dòng tiền</h2>
                        </div>
                        <table class="apple-table">
                            <thead>
                                <tr>
                                    <th>Mã GD</th>
                                    <th>Thời gian</th>
                                    <th>Loại thu nhập</th>
                                    <th>Nội dung</th>
                                    <th>Số tiền (VNĐ)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${revenueLogs}" var="log">
                                    <tr>
                                        <td><strong>#${log.transactionId}</strong></td>
                                        <td>${log.createdAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.type == 'Ticket'}"><span class="badge" style="background: rgba(0, 113, 227, 0.1); color: #0071e3;">Vé lượt</span></c:when>
                                                <c:otherwise><span class="badge" style="background: rgba(255, 149, 0, 0.1); color: #ff9500;">Vé tháng</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${log.description}</td>
                                        <td><strong style="color: #34c759;">+<fmt:formatNumber value="${log.amount}" pattern="#,###"/></strong></td>
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
