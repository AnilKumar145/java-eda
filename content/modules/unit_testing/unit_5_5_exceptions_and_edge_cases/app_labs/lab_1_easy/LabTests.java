public class LabTests {

    public static void main(String[] args) {
        testONegativeUniversalDonorSuccess();
        testRhIncompatibilityThrowsCustomException();
        testAboMismatchThrowsCustomException();
        testMalformedAboInputsThrowIllegalArgumentException();
        System.out.println("All Unit 5.5 Lab 1 Tests Passed Successfully!");
    }

    public static void testONegativeUniversalDonorSuccess() {
        Solution.BloodProfile donor = new Solution.BloodProfile("O", false);
        Solution.BloodProfile recipient = new Solution.BloodProfile("AB", true);

        Solution.TransfusionApproval approval =
            Solution.TransfusionCompatibilityGuard.authorizeTransfusion(donor, recipient);

        assert approval.isApproved() : "O- must be compatible with AB+";
        assert "STANDARD_CROSSMATCH".equals(approval.riskRating()) : "Expected STANDARD_CROSSMATCH";
        System.out.println("Lab 1 Test 1 Passed: Universal donor O- verified.");
    }

    public static void testRhIncompatibilityThrowsCustomException() {
        // A+ donating to A- (Rh violation)
        Solution.BloodProfile donor = new Solution.BloodProfile("A", true);
        Solution.BloodProfile recipient = new Solution.BloodProfile("A", false);

        boolean caught = false;
        try {
            Solution.TransfusionCompatibilityGuard.authorizeTransfusion(donor, recipient);
        } catch (Solution.TransfusionIncompatibilityException ex) {
            caught = true;
            assert ex.getDonor().rhPositive() : "Donor should be Rh+";
            assert !ex.getRecipient().rhPositive() : "Recipient should be Rh-";
            assert ex.getDetail().contains("Fatal Rh mismatch") : "Detail must state Rh mismatch: " + ex.getDetail();
        }
        assert caught : "A+ to A- must throw TransfusionIncompatibilityException";
        System.out.println("Lab 1 Test 2 Passed: Rh incompatibility exception verified.");
    }

    public static void testAboMismatchThrowsCustomException() {
        // B- donating to A- (ABO violation)
        Solution.BloodProfile donor = new Solution.BloodProfile("B", false);
        Solution.BloodProfile recipient = new Solution.BloodProfile("A", false);

        boolean caught = false;
        try {
            Solution.TransfusionCompatibilityGuard.authorizeTransfusion(donor, recipient);
        } catch (Solution.TransfusionIncompatibilityException ex) {
            caught = true;
            assert ex.getDetail().contains("ABO mismatch") : "Detail must state ABO mismatch: " + ex.getDetail();
        }
        assert caught : "B- to A- must throw TransfusionIncompatibilityException";
        System.out.println("Lab 1 Test 3 Passed: ABO mismatch exception verified.");
    }

    public static void testMalformedAboInputsThrowIllegalArgumentException() {
        String[] malformed = new String[] {"", "   ", "X", "C", "Rh+"};

        for (String badAbo : malformed) {
            boolean caught = false;
            try {
                Solution.BloodProfile donor = new Solution.BloodProfile(badAbo, false);
                Solution.BloodProfile recipient = new Solution.BloodProfile("O", false);
                Solution.TransfusionCompatibilityGuard.authorizeTransfusion(donor, recipient);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assert caught : "Should throw IllegalArgumentException for ABO '" + badAbo + "'";
        }
        System.out.println("Lab 1 Test 4 Passed: 5 malformed ABO inputs rejected.");
    }
}
