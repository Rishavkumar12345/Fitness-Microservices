package com.Microservice.AiServices.service;

import com.Microservice.AiServices.model.Recommendation;
import com.Microservice.AiServices.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServices {

    @Autowired
    private RecommendationRepository recommendationRepository;

    public List<Recommendation> findRecommendationByUserId(String userId) {

        return recommendationRepository.findByUserId(userId);

    }

    public Recommendation findRecommendationByActivityId(String activityId) {

        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(()->new RuntimeException("No Activity with activity Id "+activityId));
    }
}
