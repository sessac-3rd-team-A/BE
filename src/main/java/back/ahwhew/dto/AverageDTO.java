package back.ahwhew.dto;

import java.time.LocalDate;

public class AverageDTO {

    private LocalDate date;
    private double averagePositive;
    private double averageNegative;
    private double averageNeutral;
    private int count;

    public AverageDTO(LocalDate date,double averagePositive, double averageNegative, double averageNeutral) {
        this.date = date;
        this.averagePositive = averagePositive;
        this.averageNegative = averageNegative;
        this.averageNeutral = averageNeutral;
        this.count = count;
    }

    public LocalDate getDate(){return date;}
    public double getAveragePositive() {
        return averagePositive;
    }

    public double getAverageNegative() {return averageNegative;}

    public double getAverageNeutral() {
        return averageNeutral;
    }

    public void setAveragePositive(double averagePositive) {
        this.averagePositive = averagePositive;
    }

    public void setAverageNegative(double averageNegative) {
        this.averageNegative = averageNegative;
    }

    public void setAverageNeutral(double averageNeutral) {
        this.averageNeutral = averageNeutral;
    }

    public void incrementCount(){
        this.count++;
    }

    public int getCount() {
        return count;
    }
}

