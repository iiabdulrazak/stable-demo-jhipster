package com.comptechco.stabledemojhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.comptechco.stabledemojhipster.IntegrationTest;
import com.comptechco.stabledemojhipster.domain.Coffee;
import com.comptechco.stabledemojhipster.repository.CoffeeRepository;
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
 * Integration tests for the {@link CoffeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CoffeeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 5D;
    private static final Double UPDATED_PRICE = 6D;

    private static final String ENTITY_API_URL = "/api/coffees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoffeeMockMvc;

    private Coffee coffee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coffee createEntity(EntityManager em) {
        Coffee coffee = new Coffee().name(DEFAULT_NAME).price(DEFAULT_PRICE);
        return coffee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coffee createUpdatedEntity(EntityManager em) {
        Coffee coffee = new Coffee().name(UPDATED_NAME).price(UPDATED_PRICE);
        return coffee;
    }

    @BeforeEach
    public void initTest() {
        coffee = createEntity(em);
    }

    @Test
    @Transactional
    void createCoffee() throws Exception {
        int databaseSizeBeforeCreate = coffeeRepository.findAll().size();
        // Create the Coffee
        restCoffeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffee)))
            .andExpect(status().isCreated());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeCreate + 1);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCoffee.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createCoffeeWithExistingId() throws Exception {
        // Create the Coffee with an existing ID
        coffee.setId(1L);

        int databaseSizeBeforeCreate = coffeeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoffeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffee)))
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCoffees() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        // Get all the coffeeList
        restCoffeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coffee.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        // Get the coffee
        restCoffeeMockMvc
            .perform(get(ENTITY_API_URL_ID, coffee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coffee.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingCoffee() throws Exception {
        // Get the coffee
        restCoffeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee
        Coffee updatedCoffee = coffeeRepository.findById(coffee.getId()).get();
        // Disconnect from session so that the updates on updatedCoffee are not directly saved in db
        em.detach(updatedCoffee);
        updatedCoffee.name(UPDATED_NAME).price(UPDATED_PRICE);

        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCoffee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCoffee))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCoffee.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coffee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coffee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(coffee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(coffee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCoffeeWithPatch() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee using partial update
        Coffee partialUpdatedCoffee = new Coffee();
        partialUpdatedCoffee.setId(coffee.getId());

        partialUpdatedCoffee.name(UPDATED_NAME).price(UPDATED_PRICE);

        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoffee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoffee))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCoffee.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateCoffeeWithPatch() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();

        // Update the coffee using partial update
        Coffee partialUpdatedCoffee = new Coffee();
        partialUpdatedCoffee.setId(coffee.getId());

        partialUpdatedCoffee.name(UPDATED_NAME).price(UPDATED_PRICE);

        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoffee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCoffee))
            )
            .andExpect(status().isOk());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
        Coffee testCoffee = coffeeList.get(coffeeList.size() - 1);
        assertThat(testCoffee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCoffee.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, coffee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coffee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(coffee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoffee() throws Exception {
        int databaseSizeBeforeUpdate = coffeeRepository.findAll().size();
        coffee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoffeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(coffee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coffee in the database
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoffee() throws Exception {
        // Initialize the database
        coffeeRepository.saveAndFlush(coffee);

        int databaseSizeBeforeDelete = coffeeRepository.findAll().size();

        // Delete the coffee
        restCoffeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, coffee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Coffee> coffeeList = coffeeRepository.findAll();
        assertThat(coffeeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
