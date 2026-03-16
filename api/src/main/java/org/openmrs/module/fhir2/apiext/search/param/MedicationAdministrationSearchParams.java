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

import java.util.Set;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.search.param.BaseResourceSearchParams;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MedicationAdministrationSearchParams extends BaseResourceSearchParams {

	private ReferenceAndListParam patientReference;
	private ReferenceAndListParam encounterReference;
	private ReferenceAndListParam medicationRequestReference;
	private ReferenceAndListParam medicationReference;
	private ReferenceAndListParam performerReference;
	private TokenAndListParam status;
	private DateParam effectiveDate;

	@Builder
	public MedicationAdministrationSearchParams(ReferenceAndListParam patientReference,
			ReferenceAndListParam encounterReference,
			ReferenceAndListParam medicationRequestReference,
			ReferenceAndListParam medicationReference,
			ReferenceAndListParam performerReference,
			TokenAndListParam id, TokenAndListParam status, DateParam effectiveDate,
			DateRangeParam lastUpdated, Set<Include> includes, Set<Include> revIncludes) {

		super(id, lastUpdated, null, includes, revIncludes);

		this.patientReference = patientReference;
		this.encounterReference = encounterReference;
		this.medicationRequestReference = medicationRequestReference;
		this.medicationReference = medicationReference;
		this.performerReference = performerReference;
		this.effectiveDate = effectiveDate;
		this.status = status;
	}

	@Override
	public SearchParameterMap toSearchParameterMap() {
		SearchParameterMap map = baseSearchParameterMap();
		if (patientReference != null) {
			map.addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER, patientReference);
		}
		if (encounterReference != null) {
			map.addParameter(FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER, encounterReference);
		}
		if (medicationReference != null) {
			map.addParameter(FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER, medicationReference);
		}
		if (status != null) {
			map.addParameter(FhirConstants.STATUS_SEARCH_HANDLER, status);
		}
		return map;
	}
}
