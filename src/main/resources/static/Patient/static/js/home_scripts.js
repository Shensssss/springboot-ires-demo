let date = "";
$(function () {
  // --------- portfolio effect ---------
  $(".tc-portfolio-st7 .works .item").on("mouseenter", function () {
    $(this).siblings().removeClass("active");
    $(this).addClass("active");
  });
});

// ------------ swiper sliders -----------
$(document).ready(function () {
  // ------------ tc-header-st7 marq-slider -----------
  var swiper = new Swiper(".tc-header-st7 .mark-sliders .slider", {
    slidesPerView: "auto",
    spaceBetween: 150,
    centeredSlides: true,
    pagination: false,
    navigation: false,
    mousewheel: false,
    keyboard: true,
    speed: 20000,
    allowTouchMove: false,
    autoplay: {
      delay: 1,
    },
    loop: true,
  });

  var userLocation = sessionStorage.getItem("userLocation");

  if (userLocation) {
    $("#user-location").html(
      '<i class="fas fa-map-marker-alt"></i> ' + userLocation
    );
  } else {
    $("#user-location").html(
      '<i class="fas fa-map-marker-alt"></i> 獲取位置中...'
    );
    getCurrentLocation();
  }

  // 取得 GPS 位置
  function getCurrentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        function (position) {
          var latitude = position.coords.latitude;
          var longitude = position.coords.longitude;
          getAddressFromCoords(latitude, longitude);
        },
        function (error) {
          console.error("無法獲取位置", error);
          $("#user-location").html(
            '<i class="fas fa-map-marker-alt"></i> 無法獲取位置'
          );
        }
      );
    } else {
      console.log("瀏覽器不支持地理位置 API");
    }
  }

  // 透過 Google Geocoding API 取得地址
  function getAddressFromCoords(lat, lng) {
    var geocodingApiUrl = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=AIzaSyBgRkDJeajEe4nbQgupP7yvSBayWSoIQAg`;

    $.getJSON(geocodingApiUrl, function (data) {
      if (data.status === "OK") {
        var address = data.results[0].formatted_address;
        sessionStorage.setItem("userLocation", address);
        sessionStorage.setItem("userLat", lat);
        sessionStorage.setItem("userLng", lng);
        $("#user-location").html(
          '<i class="fas fa-map-marker-alt"></i> ' + address
        );
        // 重新載入診所列表
        loadClinics("#all_clinc");
        loadClinics("#peds", 1);
        loadClinics("#dds", 2);
        loadClinics("#oph", 3);
        loadClinics("#obgyn", 4);
        loadClinics("#derm", 5);
        loadClinics("#ent", 6);
        loadClinics("#psych", 7);
        loadClinics("#tcm", 8);
        loadClinics("#nutrition", 9);
        loadClinics("#pt", 10);
      } else {
        console.error("無法獲取地址");
      }
    }).fail(function (e) {
      console.error(e);
    });
  }

  // 讓使用者手動選擇 Google 地圖位置
  $("#user-location").on("click", function () {
    let mapsUrl = "https://www.google.com/maps/search/?api=1";
    window.open(mapsUrl, "_blank");
  });

  // 附近診所取得
  loadClinics("#all_clinc");
  loadClinics("#peds", 1);
  loadClinics("#dds", 2);
  loadClinics("#oph", 3);
  loadClinics("#obgyn", 4);
  loadClinics("#derm", 5);
  loadClinics("#ent", 6);
  loadClinics("#psych", 7);
  loadClinics("#tcm", 8);
  loadClinics("#nutrition", 9);
  loadClinics("#pt", 10);
});

// ------------ gsap scripts -----------
$(function () {
  gsap.registerPlugin(ScrollTrigger, ScrollSmoother);

  // create the smooth scroller FIRST!
  const smoother = ScrollSmoother.create({
    content: "#scrollsmoother-container",
    smooth: 1.5,
    normalizeScroll: true,
    ignoreMobileResize: true,
    effects: true,
    //preventDefault: true,
    //ease: 'power4.out',
    //smoothTouch: 0.1,
  });
});

function loadClinics(selector, majorId = null) {
  const rawLat = sessionStorage.getItem("userLat");
  const rawLng = sessionStorage.getItem("userLng");

  const userLat = rawLat && !isNaN(rawLat) ? parseFloat(rawLat) : null;
  const userLng = rawLng && !isNaN(rawLng) ? parseFloat(rawLng) : null;

  if (userLat === null || userLng === null) {
    console.log("無使用者定位經緯度");
  }

  const url = majorId
    ? `/ires-system/clinicMajor/list?majorId=${majorId}`
    : "/ires-system/clinicMajor/list";

  $.ajax({
    url,
    method: "GET",
    success: function (clinics) {
      if (Array.isArray(clinics) && clinics.length > 0) {
        const $wrapper = $(`${selector} .swiper-wrapper`);
        $wrapper.empty();
        for (let i = 0; i < clinics.length; i += 3) {
          let groupHtml = "";
          for (let j = i; j < i + 3 && j < clinics.length; j++) {
            const clinic = clinics[j];
            const rating =
              clinic.rating == 0 ||
              clinic.rating == null ||
              clinic.rating == undefined
                ? `尚未評論`
                : `<i class="fa-solid fa-star" style="color: gold"></i>
                    ${clinic.rating.toFixed(1)} Rating`;
            let distance = "未知";
            if (
              userLat !== null &&
              userLng !== null &&
              clinic.latitude !== null &&
              clinic.longitude !== null
            ) {
              distance = getDistanceFromLatLonInKm(
                userLat,
                userLng,
                clinic.latitude,
                clinic.longitude
              );
            }
            let callNumbersHtml = "載入中...";
            const apiUrl = `/ires-system/callNumber/listByClinic?clinicId=${clinic.clinicId}&date=${date}`;

            $.ajax({
              url: apiUrl,
              method: "GET",
              async: false, // 確保同步拿到資料（或可改 async callback 處理）
              success: function (data) {
                if (Array.isArray(data) && data.length > 0) {
                  callNumbersHtml = `
                  <table style="border-collapse: collapse; border: 1px solid #ccc; font-size: 14px; margin-top: 0px; text-align: center;">
                    <tr>${data
                      .map(
                        (d) =>
                          `<th style="border: 1px solid #ccc; padding: 4px;">${d.doctor.doctorName}</th>`
                      )
                      .join("")}</tr>
                    <tr>${data
                      .map(
                        (d) =>
                          `<td style="border: 1px solid #ccc; padding: 4px;">${d.number}</td>`
                      )
                      .join("")}</tr>
                  </table>
                `;
                } else {
                  callNumbersHtml = `
    <div>
      <table style="border-collapse: collapse; border: 1px solid #ccc; font-size: 14px; margin-top: 0px; text-align: center;">
        <tr><td style="border: 1px solid #ccc; padding: 4px;">無</td></tr>
        <tr><td style="border: 1px solid #ccc; padding: 4px;">尚未開診</td></tr>
      </table>
    </div>`;
                }
              },
              error: function () {
                callNumbersHtml = "載入失敗";
              },
            });
            groupHtml += `
              <div class="swiper-slide card">
              <a href="hospital-details.html?clinicId=${clinic.clinicId}">
                <div class="card-img">
                  <img src="${
                    clinic.profilePicture
                      ? `data:image/jpeg;base64,${clinic.profilePicture}`
                      : "static/img/iresclinic.png"
                  }" alt="診所圖片" />
                </div>
                <div class="card-content">
                  <span><h3>${clinic.clinicName}</h3></span>
                  <div class="rating">
                    ${rating} |
                    <div class="calender">
                      <svg width="23" height="23" viewBox="0 0 32 32" fill="none">
                        <path d="M10.6667 4C11.4031 4 12 4.59696 12 5.33333V8C12 8.73637 11.4031 9.33333 10.6667 9.33333C9.9303 9.33333 9.33334 8.73637 9.33334 8V5.33333C9.33334 4.59696 9.9303 4 10.6667 4Z" fill="black"/>
                        <path d="M21.3333 4C22.0697 4 22.6667 4.59696 22.6667 5.33333V8C22.6667 8.73637 22.0697 9.33333 21.3333 9.33333C20.5969 9.33333 20 8.73637 20 8V5.33333C20 4.59696 20.5969 4 21.3333 4Z" fill="black"/>
                        <rect x="4.75" y="6.75" width="22.5" height="20.5" rx="3.25" stroke="black" stroke-width="1.5"/>
                      </svg>
                    </div>
                    目前號碼: ${callNumbersHtml}
                  </div>
                  <div class="price-and-button">
                    <div>
                      <div class="price">${distance} <span>km</span></div>
                    </div>
                    <div style="vertical-align: bottom; display: flex; gap: 10px;">
                      <a href="hospital-details.html?clinicId=${
                        clinic.clinicId
                      }&date=${date}&time=0" class="view-room-btn">
                        上午<br>
                        <span style="font-size: 12px;">${clinic.morning}</span>
                      </a>
                      <a href="hospital-details.html?clinicId=${
                        clinic.clinicId
                      }&date=${date}&time=1" class="view-room-btn">
                        下午<br>
                        <span style="font-size: 12px;">${
                          clinic.afternoon
                        }</span>
                      </a>
                      <a href="hospital-details.html?clinicId=${
                        clinic.clinicId
                      }&date=${date}&time=2" class="view-room-btn">
                        晚上<br>
                        <span style="font-size: 12px;">${clinic.night}</span>
                      </a>
                    </div>
                  </div>
                </div>
                </a>
              </div>
            `;
          }
          $wrapper.append(groupHtml);
        }

        new Swiper(`${selector} .room-slider`, {
          slidesPerView: 3,
          spaceBetween: 20,
          slidesPerGroup: 3,
          navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
          },
          breakpoints: {
            0: { slidesPerView: 1, slidesPerGroup: 1 },
            768: { slidesPerView: 2, slidesPerGroup: 2 },
            1200: { slidesPerView: 3, slidesPerGroup: 3 },
          },
        });
      } else {
        $(selector).closest("#rooms-suites").hide();
        return;
      }
    },
  });
}

function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
  const R = 6371;
  const dLat = deg2rad(lat2 - lat1);
  const dLon = deg2rad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(deg2rad(lat1)) *
      Math.cos(deg2rad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const d = R * c;
  return d.toFixed(2);
}

function deg2rad(deg) {
  return deg * (Math.PI / 180);
}

$(document).ready(function () {});
