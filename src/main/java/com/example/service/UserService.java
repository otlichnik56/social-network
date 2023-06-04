package com.example.service;

import com.example.dto.Status;
import com.example.dto.UserDto;
import com.example.entity.FriendRequest;
import com.example.entity.Subscription;
import com.example.entity.User;
import com.example.repository.FriendRequestRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final String ANSWER_NOT_FOUND = "record not found";
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final SubscriptionRepository subscriptionRepository;


    public UserService(UserRepository userRepository, FriendRequestRepository friendRequestRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.subscriptionRepository = subscriptionRepository;
    }


    /**
     * Получить список всех пользователей
     * @return
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::userToUserDto).collect(Collectors.toList());
    }

    /**
     * Получить пользователя по имени
     * @param username
     * @return
     */
    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        userToUserDto(user);
        return userToUserDto(user);
    }


    // работа с друзьями

    /**
     * Получить список друзей
     * @param username
     * @return
     */
    public List<UserDto> getAllFriends(String username) {
        Long id = getUserId(username);
        List<FriendRequest> friendIdList = friendRequestRepository.findMyFriendsId(id);
        if (friendIdList == null) {
            return null;
        } else {
            return friendIdList.stream().map(friendRequest -> {
                User user = userRepository.findById(friendRequest.getFromUser()).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Просмотреть исходящие заявки в друзья
     * по id пользователя
     * @param username
     * @return
     */
    public List<UserDto> incomingFriendship(String username) {
        Long id = getUserId(username);
        List<FriendRequest> friendRequestList = friendRequestRepository.findAllByFromUser(id);
        if (friendRequestList == null) {
            return null;
        } else {
            return friendRequestList.stream().map(friendRequest -> {
                User user = userRepository.findById(friendRequest.getToUser()).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Просмотреть входящие заявки в друзья
     * по id пользователя
     * @param username
     * @return
     */
    public List<UserDto> outgoingFriendship(String username) {
        Long id = getUserId(username);
        List<FriendRequest> friendRequestList = friendRequestRepository.findAllByToUser(id);
        if (friendRequestList == null) {
            return null;
        } else {
            return friendRequestList.stream().map(friendRequest -> {
                User user = userRepository.findById(friendRequest.getFromUser()).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Отправить заявку в друзья
     * по id отправителя и id получателя
     * @param username
     * @param toUser
     */
    public void sendFriendship(String username, Long toUser) {
        Long fromUser = getUserId(username);
        if (checkUser(toUser)) {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setFromUser(fromUser);
            friendRequest.setToUser(toUser);
            friendRequest.setStatus(Status.ACTIVE);
            friendRequest.setDateRequest(LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            subscribe(username, toUser);
        }
    }

    /** отменить заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String cancelFriendship(String username, Long id) {
        Long fromUser = getUserId(username);
        FriendRequest friendRequest = friendRequestRepository.findByFromUserAndToUser(fromUser, id).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else if (Objects.equals(fromUser, friendRequest.getFromUser())) {
            friendRequest.setStatus(Status.NOT_ACTIVE);
            friendRequestRepository.save(friendRequest);
            return "friend request canceled";
        } else {
            return ANSWER_NOT_FOUND;
        }
    }

    /** Принять заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String acceptFriendship(String username, Long id) {
        Long toUser = getUserId(username);
        FriendRequest friendRequest = friendRequestRepository.findByFromUserAndToUser(id, toUser).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else if (Objects.equals(toUser, friendRequest.getToUser())) {
            friendRequest.setStatus(Status.CONFIRMATION);
            friendRequest.setDateResponse(LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            subscribe(username, friendRequest.getFromUser());
            return "you have one new friend";
        } else {
            return ANSWER_NOT_FOUND;
        }
    }

    /** оклонить заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String declineFriendship(String username, Long id) {
        Long toUser = getUserId(username);
        FriendRequest friendRequest = friendRequestRepository.findByFromUserAndToUser(id, toUser).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else if (Objects.equals(toUser, friendRequest.getToUser())) {
            friendRequest.setStatus(Status.NOT_ACTIVE);
            friendRequest.setDateResponse(LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            return "friend request rejected";
        } else {
            return ANSWER_NOT_FOUND;
        }
    }

    /** Удалить из друзей
     * по id пользователя и id удаляемого
     * @param username
     * @param toUser
     * @return
     */
    public String deleteFriend(String username, Long toUser) {
        Long fromUser = getUserId(username);
        if (checkUser(toUser) && fromUser != null) {
            FriendRequest friendRequest = friendRequestRepository.findFriend(fromUser, toUser).orElse(null);
            if (friendRequest == null) {
                return ANSWER_NOT_FOUND;
            } else {
                friendRequest.setStatus(Status.NOT_ACTIVE);
                friendRequestRepository.save(friendRequest);
                unsubscribe(username, toUser);
                return "successfully unfriended";
            }
        } else {
            return ANSWER_NOT_FOUND;
        }
    }


    // работа с подписками

    /** Получить всех на кого подписан
     * По своему id
     * @param username
     * @return
     */
    public List<UserDto> getAllSubscriptions(String username) {
        List<Subscription> subscriptionIdList = subscriptionRepository.getMySubscriptions(getUserId(username));
        if (subscriptionIdList == null) {
            return null;
        } else {
            return subscriptionIdList.stream().map(subscription -> {
                User user = userRepository.findById(subscription.getToUser()).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Подписаться на пользователя
     * По своему id и id на кого подписан
     * @param username
     * @param toUser
     */
    public void subscribe(String username, Long toUser) {
        Long fromUser = getUserId(username);
        if (checkUser(toUser) && fromUser != null) {
            Subscription subscription = new Subscription();
            subscription.setFromUser(fromUser);
            subscription.setToUser(toUser);
            subscription.setStatus(true);
            subscription.setDateSubscription(LocalDateTime.now());
            subscriptionRepository.save(subscription);
        }
    }

    /** Отписаться от пользователя
     * По своему id и id на кого подписан
     * @param username
     * @param toUser
     * @return
     */
    public boolean unsubscribe(String username, Long toUser) {
        Subscription subscription = subscriptionRepository.findSubscription(getUserId(username), toUser).orElse(null);
        if (subscription == null) {
            return false;
        } else {
            subscription.setStatus(false);
            subscriptionRepository.save(subscription);
            return true;
        }
    }

    /** Возвращает номер чата для переписки
     * id запроса в друзья
     * @param fromUser
     * @param toUser
     * @return
     */
    public String getChat(String fromUser, String toUser) {
        FriendRequest friendRequest = friendRequestRepository.findFriend(getUserId(fromUser), getUserId(toUser)).orElse(null);
        if (friendRequest == null) {
            return null;
        } else {
            return friendRequest.getId().toString();
        }
    }


    // приватные методы класса

    private UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            return userDto;
        }
    }

    private boolean checkUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    private Long getUserId(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }

}
