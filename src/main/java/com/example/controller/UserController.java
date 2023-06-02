package com.example.controller;

import com.example.dto.JwtResponse;
import com.example.dto.MessageResponse;
import com.example.dto.UserDto;
import com.example.entity.FriendRequest;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 4800)
@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public List<UserDto> getAllUsers() {
        logger.info("UserController. method getAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/user/{username}")
    public UserDto getUser(@PathVariable(name = "username") String username) {
        logger.info("UserController. method getUser");
        return userService.getUser(username);
    }

    @GetMapping("/user/my_friends/{id}")
    public List<UserDto> getAllFriends(@PathVariable(name = "id") Long id) {
        logger.info("UserController. method getAllFriends");
        return userService.getAllFriends(id);
    }

    @GetMapping("/user/incoming_friendship/{id}")
    public List<FriendRequest> incomingFriendship(@PathVariable(name = "id") Long id) {
        return userService.incomingFriendship(id);
    }

    @GetMapping("/user/outgoing_friendship/{id}")
    public List<FriendRequest> outgoingFriendship(@PathVariable(name = "id") Long id) {
        return userService.outgoingFriendship(id);
    }

    @PostMapping("/user/send_friendship/{id}")
    public String sendFriendship(JwtResponse jwtResponse,
                                 @PathVariable(name = "id") Long id) {
        userService.sendFriendship(jwtResponse.getId(), id);
        return "friend request sent";
    }

    @PatchMapping("/user/cancel_friendship/{id}")
    public String cancelFriendship(@PathVariable(name = "id") Long id) {
        return userService.cancelFriendship(id);
    }

    @PatchMapping("/user/accept_friendship/{id}")
    public String acceptFriendship(@PathVariable(name = "id") Long id) {
        return userService.acceptFriendship(id);
    }

    @PatchMapping("/user/decline_friendship/{id}")
    public String declineFriendship(@PathVariable(name = "id") Long id) {
        return userService.declineFriendship(id);
    }

    @PatchMapping("/user/delete_friend/{id}")
    public String deleteFriend(JwtResponse jwtResponse,
                               @PathVariable(name = "id") Long id) {
        return userService.deleteFriend(jwtResponse.getId(), id);
    }

    @GetMapping("/user/my_subscriptions/{id}")
    public List<UserDto> getAllSubscriptions(@PathVariable(name = "id") Long id) {
        logger.info("UserController. method getAllSubscriptions");
        return userService.getAllSubscriptions(id);
    }

    @PostMapping("/user/subscribe/{id}")
    public String subscribe(JwtResponse jwtResponse,
                            @PathVariable(name = "id") Long id) {
        userService.subscribe(jwtResponse.getId(), id);
        return "you are subscribed to user updates";
    }

    @PostMapping("/user/unsubscribe/{id}")
    public String unsubscribe(JwtResponse jwtResponse,
                            @PathVariable(name = "id") Long id) {
        if (userService.unsubscribe(jwtResponse.getId(), id)) {
            return "you unsubscribed";
        } else {
            return "subscription not found";
        }
    }






    @GetMapping("/all")
    public MessageResponse allAccess() {
        return new MessageResponse("Server is up.....");
    }

    @GetMapping("/greeting")
    @PreAuthorize("isAuthenticated()")
    public MessageResponse userAccess() {
        return new MessageResponse("Congratulations! You are an authenticated user.");
    }

}
