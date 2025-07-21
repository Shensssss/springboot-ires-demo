// 取得病患資料並顯示在頁面
function fetchPatients() {
    $.get('/ires-system/account/patient')
        .done(function (res) {

            var patient = res;
            $('#name').val(patient.name);
            $('#gender').val(patient.gender);
            $('#birthday').val(patient.birthday);
            $('#phone').val(patient.phone);
            $('#address').val(patient.address || '');
            $('#email').val(patient.email);
            $('#emergencyContent').val(patient.emergencyContent || '');
            $('#emergencyName').val(patient.emergencyName || '');
            $('#relation').val(patient.relation || '');
            $('#bloodType').val(patient.bloodType);
            $('#notes').val(patient.notes || '');

            if (patient.profilePicture) {
                $('#preview').attr('src', 'data:image/jpeg;base64,' + patient.profilePicture).show();
            }
        })
        .fail(function (err) {
            alert('載入失敗 (' + err.status + ')');
        });
}
fetchPatients();

//提交異動資料
$("#infoForm").on("submit", function (e) {
    e.preventDefault();
    const get = id => $(`#${id}`).val();

    // 取得 preview 圖片的 src
    const src = $('#preview').attr('src');
    let base64Data = null;

    if (src && src.startsWith('data:image/')) {
        // 將 base64 data 從 data URL 中分離出來
        base64Data = src.split(',')[1];
    }
    const patientData = {
        name: get("name"),
        gender: get("gender"),
        birthday: get("birthday"),
        email: get("email"),
        phone: get("phone"),
        address: get("address"),
        emergencyContent: get("emergencyContent"),
        emergencyName: get("emergencyName"),
        relation: get("relation"),
        bloodType: get("bloodType"),
        notes: get("notes"),
        profilePicture: base64Data
        // profilePicture: $('#preview').attr('src') || null
    };
    sendPatientData(patientData);
});

// 資料傳送到後端
function sendPatientData(data) {
    $.ajax({
        url: '/ires-system/account/patient',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: res => {
            alert('資料已儲存！');
            fetchPatients();
        },
        error: xhr => {
            const msg = xhr.responseJSON?.message || `錯誤：${xhr.status}`;
            alert('送出失敗：' + msg);
        }
    });
}

//圖片預覽與處理
$('#profilePicture').change(function () {
    const file = this.files[0];
    const reader = new FileReader();
    reader.onloadend = function (event) {
        $('#preview').attr('src', event.target.result).show();
    };
    reader.readAsDataURL(file);
});