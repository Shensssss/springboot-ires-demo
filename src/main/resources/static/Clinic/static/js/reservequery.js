(() => {
    // const clinicAccount = sessionStorage.getItem("account");
    // const clinicName = sessionStorage.getItem("clinicName");
    const clinicId = sessionStorage.getItem("clinicId");

    const doctorSelect = document.querySelector('#doctor');
    const dateSInput = document.querySelector('#dateS');
    const dateEInput = document.querySelector('#dateE');
    const scheduleSelect = document.querySelector('#schedule');
    const searchBtn = document.querySelector('#btnSearchDoctor');
    const ExportBtn = document.querySelector('#btnExport');
    const msgBox = document.querySelector('#msg');

    let ExportData = []; // 存放查詢後資料

    // 初始化 - 載入醫師清單
    init();
    setTodayToDateRange(); // 日期初始化

    // 綁定匯出按鈕
    ExportBtn.addEventListener('click', () => {

        if (!Array.isArray(ExportData) || ExportData.length === 0) {
            msgBox.textContent = "無資料可匯出，請重新查詢。";
            return;
        }
        else {
            msgBox.textContent = `已完成匯出，共 ${ExportData.length} 筆資料`;

            const csvRows = [];
            const headers = ["預約日期", "預約時段", "預約號碼", "姓名", "看診醫師", "狀態"];
            csvRows.push(headers.join(","));

            ExportData.forEach(row => {
                const csvRow = row.map(cell => `"${String(cell).replace(/"/g, '""')}"`);
                csvRows.push(csvRow.join(","));
            });

            const csvContent = csvRows.join("\n");
            const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
            const url = URL.createObjectURL(blob);

            const link = document.createElement("a");
            link.setAttribute("href", url);
            link.setAttribute("download", "預約結果.csv");
            link.style.display = "none";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    })

    // 綁定查詢按鈕
    searchBtn.addEventListener('click', () => {
        let doctor_id = doctorSelect.value;
        const dateS = dateSInput.value;
        const dateE = dateEInput.value;
        const schedule = scheduleSelect.value;

        if (!dateS || !dateE) {
            msgBox.textContent = "「日期」為必填欄位，請點選「日期」。";
            return;
        }
        // 預約日期  防呆:起<=訖
        else {
            if (dateS > dateE) {
                msgBox.textContent = "「起始日期」不可大於「截止日期」，請重新點選「起始日期」。";
                return;
            }
        }


        if (doctor_id.length === 0) {
            doctor_id = "0";
        }



        // 將文字時段轉為對應數字
        let time_period = 0;
        if (schedule) {
            switch (schedule) {
                case 'morning':
                    time_period = 1;
                    break;
                case 'afternoon':
                    time_period = 2;
                    break;
                case 'night':
                    time_period = 3;
                    break;
                default:
                    time_period = 0;
                    return;
            }
        }

        const payload = {
            clinic_id: parseInt(clinicId),
            doctor_id: parseInt(doctor_id),    // 允許無值，因此帶入為0
            dateS: dateS,
            dateE: dateE,
            time_period: time_period
        };

        console.log("送出 payload：", payload);

        fetch("../clinic/reservequery/result", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(resp => resp.json())
            .then(data => {
                console.log("查詢成功", data);
                msgBox.textContent = "查詢成功！";
                ExportData = data;  // 更新 存放查詢後資料

                const resultTableBody = document.querySelector('#resultTable tbody');
                resultTableBody.innerHTML = '';

                if (!Array.isArray(data) || data.length === 0) {
                    msgBox.textContent = "查無資料";
                    resultTableBody.innerHTML = '<tr><td colspan="4">查無資料</td></tr>';
                    return;
                }
                else {
                    msgBox.textContent = `查詢成功，共 ${ExportData.length} 筆資料`;
                }

                // 顯示查詢結果
                data.forEach(row => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${row[0]}</td>
                        <td>${row[1]}</td>
                        <td>${row[2]}</td>
                        <td>${row[3]}</td>
                        <td>${row[4]}</td>
                        <td>${row[5]}</td>
                    `;
                    resultTableBody.appendChild(tr);
                });
            })
            .catch(error => {
                console.error("查詢失敗", error);
                msgBox.textContent = "查詢失敗，請稍後再試";
            });
    });

    function init() {
        const clinic_account = sessionStorage.getItem("account");

        fetch("../clinic/reservequery/SearchDoctor?clinic_account=" + clinic_account)
            .then(resp => resp.json())
            .then(data => {
                console.log("載入成功：", data);
                doctorSelect.innerHTML = '<option value="">請選擇</option>';
                data.forEach(doctor => {
                    const option = document.createElement("option");
                    option.value = doctor.doctor_id;
                    option.textContent = doctor.doctor_name;
                    doctorSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("載入醫師清單失敗", error);
                msgBox.textContent = "載入醫師清單失敗，請稍後再試";
            });
    }

    function setTodayToDateRange() {
        const today = new Date().toISOString().split("T")[0]; // 格式 yyyy-MM-dd
        document.getElementById('dateS').value = today;
        document.getElementById('dateE').value = today;
    }
})();
