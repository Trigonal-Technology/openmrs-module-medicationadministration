/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext;

/**
 * Constants for MedicationAdministration FHIR extension search handlers.
 * Uses same names as org.openmrs.module.fhir2.FhirConstants where applicable.
 */
public final class FhirMedicationAdministrationConstants {

	private FhirMedicationAdministrationConstants() {
	}

	public static final String MEDICATION_ADMINISTRATION_PERFORMER_SEARCH_HANDLER = "medicationAdministration.performer.search.handler";
}
