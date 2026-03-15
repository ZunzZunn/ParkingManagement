<%-- 
    Document   : parking-map
    Created on : Mar 16, 2026, 12:54:47 AM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% model.User account = (model.User) session.getAttribute("account"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sơ đồ bãi xe - iParking</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/parking-map.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="sidebar.jsp"><jsp:param name="activePage" value="parking-map" /></jsp:include>

                <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="title" value="Sơ đồ bãi đỗ xe" />
                    <jsp:param name="subtitle" value="Giám sát tình trạng chỗ đỗ theo thời gian thực" />
                </jsp:include>

                <section class="content-area">
                    <div class="map-legend">
                        <div class="legend-item"><div class="legend-dot" style="background: #34c759;"></div> Trống (Available)</div>
                        <div class="legend-item"><div class="legend-dot" style="background: #0071e3;"></div> Đang đỗ (Occupied)</div>
                        <div class="legend-item"><div class="legend-dot" style="background: #ff9500;"></div> Đã đặt (Reserved)</div>
                        <div class="legend-item"><div class="legend-dot" style="background: #ff3b30;"></div> Bảo trì (Maintenance)</div>
                    </div>

                    <h2 class="zone-header"><i class="fa-solid fa-motorcycle" style="color: var(--apple-blue);"></i> Khu vực A - Xe máy</h2>
                    <div class="slot-grid">
                        <c:forEach items="${motorSlots}" var="s">
                            <div class="slot-card status-${s.status}" title="Trạng thái: ${s.status}" onclick="openSlotModal(${s.slotID})">
                                <i class="fa-solid fa-motorcycle" style="font-size: 24px;"></i>
                                <h3>${s.slotCode}</h3>
                                <small>${s.status == 'Available' ? 'Trống' : s.status == 'Occupied' ? 'Đang đỗ' : s.status == 'Maintenance' ? 'Bảo trì' : 'Đã đặt'}</small>
                            </div>
                        </c:forEach>
                    </div>

                    <h2 class="zone-header"><i class="fa-solid fa-car" style="color: var(--apple-blue);"></i> Khu vực B - Ô tô</h2>
                    <div class="slot-grid">
                        <c:forEach items="${carSlots}" var="s">
                            <div class="slot-card status-${s.status}" title="Trạng thái: ${s.status}" onclick="openSlotModal(${s.slotID})">
                                <i class="fa-solid fa-car" style="font-size: 24px;"></i>
                                <h3>${s.slotCode}</h3>
                                <small>${s.status == 'Available' ? 'Trống' : s.status == 'Occupied' ? 'Đang đỗ' : s.status == 'Maintenance' ? 'Bảo trì' : 'Đã đặt'}</small>
                            </div>
                        </c:forEach>
                    </div>

                    <h2 class="zone-header"><i class="fa-solid fa-bicycle" style="color: var(--apple-blue);"></i> Khu vực C - Xe đạp</h2>
                    <div class="slot-grid">
                        <c:forEach items="${bikeSlots}" var="s">
                            <div class="slot-card status-${s.status}" title="Trạng thái: ${s.status}" onclick="openSlotModal(${s.slotID})">
                                <i class="fa-solid fa-bicycle" style="font-size: 24px;"></i>
                                <h3>${s.slotCode}</h3>
                                <small>${s.status == 'Available' ? 'Trống' : s.status == 'Occupied' ? 'Đang đỗ' : s.status == 'Maintenance' ? 'Bảo trì' : 'Đã đặt'}</small>
                            </div>
                        </c:forEach>
                    </div>
                </section>
            </main>
        </div>

        <div class="map-modal" id="slotModal">
            <div class="map-modal-content">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <h3 id="modalTitle" style="margin: 0; color: var(--apple-text-dark);"><i class="fa-solid fa-circle-info"></i> Chi tiết ô đỗ</h3>
                    <i class="fa-solid fa-xmark" style="cursor: pointer; font-size: 24px; color: var(--apple-text-light);" onclick="closeSlotModal()"></i>
                </div>
                <div id="modalBody" style="text-align: left;">
                </div>
            </div>
        </div>

        <script>
            const slotModal = document.getElementById('slotModal');
            const modalBody = document.getElementById('modalBody');
            const modalTitle = document.getElementById('modalTitle');

            // Mở Modal và lấy dữ liệu
            async function openSlotModal(slotId) {
                slotModal.classList.add('active');
                modalBody.innerHTML = '<div style="text-align:center; padding: 20px;"><i class="fa-solid fa-spinner fa-spin fa-2x" style="color: var(--apple-blue);"></i><p>Đang tải dữ liệu...</p></div>';

                try {
                    const params = new URLSearchParams();
                    params.append('action', 'getDetail');
                    params.append('slotId', slotId);
                    const res = await fetch('${pageContext.request.contextPath}/parking-map', {method: 'POST', body: params});
                    const data = await res.json();

                    modalTitle.innerHTML = `<i class="fa-solid fa-location-dot" style="color: var(--apple-blue);"></i> Ô đỗ: \${data.slotCode}`;

                    if (data.status === 'Available') {
                        modalBody.innerHTML = `
                        <div style="background: rgba(52, 199, 89, 0.1); color: #34c759; padding: 8px 16px; border-radius: 12px; display: inline-block; margin-bottom: 20px; font-weight: 600;">Trạng thái: Trống</div>
                        <div class="form-group">
                            <label style="font-weight: 500; color: var(--apple-text-dark);">Nhập biển số xe vào:</label>
                            <input type="text" id="inLicensePlate" placeholder="VD: 29A-123.45" class="apple-input" style="text-transform: uppercase;">
                        </div>
                        <button class="apple-modal-btn btn-in" onclick="submitCheckIn(\${data.slotId}, \${data.typeId})"><i class="fa-solid fa-arrow-right-to-bracket"></i> Cho xe vào bãi</button>
                        
                        <button class="apple-modal-btn btn-maintenance" onclick="submitToggleMaintenance(\${data.slotId}, 'Maintenance')"><i class="fa-solid fa-triangle-exclamation"></i> Tạm khóa / Đánh dấu bảo trì</button>
                    `;
                    } else if (data.status === 'Occupied') {
                        modalBody.innerHTML = `
                        <div style="background: rgba(0, 113, 227, 0.1); color: #0071e3; padding: 8px 16px; border-radius: 12px; display: inline-block; margin-bottom: 20px; font-weight: 600;">Trạng thái: Đang đỗ xe</div>
                        <div style="background: var(--apple-bg-grey); padding: 20px; border-radius: 16px; margin-bottom: 24px; border: 1px solid var(--apple-border);">
                            <p style="margin: 0 0 4px; color: var(--apple-text-light); font-size: 14px;">Biển số xe đang đỗ:</p>
                            <h2 style="margin: 0; color: var(--apple-text-dark); letter-spacing: 2px;">\${data.licensePlate}</h2>
                            <hr style="border: 0; border-top: 1px solid var(--apple-border); margin: 16px 0;">
                            <p style="margin: 0 0 4px; color: var(--apple-text-light); font-size: 14px;">Giờ vào bãi:</p>
                            <p style="margin: 0; font-weight: 600; color: var(--apple-text-dark);">\${data.checkInTime}</p>
                        </div>
                        <button class="apple-modal-btn btn-out" onclick="submitCheckOut(\${data.slotId})"><i class="fa-solid fa-arrow-right-from-bracket"></i> Thanh toán & Cho xe ra</button>
                    `;
                    } else if (data.status === 'Maintenance') {
                        // XỬ LÝ RIÊNG CHO Ô ĐANG BẢO TRÌ
                        modalBody.innerHTML = `
                        <div style="background: rgba(255, 59, 48, 0.1); color: #ff3b30; padding: 8px 16px; border-radius: 12px; display: inline-block; margin-bottom: 20px; font-weight: 600;">Trạng thái: Đang bảo trì</div>
                        <p style="color: var(--apple-text-light); line-height: 1.5; background: var(--apple-bg-grey); padding: 16px; border-radius: 12px; margin-bottom: 24px;">Ô đỗ này đang được phong tỏa để sửa chữa hoặc dọn dẹp. Không thể tiếp nhận xe.</p>
    
                        <button class="apple-modal-btn btn-complete-maintenance" onclick="submitToggleMaintenance(\${data.slotId}, 'Available')"><i class="fa-solid fa-wrench"></i> Hoàn tất bảo trì (Mở lại ô)</button>
                        `;
                    } else {
                        // Ô ĐÃ ĐẶT TRƯỚC (Reserved)
                        modalBody.innerHTML = `
                        <div style="background: rgba(255, 149, 0, 0.1); color: #ff9500; padding: 8px 16px; border-radius: 12px; display: inline-block; margin-bottom: 20px; font-weight: 600;">Trạng thái: Đã đặt vé tháng</div>
                        <p style="color: var(--apple-text-light); line-height: 1.5; background: var(--apple-bg-grey); padding: 16px; border-radius: 12px;">Ô đỗ này đã được khách hàng thuê cố định. Không khả dụng cho xe vãng lai.</p>
                    `;
                    }
                } catch (e) {
                    modalBody.innerHTML = '<p style="color: red;">Lỗi kết nối máy chủ!</p>';
                }
            }

            function closeSlotModal() {
                slotModal.classList.remove('active');
            }

            // Gửi lệnh Cho xe vào
            async function submitCheckIn(slotId, typeId) {
                const lp = document.getElementById('inLicensePlate').value.trim();
                if (!lp) {
                    alert('Vui lòng nhập biển số xe!');
                    return;
                }

                const params = new URLSearchParams();
                params.append('action', 'checkIn');
                params.append('slotId', slotId);
                params.append('typeId', typeId);
                params.append('licensePlate', lp);

                const res = await fetch('${pageContext.request.contextPath}/parking-map', {method: 'POST', body: params});
                const data = await res.json();
                if (data.status === 'success') {
                    window.location.reload();
                } else {
                    alert('Lỗi hệ thống!');
                }
            }

            // Gửi lệnh Cho xe ra
            async function submitCheckOut(slotId) {
                if (!confirm('Bạn có chắc chắn muốn cho xe này ra khỏi bãi?'))
                    return;
                const params = new URLSearchParams();
                params.append('action', 'checkOut');
                params.append('slotId', slotId);

                const res = await fetch('${pageContext.request.contextPath}/parking-map', {method: 'POST', body: params});
                const data = await res.json();
                if (data.status === 'success') {
                    window.location.reload();
                } else {
                    alert('Lỗi hệ thống!');
                }
            }

            // Gửi lệnh Đổi trạng thái Bảo trì
            async function submitToggleMaintenance(slotId, newStatus) {
                const msg = newStatus === 'Maintenance'
                        ? 'Bạn muốn tạm khóa ô đỗ này để bảo trì?'
                        : 'Bảo trì đã xong. Mở lại ô đỗ này để đón khách?';
                if (!confirm(msg))
                    return;

                const params = new URLSearchParams();
                params.append('action', 'toggleMaintenance');
                params.append('slotId', slotId);
                params.append('status', newStatus);

                const res = await fetch('${pageContext.request.contextPath}/parking-map', {method: 'POST', body: params});
                const data = await res.json();
                if (data.status === 'success') {
                    window.location.reload();
                } else {
                    alert('Lỗi hệ thống!');
                }
            }

            // ==========================================
            // AUTO-FORMAT BIỂN SỐ XE KHI ĐANG GÕ
            // ==========================================
            document.addEventListener('input', function (e) {
                // Chỉ bắt sự kiện gõ phím trên ô nhập biển số
                if (e.target && e.target.id === 'inLicensePlate') {

                    // 1. Viết hoa toàn bộ và xóa sạch mọi khoảng trắng, dấu phẩy, dấu chấm...
                    let val = e.target.value.toUpperCase();
                    let clean = val.replace(/[^A-Z0-9]/g, '');
                    let formatted = clean;

                    // 2. Chỉ bắt đầu định dạng khi gõ được từ 7 ký tự trở lên (độ dài tối thiểu của biển số)
                    if (clean.length >= 7) {
                        let tail5 = clean.slice(-5); // Lấy 5 ký tự cuối
                        let tail4 = clean.slice(-4); // Lấy 4 ký tự cuối

                        // Nếu 5 ký tự cuối đều là SỐ -> Format kiểu biển 5 số (VD: 30A-570.37)
                        if (/^\d{5}$/.test(tail5)) {
                            let prefix = clean.slice(0, -5);
                            formatted = prefix + '-' + tail5.slice(0, 3) + '.' + tail5.slice(3);
                        }
                        // Nếu 4 ký tự cuối đều là SỐ -> Format kiểu biển 4 số (VD: 29H1-1234)
                        else if (/^\d{4}$/.test(tail4)) {
                            let prefix = clean.slice(0, -4);
                            formatted = prefix + '-' + tail4;
                        }
                    }

                    // 3. Hiển thị lại lên ô nhập liệu cho người dùng thấy
                    if (e.target.value !== formatted) {
                        e.target.value = formatted;
                    }
                }
            });
        </script>
    </body>
</html>
