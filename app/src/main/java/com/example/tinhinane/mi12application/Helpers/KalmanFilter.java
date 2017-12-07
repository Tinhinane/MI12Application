package com.example.tinhinane.mi12application.Helpers;

/**
 * Created by tinhinane on 07/12/17.
 */

public class KalmanFilter {

    private double processNoise;//Process noise
    private double measurementNoise;//Measurement noise
    private double estimatedRSSI;//Output filtered RSSI
    private double errorCovarianceRSSI;//Calculated covariance
    private boolean isInitialized = false;//Initialization flag

    public KalmanFilter(){
        this.processNoise= 0.125;
        this.measurementNoise= 0.8;
    }

    public KalmanFilter(double processNoise, double measurementNoise) {
        this.processNoise = processNoise;
        this.measurementNoise = measurementNoise;
    }

    public double applyFilter(double rssi) {

        double priorRSSI;
        double kalmanGain;
        double priorErrorCovarianceRSSI;
        if (!isInitialized) {
            priorRSSI = rssi;
            priorErrorCovarianceRSSI = 1;
            isInitialized = true;
        } else {
            priorRSSI = estimatedRSSI;
            priorErrorCovarianceRSSI = errorCovarianceRSSI + processNoise;
        }

            kalmanGain = priorErrorCovarianceRSSI / (priorErrorCovarianceRSSI + measurementNoise);
            estimatedRSSI = priorRSSI + (kalmanGain * (rssi - priorRSSI));
            errorCovarianceRSSI = (1 - kalmanGain) * priorErrorCovarianceRSSI;

            return estimatedRSSI;
    }

}
