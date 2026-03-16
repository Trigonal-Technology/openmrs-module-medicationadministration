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

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Reference;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.PractitionerReferenceTranslator;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationNoteDao;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationNoteTranslator;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Setter;

@Component
@Setter(AccessLevel.PACKAGE)
public class MedicationAdministrationNoteTranslatorImpl implements MedicationAdministrationNoteTranslator {

	@Autowired
	@Qualifier("practitionerReferenceTranslatorProviderImpl")
	private PractitionerReferenceTranslator practitionerReferenceTranslator;

	@Autowired
	private FhirMedicationAdministrationNoteDao fhirMedicationAdministrationNoteDao;

	@Override
	public Annotation toFhirResource(@Nonnull MedicationAdministrationNote openmrsObject) {
		notNull(openmrsObject, "The Openmrs MedicationAdministrationNote object should not be null");
		Annotation fhirObject = new Annotation();
		fhirObject.setId(openmrsObject.getUuid());
		if (openmrsObject.getAuthor() != null) {
			fhirObject.setAuthor(practitionerReferenceTranslator.toFhirResource(openmrsObject.getAuthor()));
		}
		if (openmrsObject.getText() != null) {
			fhirObject.setText(openmrsObject.getText());
		}
		if (openmrsObject.getRecordedTime() != null) {
			fhirObject.setTime(openmrsObject.getRecordedTime());
		}
		return fhirObject;
	}

	@Override
	public MedicationAdministrationNote toOpenmrsType(@Nonnull Annotation fhirObject) {
		notNull(fhirObject, "The Fhir Annotation object should not be null");
		MedicationAdministrationNote existing = fhirObject.hasId()
				? fhirMedicationAdministrationNoteDao.get(fhirObject.getId())
				: null;
		return toOpenmrsType(existing != null ? existing : new MedicationAdministrationNote(), fhirObject);
	}

	@Override
	public MedicationAdministrationNote toOpenmrsType(@Nonnull MedicationAdministrationNote openmrsObject, @Nonnull Annotation fhirObject) {
		notNull(openmrsObject, "The existing Openmrs MedicationAdministrationNote object should not be null");
		notNull(fhirObject, "The FHIR Annotation object should not be null");

		if (fhirObject.hasId()) {
			openmrsObject.setUuid(fhirObject.getId());
		}
		if (fhirObject.hasAuthor()) {
			openmrsObject.setAuthor((Provider) practitionerReferenceTranslator.toOpenmrsType((Reference) fhirObject.getAuthor()));
		}
		if (fhirObject.hasText()) {
			openmrsObject.setText(fhirObject.getText());
		}
		if (fhirObject.hasTime()) {
			openmrsObject.setRecordedTime(fhirObject.getTime());
		}
		return openmrsObject;
	}
}
