//顯示來自notification的資料
fetch("/ires-system/notification/patient")
  .then(res => res.json())
  .then(list => {
    const box = document.getElementById("notificationContainer");
    const template = document.getElementById("notificationTemplate");
    box.innerHTML = "";
    list.sort((a, b) => new Date(b.sentDatetime) - new Date(a.sentDatetime));
    list.forEach(data => {
      const clone = template.content.cloneNode(true);
      const item = clone.querySelector(".notifications");

      item.querySelector(".notificationType").textContent = data.type;
      item.querySelector(".clinicName").textContent = data.clinicName;
      item.querySelector(".message").textContent = data.message;

      item.querySelector(".deleteBtn").onclick = () => {
        fetch(`/ires-system/notification/${data.notificationId}`, {
          method: "DELETE"
        })
          .then(res => res.json())
          .then(result => {
            if (result.successful) {
              item.classList.add("fade-out");
              setTimeout(() => item.remove(), 500);
            }
          });
      };

      box.prepend(clone);
    });
  });