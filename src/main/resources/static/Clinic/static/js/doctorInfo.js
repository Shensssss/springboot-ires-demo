        
    //AJAX請求：showAll
    function fetchShowAll(){
        fetch('/ires-system/doctor/showAll', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })

            .then(resp => resp.json())
            .then(result => {
                if (result.statusCode === 200) {
                    const doctors = result.data|| [];
                    //先清空原有資料
                    $("#container").empty();
                    //將每位醫師資料放入個別卡片
                    doctors.forEach(doctor => {
                        const eduHtml = doctor.education && doctor.education.length > 0 ? `<p><span class="label">學歷：</span><span class="edu">${doctor.education.join("、")}</span></p>`: "";
                        const expHtml = doctor.experience && doctor.experience.length > 0 ? `<p><span class="label">經歷：</span><span class="exp">${doctor.experience.join("、")}</span></p>`: "";
                        const memoHtml = doctor.memo && doctor.memo.length > 0 ? `<p><span class="label">專長：</span><span class="memo">${doctor.memo.join("、")}</span></p>`: "";
                        const picSrc = doctor.profilePicture ? `data:image/png;base64,${doctor.profilePicture}` : "";

                            const cardHtml = `
                                <div class="card" data-doctor-id="${doctor.doctorId}">
                                    <button class="editBtn">編輯</button>
                                    <button class="deleteBtn">刪除</button>
                                    <p><span class="name">${doctor.doctorName}</span> 醫師</p>
                                    ${picSrc ? `<img src="${picSrc}" alt="照片未上傳" style="max-width: 100px;">` : ""}
                                    ${eduHtml}
                                    ${expHtml}
                                    ${memoHtml}
                                </div>
                            `;

                            $("#container").append(cardHtml);
                        })
                }else {
                    alert(result.message);
                }
            }); 
    }

    //網頁開啟就呼叫顯示所有醫師
    document.addEventListener("DOMContentLoaded", function() {
        fetchShowAll();
    });

    //Ajax請求：add
    function fetchAdd(data) {
        fetch('/ires-system/doctor/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(resp => resp.json())
        .then(result => {
            if (result.statusCode === 200) {
                alert(result.message);
                $("#addOverlay").removeClass("show");
                $("#addForm").hide();
                fetchShowAll();
            } else {
                alert(result.message + "，請稍後再試！");
                $("#addOverlay").removeClass("show");
                $("#addForm").hide();
            }
        });
    }

    //Ajax請求：edit
    function fetchEdit(data) {
        // console.log("Edit data:", data);
        fetch('/ires-system/doctor/edit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(resp => resp.json())
        .then(result => {
            if (result.statusCode === 200) {
                alert(result.message);
                $("#editOverlay").removeClass("show");
                $("#editForm").hide();
                fetchShowAll();
            } else {
                alert(result.message + "，請稍後再試！");
                $("#editOverlay").removeClass("show");
                $("#editForm").hide();
            }
        });
    }
   
    
    //清除表單內容
    function clearFormValues(prefix){
        $(`#${prefix}Name`).val("");
        $(`#${prefix}ProfilePicture`).val("");
        $(`#${prefix}Preview`).attr("src", "").hide();

        [1, 2, 3].forEach(i => {
            $(`#${prefix}Edu${i}`).val("");
            $(`#${prefix}Exp${i}`).val("");
            $(`#${prefix}Memo${i}`).val("");
        });
    }

    
    //取得表單欄位資料
    function getFormValues(prefix){
        const name = $(`#${prefix}Name`).val().trim();
        // 陣列中任一處輸入空字串時=false會被filter擋下不顯示
        const edu = [$(`#${prefix}Edu1`).val(), $(`#${prefix}Edu2`).val(), $(`#${prefix}Edu3`).val()].filter(Boolean);
        const exp = [$(`#${prefix}Exp1`).val(), $(`#${prefix}Exp2`).val(), $(`#${prefix}Exp3`).val()].filter(Boolean);
        const memo = [$(`#${prefix}Memo1`).val(), $(`#${prefix}Memo2`).val(),$(`#${prefix}Memo3`).val()].filter(Boolean);
        return { name, edu, exp, memo };
    }

    // 預覽新增照片
    $("#addProfilePicture").on("change", function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $("#addPreview").attr("src", e.target.result).show();
            };
            reader.readAsDataURL(file);
        } else {
            $("#addPreview").hide();
        }
    });

    // 預覽編輯照片
    $("#editProfilePicture").on("change", function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $("#editPreview").attr("src", e.target.result).show();
            };
            reader.readAsDataURL(file);
        } else {
            $("#editPreview").hide();
        }
    });
    

    // 一、新增
    // 按下新增
    $("#addBtn").on("click", function () {
        const name = $("#searchName").val().trim();
        // 檢查網頁上是否已存在
        let duplicate = false;
        $(".card .name").each(function () {
            if ($(this).text().trim() === name && name !== "") {
                duplicate = true;
                return false;
            }
        });

        if (duplicate) {
            alert("此位醫師已存在，請勿重複新增！");
            return;
        }


        // 不重複後開始新增，顯示新增表單(帶入輸入的姓名)
        clearFormValues("add");
        $("#addName").val(name);
        $("#addForm").show();
        $("#addOverlay").addClass("show");

        // 滾到表單內部頂部，並強制視窗到最上方
        $("#addOverlay .form-popup").scrollTop(0);
        $("html, body").scrollTop(0);
    });

    // 按下取消則清空欄位並隱藏表單
    $("#cancelAddBtn").on("click", function () {
        clearFormValues("add");
        $("#addOverlay").removeClass("show");
        $("#addForm").hide();
    });    

    // 按下儲存將填入的資料送至後端，重發請求取得所有醫師資料
    $("#saveAddBtn").on("click", function () {

        const { name, edu, exp, memo} = getFormValues("add");
        if (!name) {
            alert("姓名不可為空白！");
            return;
        }

        // 檢查網頁上是否已存在
        let duplicate = false;
        $(".card .name").each(function () {
            if ($(this).text().trim() === name && name !== "") {
                duplicate = true;
                return false;
            }
        });

        if (duplicate) {
            alert("此位醫師已存在，請勿重複新增！");
            return;
        }

        const file = document.querySelector("#addProfilePicture").files[0];
        if(file){
            const fileReader = new FileReader();
            fileReader.addEventListener("load", function(e){
                const imageBase64 = e.target.result.split(',')[1];
                fetchAdd({
                    doctorName: name,
                    education: edu,
                    experience: exp,
                    memo: memo,
                    profilePicture: imageBase64,
                })
            })
            fileReader.readAsDataURL(file);
        }else{
            fetchAdd({
                doctorName: name,
                education: edu,
                experience: exp,
                memo: memo,
                profilePicture: "",
            })
        }
    })

    // 二、編輯
    // 按下編輯
    $("#container").on("click", ".editBtn", function () {
        const card = $(this).closest(".card");
        const doctorId = card.data("doctorId");
        $("#editForm").data("doctorId", doctorId);

        const name = card.find(".name").text().trim();
        const picSrc = card.find("img").attr("src") || "";
        $("#editPreview").attr("src", picSrc).show();
        // 儲存原圖 base64，如果沒有圖片，存空字串
        $("#editForm").data("originalProfilePicture", picSrc.startsWith("data:image") ? picSrc.split(",")[1] : "");
        const edu = card.find(".edu").map(function () { return $(this).text(); }).get();
        const exp = card.find(".exp").map(function () { return $(this).text(); }).get();
        const memo = card.find(".memo").map(function () { return $(this).text(); }).get();

        $("#editName").val(name);
        $("#editEdu1").val(edu[0] || "");
        $("#editEdu2").val(edu[1] || "");
        $("#editEdu3").val(edu[2] || "");
        $("#editExp1").val(exp[0] || "");
        $("#editExp2").val(exp[1] || "");
        $("#editExp3").val(exp[2] || "");
        $("#editMemo1").val(memo[0] || "");
        $("#editMemo2").val(memo[1] || "");
        $("#editMemo3").val(memo[2] || "");

        $("#editForm").show();
        $("#editOverlay").addClass("show");
        // 滾動表單內部到頂部
        $("#editOverlay .form-popup").scrollTop(0);
        // 視窗自動滾動至表單頂部
        setTimeout(function () {
            const offsetTop = $("#editOverlay .form-popup").offset().top;
            $("html, body").animate({ scrollTop: offsetTop - 20 }, 300);
        }, 100);
    })

    // 取消編輯
    $("#cancelEditBtn").on("click", function () {
        clearFormValues("edit");
        $("#editOverlay").removeClass("show");
        $("#editForm").hide();
    });

    // 儲存編輯
    $("#saveEditBtn").on("click", function () {
        const { name, edu, exp, memo} = getFormValues("edit");
        if (!name) {
            alert("姓名不可為空白！");
            return;
        }
        const doctorId = $("#editForm").data("doctorId");
        const originalProfilePicture = $("#editForm").data("originalProfilePicture");

        const file = document.querySelector('#editProfilePicture').files[0];
        if(file){
            const fileReader = new FileReader();
            fileReader.addEventListener("load", function(e){
                const imageBase64 = e.target.result.split(",")[1];
                fetchEdit({
                    doctorId: doctorId,
                    doctorName: name,
                    education: edu,
                    experience: exp,
                    memo: memo,
                    profilePicture: imageBase64,
                })
            })
            fileReader.readAsDataURL(file);
        }else{
            fetchEdit({
                doctorId: doctorId,
                doctorName: name,
                education: edu,
                experience: exp,
                memo: memo,
                profilePicture: originalProfilePicture,
            })
        }
    });
    
    // 三、刪除
    $("#container").on("click", ".deleteBtn", function () {
        if (confirm("確定要刪除此位醫師嗎？ 刪除後不可回復")) {
            const doctorId = $(this).closest(".card").data("doctorId");
            fetch('/ires-system/doctor/delete',  {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({doctorId})
            })
                .then(resp => resp.json())
                .then(result => {
                    if(result.statusCode === 200){
                        alert(result.message);
                        fetchShowAll();
                    }else{
                        alert(result.message + "，請稍後再試！")
                    }
                })
        }
    });

    //四、搜尋
    $("#searchBtn").on("click", function () {
        const keyword = $("#searchName").val().trim();
        if (keyword === "") {
            fetchShowAll();
            return;
        }

        fetch(`/ires-system/doctor/showSearchedByName?name=${encodeURIComponent(keyword)}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
            .then(resp => resp.json())
            .then(result => {
                if (result.statusCode === 200) {
                    const doctors = result.data || [];
                    $("#container").empty();
                    doctors.forEach(doctor => {
                        const eduHtml = doctor.education?.length ? `<p><span class="label">學歷：</span><span class="edu">${doctor.education.join("、")}</span></p>` : "";
                        const expHtml = doctor.experience?.length ? `<p><span class="label">經歷：</span><span class="exp">${doctor.experience.join("、")}</span></p>` : "";
                        const memoHtml = doctor.memo?.length ? `<p><span class="label">專長：</span><span class="memo">${doctor.memo.join("、")}</span></p>` : "";
                        const picSrc = doctor.profilePicture ? `data:image/png;base64,${doctor.profilePicture}` : "";

                        const cardHtml = `
                            <div class="card" data-doctor-id="${doctor.doctorId}">
                                <button class="editBtn">編輯</button>
                                <button class="deleteBtn">刪除</button>
                                <p><span class="name">${doctor.doctorName}</span> 醫師</p>
                                ${picSrc ? `<img src="${picSrc}" alt="照片未上傳" style="max-width: 100px;">` : ""}
                                ${eduHtml}
                                ${expHtml}
                                ${memoHtml}
                            </div>
                        `;
                        $("#container").append(cardHtml);
                    });
                } else if(result.statusCode === 404){
                    alert(result.message);
                }else{
                    alert("發生錯誤，請聯絡管理員")
                }
            });
    });