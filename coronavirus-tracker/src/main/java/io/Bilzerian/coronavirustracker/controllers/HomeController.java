package io.Bilzerian.coronavirustracker.controllers;

import io.Bilzerian.coronavirustracker.models.LocationStats;
import io.Bilzerian.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// not a rest controller
@Controller
public class HomeController {
    @Autowired
    CoronaVirusDataService coronaVirusDataService;
    @GetMapping("/")
    public String home(Model model)
    {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotal()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiff()).sum();
        int totalDeaths = allStats.stream().mapToInt(stat -> stat.getDeaths()).sum();
        int totalNewDeaths = allStats.stream().mapToInt(stat -> stat.getDeathsDiff()).sum();
        int totalRecovered = allStats.stream().mapToInt(stat -> stat.getRecovered()).sum();
        int totalNewRecovered = allStats.stream().mapToInt(stat -> stat.getRecoveredDiff()).sum();
        int totalActiveCases = (allStats.stream().mapToInt(stat -> stat.getActiveCases()).sum());
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        model.addAttribute("totalDeaths",totalDeaths);
        model.addAttribute("totalNewDeaths",totalNewDeaths);
        model.addAttribute("totalRecovered",totalRecovered);
        model.addAttribute("totalNewRecovered",totalNewRecovered);
        model.addAttribute("totalActiveCases",totalActiveCases);
        return "home";
    }
}
