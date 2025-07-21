// 報到按鈕啟動掃碼
document.addEventListener("click", e => {
  if (!e.target.classList.contains("checkIn")) return;

  //確認裝置支援攝影機
  if (!navigator.mediaDevices?.getUserMedia) {
    alert("不支援攝影機");
    return;
  }
  //開啟攝影機並播放
  navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } })
    .then(stream => {
      const video = document.createElement("video");
      video.srcObject = stream;
      video.setAttribute("playsinline", true);
      video.muted = true;
      video.autoplay = true;

      Object.assign(video.style, {
        position: "fixed",
        top: "0",
        left: "0",
        width: "100vw",
        height: "100vh",
        zIndex: "9999"
      });

      document.body.appendChild(video);
      video.play().catch(err => console.error("video 播放失敗", err));
      //建立 Canvas 擷取畫面
      const canvas = document.createElement("canvas");
      const ctx = canvas.getContext("2d", { willReadFrequently: true });

      video.addEventListener("loadedmetadata", () => {
        canvas.width = video.videoWidth || 640;
        canvas.height = video.videoHeight || 480;
        if (typeof jsQR !== "function") {
          alert("jsQR 未載入，無法執行掃描");
          return;
        }
        
        console.log("開始掃描");
        
        //掃描 QRCode
        const scan = () => {
          ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
          const imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
          const code = jsQR(imgData.data, canvas.width, canvas.height);

          if (code && typeof code.data === "string" && code.data.length === 19) {
            console.log("掃描成功:", code.data);
            //傳送報到資料
            fetch("/ires-system/checkIn", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ appointmentId: code.data })
            })
              .then(res => res.json())
              .then(data => {
                if (!data.success) {
                  alert(data.message || "報到失敗");
                  return;
                }

                const actions = e.target.closest(".actions");
                let btnGroup = actions.querySelector(".group.checkIn");
                if (!btnGroup) {
                  btnGroup = document.createElement("div");
                  btnGroup.className = "group checkIn";
                  actions.appendChild(btnGroup);
                }

                btnGroup.hidden = false;
                //清理資源
                const checkInBtn = e.target.closest(".checkIn");
                if (checkInBtn) checkInBtn.remove();

                localStorage.setItem("justCheckedInId", code.data);
                location.reload();
              })
              .finally(() => {
                stream.getTracks().forEach(t => t.stop());
                video.remove();
              });
          } else {//若無掃到有效 QRCode
            console.log("沒有掃到有效 QRCode");
            requestAnimationFrame(scan);
          }
        };

        scan();
      });
    })
    .catch(err => {
      console.error("相機錯誤", err);
      alert("請檢查權限設定");
    });
});