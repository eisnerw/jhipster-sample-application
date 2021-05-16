package com.eisnerw.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eisnerw.IntegrationTest;
import com.eisnerw.domain.Birthday;
import com.eisnerw.repository.BirthdayRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BirthdayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BirthdayResourceIT {

    private static final String DEFAULT_LNAME = "AAAAAAAAAA";
    private static final String UPDATED_LNAME = "BBBBBBBBBB";

    private static final String DEFAULT_FNAME = "AAAAAAAAAA";
    private static final String UPDATED_FNAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_ALIVE = false;
    private static final Boolean UPDATED_IS_ALIVE = true;

    private static final String DEFAULT_ADDITIONAL = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/birthdays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BirthdayRepository birthdayRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBirthdayMockMvc;

    private Birthday birthday;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Birthday createEntity(EntityManager em) {
        Birthday birthday = new Birthday()
            .lname(DEFAULT_LNAME)
            .fname(DEFAULT_FNAME)
            .dob(DEFAULT_DOB)
            .isAlive(DEFAULT_IS_ALIVE)
            .additional(DEFAULT_ADDITIONAL);
        return birthday;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Birthday createUpdatedEntity(EntityManager em) {
        Birthday birthday = new Birthday()
            .lname(UPDATED_LNAME)
            .fname(UPDATED_FNAME)
            .dob(UPDATED_DOB)
            .isAlive(UPDATED_IS_ALIVE)
            .additional(UPDATED_ADDITIONAL);
        return birthday;
    }

    @BeforeEach
    public void initTest() {
        birthday = createEntity(em);
    }

    @Test
    @Transactional
    void createBirthday() throws Exception {
        int databaseSizeBeforeCreate = birthdayRepository.findAll().size();
        // Create the Birthday
        restBirthdayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(birthday)))
            .andExpect(status().isCreated());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeCreate + 1);
        Birthday testBirthday = birthdayList.get(birthdayList.size() - 1);
        assertThat(testBirthday.getLname()).isEqualTo(DEFAULT_LNAME);
        assertThat(testBirthday.getFname()).isEqualTo(DEFAULT_FNAME);
        assertThat(testBirthday.getDob()).isEqualTo(DEFAULT_DOB);
        assertThat(testBirthday.getIsAlive()).isEqualTo(DEFAULT_IS_ALIVE);
        assertThat(testBirthday.getAdditional()).isEqualTo(DEFAULT_ADDITIONAL);
    }

    @Test
    @Transactional
    void createBirthdayWithExistingId() throws Exception {
        // Create the Birthday with an existing ID
        birthday.setId(1L);

        int databaseSizeBeforeCreate = birthdayRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBirthdayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(birthday)))
            .andExpect(status().isBadRequest());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBirthdays() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        // Get all the birthdayList
        restBirthdayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(birthday.getId().intValue())))
            .andExpect(jsonPath("$.[*].lname").value(hasItem(DEFAULT_LNAME)))
            .andExpect(jsonPath("$.[*].fname").value(hasItem(DEFAULT_FNAME)))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].isAlive").value(hasItem(DEFAULT_IS_ALIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].additional").value(hasItem(DEFAULT_ADDITIONAL)));
    }

    @Test
    @Transactional
    void getBirthday() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        // Get the birthday
        restBirthdayMockMvc
            .perform(get(ENTITY_API_URL_ID, birthday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(birthday.getId().intValue()))
            .andExpect(jsonPath("$.lname").value(DEFAULT_LNAME))
            .andExpect(jsonPath("$.fname").value(DEFAULT_FNAME))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.isAlive").value(DEFAULT_IS_ALIVE.booleanValue()))
            .andExpect(jsonPath("$.additional").value(DEFAULT_ADDITIONAL));
    }

    @Test
    @Transactional
    void getNonExistingBirthday() throws Exception {
        // Get the birthday
        restBirthdayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBirthday() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();

        // Update the birthday
        Birthday updatedBirthday = birthdayRepository.findById(birthday.getId()).get();
        // Disconnect from session so that the updates on updatedBirthday are not directly saved in db
        em.detach(updatedBirthday);
        updatedBirthday.lname(UPDATED_LNAME).fname(UPDATED_FNAME).dob(UPDATED_DOB).isAlive(UPDATED_IS_ALIVE).additional(UPDATED_ADDITIONAL);

        restBirthdayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBirthday.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBirthday))
            )
            .andExpect(status().isOk());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
        Birthday testBirthday = birthdayList.get(birthdayList.size() - 1);
        assertThat(testBirthday.getLname()).isEqualTo(UPDATED_LNAME);
        assertThat(testBirthday.getFname()).isEqualTo(UPDATED_FNAME);
        assertThat(testBirthday.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testBirthday.getIsAlive()).isEqualTo(UPDATED_IS_ALIVE);
        assertThat(testBirthday.getAdditional()).isEqualTo(UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    void putNonExistingBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, birthday.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(birthday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(birthday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(birthday)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBirthdayWithPatch() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();

        // Update the birthday using partial update
        Birthday partialUpdatedBirthday = new Birthday();
        partialUpdatedBirthday.setId(birthday.getId());

        partialUpdatedBirthday.lname(UPDATED_LNAME).fname(UPDATED_FNAME).dob(UPDATED_DOB).additional(UPDATED_ADDITIONAL);

        restBirthdayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBirthday.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBirthday))
            )
            .andExpect(status().isOk());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
        Birthday testBirthday = birthdayList.get(birthdayList.size() - 1);
        assertThat(testBirthday.getLname()).isEqualTo(UPDATED_LNAME);
        assertThat(testBirthday.getFname()).isEqualTo(UPDATED_FNAME);
        assertThat(testBirthday.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testBirthday.getIsAlive()).isEqualTo(DEFAULT_IS_ALIVE);
        assertThat(testBirthday.getAdditional()).isEqualTo(UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    void fullUpdateBirthdayWithPatch() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();

        // Update the birthday using partial update
        Birthday partialUpdatedBirthday = new Birthday();
        partialUpdatedBirthday.setId(birthday.getId());

        partialUpdatedBirthday
            .lname(UPDATED_LNAME)
            .fname(UPDATED_FNAME)
            .dob(UPDATED_DOB)
            .isAlive(UPDATED_IS_ALIVE)
            .additional(UPDATED_ADDITIONAL);

        restBirthdayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBirthday.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBirthday))
            )
            .andExpect(status().isOk());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
        Birthday testBirthday = birthdayList.get(birthdayList.size() - 1);
        assertThat(testBirthday.getLname()).isEqualTo(UPDATED_LNAME);
        assertThat(testBirthday.getFname()).isEqualTo(UPDATED_FNAME);
        assertThat(testBirthday.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testBirthday.getIsAlive()).isEqualTo(UPDATED_IS_ALIVE);
        assertThat(testBirthday.getAdditional()).isEqualTo(UPDATED_ADDITIONAL);
    }

    @Test
    @Transactional
    void patchNonExistingBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, birthday.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(birthday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(birthday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBirthday() throws Exception {
        int databaseSizeBeforeUpdate = birthdayRepository.findAll().size();
        birthday.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBirthdayMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(birthday)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Birthday in the database
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBirthday() throws Exception {
        // Initialize the database
        birthdayRepository.saveAndFlush(birthday);

        int databaseSizeBeforeDelete = birthdayRepository.findAll().size();

        // Delete the birthday
        restBirthdayMockMvc
            .perform(delete(ENTITY_API_URL_ID, birthday.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Birthday> birthdayList = birthdayRepository.findAll();
        assertThat(birthdayList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
