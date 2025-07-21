(() => {
    //const clinic_account = sessionStorage.getItem("clinic_account");
    let clinic_account = sessionStorage.getItem("account");
    if (!clinic_account) {
        alert("尚未登入或診所帳號遺失");
        location.href = "/ires-system/Clinic/login.html";
        return;
    }

    fetch("/ires-system/clinic/clinicNotification/Search", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ clinic_account })
    })
        .then(res => {
            if (!res.ok) throw new Error("伺服器錯誤");
            return res.json();
        })
        .then(data => {
            const listContainer = document.querySelector(".notification-list");
            if (!listContainer) {
                console.error("找不到 .notification-list 元素");
                return;
            }

            listContainer.innerHTML = "";

            if (!Array.isArray(data) || data.length === 0) {
                listContainer.innerHTML = "<p style='padding:10px;'>目前無通知</p>";
                return;
            }

            data.forEach(row => {
                if (!Array.isArray(row) || row.length < 6) return;

                const [appointmentId, patientName, message, type, read, date] = row;

                const item = document.createElement("div");
                item.classList.add("notification-item");

                // 計算 Ｎ天前
                const dateObj = new Date(date.replace(/-/g, '/')); // 日期字串處理
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                dateObj.setHours(0, 0, 0, 0);

                const diffTime = today - dateObj;
                const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
                const dateLabel = isNaN(diffDays) ? date : `${date}（${diffDays}天前）`;

                item.innerHTML = `
                    <div class="notification-header">
                        <span class="notification-type">${getTypeText(type)}</span>
                        <div class="notification-date-wrapper">
                            <span class="notification-date">${dateLabel}</span>
                            <button class="notification-close-btn" title="移除">×</button>
                        </div>
                    </div>
                    <div class="notification-content">
                        ${message}
                    </div>
                `;
                // 點擊叉叉就移除這筆通知
                item.querySelector(".notification-close-btn").addEventListener("click", () => {
                    itemRead(appointmentId);
                    item.remove();
                });

                listContainer.appendChild(item);
            });
        })
        .catch(err => {
            console.error("載入通知失敗：", err);
            alert("載入通知失敗，請稍後再試");
        });


    function getTypeText(type) {
        switch (type) {
            case "預約成功通知": return "📅 預約通知";
            case "報到成功通知": return "✅ 報到通知";
            case "系統通知": return "⚠️ 系統通知";
            case "feedback": return "💬 評價通知";
            default: return "📬 一般通知";
        }
    }

    function itemRead(appointmentId) {
        fetch('/ires-system/clinic/clinicNotification/updateReadStatus', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                appointment_id: appointmentId // ✅ 注意這邊 key 要小寫的 _id
            }),
        })
            .then(resp => resp.text())
            .then(message => {
                if (message.includes('1')) {
                    // location.href = "/ires-system/Clinic/Search.html";
                } else {
                    console.error("更新 read 狀態失敗");
                }
            })
            .catch(err => {
                msg.className = 'error';
                msg.textContent = '發生錯誤，請稍後再試';
                console.error('fetch error:', err);
            });

    }
})();


