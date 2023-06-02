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

import java.time.LocalDate;
import java.util.List;
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
     * @param id
     * @return
     */
    public List<UserDto> getAllFriends(Long id) {
        List<FriendRequest> friendIdList = friendRequestRepository.findMyFriendsId(id);
        if (friendIdList == null) {
            return null;
        } else {
            return friendIdList.stream().map(friendRequest -> {
                User user = userRepository.findById(String.valueOf(friendRequest.getFromUser())).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Просмотреть исходящие заявки в друзья
     * по id пользователя
     * @param id
     * @return
     */
    public List<FriendRequest> incomingFriendship(Long id) {
        return friendRequestRepository.findAllByFromUser(id);
    }

    /** Просмотреть входящие заявки в друзья
     * по id пользователя
     * @param id
     * @return
     */
    public List<FriendRequest> outgoingFriendship(Long id) {
        return friendRequestRepository.findAllByToUser(id);
    }

    /** Отправить заявку в друзья
     * по id отправителя и id получателя
     * @param fromUser
     * @param toUser
     */
    public void sendFriendship(Long fromUser, Long toUser) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setFromUser(fromUser);
        friendRequest.setToUser(toUser);
        friendRequest.setStatus(Status.ACTIVE);
        friendRequest.setDateRequest(LocalDate.now());
        subscribe(fromUser, toUser);
    }

    /** отменить заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String cancelFriendship(Long id) {
        FriendRequest friendRequest = friendRequestRepository.findByFromToUsers(id).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else {
            friendRequest.setStatus(Status.NOT_ACTIVE);
            friendRequestRepository.save(friendRequest);
            return "friend request canceled";
        }
    }

    /** Принять заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String acceptFriendship(Long id) {
        FriendRequest friendRequest = friendRequestRepository.findByFromToUsers(id).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else {
            friendRequest.setStatus(Status.CONFIRMATION);
            friendRequestRepository.save(friendRequest);
            subscribe(friendRequest.getToUser(), friendRequest.getFromUser());
            return "you have one new friend";
        }
    }

    /** оклонить заявку в друзья
     * по id заявки
     * @param id
     * @return
     */
    public String declineFriendship(Long id) {
        FriendRequest friendRequest = friendRequestRepository.findByFromToUsers(id).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else {
            friendRequest.setStatus(Status.NOT_ACTIVE);
            friendRequestRepository.save(friendRequest);
            return "friend request rejected";
        }
    }

    /** Удалить из друзей
     * по id пользователя и id удаляемого
     * @param fromUser
     * @param toUser
     * @return
     */
    public String deleteFriend(Long fromUser, Long toUser) {
        FriendRequest friendRequest = friendRequestRepository.findFriend(fromUser, toUser).orElse(null);
        if (friendRequest == null) {
            return ANSWER_NOT_FOUND;
        } else {
            friendRequest.setStatus(Status.NOT_ACTIVE);
            friendRequestRepository.save(friendRequest);
            unsubscribe(fromUser, toUser);
            return "successfully unfriended";
        }
    }


    // работа с подписками

    /** Получить всех на кого подписан
     * По своему id
     * @param id
     * @return
     */
    public List<UserDto> getAllSubscriptions(Long id) {
        List<Subscription> subscriptionIdList = subscriptionRepository.getMySubscriptionsId(id);
        if (subscriptionIdList == null) {
            return null;
        } else {
            return subscriptionIdList.stream().map(subscription -> {
                User user = userRepository.findById(String.valueOf(subscription.getToUser())).orElse(null);
                return userToUserDto(user);
            }).collect(Collectors.toList());
        }
    }

    /** Подписаться на пользователя
     * По своему id и id на кого подписан
     * @param fromUser
     * @param toUser
     */
    public void subscribe(Long fromUser, Long toUser) {
        Subscription subscription = new Subscription();
        subscription.setFromUser(fromUser);
        subscription.setToUser(toUser);
        subscription.setStatus(true);
        subscription.setDateSubscription(LocalDate.now());
        subscriptionRepository.save(subscription);
    }

    /** Отписаться от пользователя
     * По своему id и id на кого подписан
     * @param fromUser
     * @param toUser
     * @return
     */
    public boolean unsubscribe(Long fromUser, Long toUser) {
        Subscription subscription = subscriptionRepository.findSubscription(fromUser, toUser).orElse(null);
        if (subscription == null) {
            return false;
        } else {
            subscription.setStatus(false);
            subscriptionRepository.save(subscription);
            return true;
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

}
