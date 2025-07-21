document.addEventListener("DOMContentLoaded", () => {
    fetchAndRenderFavorites();

    // 若從其他頁面更新收藏，重新刷新
    window.addEventListener("storage", e => {
        if (e.key === "favoritesUpdated") {
            fetchAndRenderFavorites();
        }
    });
});

function fetchAndRenderFavorites() {
    fetch("/ires-system/favorites/list", {
        method: "GET",
        credentials: "include"
    })
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("favoritesList");
            const template = document.getElementById("favoriteTemplate");
            list.innerHTML = "";

            (Array.isArray(data) ? data : []).forEach(fav => {
                const cardFragment = template.content.cloneNode(true);
                const cardElement = cardFragment.querySelector(".favoriteCard");

                // 填入資料
                cardFragment.querySelector(".clinicName").textContent = fav.name || "診所名稱";
                cardFragment.querySelector(".clinicAddress").textContent = "地址：" + (fav.address || "未知");
                cardFragment.querySelector(".clinicId").textContent = `診所 ID：${fav.clinicId}`;

                // 電話連結
                const phoneLink = document.createElement("a");
                phoneLink.href = `tel:${fav.phone}`;
                phoneLink.textContent = fav.phone;
                const phoneContainer = cardFragment.querySelector(".clinicPhone");
                phoneContainer.textContent = "電話：";
                phoneContainer.appendChild(phoneLink);

                // 綁定刪除按鈕
                const removeBtn = cardFragment.querySelector(".removeBtn");
                removeBtn.addEventListener("click", () => {
                    fetch(`/ires-system/favorites/remove?clinicId=${fav.clinicId}`, {
                        method: "DELETE"
                    })
                        .then(r => r.json())
                        .then(result => {
                            if (result.success) {
                                cardElement.classList.add("fade-out");
                                setTimeout(() => cardElement.remove(), 300);
                            } else {
                                alert(result.message || "移除失敗");
                            }
                        });
                });

                list.appendChild(cardElement);
            });
        })
        .catch(err => {
            console.error("載入收藏清單失敗", err);
            alert("無法取得收藏診所列表");
        });
}