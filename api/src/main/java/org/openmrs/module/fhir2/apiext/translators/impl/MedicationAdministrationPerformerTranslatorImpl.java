/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.translators.impl;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.Nonnull;
import java.util.Optional;

import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Reference;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.ConceptTranslator;
import org.openmrs.module.fhir2.api.translators.PractitionerReferenceTranslator;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationPerformerDao;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationPerformerTranslator;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Setter;

@Component
@Setter(AccessLevel.PACKAGE)
public class MedicationAdministrationPerformerTranslatorImpl implements MedicationAdministrationPerformerTranslator {

	@Autowired
	private ConceptTranslator conceptTranslator;

	@Autowired
	private FhirMedicationAdministrationPerformerDao fhirMedicationAdministrationPerformerDao;

	@Autowired
	@Qualifier("practitionerReferenceTranslatorProviderImpl")
	private PractitionerReferenceTranslator practitionerReferenceTranslator;

	@Override
	public MedicationAdministration.MedicationAdministrationPerformerComponent toFhirResource(@Nonnull MedicationAdministrationPerformer openmrsObject) {
		notNull(openmrsObject, "The Openmrs MedicationAdministrationPerformer object should not be null");
		MedicationAdministration.MedicationAdministrationPerformerComponent fhirObject = new MedicationAdministration.MedicationAdministrationPerformerComponent();
		fhirObject.setId(openmrsObject.getUuid());
		if (openmrsObject.getActor() != null) {
			fhirObject.setActor(practitionerReferenceTranslator.toFhirResource(openmrsObject.getActor()));
		}
		if (openmrsObject.getFunction() != null) {
			fhirObject.setFunction(conceptTranslator.toFhirResource(openmrsObject.getFunction()));
		}
		return fhirObject;
	}

	@Override
	public MedicationAdministrationPerformer toOpenmrsType(@Nonnull MedicationAdministration.MedicationAdministrationPerformerComponent fhirObject) {
		notNull(fhirObject, "The Fhir MedicationAdministration.MedicationAdministrationPerformerComponent object should not be null");
		return toOpenmrsType(
				Optional.ofNullable(fhirMedicationAdministrationPerformerDao.get(fhirObject.getId()))
						.orElseGet(MedicationAdministrationPerformer::new),
				fhirObject);
	}

	@Override
	public MedicationAdministrationPerformer toOpenmrsType(@Nonnull MedicationAdministrationPerformer openmrsObject,
			@Nonnull MedicationAdministration.MedicationAdministrationPerformerComponent fhirObject) {
		notNull(openmrsObject, "The existing Openmrs MedicationAdministrationPerformer object should not be null");
		notNull(fhirObject, "The FHIR MedicationAdministration.MedicationAdministrationPerformerComponent object should not be null");

		if (fhirObject.hasId()) {
			openmrsObject.setUuid(fhirObject.getId());
		}
		if (fhirObject.hasActor()) {
			openmrsObject.setActor((Provider) practitionerReferenceTranslator.toOpenmrsType((Reference) fhirObject.getActor()));
		}
		if (fhirObject.hasFunction()) {
			openmrsObject.setFunction(conceptTranslator.toOpenmrsType(fhirObject.getFunction()));
		}
		return openmrsObject;
	}
}
