package com.example.SensorDataBackendProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String getMainPage(Model model) {
        model.addAttribute("view", "home.html");
        model.addAttribute("showHeader", true);
        return "main"; // Zwraca główny plik HTML
    }

    @GetMapping("/sensor-data")
    public String getSensorDataPage(Model model) {
        model.addAttribute("view", "sensor-data.html");
        model.addAttribute("showHeader", false);
        return "main";
    }

    @GetMapping("/alarms")
    public String getAlarmPage(Model model) {
        model.addAttribute("view", "alarmy.html");
        model.addAttribute("showHeader", false);
        return "main";
    }

    @GetMapping("/predictions")
    public String getPredictionPage(Model model) {
        model.addAttribute("view", "predykcja.html");
        model.addAttribute("showHeader", false);
        return "main";
    }

    @GetMapping("/set-alarm")
    public String getSetAlarmPage(Model model) {
        model.addAttribute("view", "ustaw-alarm.html");
        model.addAttribute("showHeader", false);
        return "main";
    }

    @GetMapping("/alarm-details")
    public String getAlarmDetailsPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("alarmId", id); // Przekaż ID alarmu do widoku
        model.addAttribute("view", "alarm-details.html");
        model.addAttribute("showHeader", false);
        return "main"; // Zwraca główny plik HTML (szablon), w którym osadzisz alarm-details.html
    }

    @GetMapping("/reports")
    public String getReportsPage(Model model) {
        model.addAttribute("view", "reports.html");
        model.addAttribute("showHeader", false);
        return "main";
    }
}
