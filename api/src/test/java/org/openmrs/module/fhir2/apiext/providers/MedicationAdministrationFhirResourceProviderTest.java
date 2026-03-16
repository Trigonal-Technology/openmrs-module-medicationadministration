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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;

@RunWith(MockitoJUnitRunner.class)
public class MedicationAdministrationFhirResourceProviderTest {

	private static final String UUID = "med-admin-uuid-123";

	@Mock
	private FhirMedicationAdministrationService fhirMedicationAdministrationService;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private MedicationAdministrationFhirResourceProvider provider;

	@Before
	public void setup() {
		provider = new MedicationAdministrationFhirResourceProvider();
		provider.setFhirMedicationAdministrationService(fhirMedicationAdministrationService);
	}

	@Test
	public void getResourceType_shouldReturnMedicationAdministration() {
		Class<? extends IBaseResource> type = provider.getResourceType();

		assertThat(type, equalTo(MedicationAdministration.class));
	}

	@Test
	public void getMedicationAdministrationById_shouldReturnResource() {
		MedicationAdministration resource = new MedicationAdministration();
		resource.setId(UUID);
		when(fhirMedicationAdministrationService.get(UUID)).thenReturn(resource);

		MedicationAdministration result = provider.getMedicationAdministrationById(new IdType(UUID));

		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(UUID));
		verify(fhirMedicationAdministrationService).get(UUID);
	}

	@Test
	public void getMedicationAdministrationById_shouldThrowWhenNotFound() {
		when(fhirMedicationAdministrationService.get("bad-uuid")).thenThrow(
				new ResourceNotFoundException("Could not find MedicationAdministration with Id bad-uuid"));

		exceptionRule.expect(ResourceNotFoundException.class);
		exceptionRule.expectMessage("Could not find MedicationAdministration with Id bad-uuid");

		provider.getMedicationAdministrationById(new IdType("bad-uuid"));
	}

	@Test
	public void createMedicationAdministration_shouldDelegateToService() {
		MedicationAdministration input = new MedicationAdministration();
		MedicationAdministration created = new MedicationAdministration();
		created.setId(UUID);
		when(fhirMedicationAdministrationService.create(any(MedicationAdministration.class))).thenReturn(created);

		provider.createMedicationAdministration(input);

		verify(fhirMedicationAdministrationService).create(input);
	}

	@Test
	public void updateMedicationAdministration_shouldDelegateToService() {
		MedicationAdministration input = new MedicationAdministration();
		input.setId(UUID);
		MedicationAdministration updated = new MedicationAdministration();
		updated.setId(UUID);
		when(fhirMedicationAdministrationService.update(any(String.class), any(MedicationAdministration.class)))
				.thenReturn(updated);

		provider.updateMedicationAdministration(new IdType(UUID), input);

		verify(fhirMedicationAdministrationService).update(UUID, input);
	}

	@Test
	public void deleteMedicationAdministration_shouldDelegateToService() {
		provider.deleteMedicationAdministration(new IdType(UUID));

		verify(fhirMedicationAdministrationService).delete(UUID);
	}

	@Test
	public void deleteMedicationAdministration_shouldReturnOperationOutcome() {
		OperationOutcome result = provider.deleteMedicationAdministration(new IdType(UUID));

		assertThat(result, notNullValue());
	}

	@Test
	public void searchForMedicationAdministrations_shouldDelegateToService() {
		IBundleProvider bundleProvider = org.mockito.Mockito.mock(IBundleProvider.class);
		when(fhirMedicationAdministrationService.searchForMedicationAdministration(any(MedicationAdministrationSearchParams.class)))
				.thenReturn(bundleProvider);

		IBundleProvider result = provider.searchForMedicationAdministrations(
				null, null, null, null, null, null, null, null, null, null, null);

		assertThat(result, equalTo(bundleProvider));
		verify(fhirMedicationAdministrationService).searchForMedicationAdministration(any(MedicationAdministrationSearchParams.class));
	}
}
