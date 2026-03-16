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
import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.ConceptTranslator;
import org.openmrs.module.fhir2.api.translators.EncounterReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.MedicationReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.MedicationRequestReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationNoteTranslator;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationPerformerTranslator;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;

@RunWith(MockitoJUnitRunner.class)
public class MedicationAdministrationTranslatorImplTest {

	private static final String UUID = "test-uuid-123";
	private static final String PATIENT_UUID = "patient-uuid";
	private static final String ENCOUNTER_UUID = "encounter-uuid";
	private static final String DRUG_UUID = "drug-uuid";
	private static final String ORDER_UUID = "order-uuid";

	@Mock
	private PatientReferenceTranslator patientReferenceTranslator;
	@Mock
	private EncounterReferenceTranslator encounterReferenceTranslator;
	@Mock
	private MedicationRequestReferenceTranslator medicationRequestReferenceTranslator;
	@Mock
	private MedicationReferenceTranslator medicationReferenceTranslator;
	@Mock
	private MedicationAdministrationPerformerTranslator medicationAdministrationPerformerTranslator;
	@Mock
	private MedicationAdministrationNoteTranslator medicationAdministrationNoteTranslator;
	@Mock
	private ConceptTranslator conceptTranslator;

	@InjectMocks
	private MedicationAdministrationTranslatorImpl translator;

	private org.openmrs.module.ipd.api.model.MedicationAdministration openmrsObject;

	@Before
	public void setup() {
		openmrsObject = new org.openmrs.module.ipd.api.model.MedicationAdministration();
		openmrsObject.setUuid(UUID);
		openmrsObject.setStatus(org.openmrs.module.ipd.api.model.MedicationAdministration.MedicationAdministrationStatus.COMPLETED);
		openmrsObject.setAdministeredDateTime(new Date());
		openmrsObject.setDose(5.0);
		openmrsObject.setDosingInstructions("Take with food");
	}

	@Test
	public void toFhirResource_shouldTranslateToFhirR4() {
		org.openmrs.Patient patient = new org.openmrs.Patient();
		patient.setUuid(PATIENT_UUID);
		openmrsObject.setPatient(patient);

		Concept doseUnits = new Concept();
		doseUnits.setUuid("unit-uuid");
		openmrsObject.setDoseUnits(doseUnits);

		Reference patientRef = new Reference("Patient/" + PATIENT_UUID);
		when(patientReferenceTranslator.toFhirResource(any(org.openmrs.Patient.class))).thenReturn(patientRef);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir, notNullValue());
		assertThat(fhir.getId(), equalTo(UUID));
		assertThat(fhir.getStatus(), equalTo(MedicationAdministration.MedicationAdministrationStatus.COMPLETED));
		assertThat(fhir.getSubject(), notNullValue());
		assertThat(fhir.getSubject().getReference(), equalTo("Patient/" + PATIENT_UUID));
		assertThat(fhir.getEffectiveDateTimeType(), notNullValue());
		assertThat(fhir.getDosage(), notNullValue());
		assertThat(fhir.getDosage().getText(), equalTo("Take with food"));
		assertThat(fhir.getDosage().getDose().getValue().doubleValue(), equalTo(5.0));
	}

	@Test
	public void toFhirResource_shouldHandleMinimalObject() {
		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir, notNullValue());
		assertThat(fhir.getId(), equalTo(UUID));
		assertThat(fhir.getStatus(), equalTo(MedicationAdministration.MedicationAdministrationStatus.COMPLETED));
		assertThat(fhir.hasSubject(), equalTo(false));
		assertThat(fhir.hasContext(), equalTo(false));
		assertThat(fhir.hasMedication(), equalTo(false));
	}

	@Test
	public void toFhirResource_shouldMapEncounter() {
		org.openmrs.Encounter encounter = new org.openmrs.Encounter();
		encounter.setUuid(ENCOUNTER_UUID);
		openmrsObject.setEncounter(encounter);

		Reference encounterRef = new Reference("Encounter/" + ENCOUNTER_UUID);
		when(encounterReferenceTranslator.toFhirResource(any(org.openmrs.Encounter.class))).thenReturn(encounterRef);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getContext(), notNullValue());
		assertThat(fhir.getContext().getReference(), equalTo("Encounter/" + ENCOUNTER_UUID));
	}

	@Test
	public void toFhirResource_shouldMapMedication() {
		Drug drug = new Drug();
		drug.setUuid(DRUG_UUID);
		openmrsObject.setDrug(drug);

		Reference medicationRef = new Reference("Medication/" + DRUG_UUID);
		when(medicationReferenceTranslator.toFhirResource(any(Drug.class))).thenReturn(medicationRef);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getMedication(), notNullValue());
		assertThat(fhir.getMedicationReference().getReference(), equalTo("Medication/" + DRUG_UUID));
	}

	@Test
	public void toFhirResource_shouldMapRequest() {
		DrugOrder order = new DrugOrder();
		order.setUuid(ORDER_UUID);
		openmrsObject.setDrugOrder(order);

		Reference requestRef = new Reference("MedicationRequest/" + ORDER_UUID);
		when(medicationRequestReferenceTranslator.toFhirResource(any(DrugOrder.class))).thenReturn(requestRef);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getRequest(), notNullValue());
		assertThat(fhir.getRequest().getReference(), equalTo("MedicationRequest/" + ORDER_UUID));
	}

	@Test
	public void toFhirResource_shouldMapPerformers() {
		MedicationAdministrationPerformer performer = new MedicationAdministrationPerformer();
		performer.setActor(new Provider());
		Set<MedicationAdministrationPerformer> performers = new HashSet<>();
		performers.add(performer);
		openmrsObject.setPerformers(performers);

		MedicationAdministration.MedicationAdministrationPerformerComponent comp =
				new MedicationAdministration.MedicationAdministrationPerformerComponent();
		comp.setActor(new Reference("Practitioner/p1"));
		when(medicationAdministrationPerformerTranslator.toFhirResource(any(org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer.class)))
				.thenReturn(comp);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getPerformer(), notNullValue());
		assertThat(fhir.getPerformer().size(), equalTo(1));
	}

	@Test
	public void toFhirResource_shouldMapNotes() {
		MedicationAdministrationNote note = new MedicationAdministrationNote();
		Set<MedicationAdministrationNote> notes = new HashSet<>();
		notes.add(note);
		openmrsObject.setNotes(notes);

		Annotation annotation = new Annotation();
		annotation.setText("Note text");
		when(medicationAdministrationNoteTranslator.toFhirResource(any(MedicationAdministrationNote.class)))
				.thenReturn(annotation);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getNote(), notNullValue());
		assertThat(fhir.getNote().size(), equalTo(1));
		assertThat(fhir.getNote().get(0).getText(), equalTo("Note text"));
	}

	@Test
	public void toFhirResource_shouldMapRouteAndSite() {
		Concept route = new Concept();
		Concept site = new Concept();
		openmrsObject.setRoute(route);
		openmrsObject.setSite(site);

		CodeableConcept routeCc = new CodeableConcept();
		CodeableConcept siteCc = new CodeableConcept();
		when(conceptTranslator.toFhirResource(route)).thenReturn(routeCc);
		when(conceptTranslator.toFhirResource(site)).thenReturn(siteCc);

		MedicationAdministration fhir = translator.toFhirResource(openmrsObject);

		assertThat(fhir.getDosage().getRoute(), equalTo(routeCc));
		assertThat(fhir.getDosage().getSite(), equalTo(siteCc));
	}
}
