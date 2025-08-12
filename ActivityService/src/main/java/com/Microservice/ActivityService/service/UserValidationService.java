package com.Microservice.ActivityService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    @Autowired
    private WebClient UserServiceWebClient;

    public boolean ValidateUser(String userId){

        try{
            return UserServiceWebClient.get()
                    .uri("api/user/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }catch(WebClientResponseException e){
            if(e.getStatusCode()== HttpStatus.NOT_FOUND)
                throw new RuntimeException("UserID not found");
            else if(e.getStatusCode()==HttpStatus.BAD_REQUEST)
                throw new RuntimeException("Invalid request");
        }

        return false;
    }
}
