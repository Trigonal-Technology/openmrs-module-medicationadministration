package org.openmrs.module.ipd.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ipd.api.db.MedicationAdministrationDAO;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.ipd.api.service.MedicationAdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("medicationAdministrationService")
public class MedicationAdministrationServiceImpl extends BaseOpenmrsService implements MedicationAdministrationService {

    @Autowired
    private MedicationAdministrationDAO dao;

    @Override
    public MedicationAdministration getMedicationAdministrationByUuid(String uuid) {
        return dao.getMedicationAdministrationByUuid(uuid);
    }

    @Override
    public MedicationAdministration saveMedicationAdministration(MedicationAdministration medicationAdministration) {
        return dao.saveMedicationAdministration(medicationAdministration);
    }

    @Override
    public void voidMedicationAdministration(MedicationAdministration medicationAdministration, String reason) {
        medicationAdministration.setVoided(true);
        medicationAdministration.setVoidReason(reason);
        dao.saveMedicationAdministration(medicationAdministration);
    }
}
