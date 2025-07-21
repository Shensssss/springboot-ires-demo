google.maps.event.addDomListener(window, "load", init);

function init() {
  var mapOptions = {
    zoom: 17,
    center: new google.maps.LatLng(25.0821576, 121.5750363),
  };

  var mapElement = document.getElementById("map");

  var map = new google.maps.Map(mapElement, mapOptions);

  var marker = new google.maps.Marker({
    position: new google.maps.LatLng(25.0821576, 121.5750363),
    map: map,
    title: "台北菁英診所 內湖分店",
  });

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      function (position) {
        const userLat = position.coords.latitude;
        const userLng = position.coords.longitude;

        const userLocation = new google.maps.LatLng(userLat, userLng);
        const clinicLocation = new google.maps.LatLng(25.0821576, 121.5750363);

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
