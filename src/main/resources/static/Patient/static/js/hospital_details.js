let clinicId = 0;
let doctorId = 0;
let clinicName = "";
let clinicAddress = "";
let clinicPhone = "";
let clinicEmail = "";
let clinicMemo = "";
let doctorList = "";
let doctorSelect = "";
let majorList = "";
let majorListImg = "";
let commentHtml = "";
let patientIdJson = {};
let ratingLength = 0;
let ratingCount = 0;
let quota = 0;
let morningQuota = 0;
let afternoonQuota = 0;
let eveningQuota = 0;
let latitude = 0.0;
let longitude = 0.0;
let weekMorning = [];
let weekAfternoon = [];
let weekNight = [];

const waitForMapElement = () => {
  const mapDiv = document.getElementById("map");
  if (mapDiv) {
    // 使用全域 latitude, longitude 初始化地圖，避免 clinic 未定義錯誤
    init(Number(latitude), Number(longitude));
  } else {
    console.log("⏳ 等待 #map...");
    setTimeout(waitForMapElement, 200);
  }
};

$(document).ready(function () {
  const params = new URLSearchParams(window.location.search);
  clinicId = params.get("clinicId");

  $.ajax({
    url: `/ires-system/clinic/id/${clinicId}`,
    type: "GET",
    success: (clinic) => {
      const address =
        clinic.addressCity + clinic.addressTown + clinic.addressRoad;
      clinicName = clinic.clinicName;
      clinicAddress = address;
      clinicPhone = clinic.phone;
      clinicEmail = clinic.account;
      clinicMemo = clinic.memo;
      quota = clinic.quota;
      latitude = clinic.latitude;
      longitude = clinic.longitude;
      console.log(`${latitude}. ${longitude}`);
      weekMorning = clinic.weekMorning
        ? clinic.weekMorning.split(",").map(Number)
        : [];
      weekAfternoon = clinic.weekAfternoon
        ? clinic.weekAfternoon.split(",").map(Number)
        : [];
      weekNight = clinic.weekNight
        ? clinic.weekNight.split(",").map(Number)
        : [];
      $.ajax({
        url: `/ires-system/clinicMajor/major?clinicId=${clinicId}`,
        type: "GET",
        success: (major) => {
          renderMajorList(major);
        },
        error: (e) => {
          // alert(e);
        },
      });
      $.ajax({
        url: `/ires-system/evaluations/list?clinicId=${clinicId}`,
        type: "GET",
        success: (data) => {
          ratingLength = data.length;
          renderComment(data);
          $.ajax({
            url: `/ires-system/doctor/showAll?clinicId=${clinicId}`,
            type: "GET",
            success: function (response) {
              renderDoctorselect(response.data);
              renderDoctorList(response.data);
              // 查詢三時段名額，全部取得後再渲染
              Promise.all([
                $.get(
                  `/ires-system/appointment/apiToday?period=morning&clinicIdParam=${clinicId}`
                ),
                $.get(
                  `/ires-system/appointment/apiToday?period=afternoon&clinicIdParam=${clinicId}`
                ),
                $.get(
                  `/ires-system/appointment/apiToday?period=evening&clinicIdParam=${clinicId}`
                ),
              ]).then(([morningData, afternoonData, eveningData]) => {
                morningQuota = quota - morningData.data.length;
                afternoonQuota = quota - afternoonData.data.length;
                eveningQuota = quota - eveningData.data.length;
                renderhospitalDetails(clinic);
                updateTimeOptions();

                setTimeout(() => {
                  waitForMapElement();
                }, 100);

                const dateParam = params.get("date");
                const timeParam = params.get("time");
                if (dateParam) {
                  $('input[type="date"]').val(dateParam);
                } else {
                  const today = new Date().toISOString().split("T")[0];
                  $('input[type="date"]').val(today);
                }
                if (timeParam !== null) {
                  $('select[name="time"]').val(timeParam);
                }
              });
            },
            error: function () {
              alert("無法取得診所列表，請稍後再試！");
            },
          });
        },
        // error: (e) => alert(e),
      });

      $.ajax({
        url: "/ires-system/account/patient",
        type: "GET",
        success: function (patient) {
          patientIdJson = patient;
        },
        error: function () {
          // alert("尚未登入或 session 失效");
        },
      });
    },
    error: (e) => {
      alert(e);
    },
  });
});

// 預約日期變更時，查詢各時段剩餘名額並動態更新時段選項
$(document).on("change", '.appointment-form input[type="date"]', function () {
  const selectedDate = $(this).val();
  if (!selectedDate) return;

  const quotaInt = parseInt(quota);

  $.ajax({
    url: `/ires-system/appointment/apiToday?date=${selectedDate}&period=morning&clinicIdParam=${clinicId}`,
    type: "GET",
    success: function (data) {
      morningQuota = quotaInt - data.data.length;
      updateTimeOptions();
    },
    error: function () {
      console.error("上午名額查詢失敗");
    },
  });

  $.ajax({
    url: `/ires-system/appointment/apiToday?date=${selectedDate}&period=afternoon&clinicIdParam=${clinicId}`,
    type: "GET",
    success: function (data) {
      afternoonQuota = quotaInt - data.data.length;
      updateTimeOptions();
    },
    error: function () {
      console.error("下午名額查詢失敗");
    },
  });

  $.ajax({
    url: `/ires-system/appointment/apiToday?date=${selectedDate}&period=evening&clinicIdParam=${clinicId}`,
    type: "GET",
    success: function (data) {
      eveningQuota = quotaInt - data.data.length;
      updateTimeOptions();
    },
    error: function () {
      console.error("晚上名額查詢失敗");
    },
  });
});

function updateTimeOptions() {
  const safeMorning = isNaN(morningQuota) ? 0 : morningQuota;
  const safeAfternoon = isNaN(afternoonQuota) ? 0 : afternoonQuota;
  const safeEvening = isNaN(eveningQuota) ? 0 : eveningQuota;

  const timeSelects = $('.appointment-form select[name="time"]');
  timeSelects.empty();

  const selectedDate = $('input[type="date"]').val();
  const dayOfWeek = selectedDate
    ? new Date(selectedDate).getDay() || 7
    : new Date().getDay() || 7;

  const isMorningOpen = weekMorning.includes(dayOfWeek);
  const isAfternoonOpen = weekAfternoon.includes(dayOfWeek);
  const isEveningOpen = weekNight.includes(dayOfWeek);

  timeSelects.append(
    `<option value="1" ${
      !isMorningOpen ? "disabled" : safeMorning <= 0 ? "disabled" : ""
    }>
      上午(${
        !isMorningOpen
          ? "休診中"
          : safeMorning <= 0
          ? "預約已滿"
          : `可預約名額 ${safeMorning}`
      })
    </option>`
  );
  timeSelects.append(
    `<option value="2" ${
      !isAfternoonOpen ? "disabled" : safeAfternoon <= 0 ? "disabled" : ""
    }>
      下午(${
        !isAfternoonOpen
          ? "休診中"
          : safeAfternoon <= 0
          ? "預約已滿"
          : `可預約名額 ${safeAfternoon}`
      })
    </option>`
  );
  timeSelects.append(
    `<option value="3" ${
      !isEveningOpen ? "disabled" : safeEvening <= 0 ? "disabled" : ""
    }>
      晚上(${
        !isEveningOpen
          ? "休診中"
          : safeEvening <= 0
          ? "預約已滿"
          : `可預約名額 ${safeEvening}`
      })
    </option>`
  );
}

$(document).on("click", "#submit-comment", function () {
  const comment = $("#comment-text").val().trim();
  if (!comment) {
    alert("請輸入評論內容");
    return;
  }

  const rating = $("#star-rating .fas").length;
  const clinicId = new URLSearchParams(window.location.search).get("clinicId");

  const data = {
    patient: { patientId: patientIdJson.patientId },
    clinic: { clinicId: clinicId },
    rating: rating,
    comment: comment,
  };

  $.ajax({
    url: "/ires-system/evaluations/add",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function (res) {
      alert("評論已送出！");
      location.reload();
    },
    error: function (err) {
      console.error(err);
      alert("送   出失敗，請稍後再試！");
    },
  });
});

function init(lat = latitude, lng = longitude) {
  var mapOptions = {
    zoom: 17,
    center: new google.maps.LatLng(lat, lng),
  };

  var mapElement = document.getElementById("map");
  if (!mapElement) {
    console.error("❌ 地圖容器 #map 未找到，取消初始化");
    return;
  }
  var map = new google.maps.Map(mapElement, mapOptions);

  var marker = new google.maps.Marker({
    position: new google.maps.LatLng(lat, lng),
    map: map,
    title: clinicName,
  });

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      function (position) {
        const userLat = position.coords.latitude;
        const userLng = position.coords.longitude;

        const userLocation = new google.maps.LatLng(userLat, userLng);
        const clinicLocation = new google.maps.LatLng(lat, lng);

        const distanceInMeters =
          google.maps.geometry.spherical.computeDistanceBetween(
            userLocation,
            clinicLocation
          );
        const distanceInKm = (distanceInMeters / 1000).toFixed(2);

        document.getElementById(
          "distance-result"
        ).innerText = `您目前距離診所約 ${distanceInKm} 公里`;
      },
      function () {
        document.getElementById("distance-result").innerText =
          "⚠️ 無法取得您的定位資訊";
      }
    );
  }
}

function renderDoctorList(doctors) {
  if (doctors.length === 0) {
    doctorList = "<p>沒有符合條件的診所。</p>";
    return;
  }

  const date = new Date().toISOString().split("T")[0]; // yyyy-mm-dd
  const api = `/ires-system/callNumber/listByClinic?clinicId=${clinicId}&date=${date}`;

  $.ajax({
    url: api,
    type: "GET",
    async: false,
    success: function (callNumbers) {
      const numberMap = {}; // doctorId => number
      callNumbers.forEach((c) => {
        if (!numberMap[c.doctorId] || c.number > numberMap[c.doctorId]) {
          numberMap[c.doctorId] = c.number;
        }
      });

      doctors.forEach(function (doctor) {
        const doctorImg = doctor.profilePicture.startsWith("data:")
          ? doctor.profilePicture
          : `data:image/jpeg;base64,${doctor.profilePicture}`;
        const numberBadge = numberMap[doctor.doctorId]
          ? `<span class="position-absolute top-0 end-0 badge bg-danger rounded-circle m-2">${
              numberMap[doctor.doctorId]
            }</span>`
          : "";

        doctorList += `
          <div class="col-md-6 d-flex position-relative">
            ${numberBadge}
            <div class="card-body p-0">
              <div class="bg-white d-flex p-3 p-lg-4 p-md-3 p-sm-4">
                <div class="d-block doctor-avatar flex-shrink-0 me-3 p-2 position-relative rounded-bottom rounded-circle shadow">
                  <img src="${doctorImg}" class="rounded-circle" />
                </div>
                <div class="flex-grow-1">
                  <h6 class="fs-17 fw-semibold mb-2 mb-sm-1">${doctor.doctorName}</h6>
                  <div class="fw-semibold mb-0 mb-sm-2 small text-primary">${doctor.education}</div>
                  <div class="d-none d-sm-block small">${doctor.memo}</div>
                  <div class="d-none d-sm-block small">${doctor.experience}</div>
                </div>
              </div>
              <div class="bg-white p-3 pt-0">
                <div class="row g-3">
                  <div class="col-auto col-md-12">
                    <h6 class="fs-13 mb-1 text-muted">工作於</h6>
                    <div class="fw-bold text-black fs-15">${clinicName}</div>
                  </div>
                  <div class="col-auto">
                    <h6 class="fs-13 mb-1 text-muted">經歷</h6>
                    <div class="fw-bold text-black fs-15">${doctor.experience}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>`;
      });
    },
    error: () => {
      console.error("無法取得叫號資料");
    },
  });
}

function renderComment(comments) {
  if (comments.length === 0) {
    commentHtml = "<p>沒有人留言。</p>";
    return;
  }
  comments.forEach(function (comment) {
    ratingCount = comment.clinic.rating;
    const avatar = comment.patient.profilePicture
      ? comment.patient.profilePicture.startsWith("data:")
        ? comment.patient.profilePicture
        : `data:image/jpeg;base64,${comment.patient.profilePicture}`
      : "static/picture/300.png";

    const name =
      comment.patient && comment.patient.name
        ? comment.patient.name
        : "匿名使用者";
    const rating = parseInt(comment.rating) || 0;
    let starsHtml = "";
    for (let i = 1; i <= 5; i++) {
      starsHtml += `<i class="${i <= rating ? "fas" : "far"} fa-star"></i>`;
    }

    commentHtml += `
      <div class="mb-4 pb-4 border-bottom">
        <div class="d-flex align-items-center">
         <div class="text-center me-4 d-flex flex-column align-items-center" style="min-width: 80px;">
          <img src="${avatar}" class="rounded-circle mb-2" alt="頭像" width="64" height="64" style="object-fit: cover;">
          <div class="fw-semibold">${name}</div>
        </div>
          <div class="flex-grow-1">
            <div class="mb-2 text-warning">
              ${starsHtml}
            </div>
            <div class="bg-light p-3 rounded shadow-sm">
              <div class="readmore js-read-more" data-rm-words="80">
                ${comment.comment}
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
  });
}

function renderDoctorselect(doctors) {
  doctors.forEach(function (doctor) {
    doctorSelect += `
      <option value="${doctor.doctorId}">${doctor.doctorName}</option>
    `;
  });
}

function renderMajorList(majors) {
  majors.forEach(function (major) {
    majorList += `
       <option value=${major.majorId}>${major.majorName}</option>
    `;
    const imageSrc = major.image.startsWith("data:")
      ? major.image
      : `data:image/jpeg;base64,${major.image}`;

    majorListImg += `
    <div class="col-sm-6 col-md-4 col-lg-3 col-xl-4 d-flex">
      <a class="card shadow border-0 text-inherit flex-fill w-100">
        <div class="card-body text-center">
          <h6 class="speciality-title overflow-hidden mb-1 fw-bold fs-18 mb-3">
            ${major.majorName}
          </h6>
          <img
            src="${imageSrc}"
            alt="..."
            height="80"
            width="80"
            class="mb-3"
          />
          <div class="mb-2 small">
            ${major.description}
          </div>
          <small class="text-primary fw-medium d-block">
            ${major.symptom}
          </small>
        </div>
      </a>
    </div>
    `;
  });
}

function renderhospitalDetails(clinic) {
  const container = $("#hospital-details-page");
  container.empty();

  if (!clinic) {
    container.append("<p>沒有符合條件的診所。</p>");
    return;
  }

  // 判斷是否顯示登入提示
  const showLoginPrompt = sessionStorage.getItem("patient");

  let ratingStarsHtml = "";
  for (let i = 1; i <= 5; i++) {
    const iconClass = i <= Math.round(ratingCount) ? "fas" : "far";
    ratingStarsHtml += `
      <i class="align-items-center d-inline-flex fa-star ${iconClass} justify-content-center rounded-1 bg-warning text-dark"></i>
    `;
  }

  // 登入提示 HTML
  const loginPromptHtml = !showLoginPrompt
    ? `
    <div class="text-center">
      <span>
        已經有帳號了嗎？
        <a href="./login.html?redirect=${encodeURIComponent(
          window.location.href
        )}" class="login-dynamic-link">登入</a>
      </span>
    </div>
  `
    : "";

  const html = `
     <div
          class="bg-img-hero position-relative profile-header py-5 text-white background-no-repeat background-size-cover"
        >
          <div class="container mt-5 mb-4 position-relative pt-5 z-index-1">
            <div class="row justify-content-center">
              <div class="col-lg-7">
                <div
                  class="doctor-info position-relative text-center text-white"
                >
                  <div
                    class="align-items-center d-flex fs-13 justify-content-center mb-2 star-rating"
                  >
                    <div class="star-rating__box">
                      ${ratingStarsHtml}
                    </div>
                    <div class="ms-2 review-numbers">
                      <span class="reviews-stats">${ratingCount}</span>
                      <span class="reviews-count">(${ratingLength})</span>
                    </div>
                  </div>
                  <h1 class="display-5 fw-semibold mb-5 text-white">
                    ${clinicName}
                  </h1>
                  <div class="fs-25 fw-semibold">
                    電話:
                    <span
                      class="bg-warning fw-bold px-3 py-1 rounded text-danger"
                      >${clinicPhone}</span
                    >
                  </div>
                  <div class="h-email text-decoration-underline">
                    ${clinicEmail}
                  </div>
                  <div class="fs-16 fw-medium l-spacing-1 mt-3 text-uppercase">
                    <i class="fa-solid fa-location-dot me-2"></i
                    >${clinicAddress}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="h-details_content py-5">
          <div class="container py-3">
            <div class="row">
              <div class="col-md-8 content pe-xxl-5">
                <div class="cs-content">
                  <div class="mb-5">
                    <h4 class="fw-semibold mb-4 text-capitalize">
                      <span class="underline position-relative text-primary"
                        >關於我們</span
                      >
                    </h4>
                    <div
                      class="card shadow border-0 text-inherit flex-fill w-100"
                    >
                      <div class="card-body p-4 p-sm-5">
                        <div class="readmore js-read-more" data-rm-words="80">
                          ${clinicMemo}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="mb-5">
                    <h4 class="fw-semibold mb-4 text-capitalize">
                      <span class="underline position-relative text-primary"
                        >診療科別</span
                      >
                    </h4>
                    <div class="g-4 justify-content-center row">
                    ${majorListImg}
                    </div>
                  </div>
                  <div class="mb-5">
                    <h4 class="fw-semibold mb-4 text-capitalize">
                      <span class="underline position-relative text-primary"
                        >醫生</span
                      >
                    </h4>
                    <div class="row g-4">
                      ${doctorList}
                    </div>
                  </div>
                  <div class="mb-5">
                    <h4 class="fw-semibold mb-4 text-capitalize">
                      <span class="underline position-relative text-primary"
                        >地理位置</span
                      >
                      <div class="text-center">
                        <p id="distance-result" class="mt-2 text-success"></p>
                      </div>
                    </h4>
                    <div id="map" class="border rounded-4"></div>
                  </div>
                </div>
              </div>
              <div class="col-md-4 sidebar">
                <div class="appointment-form">
                  <h4 class="fw-semibold mb-4 text-capitalize">
                    預約
                    <span class="underline position-relative text-primary"
                      >看診</span
                    >
                  </h4>
                  <ul class="nav nav-tabs mb-5" role="tablist">
                    <li class="nav-item" role="presentation">
                      <button
                        class="nav-link ms-0 active"
                        id="tab-one"
                        data-bs-toggle="tab"
                        data-bs-target="#tab-one-pane"
                        type="button"
                        role="tab"
                        aria-controls="tab-one-pane"
                        aria-selected="true"
                      >
                        初診
                      </button>
                    </li>
                    <li class="nav-item" role="presentation">
                      <button
                        class="nav-link"
                        id="tab-two"
                        data-bs-toggle="tab"
                        data-bs-target="#tab-two-pane"
                        type="button"
                        role="tab"
                        aria-controls="tab-two-pane"
                        aria-selected="false"
                      >
                        複診
                      </button>
                    </li>
                  </ul>
                  <div class="tab-content">
                    <div
                      class="tab-pane fade show active"
                      id="tab-one-pane"
                      role="tabpanel"
                      aria-labelledby="tab-one"
                      tabindex="0"
                    >
                      <form>
                        <div class="row g-4">
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">預約日期</label>
                              <input
                                type="date"
                                class="form-control datepicker"
                              />
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                            <label class="required">預約時段</label>
                             <select name="time" class="form-select">
                             </select>
                            </div>
                          </div>
                          <div class="col-sm-6">
                            <div class="form-group">
                              <label class="required">科別</label>
                              <select name="majorId" class="form-select">
                               ${majorList}
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-6">
                            <div class="form-group">
                              <label class="required">醫生</label>
                              <select name="doctorId" class="form-select">
                                ${doctorSelect}
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required"> 預約類型</label>
                              <select name="visitType" class="form-select">
                                <option value=0>
                                  醫師診間
                                </option>
                                <option value=1>
                                  線上看診
                                </option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">診所地點</label>
                              <select name="clinicLocation" class="form-select">
                                <option value=${clinicName}>${clinicName}</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">病況類別</label>
                              <select name="caseType" class="form-select">
                                <option value=0>近期發病</option>
                                <option value=1>長期病症</option>
                                <option value=2>報告查詢</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">描述病徵</label>
                              <textarea
                                name="notes" 
                                class="form-control"
                                placeholder="請填寫你的病徵"
                                rows="2"
                              ></textarea>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-check text-start">
                              <input
                                class="form-check-input"
                                type="checkbox"
                                value=""
                                id="flexCheckDefaultOne"
                              />
                              <label
                                class="form-check-label"
                                for="flexCheckDefaultOne"
                              >
                                我同意
                                <a href="#" class="text-decoration-underline"
                                  >條款 &amp; 隱私政策</a
                                >
                              </label>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <button
                              class="btn btn-primary btn-lg w-100"
                              type="submit"
                            >
                              預約看診
                            </button>
                          </div>
                        </div>
                        <hr />
                        ${loginPromptHtml}
                      </form>
                    </div>
                    <div
                      class="tab-pane fade"
                      id="tab-two-pane"
                      role="tabpanel"
                      aria-labelledby="tab-two"
                      tabindex="0"
                    >
                      <form>
                        <div class="row g-4">
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">預約日期</label>
                              <input
                                type="date"
                                class="form-control datepicker"
                              />
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">預約時段</label>
                              <select name="time" class="form-select">
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-6">
                            <div class="form-group">
                              <label class="required">科別</label>
                              <select name="majorId" class="form-select">
                               ${majorList}
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-6">
                            <div class="form-group">
                              <label class="required">醫生</label>
                              <select name="doctorId" class="form-select">
                                ${doctorSelect}
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required"> 預約類型</label>
                              <select name="visitType" class="form-select">
                                <option value=0>
                                  醫師診間
                                </option>
                                <option value=1>
                                  線上看診
                                </option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">診所地點</label>
                              <select name="Department" class="form-select">
                                <option value=${clinicName}>${clinicName}</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">病況類別</label>
                              <select name="caseType" class="form-select">
                                <option value=0>近期發病</option>
                                <option value=1>長期病症</option>
                                <option value=2>報告查詢</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-group">
                              <label class="required">描述病徵</label>
                              <textarea
                                name="notes" 
                                class="form-control"
                                placeholder="請填寫你的病徵"
                                rows="2"
                              ></textarea>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <div class="form-check text-start">
                              <input
                                class="form-check-input"
                                type="checkbox"
                                value=""
                                id="flexCheckDefaultTwo"
                              />
                              <label
                                class="form-check-label"
                                for="flexCheckDefaultTwo"
                              >
                                我同意
                                <a href="#" class="text-decoration-underline"
                                  >條款 &amp; 隱私政策</a
                                >
                              </label>
                            </div>
                          </div>
                          <div class="col-sm-12">
                            <button
                              class="btn btn-primary btn-lg w-100"
                              type="submit"
                            >
                              預約看診
                            </button>
                          </div>
                        </div>
                        <hr />
                        ${loginPromptHtml}
                      </form>
                    </div>
                  </div>
                </div>
                <div>
                  <h4 class="fw-semibold mb-4 text-capitalize">
                    <span class="underline position-relative text-primary">評論</span>
                  </h4>
                  <div class="card shadow border-0 text-inherit flex-fill w-100 mb-4">
                    <div class="card-body p-4">
                      ${commentHtml}
                      <div class="border-0 text-inherit flex-fill w-100">
                      <div>
                        <div id="star-rating" class="text-warning fs-4">
                          <i class="far fa-star" data-value="1"></i>
                          <i class="far fa-star" data-value="2"></i>
                          <i class="far fa-star" data-value="3"></i>
                          <i class="far fa-star" data-value="4"></i>
                          <i class="far fa-star" data-value="5"></i>
                        </div>
                      </div>
                      <div>
                        <textarea class="form-control bg-light border-0" id="comment-text" placeholder="請輸入您的評論內容" rows="3"></textarea>
                      </div>
  
                      <div class="text-end">
                        <button class="btn btn-primary" id="submit-comment" ${
                          !patientIdJson || !patientIdJson.patientId
                            ? "disabled"
                            : ""
                        }>送出評論</button>
                      </div>
                  </div>
                    </div>
                  </div>
                  
                </div>
              </div>
            </div>
          </div>
        </div>`;
  container.append(html);

  if (!patientIdJson || !patientIdJson.patientId) {
    $("#submit-comment")
      .prop("disabled", true)
      .addClass("disabled")
      .attr("title", "請先登入才能評論");
    $("#comment-text")
      .prop("disabled", true)
      .attr("placeholder", "請先登入才能輸入評論");
    $("#star-rating i").css("pointer-events", "none");
  }

  let selectedRating = 0;
  $("#star-rating")
    .off("click")
    .on("click", "i", function () {
      selectedRating = Number($(this).data("value"));
      $("#star-rating i").each(function (idx) {
        if (idx < selectedRating) {
          $(this).removeClass("far").addClass("fas");
        } else {
          $(this).removeClass("fas").addClass("far");
        }
      });
    });
}
$(document).on("submit", ".appointment-form form", function (e) {
  e.preventDefault();

  if (!patientIdJson || !patientIdJson.patientId) {
    alert("請先登入後再進行預約！");
    return;
  }

  const $form = $(this);

  // 表單驗證
  const agree = $form.find('input[type="checkbox"]').prop("checked");
  if (!agree) {
    alert("請先勾選同意條款");
    return;
  }

  const isFirstVisit = $("#tab-one-pane").hasClass("active") ? 1 : 0;

  const visitType =
    parseInt($form.find('select[name="visitType"]').val()) || null;
  const caseType =
    parseInt($form.find('select[name="caseType"]').val()) || null;

  let majorIdVal = $form.find('select[name="majorId"]').val();
  majorId = majorIdVal && majorIdVal !== "none" ? parseInt(majorIdVal) : null;

  doctorId = parseInt($form.find('select[name="doctorId"]').val()) || null;

  const formData = {
    appointmentDate: $form.find('input[type="date"]').val(),
    timePeriod: parseInt($form.find('select[name="time"]').val()),
    majorId: majorId,
    doctorId: doctorId,
    clinicId: parseInt(clinicId),
    firstVisit: isFirstVisit,
    notes: $form.find('textarea[name="notes"]').val(),
    appointmentType: visitType,
    selfCondition: caseType,
    patientId: patientIdJson.patientId,
  };

  $.ajax({
    url: "/ires-system/appointment/reserve",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify([formData]),
    success: function (res) {
      alert("預約成功");
      location.reload();
    },
    error: function (err) {
      alert("預約失敗：" + err.responseText);
    },
  });
});
