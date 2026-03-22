<%-- 
    Document   : revenue
    Created on : Mar 22, 2026, 3:36:41 PM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Doanh Thu - iParking</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/revenue.css"> <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="revenue" />
            </jsp:include>

            <main class="main-content">
                <header class="top-header">
                    <div class="greeting">
                        <h1>Báo cáo Doanh thu</h1>
                        <p>Theo dõi tình hình tài chính của bãi đỗ xe</p>
                    </div>

                    <div class="header-actions">
                        <label class="theme-switch">
                            <input type="checkbox" id="themeToggle">
                            <span class="slider">
                                <i class="fa-solid fa-moon"></i>
                                <i class="fa-solid fa-sun"></i>
                            </span>
                        </label>
                    </div>
                </header>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="icon-wrapper icon-blue">
                            <i class="fa-solid fa-wallet"></i>
                        </div>
                        <div class="stat-info">
                            <p>Tổng doanh thu (Tháng này)</p>
                            <h3><fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="0"/> đ</h3>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="icon-wrapper icon-purple">
                            <i class="fa-solid fa-ticket"></i>
                        </div>
                        <div class="stat-info">
                            <p>Doanh thu vé tháng</p>
                            <h3><fmt:formatNumber value="${monthlyPassRevenue}" type="number" maxFractionDigits="0"/> đ</h3>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="icon-wrapper icon-green">
                            <i class="fa-solid fa-money-bill-trend-up"></i>
                        </div>
                        <div class="stat-info">
                            <p>Doanh thu vé lượt (Hôm nay)</p>
                            <h3><fmt:formatNumber value="${dailyRevenue}" type="number" maxFractionDigits="0"/> đ</h3>
                        </div>
                    </div>
                </div>

                <div class="content-card" style="margin-bottom: 24px;">
                    <div class="card-header">
                        <h2>Biểu đồ doanh thu vé lượt (7 ngày qua)</h2>
                    </div>
                    <div class="chart-container">
                        <canvas id="revenueChart"></canvas>
                    </div>
                </div>

                <div class="filter-card">
                    <form action="revenue" method="GET" class="filter-grid" id="filterForm">
                        <div class="form-group" style="margin: 0;">
                            <label>Từ ngày</label>
                            <input type="date" name="fromDate" value="${paramFromDate}" class="form-control">
                        </div>
                        <div class="form-group" style="margin: 0;">
                            <label>Đến ngày</label>
                            <input type="date" name="toDate" value="${paramToDate}" class="form-control">
                        </div>
                        <div class="form-group" style="margin: 0;">
                            <label>Nguồn thu</label>
                            <select name="source" class="form-control">
                                <option value="All" ${paramSource == 'All' ? 'selected' : ''}>-- Tất cả --</option>
                                <option value="Vé vãng lai" ${paramSource == 'Vé vãng lai' ? 'selected' : ''}>Vé vãng lai</option>
                                <option value="Gia hạn vé tháng" ${paramSource == 'Gia hạn vé tháng' ? 'selected' : ''}>Gia hạn vé tháng</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin: 0;">
                            <label>Tìm kiếm (Biển số / Tên KH)</label>
                            <input type="text" name="search" value="${paramSearch}" class="form-control auto-format-plate" placeholder="Nhập từ khóa...">
                        </div>
                        <div class="form-group" style="margin: 0;">
                            <label>&nbsp;</label> 
                            <a href="revenue" class="btn-reset-filter" title="Xóa tất cả bộ lọc">
                                <i class="fa-solid fa-rotate-right"></i>
                            </a>
                        </div>
                    </form>
                </div>

                <div id="tableContainer">
                    <div class="content-card revenue-table-card">
                        <div class="card-header">
                            <h2>Chi tiết doanh thu</h2>
                            <button id="btnExportExcel" class="btn-add" style="background-color: #107c41; color: white;">
                                <i class="fa-solid fa-file-excel"></i> Xuất báo cáo
                            </button>
                        </div>
                        <div class="table-responsive">
                            <table class="apple-table">
                                <thead>
                                    <tr>
                                        <th>Mã GD</th>
                                        <th>Thời gian</th>
                                        <th>Nguồn thu</th>
                                        <th>Chi tiết</th>
                                        <th class="text-right">Số tiền</th>
                                    </tr>
                                </thead>
                                <tbody id="tableBody">
                                    <c:forEach items="${paginatedRevenues}" var="rev">
                                        <tr>
                                            <td class="col-tx-id">${rev.transactionId}</td>
                                            <td><fmt:formatDate value="${rev.date}" pattern="HH:mm - dd/MM/yyyy"/></td>
                                            <td>
                                                <span class="badge ${rev.source == 'Vé vãng lai' ? 'badge-completed' : 'badge-active'}">
                                                    ${rev.source}
                                                </span>
                                            </td>
                                            <td class="col-desc"><strong>${rev.description}</strong></td>
                                            <td class="col-amount">
                                                + <fmt:formatNumber value="${rev.amount}" type="number" maxFractionDigits="0"/> đ
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty paginatedRevenues}">
                                        <tr class="empty-row">
                                            <td colspan="5" style="text-align: center; padding: 30px; color: gray;">Không tìm thấy giao dịch nào phù hợp với bộ lọc.</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <c:if test="${totalPages > 1}">
                            <c:set var="filterQuery" value="&fromDate=${paramFromDate}&toDate=${paramToDate}&source=${paramSource}&search=${paramSearch}"/>
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="revenue?page=${currentPage - 1}${filterQuery}" class="page-btn"><i class="fa-solid fa-chevron-left"></i></a>
                                    </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <a href="revenue?page=${i}${filterQuery}" class="page-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <a href="revenue?page=${currentPage + 1}${filterQuery}" class="page-btn"><i class="fa-solid fa-chevron-right"></i></a>
                                    </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>

            </main>
        </div>

        <script>
            // 1. Giao diện Dark/Light mode
            const themeToggle = document.getElementById('themeToggle');
            const htmlElement = document.documentElement;
            if (localStorage.getItem('theme') === 'dark') {
                htmlElement.setAttribute('data-theme', 'dark');
                themeToggle.checked = true;
            }
            themeToggle.addEventListener('change', () => {
                if (themeToggle.checked) {
                    htmlElement.setAttribute('data-theme', 'dark');
                    localStorage.setItem('theme', 'dark');
                } else {
                    htmlElement.setAttribute('data-theme', 'light');
                    localStorage.setItem('theme', 'light');
                }
            });

            // ==========================================
            // KHÔI PHỤC CODE VẼ BIỂU ĐỒ CHART.JS
            // ==========================================
            const ctx = document.getElementById('revenueChart').getContext('2d');

            let gradient = ctx.createLinearGradient(0, 0, 0, 400);
            gradient.addColorStop(0, 'rgba(0, 113, 227, 0.4)');
            gradient.addColorStop(1, 'rgba(0, 113, 227, 0.0)');

            const chartLabels = ${chartLabels != null ? chartLabels : '[]'};
            const chartDataPoints = ${chartData != null ? chartData : '[]'};

            const revenueChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: chartLabels,
                    datasets: [{
                            label: 'Doanh thu (VNĐ)',
                            data: chartDataPoints,
                            borderColor: '#0071e3',
                            backgroundColor: gradient,
                            borderWidth: 3,
                            pointBackgroundColor: '#ffffff',
                            pointBorderColor: '#0071e3',
                            pointBorderWidth: 2,
                            pointRadius: 4,
                            pointHoverRadius: 6,
                            fill: true,
                            tension: 0.4
                        }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {display: false},
                        tooltip: {
                            backgroundColor: 'rgba(0,0,0,0.8)',
                            padding: 12,
                            titleFont: {family: '-apple-system', size: 14},
                            bodyFont: {family: '-apple-system', size: 14},
                            displayColors: false,
                            callbacks: {
                                label: function (context) {
                                    let value = context.parsed.y;
                                    return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value);
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            grid: {display: false},
                            ticks: {font: {family: '-apple-system'}}
                        },
                        y: {
                            grid: {
                                color: 'rgba(0, 0, 0, 0.05)',
                                drawBorder: false
                            },
                            beginAtZero: true,
                            ticks: {
                                font: {family: '-apple-system'},
                                callback: function (value) {
                                    return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                                }
                            }
                        }
                    }
                }
            });

            // ==========================================
            // 2. JAVASCRIPT LỌC REAL-TIME & FLIP ANIMATION
            // ==========================================
            const filterForm = document.getElementById('filterForm');
            const tableBody = document.getElementById('tableBody');
            let debounceTimer;

            async function fetchTableData(urlParams) {
                // Bước 1: Ghi lại vị trí các dòng cũ (FIRST)
                const positions = new Map();
                tableBody.querySelectorAll('tr').forEach(row => {
                    // Lấy Mã GD làm key
                    const idCell = row.querySelector('.col-tx-id');
                    const id = idCell ? idCell.innerText.trim() : null;
                    if (id) {
                        positions.set(id, row.getBoundingClientRect().top);
                    }
                });

                // --- HIỆU ỨNG PHẢN HỒI TỨC THÌ (DIM/FADE) ---
                // Làm mờ bảng ngay khi người dùng gõ phím hoặc chọn bộ lọc
                tableBody.style.transition = 'opacity 0.2s ease';
                tableBody.style.opacity = '0.4';

                try {
                    // Gọi AJAX
                    const response = await fetch('revenue?' + urlParams);
                    const html = await response.text();
                    const doc = new DOMParser().parseFromString(html, 'text/html');

                    // Bước 2: Cập nhật DOM nhanh
                    tableBody.innerHTML = doc.getElementById('tableBody').innerHTML;

                    // Cập nhật thanh phân trang
                    const newPagination = doc.querySelector('.pagination');
                    const currentPagination = document.querySelector('.pagination');
                    if (newPagination && currentPagination) {
                        currentPagination.innerHTML = newPagination.innerHTML;
                    } else if (newPagination && !currentPagination) {
                        document.querySelector('.table-responsive').insertAdjacentHTML('afterend', newPagination.outerHTML);
                    } else if (!newPagination && currentPagination) {
                        currentPagination.remove();
                    }

                    // Bước 3: HIỆU ỨNG TRƯỢT FLIP & HIỆN RÕ TRỞ LẠI
                    requestAnimationFrame(() => {
                        // Khôi phục lại độ sáng 100% khi có dữ liệu mới
                        tableBody.style.opacity = '1';

                        tableBody.querySelectorAll('tr').forEach(row => {
                            const idCell = row.querySelector('.col-tx-id');
                            const id = idCell ? idCell.innerText.trim() : null;

                            const oldTop = positions.get(id);

                            if (oldTop) {
                                const delta = oldTop - row.getBoundingClientRect().top;
                                if (delta !== 0) {
                                    row.style.transition = 'none';
                                    row.style.transform = `translateY(${delta}px)`;

                                    requestAnimationFrame(() => {
                                        row.style.transition = 'transform 0.5s cubic-bezier(0.2, 1, 0.2, 1)';
                                        row.style.transform = '';
                                    });
                                }
                            } else {
                                // Dòng mới: Cho mờ đi rồi từ từ hiện ra
                                row.style.opacity = '0';
                                setTimeout(() => {
                                    row.style.transition = 'opacity 0.4s';
                                    row.style.opacity = '1';
                                }, 50);
                            }
                        });
                    });

                    // Bước 4: Đồng bộ URL
                    window.history.pushState({}, '', 'revenue?' + urlParams);

                } catch (e) {
                    console.error("Lỗi tải bảng:", e);
                    // Đảm bảo bảng sáng trở lại nếu xảy ra lỗi mạng
                    tableBody.style.opacity = '1';
                }
            }

            filterForm.addEventListener('input', (e) => {
                clearTimeout(debounceTimer);
                const delay = (e.target.type === 'text') ? 250 : 0;

                debounceTimer = setTimeout(() => {
                    const params = new URLSearchParams(new FormData(filterForm));
                    params.delete('page');
                    fetchTableData(params.toString());
                }, delay);
            });

            document.addEventListener('click', (e) => {
                const btn = e.target.closest('.page-btn');
                if (btn) {
                    e.preventDefault();
                    const url = new URL(btn.href);
                    fetchTableData(url.searchParams.toString());
                }
            });
        </script>

        <script>
            // Xử lý nút Xuất Excel
            const btnExportExcel = document.getElementById('btnExportExcel');
            if (btnExportExcel) {
                btnExportExcel.addEventListener('click', function () {
                    // Lấy toàn bộ tham số bộ lọc hiện tại trên form
                    const filterForm = document.getElementById('filterForm');
                    const params = new URLSearchParams(new FormData(filterForm));

                    // Gắn thêm cờ action=export để báo cho Backend biết
                    params.set('action', 'export');

                    // Điều hướng URL để tải file về
                    window.location.href = 'revenue?' + params.toString();
                });
            }
        </script>
    </body>
</html>
