package tw.idv.shen.web.patient.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@Controller
@RequestMapping("patient")
public class PatientRegister {

    @Autowired
    private PatientService service;

    @PostMapping("register")
    @ResponseBody
    public Patient register(@RequestBody Patient patient) {
    	patient.setMessage("success");
    	patient.setSuccessful(true);
        return service.register(patient);
    }
}