package com.example.repository;

import com.example.entity.Publication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PublicationRepository extends PagingAndSortingRepository<Publication, Long> {
    List<Publication> findByUsernameIgnoreCase(String username);

    @Query(value = "SELECT * FROM publications JOIN subscriptions ON publications.author_id = subscriptions.to_user JOIN users on subscriptions.from_user = users.id WHERE users.id = :id", nativeQuery = true)
    List<Publication> findPublicationsMySubscriptions(Long id, Pageable pageable);

}
