package com.comptechco.stabledemojhipster.web.rest;

import com.comptechco.stabledemojhipster.domain.Coffee;
import com.comptechco.stabledemojhipster.repository.CoffeeRepository;
import com.comptechco.stabledemojhipster.service.CoffeeService;
import com.comptechco.stabledemojhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.comptechco.stabledemojhipster.domain.Coffee}.
 */
@RestController
@RequestMapping("/api")
public class CoffeeResource {

    private final Logger log = LoggerFactory.getLogger(CoffeeResource.class);

    private static final String ENTITY_NAME = "coffee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoffeeService coffeeService;

    private final CoffeeRepository coffeeRepository;

    public CoffeeResource(CoffeeService coffeeService, CoffeeRepository coffeeRepository) {
        this.coffeeService = coffeeService;
        this.coffeeRepository = coffeeRepository;
    }

    /**
     * {@code POST  /coffees} : Create a new coffee.
     *
     * @param coffee the coffee to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coffee, or with status {@code 400 (Bad Request)} if the coffee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/coffees")
    public ResponseEntity<Coffee> createCoffee(@Valid @RequestBody Coffee coffee) throws URISyntaxException {
        log.debug("REST request to save Coffee : {}", coffee);
        if (coffee.getId() != null) {
            throw new BadRequestAlertException("A new coffee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Coffee result = coffeeService.save(coffee);
        return ResponseEntity
            .created(new URI("/api/coffees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /coffees/:id} : Updates an existing coffee.
     *
     * @param id the id of the coffee to save.
     * @param coffee the coffee to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coffee,
     * or with status {@code 400 (Bad Request)} if the coffee is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coffee couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/coffees/{id}")
    public ResponseEntity<Coffee> updateCoffee(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Coffee coffee
    ) throws URISyntaxException {
        log.debug("REST request to update Coffee : {}, {}", id, coffee);
        if (coffee.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coffee.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coffeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Coffee result = coffeeService.save(coffee);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coffee.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /coffees/:id} : Partial updates given fields of an existing coffee, field will ignore if it is null
     *
     * @param id the id of the coffee to save.
     * @param coffee the coffee to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coffee,
     * or with status {@code 400 (Bad Request)} if the coffee is not valid,
     * or with status {@code 404 (Not Found)} if the coffee is not found,
     * or with status {@code 500 (Internal Server Error)} if the coffee couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/coffees/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Coffee> partialUpdateCoffee(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Coffee coffee
    ) throws URISyntaxException {
        log.debug("REST request to partial update Coffee partially : {}, {}", id, coffee);
        if (coffee.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coffee.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coffeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Coffee> result = coffeeService.partialUpdate(coffee);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coffee.getId().toString())
        );
    }

    /**
     * {@code GET  /coffees} : get all the coffees.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coffees in body.
     */
    @GetMapping("/coffees")
    public List<Coffee> getAllCoffees() {
        log.debug("REST request to get all Coffees");
        return coffeeService.findAll();
    }

    /**
     * {@code GET  /coffees/:id} : get the "id" coffee.
     *
     * @param id the id of the coffee to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coffee, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/coffees/{id}")
    public ResponseEntity<Coffee> getCoffee(@PathVariable Long id) {
        log.debug("REST request to get Coffee : {}", id);
        Optional<Coffee> coffee = coffeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(coffee);
    }

    /**
     * {@code DELETE  /coffees/:id} : delete the "id" coffee.
     *
     * @param id the id of the coffee to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/coffees/{id}")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        log.debug("REST request to delete Coffee : {}", id);
        coffeeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
