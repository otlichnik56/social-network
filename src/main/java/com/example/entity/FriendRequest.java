package com.example.entity;

import com.example.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import javax.persistence.*;

@Data
@Entity
@Table(name = "friend_requests")
@AllArgsConstructor
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "from_user")
    private Long fromUser;

    @Column(name = "to_user")
    private Long toUser;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    @Column(name = "date_request")
    private LocalDate dateRequest;

    @Column(name = "date_response")
    private LocalDate dateResponse;

    public FriendRequest() {

    }
}
