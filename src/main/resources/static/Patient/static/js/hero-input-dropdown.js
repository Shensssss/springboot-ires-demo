(function () {
  "use strict";
  var allSelect = Array.from(document.querySelectorAll(".custom-select"));

  allSelect.forEach(function (select, index) {
    var selectElm = select.querySelector("select");
    selectElm.style.display = "none";
    var options = Array.from(selectElm.children);
    var mainContainer = document.createElement("div");
    var ul = document.createElement("ul");
    mainContainer.className = "custom-select-options";
    if (select.classList.contains("icon-imgs")) {
      mainContainer.classList.add("has-images");
    }
    mainContainer.setAttribute(
      "data-select-head",
      "custom-select" + index + ""
    );

    var SelectHead = document.createElement("div");
    createHead(SelectHead);
    var liContainer = "";
    appendingOptions(options);

    var activeHead = select.querySelector(".custom-select-active");
    var SelectOptionsList = document.querySelector(
      ".custom-select-options[data-select-head=" + "custom-select" + index + "]"
    );

    function createHead(headElem) {
      var DefaultSelectedValue =
        selectElm.querySelector("[selected]") ||
        selectElm.querySelector("option");
      Object.assign(headElem, {
        className: "custom-select-active",
        innerHTML: DefaultSelectedValue.textContent,
      });
      headElem.setAttribute("data-select-index", "custom-select" + index + "");
      select.appendChild(headElem);
      document.body.appendChild(mainContainer);
      mainContainer.appendChild(ul);
    }

    function appendingOptions(list) {
      var code = "";
      for (var x = 0; x < list.length; x++) {
        if (list[x].tagName == "OPTION") {
          code =
            "<li data-value=" +
            list[x].value +
            ">" +
            list[x].textContent +
            "</li>";
        } else {
          var head =
            "<li class='label'><h3>" +
            list[x].getAttribute("label") +
            "</h3></li>";
          var lists = "";
          var optgroupChildren = Array.from(list[x].children);
          for (var i = 0; i < optgroupChildren.length; i++) {
            lists +=
              "<li data-value=" +
              optgroupChildren[i].value +
              ">" +
              optgroupChildren[i].textContent +
              "</li>";
          }
          code = head + lists;
        }

        if (select.classList.contains("icon-imgs") && list[x].dataset.imgUrl) {
          code =
            "<li data-value=" +
            list[x].value +
            "> <div class='custom-select-img-container'><img src='" +
            list[x].dataset.imgUrl +
            "'></div>" +
            list[x].textContent +
            "</li>";
        }
        liContainer += code;
      }

      ul.innerHTML = liContainer;
    }

    function adjustSize() {
      mainContainer.style.width = SelectHead.offsetWidth + "px";
      mainContainer.style.left = SelectHead.getBoundingClientRect().left + "px";
      mainContainer.style.top =
        SelectHead.getBoundingClientRect().bottom +
        5 +
        window.pageYOffset +
        "px";
    }

    function debounce(func, wait, immediate) {
      var timeout;
      return function () {
        var context = this,
          args = arguments;
        var later = function () {
          timeout = null;
          if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
      };
    }

    window.addEventListener("resize", function () {
      debounce(adjustSize());
    });

    activeHead.addEventListener("click", function (e) {
      var thisSelectIndex = this.getAttribute("data-select-index");
      var thisList = document.querySelector(
        ".custom-select-options[data-select-head=" + thisSelectIndex + "]"
      );
      if (thisList.classList.contains("is-active")) {
        thisList.classList.remove("is-active");
      } else {
        e.stopPropagation();
        var allLists = Array.from(
          document.querySelectorAll(".custom-select-options")
        );
        allLists.forEach(function (list) {
          if (list.classList.contains("is-active")) {
            list.classList.remove("is-active");
          }
        });
        adjustSize();
        SelectOptionsList.classList.add("is-active");
      }
      ul.querySelector("li[data-value]").classList.add("is-highlighted");
    });

    SelectOptionsList.addEventListener("click", function (e) {
      function findParentWithData(elem) {
        try {
          if (elem.getAttribute("data-value")) return elem;
        } catch (e) {
          return false;
        }
        while (!elem.getAttribute("data-value")) {
          return findParentWithData(elem.parentNode);
        }
      }
      var li = findParentWithData(e.target);
      if (li !== false) {
        var selectedValue = findParentWithData(e.target).textContent;
        SelectOptionsList.classList.remove("is-active");
        SelectHead.textContent = selectedValue;
        selectElm.value = findParentWithData(e.target).getAttribute(
          "data-value"
        );
        var event = new Event("change");
        selectElm.dispatchEvent(event);
      }
    });
  });

  var allOptions = Array.from(
    document.querySelectorAll(".custom-select-options")
  );

  document.addEventListener("keydown", function (event) {
    var activeOption =
      document.querySelector(".custom-select-options.is-active ul") || "";
    var activeUl =
      document.querySelector(".custom-select-options.is-active") || "";
    if (activeOption) {
      var selcted = activeUl.getAttribute("data-select-head");
      var selectedLi = activeOption.querySelector(
        "li[data-value].is-highlighted"
      );
      var SelectHead = document.querySelector(
        "[data-select-index=" + selcted + "]"
      );
      var selectElm = SelectHead.parentElement.previousSibling;
      if (event.keyCode == 40) {
        if (selectedLi && selectedLi.nextSibling !== null) {
          if (selectedLi.nextSibling.getAttribute("data-value")) {
            selectedLi.classList.remove("is-highlighted");
            selectedLi.nextSibling.classList.add("is-highlighted");
          }
        }
      } else if (event.keyCode == 38) {
        if (selectedLi.previousSibling !== null) {
          if (selectedLi.previousSibling.getAttribute("data-value")) {
            selectedLi.classList.remove("is-highlighted");
            selectedLi.previousSibling.classList.add("is-highlighted");
          }
        }
      } else if (event.keyCode == 13) {
        var selectElement = document.querySelector(
          ".custom-select-options.is-active li.is-highlighted"
        );
        SelectHead.textContent = selectElement.textContent;
        selectElm.value = selectElement.getAttribute("data-value");
        activeUl.classList.remove("is-active");
      }
    }
  });

  document.addEventListener("click", function () {
    allOptions.forEach(function (option) {
      option.classList.remove("is-active");
    });
  });
})();

window.initDropdownHighlight = function (
  ulSelector = ".custom-select-options ul"
) {
  const ul = document.querySelector(ulSelector);
  if (!ul) return;

  const firstItem = ul.querySelector("li[data-value]");
  if (firstItem) {
    firstItem.classList.add("is-highlighted");
  }
};
