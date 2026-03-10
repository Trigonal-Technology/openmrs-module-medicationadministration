/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ipd.api.model;

import org.openmrs.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * The MedicationAdministration class records detailed information about the
 * provision of a supply of a medication
 * with the intention that it is subsequently consumed by a patient (usually in
 * response to a prescription).
 *
 * @see <a href="https://www.hl7.org/fhir/medicationadministration.html">
 *      https://www.hl7.org/fhir/medicationadministration.html
 *      </a>
 * @since 2.5.12
 */
@Entity
@Table(name = "medication_administration")
public class MedicationAdministration extends BaseFormRecordableOpenmrsData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medication_administration_id")
	private Integer medicationAdministrationId;

	/**
	 * FHIR:subject
	 * Patient for whom the medication is intended
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "patient_id")
	private Patient patient;

	/**
	 * FHIR:context
	 * Encounter when the dispensing event occurred
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "encounter_id")
	private Encounter encounter;

	/**
	 * FHIR:medication.reference(Medication)
	 * Corresponds to drugOrder.drug
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "drug_id")
	private Drug drug;

	/**
	 * FHIR:performer
	 * 
	 * @see <a href=
	 *      "https://www.hl7.org/fhir/medicationadministration-definitions.html#MedicationAdministration.performer">
	 *      https://www.hl7.org/fhir/medicationadministration-definitions.html#MedicationAdministration.performer
	 *      </a>specification, It should be assumed that the actor can be the
	 *      performer, verifier or witness of the medication administration.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "medication_administration_id")
	private Set<MedicationAdministrationPerformer> performers;

	/**
	 * FHIR:request
	 * The drug order that led to this administration event;
	 * note that request maps to a "MedicationRequest" FHIR resource
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "drug_order_id")
	private DrugOrder drugOrder;

	public enum MedicationAdministrationStatus {
		INPROGRESS("in-progress"),
		NOTDONE("not-done"),
		ONHOLD("on-hold"),
		COMPLETED("completed"),
		ENTEREDINERROR("entered-in-error"),
		STOPPED("stopped"),
		DECLINED("declined"),
		UNKNOWN("unknown");

		private final String code;

		MedicationAdministrationStatus(String code) {
			this.code = code;
		}

		public String toCode() {
			return code;
		}

		public static MedicationAdministrationStatus fromCode(String code) {
			for (MedicationAdministrationStatus s : values()) {
				if (s.code.equalsIgnoreCase(code)) {
					return s;
				}
			}
			return null;
		}
	}

	/**
	 * FHIR:status
	 * 
	 * @see <a href=
	 *      "https://www.hl7.org/fhir/valueset-medicationadministration-status.html">
	 *      https://www.hl7.org/fhir/valueset-medicationadministration-status.html
	 *      </a>
	 *      i.e. in-progress, cancelled, on-hold, completed, entered-in-error,
	 *      stopped, declined, unknown
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MedicationAdministrationStatus status;

	/**
	 * FHIR:statusReason.statusReasonCodeableConcept
	 * 
	 * @see <a href=
	 *      "https://www.hl7.org/fhir/valueset-medicationadministration-status-reason.html">
	 *      https://www.hl7.org/fhir/valueset-medicationadministration-status-reason.html
	 *      </a>
	 *      i.e "Stock Out"
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "status_reason")
	private Concept statusReason;

	/**
	 * FHIR:effective.effectiveDateTime
	 * Time when the medication was administered
	 */
	@Column(name = "administered_date_time")
	private Date administeredDateTime;

	/**
	 * FHIR:dosage.text
	 * The dosage instructions should reflect the dosage of the medication that was
	 * administered.
	 */
	@Column(name = "dosing_instructions", length = 65535)
	private String dosingInstructions;

	/**
	 * FHIR:dosage.dose.value
	 * Numbered Value of the amount of the medication given at one administration
	 * event
	 */
	@Column(name = "dose")
	private Double dose;

	/**
	 * FHIR:dosage.dose.unit
	 * Units of the amount of the medication given at one administration event. For
	 * example, mg, mL, etc.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "dose_units")
	private Concept doseUnits;

	/**
	 * FHIR:dosage.route
	 * Path of substance into body, For example, topical, intravenous, etc.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "route")
	private Concept route;

	/**
	 * FHIR:dosage.site
	 * Body site administered to, For example, left arm, etc.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "site")
	private Concept site;

	/**
	 * FHIR:note
	 * 
	 * @see <a href="https://hl7.org/fhir/R4/datatypes.html#Annotation">
	 *      https://hl7.org/fhir/R4/datatypes.html#Annotation
	 *      </a>
	 *      Notes or additional information about the medication administration.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "medication_administration_id")
	private Set<MedicationAdministrationNote> notes;

	public MedicationAdministration() {
	}

	/**
	 * @see BaseOpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getMedicationAdministrationId();
	}

	/**
	 * @see BaseOpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		setMedicationAdministrationId(id);
	}

	public Integer getMedicationAdministrationId() {
		return medicationAdministrationId;
	}

	public void setMedicationAdministrationId(Integer medicationAdministrationId) {
		this.medicationAdministrationId = medicationAdministrationId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public Set<MedicationAdministrationPerformer> getPerformers() {
		return performers;
	}

	public void setPerformers(Set<MedicationAdministrationPerformer> performers) {
		this.performers = performers;
	}

	public DrugOrder getDrugOrder() {
		return drugOrder;
	}

	public void setDrugOrder(DrugOrder drugOrder) {
		this.drugOrder = drugOrder;
	}

	public MedicationAdministrationStatus getStatus() {
		return status;
	}

	public void setStatus(MedicationAdministrationStatus status) {
		this.status = status;
	}

	public Concept getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(Concept statusReason) {
		this.statusReason = statusReason;
	}

	public Date getAdministeredDateTime() {
		return administeredDateTime;
	}

	public void setAdministeredDateTime(Date administeredDateTime) {
		this.administeredDateTime = administeredDateTime;
	}

	public Double getDose() {
		return dose;
	}

	public void setDose(Double dose) {
		this.dose = dose;
	}

	public String getDosingInstructions() {
		return dosingInstructions;
	}

	public void setDosingInstructions(String dosingInstructions) {
		this.dosingInstructions = dosingInstructions;
	}

	public Concept getDoseUnits() {
		return doseUnits;
	}

	public void setDoseUnits(Concept doseUnits) {
		this.doseUnits = doseUnits;
	}

	public Concept getRoute() {
		return route;
	}

	public void setRoute(Concept route) {
		this.route = route;
	}

	public Concept getSite() {
		return site;
	}

	public void setSite(Concept site) {
		this.site = site;
	}

	public Set<MedicationAdministrationNote> getNotes() {
		return notes;
	}

	public void setNotes(Set<MedicationAdministrationNote> notes) {
		this.notes = notes;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		MedicationAdministration other = (MedicationAdministration) obj;
		boolean yes = Objects.equals(this.medicationAdministrationId, other.medicationAdministrationId)
				|| Objects.equals(this.getUuid(), other.getUuid());
		return yes;
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(this.getUuid());
		return hash;
	}

}
