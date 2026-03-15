<%-- 
    Document   : transactions
    Created on : Mar 15, 2026, 1:03:58 AM
    Author     : myniy
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
        <title>Quản lý giao dịch - iParking</title>
        <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='22' fill='%230071e3'/><text x='50' y='55' dominant-baseline='middle' text-anchor='middle' font-family='-apple-system, sans-serif' font-size='65' font-weight='bold' fill='white'>P</text></svg>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="sidebar.jsp">
                <jsp:param name="activePage" value="transactions" />
            </jsp:include>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Quản lý Giao dịch" />
                    <jsp:param name="subtitle" value="Toàn bộ lịch sử xe ra vào bãi được ghi nhận tại đây." />
                </jsp:include>

                <section class="content-area">
                    <div class="filter-card">
                        <form action="transactions" method="GET" class="filter-grid" id="filterForm">
                            <div class="form-group" style="margin: 0;">
                                <label>Biển số xe</label>
                                <input type="text" name="licensePlate" value="${param.licensePlate}" class="form-control auto-format-plate" placeholder="Nhập biển số...">
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Loại xe</label>
                                <select name="typeId" class="form-control">
                                    <option value="">-- Tất cả --</option>
                                    <option value="1" ${param.typeId == '1' ? 'selected' : ''}>Xe máy</option>
                                    <option value="2" ${param.typeId == '2' ? 'selected' : ''}>Ô tô</option>
                                    <option value="3" ${param.typeId == '3' ? 'selected' : ''}>Xe đạp</option>
                                </select>
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Khu vực</label>
                                <select name="zone" class="form-control">
                                    <option value="">-- Tất cả --</option>
                                    <option value="Khu Xe Máy" ${param.zone == 'Khu Xe Máy' ? 'selected' : ''}>Khu A (Xe máy)</option>
                                    <option value="Khu Ô tô" ${param.zone == 'Khu Ô tô' ? 'selected' : ''}>Khu B (Ô tô)</option>
                                    <option value="Khu Xe Đạp" ${param.zone == 'Khu Xe Đạp' ? 'selected' : ''}>Khu C (Xe đạp)</option>
                                </select>
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Trạng thái</label>
                                <select name="status" class="form-control">
                                    <option value="">-- Tất cả --</option>
                                    <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Đang đỗ</option>
                                    <option value="Completed" ${param.status == 'Completed' ? 'selected' : ''}>Đã rời đi</option>
                                </select>
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Từ ngày</label>
                                <input type="date" name="fromDate" value="${param.fromDate}" class="form-control">
                            </div>
                            <div class="form-group" style="margin: 0;">
                                <label>Đến ngày</label>
                                <input type="date" name="toDate" value="${param.toDate}" class="form-control">
                            </div>

                            <a href="transactions" class="btn-filter" style="background: var(--apple-border); color: var(--apple-text-dark); text-align: center; text-decoration: none; line-height: 40px;" title="Xóa bộ lọc"><i class="fa-solid fa-rotate-right"></i></a>
                        </form>
                    </div>
                    <div id="tableContainer">
                        <div class="content-card">
                            <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                                <h2 id="transactionCount">Tất cả giao dịch (${totalTransactions} lượt)</h2>
                                <button id="btnOpenModal" class="btn-add"><i class="fa-solid fa-arrow-right-to-bracket"></i> Ghi nhận Xe Vào</button>
                            </div>

                            <table class="apple-table">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Biển số</th>
                                        <th>Loại xe</th>
                                        <th>Khu vực (Ô đỗ)</th>
                                        <th>Thời gian vào</th>
                                        <th>Thời gian ra</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody id="tableBody">
                                    <c:forEach items="${allTransactions}" var="tx" varStatus="loop">
                                        <tr>
                                            <td>${(currentPage - 1) * 15 + loop.index + 1}</td>
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
                                            <td>
                                                <c:choose>
                                                    <c:when test="${tx.status == 'Active'}">
                                                        <form action="${pageContext.request.contextPath}/transactions" method="POST" style="margin: 0;">
                                                            <input type="hidden" name="action" value="checkout">
                                                            <input type="hidden" name="licensePlate" value="${tx.licensePlate}">
                                                            <button type="submit" class="btn-checkout" onclick="return confirm('Xác nhận cho xe ${tx.licensePlate} ra khỏi bãi?')">
                                                                Cho xe ra
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: var(--apple-text-light);"><i class="fa-solid fa-check"></i> Hoàn tất</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <c:if test="${totalPages > 1}">
                                <c:set var="q" value="&licensePlate=${param.licensePlate}&typeId=${param.typeId}&zone=${param.zone}&status=${param.status}&fromDate=${param.fromDate}&toDate=${param.toDate}" />

                                <div class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a href="transactions?page=${currentPage - 1}${q}" class="page-btn"><i class="fa-solid fa-chevron-left"></i></a>
                                        </c:if>

                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <a href="transactions?page=${i}${q}" class="page-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages}">
                                        <a href="transactions?page=${currentPage + 1}${q}" class="page-btn"><i class="fa-solid fa-chevron-right"></i></a>
                                        </c:if>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </section>
            </main>
        </div>

        <div class="modal-overlay" id="addModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Cho xe vào bãi</h2>
                    <button class="btn-close" id="btnCloseModal"><i class="fa-solid fa-xmark"></i></button>
                </div>
                <form action="${pageContext.request.contextPath}/transactions" method="POST">
                    <input type="hidden" name="action" value="checkin">
                    <div class="form-group">
                        <label>Biển số xe</label>
                        <input type="text" name="licensePlate" class="form-control auto-format-plate" placeholder="VD: 29A-123.45" required>
                    </div>
                    <div class="form-group">
                        <label>Loại phương tiện</label>
                        <select name="typeId" id="typeSelect" class="form-control" required>
                            <option value="">-- Chọn loại xe --</option>
                            <option value="1">Xe máy</option>
                            <option value="2">Ô tô 4-5 chỗ</option>
                            <option value="3">Xe đạp / Xe điện</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Chỗ đỗ trống</label>
                        <select name="slotId" id="slotSelect" class="form-control" required>
                            <option value="">-- Vui lòng chọn loại xe trước --</option>
                            <c:forEach items="${availableSlots}" var="slot">
                                <option value="${slot.slotId}" data-type="${slot.typeId}">${slot.zone} - ${slot.slotCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn-add" style="width: 100%; justify-content: center; margin-top: 20px; font-size: 16px; padding: 12px;">Xác nhận Check-in</button>
                </form>
            </div>
        </div>

        <script>
            // 1. Đóng mở Modal
            const modal = document.getElementById('addModal');
            document.getElementById('btnOpenModal').addEventListener('click', () => modal.classList.add('active'));
            document.getElementById('btnCloseModal').addEventListener('click', () => modal.classList.remove('active'));
            // Nhấn ra ngoài rìa đen cũng tự đóng
            window.addEventListener('click', (e) => {
                if (e.target === modal)
                    modal.classList.remove('active');
            });

            // 2. JS Thông minh: Chọn loại xe nào thì chỉ hiện chỗ đỗ của xe đó
            const typeSelect = document.getElementById('typeSelect');
            const slotSelect = document.getElementById('slotSelect');
            const allSlotOptions = Array.from(slotSelect.options); // Lưu bộ nhớ tạm

            typeSelect.addEventListener('change', function () {
                const selectedType = this.value;
                slotSelect.innerHTML = '<option value="">-- Chọn chỗ đỗ --</option>'; // Xóa trắng

                // Lọc chỗ đỗ đúng theo type
                allSlotOptions.forEach(opt => {
                    if (opt.getAttribute('data-type') === selectedType) {
                        slotSelect.appendChild(opt);
                    }
                });
            });
        </script>

        <script>
            const filterForm = document.getElementById('filterForm');
            const tableBody = document.getElementById('tableBody');
            let debounceTimer;

            // Hàm lấy dữ liệu (Hỗ trợ cả lọc và chuyển trang)
            async function fetchTableData(urlParams) {
                // 1. Ghi lại vị trí các dòng cũ (FIRST)
                const positions = new Map();
                tableBody.querySelectorAll('tr').forEach(row => {
                    const id = row.querySelector('strong')?.innerText;
                    if (id)
                        positions.set(id, row.getBoundingClientRect().top);
                });

                try {
                    const response = await fetch('transactions?' + urlParams);
                    const html = await response.text();
                    const doc = new DOMParser().parseFromString(html, 'text/html');

                    // 2. Cập nhật DOM nhanh
                    tableBody.innerHTML = doc.getElementById('tableBody').innerHTML;
                    const newPagination = doc.querySelector('.pagination');
                    const currentPagination = document.querySelector('.pagination');
                    if (newPagination && currentPagination)
                        currentPagination.innerHTML = newPagination.innerHTML;
                    else if (currentPagination)
                        currentPagination.innerHTML = '';

                    const newCount = doc.getElementById('transactionCount').innerText;
                    document.getElementById('transactionCount').innerText = newCount;

                    // 3. HIỆU ỨNG TRƯỢT (FLIP) - Trơn tru như thanh tìm kiếm cũ
                    requestAnimationFrame(() => {
                        tableBody.querySelectorAll('tr').forEach(row => {
                            const id = row.querySelector('strong')?.innerText;
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
                                row.style.opacity = '0';
                                setTimeout(() => {
                                    row.style.transition = 'opacity 0.4s';
                                    row.style.opacity = '1';
                                }, 50);
                            }
                        });
                    });

                    // 4. Đồng bộ URL để không bị mất lọc khi F5
                    window.history.pushState({}, '', 'transactions?' + urlParams);
                } catch (e) {
                    console.error("Lỗi tải bảng:", e);
                }
            }

            // Xử lý Lọc tự động (Real-time)
            filterForm.addEventListener('input', (e) => {
                clearTimeout(debounceTimer);
                const delay = (e.target.type === 'text') ? 250 : 0;
                debounceTimer = setTimeout(() => {
                    const params = new URLSearchParams(new FormData(filterForm));
                    params.delete('page'); // Khi lọc mới thì quay về trang 1
                    fetchTableData(params.toString());
                }, delay);
            });

            // Xử lý Phân trang AJAX (Không load lại trang)
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
            document.addEventListener('input', function (e) {
                // Kiểm tra xem phần tử đang gõ có chứa class auto-format-plate không
                if (e.target && e.target.classList.contains('auto-format-plate')) {

                    let val = e.target.value.toUpperCase();
                    let clean = val.replace(/[^A-Z0-9]/g, '');
                    let formatted = clean;

                    if (clean.length >= 7) {
                        let tail5 = clean.slice(-5);
                        let tail4 = clean.slice(-4);

                        if (/^\d{5}$/.test(tail5)) {
                            let prefix = clean.slice(0, -5);
                            formatted = prefix + '-' + tail5.slice(0, 3) + '.' + tail5.slice(3);
                        } else if (/^\d{4}$/.test(tail4)) {
                            let prefix = clean.slice(0, -4);
                            formatted = prefix + '-' + tail4;
                        }
                    }

                    if (e.target.value !== formatted) {
                        e.target.value = formatted;
                        // Kích hoạt sự kiện 'input' để tính năng Search Real-time nhận diện được sự thay đổi
                        e.target.dispatchEvent(new Event('input', {bubbles: true}));
                    }
                }
            });
        </script>
    </body>
</html>
