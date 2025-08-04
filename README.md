# iRes 線上智能預約系統

本系統是針對診所與病患所開發的預約系統，期望整合小診所們線上預約服務，省去僅能傳統電話掛號、不確定人數、在現場久候的看診困境。 病患可以隨時線上預約，並看見即時叫號，收到即將到號通知再前往診所，節省等候時間，提升看診體驗，且可服務特殊群體(如：不方便撥電話的聾啞人士)。 診所透過數位化管理，亦可降低人力掛號負擔，增進效能，並可在網路上提升曝光度，促進病患前往看診意願。

> ⚠️ 本系統為 Tibame 職訓班專題作品，僅供學習與展示使用。原以Spring MVC架構撰寫，此份為重構Spring Boot版本。

---

## 開發團隊

吳應賢、林鼎鈞、沈美均、陳慧慈、蔡僑寬、黃慶𪰧

---

## 我的負責項目
診所端之
- 📍 **診所資料管理**：基本資訊、營業時間維護
- 👨‍⚕️ **醫師資料管理**：基本資訊維護
- 🧑‍⚕️ **病患資料管理**：  
  - 基本資訊查詢 
  - 備註查詢與變更
  - 預約歷史紀錄查詢

---

## 使用技術

### 後端技術

- Java 17
- Spring Boot (含Spring MVC)
- Hibernate / JPA  
- MySQL  
- Maven  
- Apache Tomcat
- RESTful API 設計
- JSON（資料交換格式）

### 前端技術

- HTML / CSS  
- JavaScript (含jQuery / AJAX)
- Bootstrap

---

## 專案建置與執行方式

### 1. 系統需求

- JDK 17+
- Maven 3.5+
- MySQL 8.0.X
- Apache Tomcat（或 Spring Boot 3.X內建伺服器）

### 2. 專案啟動步驟

```bash
# 1. 匯入專案（建議使用 Spring Tool Suite 4）
# 2. 匯入資料庫結構與初始資料
# 3. 修改 application.properties 內的資料庫連線資訊
# 4. 確保 pom.xml 相依關係正常 (maven → update project)
# 5. 啟動專案
# 6. 瀏覽器開啟 http://localhost:8080/ires-system
