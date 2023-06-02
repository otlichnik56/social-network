package com.example.controller;

import com.example.dto.Post;
import com.example.entity.Publication;
import com.example.service.PublicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 4800)
@RestController
@RequestMapping("/post")
public class PublicationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final PublicationService publicationService;

    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping("/{id}")
    public Publication getPublication(@PathVariable(name = "id") Long id) {
        return publicationService.getPublication(id);
    }

    @GetMapping("/my/{id}")
    public List<Publication> getMyPublications(@PathVariable(name = "id") Long id) {
        return publicationService.getMyPublications(id);
    }

    @PostMapping(consumes = "multipart/form-data")
    public Publication createPublication(@RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) throws IOException {
        return publicationService.createPublication(post, file);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ПОСМОТРЕТЬ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @PatchMapping(path = "/{id}", consumes = "multipart/form-data")
    public Publication updatePublication(@PathVariable(name = "id") Long id,
                                         @RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) throws IOException {
        return publicationService.updatePublication(id, post, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePublication(@PathVariable(name = "id") Long id){
        if (publicationService.removePublication(id)) {
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }



}
