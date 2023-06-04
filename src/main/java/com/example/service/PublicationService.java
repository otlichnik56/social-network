package com.example.service;

import com.example.dto.Post;
import com.example.entity.Publication;
import com.example.entity.Subscription;
import com.example.entity.User;
import com.example.repository.PublicationRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PublicationService(PublicationRepository publicationRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public Publication getPublication(Long id) {
        return publicationRepository.findById(id).orElse(null);
    }

    public List<Publication> getMyPublications(String username) {
        return publicationRepository.findByUsernameIgnoreCase(username);
    }

    public Publication createPublication(String username, Post post, MultipartFile file) {
        Publication publication = new Publication();
        publication.setDate(LocalDateTime.now());
        return enterPublication(username, post, file, publication);
    }

    public Publication updatePublication(String username, Long id, Post post, MultipartFile file) {
        Publication publication = publicationRepository.findById(id).orElse(null);
        if (publication != null && accessControl(username, publication.getUsername())) { // добавить или с accessControl
            return enterPublication(username, post, file, publication);
        } else {
            return null;
        }
    }

    public boolean removePublication(String username, Long id){
        Publication publication = publicationRepository.findById(id).orElse(null);
        if (publication != null && accessControl(username, publication.getUsername())) { // добавить или с accessControl
            publicationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


    public List<Publication> getPublicationsMySubscriptions(String username, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("date").descending());
        User user = userRepository.findByUsername(username).orElse(null);
        assert user != null;
        List<Subscription> subscriptionList = subscriptionRepository.getMySubscriptions(user.getId());
        if (subscriptionList == null) {
            return null;
        } else {
            List<Long> ids = subscriptionList.stream().map(Subscription::getToUser).collect(Collectors.toList());
            return publicationRepository.findByAuthorIdIn(ids, pageable);
        }
    }


    // приватные методы класса

    private Publication enterPublication(String username, Post post, MultipartFile file, Publication publication){
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        } else {
            publication.setAuthorId(user.getId());
            publication.setUsername(user.getUsername());
            publication.setHeader(post.getHeader());
            publication.setText(post.getText());
            try {
                publication.setImage(file.getBytes());
            } catch (IOException e) {
                publication.setImage(null);
            }
            publicationRepository.save(publication);
            return publication;
        }
    }

    private boolean accessControl(String username, String author) {
        return username.equals(author);
    }

}
