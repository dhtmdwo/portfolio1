package com.example.be12fin5verdosewmthisbe.store.repository;

import com.example.be12fin5verdosewmthisbe.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    // Your code here

}
        