# Lab Tasks: Emergency Triage Patient Scoring Test Suite

## Task 1: Declare the Data Models
In `Solution.java`:
- `record PatientVitals(int heartRate, int respiratoryRate, int spo2, int systolicBp)`
- `record TriageAssessment(int totalScore, int acuityTier, String acuityName, boolean resuscitationRequired)`

## Task 2: Implement Clinical Triage Logic
Implement `ClinicalTriageEngine.scorePatient(PatientVitals vitals)`:
1. Validate physiological boundaries (throw `IllegalArgumentException` if any vital out of range):
   - `heartRate`: 10 to 300
   - `respiratoryRate`: 4 to 80
   - `spo2`: 40 to 100
   - `systolicBp`: 30 to 300
2. Compute MEWS partial points:
   - Heart Rate: `< 40` -> 2 pts, `41-50` -> 1 pt, `51-100` -> 0 pts, `101-110` -> 1 pt, `111-129` -> 2 pts, `>= 130` -> 3 pts
   - Respiratory Rate: `< 9` -> 2 pts, `9-14` -> 0 pts, `15-20` -> 1 pt, `21-29` -> 2 pts, `>= 30` -> 3 pts
   - SpO2: `< 85` -> 3 pts, `85-89` -> 2 pts, `90-92` -> 1 pt, `>= 93` -> 0 pts
   - Systolic BP: `< 70` -> 3 pts, `71-80` -> 2 pts, `81-100` -> 1 pt, `101-199` -> 0 pts, `>= 200` -> 2 pts
3. Tier Assignment:
   - Score `>= 7` -> Tier 1 (`RESUSCITATION`, `resuscitationRequired = true`)
   - Score `4-6` -> Tier 2 (`EMERGENT`, `resuscitationRequired = false`)
   - Score `1-3` -> Tier 3 (`URGENT`, `resuscitationRequired = false`)
   - Score `0` -> Tier 4 (`NON_URGENT`, `resuscitationRequired = false`)

## Task 3: Author AAA Test Suite
In `LabTests.java`, write tests with AAA pattern:
1. Normal vitals evaluate to Tier 4 Non-Urgent (score 0).
2. Moderate vitals evaluate to Tier 2 Emergent (score 4-6).
3. Critical vitals evaluate to Tier 1 Resuscitation (score >= 7, resuscitationRequired = true).
4. Boundary physiological violations throw `IllegalArgumentException`.
