package com.Microservice.AiServices.controller;

import com.Microservice.AiServices.model.Recommendation;
import com.Microservice.AiServices.repository.RecommendationRepository;
import com.Microservice.AiServices.service.RecommendationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/aiservice")
public class RecommendationController {

    @Autowired
    private RecommendationServices recommendationServices;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getRecommendationByUserId(@PathVariable String userId){
        return ResponseEntity.ok(recommendationServices.findRecommendationByUserId(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getRecommendationByactivityId(@PathVariable String activityId){
        return ResponseEntity.ok(recommendationServices.findRecommendationByActivityId(activityId));
    }
}
