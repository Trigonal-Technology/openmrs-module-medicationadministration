/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.providers;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

import javax.annotation.Nonnull;

import java.util.HashSet;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.openmrs.module.fhir2.api.annotations.R4Provider;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.providers.util.FhirProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("medicationAdministrationFhirR4ResourceProvider")
@R4Provider
public class MedicationAdministrationFhirResourceProvider implements IResourceProvider {

	@Getter(PROTECTED)
	@Setter(value = PACKAGE)
	@Autowired
	private FhirMedicationAdministrationService fhirMedicationAdministrationService;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationAdministration.class;
	}

	@Read
	@SuppressWarnings("unused")
	public MedicationAdministration getMedicationAdministrationById(@IdParam @Nonnull IdType id) {
		MedicationAdministration resource = fhirMedicationAdministrationService.get(id.getIdPart());
		if (resource == null) {
			throw new ResourceNotFoundException("Could not find MedicationAdministration with Id " + id.getIdPart());
		}
		return resource;
	}

	public MethodOutcome createMedicationAdministration(@ResourceParam MedicationAdministration medicationAdministration) {
		return FhirProviderUtils.buildCreate(fhirMedicationAdministrationService.create(medicationAdministration));
	}

	public MethodOutcome updateMedicationAdministration(@IdParam IdType id, @ResourceParam MedicationAdministration medicationAdministration) {
		if (id == null || id.getIdPart() == null) {
			throw new InvalidRequestException("id must be specified to update");
		}
		medicationAdministration.setId(id.getIdPart());
		return FhirProviderUtils.buildUpdate(fhirMedicationAdministrationService.update(id.getIdPart(), medicationAdministration));
	}

	public OperationOutcome deleteMedicationAdministration(@IdParam @Nonnull IdType id) {
		fhirMedicationAdministrationService.delete(id.getIdPart());
		return FhirProviderUtils.buildDeleteR4();
	}

	@Search
	@SuppressWarnings("unused")
	public IBundleProvider searchForMedicationAdministrations(
			@OptionalParam(name = MedicationAdministration.SP_PATIENT, chainWhitelist = { "", Patient.SP_IDENTIFIER,
					Patient.SP_GIVEN, Patient.SP_FAMILY,
					Patient.SP_NAME }, targetTypes = Patient.class) ReferenceAndListParam patientReference,
			@OptionalParam(name = MedicationAdministration.SP_SUBJECT, chainWhitelist = { "", Patient.SP_IDENTIFIER,
					Patient.SP_GIVEN, Patient.SP_FAMILY,
					Patient.SP_NAME }, targetTypes = Patient.class) ReferenceAndListParam subjectReference,
			@OptionalParam(name = MedicationAdministration.SP_CONTEXT, chainWhitelist = { "" }, targetTypes = Encounter.class) ReferenceAndListParam contextReference,
			@OptionalParam(name = MedicationAdministration.SP_MEDICATION, chainWhitelist = { "" }, targetTypes = Medication.class) ReferenceAndListParam medicationReference,
			@OptionalParam(name = MedicationAdministration.SP_REQUEST, chainWhitelist = { "" }, targetTypes = MedicationRequest.class) ReferenceAndListParam requestReference,
			@OptionalParam(name = MedicationAdministration.SP_PERFORMER, chainWhitelist = { "", Practitioner.SP_IDENTIFIER,
					Practitioner.SP_GIVEN, Practitioner.SP_FAMILY,
					Practitioner.SP_NAME }, targetTypes = Practitioner.class) ReferenceAndListParam performerReference,
			@OptionalParam(name = MedicationAdministration.SP_STATUS) TokenAndListParam status,
			@OptionalParam(name = "effective") DateParam effectiveDate,
			@OptionalParam(name = MedicationAdministration.SP_RES_ID) TokenAndListParam id,
			@OptionalParam(name = "_lastUpdated") DateRangeParam lastUpdated,
			@IncludeParam(allow = { "MedicationAdministration:" + MedicationAdministration.SP_SUBJECT,
					"MedicationAdministration:" + MedicationAdministration.SP_CONTEXT,
					"MedicationAdministration:" + MedicationAdministration.SP_MEDICATION,
					"MedicationAdministration:" + MedicationAdministration.SP_REQUEST }) HashSet<Include> includes) {

		if (patientReference == null) {
			patientReference = subjectReference;
		}
		if (CollectionUtils.isEmpty(includes)) {
			includes = null;
		}

		MedicationAdministrationSearchParams searchParams = new MedicationAdministrationSearchParams();
		searchParams.setPatientReference(patientReference);
		searchParams.setEncounterReference(contextReference);
		searchParams.setMedicationReference(medicationReference);
		searchParams.setMedicationRequestReference(requestReference);
		searchParams.setPerformerReference(performerReference);
		searchParams.setStatus(status);
		searchParams.setEffectiveDate(effectiveDate);
		searchParams.setId(id);
		searchParams.setLastUpdated(lastUpdated);
		searchParams.setIncludes(includes);
		searchParams.setRevIncludes(null);

		return fhirMedicationAdministrationService.searchForMedicationAdministration(searchParams);
	}
}
