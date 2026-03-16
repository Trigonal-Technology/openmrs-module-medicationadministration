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

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.Concept;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.ConceptTranslator;
import org.openmrs.module.fhir2.api.translators.PractitionerReferenceTranslator;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationPerformerDao;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MedicationAdministrationPerformerTranslatorImplTest {

	private static final String UUID = "performer-uuid-123";

	@Mock
	private ConceptTranslator conceptTranslator;
	@Mock
	private FhirMedicationAdministrationPerformerDao fhirMedicationAdministrationPerformerDao;
	@Mock
	private PractitionerReferenceTranslator practitionerReferenceTranslator;

	@InjectMocks
	private MedicationAdministrationPerformerTranslatorImpl translator;

	@Test
	public void toFhirResource_shouldTranslateToPerformerComponent() {
		MedicationAdministrationPerformer performer = new MedicationAdministrationPerformer();
		performer.setUuid(UUID);
		performer.setActor(new Provider());
		performer.setFunction(new Concept());

		Reference actorRef = new Reference("Practitioner/p1");
		CodeableConcept functionCc = new CodeableConcept();
		when(practitionerReferenceTranslator.toFhirResource(any(Provider.class))).thenReturn(actorRef);
		when(conceptTranslator.toFhirResource(any(Concept.class))).thenReturn(functionCc);

		MedicationAdministration.MedicationAdministrationPerformerComponent result =
				translator.toFhirResource(performer);

		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(UUID));
		assertThat(result.getActor(), equalTo(actorRef));
		assertThat(result.getFunction(), equalTo(functionCc));
	}

	@Test
	public void toFhirResource_shouldHandleMinimalPerformer() {
		MedicationAdministrationPerformer performer = new MedicationAdministrationPerformer();
		performer.setUuid(UUID);

		MedicationAdministration.MedicationAdministrationPerformerComponent result =
				translator.toFhirResource(performer);

		assertThat(result.getId(), equalTo(UUID));
		assertThat(result.hasActor(), equalTo(false));
		assertThat(result.hasFunction(), equalTo(false));
	}

	@Test
	public void toOpenmrsType_shouldTranslateFromPerformerComponent() {
		MedicationAdministration.MedicationAdministrationPerformerComponent comp =
				new MedicationAdministration.MedicationAdministrationPerformerComponent();
		comp.setId(UUID);
		comp.setActor(new Reference("Practitioner/p1"));
		CodeableConcept function = new CodeableConcept();
		function.addCoding().setCode("performer");
		comp.setFunction(function);

		when(fhirMedicationAdministrationPerformerDao.get(UUID)).thenReturn(null);
		when(practitionerReferenceTranslator.toOpenmrsType(any(Reference.class))).thenReturn(new Provider());
		when(conceptTranslator.toOpenmrsType(any(CodeableConcept.class))).thenReturn(new Concept());

		MedicationAdministrationPerformer result = translator.toOpenmrsType(comp);

		assertThat(result, notNullValue());
		assertThat(result.getUuid(), equalTo(UUID));
		assertThat(result.getActor(), notNullValue());
		assertThat(result.getFunction(), notNullValue());
	}

	@Test
	public void toOpenmrsType_shouldUpdateExistingWhenDaoReturnsMatch() {
		MedicationAdministrationPerformer existing = new MedicationAdministrationPerformer();
		existing.setUuid(UUID);

		MedicationAdministration.MedicationAdministrationPerformerComponent comp =
				new MedicationAdministration.MedicationAdministrationPerformerComponent();
		comp.setId(UUID);
		comp.setActor(new Reference("Practitioner/p1"));

		when(fhirMedicationAdministrationPerformerDao.get(UUID)).thenReturn(existing);
		when(practitionerReferenceTranslator.toOpenmrsType(any(Reference.class))).thenReturn(new Provider());

		MedicationAdministrationPerformer result = translator.toOpenmrsType(existing, comp);

		assertThat(result.getActor(), notNullValue());
	}

	@Test(expected = NullPointerException.class)
	public void toFhirResource_shouldThrowWhenNull() {
		translator.toFhirResource(null);
	}

	@Test(expected = NullPointerException.class)
	public void toOpenmrsType_shouldThrowWhenComponentNull() {
		translator.toOpenmrsType((MedicationAdministration.MedicationAdministrationPerformerComponent) null);
	}
}
