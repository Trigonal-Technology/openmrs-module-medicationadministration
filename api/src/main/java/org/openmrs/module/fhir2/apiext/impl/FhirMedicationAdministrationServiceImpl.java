/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FhirMedicationAdministrationServiceImpl
		extends BaseFhirService<org.hl7.fhir.r4.model.MedicationAdministration, org.openmrs.module.ipd.api.model.MedicationAdministration>
		implements FhirMedicationAdministrationService {

	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE)
	@Autowired
	private FhirMedicationAdministrationDao dao;

	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE)
	@Autowired
	private MedicationAdministrationTranslator translator;

	@Override
	protected org.openmrs.module.fhir2.api.translators.OpenmrsFhirTranslator<org.openmrs.module.ipd.api.model.MedicationAdministration, org.hl7.fhir.r4.model.MedicationAdministration> getTranslator() {
		return translator;
	}

	@Override
	protected org.openmrs.module.fhir2.api.dao.FhirDao<org.openmrs.module.ipd.api.model.MedicationAdministration> getDao() {
		return dao;
	}

	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE)
	@Autowired
	@Qualifier("medicationAdministrationSearchQueryInclude")
	private SearchQueryInclude<org.hl7.fhir.r4.model.MedicationAdministration> searchQueryInclude;

	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE)
	@Autowired
	private SearchQuery<org.openmrs.module.ipd.api.model.MedicationAdministration, org.hl7.fhir.r4.model.MedicationAdministration, FhirMedicationAdministrationDao, MedicationAdministrationTranslator, SearchQueryInclude<org.hl7.fhir.r4.model.MedicationAdministration>> searchQuery;

	@Override
	public IBundleProvider searchForMedicationAdministration(MedicationAdministrationSearchParams searchParams) {
		SearchParameterMap theParams = searchParams.toSearchParameterMap();
		return searchQuery.getQueryResults(theParams, dao, translator, searchQueryInclude);
	}
}
