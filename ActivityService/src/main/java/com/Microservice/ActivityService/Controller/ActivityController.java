package com.Microservice.ActivityService.Controller;

import com.Microservice.ActivityService.dto.ActivityRequest;
import com.Microservice.ActivityService.dto.ActivityResponse;
import com.Microservice.ActivityService.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/ActivityService")
@RequiredArgsConstructor
public class ActivityController {

    @Autowired
    private final ActivityService activityService;

    @PostMapping("/registeractivity")
    public ResponseEntity<ActivityResponse>trackactivity(@RequestBody ActivityRequest activityRequest){

        return ResponseEntity.ok(activityService.trackUserActivity(activityRequest));
    }

    @GetMapping("/getUserActivity")
    public ResponseEntity<List<ActivityResponse>>getuseractivity(@RequestHeader("X-User-ID") String userId){

        return ResponseEntity.ok(activityService.tackuser(userId));
    }

    @GetMapping("/getActivity/{activityId}")
    public ResponseEntity<ActivityResponse>getactivity(@PathVariable String activityId){

        return ResponseEntity.ok(activityService.getactivity(activityId));
    }
}
