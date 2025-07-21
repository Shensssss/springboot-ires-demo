package tw.idv.shen.web.clinic.controller;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;

@Controller
@RequestMapping("/clinic")
public class SearchClinic {

    @Autowired
    private ClinicService service;

    @GetMapping("/filter")
    @ResponseBody
    public List<Clinic> filterClinics(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) String towns,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxDistanceKm,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLng,  
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime
    ) {
        return service.filterClinics(majorId, towns, minRating, maxDistanceKm, userLat, userLng, date, startTime, endTime);
    }
    
    @GetMapping("/id/{clinicId}")
    @ResponseBody
    public Clinic findClinic(@PathVariable Integer clinicId) {
        return service.findById(clinicId);
    }
}