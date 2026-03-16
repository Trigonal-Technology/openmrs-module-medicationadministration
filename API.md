# Medication Administration Module API Reference

The **medication-administration** module is a **core API module** that provides the domain model and service layer for recording medication administration events. It does **not** expose REST endpoints directly.

**REST APIs** for medication administration are provided by the **IPD module**. See [IPD Module API.md](../openmrs-module-ipd/API.md) for REST endpoints such as:
- `POST /ipd/scheduledMedicationAdministrations`
- `POST /ipd/adhocMedicationAdministrations`
- `PUT /ipd/adhocMedicationAdministrations/{uuid}`

---

## Table of Contents

1. [Overview](#overview)
2. [Comparison with Bahmni Upstream](#comparison-with-bahmni-upstream)
3. [FHIR R4 MedicationAdministration](#fhir-r4-medicationadministration-nidan)
4. [Java Service API](#java-service-api)
5. [Domain Model](#domain-model)
6. [Privileges](#privileges)
7. [Usage Example](#usage-example)

---

## Overview

This module implements the FHIR [MedicationAdministration](https://www.hl7.org/fhir/medicationadministration.html) resource concept for OpenMRS. It stores:

- **MedicationAdministration** – Records when a medication was administered to a patient
- **MedicationAdministrationPerformer** – Who performed/verified/witnessed the administration
- **MedicationAdministrationNote** – Annotations/notes about the administration

The module is consumed by the **IPD (Inpatient Department)** module, which adds REST controllers, scheduling, and ward integration.

---

## Comparison with Bahmni Upstream

This module is a **fork of [Bahmni/openmrs-module-medicationadministration](https://github.com/Bahmni/openmrs-module-medicationadministration)**. The Nidan version was adapted for **standalone OpenMRS** by removing Bahmni-specific dependencies and replacing FHIR2 extension components with native OpenMRS implementations.

### Removed Bahmni Dependencies

| Dependency | Bahmni Usage | Nidan Change |
|------------|--------------|--------------|
| **bahmnicore** | Required in test config; used for Bahmni-specific services | Removed from main config; test config may still reference (legacy) |
| **FHIR2 apiext activator** | `org.openmrs.module.fhir2.apiext.MedicationAdministrationActivator` | Replaced with `org.openmrs.module.ipd.api.MedicationAdministrationActivator` (in-module) |
| **FhirMedicationAdministrationDao** | FHIR2 extension DAO for medication administration | Replaced with `HibernateMedicationAdministrationDAO` (native Hibernate) |
| **fhir2-omod** | Optional FHIR2 OMOD dependency | Commented out in pom; not required at runtime |

### Package Relocation

| Component | Bahmni | Nidan |
|-----------|--------|-------|
| Activator | `org.openmrs.module.fhir2.apiext` | `org.openmrs.module.ipd.api` |
| Model, Service, DAO | FHIR2 extension packages | `org.openmrs.module.ipd.api.model`, `.service`, `.db` |

### OpenMRS Compatibility Changes

1. **Activator**: Simple `BaseModuleActivator` subclass in the module; no FHIR2 extension bootstrap.
2. **DAO layer**: Uses `MedicationAdministrationDAO` + `HibernateMedicationAdministrationDAO` with standard Hibernate `SessionFactory` and `Criteria` API.
3. **Service layer**: `MedicationAdministrationServiceImpl` extends `BaseOpenmrsService`; no FHIR translation layer.
4. **Models**: Use OpenMRS base classes (`BaseFormRecordableOpenmrsData`, `BaseOpenmrsData`) from `org.openmrs`; no FHIR resource wrappers.

### Liquibase / Database Changes

- **MySQL vs PostgreSQL**: Split `gen_random_uuid()` (PostgreSQL) vs `uuid()` (MySQL/MariaDB) in concept-creation changeSets.
- **Status column (BAH-4061)**: Status changed from concept FK (`int`) to `varchar(255)` enum (`INPROGRESS`, `COMPLETED`, etc.) for simpler queries and MySQL compatibility.
- **Collation**: MySQL changeSets use `COLLATE utf8mb4_unicode_ci` where needed for string comparisons.
- **Reserved keyword**: `function` column renamed to `performer_function` in `medication_administration_performer` (MySQL reserved word).

### FHIR R4 MedicationAdministration (Nidan)

The module now supports **FHIR R4 MedicationAdministration** via the FHIR2 extension layer (`org.openmrs.module.fhir2.apiext`):

- **Resource provider**: `MedicationAdministrationFhirResourceProvider` – `@R4Provider` for FHIR REST
- **Service**: `FhirMedicationAdministrationService` – `get`, `create`, `update`, `delete`, `searchForMedicationAdministration`
- **Translator**: `MedicationAdministrationTranslatorImpl` – domain ↔ FHIR R4
- **Search params**: `patient`, `subject`, `context` (encounter), `medication`, `request`, `performer`, `status`, `effective`, `_id`, `_lastUpdated`
- **Includes**: `MedicationAdministration:subject`, `:context`, `:medication`, `:request`

API responses align with [FHIR R4 MedicationAdministration](https://www.hl7.org/fhir/medicationadministration.html) (resource type, required elements, search params).
Requires `fhir2-api` and `fhir2-omod` at runtime.

### API Differences

| Aspect | Bahmni Upstream | Nidan Fork |
|--------|-----------------|------------|
| **Service interface** | `FhirMedicationAdministrationService` extends `FhirService` | Both: `MedicationAdministrationService` (domain CRUD) and `FhirMedicationAdministrationService` (FHIR) |
| **Primary API** | `searchForMedicationAdministration` → `IBundleProvider` | Same FHIR search; plus `getMedicationAdministrationByUuid`, `saveMedicationAdministration`, `voidMedicationAdministration` (domain) |
| **Data types** | Works with `org.hl7.fhir.r4.model.MedicationAdministration` via translators | Works directly with `org.openmrs.module.ipd.api.model.MedicationAdministration` |
| **REST exposure** | FHIR R4 endpoints via FHIR2 module: `/ws/fhir2/R4/MedicationAdministration` (search, read, create, update) | No FHIR endpoints. REST via **IPD module** only: `/ipd/scheduledMedicationAdministrations`, `/ipd/adhocMedicationAdministrations`, etc. |
| **Search** | `MedicationAdministrationSearchParams` for FHIR search (patient, status, etc.) | No search API in medication-administration module; IPD handles filtering |
| **Translators** | `MedicationAdministrationTranslator`, `MedicationAdministrationPerformerTranslator`, `MedicationAdministrationNoteTranslator` (domain ↔ FHIR) | None; domain model used directly |

**Summary:** Bahmni exposes a **FHIR R4 MedicationAdministration API** (search + CRUD) through the FHIR2 module. Nidan exposes a **domain-model Java API** only; REST access is via the IPD module’s custom endpoints, not FHIR.

---

## Java Service API

### MedicationAdministrationService

**Package:** `org.openmrs.module.ipd.api.service`

**Spring bean name:** `medicationAdministrationService`

| Method | Description | Returns |
|--------|-------------|---------|
| `getMedicationAdministrationByUuid(String uuid)` | Fetch a medication administration by UUID | `MedicationAdministration` or null |
| `saveMedicationAdministration(MedicationAdministration ma)` | Save or update a medication administration | `MedicationAdministration` |
| `voidMedicationAdministration(MedicationAdministration ma, String reason)` | Void a medication administration with a reason | void |

**Example:**
```java
MedicationAdministrationService service = Context.getService(MedicationAdministrationService.class);

// Get by UUID
MedicationAdministration ma = service.getMedicationAdministrationByUuid("abc-123-def");

// Save new or updated
MedicationAdministration saved = service.saveMedicationAdministration(ma);

// Void
service.voidMedicationAdministration(ma, "Entered in error");
```

---

## Domain Model

### MedicationAdministration

**Package:** `org.openmrs.module.ipd.api.model`

**Table:** `medication_administration`

| Field | Type | Description |
|-------|------|-------------|
| medicationAdministrationId | Integer | Primary key |
| patient | Patient | Patient who received the medication |
| encounter | Encounter | Encounter when administered |
| drug | Drug | Drug administered |
| drugOrder | DrugOrder | Prescription/order that led to this administration |
| performers | Set&lt;MedicationAdministrationPerformer&gt; | Who performed/verified/witnessed |
| status | MedicationAdministrationStatus | in-progress, completed, entered-in-error, etc. |
| statusReason | Concept | Reason for status (e.g. "Stock Out") |
| administeredDateTime | Date | When the medication was administered |
| dosingInstructions | String | Free-text dosage instructions |
| dose | Double | Amount administered |
| doseUnits | Concept | Units (mg, mL, etc.) |
| route | Concept | Route (oral, IV, etc.) |
| site | Concept | Body site administered to |
| notes | Set&lt;MedicationAdministrationNote&gt; | Annotations |

**Status enum values:**
- `INPROGRESS` (in-progress)
- `NOTDONE` (not-done)
- `ONHOLD` (on-hold)
- `COMPLETED` (completed)
- `ENTEREDINERROR` (entered-in-error)
- `STOPPED` (stopped)
- `DECLINED` (declined)
- `UNKNOWN` (unknown)

---

### MedicationAdministrationPerformer

**Package:** `org.openmrs.module.ipd.api.model`

**Table:** `medication_administration_performer`

| Field | Type | Description |
|-------|------|-------------|
| medicationAdministrationPerformerId | Integer | Primary key |
| actor | Provider | Provider who performed/verified/witnessed |
| function | Concept | Role: performer, verifier, witness (DB column: `performer_function`) |

---

### MedicationAdministrationNote

**Package:** `org.openmrs.module.ipd.api.model`

**Table:** `medication_administration_note`

| Field | Type | Description |
|-------|------|-------------|
| medicationAdministrationNoteId | Integer | Primary key |
| author | Provider | Who wrote the note |
| recordedTime | Date | When the note was recorded |
| text | String | Note content |

---

## Privileges

| Privilege | Description |
|-----------|-------------|
| Get Medication Administrations | Read medication administration records |
| Edit Medication Administration | Create/update medication administrations |
| Delete Medication Administration | Void medication administrations |

---

## Usage Example

Creating a medication administration from Java (e.g. in another module):

```java
import org.openmrs.api.context.Context;
import org.openmrs.*;
import org.openmrs.module.ipd.api.model.*;
import org.openmrs.module.ipd.api.service.MedicationAdministrationService;

// Get services
MedicationAdministrationService maService = Context.getService(MedicationAdministrationService.class);
PatientService patientService = Context.getPatientService();
EncounterService encounterService = Context.getEncounterService();
DrugOrderService orderService = Context.getOrderService();
ConceptService conceptService = Context.getConceptService();

// Load references
Patient patient = patientService.getPatientByUuid("patient-uuid");
Encounter encounter = encounterService.getEncounterByUuid("encounter-uuid");
DrugOrder drugOrder = (DrugOrder) orderService.getOrderByUuid("order-uuid");
Drug drug = drugOrder.getDrug();
Concept route = conceptService.getConceptByUuid("route-concept-uuid");
Concept doseUnits = conceptService.getConceptByUuid("dose-units-uuid");
Provider performer = Context.getProviderService().getProviderByUuid("provider-uuid");

// Create performer
MedicationAdministrationPerformer performerEntity = new MedicationAdministrationPerformer();
performerEntity.setActor(performer);
performerEntity.setFunction(conceptService.getConceptByName("performer")); // or appropriate concept
performerEntity.setCreator(Context.getAuthenticatedUser());
performerEntity.setDateCreated(new Date());

// Create medication administration
MedicationAdministration ma = new MedicationAdministration();
ma.setPatient(patient);
ma.setEncounter(encounter);
ma.setDrugOrder(drugOrder);
ma.setDrug(drug);
ma.setStatus(MedicationAdministration.MedicationAdministrationStatus.COMPLETED);
ma.setAdministeredDateTime(new Date());
ma.setDose(500.0);
ma.setDoseUnits(doseUnits);
ma.setRoute(route);
ma.setDosingInstructions("Take with food");
ma.setPerformers(Collections.singleton(performerEntity));
ma.setCreator(Context.getAuthenticatedUser());
ma.setDateCreated(new Date());

// Save
MedicationAdministration saved = maService.saveMedicationAdministration(ma);
```

---

## REST API (via IPD Module)

For HTTP/REST access to medication administration, use the **IPD module** endpoints. See [IPD API.md](../openmrs-module-ipd/API.md) sections:

- **1. Medication Administration** – Create scheduled/adhoc administrations, update adhoc
- **3. Visit Medications** – Get prescribed orders and emergency medications
- **4. Schedule** – Medication slots and schedules

**Example REST request (via IPD):**
```bash
curl -X POST "http://localhost:8080/openmrs/ws/rest/v1/ipd/adhocMedicationAdministrations" \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=..." \
  -d '{
    "patientUuid": "patient-uuid",
    "encounterUuid": "encounter-uuid",
    "orderUuid": "order-uuid",
    "providers": [{"providerUuid": "provider-uuid", "function": "performer"}],
    "status": "COMPLETED",
    "drugUuid": "drug-concept-uuid",
    "dose": 500.0,
    "doseUnits": "mg",
    "route": "oral",
    "administeredDateTime": 1710580200
  }'
```
