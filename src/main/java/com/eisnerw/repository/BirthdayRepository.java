package com.eisnerw.repository;

import com.eisnerw.domain.Birthday;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Birthday entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BirthdayRepository extends JpaRepository<Birthday, Long> {}
