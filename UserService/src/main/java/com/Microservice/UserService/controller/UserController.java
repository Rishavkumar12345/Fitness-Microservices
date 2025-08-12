package com.Microservice.UserService.controller;

import com.Microservice.UserService.dto.RegisterRequest;
import com.Microservice.UserService.dto.UserResponse;
import com.Microservice.UserService.model.User;
import com.Microservice.UserService.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse>registerUser(@Valid @RequestBody RegisterRequest registerRequest){

        return ResponseEntity.ok(userService.getregisteruser(registerRequest));
    }

    @GetMapping("/getProfileById/{userId}")
    public ResponseEntity<UserResponse>getuser(@PathVariable String userId){
        return ResponseEntity.ok(userService.getuserbyid(userId));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.ValidateUser(userId));
    }
}
