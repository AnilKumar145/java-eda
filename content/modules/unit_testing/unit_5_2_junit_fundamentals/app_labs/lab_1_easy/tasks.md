# Lab Tasks: Pediatric Dosage Calculator Test Harness

## Task 1: Declare the Dose Result Model
In `Solution.java`:
- `record DoseVolumeResult(double doseMg, double volumeMl, boolean cappedAtAdultMax)`

## Task 2: Implement Calculation Logic
Implement `PediatricDosageCalculator.calculateLiquidVolume(double weightKg, double mgPerKg, double concentrationMgPerMl, double maxAdultDoseMg)`:
1. Input validation:
   - `weightKg <= 0`: throw `IllegalArgumentException("Patient weight must be strictly positive")`
   - `mgPerKg <= 0`: throw `IllegalArgumentException("Dose rate must be strictly positive")`
   - `concentrationMgPerMl <= 0`: throw `IllegalArgumentException("Liquid concentration must be strictly positive")`
   - `maxAdultDoseMg <= 0`: throw `IllegalArgumentException("Maximum adult dose must be strictly positive")`
2. Arithmetic:
   - Raw dose = `weightKg * mgPerKg`
   - If raw dose > `maxAdultDoseMg`, final dose = `maxAdultDoseMg`, `capped = true`.
   - Else final dose = raw dose, `capped = false`.
   - Volume mL = `final dose / concentrationMgPerMl`.
   - Round both dose and volume to 2 decimal places.

## Task 3: Author Verification Tests
In `LabTests.java`:
1. Verify standard uncapped dose calculation.
2. Verify heavy patient exceeding adult cap is clamped to `maxAdultDoseMg` and `cappedAtAdultMax == true`.
3. Verify negative weight throws `IllegalArgumentException`.
4. Verify zero concentration throws `IllegalArgumentException`.
