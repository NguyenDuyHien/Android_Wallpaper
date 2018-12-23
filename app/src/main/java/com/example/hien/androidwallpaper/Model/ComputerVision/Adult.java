package com.example.hien.androidwallpaper.Model.ComputerVision;

/**
 * Created by Hien on 30/11/2018.
 */

public class Adult {
    private boolean isAdultContent, isRacyContent;
    private double adoultScore, racyScore;

    public Adult() {
    }

    public Adult(boolean isAdultContent, boolean isRacyContent, double adoultScore, double racyScore) {
        this.isAdultContent = isAdultContent;
        this.isRacyContent = isRacyContent;
        this.adoultScore = adoultScore;
        this.racyScore = racyScore;
    }

    public boolean isAdultContent() {
        return isAdultContent;
    }

    public void setAdultContent(boolean adultContent) {
        isAdultContent = adultContent;
    }

    public boolean isRacyContent() {
        return isRacyContent;
    }

    public void setRacyContent(boolean racyContent) {
        isRacyContent = racyContent;
    }

    public double getAdoultScore() {
        return adoultScore;
    }

    public void setAdoultScore(double adoultScore) {
        this.adoultScore = adoultScore;
    }

    public double getRacyScore() {
        return racyScore;
    }

    public void setRacyScore(double racyScore) {
        this.racyScore = racyScore;
    }
}
