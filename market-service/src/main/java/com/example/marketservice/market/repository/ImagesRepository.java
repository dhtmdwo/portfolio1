package com.example.marketservice.market.repository;

import com.example.marketservice.market.model.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Integer> {
    // Your code here
}
        