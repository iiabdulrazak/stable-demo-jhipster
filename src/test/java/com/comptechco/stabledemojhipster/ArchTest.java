package com.comptechco.stabledemojhipster;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.comptechco.stabledemojhipster");

        noClasses()
            .that()
            .resideInAnyPackage("com.comptechco.stabledemojhipster.service..")
            .or()
            .resideInAnyPackage("com.comptechco.stabledemojhipster.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.comptechco.stabledemojhipster.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
