package com.fakeprofile.controller;

import com.fakeprofile.service.DetectionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/detections")
public class DetectionController {
    private final DetectionService service;
    public DetectionController(DetectionService s){service=s;}

    @PostMapping
    public Map<String,Object> analyze(Authentication a,@RequestBody Map<String,Object> body){
        return service.analyze(a.getName(),body);
    }

    @GetMapping("/my")
    public List<Map<String,Object>> my(Authentication a){return service.history(a.getName());}

    @GetMapping("/stats")
    public Map<String,Object> stats(Authentication a){return service.stats(a.getName());}
}
