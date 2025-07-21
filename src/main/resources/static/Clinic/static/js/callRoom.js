const url = `ws://${location.host}${location.pathname.substring(0, location.pathname.lastIndexOf('/') + 1)}call`;
console.log(url);
let socket;
socket = new WebSocket(url);

socket.onopen = () => {
    console.log("WebSocket 已連線至硬體設備");
    socket.send("0");
};
socket.onerror = (e) => console.error("WebSocket 錯誤", e);
socket.onclose = () => console.warn("WebSocket 已關閉");

function getStatusClass(status) {
    switch (status) {
        case '已報到': return 'status-arrived';
        case '未報到': return 'status-not-arrived';
        case '已取消': return 'status-cancelled';
        case '已完成': return 'status-finished';
        default: return '';
    }
}

function updateButtonStates(status) {
    const disabled = (status === 0); // 尚未開診時 disable
    document.getElementById("prevBtn").disabled = disabled;
    document.getElementById("nextBtn").disabled = disabled;
    document.getElementById("insertBtn").disabled = disabled;
    document.getElementById("completeBtn").disabled = disabled;
}

function updateUpcomingList(currentNumber) {
    const room = JSON.parse(localStorage.getItem("callRoom"));
    const remaining = room.patients
        .filter(p => p.status === "未報到" && parseInt(p.number) > parseInt(currentNumber))
        .slice(0, 5);

    const upcomingList = document.getElementById("upcomingList");
    upcomingList.innerHTML = remaining.length
        ? remaining.map(p => `<li class="list-group-item">${p.number} - ${p.name}</li>`).join("")
        : `<li class="list-group-item text-muted">無將來號碼</li>`;
}

function sendNumberToDevice(number, retryCount = 0) {
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(number);
        console.log("傳送叫號：", number);
    } else if (socket.readyState === WebSocket.CONNECTING && retryCount < 5) {
        console.warn("WebSocket 連線中，等待 300ms 重送");
        setTimeout(() => sendNumberToDevice(number, retryCount + 1), 300);
    } else {
        console.error("WebSocket 無法使用，readyState=", socket.readyState);
    }
}

function renderRoomData(highlightNumber = null) {
    const data = localStorage.getItem("callRoom");
    const container = document.getElementById("roomCardContainer");

    if (!data) {
        container.innerHTML = "<p class='text-danger'>找不到診間資料</p>";
        return;
    }

    container.innerHTML = "";

    const room = JSON.parse(data);

    const card = document.createElement("div");
    card.className = "card shadow-sm full-height-card";
    card.innerHTML = `
    <div class="card-header bg-info text-white">
      醫師：${room.doctor}（共 ${room.patients.length} 位病人）
    </div>
    <div class="card-body p-3">
      <div class="patient-table-scroll">
        <table class="table table-bordered table-sm">
          <thead class="table-light">
            <tr><th>號碼</th><th>姓名</th><th>狀態</th></tr>
          </thead>
          <tbody>
            ${room.patients.map(p => `
              <tr data-number="${p.number}">
                <td>${p.number}</td>
                <td>${p.name}</td>
                <td><span class="${getStatusClass(p.status)}">${p.status}</span></td>
              </tr>
            `).join("")}
          </tbody>
        </table>
      </div>
    </div>
  `;

    container.appendChild(card);

    const notArrived = room.patients.filter(p => p.status === '未報到');

    let current = highlightNumber
        ? room.patients.find(p => p.number === highlightNumber.toString())
        : notArrived[0];

    if (current) {
        const currentNumberEl = document.getElementById("currentNumber");
        currentNumberEl.innerText = `號碼：${current.number}`;
        currentNumberEl.dataset.number = current.number;
        document.getElementById("currentName").innerText = `姓名：${current.name}`;

        const row = document.querySelector(`tr[data-number='${current.number}']`);
        if (row) row.classList.add("selected-row");
    }

    const upcoming = notArrived
        .filter(p => !highlightNumber || p.number !== highlightNumber.toString())
        .slice(1, 6);

    const upcomingList = document.getElementById("upcomingList");
    upcomingList.innerHTML = upcoming.length
        ? upcoming.map(p => `<li class="list-group-item">${p.number} - ${p.name}</li>`).join("")
        : `<li class="list-group-item text-muted">無將來號碼</li>`;
}

function getSelectedConsultationStatus() {
    const selected = document.querySelector('input[name="statusOption"]:checked');
    return selected ? parseInt(selected.value) : null;
}

function getCurrentNumber() {
    const el = document.getElementById("currentNumber");
    if (!el) {
        console.warn("找不到 #currentNumber 元素");
        return null;
    }

    const raw = el.dataset.number;
    const number = parseInt(raw);

    if (isNaN(number)) {
        console.warn("解析 data-number 失敗", raw);
        return null;
    }

    return number;
}

function updateCallNumberOnServer(number, consultationStatus) {
    const room = JSON.parse(localStorage.getItem("callRoom"));
    if (!room) {
        console.warn("未找到 callRoom 設定");
        return;
    }

    const query = new URLSearchParams({
        doctorId: room.doctorId,
        timePeriod: room.timePeriod,
        date: room.date,
        number: number,
        consultationStatus: consultationStatus
    });

    fetch(`/ires-system/callNumber/init?${query.toString()}`, {
        method: 'GET'
    })
        .then(res => res.json())
        .then(data => console.log("已同步更新號碼與看診狀態：", data))
        .catch(err => console.error("後端更新失敗", err));
}

function insertNumber() {
    const input = document.getElementById("insertNumber");
    const value = input.value.trim();

    if (!value || isNaN(value) || parseInt(value) <= 0) {
        alert("請輸入有效的號碼");
        return;
    }

    const number = parseInt(value);
    const formattedNumber = number.toString();
    const room = JSON.parse(localStorage.getItem("callRoom"));

    const existing = room.patients.find(p => p.number === formattedNumber);
    if (!existing) {
        alert("此號碼不存在，無法插號！");
        return;
    }

    input.value = "";
    const modal = bootstrap.Modal.getInstance(document.getElementById('insertModal'));
    modal.hide();

    highlightRow(formattedNumber);
    const status = getSelectedConsultationStatus();
    updateCallNumberOnServer(formattedNumber, status);
}

function prevNumber() {
    const room = JSON.parse(localStorage.getItem("callRoom"));
    const eligible = room.patients
        .filter(p => p.status !== "已取消" && p.status !== "已完成")
        .sort((a, b) => parseInt(a.number) - parseInt(b.number));

    const currentText = document.getElementById("currentNumber").innerText.replace("號碼：", "").trim();
    const currentIndex = eligible.findIndex(p => p.number === currentText);

    if (currentIndex > 0) {
        const prev = eligible[currentIndex - 1].number;
        highlightRow(prev);
        const status = getSelectedConsultationStatus();
        updateCallNumberOnServer(prev, status);
    }
}

function nextNumber() {
    const room = JSON.parse(localStorage.getItem("callRoom"));
    const currentNumber = getCurrentNumber();
    if (!room || !currentNumber) return null;

    const eligible = room.patients
        .filter(p => p.status !== "已取消" && p.status !== "已完成")
        .sort((a, b) => parseInt(a.number) - parseInt(b.number));

    const currentIndex = eligible.findIndex(p => p.number === currentNumber.toString());

    if (currentIndex < eligible.length - 1) {
        const next = eligible[currentIndex + 1].number;
        highlightRow(next);

        const status = getSelectedConsultationStatus();
        updateCallNumberOnServer(next, status);

        return next;
    }

    return null; // 沒有下一號
}

function completeConsultation() {
    const number = getCurrentNumber();
    const room = JSON.parse(localStorage.getItem("callRoom"));

    if (!room || !number) {
        alert("無法取得目前叫號資訊");
        return;
    }

    const current = room.patients.find(p => p.number === number.toString());
    if (!current || !current.appointmentId) {
        alert("找不到該病患的 appointmentId");
        return;
    }

    const payload = {
        appointmentId: current.appointmentId,
        status: 3,
        doctorId: room.doctorId,
        timePeriod: room.timePeriod,
        appointmentDate: room.date
    };

    fetch("/ires-system/appointment/update", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("HTTP Error " + res.status);
            return res.json();
        })
        .then(data => {
            console.log("完成看診成功：", data);

            const idx = room.patients.findIndex(p => p.number === number.toString());
            if (idx !== -1) {
                room.patients[idx].status = "已完成";
            }

            localStorage.setItem("callRoom", JSON.stringify(room));

            const eligible = room.patients.filter(p => p.status === "未報到");
            const currentIndex = eligible.findIndex(p => p.number === number.toString());
            const nextPatient = eligible[currentIndex + 1];
            const nextNumber = nextPatient ? nextPatient.number : null;

            renderRoomData();

            if (nextNumber) {
                highlightRow(nextNumber);
                const status = getSelectedConsultationStatus();
                updateCallNumberOnServer(nextNumber, status);
            } else {
                document.getElementById("currentNumber").innerText = "號碼：";
                document.getElementById("currentName").innerText = "姓名：";
                updateUpcomingList(9999); // 清空 upcomingList
            }
        })
        .catch(err => {
            console.error("完成看診失敗：", err);
            alert("完成看診失敗");
        });
}

function highlightRow(number) {
    const numStr = number.toString();

    document.querySelectorAll("tr[data-number]").forEach(row => {
        row.querySelectorAll("td").forEach(td => td.classList.remove("selected-row"));
    });

    const row = document.querySelector(`tr[data-number='${numStr}']`);
    if (row) {
        row.querySelectorAll("td").forEach(td => td.classList.add("selected-row"));
    } else {
        console.warn("找不到符合號碼的 row：", numStr);
    }

    const room = JSON.parse(localStorage.getItem("callRoom"));
    const current = room.patients.find(p => p.number === numStr);
    if (current) {
        const currentNumberEl = document.getElementById("currentNumber");
        currentNumberEl.innerText = `號碼：${current.number}`;
        currentNumberEl.dataset.number = current.number;
        document.getElementById("currentName").innerText = `姓名：${current.name}`;
        sendNumberToDevice(current.number);
        updateUpcomingList(current.number);
    }
}

// 執行初始化邏輯
window.addEventListener("DOMContentLoaded", () => {
    renderRoomData();

    const room = JSON.parse(localStorage.getItem("callRoom"));
    if (room) {
        console.log("doctorId:", room.doctorId);
        console.log("timePeriod:", room.timePeriod);
        console.log("date:", room.date);

        fetch(`/ires-system/callNumber/init?doctorId=${room.doctorId}&timePeriod=${room.timePeriod}&date=${room.date}`, {
            method: 'GET'
        })
            .then(res => res.json())
            .then(data => {
                console.log("診間叫號初始化成功", data);

                if (data.consultationStatus === 1) {
                    if (data.number) {
                        highlightRow(data.number); // 看診中時 highlight 初始號碼
                    } else {
                        const roomData = JSON.parse(localStorage.getItem("callRoom"));
                        const first = roomData?.patients?.find(p => p.status === "未報到");
                        if (first) highlightRow(first.number);
                    }
                }
                if (typeof data.consultationStatus === "number") {
                    updateButtonStates(data.consultationStatus);
                } else {
                    console.warn("未提供 consultationStatus，按鈕狀態未更新");
                }

                switch (data.consultationStatus) {
                    case 0: document.getElementById("status1").checked = true; break;
                    case 1: document.getElementById("status2").checked = true; break;
                    case 2: document.getElementById("status3").checked = true; break;
                }

            })
            .catch(err => console.error("診間叫號初始化失敗", err));
    }

    const radios = document.querySelectorAll('input[name="statusOption"]');

    radios.forEach(radio => {
        radio.addEventListener("change", () => {
            const status = parseInt(radio.value);
            updateButtonStates(status);

            if (status === 1) {
                const roomData = JSON.parse(localStorage.getItem("callRoom"));
                const first = roomData?.patients?.find(p =>
                    p.status === "未報到" || p.status === "已報到"
                );
                if (first) highlightRow(first.number);
            }

            const number = getCurrentNumber();
            if (!isNaN(number) && !isNaN(status)) {
                updateCallNumberOnServer(number, status);
            }
        });
    });


    setTimeout(() => {
        const leftCard = document.querySelector("#roomCardContainer .card");
        const rightCol = document.querySelector(".col-md-5");
        if (leftCard && rightCol) {
            leftCard.style.height = rightCol.offsetHeight + "px";
        }
    }, 200);

    const initRadio = document.querySelector('input[name="statusOption"]:checked');
    if (initRadio) {
        updateButtonStates(parseInt(initRadio.value));
    }
});