package tw.idv.shen.web.patient.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;
import tw.idv.shen.web.patient.entity.Favorite;
import tw.idv.shen.web.patient.service.FavoriteService;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

	@Autowired
	private FavoriteService favoriteService;

	@Autowired
	private ClinicService clinicService;

	@GetMapping("/list")
	public ResponseEntity<?> getFavorites(HttpSession session) {
		Integer patientId = (Integer) session.getAttribute("patientId");
		if (patientId == null) {
			return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登入"));
		}

		List<Map<String, Object>> favorites = favoriteService.getFavoritesByPatientId(patientId);
		return ResponseEntity.ok(favorites);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addFavorite(@RequestBody Map<String, Integer> body, HttpSession session) {
		Integer clinicId = body.get("clinicId");
		System.out.println("clinicId = " + clinicId);
		Integer patientId = (Integer) session.getAttribute("patientId");
		System.out.println("💬 Session中的 patientId = " + session.getAttribute("patientId"));
		if (clinicId == null || patientId == null) {
			System.out.println("⚠️ 錯誤：clinicId 或 patientId 為空");
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "clinicId 或 patientId 為空"));
		}
		boolean success = favoriteService.addFavorite(patientId, clinicId);
		if (success) {
			return ResponseEntity.ok(Map.of("success", true));
		} else {
			return ResponseEntity.ok(Map.of("success", false, "message", "已加入收藏"));
		}
	}

	@DeleteMapping("/remove")
	public ResponseEntity<?> removeFavorite(@RequestParam("clinicId") Integer clinicId, HttpSession session) {
		Integer patientId = (Integer) session.getAttribute("patientId");
		if (clinicId == null || patientId == null) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "參數缺失"));
		}

		boolean success = favoriteService.removeFavorite(patientId, clinicId);
		if (success) {
			return ResponseEntity.ok(Map.of("success", true));
		} else {
			return ResponseEntity.ok(Map.of("success", false, "message", "尚未收藏"));
		}
	}

	@GetMapping("/all")
	public List<Map<String, Object>> getAllFavorites(HttpSession session) {
		Integer rawId = (Integer) session.getAttribute("patientId");
		if (rawId == null) {
			return Collections.emptyList(); // or throw new ResponseStatusException(...)
		}

		Long patientId = rawId.longValue();
		List<Favorite> favorites = favoriteService.findByPatientId(patientId);

		List<Map<String, Object>> result = new ArrayList<>();
		for (Favorite fav : favorites) {
			Map<String, Object> map = new HashMap<>();
			Integer clinicId = fav.getClinicId();
			map.put("clinicId", clinicId);

			Clinic clinic = clinicService.findById(clinicId); // 假設此方法吃 Integer
			String name = (clinic != null) ? clinic.getClinicName() : "未知診所";
			map.put("clinicName", name);

			result.add(map);
		}

		return result;
	}
}
