package io.Bilzerian.coronavirustracker.models;

public class LocationStats
{
    private String state;
    private String country;
    private int latestTotal;
    private int activeCases;
    private int diff;
    private int deaths;
    private int deathsDiff;
    private double mortalityRate;
    private int recovered;
    private int recoveredDiff;
    private double recoveryRate;

    public int getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(int activeCases) {
        this.activeCases = activeCases;
    }

    public LocationStats(String state, int latestTotal, int diff, int activeCases, int deaths, int deathsDiff, double mortalityRate, int recovered, int recoveredDiff, double recoveryRate) {
        this.state=state;
        this.latestTotal = latestTotal;
        this.diff = diff;
        this.deaths = deaths;
        this.deathsDiff = deathsDiff;
        this.mortalityRate = mortalityRate;
        this.recovered = recovered;
        this.recoveredDiff = recoveredDiff;
        this.recoveryRate = recoveryRate;
        this.activeCases = activeCases;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getRecoveredDiff() {
        return recoveredDiff;
    }

    public void setRecoveredDiff(int recoveredDiff) {
        this.recoveredDiff = recoveredDiff;
    }

    public double getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public double getMortalityRate() {
        return mortalityRate;
    }

    public void setMortalityRate(double mortalityRate) {
        this.mortalityRate = mortalityRate;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getDeathsDiff() {
        return deathsDiff;
    }

    public void setDeathsDiff(int deathsDiff) {
        this.deathsDiff = deathsDiff;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return "LocationStats{" +
                "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", latestTotal=" + latestTotal +
                '}';
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestTotal() {
        return latestTotal;
    }

    public void setLatestTotal(int latestTotal) {
        this.latestTotal = latestTotal;
    }
}
