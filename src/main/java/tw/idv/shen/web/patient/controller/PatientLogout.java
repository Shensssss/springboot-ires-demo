package tw.idv.shen.web.patient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("patient")
public class PatientLogout {

    @PostMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除所有 session
        return "redirect:/Patient/login.html"; // 或 return null 由前端跳轉
    }
}