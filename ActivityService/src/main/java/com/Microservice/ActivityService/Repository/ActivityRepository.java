package com.Microservice.ActivityService.Repository;

import com.Microservice.ActivityService.dto.ActivityResponse;
import com.Microservice.ActivityService.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity,String> {

    List<Activity> findByUserId(String userId);
}
