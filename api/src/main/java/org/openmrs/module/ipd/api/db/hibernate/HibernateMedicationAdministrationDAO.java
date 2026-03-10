package org.openmrs.module.ipd.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.ipd.api.db.MedicationAdministrationDAO;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("medicationAdministrationDAO")
public class HibernateMedicationAdministrationDAO implements MedicationAdministrationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public MedicationAdministration getMedicationAdministrationByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MedicationAdministration.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (MedicationAdministration) criteria.uniqueResult();
    }

    @Override
    public MedicationAdministration saveMedicationAdministration(MedicationAdministration medicationAdministration) {
        sessionFactory.getCurrentSession().saveOrUpdate(medicationAdministration);
        return medicationAdministration;
    }

    @Override
    public void deleteMedicationAdministration(MedicationAdministration medicationAdministration) {
        sessionFactory.getCurrentSession().delete(medicationAdministration);
    }
}
