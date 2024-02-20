package com.dailycodelearner.repository;

import com.dailycodelearner.model.Tutorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TutorialRepository extends JpaRepository<Tutorial,Long> {

    public Page<Tutorial> findByTitleContaining(String title, Pageable pageable);

    Page<Tutorial> findByPublished(boolean b, Pageable pageable);

    List<Tutorial> findByTitleContaining(String title, Sort by);
}
