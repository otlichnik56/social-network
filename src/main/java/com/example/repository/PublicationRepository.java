package com.example.repository;

import com.example.entity.Publication;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PublicationRepository extends PagingAndSortingRepository<Publication, Long> {

    List<Publication> findByUsernameIgnoreCase(String username);

    List<Publication> findByAuthorIdIn(List<Long> ids, Pageable pageable);

}
