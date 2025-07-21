$(document).ready(function () {
  let activeDatepicker = null; // Store the currently active datepicker

  $(".datepicker")
    .datepicker({
      weekStart: 1,
      color: "#dd0000",
    })
    .on("show", function (e) {
      // Close the previously active datepicker, if any
      if (activeDatepicker && activeDatepicker !== this) {
        $(activeDatepicker).datepicker("hide");
      }
      // Update the active datepicker
      activeDatepicker = this;
    })
    .on("hide", function (e) {
      // Clear the active datepicker if it's the one being hidden
      if (activeDatepicker === this) {
        activeDatepicker = null;
      }
    });

  // Click event handler for hero slider to close the datepicker
  $(".swiper-container-wrapper").on("click", function () {
    if (activeDatepicker) {
      $(activeDatepicker).datepicker("hide");
    }
  });
});
