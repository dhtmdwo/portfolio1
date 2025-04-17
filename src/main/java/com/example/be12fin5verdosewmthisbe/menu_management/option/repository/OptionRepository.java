package com.example.be12fin5verdosewmthisbe.menu_management.option.repository;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    Page<Option> findByNameContaining(String keyword, Pageable pageable);
    @EntityGraph(attributePaths = {"optionValueList"})
    Optional<Option> findWithOptionValuesById(Long id);
}
