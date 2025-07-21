// 移動報到成功的卡片到最上方
function moveCheckedInCardToTop() {
  const justId = localStorage.getItem("justCheckedInId");
  if (!justId) return;

  const list = document.querySelector("#appointmentList");
  const target = list?.querySelector(`.appointment[data-id="${justId}"]`);
  if (target) {
    list.prepend(target);
    target.classList.add("highlight");
    target.scrollIntoView({ behavior: "smooth", block: "start" }); // 可選：捲動到上方
  }

  localStorage.removeItem("justCheckedInId");
}
// 顯示來自預約表的資料
const appointmentsList = document.getElementById("appointmentList");
const template = document.getElementById("appointmentTemplate");
const modal = document.getElementById("editAppointment");
const form = document.getElementById("editForm");
const doctorSelect = document.getElementById("doctorSelect");
const doctorLabel = document.getElementById("doctorLabel");
const timePeriodText = { 1: "上午時段", 2: "下午時段", 3: "晚上時段" };
const checkInStatus = { 0: "未報到", 1: "已報到", 2: "取消報到" };
const formatDate = date => new Date(date).toLocaleDateString("zh-TW");
const getCutoffHour = period => ({ 1: 12, 2: 18, 3: 23 }[period] || 0);
const showError = (title, err) => {
  console.error(`${title}：`, err);
  alert(`${title}，請稍後再試`);
};
const getAppointmentElementById = id =>
  document.querySelector(`.appointment[data-id="${id}"]`);

// 載入可預約醫師
function loadDoctors(clinicId, date, timePeriod, selectedDoctorId = null) {
  fetch(`/ires-system/editAppointment/available?clinicId=${clinicId}&date=${date}&timePeriod=${timePeriod}`)
    .then(r => r.json())
    .then(res => {
      doctorSelect.innerHTML = "";
      (Array.isArray(res.data) ? res.data : []).forEach(doc => {
        const opt = document.createElement("option");
        opt.value = doc.doctorId;
        opt.textContent = doc.doctorName;
        if (doc.doctorId === selectedDoctorId) opt.selected = true;
        doctorSelect.appendChild(opt);
      });
    })
    .catch(err => showError("載入醫師清單失敗", err));
}

// 載入預約資料清單
function renderAppointments() {
  fetch('/ires-system/reservation', { method: 'GET', credentials: 'include' })
    .then(res => res.json())
    .then(data => {
      const now = new Date();

      data.sort((a, b) => {
        const getScore = appt => {
          const apptDate = new Date(appt.appointmentDate);
          const cutoffHour = getCutoffHour(appt.timePeriod);
          apptDate.setHours(cutoffHour, 0, 0, 0);

          const isToday = now.toDateString() === apptDate.toDateString();
          const isFuture = now < apptDate;
          const notCheckedIn = appt.status === 0;

          if (isToday && notCheckedIn && isFuture) return 3;
          if (appt.status === 1) return 2;
          return 1;
        };

        const scoreDiff = getScore(b) - getScore(a);
        if (scoreDiff !== 0) return scoreDiff;

        return b.appointmentDate - a.appointmentDate;
      });

      const nowTime = new Date();

      data.forEach(appt => {
        if (!appt.appointmentId) return;

        const item = template.content.cloneNode(true);
        const el = item.querySelector(".appointment");
        el.dataset.id = appt.appointmentId;
        el.dataset.clinicId = appt.clinic.clinicId;
        item.querySelector(".clinic").textContent = appt.clinic.clinicName;
        item.querySelector(".time").textContent =
          `預約時間: ${formatDate(appt.appointmentDate)} ${timePeriodText[appt.timePeriod]}`;
        item.querySelector(".reserveNo").textContent = `看診號碼: ${appt.reserveNo}`;
        item.querySelector(".status").textContent = `狀態: ${checkInStatus[appt.status]}`;

        const actions = item.querySelector(".actions");
        actions.innerHTML = "";

        const futureTime = new Date(appt.appointmentDate);
        futureTime.setHours(getCutoffHour(appt.timePeriod), 0, 0, 0);

        appointmentsList.appendChild(item);

        const showButtons = () => {
          if (nowTime < futureTime && appt.status !== 1) {
            const btnEdit = document.createElement("button");
            btnEdit.className = "edit";
            btnEdit.textContent = "修改預約";
            btnEdit.dataset.id = appt.appointmentId;
            actions.appendChild(btnEdit);
          }

          if (!favoritedClinics.has(appt.clinic.clinicId)) {
            const btnFav = document.createElement("button");
            btnFav.className = "favorite";
            btnFav.textContent = "加入收藏";
            actions.appendChild(btnFav);
          }

          if (appt.status === 0 && nowTime < futureTime) {
            const btnCheckIn = document.createElement("button");
            btnCheckIn.className = "checkIn";
            btnCheckIn.textContent = "報到";
            actions.appendChild(btnCheckIn);
          }
        };

        showButtons();
      });

      moveCheckedInCardToTop();
    });
}

const favoritedClinics = new Set();

fetch('/ires-system/favorites/all', { credentials: 'include' })
  .then(res => {
    const contentType = res.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return res.json();
    } else {
      throw new Error("伺服器未回傳 JSON，實際回傳為：" + contentType);
    }
  })
  .then(data => {
    (Array.isArray(data) ? data : []).forEach(fav => favoritedClinics.add(fav.clinicId));
    renderAppointments();
  })
  .catch(err => {
    console.error("取得收藏清單失敗", err);
    renderAppointments();
  });

// 開啟/關閉小彈窗 + 載入預約資訊
document.addEventListener("click", e => {
  const editBtn = e.target.closest(".edit");
  if (editBtn) {
    const id = editBtn.dataset?.id?.trim();
    if (!id || id === "undefined") {
      alert("找不到預約 ID！");
      return;
    }

    form.dataset.id = id;

    fetch(`/ires-system/editAppointment/${id}`)
      .then(res => res.ok ? res.json() : res.text().then(t => { throw new Error(t); }))
      .then(d => {
        if (!d?.appointmentDate) return alert("預約資料格式錯誤");

        form.date.value = new Date(d.appointmentDate).toISOString().split("T")[0];
        form.timePeriod.value = d.timePeriod;

        // 使用從後端傳來的醫生資料更新下拉選單
        doctorSelect.innerHTML = "";
        (Array.isArray(d.doctorList) ? d.doctorList : []).forEach(doc => {
          const opt = document.createElement("option");
          opt.value = doc.doctorId;
          opt.textContent = doc.doctorName;
          if (doc.doctorId === d.doctorId) opt.selected = true;
          doctorSelect.appendChild(opt);
        });

        if (doctorLabel) doctorLabel.textContent = d.doctorName;
        modal.hidden = false;
      })
      .catch(err => showError("載入預約資料失敗", err));
  }

  if (e.target.classList.contains("closeBtn")) modal.hidden = true;
});

// 表單送出 - 更新預約
form.addEventListener("submit", e => {
  e.preventDefault();

  const body = JSON.stringify({
    appointmentId: form.dataset.id,
    appointmentDate: form.date.value,
    timePeriod: form.timePeriod.value,
    doctorId: doctorSelect.value
  });

  fetch("/ires-system/editAppointment/update", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body
  })
    .then(res => res.json())
    .then(d => {
      alert(d.message || "更新成功");
      modal.hidden = true;

      const id = form.dataset.id;
      const apptEl = getAppointmentElementById(id);
      if (apptEl) {
        const period = (timePeriodText[form.timePeriod.value] || "").replace("時段", "");
        const doctor = doctorSelect.options[doctorSelect.selectedIndex]?.text || "";
        apptEl.querySelector(".time").textContent = `預約時間: ${form.date.value} ${period} ${doctor}`;
        apptEl.querySelector(".status").textContent = `狀態: ${checkInStatus[d.status] || "未知"}`;
      }
      localStorage.setItem("justCheckedInId", form.dataset.id);
      location.reload();
    })

    .catch(err => showError("更新失敗", err));
});

/*加入收藏*/
document.addEventListener("click", e => {
  const favBtn = e.target.closest(".favorite");
  if (favBtn) {
    const apptEl = favBtn.closest(".appointment");
    const clinicId = apptEl?.dataset?.clinicId || apptEl?.querySelector(".clinicId")?.textContent;
    console.log("送出的 clinicId = ", clinicId);
    fetch("/ires-system/favorites/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ clinicId })

    })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          alert("已加入收藏");
          favoritedClinics.add(clinicId);
          favBtn.remove(); // 或 favBtn.hidden = true;

          // 若 favorites 頁面有掛載，可直接新增
          const favoritesList = document.getElementById("favoritesList");
          if (favoritesList) {
            const card = apptEl.cloneNode(true);
            card.classList.add("favoriteCard");
            favoritesList.appendChild(card);
          }

          // optional: localStorage 通知其他頁面
          localStorage.setItem("favoritesUpdated", Date.now());
        } else {
          alert(data.message || "收藏失敗");
        }
      })
      .catch(err => {
        console.error("加入收藏時發生錯誤", err);
        alert("收藏失敗，請稍後再試");
      });
  }
});