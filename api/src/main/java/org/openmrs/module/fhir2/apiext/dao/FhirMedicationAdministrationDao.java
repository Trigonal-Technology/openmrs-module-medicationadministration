/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.dao;

import javax.annotation.Nonnull;
import java.util.List;

import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.ipd.api.model.MedicationAdministration;

public interface FhirMedicationAdministrationDao extends FhirDao<MedicationAdministration> {

	@Override
	MedicationAdministration get(@Nonnull String uuid);

	@Override
	MedicationAdministration createOrUpdate(@Nonnull MedicationAdministration newEntry);

	@Override
	MedicationAdministration delete(@Nonnull String uuid);

	@Override
	List<MedicationAdministration> getSearchResults(@Nonnull SearchParameterMap theParams);
}
