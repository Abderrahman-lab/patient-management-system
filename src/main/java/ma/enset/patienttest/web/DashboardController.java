package ma.enset.patienttest.web;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.getStatistiques();
        model.addAllAttributes(stats);
        return "dashboard";
    }
}
