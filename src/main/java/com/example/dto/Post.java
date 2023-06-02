package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Post {

    private  Long authorId;

    private String username;

    private String header;

    private String text;

}
