<%-- 
    Document   : login
    Created on : Mar 14, 2026, 5:27:32 PM
    Author     : ZunzZunn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng nhập - iParking</title>
        <link rel="stylesheet" href="css/login.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <div class="split-screen-container">
            <div class="left-panel">
                <div class="left-panel-bg"></div>

                <div class="intro-content">
                    <div class="intro-logo">
                        <i class="fa-solid fa-square-parking"></i> iParking
                    </div>
                    <h1>Hệ thống quản lý bãi xe thông minh</h1>
                    <p>Giải pháp tối ưu vận hành, kiểm soát doanh thu chặt chẽ và mang lại trải nghiệm đỗ xe hiện đại, an toàn.</p>
                </div>
            </div>

            <div class="right-panel">
                <div class="login-form-wrapper">
                    <div class="logo-area icon-parking-style">
                        <i class="fa-solid fa-square-parking"></i>
                        <h2>Đăng nhập hệ thống</h2>
                        <p>Chào mừng trở lại! Vui lòng nhập thông tin.</p>
                    </div>

                    <form action="login" method="POST">
                        <div class="form-group">
                            <label for="username">Tài khoản</label>
                            <input type="text" id="username" name="username" placeholder="Nhập tên đăng nhập" required autocomplete="off">
                        </div>

                        <div class="form-group">
                            <label for="password">Mật khẩu</label>
                            <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>
                        </div>                      

                        <button type="submit" class="submit-btn">Đăng nhập</button>
                    </form>

                    <a href="#" id="openForgotModal" class="forgot-link">Quên mật khẩu?</a>

                    <% if(request.getAttribute("error") != null) { %>
                    <div class="error-message">
                        <i class="fa-solid fa-circle-exclamation"></i>
                        <%= request.getAttribute("error") %>
                    </div>
                    <% } %>

                    <div class="footer-text">
                        iParking Management System &copy; 2026
                    </div>
                </div>
            </div>
        </div>
        <div class="modal-overlay" id="forgotModal">
            <div class="modal-content">
                <button class="btn-close" id="closeForgotModal"><i class="fa-solid fa-xmark"></i></button>
                <div class="modal-header">
                    <h2>Khôi phục mật khẩu</h2>
                    <p id="forgotDesc">Nhập Tên đăng nhập và Email của bạn để nhận mã xác nhận.</p>
                </div>

                <div id="forgotMsg" style="display:none; color: #ff3b30; font-size: 14px; margin-bottom: 16px;"></div>

                <div id="step1">
                    <div class="form-group">
                        <label>Tên đăng nhập</label>
                        <input type="text" id="fgUsername" placeholder="Nhập tên đăng nhập...">
                    </div>
                    <div class="form-group">
                        <label>Email đăng ký</label>
                        <input type="email" id="fgEmail" placeholder="Nhập email...">
                    </div>
                    <button type="button" class="submit-btn" id="btnSendOTP">Gửi mã OTP</button>
                </div>

                <div id="step2" style="display: none;">
                    <div class="form-group">
                        <label>Mã OTP (6 số)</label>
                        <input type="text" id="fgOTP" placeholder="Nhập mã từ email...">
                    </div>
                    <div class="form-group">
                        <label>Mật khẩu mới</label>
                        <input type="password" id="fgNewPass" placeholder="Nhập mật khẩu mới...">
                    </div>
                    <button type="button" class="submit-btn" id="btnResetPass">Xác nhận Đổi mật khẩu</button>
                </div>
            </div>
        </div>

        <script>
            const modal = document.getElementById('forgotModal');
            const msgDiv = document.getElementById('forgotMsg');

            document.getElementById('openForgotModal').addEventListener('click', (e) => {
                e.preventDefault();
                modal.classList.add('active');
            });

            document.getElementById('closeForgotModal').addEventListener('click', () => {
                modal.classList.remove('active');
            });

            // Xử lý gửi OTP
            document.getElementById('btnSendOTP').addEventListener('click', async () => {
                const btn = document.getElementById('btnSendOTP');
                const username = document.getElementById('fgUsername').value;
                const email = document.getElementById('fgEmail').value;

                if (!username || !email) {
                    msgDiv.style.display = 'block';
                    msgDiv.innerText = "Vui lòng nhập đủ thông tin!";
                    return;
                }

                btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Đang gửi...';
                btn.disabled = true;

                const params = new URLSearchParams();
                params.append('action', 'sendOTP');
                params.append('username', username);
                params.append('email', email);

                const res = await fetch('${pageContext.request.contextPath}/forgot-password', {
                    method: 'POST', body: params
                });
                const text = await res.text();

                if (text === 'success') {
                    msgDiv.style.display = 'none';
                    document.getElementById('step1').style.display = 'none';
                    document.getElementById('step2').style.display = 'block';
                    document.getElementById('forgotDesc').innerText = "Vui lòng kiểm tra email và nhập mã OTP (có hiệu lực 5 phút).";
                } else {
                    msgDiv.style.display = 'block';
                    msgDiv.innerText = text;
                }
                btn.innerHTML = 'Gửi mã OTP';
                btn.disabled = false;
            });

            // Xử lý đổi mật khẩu
            document.getElementById('btnResetPass').addEventListener('click', async () => {
                const btn = document.getElementById('btnResetPass');
                const otp = document.getElementById('fgOTP').value;
                const newPass = document.getElementById('fgNewPass').value;

                if (!otp || !newPass) {
                    msgDiv.style.display = 'block';
                    msgDiv.innerText = "Vui lòng nhập đủ thông tin!";
                    return;
                }

                btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Đang xử lý...';
                btn.disabled = true;

                const params = new URLSearchParams();
                params.append('action', 'verifyAndReset');
                params.append('otp', otp);
                params.append('newPassword', newPass);

                const res = await fetch('${pageContext.request.contextPath}/forgot-password', {
                    method: 'POST', body: params
                });
                const text = await res.text();

                if (text === 'success') {
                    alert("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                    window.location.reload();
                } else {
                    msgDiv.style.display = 'block';
                    msgDiv.innerText = text;
                    btn.innerHTML = 'Xác nhận Đổi mật khẩu';
                    btn.disabled = false;
                }
            });
        </script>
    </body>
</html>
