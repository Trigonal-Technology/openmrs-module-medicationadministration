/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ipd.api.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.module.ipd.api.db.MedicationAdministrationDAO;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.ipd.api.service.MedicationAdministrationService;

@RunWith(MockitoJUnitRunner.class)
public class MedicationAdministrationServiceImplTest {

	private static final String UUID = "med-admin-uuid-123";
	private static final String VOID_REASON = "Administered elsewhere";

	@Mock
	private MedicationAdministrationDAO dao;

	@InjectMocks
	private MedicationAdministrationServiceImpl service;

	@Test
	public void getMedicationAdministrationByUuid_shouldDelegateToDao() {
		MedicationAdministration expected = new MedicationAdministration();
		expected.setUuid(UUID);
		when(dao.getMedicationAdministrationByUuid(UUID)).thenReturn(expected);

		MedicationAdministration result = service.getMedicationAdministrationByUuid(UUID);

		assertThat(result, notNullValue());
		assertThat(result.getUuid(), equalTo(UUID));
		verify(dao).getMedicationAdministrationByUuid(UUID);
	}

	@Test
	public void getMedicationAdministrationByUuid_shouldReturnNullWhenNotFound() {
		when(dao.getMedicationAdministrationByUuid("bad-uuid")).thenReturn(null);

		MedicationAdministration result = service.getMedicationAdministrationByUuid("bad-uuid");

		assertThat(result, nullValue());
	}

	@Test
	public void saveMedicationAdministration_shouldDelegateToDao() {
		MedicationAdministration ma = new MedicationAdministration();
		ma.setUuid(UUID);
		when(dao.saveMedicationAdministration(any(MedicationAdministration.class))).thenReturn(ma);

		MedicationAdministration result = service.saveMedicationAdministration(ma);

		assertThat(result, notNullValue());
		verify(dao).saveMedicationAdministration(ma);
	}

	@Test
	public void voidMedicationAdministration_shouldSetVoidedAndSave() {
		MedicationAdministration ma = new MedicationAdministration();
		ma.setUuid(UUID);
		ma.setVoided(false);

		service.voidMedicationAdministration(ma, VOID_REASON);

		assertThat(ma.getVoided(), equalTo(true));
		assertThat(ma.getVoidReason(), equalTo(VOID_REASON));
		verify(dao).saveMedicationAdministration(ma);
	}
}
