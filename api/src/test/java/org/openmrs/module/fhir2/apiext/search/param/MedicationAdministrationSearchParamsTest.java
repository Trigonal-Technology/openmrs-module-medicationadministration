/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.search.param;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.junit.Test;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;

public class MedicationAdministrationSearchParamsTest {

	@Test
	public void toSearchParameterMap_shouldIncludePatientReference() {
		ReferenceAndListParam patientRef = new ReferenceAndListParam();
		patientRef.addAnd(new ReferenceOrListParam().add(new ReferenceParam("Patient", "123")));

		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder()
				.patientReference(patientRef)
				.build();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map, notNullValue());
		assertThat(map.getParameters(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER), not(empty()));
	}

	@Test
	public void toSearchParameterMap_shouldIncludeEncounterReference() {
		ReferenceAndListParam encounterRef = new ReferenceAndListParam();
		encounterRef.addAnd(new ReferenceOrListParam().add(new ReferenceParam("Encounter", "enc-1")));

		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder()
				.encounterReference(encounterRef)
				.build();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map.getParameters(FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER), not(empty()));
	}

	@Test
	public void toSearchParameterMap_shouldIncludeMedicationReference() {
		ReferenceAndListParam medRef = new ReferenceAndListParam();
		medRef.addAnd(new ReferenceOrListParam().add(new ReferenceParam("Medication", "med-1")));

		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder()
				.medicationReference(medRef)
				.build();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map.getParameters(FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER), not(empty()));
	}

	@Test
	public void toSearchParameterMap_shouldIncludeStatus() {
		TokenAndListParam status = new TokenAndListParam();
		status.addAnd(new TokenParam("completed"));

		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder()
				.status(status)
				.build();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map.getParameters(FhirConstants.STATUS_SEARCH_HANDLER), not(empty()));
	}

	@Test
	public void toSearchParameterMap_shouldIncludeCommonParams() {
		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder()
				.id(new TokenAndListParam().addAnd(new TokenParam("uuid-1")))
				.lastUpdated(new DateRangeParam(new DateParam("2024-01-01")))
				.build();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map, notNullValue());
		assertThat(map.getParameters(FhirConstants.COMMON_SEARCH_HANDLER), not(empty()));
	}

	@Test
	public void toSearchParameterMap_shouldHandleEmptyParams() {
		MedicationAdministrationSearchParams params = new MedicationAdministrationSearchParams();

		SearchParameterMap map = params.toSearchParameterMap();

		assertThat(map, notNullValue());
	}
}
