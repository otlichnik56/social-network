package com.example.repository;

import com.example.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query(value = "SELECT * FROM friend_requests WHERE status = 1 AND (from_user = :id OR to_user = :id)", nativeQuery = true)
    List<FriendRequest> findMyFriendsId(Long id);

    @Query(value = "SELECT * FROM friend_requests WHERE status = 1 AND ((from_user = :fromUser AND to_user = :toUser) OR (from_user = :toUser AND to_user = :fromUser))", nativeQuery = true)
    Optional<FriendRequest> findFriend(Long fromUser, Long toUser);

    @Query(value = "SELECT * FROM friend_requests WHERE status = 0 AND from_user = :id", nativeQuery = true)
    List<FriendRequest> findAllByFromUser(Long id);

    @Query(value = "SELECT * FROM friend_requests WHERE status = 0 AND to_user = :id", nativeQuery = true)
    List<FriendRequest> findAllByToUser(Long id);

    @Query(value = "SELECT * FROM friend_requests WHERE id = :id", nativeQuery = true)
    Optional<FriendRequest> findByFromToUsers(Long id);

}
