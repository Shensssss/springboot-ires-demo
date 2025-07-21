(() => {
    const clinicId = sessionStorage.getItem("clinicId");
    const btn1 = document.querySelector('#btn-submit');
    const oPassword = document.querySelector('#current-password');
    const nPassword = document.querySelector('#new-password');
    const confirmPassword = document.querySelector('#confirm-password');
    const msg = document.querySelector('#msg');

    init();

    function init() {
        btn1.addEventListener('click', accountupdatepsd);
    }

    function accountupdatepsd(event) {
        event.preventDefault(); // 防止表單預設送出

        if (nPassword.value && confirmPassword.value) {
            if (nPassword.value.length < 6 || nPassword.value.length > 20) {
                msg.textContent = '密碼長度須介於6~20字元';
                return;
            }

            if (confirmPassword.value !== nPassword.value) {
                msg.textContent = '密碼與確認密碼不相符';
                return;
            }
        }

        msg.textContent = '';

        //URL 大小寫注意，要與後端對齊
        fetch('/ires-system/clinic/accountupdatepsd/api', {
            method: 'POST', // 注意請求方法，應為POST
            headers: {
                'Content-Type': 'application/json',
            },
            // 送出的json 
            body: JSON.stringify({
                clinic_id: parseInt(clinicId),  // 這邊要抓 clinic的 id
                oPassword: oPassword.value,
                nPassword: nPassword.value,
                confirmPassword: confirmPassword.value
            }),
        })
            .then(resp => resp.text())
            .then(message => {
                if (message.includes('成功')) {
                    msg.classList.remove('error');
                    msg.classList.add('info');
                    oPassword.value = '';
                    nPassword.value = '';
                    confirmPassword.value = '';
                    btn1.disabled = true;
                } else {
                    msg.classList.remove('info');
                    msg.classList.add('error');
                    // msg.className = 'error';
                }
                msg.textContent = message;
            })
            .catch(err => {
                msg.className = 'error';
                msg.textContent = '發生錯誤，請稍後再試';
                console.error('fetch error:', err);
            });
    }

})();

function togglePassword(inputId, icon) {
    const input = document.getElementById(inputId);
    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';

    // 切換圖示
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
}

