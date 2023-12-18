package back.ahwhew.dto;

public class AverageDTO {

    private double averagePositive;
    private double averageNegative;
    private double averageNeutral;

    public AverageDTO(double averagePositive, double averageNegative, double averageNeutral) {
        this.averagePositive = averagePositive;
        this.averageNegative = averageNegative;
        this.averageNeutral = averageNeutral;
    }

    public double getAveragePositive() {
        return averagePositive;
    }

    public double getAverageNegative() {
        return averageNegative;
    }

    public double getAverageNeutral() {
        return averageNeutral;
    }
}

