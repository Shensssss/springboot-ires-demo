new Swiper(".swiper-container", {
  slidesPerView: 3.4,
  centeredSlides: true,
  spaceBetween: 30,
  autoplay: true,
  loop: true,
  breakpoints: {
    1300: {
      slidesPerView: 3.4,
    },
    992: {
      slidesPerView: 2.5,
    },
    768: {
      slidesPerView: 1.5,
    },
    576: {
      slidesPerView: 1,
      spaceBetween: 10,
    },
    0: {
      slidesPerView: 1,
      spaceBetween: 10,
    },
  },
});
