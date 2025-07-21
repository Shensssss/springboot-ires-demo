(function ($) {
  "use strict";

  //註冊
  $(document).ready(function () {
    $("#register").on("submit", function (e) {
      e.preventDefault();

      const password = $("#password").val();
      const confirmPassword = $("#confirm-password").val();
      if (password !== confirmPassword) {
        alert("密碼與確認密碼不一致，請重新輸入。");
        return;
      }

      const fileInput = $("#register_profile_picture")[0];
      const file = fileInput.files[0];

      if (file) {
        const reader = new FileReader();
        reader.onloadend = function () {
          const base64Data = reader.result.split(",")[1];
          sendForm(base64Data);
        };
        reader.readAsDataURL(file);
      } else {
        sendForm(null);
      }

      function sendForm(base64Image) {
        const formData = {
          name: $("#register_name").val(),
          phone: $("#register_phone").val(),
          email: $("#register_email").val(),
          password: $("#register_password").val(),
          birthday: $("#register_birthdate").val(),
          gender: parseInt($("#register_gender").val()),
          address: $("#register_address").val(),
          emergencyName: $("#register_emergency_name").val(),
          emergencyContent: $("#register_emergency_content").val(),
          relation: parseInt($("#register_relation").val()),
          bloodType: parseInt($("#register_blood_type").val()),
          notes: $("#register_notes").val(),
          status: 1,
          profilePicture: base64Image,
        };

        $.ajax({
          url: "/ires-system/patient/register",
          method: "POST",
          data: JSON.stringify(formData),
          contentType: "application/json",
          success: function (response) {
            if (response.successful) {
              alert("註冊成功！");
              window.location.href = "login.html";
            } else {
              alert("註冊失敗：" + response.message);
            }
          },
          error: function () {
            alert("發生錯誤，請稍後再試。");
          },
        });
      }
    });
  });
  //登入
  $(document).ready(function () {
    $("#login").on("submit", function (e) {
      e.preventDefault();

      const formData = {
        email: $("#login_email").val(),
        password: $("#login_password").val(),
      };

      $.ajax({
        url: "/ires-system/patient/login",
        method: "POST",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: function (response) {
          console.log(response);
          if (response.successful) {
            sessionStorage.setItem("patient", JSON.stringify(response));

            const urlParams = new URLSearchParams(window.location.search);
            const redirect = urlParams.get("redirect");

            if (redirect) {
              window.location.href = decodeURIComponent(redirect);
            } else {
              window.location.href = "index.html";
            }
          } else {
            alert("登入失敗：" + response.message);
          }
        },
        error: function () {
          alert("發生錯誤，請稍後再試。");
        },
      });
    });
  });

  /*-------------------------------------
	Background image
	-------------------------------------*/
  $("[data-bg-image]").each(function () {
    var img = $(this).data("bg-image");
    $(this).css({
      backgroundImage: "url(" + img + ")",
    });
  });

  /*-------------------------------------
    After Load All Content Add a Class
    -------------------------------------*/
  window.onload = addNewClass();

  function addNewClass() {
    $(".fxt-template-animation")
      .imagesLoaded()
      .done(function (instance) {
        $(".fxt-template-animation").addClass("loaded");
      });
  }

  /*-------------------------------------
    Toggle Class
    -------------------------------------*/
  $(".toggle-password").on("click", function () {
    $(this).toggleClass("fa-eye fa-eye-slash");
    var input = $($(this).attr("toggle"));
    if (input.attr("type") == "password") {
      input.attr("type", "text");
    } else {
      input.attr("type", "password");
    }
  });

  /*-------------------------------------
    Youtube Video
    -------------------------------------*/
  if ($.fn.YTPlayer !== undefined && $("#fxtVideo").length) {
    $("#fxtVideo").YTPlayer({ useOnMobile: true });
  }

  /*-------------------------------------
    Vegas Slider
    -------------------------------------*/
  if ($.fn.vegas !== undefined && $("#vegas-slide").length) {
    var target_slider = $("#vegas-slide"),
      vegas_options = target_slider.data("vegas-options");
    if (typeof vegas_options === "object") {
      target_slider.vegas(vegas_options);
    }
  }

  /*-------------------------------------
    OTP Form (Focusing on next input)
    -------------------------------------*/
  $("#otp-form .otp-input").keyup(function () {
    if (this.value.length == this.maxLength) {
      $(this).next(".otp-input").focus();
    }
  });

  /*-------------------------------------
	Social Animation
	-------------------------------------*/
  $("#fxt-login-option >ul >li").hover(function () {
    $("#fxt-login-option >ul >li").removeClass("active");
    $(this).addClass("active");
  });

  /*-------------------------------------
    Preloader
    -------------------------------------*/
  $("#preloader").fadeOut("slow", function () {
    $(this).remove();
  });
})(jQuery);
