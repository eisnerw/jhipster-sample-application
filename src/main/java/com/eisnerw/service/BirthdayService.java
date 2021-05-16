package com.eisnerw.service;

import com.eisnerw.domain.Birthday;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Birthday}.
 */
public interface BirthdayService {
    /**
     * Save a birthday.
     *
     * @param birthday the entity to save.
     * @return the persisted entity.
     */
    Birthday save(Birthday birthday);

    /**
     * Partially updates a birthday.
     *
     * @param birthday the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Birthday> partialUpdate(Birthday birthday);

    /**
     * Get all the birthdays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Birthday> findAll(Pageable pageable);

    /**
     * Get the "id" birthday.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Birthday> findOne(Long id);

    /**
     * Delete the "id" birthday.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
