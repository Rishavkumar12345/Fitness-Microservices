package com.Microservice.ActivityService.service;

import com.Microservice.ActivityService.Repository.ActivityRepository;
import com.Microservice.ActivityService.dto.ActivityRequest;
import com.Microservice.ActivityService.dto.ActivityResponse;
import com.Microservice.ActivityService.models.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserValidationService userValidationService;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public ActivityResponse trackUserActivity(ActivityRequest activityRequest) {

        boolean validate=userValidationService.ValidateUser(activityRequest.getUserId());

        if(!validate){
            throw  new RuntimeException("Invalid user ID "+activityRequest.getUserId());
        }

        Activity activity=Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .duration(activityRequest.getDuration())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();

        Activity savedactivity=activityRepository.save(activity);

        //publish to RabbitMq to AI processing
        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,savedactivity);
        }catch(Exception e){
            log.error("Fail to publish RabbitMq",e);
        }

        return mapToResponse(savedactivity);
    }

    public ActivityResponse mapToResponse(Activity savedactivity){
        ActivityResponse activityResponse=new ActivityResponse();
        activityResponse.setId(savedactivity.getId());
        activityResponse.setUserId(savedactivity.getUserId());
        activityResponse.setType(savedactivity.getType());
        activityResponse.setDuration(savedactivity.getDuration());
        activityResponse.setCaloriesBurned(savedactivity.getCaloriesBurned());
        activityResponse.setStartTime(savedactivity.getStartTime());
        activityResponse.setAdditionalMetrics(savedactivity.getAdditionalMetrics());
        activityResponse.setCreatedAt(savedactivity.getCreatedAt());
        activityResponse.setUpdatedAt(savedactivity.getUpdatedAt());

        return activityResponse;
    }

    public List<ActivityResponse> tackuser(String userId) {

        List<Activity> activity=activityRepository.findByUserId(userId);

        return activity.stream().map(this::mapToResponse).collect(Collectors.toList());


    }

    public ActivityResponse getactivity(String activityId) {

        return activityRepository.findById(activityId).map(this::mapToResponse)
                .orElseThrow(()->new RuntimeException("Activity is not found"));
    }
}
