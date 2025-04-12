package com.example.be12fin5verdosewmthisbe.menu_management.option.repository;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

}
