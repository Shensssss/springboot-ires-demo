flatpickr("#multiDate", {
	mode: "multiple",
	dateFormat: "Y-m-d",
	locale: "zh"
});

function formatDateTime(value) {
	if (!value) return "";
	const dt = new Date(value);
	const pad = n => n.toString().padStart(2, "0");
	return `${dt.getFullYear()}-${pad(dt.getMonth() + 1)}-${pad(dt.getDate())} `
		+ `${pad(dt.getHours())}:${pad(dt.getMinutes())}:${pad(dt.getSeconds())}`;
}

function formatDate(input) {
	if (!input) return "";

	const date = typeof input === 'string' ? toLocalDateOnly(input) : new Date(input);
	return date.toLocaleDateString("zh-TW", {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit'
	});
}

function formatInputDate(input) {
	if (!input) return "";

	const date = typeof input === 'string' ? toLocalDateOnly(input) : new Date(input);
	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');
	return `${yyyy}-${mm}-${dd}`;
}

function toLocalDateOnly(str) {
	const [year, month, day] = str.split("-");
	return new Date(Number(year), Number(month) - 1, Number(day));
}

function renderAppointmentHistory(appointments) {
	const tbody = document.getElementById("appointmentList");
	tbody.innerHTML = "";

	const timeMap = { 1: "早上", 2: "下午", 3: "晚上" };
	const today = new Date();
	today.setHours(0, 0, 0, 0);

	appointments.forEach(app => {
		const tr = document.createElement("tr");
		tr.dataset.id = app.appointmentId;

		const appDate = toLocalDateOnly(formatInputDate(app.appointmentDate));
		const isPast = appDate < today;

		console.log(appDate);
		console.log(formatDate(app.appointmentDate));
		console.log(formatInputDate(app.appointmentDate));

		tr.innerHTML = `
			<td>
				<span class="view">${formatDate(app.appointmentDate)}</span>
				<input class="edit edit-date" type="date" value="${formatInputDate(app.appointmentDate)}" style="display:none">
			</td>
			<td><span class="view">${timeMap[app.timePeriod] || "未知"}</span>
				<select class="edit period-edit" style="display:none">
					<option ${app.timePeriod === 1 ? 'selected' : ''}>早上</option>
					<option ${app.timePeriod === 2 ? 'selected' : ''}>下午</option>
					<option ${app.timePeriod === 3 ? 'selected' : ''}>晚上</option>
				</select>
			</td>
			<td>
				<span class="view">${app.doctorName || ''}</span>
				<input 
					class="edit doctor-edit" 
					type="text" 
					value="${app.doctorName || ''}" 
					data-id="${app.doctorId}" 
					data-uid="${app.appointmentId}" 
					style="display:none" 
					readonly
				>
			</td>
			<td>${app.reserveNo || '-'}</td>
			<td>${
			app.status === 1 ? '已報到' :
				app.status === 2 ? '已取消' :
					app.status === 3 ? '已完成' :
						'未報到'
		}</td>
			<td class="created">${formatDateTime(app.createTime)}</td>
			<td class="modified">${formatDateTime(app.updateTime)}</td>
			<td>
				<button class="editBtn" ${isPast ? 'disabled' : ''}>修改</button>
				<button class="saveBtn" disabled>儲存</button>
				<button class="cancelBtn" disabled>取消</button>
				<button class="deleteBtn">刪除</button>
			</td>
		`;

		tbody.appendChild(tr);
	});
}

function renderPatients(page = 1, keyword = "") {
	const tbody = document.getElementById("patientTableBody");
	const pagination = document.getElementById("pagination");
	tbody.innerHTML = "";
	pagination.innerHTML = "";

	fetch(`/ires-system/patient/patientList?page=${page}&keyword=${encodeURIComponent(keyword)}`)
		.then(response => {
			if (!response.ok) {
				throw new Error("後端回應失敗");
			}
			return response.json();
		})
		.then(data => {
			const patients = data.patients || [];
			const totalPages = data.totalPages || 1;

			patients.forEach(p => {
				const tr = document.createElement("tr");
				tr.innerHTML = `<td>${p.phone}</td><td>${p.name}</td>`;
				tr.ondblclick = async () => {
					document.getElementById("patient").value = `${p.phone} ${p.name}`;
					document.getElementById("patientTableContainer").style.display = "none";
					window.selectedPhone = p.phone;

					try {
						// 從手機號查詢 patientId
						const res = await fetch(`/ires-system/patient/findByPhone?phone=${encodeURIComponent(p.phone)}`);
						if (!res.ok) throw new Error("查詢病患失敗");

						const patientData = await res.json();
						const patientId = patientData.patientId;

						// 查詢該病患歷史預約
						const historyRes = await fetch(`/ires-system/appointment/history?patientId=${patientId}`);
						if (!historyRes.ok) throw new Error("載入歷史預約失敗");

						const appointments = await historyRes.json();
						renderAppointmentHistory(appointments);
					} catch (err) {
						console.error("載入病患預約紀錄失敗：", err);
						alert("無法載入病患預約紀錄");
					}
				};

				tbody.appendChild(tr);
			});

			for (let i = 1; i <= totalPages; i++) {
				const btn = document.createElement("button");
				btn.textContent = i;
				btn.onclick = () => renderPatients(i, keyword);
				pagination.appendChild(btn);
			}
		})
		.catch(err => {
			console.error("病患資料載入失敗", err);
			alert("無法取得病患資料，請稍後再試");
		});
}

function renderDoctors(page = 1, keyword = "") {
	const pageSize = 10; // 可依後端預設調整

	fetch(`/ires-system/doctor/doctorList?page=${page}&keyword=${encodeURIComponent(keyword)}`)
		.then(response => {
			if (!response.ok) {
				throw new Error("後端回應失敗");
			}
			return response.json();
		})
		.then(data => {
			const doctors = data.doctors || [];
			const totalPages = data.totalPages || 1;

			const tbody = document.getElementById("doctorTableBody");
			const pagination = document.getElementById("doctorPagination");
			tbody.innerHTML = "";
			pagination.innerHTML = "";

			// 填入資料列
			doctors.forEach(doctor => {
				const tr = document.createElement("tr");
				tr.innerHTML = `<td>${doctor.id}</td><td>${doctor.name}</td>`;
				tr.ondblclick = () => {
					// 將選到的醫師寫入表單欄位
					document.getElementById("doctorInput").value = doctor.name;
					document.getElementById("doctorInput").dataset.id = doctor.id;
					document.getElementById("doctorTableContainer").style.display = "none";
				};
				tbody.appendChild(tr);
			});

			// 分頁按鈕
			for (let i = 1; i <= totalPages; i++) {
				const btn = document.createElement("button");
				btn.textContent = i;
				btn.onclick = () => renderDoctors(i, document.getElementById("doctorSearch").value);
				pagination.appendChild(btn);
			}
		})
		.catch(error => {
			console.error("取得醫師資料時發生錯誤：", error);
			alert("無法取得醫師資料，請稍後再試");
		});
}

function registerEventHandlers() {
	document.getElementById("patient").addEventListener("click", () => {
		document.getElementById("patientTableContainer").style.display = "block";
		document.getElementById("doctorTableContainer").style.display = "none";
		renderPatients();
	});

	document.getElementById("doctorInput").addEventListener("click", () => {
		document.getElementById("doctorTableContainer").style.display = "block";
		document.getElementById("patientTableContainer").style.display = "none";
		renderDoctors();
	});

	document.getElementById("patientSearch").addEventListener("input", () => {
		renderPatients(1, document.getElementById("patientSearch").value);
	});

	document.getElementById("doctorSearch").addEventListener("input", () => {
		renderDoctors(1, document.getElementById("doctorSearch").value);
	});

	document.addEventListener("mousedown", e => {
		const inside =
			document.getElementById("patient").contains(e.target) ||
			document.getElementById("patientTableContainer").contains(e.target) ||
			document.getElementById("doctorInput").contains(e.target) ||
			document.getElementById("doctorTableContainer").contains(e.target);
		if (!inside) {
			document.getElementById("patientTableContainer").style.display = "none";
			document.getElementById("doctorTableContainer").style.display = "none";
		}
	});

	document.getElementById("btnReserve").addEventListener("click", async () => {
		try {
			const patientInput = document.getElementById("patient").value.trim();
			const phone = patientInput.split(" ")[0];
			const doctorId = document.getElementById("doctorInput").dataset.id;
			const clinicId = document.getElementById("doctorInput").dataset.clinicId;
			const timeslot = document.getElementById("timeslot").value;
			const dates = document.getElementById("multiDate").value.split(", ");
			const timePeriodMap = { "早上": 1, "下午": 2, "晚上": 3 };
			const timePeriod = timePeriodMap[timeslot] || 1;

			// 查病患 ID
			const patientRes = await fetch(`/ires-system/patient/findByPhone?phone=${encodeURIComponent(phone)}`);
			if (!patientRes.ok) {
				alert("查無病患");
				return;
			}
			const patientData = await patientRes.json();
			const patientId = patientData.patientId;

			// 組多筆預約資料
			const payload = dates.map(date => ({
				patientId,
				doctorId,
				clinicId,
				appointmentDate: date,
				timePeriod
			}));

			// 傳送預約
			const reserveRes = await fetch("/ires-system/appointment/reserve", {
				method: "POST",
				headers: { "Content-Type": "application/json; charset=utf-8" },
				body: JSON.stringify(payload)
			});

			const resultText = await reserveRes.text();

			if (!reserveRes.ok) {
				alert(resultText);
				return;
			}

			alert(resultText);

			// 查歷史預約
			const historyRes = await fetch(`/ires-system/appointment/history?patientId=${patientId}`);
			if (!historyRes.ok) throw new Error("載入歷史預約失敗");

			const appointments = await historyRes.json();
			renderAppointmentHistory(appointments);

		} catch (err) {
			console.error("預約流程失敗：", err);
			alert("處理預約時發生錯誤，請稍後再試");
		}
	});

	document.getElementById("appointmentList").addEventListener("click", async (e) => {
		const tr = e.target.closest("tr");
		if (!tr) return;

		const appointmentId = tr.dataset.id; // 重點：appointmentId 來自 tr data-id
		const editBtn = tr.querySelector(".editBtn");
		const saveBtn = tr.querySelector(".saveBtn");
		const cancelBtn = tr.querySelector(".cancelBtn");
		const deleteBtn = tr.querySelector(".deleteBtn");

		if (e.target.classList.contains("editBtn")) {
			// const appointmentDate = new Date(tr.querySelector('input[type="date"]').value);
			const dateStr = tr.querySelector('input[type="date"]').value;
			console.log(dateStr);
			const appointmentDate = new Date(dateStr + 'T00:00:00+08:00');
			console.log(appointmentDate);

			const today = new Date();
			today.setHours(0, 0, 0, 0);

			if (appointmentDate < today) {
				alert("無法修改已過期的預約日期！");
				return;
			}

			tr.querySelectorAll(".view").forEach(el => el.style.display = "none");
			tr.querySelectorAll(".edit").forEach(el => el.style.display = "inline-block");
			editBtn.disabled = true;
			deleteBtn.disabled = true;
			saveBtn.disabled = false;
			cancelBtn.disabled = false;

			tr.dataset.originalDate = tr.querySelector('input[type="date"]').value;
			tr.dataset.originalTime = tr.querySelector('select').value;
			tr.dataset.originalDoctor = tr.querySelector('input[type="text"]').value;
		}

		if (e.target.classList.contains("saveBtn")) {
			const dateVal = tr.querySelector('input[type="date"]').value;
			const timeVal = tr.querySelector('select').value;

			const doctorInput = tr.querySelector('.doctor-edit');
			const doctorId = doctorInput.dataset.id;

			const timePeriodMap = { "早上": 1, "下午": 2, "晚上": 3 };
			const timePeriod = timePeriodMap[timeVal] || 1;

			const payload = {
				appointmentId,
				appointmentDate: dateVal,
				timePeriod,
				doctorId
			};

			const res = await fetch("/ires-system/appointment/update", {
				method: "PUT",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify(payload)
			});

			if (res.ok) {
				const data = await res.json();
				tr.querySelectorAll(".view")[0].textContent = dateVal;
				tr.querySelectorAll(".view")[1].textContent = timeVal;
				tr.querySelectorAll(".view")[2].textContent = doctorInput.value;
				tr.querySelector(".modified").textContent = formatDateTime(data.updateTime);


				try {
					// 從手機號查詢 patientId
					if (!window.selectedPhone) {
						alert("請先選擇病患");
						return;
					}
					const res = await fetch(`/ires-system/patient/findByPhone?phone=${encodeURIComponent(window.selectedPhone)}`);

					if (!res.ok) throw new Error("查詢病患失敗");

					const patientData = await res.json();
					const patientId = patientData.patientId;

					// 查詢該病患歷史預約
					const historyRes = await fetch(`/ires-system/appointment/history?patientId=${patientId}`);
					if (!historyRes.ok) throw new Error("載入歷史預約失敗");

					const appointments = await historyRes.json();
					renderAppointmentHistory(appointments);
				} catch (err) {
					console.error("載入病患預約紀錄失敗：", err);
					alert("無法載入病患預約紀錄");
				}
			} else {
				alert("儲存失敗");
				return;
			}

			tr.querySelectorAll(".view").forEach(el => el.style.display = "inline");
			tr.querySelectorAll(".edit").forEach(el => el.style.display = "none");
			saveBtn.disabled = true;
			cancelBtn.disabled = true;
			editBtn.disabled = false;
			deleteBtn.disabled = false;
		}

		if (e.target.classList.contains("cancelBtn")) {
			tr.querySelector('input[type="date"]').value = tr.dataset.originalDate;
			tr.querySelector('select').value = tr.dataset.originalTime;
			tr.querySelector('input[type="text"]').value = tr.dataset.originalDoctor;

			tr.querySelectorAll(".edit").forEach(el => el.style.display = "none");
			tr.querySelectorAll(".view").forEach(el => el.style.display = "inline");

			saveBtn.disabled = true;
			cancelBtn.disabled = true;
			editBtn.disabled = false;
			deleteBtn.disabled = false;
		}

		if (e.target.classList.contains("deleteBtn")) {
			if (!deleteBtn.disabled && confirm("確定要刪除這筆預約嗎？")) {
				const res = await fetch(`/ires-system/appointment/delete/${appointmentId}`, {
					method: "DELETE"
				});
				if (res.ok) {
					tr.remove();
				} else {
					alert("刪除失敗");
				}
			}
		}
	});

	document.addEventListener("click", (e) => {
		const popup = document.getElementById("doctorPopup");

		if (e.target.classList.contains("doctor-edit")) {
			const input = e.target;
			const uid = input.dataset.uid;
			const tbody = popup.querySelector(".popup-body");
			const search = popup.querySelector(".popup-search");
			const footer = popup.querySelector(".popup-footer");

			const rect = input.getBoundingClientRect();
			popup.style.left = rect.left + window.scrollX + "px";
			popup.style.top = rect.bottom + window.scrollY + "px";
			popup.style.width = input.offsetWidth + "px";  // ★ 讓 popup 寬度與 input 一樣
			popup.style.display = "block";

			popup.dataset.uid = uid;

			const render = (keyword = "", page = 1) => {
				const pageSize = 5;

				fetch(`/ires-system/doctor/doctorList?page=${page}&keyword=${encodeURIComponent(keyword)}`)
					.then(response => response.json())
					.then(data => {
						const doctors = data.doctors || [];
						const totalPages = data.totalPages || 1;

						tbody.innerHTML = "";
						footer.innerHTML = "";

						doctors.forEach(doctor => {
							const tr = document.createElement("tr");
							tr.innerHTML = `<td>${doctor.id}</td><td>${doctor.name}</td>`;
							tr.addEventListener("dblclick", () => {
								const targetInput = document.querySelector(`.doctor-edit[data-uid="${uid}"]`);
								if (targetInput) {
									targetInput.value = doctor.name;
									targetInput.dataset.id = doctor.id;
									popup.style.display = "none";
								}
							});
							tbody.appendChild(tr);
						});

						for (let i = 1; i <= totalPages; i++) {
							const btn = document.createElement("button");
							btn.textContent = i;
							btn.onclick = () => render(keyword, i);
							footer.appendChild(btn);
						}
					})
					.catch(error => {
						console.error("取得醫師資料失敗", error);
						alert("無法取得醫師清單");
					});
			};

			search.value = "";
			search.oninput = () => render(search.value.trim());
			render();
		}
		else if (!popup.contains(e.target)) {
			popup.style.display = "none";
		}
	});
};

document.addEventListener("DOMContentLoaded", registerEventHandlers);