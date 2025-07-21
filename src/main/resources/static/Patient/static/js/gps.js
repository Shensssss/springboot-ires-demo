// ------------ localposition ---------------
$(document).ready(function () {
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
        $("#user-location").html(
          '<i class="fas fa-map-marker-alt"></i> ' + address
        );
      } else {
        console.error("無法獲取地址", data.status);
      }
    }).fail(function () {
      console.error("Geocoding API 請求錯誤");
    });
  }

  // 讓使用者手動選擇 Google 地圖位置
  $("#user-location").on("click", function () {
    let mapsUrl = "https://www.google.com/maps/search/?api=1";
    window.open(mapsUrl, "_blank");
  });
});
