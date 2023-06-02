package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import javax.persistence.*;

@Data
@Entity
@Table(name = "subscriptions")
@AllArgsConstructor

public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "from_user")
    private Long fromUser;

    @Column(name = "to_user")
    private Long toUser;

    @Column(name = "status")
    private boolean status;

    @Column(name = "date_subscription")
    private LocalDate dateSubscription;

    public Subscription() {

    }
}
