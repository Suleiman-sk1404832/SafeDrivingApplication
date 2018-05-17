package qa.edu.qu.cmps312.safedrivingapplication.models;

public class Trip {

    private int noOfTrips;
    private float totTimeInMin;
    private float totDangerTimeInMin;
    private float totDistanceTraveled;
    private float avgSpeed;

    public Trip() {
    }

    public Trip(float totTimeInMin, float totDangerTimeInMin, float totDistanceTraveled, float avgSpeed) {
        this.totTimeInMin = totTimeInMin;
        this.totDangerTimeInMin = totDangerTimeInMin;
        this.totDistanceTraveled = totDistanceTraveled;
        this.avgSpeed = avgSpeed;
    }

    public int getNoOfTrips() {
        return noOfTrips;
    }

    public void setNoOfTrips(int noOfTrips) {
        this.noOfTrips = noOfTrips;
    }

    public float getTotTimeInMin() {
        return totTimeInMin;
    }

    public void setTotTimeInMin(float totTimeInMin) {
        this.totTimeInMin = totTimeInMin;
    }

    public float getTotDangerTimeInMin() {
        return totDangerTimeInMin;
    }

    public void setTotDangerTimeInMin(float totDangerTimeInMin) {
        this.totDangerTimeInMin = totDangerTimeInMin;
    }

    public float getTotDistanceTraveled() {
        return totDistanceTraveled;
    }

    public void setTotDistanceTraveled(float totDistanceTraveled) {
        this.totDistanceTraveled = totDistanceTraveled;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    //Statistics Methods
    //Used when number of trips is more than 1

    public float getAverageTimeInMin(){
        return totTimeInMin/noOfTrips;
    }

    public float getAverageDangerousTimeInMin(){
        return totDangerTimeInMin/noOfTrips;
    }

    public float getAverageDistanceTraveled(){
        return totDistanceTraveled/noOfTrips;
    }

    public float getAverageSpeed(){
        return avgSpeed/noOfTrips;
    }



}
