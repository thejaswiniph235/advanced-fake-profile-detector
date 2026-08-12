package com.fakeprofile.controller;

import com.fakeprofile.model.Detection;
import com.fakeprofile.repository.DetectionRepository;
import com.fakeprofile.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository users;
    private final DetectionRepository detections;

    public AdminController(UserRepository u, DetectionRepository d) {
        users = u;
        detections = d;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        long total = detections.count();

        long bots = detections.findAll()
                .stream()
                .filter(d -> "BOT".equals(d.getPrediction()))
                .count();

        long suspicious = detections.findAll()
                .stream()
                .filter(d -> "SUSPICIOUS".equals(d.getPrediction()))
                .count();

        Map<String, Object> result = new HashMap<>();

        result.put("users", users.count());
        result.put("detections", total);
        result.put("bots", bots);
        result.put("suspicious", suspicious);
        result.put("genuine", Math.max(0, total - bots - suspicious));

        return result;
    }

    @GetMapping("/users")
    public List<Map<String, Object>> allUsers() {
        return users.findAll()
                .stream()
                .map(u -> {
                    Map<String, Object> result = new HashMap<>();

                    result.put("id", u.getId());
                    result.put("email", u.getEmail());
                    result.put("role", u.getRole().name());
                    result.put("createdAt", u.getCreatedAt().toString());

                    return result;
                })
                .toList();
    }

    @GetMapping("/detections")
    public List<Map<String, Object>> allDetections() {
        return detections.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Detection::getCreatedAt
                        ).reversed()
                )
                .limit(100)
                .map(d -> {
                    Map<String, Object> result = new HashMap<>();

                    result.put("id", d.getId());
                    result.put("username", d.getUsername());
                    result.put("prediction", d.getPrediction());
                    result.put("riskScore", d.getRiskScore());
                    result.put("user", d.getUser().getEmail());
                    result.put("createdAt", d.getCreatedAt().toString());

                    return result;
                })
                .toList();
    }
}
