public class StarterCode {

    public record DoseVolumeResult(double doseMg, double volumeMl, boolean cappedAtAdultMax) {}

    public static class PediatricDosageCalculator {

        public static DoseVolumeResult calculateLiquidVolume(
            double weightKg,
            double mgPerKg,
            double concentrationMgPerMl,
            double maxAdultDoseMg
        ) {
            // TODO: Validate arguments are strictly positive (else throw IllegalArgumentException).
            // Calculate raw dose = weightKg * mgPerKg.
            // If raw dose > maxAdultDoseMg, cap at maxAdultDoseMg and set capped = true.
            // Calculate volume = dose / concentrationMgPerMl.
            // Round dose and volume to 2 decimal places and return DoseVolumeResult.
            throw new UnsupportedOperationException("TODO: Implement calculateLiquidVolume");
        }
    }
}
