$(function () {
  var wind = $(window);

  wow = new WOW({
    boxClass: "wow",
    animateClass: "animated",
    offset: 200,
    mobile: false,
    live: false,
  });
  wow.init();
  // ---------- background change -----------
  var pageSection = $(".bg-img");
  pageSection.each(function (indx) {
    if ($(this).attr("data-background")) {
      $(this).css(
        "background-image",
        "url(" + $(this).data("background") + ")"
      );
    }
  });

  // ----------- side menu -----------
  $(".side_menu_btn").on("click", function () {
    $(this).toggleClass("active");
    $(".side_overlay").toggleClass("show");
    // $(".side_menu").toggleClass("show");
  });

  $(".side_menu .clss").on("click", function () {
    $(".side_overlay").toggleClass("show");
    // $(".side_menu").toggleClass("show");
  });

  // ---------- to top -----------

  wind.on("scroll", function () {
    var bodyScroll = wind.scrollTop(),
      toTop = $("#to_top");

    if (bodyScroll > 700) {
      toTop.addClass("show");
    } else {
      toTop.removeClass("show");
    }
  });

  $("#to_top").click(function () {
    $("html, body").animate(
      {
        scrollTop: 0,
      },
      0
    );
    return false;
  });

  // -------- fav-btn --------
  $(".fav-btn").on("click", function () {
    $(this).toggleClass("active");
  });

  // -------- cls --------
  $(".cls").on("click", function () {
    $(this).parent().fadeOut();
  });

  // ---------- tooltip -----------
  var tooltipTriggerList = [].slice.call(
    document.querySelectorAll('[data-bs-toggle="tooltip"]')
  );
  var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl);
  });
});

// ------------ Preloader -----------
$(function () {
  const svg = document.getElementById("svg");
  const tl = gsap.timeline();
  const curve = "M0 502S175 272 500 272s500 230 500 230V0H0Z";
  const flat = "M0 2S175 1 500 1s500 1 500 1V0H0Z";

  tl.to(".loader-wrap-heading .load-text , .loader-wrap-heading .cont", {
    delay: 1.5,
    y: -100,
    opacity: 0,
  });
  tl.to(svg, {
    duration: 0.5,
    attr: { d: curve },
    ease: "power2.easeIn",
  }).to(svg, {
    duration: 0.5,
    attr: { d: flat },
    ease: "power2.easeOut",
  });
  tl.to(".loader-wrap", {
    y: -1500,
  });
  tl.to(".loader-wrap", {
    zIndex: -1,
    display: "none",
  });
  // tl.from(
  //     "header",
  //     {
  //         y: 200,
  //     },
  //     "-=1.5"
  // );
  // tl.from(
  //     "header .container",
  //     {
  //         y: 40,
  //         opacity: 0,
  //         delay: 0.3,
  //     },
  //     "-=1.5"
  // );
});

$(window).on("load", function () {
  // ------------ Preloader -----------
  var body = $("body");
  body.addClass("loaded");
  setTimeout(function () {
    body.removeClass("loaded");
  }, 1500);
});

// ------------ mousecursor scripts -----------
$(function () {
  function mousecursor() {
    if ($("body")) {
      const e = document.querySelector(".cursor-inner"),
        t = document.querySelector(".cursor-outer");
      let n,
        i = 0,
        o = !1;
      (window.onmousemove = function (s) {
        o ||
          (t.style.transform =
            "translate(" + s.clientX + "px, " + s.clientY + "px)"),
          (e.style.transform =
            "translate(" + s.clientX + "px, " + s.clientY + "px)"),
          (n = s.clientY),
          (i = s.clientX);
      }),
        $("body").on("mouseenter", "a, .cursor-pointer", function () {
          e.classList.add("cursor-hover"), t.classList.add("cursor-hover");
        }),
        $("body").on("mouseleave", "a, .cursor-pointer", function () {
          e.classList.remove("cursor-hover"),
            t.classList.remove("cursor-hover");
        }),
        // $("body").on("mouseenter", ".swiper-wrapper.curs-scroll", function () {
        //     e.classList.add("cursor-scroll"), t.classList.add("cursor-scroll")
        // }), $("body").on("mouseleave", ".swiper-wrapper.curs-scroll", function () {
        //     $(this).is("a") && $(this).closest(".cursor-pointer").length || (e.classList.remove("cursor-scroll"), t.classList.remove("cursor-scroll"))
        // }),

        (e.style.visibility = "visible"),
        (t.style.visibility = "visible");
    }
  }

  $(function () {
    mousecursor();
  });
});

$(function () {
  // gsap.registerPlugin(ScrollTrigger, ScrollSmoother);

  $(".side_menu_btn").on("click", function () {
    gsap.to(".side_menu", {
      opacity: 1,
      visibility: "visible",
      duration: 0.5,
      ease: "power2.out",
      zIndex: 999,
    });
    gsap.to(".main_link", {
      x: 0,
      y: 0,
      scale: 1,
      opacity: 1,
      delay: 0.5,
      stagger: {
        amount: 0.5,
        from: "start",
      },
    });
    gsap.to(".menu-info", {
      opacity: 1,
      scale: 1,
      visibility: "visible",
      duration: 0.5,
      delay: 1.5,
      ease: "power2.out",
    });
    gsap.to("#scrollsmoother-container", {
      opacity: 0.1,
      scale: 0.97,
      visibility: "visible",
      duration: 1,
      delay: 0.1,
      ease: "power2.out",
    });
  });

  $(".side_menu .clss").on("click", function () {
    gsap.to(".side_menu", {
      opacity: 0,
      visibility: "hidden",
      duration: 0.5,
      ease: "power2.out",
      // delay: 0.01,
      zIndex: -1,
    });
    gsap.to(".main_link", {
      x: 0,
      y: 0,
      scale: 1.3,
      opacity: 0,
      stagger: {
        amount: 0.5,
        from: "start",
      },
    });
    gsap.to(".menu-info", {
      opacity: 0,
      scale: 1,
      visibility: "visible",
      duration: 0.5,
      delay: 0.1,
      ease: "power2.out",
    });
    gsap.to("#scrollsmoother-container", {
      opacity: 1,
      scale: 1,
      visibility: "visible",
      duration: 1,
      delay: 0.1,
      ease: "power2.out",
    });
  });
});

$(document).ready(function () {
  const currentUrl = encodeURIComponent(window.location.href);
  $(".login-dynamic-link").each(function () {
    $(this).attr("href", `./login.html?redirect=${currentUrl}`);
  });
  // 根據登入狀態切換 Menu 或 Login
  const patient = sessionStorage.getItem("patient");
  if (patient) {
    $("#login-link").addClass("d-none");
    $("#menu-link").removeClass("d-none");
  } else {
    $("#login-link").removeClass("d-none");
    $("#menu-link").addClass("d-none");
  }
  $.ajax({
    url: "/ires-system/major/list",
    method: "GET",
    success: function (data) {
      if (Array.isArray(data)) {
        const select = $(".major-select");
        const ul = $(".custom-select-options ul");

        select.empty();
        ul.empty();

        select.append('<option value="all" selected>全部</option>');
        ul.append('<li data-value="all" class="is-highlighted">全部</li>');

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
    },
  });

  $(".distance-range").on("input", function () {
    $(this)
      .closest(".booking-form-item")
      .find(".distance-value")
      .text($(this).val());
  });

  flatpickr(".datepicks_val", {
    dateFormat: "Y-m-d",
    defaultDate: new Date(),
    minDate: "today",
  });

  //-------------time select ---------------
  flatpickr(".timepicker_start", {
    enableTime: true,
    noCalendar: true,
    dateFormat: "H:i",
    minTime: "00:00",
    maxTime: "23:59",
    defaultDate: "00:00",
  });

  flatpickr(".timepicker_end", {
    enableTime: true,
    noCalendar: true,
    dateFormat: "H:i",
    minTime: "00:00",
    maxTime: "23:59",
    defaultDate: "23:59",
  });

  $(".clinicFilterForm").on("submit", function (e) {
    e.preventDefault();

    const date = $(".datepicks_val").val();
    const startTime = $(".timepicker_start").val();
    const endTime = $(".timepicker_end").val();
    let majorId = $(".major-select").val();
    const maxDistanceKm = $(".distance-value").text();
    const userLat = sessionStorage.getItem("userLat") || "";
    const userLng = sessionStorage.getItem("userLng") || "";
    if (majorId === "all") {
      majorId = "";
    }

    const query = new URLSearchParams({
      date,
      startTime,
      endTime,
      majorId,
      maxDistanceKm,
      userLat,
      userLng,
    });

    window.location.href =
      "/ires-system/Patient/hospital.html?" + query.toString();
  });

  clinicLinks();
  // 登出功能
});
$(".logout-link").on("click", function (e) {
  e.preventDefault();
  if (confirm("確定要登出嗎？")) {
    $.post("/ires-system/patient/logout")
      .done(function () {
        sessionStorage.removeItem("patient");
        window.location.href = "./index.html";
      })
      .fail(function () {
        alert("登出失敗，請稍後再試！");
      });
  }
});
function clinicLinks() {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");
  date = `${yyyy}-${mm}-${dd}`;

  const startTime = "00:00";
  const endTime = "23:59";
  const distance = 10;

  const linkMap = {
    "#findAll": "",
    "#findPeds": 1,
    "#findDds": 2,
    "#findOph": 3,
    "#findObgyn": 4,
    "#findDerm": 5,
    "#findEnt": 6,
    "#findPsych": 7,
    "#findTcm": 8,
    "#findNutrition": 9,
    "#findPt": 10,
  };

  for (const [selector, majorId] of Object.entries(linkMap)) {
    $(selector).on("click", function (e) {
      e.preventDefault();
      const query = new URLSearchParams({
        date,
        startTime,
        endTime,
        majorId,
        distance,
      });
      window.location.href =
        "/ires-system/Patient/hospital.html?" + query.toString();
    });
  }
}

const routes = {
  主頁: "index.html",
  預約紀錄: "reservation.html",
  我的收藏: "favorites.html",
  通知消息: "notification.html",
  帳戶設定: "account.html",
};

document.querySelectorAll(".main_links li a").forEach((link) =>
  link.addEventListener("click", (e) => {
    e.preventDefault();
    const url = routes[link.textContent.trim()];
    if (url) window.location.href = url;
  })
);
