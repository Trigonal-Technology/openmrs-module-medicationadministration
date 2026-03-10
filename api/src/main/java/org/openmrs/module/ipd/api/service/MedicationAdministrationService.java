package org.openmrs.module.ipd.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.springframework.transaction.annotation.Transactional;

public interface MedicationAdministrationService extends OpenmrsService {

    @Transactional(readOnly = true)
    MedicationAdministration getMedicationAdministrationByUuid(String uuid);

    @Transactional
    MedicationAdministration saveMedicationAdministration(MedicationAdministration medicationAdministration);

    @Transactional
    void voidMedicationAdministration(MedicationAdministration medicationAdministration, String reason);
}
