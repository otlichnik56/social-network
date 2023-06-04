package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "publications")
@AllArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "username")
    private String username;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "header")
    private String header;

    @Column(name = "text")
    private String text;

    @Column(name = "image")
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] image;

    public Publication() {

    }
}
