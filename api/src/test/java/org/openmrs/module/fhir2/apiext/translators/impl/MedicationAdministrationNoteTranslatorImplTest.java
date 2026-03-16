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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.PractitionerReferenceTranslator;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationNoteDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MedicationAdministrationNoteTranslatorImplTest {

	private static final String UUID = "note-uuid-123";
	private static final String TEXT = "Note text";

	@Mock
	private PractitionerReferenceTranslator practitionerReferenceTranslator;
	@Mock
	private FhirMedicationAdministrationNoteDao fhirMedicationAdministrationNoteDao;

	@InjectMocks
	private MedicationAdministrationNoteTranslatorImpl translator;

	@Test
	public void toFhirResource_shouldTranslateToAnnotation() {
		MedicationAdministrationNote note = new MedicationAdministrationNote();
		note.setUuid(UUID);
		note.setText(TEXT);
		note.setRecordedTime(new Date());
		Provider author = new Provider();
		note.setAuthor(author);

		Reference authorRef = new Reference("Practitioner/p1");
		when(practitionerReferenceTranslator.toFhirResource(any(Provider.class))).thenReturn(authorRef);

		Annotation result = translator.toFhirResource(note);

		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(UUID));
		assertThat(result.getText(), equalTo(TEXT));
		assertThat(result.getAuthor(), notNullValue());
		assertThat(result.getTime(), notNullValue());
	}

	@Test
	public void toFhirResource_shouldHandleMinimalNote() {
		MedicationAdministrationNote note = new MedicationAdministrationNote();
		note.setUuid(UUID);
		note.setText(TEXT);

		Annotation result = translator.toFhirResource(note);

		assertThat(result.getId(), equalTo(UUID));
		assertThat(result.getText(), equalTo(TEXT));
		assertThat(result.getAuthor(), equalTo(null));
		assertThat(result.getTime(), equalTo(null));
	}

	@Test
	public void toOpenmrsType_shouldTranslateFromAnnotation() {
		Annotation annotation = new Annotation();
		annotation.setId(UUID);
		annotation.setText(TEXT);
		annotation.setTime(new Date());
		annotation.setAuthor(new Reference("Practitioner/p1"));

		when(fhirMedicationAdministrationNoteDao.get(UUID)).thenReturn(null);
		when(practitionerReferenceTranslator.toOpenmrsType(any(Reference.class))).thenReturn(new Provider());

		MedicationAdministrationNote result = translator.toOpenmrsType(annotation);

		assertThat(result, notNullValue());
		assertThat(result.getUuid(), equalTo(UUID));
		assertThat(result.getText(), equalTo(TEXT));
		assertThat(result.getRecordedTime(), notNullValue());
		assertThat(result.getAuthor(), notNullValue());
	}

	@Test
	public void toOpenmrsType_shouldUpdateExistingWhenIdPresent() {
		MedicationAdministrationNote existing = new MedicationAdministrationNote();
		existing.setUuid(UUID);
		existing.setText("Old text");

		Annotation annotation = new Annotation();
		annotation.setId(UUID);
		annotation.setText("New text");

		when(fhirMedicationAdministrationNoteDao.get(UUID)).thenReturn(existing);

		MedicationAdministrationNote result = translator.toOpenmrsType(existing, annotation);

		assertThat(result.getText(), equalTo("New text"));
	}

	@Test(expected = NullPointerException.class)
	public void toFhirResource_shouldThrowWhenNull() {
		translator.toFhirResource(null);
	}

	@Test(expected = NullPointerException.class)
	public void toOpenmrsType_shouldThrowWhenAnnotationNull() {
		translator.toOpenmrsType((Annotation) null);
	}
}
