/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.apiext.FhirMedicationAdministrationService;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.apiext.search.param.MedicationAdministrationSearchParams;
import org.openmrs.module.fhir2.apiext.translators.MedicationAdministrationTranslator;

@RunWith(MockitoJUnitRunner.class)
public class FhirMedicationAdministrationServiceImplTest {

	private static final String UUID = "test-uuid-123";
	private static final String BAD_UUID = "bad-uuid";

	@Mock
	private FhirMedicationAdministrationDao dao;

	@Mock
	private MedicationAdministrationTranslator translator;

	@Mock
	private SearchQueryInclude<MedicationAdministration> searchQueryInclude;

	@Mock
	private SearchQuery<org.openmrs.module.ipd.api.model.MedicationAdministration, MedicationAdministration, FhirMedicationAdministrationDao, MedicationAdministrationTranslator, SearchQueryInclude<MedicationAdministration>> searchQuery;

	@Mock
	private IBundleProvider bundleProvider;

	private FhirMedicationAdministrationServiceImpl service;

	private MedicationAdministration fhirResource;
	private org.openmrs.module.ipd.api.model.MedicationAdministration openmrsResource;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setup() {
		service = new FhirMedicationAdministrationServiceImpl();
		service.setDao(dao);
		service.setTranslator(translator);
		service.setSearchQuery(searchQuery);
		service.setSearchQueryInclude(searchQueryInclude);

		fhirResource = new MedicationAdministration();
		fhirResource.setId(UUID);

		openmrsResource = new org.openmrs.module.ipd.api.model.MedicationAdministration();
		openmrsResource.setUuid(UUID);
	}

	@Test
	public void get_shouldReturnFhirResource() {
		when(dao.get(UUID)).thenReturn(openmrsResource);
		when(translator.toFhirResource(openmrsResource)).thenReturn(fhirResource);

		MedicationAdministration result = service.get(UUID);

		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(UUID));
		verify(dao).get(UUID);
		verify(translator).toFhirResource(openmrsResource);
	}

	@Test
	public void get_shouldThrowWhenNotFound() {
		when(dao.get(BAD_UUID)).thenReturn(null);

		exceptionRule.expect(ResourceNotFoundException.class);
		exceptionRule.expectMessage("Resource of type MedicationAdministration with ID " + BAD_UUID + " is not known");

		service.get(BAD_UUID);
	}

	@Test
	public void delete_shouldDelete() {
		when(dao.delete(UUID)).thenReturn(openmrsResource);

		service.delete(UUID);

		verify(dao).delete(UUID);
	}

	@Test
	public void get_shouldUseTranslator() {
		when(dao.get(UUID)).thenReturn(openmrsResource);
		when(translator.toFhirResource(openmrsResource)).thenReturn(fhirResource);

		MedicationAdministration result = service.get(UUID);

		assertThat(result.getId(), equalTo(UUID));
	}

	@Test
	public void searchForMedicationAdministration_shouldDelegateToSearchQuery() {
		MedicationAdministrationSearchParams params = MedicationAdministrationSearchParams.builder().build();
		when(searchQuery.getQueryResults(any(), any(), any(), any())).thenReturn(bundleProvider);

		IBundleProvider result = service.searchForMedicationAdministration(params);

		assertThat(result, equalTo(bundleProvider));
		verify(searchQuery).getQueryResults(any(), any(), any(), any());
	}
}
