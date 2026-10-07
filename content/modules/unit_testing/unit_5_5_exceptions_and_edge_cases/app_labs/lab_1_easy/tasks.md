# Lab Tasks: Blood Bank Transfusion Compatibility Guard

## Task 1: Declare Models and Custom Exception
In `Solution.java`:
- `record BloodProfile(String abo, boolean rhPositive)`
- `record TransfusionApproval(boolean isApproved, String riskRating, String notes)`
- `class TransfusionIncompatibilityException extends RuntimeException`:
  - Contains `BloodProfile donor`, `BloodProfile recipient`, and `String detail`.

## Task 2: Implement Transfusion Compatibility Logic
Implement `TransfusionCompatibilityGuard.authorizeTransfusion(BloodProfile donor, BloodProfile recipient)`:
1. Input validation:
   - Validate `donor` and `recipient` non-null.
   - Validate `abo` is one of `{"O", "A", "B", "AB"}` (case-insensitive); throw `IllegalArgumentException` if invalid.
2. Invariant: Rh Factor Compatibility:
   - Rh+ donor can ONLY donate to Rh+ recipient.
   - If `donor.rhPositive()` and `!recipient.rhPositive()`, throw `TransfusionIncompatibilityException` ("Fatal Rh mismatch").
3. Invariant: ABO Group Compatibility:
   - "O" can donate to "O", "A", "B", "AB"
   - "A" can donate to "A", "AB"
   - "B" can donate to "B", "AB"
   - "AB" can donate to "AB"
   - If not compatible, throw `TransfusionIncompatibilityException` ("ABO mismatch").
4. If compatible:
   - Return `TransfusionApproval(true, "STANDARD_CROSSMATCH", "Compatible")`.

## Task 3: Author Verification Tests
In `LabTests.java`:
1. Universal donor O- to AB+ succeeds with `isApproved == true`.
2. Rh mismatch (A+ to A-) throws `TransfusionIncompatibilityException` with detailed metadata.
3. ABO mismatch (B- to A-) throws `TransfusionIncompatibilityException`.
4. Malformed ABO inputs (`"X"`, `""`, `"   "`, `"Rh+"`) throw `IllegalArgumentException`.
