/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.dao.impl;

import javax.annotation.Nonnull;
import javax.persistence.criteria.From;

import java.util.Optional;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.dao.internals.OpenmrsFhirCriteriaContext;
import org.openmrs.module.fhir2.apiext.dao.FhirMedicationAdministrationDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FhirMedicationAdministrationDaoImpl extends BaseFhirDao<MedicationAdministration> implements FhirMedicationAdministrationDao {

	@Override
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.GET_MEDICATION_ADMINISTRATIONS)
	public MedicationAdministration get(@Nonnull String uuid) {
		return super.get(uuid);
	}

	@Override
	@Authorized(PrivilegeConstants.EDIT_MEDICATION_ADMINISTRATION)
	public MedicationAdministration createOrUpdate(@Nonnull MedicationAdministration newEntry) {
		return super.createOrUpdate(newEntry);
	}

	@Override
	@Authorized(PrivilegeConstants.DELETE_MEDICATION_ADMINISTRATION)
	public MedicationAdministration delete(@Nonnull String uuid) {
		return super.delete(uuid);
	}

	@Override
	protected <U> void setupSearchParams(@Nonnull OpenmrsFhirCriteriaContext<MedicationAdministration, U> criteriaContext,
			@Nonnull SearchParameterMap theParams) {
		theParams.getParameters().forEach(entry -> {
			switch (entry.getKey()) {
				case FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER:
					entry.getValue()
							.forEach(e -> getSearchQueryHelper().handleEncounterReference(criteriaContext,
									(ReferenceAndListParam) e.getParam(), "e"));
					break;
				case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
					entry.getValue().forEach(patientReference -> getSearchQueryHelper().handlePatientReference(criteriaContext,
							(ReferenceAndListParam) patientReference.getParam(), "patient"));
					break;
				case FhirConstants.MEDICATION_REFERENCE_SEARCH_HANDLER:
					From<?, ?> medicationAlias = criteriaContext.addJoin("drug", "d");
					entry.getValue().forEach(d -> getSearchQueryHelper().handleMedicationReference(criteriaContext,
							medicationAlias, (ReferenceAndListParam) d.getParam()).ifPresent(criteriaContext::addPredicate));
					break;
				case FhirConstants.STATUS_SEARCH_HANDLER:
					entry.getValue()
							.forEach(param -> handleStatus(criteriaContext, (TokenAndListParam) param.getParam())
									.ifPresent(criteriaContext::addPredicate));
					break;
				case FhirConstants.COMMON_SEARCH_HANDLER:
					handleCommonSearchParameters(criteriaContext, entry.getValue()).ifPresent(criteriaContext::addPredicate);
					break;
				default:
					break;
			}
		});
	}

	private <U> Optional<javax.persistence.criteria.Predicate> handleStatus(
			OpenmrsFhirCriteriaContext<MedicationAdministration, U> criteriaContext,
			TokenAndListParam tokenAndListParam) {
		return handleAndListParam(criteriaContext.getCriteriaBuilder(), tokenAndListParam, token -> {
			if (token.getValue() != null) {
				MedicationAdministration.MedicationAdministrationStatus status =
						MedicationAdministration.MedicationAdministrationStatus.fromCode(token.getValue());
				if (status != null) {
					return Optional.of(criteriaContext.getCriteriaBuilder()
							.equal(criteriaContext.getRoot().get("status"), status));
				}
			}
			return Optional.empty();
		});
	}
}
