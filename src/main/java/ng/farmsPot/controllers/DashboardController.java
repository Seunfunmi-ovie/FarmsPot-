package ng.farmsPot.controllers;

import ng.farmsPot.dtos.responses.DashboardResponse;
import ng.farmsPot.services.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{farmerId}/dashboard")
    public DashboardResponse dashboard(@PathVariable int farmerId) {
        return dashboardService.getDashboard(farmerId);
    }
}