package com.example.coordinatestask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель координат.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;
    private Double bearing;
    private Double distance;
    private Double timestamp;

    public Coordinates(Double latitude, Double longitude, Double altitude, Double speed, Double bearing, Double distance, Double timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.distance = distance;
        this.timestamp = timestamp;
    }

    public Double distanceTo(Coordinates other) {
        Double lat1 = Math.toRadians(this.latitude);
        Double lon1 = Math.toRadians(this.longitude);
        Double lat2 = Math.toRadians(other.latitude);
        Double lon2 = Math.toRadians(other.longitude);
        Double earthRadius = 6371000.00;
        Double dLat = lat2 - lat1;
        Double dLon = lon2 - lon1;
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = earthRadius * c;
        return distance;
    }
}