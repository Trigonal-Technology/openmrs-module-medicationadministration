package org.openmrs.module.ipd.api.db;

import org.openmrs.module.ipd.api.model.MedicationAdministration;

public interface MedicationAdministrationDAO {

    MedicationAdministration getMedicationAdministrationByUuid(String uuid);

    MedicationAdministration saveMedicationAdministration(MedicationAdministration medicationAdministration);

    void deleteMedicationAdministration(MedicationAdministration medicationAdministration);
}
