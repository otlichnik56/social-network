package com.example.service;

import com.example.dto.Post;
import com.example.entity.Publication;
import com.example.entity.Subscription;
import com.example.entity.User;
import com.example.repository.PublicationRepository;
import com.example.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PublicationService(PublicationRepository publicationRepository, SubscriptionRepository subscriptionRepository) {
        this.publicationRepository = publicationRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }*/

    public Publication getPublication(Long id) {
        return publicationRepository.getReferenceById(id);
    }

    public List<Publication> getMyPublications(Long id) {
        return publicationRepository.findByAuthorId(id);
    }

    public Publication createPublication(Post post, MultipartFile file) throws IOException {
        Publication publication = new Publication();
        return enterPublication(post, file, publication);
    }

    public Publication updatePublication(Long id, Post post, MultipartFile file) throws IOException {
        Publication publication = publicationRepository.findById(id).orElse(null);
        if (publication == null) { // добавить или с accessControl
            return null;
        } else {
            return enterPublication(post, file, publication);
        }
    }

    public boolean removePublication(Long id){
        Publication publication = publicationRepository.findById(id).orElse(null);
        if (publication == null) { // добавить или с accessControl
            return false;
        } else {
            publicationRepository.deleteById(id);
            return true;
        }
    }



    /** // Возвращает публикации на кого подписан
    public List<Publication> getPublicationsMySubscriptions(Long id) {
        // написать сортировку по времени
        List<Subscription> subscriptionListId = subscriptionRepository.getMySubscriptionsId(id);
        if (subscriptionListId == null) {
            return null;
        } else {
            List<Publication> publicationList = subscriptionListId.stream().map(subscription -> {
                List<Publication> publications = publicationRepository.findByAuthorId(subscription.getToUser());
                return publications.stream().map();
            }).collect(Collectors.toList());

        }
    }*/

    // реализовать пагинацию!!!!!!!!!!!!!!!!!!!!



    // приватные методы класса
    private Publication enterPublication(Post post, MultipartFile file, Publication publication) throws IOException {
        publication.setAuthorId(post.getAuthorId());
        publication.setUsername(post.getUsername());
        publication.setHeader(post.getHeader());
        publication.setText(post.getText());
        publication.setImage(file.getBytes());
        publicationRepository.save(publication);
        return publication;
    }

    private boolean accessControl(Long id, Long authorId) {
        return id.equals(authorId);
    }

}
