package com.example.repository;

import com.example.entity.FriendRequest;
import com.example.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = "SELECT * FROM subscriptions WHERE status = true AND from_user = :id", nativeQuery = true)
    List<Subscription> getMySubscriptionsId(Long id);

    @Query(value = "SELECT * FROM subscriptions WHERE status = true AND from_user = :fromUser AND from_user = :toUser", nativeQuery = true)
    Optional<Subscription> findSubscription(Long fromUser, Long toUser);

}
