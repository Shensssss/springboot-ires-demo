// 將營業時間填入表格
function renderBusinessHours(clinic) {
    const morningTime = clinic.morning;
    const afternoonTime = clinic.afternoon;
    const nightTime = clinic.night;

    const weekMorning = clinic.weekMorning?.split(",").map(d => d.trim());
    const weekAfternoon = clinic.weekAfternoon?.split(",").map(d => d.trim());
    const weekNight = clinic.weekNight?.split(",").map(d => d.trim());

    // 設定早中晚時間
    $("#morning").text(`${morningTime}`);
    $("#afternoon").text(`${afternoonTime}`);
    $("#night").text(`${nightTime}`);

    // 清除先前勾勾
    for (let i = 1; i <= 7; i++) {
        $(`#morning-${i}`).text("");
        $(`#afternoon-${i}`).text("");
        $(`#night-${i}`).text("");
    }

    // 填入打勾（✔）
    if (weekMorning) {
        weekMorning.forEach(day => {
            const td = $(`#morning-${day}`);
            if (td.length > 0) {
                td.text("✔");
            }
        });
    }

    if (weekAfternoon) {
        weekAfternoon.forEach(day => {
            const td = $(`#afternoon-${day}`);
            if (td.length > 0) {
                td.text("✔");
            }
        });
    }

    if (weekNight.length > 0) {
        weekNight.forEach(day => {
            const td = $(`#night-${day}`);
            if (td) {
                td.text("✔");
            }
        });
    }
}

// 取得勾選的(早/中/晚有營業的)星期幾字串陣列
function getSelectedDays(timePeriod) {
    const selectedDays = [];
    for (let i = 1; i <= 7; i++) {
        const checkbox = document.getElementById(`${timePeriod}${i}`);
        if (checkbox && checkbox.checked) {
            selectedDays.push(i);
        }
    }
    return selectedDays.join(",");
}

// AJAX請求：clinicMajor
function fetchClinicMajor(clinicId){
    fetch(`/ires-system/clinicMajor/major?clinicId=${clinicId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(resp => resp.json())
        .then(majors => {
            if(majors.length === 0){
                $("#clinicMajors").text("(請按下編輯按鈕進行勾選)");
            }else{
                const clinicMajors = majors.map(major => major.majorName).join('，');
                $("#clinicMajors").text(clinicMajors);
            }

            // 取得已勾選的majaorId陣列，有值的話傳給 renderMajorCheckboxes
            // 點擊編輯按鈕時才能取得已經勾選的major，再設法預設打勾
            const selectedMajors = majors.map(major => major.majorId);
            if(selectedMajors.length > 0){
                renderMajorCheckboxes(selectedMajors);
            }else{
                renderMajorCheckboxes([]);
            }
        })
}

// AJAX請求：editClinicMajors
function editClinicMajors(selectedMajorIds) {
    return fetch("/ires-system/clinicMajor/edit", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selectedMajorIds)
    })
        .then(resp => resp.json())
}

// AJAX請求：showInfo
function fetchShowInfo(){
    fetch("/ires-system/clinic/clinicInfo/showInfo", {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(resp => resp.json())
        .then(result => {
            if (result.statusCode === 200) {
                const clinic = result.data;
                $("#basicInfo").attr("data-clinic-id", clinic.clinicId);
                $("#name").text(clinic.clinicName);
                $("#agencyId").text(clinic.agencyId);
                $("#phone").text(clinic.phone);
                $("#addressCity").text(clinic.addressCity);
                $("#addressTown").text(clinic.addressTown);
                $("#addressRoad").text(clinic.addressRoad);
                $("#latitude").text(clinic.latitude);
                $("#longitude").text(clinic.longitude);
                $("#web").text(clinic.web);
                $("#registrationFee").text(clinic.registrationFee);
                $("#memo").text(clinic.memo);
                $("#profilePicture").attr("src", "data:image/png;base64,"+clinic.profilePicture);
                renderBusinessHours(clinic);
                const clinicId = $("#basicInfo").attr("data-clinic-id");
                fetchClinicMajor(clinicId);
            }
        })
}

// 網頁開啟就呼叫顯示基本資料
$(document).ready(function(){
    fetchShowInfo();
    
});

// 編輯表單的圖片預覽
$("#editProfilePicture").on("change", function () {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            $("#editProfilePicturePreview").attr("src", e.target.result);
        };
        reader.readAsDataURL(file);
    }
});


// 從後端取得所有major長出checkbox全部選項
function renderMajorCheckboxes(selectedMajors) {
    // 一定邀先清空否則每按一次編輯會長出一次
    $("#editClinicMajors").empty();

    fetch("/ires-system/major/list", {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(resp => resp.json())
        .then(majors => {
            majors.forEach(major => {
                const htmlText = `
                    <div>
                        <input type="checkbox" id="major_${major.majorId}" value="${major.majorName}">
                        <label for="major_${major.majorId}">${major.majorName}</label><br>
                    </div>
                `;

                $("#editClinicMajors").append(htmlText);
                // 根據 selectedMajors 陣列來預設勾選
                if (selectedMajors.includes(major.majorId)) {
                    $(`#major_${major.majorId}`).prop("checked", true);
                }

            });
        })  
}

// AJAX請求：編輯基本資料存入
function fetchEditBasicInfo(profilePicture){
    const name = $("#editName").val().trim();
    const phone = $("#editPhone").val().trim();
    const city = $("#editAddressCity").val().trim();
    const town = $("#editAddressTown").val().trim();
    const road = $("#editAddressRoad").val().trim();
    const lat = $("#editLatitude").val();
    const lng = $("#editLongitude").val();
    const web = $("#editWeb").val().trim();
    const registrationFee= $("#editRegistrationFee").val().trim();
    const memo = $("#editMemo").val().trim();

    // 收集所有打勾的 majorId放入陣列
    const selectedMajorIds = [];

    $("#editClinicMajors input[type='checkbox']:checked").each(function () {
        const majorId = parseInt($(this).attr("id").split("_")[1]);
        selectedMajorIds.push(majorId);
    });

    if( selectedMajorIds.length === 0){
        alert("請至少勾選一個專科！")
        return;
    }

    fetch("/ires-system/clinic/clinicInfo/editBasicInfo", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            clinicName: name,
            profilePicture: profilePicture,
            phone: phone,
            addressCity: city,
            addressTown: town,
            addressRoad: road,
            latitude: lat,
            longitude: lng,
            web: web,
            registrationFee: registrationFee,
            memo: memo
        })
    })
        .then(resp => resp.json())
        .then(result => {
            alert(result.message);
            if (result.statusCode === 200) {
                return editClinicMajors(selectedMajorIds);
            }
        })

        .then(majorResult => {
            // 這裡是 editClinicMajors 成功後
            alert(majorResult.message);
            if (majorResult.statusCode === 200) {
                $("#editBasicOverlay").removeClass("show");
                $("#editBasicInfoForm").hide();
                fetchShowInfo();
            }
        })
}

// 取得經緯度資料
$("#geolocationBtn").on("click", function () {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                $("#editLatitude").val(lat);
                $("#editLongitude").val(lng);
                showMapPreview(lat, lng);
                alert("更新地圖成功！");
        }, function(error){
            let message = "發生未知錯誤";
            switch (error.code) {
                case error.PERMISSION_DENIED:
                    message = "您拒絕提供位置資訊";
                    break;
                case error.POSITION_UNAVAILABLE:
                    message = "無法取得位置資訊";
                    break;
                case error.TIMEOUT:
                    message = "取得位置資訊逾時";
                    break;
            }
            alert(message);
        })
    }else {
        alert("您的瀏覽器不支援地理定位功能");
    }
});

// 地圖預覽
function showMapPreview(lat, lng) {
    if (!lat || !lng) {
        $("#mapPreview").html("<p style='color:red;'>無法顯示，請點擊「更新地圖」</p>");
        return;
    }

    const iframe = `
        <iframe
        width="100%"
        height="100%"
        frameborder="0"
        style="border:0"
        src="https://www.google.com/maps?q=${lat},${lng}&hl=zh-TW&z=16&output=embed"
        allowfullscreen>
        </iframe>`;
    $("#mapPreview").html(iframe);
}


// 一、編輯基本資訊
// 按下編輯按鈕
$("#editBasicInfoBtn").on("click",function(){
    // 帶入原有資料，顯示編輯表單
    $("#editName").val($("#name").text());
    $("#editAgencyId").val($("#agencyId").text());
    const clinicId = $("#basicInfo").attr("data-clinic-id");
    fetchClinicMajor(clinicId);
    $("#editProfilePicturePreview").attr("src", $("#profilePicture").attr("src")).show();
    $("#editPhone").val($("#phone").text());
    $("#editAddressCity").val($("#addressCity").text());
    $("#editAddressTown").val($("#addressTown").text());
    $("#editAddressRoad").val($("#addressRoad").text());
    const lat = $("#latitude").text();
    const lng = $("#longitude").text();
    $("#editLatitude").val(lat);
    $("#editLontitude").val(lng);
    showMapPreview(lat, lng);
    $("#editWeb").val($("#web").text());
    $("#editRegistrationFee").val($("#registrationFee").text());
    $("#editMemo").val($("#memo").text());

    $("#editBasicOverlay").addClass("show");
    $("#editBasicInfoForm").show();
})

// 按下取消按鈕
$('#cancelEditBtn').on("click", function() {
    $("#editName").val("");
    // 清除檔案選擇欄位（input type="file"）
    $("#editProfilePicture").val("");
    // 清除圖片預覽
    $("#editProfilePicturePreview").attr("src", "").hide();
    $("#editPhone").val("");
    $("#editAddressCity").val("");
    $("#editAddressTown").val("");
    $("#editAddressRoad").val("");
    $("#editWeb").val("");
    $("#editRegistrationFee").val("");
    $("#editMemo").val("");

    $("#editBasicOverlay").removeClass("show");
    $("#editBasicInfoForm").hide();
});

// 按下儲存按鈕
$("#saveEditBtn").on("click", function() {

    const file = document.querySelector("#editProfilePicture").files[0];
        if(file){
            const fileReader = new FileReader();
            fileReader.addEventListener("load", function(e){
                const base64profilePicture = e.target.result.split(",")[1];
                fetchEditBasicInfo(base64profilePicture);
            })
            fileReader.readAsDataURL(file);
        }else{
            const src = $("#profilePicture").attr("src");
            const originalProfilePicture = src ? src.split(",")[1] : "";
            fetchEditBasicInfo(originalProfilePicture);
        }

})

// 二、編輯營業時間
// 按下編輯按鈕
$("#editBusinessHoursBtn").on("click", function(){
    $("#editMorning").val($("#morning").text());
    $("#editAfternoon").val($("#afternoon").text());
    $("#editNight").val($("#night").text());

     // 將每天的勾選狀態填進編輯表單的 checkbox
    for (let i = 1; i <= 7; i++) {
        // 有✔號標註就checked
        if ($("#morning-" + i).text() === "✔") {
            $("#weekMorning" + i).prop("checked", true);
        }
        if ($("#afternoon-" + i).text() === "✔") {
            $("#weekAfternoon" + i).prop("checked", true);
        }
        if ($("#night-" + i).text() === "✔") {
            $("#weekNight" + i).prop("checked", true);
        }
    }

    // 滾到表單內部頂部，並強制視窗到最上方
    $("#addOverlay .form-popup").scrollTop(0);
    $("html, body").scrollTop(0);

    $("#editHoursOverlay").addClass('show');
    $("#editBusinessHoursForm").show();
})

// 按下取消按鈕
$("#cancelEditHoursBtn").on("click", function(){
    $("#editMornig").val("");
    $("#editAfternoon").val("");
    $("#editNight").val("");

    for (let i = 1; i <= 7; i++) {
        $("#weekMorning" + i).prop("checked", false);
        $("#weekAfternoon" + i).prop("checked", false);
        $("#weekNight" + i).prop("checked", false);
    }

    $("#editHoursOverlay").removeClass("show");
    $("#editBusinessHoursForm").hide();
})


// 按下儲存按鈕
$("#saveEditHoursBtn").on("click", function(){
    const morning = $("#editMorning").val().trim();
    const afternoon = $("#editAfternoon").val().trim();
    const night = $("#editNight").val().trim();

    const timePattern = /^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])-(2[0-3]|[01]?[0-9]):([0-5]?[0-9])$/;

    if (!timePattern.test(morning) || !timePattern.test(afternoon) || !timePattern.test(night)) {
        alert("請填入正確時間，格式為 HH:mm-HH:mm");
        return;
    }

    const weekMorning = getSelectedDays("weekMorning");
    const weekAfternoon = getSelectedDays("weekAfternoon");
    const weekNight = getSelectedDays("weekNight");

    if (!weekMorning && !weekAfternoon && !weekNight) {
        alert("營業時段不可為空！");
        return;
    }

    fetch("/ires-system/clinic/clinicInfo/editBusinessHours", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            morning: morning,
            afternoon: afternoon,
            night: night,
            weekMorning: weekMorning,
            weekAfternoon: weekAfternoon,
            weekNight: weekNight
        })
    })
    .then(response => response.json())
    .then(result => {
        alert(result.message);
        if (result.statusCode === 200) {
            fetchShowInfo();
        }
        $("#editHoursOverlay").removeClass("show");
        $("#editBusinessHoursForm").hide();
    })
})



    

