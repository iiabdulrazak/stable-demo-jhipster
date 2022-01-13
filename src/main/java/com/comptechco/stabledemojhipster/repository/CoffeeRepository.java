package com.comptechco.stabledemojhipster.repository;

import com.comptechco.stabledemojhipster.domain.Coffee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Coffee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {}
