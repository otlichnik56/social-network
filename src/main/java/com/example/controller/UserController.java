package com.example.controller;

import com.example.dto.MessageResponse;
import com.example.dto.UserDto;
import com.example.entity.FriendRequest;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 4800)
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<UserDto> getAllUsers() {
        logger.info("UserController. method getAllUsers");
        return userService.getAllUsers();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}")
    public UserDto getUser(@PathVariable(name = "username") String username) {
        logger.info("UserController. method getUser");
        return userService.getUser(username);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_friends")
    public List<UserDto> getAllFriends(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method getAllFriends");
        return userService.getAllFriends(userDetails.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/incoming_friendship")
    public List<FriendRequest> incomingFriendship(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.incomingFriendship(userDetails.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/outgoing_friendship")
    public List<FriendRequest> outgoingFriendship(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.outgoingFriendship(userDetails.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/send_friendship/{id}")
    public String sendFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable(name = "id") Long id) {
        userService.sendFriendship(userDetails.getUsername(), id);
        return "friend request sent";
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/cancel_friendship/{id}")
    public String cancelFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable(name = "id") Long id) {
        return userService.cancelFriendship(userDetails.getUsername(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/accept_friendship/{id}")
    public String acceptFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable(name = "id") Long id) {
        return userService.acceptFriendship(userDetails.getUsername(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/decline_friendship/{id}")
    public String declineFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable(name = "id") Long id) {
        return userService.declineFriendship(userDetails.getUsername(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/delete_friend/{id}")
    public String deleteFriend(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable(name = "id") Long id) {
        return userService.deleteFriend(userDetails.getUsername(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_subscriptions")
    public List<UserDto> getAllSubscriptions(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method getAllSubscriptions");
        return userService.getAllSubscriptions(userDetails.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscribe/{id}")
    public String subscribe(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable(name = "id") Long id) {
        userService.subscribe(userDetails.getUsername(), id);
        return "you are subscribed to user updates";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unsubscribe/{id}")
    public String unsubscribe(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable(name = "id") Long id) {
        if (userService.unsubscribe(userDetails.getUsername(), id)) {
            return "you unsubscribed";
        } else {
            return "subscription not found";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chat/{username}")
    public String getChat(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable(name = "username") String username) {
        String chatId = userService.getChat(userDetails.getUsername(), username);
        if (chatId == null) {
            return "you cannot chat with this user";
        } else {
            return "chat number with user " + username + ": " + chatId;
        }
    }




    @GetMapping("/all")
    public MessageResponse allAccess() {
        return new MessageResponse("Server is up.....");
    }

    @GetMapping("/greeting")
    @PreAuthorize("isAuthenticated()")
    public MessageResponse userAccess(@AuthenticationPrincipal UserDetails userDetails) {
        return new MessageResponse("Congratulations! You are an authenticated user - " + userDetails.getUsername());
    }

}
