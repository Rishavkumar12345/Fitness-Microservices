package com.Microservice.AiServices.service;

import com.Microservice.AiServices.model.Activity;
import com.Microservice.AiServices.model.Recommendation;
import com.Microservice.AiServices.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityProcessListner {

    private final ActivityAiService aiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void activityProcess1(Activity activity) {
        try {
            // your existing processing logic, e.g. WebClient call
            activityProcess(activity);
        } catch (Exception ex) {
            log.error("Processing failed for activity id={} : {}", activity.getId(), ex.toString(), ex);

            // If this is a transient error you want to retry later, rethrow to allow retry,
            // otherwise send to DLQ / mark as rejected:
            throw new AmqpRejectAndDontRequeueException("Permanent failure processing activity", ex);
        }
    }

    @RabbitListener(queues = "activity.queue")
    public void activityProcess(Activity activity) {
        log.info("RabbitMq process Data: {}", activity.getId());
        //log.info("Generated recommendation from ai: {}", aiService.generateRecommendation(activity));
        Recommendation recommendation=aiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }
}
