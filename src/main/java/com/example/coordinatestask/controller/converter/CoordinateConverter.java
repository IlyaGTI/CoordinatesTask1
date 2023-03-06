package com.example.coordinatestask.controller.converter;

import com.example.coordinatestask.model.Coordinates;
import org.springframework.stereotype.Component;
/**
 Класс для конвертации строки координат в объект Coordinates.
 Объект Coordinates содержит информацию о
 широте, долготе, высоте, скорости, направлении движения, пройденном расстоянии и времени.
 Метод convertStringToCoordinates принимает строку координат в формате
 "latitude,longitude,altitude,speed,bearing,distance,timestamp"
 и возвращает объект Coordinates, заполненный этими значениями.
 */
@Component
public class CoordinateConverter {
    public Coordinates convertStringToCoordinates(String coordinatesStr) {
        String[] coordinatesArray = coordinatesStr.split(",");
        Double latitude = Double.parseDouble(coordinatesArray[0]);
        Double longitude = Double.parseDouble(coordinatesArray[1]);
        Double altitude = Double.parseDouble(coordinatesArray[2]);
        Double speed = Double.parseDouble(coordinatesArray[3]);
        Double bearing = Double.parseDouble(coordinatesArray[4]);
        Double distance = Double.parseDouble(coordinatesArray[5]);
        Double timestamp = Double.parseDouble(coordinatesArray[6]);
        return new Coordinates(latitude, longitude, altitude, speed, bearing, distance, timestamp);
    }
}