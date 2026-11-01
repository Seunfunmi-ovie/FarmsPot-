package ng.farmsPot.controllers;

import ng.farmsPot.config.AccessGuard;
import ng.farmsPot.dtos.responses.DashboardResponse;
import ng.farmsPot.services.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AccessGuard accessGuard;

    public DashboardController(DashboardService dashboardService, AccessGuard accessGuard) {
        this.dashboardService = dashboardService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/{farmerId}/dashboard")
    public DashboardResponse dashboard(@RequestAttribute("authFarmerId") int authFarmerId, @PathVariable int farmerId) {
        accessGuard.requireSelf(authFarmerId, farmerId);
        return dashboardService.getDashboard(farmerId);
    }
}