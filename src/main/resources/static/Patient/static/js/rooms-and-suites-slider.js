// Initialize Swiper
new Swiper(".room-slider", {
  autoplay: false,
  slidesPerView: 3, // Show 3 slides at a time
  spaceBetween: 30,
  slidesPerGroup: 3, // Slide 3 slides at once
  loop: true,
  pagination: {
    el: ".swiper-pagination",
    clickable: true,
  },
  navigation: {
    nextEl: ".swiper-button-next",
    prevEl: ".swiper-button-prev",
  },
  pagination: false,
  breakpoints: {
    1200: {
      slidesPerView: 3,
      slidesPerGroup: 3,
    },
    768: {
      slidesPerView: 2,
      slidesPerGroup: 2,
    },
    0: {
      slidesPerView: 1,
      slidesPerGroup: 1,
    },
  },
});
