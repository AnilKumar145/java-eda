public class Solution {

    public record DoseVolumeResult(double doseMg, double volumeMl, boolean cappedAtAdultMax) {}

    public static class PediatricDosageCalculator {

        public static DoseVolumeResult calculateLiquidVolume(
            double weightKg,
            double mgPerKg,
            double concentrationMgPerMl,
            double maxAdultDoseMg
        ) {
            if (weightKg <= 0) {
                throw new IllegalArgumentException("Patient weight must be strictly positive: " + weightKg);
            }
            if (mgPerKg <= 0) {
                throw new IllegalArgumentException("Dose rate must be strictly positive: " + mgPerKg);
            }
            if (concentrationMgPerMl <= 0) {
                throw new IllegalArgumentException("Liquid concentration must be strictly positive: " + concentrationMgPerMl);
            }
            if (maxAdultDoseMg <= 0) {
                throw new IllegalArgumentException("Maximum adult dose must be strictly positive: " + maxAdultDoseMg);
            }

            double rawDose = weightKg * mgPerKg;
            boolean capped = false;
            double finalDose = rawDose;

            if (rawDose > maxAdultDoseMg) {
                finalDose = maxAdultDoseMg;
                capped = true;
            }

            double rawVolume = finalDose / concentrationMgPerMl;

            double roundedDose = Math.round(finalDose * 100.0) / 100.0;
            double roundedVolume = Math.round(rawVolume * 100.0) / 100.0;

            return new DoseVolumeResult(roundedDose, roundedVolume, capped);
        }
    }
}
