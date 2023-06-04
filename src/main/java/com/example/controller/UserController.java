package com.example.controller;

import com.example.dto.MessageResponse;
import com.example.dto.UserDto;
import com.example.entity.FriendRequest;
import com.example.entity.Publication;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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


    @Operation(summary = "Список всех пользователей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto[].class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        logger.info("UserController. method getAllUsers");
        return userService.getAllUsers();
    }

    @Operation(summary = "Найти пользователя по имени",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}")
    public UserDto getUser(@PathVariable(name = "username") String username) {
        logger.info("UserController. method getUser");
        return userService.getUser(username);
    }

    @Operation(summary = "Список друзей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto[].class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_friends")
    public List<UserDto> getAllFriends(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method getAllFriends");
        return userService.getAllFriends(userDetails.getUsername());
    }

    @Operation(summary = "Исходящие заявки в друзья",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto[].class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/incoming_friendship")
    public List<UserDto> incomingFriendship(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method incomingFriendship. get all my incoming friendship");
        return userService.incomingFriendship(userDetails.getUsername());
    }

    @Operation(summary = "Входящие заявки в друзья",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto[].class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/outgoing_friendship")
    public List<UserDto> outgoingFriendship(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method outgoingFriendship. get all my outgoing friendship");
        return userService.outgoingFriendship(userDetails.getUsername());
    }

    @Operation(summary = "Отправить заявку в друзья",
            description = "введите ID пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "заявка успешно отправлена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/send_friendship/{id}")
    public String sendFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable(name = "id") Long id) {
        logger.info("UserController. method sendFriend. send friendship from user with id = " + id);
        userService.sendFriendship(userDetails.getUsername(), id);
        return "friend request sent";
    }

    @Operation(summary = "Отменить заявку в друзья",
            description = "введите ID пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "заявка успешно отменена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/cancel_friendship/{id}")
    public String cancelFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable(name = "id") Long id) {
        logger.info("UserController. method cancelFriend. cancel friendship from user with id = " + id);
        return userService.cancelFriendship(userDetails.getUsername(), id);
    }

    @Operation(summary = "Принять заявку в друзья",
            description = "введите ID пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "заявка успешно принята",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/accept_friendship/{id}")
    public String acceptFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable(name = "id") Long id) {
        logger.info("UserController. method acceptFriend. accept friendship from user with id = " + id);
        return userService.acceptFriendship(userDetails.getUsername(), id);
    }

    @Operation(summary = "Отклонить заявку в друзья",
            description = "введите ID пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "заявка успешно отклонена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/decline_friendship/{id}")
    public String declineFriendship(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable(name = "id") Long id) {
        logger.info("UserController. method declineFriendship. decline friendship from user with id = " + id);
        return userService.declineFriendship(userDetails.getUsername(), id);
    }

    @Operation(summary = "Удаление из друзей",
            description = "введите ID пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "пользователь успешно удален из списка друзей",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "пользователь не найдена"
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/delete_friend/{id}")
    public String deleteFriend(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable(name = "id") Long id) {
        logger.info("UserController. method deleteFriend. delete friend with id = " + id);
        return userService.deleteFriend(userDetails.getUsername(), id);
    }

    @Operation(summary = "Получить список подписок",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список подписок",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto[].class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_subscriptions")
    public List<UserDto> getAllSubscriptions(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("UserController. method getAllSubscriptions");
        return userService.getAllSubscriptions(userDetails.getUsername());
    }

    @Operation(summary = "Подписаться на пользователя",
            description = "введите id пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "вы подписаны на пользователя",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscribe/{id}")
    public String subscribe(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable(name = "id") Long id) {
        logger.info("UserController. method subscribe. id user = " + id);
        userService.subscribe(userDetails.getUsername(), id);
        return "you are subscribed to user updates";
    }

    @Operation(summary = "Отписаться на пользователя",
            description = "введите id пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "вы отписались",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "пользователь не найден"
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/unsubscribe/{id}")
    public String unsubscribe(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable(name = "id") Long id) {
        logger.info("UserController. method unsubscribe. id user = " + id);
        if (userService.unsubscribe(userDetails.getUsername(), id)) {
            return "you unsubscribed";
        } else {
            return "subscription not found";
        }
    }

    @Operation(summary = "Получение id чата для переписки с другом",
            description = "введите имя пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "получен id чата",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "пользователь не найден"
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chat/{username}")
    public String getChat(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable(name = "username") String username) {
        logger.info("UserController. method getChat with user = " + username);
        String chatId = userService.getChat(userDetails.getUsername(), username);
        if (chatId == null) {
            return "you cannot chat with this user";
        } else {
            return "chat number with user " + username + ": " + chatId;
        }
    }


}
