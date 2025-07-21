const params = new URLSearchParams(window.location.search);

const date = params.get("date") || "";
const startTime = params.get("startTime") || "";
const endTime = params.get("endTime") || "";
const majorId = params.get("majorId") || "";
const maxDistanceKm = params.get("maxDistanceKm") || "";
const towns = params.get("towns") || "";
const minRating = params.get("minRating") || "";
const userLat = sessionStorage.getItem("userLat") || "";
const userLng = sessionStorage.getItem("userLng") || "";
$(document).ready(function () {
  const date = params.get("date");
  const startTime = params.get("startTime");
  const endTime = params.get("endTime");
  const majorId = params.get("majorId");
  const maxDistanceKm = params.get("maxDistanceKm");
  const userLat = params.get("userLat");
  const userLng = params.get("userLng");
  const towns = params.get("towns");
  $(".ratys").raty({
    starType: "img",
    half: false,
    starOff: "/ires-system/Patient/assets/plugins/raty/images/star-off.png",
    starOn: "/ires-system/Patient/assets/plugins/raty/images/star-on.png",
    score: 0,
  });
  if (date && startTime && endTime) {
    $.ajax({
      url: "/ires-system/clinic/filter",
      type: "GET",
      data: {
        date,
        startTime,
        endTime,
        majorId,
        maxDistanceKm,
        userLat,
        userLng,
        towns,
        minRating,
      },
      success: function (clinics) {
        console.log(clinics);
        renderClinicList(clinics);
      },
      error: function () {
        alert("無法取得診所列表，請稍後再試！");
      },
    });
  }
  $.ajax({
    url: "/ires-system/major/list",
    method: "GET",
    success: function (data) {
      if (Array.isArray(data)) {
        if ($("#hospital_major")) {
          const select = $("#hospital_major");
          select.empty();
          select.append('<option value="all" selected>全部</option>');
          data.forEach(function (item) {
            const option = $("<option>", {
              value: item.majorId,
              text: item.majorName,
            });
            select.append(option);
          });
        }
        ul.append('<li data-value="all" class="is-highlighted">全部</li>');
        if ($("#hospital_major_list")) {
          const select = $("#hospital_major_list");
          const ul = $(".custom-select-options ul");
          select.empty();
          ul.empty();
          select.append('<option value="" selected>全部</option>');
          data.forEach(function (item) {
            const option = $("<option>", {
              value: item.majorId,
              text: item.majorName,
            });
            select.append(option);

            const li = $("<li>", {
              "data-value": item.majorId,
              text: item.majorName,
            });
            ul.append(li);
          });
        }
      }
    },
  });
  // 在 document ready 裡加上這段
});

function renderAddress(clinics) {
  const container = $("#address_town");
  container.empty();

  if (clinics.length === 0) {
    container.append("<p>無地區篩選條件</p>");
    return;
  }

  // 建立巢狀結構：{ city: { town: count } }
  const cityMap = {};
  clinics.forEach(function (clinic) {
    const city = clinic.addressCity || "未知縣市";
    const town = clinic.addressTown || "未知地區";
    if (!cityMap[city]) cityMap[city] = {};
    if (cityMap[city][town]) {
      cityMap[city][town]++;
    } else {
      cityMap[city][town] = 1;
    }
  });

  // 輸出縣市與地區 checkbox 結構
  Object.keys(cityMap).forEach(function (city, cityIndex) {
    const cityId = `city_${cityIndex}`;
    const cityHtml = `<h5 class="fw-bold mt-3 mb-2">${city}</h5>`;
    container.append(cityHtml);

    const towns = cityMap[city];
    Object.keys(towns).forEach(function (town, townIndex) {
      const checkboxId = `locality_${cityIndex}_${townIndex}`;
      const html = `
        <div class="form-check mb-1 ms-2">
          <input
            class="form-check-input"
            type="checkbox"
            value="${town}"
            id="${checkboxId}"
          />
          <label class="form-check-label" for="${checkboxId}">
            ${town}<span class="count ms-1">(${towns[town]})</span>
          </label>
        </div>
      `;
      container.append(html);
    });
  });
}
// 新增診所卡片產生函數
async function buildClinicCard(clinic) {
  const rating =
    clinic.rating == 0 || clinic.rating == null || clinic.rating == undefined
      ? `<span>尚未評論</span>`
      : `<span class="reviews-stats">${clinic.rating.toFixed(1)}</span>
         <span class="reviews-count">(${clinic.comments})</span>`;

  let ratingStarsHtml = "";
  for (let i = 1; i <= 5; i++) {
    const iconClass = i <= Math.round(clinic.rating) ? "fas" : "far";
    ratingStarsHtml += `<i class="${iconClass} fa-star"></i>`;
  }

  let distanceValue = "";
  if (
    userLat &&
    userLng &&
    clinic.latitude != null &&
    clinic.longitude != null
  ) {
    distanceValue = getDistanceFromLatLonInKm(
      parseFloat(userLat),
      parseFloat(userLng),
      parseFloat(clinic.latitude),
      parseFloat(clinic.longitude)
    );
  }
  const distanceHtml = distanceValue ? `距離${distanceValue}km` : "距離未知";

  const apiUrl = `/ires-system/callNumber/listByClinic?clinicId=${clinic.clinicId}&date=${date}`;
  let tableHtml = "";

  await $.ajax({
    url: apiUrl,
    method: "GET",
    async: false,
    success: function (data) {
      if (data.length > 0) {
        tableHtml += `<table style="border-collapse: separate; border-spacing: 0; font-size: 14px; margin-top: 10px; border: 1px solid #ddd; border-radius: 6px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.05); width: 100%;">`;
        tableHtml += "<tr>";
        data.forEach((item, index) => {
          const isLast = index === data.length - 1;
          const borderRightStyle = isLast
            ? ""
            : "border-right: 1px solid #ddd;";
          tableHtml += `<th style="padding: 10px; background-color: #f8f9fa; border-right: ${borderRightStyle} font-weight: 600; color: #333;">${item.doctor.doctorName}</th>`;
        });
        tableHtml += "</tr><tr>";
        data.forEach((item, index) => {
          const isLast = index === data.length - 1;
          const borderRightStyle = isLast
            ? ""
            : "border-right: 1px solid #ddd;";
          tableHtml += `<td style="padding: 10px; background-color: #ffffff; border-top: 1px solid #eee; border-right: ${borderRightStyle}; text-align: center;">${item.number}</td>`;
        });
        tableHtml += "</tr></table>";
      }
    },
  });

  return `
    <div class="border-0 card mb-3 overflow-hidden rounded card-hover shadow card-hover-sm card-hover">
      <div class="d-sm-flex hospital-list__item">
        <a href="hospital-details.html?clinicId=${
          clinic.clinicId
        }" class="bg-dark d-block flex-shrink-0 h-list__img overflow-hidden shadow">
          <img src="${
            clinic.profilePicture
              ? `data:image/jpeg;base64,${clinic.profilePicture}`
              : "static/img/iresclinic.png"
          }" alt="${
    clinic.clinicName
  }" style="aspect-ratio: 4 / 3; object-fit: cover; width: 100%;" />
        </a>
        <div class="d-flex" style="width: 100%;">
          <div class="flex-grow-1 p-4">
            <div class="align-items-center d-flex fs-13 mb-2 star-rating">
              <div class="d-flex text-warning">
                ${ratingStarsHtml}
              </div>
              <div class="ms-2 review-numbers">
                ${rating}
              </div>
            </div>
            <h5 class="fs-19 fw-bold h-title mb-1 overflow-hidden text-capitalize">
              <a class="text-dark" href="hospital-details.html">${
                clinic.clinicName
              }</a>
            </h5>
            <address class="fw-medium text-primary">
              <i class="fa-solid fa-location-dot me-2"></i>${
                clinic.addressCity
              }${clinic.addressTown}${clinic.addressRoad}
            </address>
            <div class="d-flex flex-wrap mt-3">
              <a class="border directions-link fs-13 py-1 rounded-5">
                <i class="fa-solid fa-compass me-2"></i>${distanceHtml}</a>
              <br/>
              <a href="tel:${
                clinic.phone
              }" class="border directions-link fs-13 py-1 rounded-5 ms-1">
                <i class="fa-solid fa-phone me-2"></i>${clinic.phone}</a>
            </div>
          </div>
          <div class="table-container" style="margin-left: auto; padding: 16px; max-width: 300px; overflow-x: auto;">
            ${tableHtml}
          </div>
        </div>
      </div>
    </div>
  `;
}

// 分頁功能的診所列表渲染
async function renderClinicList(clinics) {
  const container = $("#clinic-list");
  container.empty();

  const itemsPerPage = 10;
  let currentPage = 1;

  async function renderPage(page) {
    container.empty();
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, clinics.length);
    const pageClinics = clinics.slice(startIndex, endIndex);

    renderAddress(clinics); // 只需要呼叫一次

    if (pageClinics.length === 0) {
      container.append("<p>沒有符合條件的診所。</p>");
      return;
    }

    for (const clinic of pageClinics) {
      const html = await buildClinicCard(clinic);
      container.append(html);
    }

    renderPaginationControls(clinics.length, page);
  }

  function renderPaginationControls(totalItems, currentPage) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    if (totalPages <= 1) return;

    const paginationHtml = $(
      '<div class="pagination d-flex justify-content-center mt-4"></div>'
    );
    for (let i = 1; i <= totalPages; i++) {
      const pageBtn = $(
        `<button class="btn btn-outline-primary mx-1">${i}</button>`
      );
      if (i === currentPage) pageBtn.addClass("active");

      pageBtn.on("click", () => {
        renderPage(i);
        $("html, body").animate({ scrollTop: 0 }, 300);
      });

      paginationHtml.append(pageBtn);
    }
    container.append(paginationHtml);
  }

  renderPage(currentPage);
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

$("#sidebar-apply-filter-btn").on("click", function () {
  const date = params.get("date");
  const startTime = params.get("startTime");
  const endTime = params.get("endTime");
  let majorId = $("#hospital_major").val();
  const minRating = $(".ratys input[name='score']").val() || "";
  const maxDistanceKm = parseInt(
    $(".irs-to")
      .text()
      .replace(/[^0-9]/g, ""),
    10
  );
  if (majorId == "all") majorId = "";
  const selectedTowns = $(".sidebar-filters input[type='checkbox']:checked")
    .map(function () {
      return $(this).val();
    })
    .get();
  const town = selectedTowns.filter((t) => t && t.trim() !== "");
  const towns = town.join(",");
  const queryParams = new URLSearchParams({
    date,
    startTime,
    endTime,
    majorId,
    maxDistanceKm,
    userLat,
    userLng,
    minRating,
    towns,
  });

  window.location.href =
    "/ires-system/Patient/hospital.html?" + queryParams.toString();
});
