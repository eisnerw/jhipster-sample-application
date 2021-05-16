package com.eisnerw.service.impl;

import com.eisnerw.domain.Birthday;
import com.eisnerw.repository.BirthdayRepository;
import com.eisnerw.service.BirthdayService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Birthday}.
 */
@Service
@Transactional
public class BirthdayServiceImpl implements BirthdayService {

    private final Logger log = LoggerFactory.getLogger(BirthdayServiceImpl.class);

    private final BirthdayRepository birthdayRepository;

    public BirthdayServiceImpl(BirthdayRepository birthdayRepository) {
        this.birthdayRepository = birthdayRepository;
    }

    @Override
    public Birthday save(Birthday birthday) {
        log.debug("Request to save Birthday : {}", birthday);
        return birthdayRepository.save(birthday);
    }

    @Override
    public Optional<Birthday> partialUpdate(Birthday birthday) {
        log.debug("Request to partially update Birthday : {}", birthday);

        return birthdayRepository
            .findById(birthday.getId())
            .map(
                existingBirthday -> {
                    if (birthday.getLname() != null) {
                        existingBirthday.setLname(birthday.getLname());
                    }
                    if (birthday.getFname() != null) {
                        existingBirthday.setFname(birthday.getFname());
                    }
                    if (birthday.getDob() != null) {
                        existingBirthday.setDob(birthday.getDob());
                    }
                    if (birthday.getIsAlive() != null) {
                        existingBirthday.setIsAlive(birthday.getIsAlive());
                    }
                    if (birthday.getAdditional() != null) {
                        existingBirthday.setAdditional(birthday.getAdditional());
                    }

                    return existingBirthday;
                }
            )
            .map(birthdayRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Birthday> findAll(Pageable pageable) {
        log.debug("Request to get all Birthdays");
        return birthdayRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Birthday> findOne(Long id) {
        log.debug("Request to get Birthday : {}", id);
        return birthdayRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Birthday : {}", id);
        birthdayRepository.deleteById(id);
    }
}
