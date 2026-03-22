<%-- 
    Document   : header
    Created on : Mar 15, 2026, 6:37:13 PM
    Author     : myniy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<header class="top-header">
    <div class="greeting">
        <h1>${param.title}</h1>
        <p>${param.subtitle}</p>
    </div>

    <div class="header-actions">
        <label class="theme-switch" for="checkbox" title="Đổi giao diện">
            <input type="checkbox" id="checkbox" />
            <div class="slider">
                <i class="fa-solid fa-moon"></i>
                <i class="fa-solid fa-sun"></i>
            </div>
        </label>
    </div>
</header>

<script>
    const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');

    function syncTheme() {
        const currentTheme = localStorage.getItem('theme');
        if (currentTheme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
            if (toggleSwitch)
                toggleSwitch.checked = true;
        } else {
            document.documentElement.removeAttribute('data-theme');
            if (toggleSwitch)
                toggleSwitch.checked = false;
        }
    }

    syncTheme();

    window.addEventListener('pageshow', function (event) {
        syncTheme();
    });

    if (toggleSwitch) {
        toggleSwitch.addEventListener('change', function (e) {
            if (e.target.checked) {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            } else {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('theme', 'light');
            }
        });
    }
</script>

<%-- Logic hiển thị Popup thông báo xác minh Email --%>
<% 
    if (session.getAttribute("showEmailWarning") != null && (Boolean) session.getAttribute("showEmailWarning")) { 
%>
<style>
    /* Nhúng CSS trực tiếp vào đây để chống Cache tuyệt đối */
    .apple-notification {
        position: fixed;
        top: -120px; /* Ẩn phía trên màn hình ban đầu */
        left: 50%;
        transform: translateX(-50%);
        width: 90%;
        max-width: 420px;
        background: rgba(255, 255, 255, 0.85);
        backdrop-filter: blur(20px);
        -webkit-backdrop-filter: blur(20px);
        border: 1px solid rgba(0, 0, 0, 0.05);
        border-radius: 24px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        padding: 16px;
        gap: 16px;
        z-index: 10000;
        transition: top 0.6s cubic-bezier(0.16, 1, 0.3, 1);
    }

    [data-theme="dark"] .apple-notification {
        background: rgba(28, 28, 30, 0.85);
        border: 1px solid rgba(255, 255, 255, 0.1);
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
    }

    .notif-icon {
        width: 44px;
        height: 44px;
        background: rgba(255, 149, 0, 0.1);
        color: #ff9500;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 20px;
        flex-shrink: 0;
    }

    .notif-content {
        flex: 1;
    }
    .notif-content h4 {
        margin: 0 0 4px 0;
        font-size: 15px;
        font-weight: 600;
        color: var(--apple-text-dark);
    }
    .notif-content p {
        margin: 0;
        font-size: 13px;
        color: var(--apple-text-light);
        line-height: 1.4;
    }

    .notif-close {
        background: none;
        border: none;
        color: var(--apple-text-light);
        font-size: 20px;
        cursor: pointer;
        padding: 8px;
        transition: color 0.2s;
    }
    .notif-close:hover {
        color: var(--apple-text-dark);
    }
    .apple-notification.show {
        top: 24px;
    }
</style>

<div id="emailVerificationNotification" class="apple-notification">
    <div class="notif-icon">
        <i class="fa-solid fa-envelope-open-text"></i>
    </div>
    <div class="notif-content">
        <h4>Xác minh tài khoản</h4>
        <p>Email của bạn chưa được xác minh. Vui lòng kiểm tra hộp thư để đảm bảo an toàn cho tài khoản.</p>
    </div>
    <button class="notif-close" onclick="closeEmailNotification()">
        <i class="fa-solid fa-xmark"></i>
    </button>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        setTimeout(() => {
            document.getElementById('emailVerificationNotification').classList.add('show');
        }, 500);

        setTimeout(() => {
            closeEmailNotification();
        }, 6500);
    });

    function closeEmailNotification() {
        const notif = document.getElementById('emailVerificationNotification');
        if (notif) {
            notif.classList.remove('show');
        }
    }
</script>
<% 
        session.removeAttribute("showEmailWarning");
    } 
%>