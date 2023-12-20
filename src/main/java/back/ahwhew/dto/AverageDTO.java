package back.ahwhew.dto;

import java.time.LocalDate;

public class AverageDTO {

    private LocalDate date;
    private double averagePositive;
    private double averageNegative;
    private double averageNeutral;

    public AverageDTO(LocalDate date,double averagePositive, double averageNegative, double averageNeutral) {
        this.date = date;
        this.averagePositive = averagePositive;
        this.averageNegative = averageNegative;
        this.averageNeutral = averageNeutral;
    }

    public LocalDate getDate(){return date;}
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

