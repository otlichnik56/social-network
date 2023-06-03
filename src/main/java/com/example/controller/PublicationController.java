package com.example.controller;

import com.example.dto.Post;
import com.example.entity.Publication;
import com.example.service.PublicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @PreAuthorize("isAuthenticated()")
    public Publication getPublication(@PathVariable(name = "id") Long id) {
        return publicationService.getPublication(id);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<Publication> getMyPublications(@AuthenticationPrincipal UserDetails userDetails) {
        return publicationService.getMyPublications(userDetails.getUsername());
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public Publication createPublication(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) {
        return publicationService.createPublication(userDetails.getUsername(), post, file);
    }

    @PatchMapping(path = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public Publication updatePublication(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable(name = "id") Long id,
                                         @RequestPart(value = "properties") Post post,
                                         @RequestPart(value = "image") MultipartFile file) {
        return publicationService.updatePublication(userDetails.getUsername(), id, post, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removePublication(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable(name = "id") Long id){
        if (publicationService.removePublication(userDetails.getUsername(), id)) {
            return ResponseEntity.status(200).body("publication successfully deleted");
        } else {
            return ResponseEntity.status(401).build();
        }
    }


    @GetMapping("/news")
    @PreAuthorize("isAuthenticated()")
    public List<Publication> getNews(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                     @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) Integer limit) {
        return publicationService.getPublicationsMySubscriptions(userDetails.getUsername(), offset, limit);
    }

}
