package com.example.coordinatestask.service.impl;

import com.example.coordinatestask.model.Coordinates;
import com.example.coordinatestask.service.CoordinateService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoordinateServiceImpl implements CoordinateService {
    private static final String FILEPATH= "nmea.log";

    /**
     * Реализиция метода чтения координат из файла логов. В зависимости от типа координат используютя разные методы
     * преобразовния координат.
     * @return Возвращает список координат (Coordinates)
     * @throws IOException
     */
    @Override
    public List<Coordinates> getCoordinatesFromLog() throws IOException {
        List<Coordinates> coordinates = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILEPATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("$GPGGA")) {
                    Coordinates coordinate = parseGPGGA(line);
                    if (coordinate != null) {
                        coordinates.add(coordinate);
                    }
                } else if (line.startsWith("$GNVTG")) {
                    updateGNVTG(line, coordinates);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    /**
     * Реализиция метода вычсления дистанции между двумя ккординаатами
     * @param c1 Начальная координата
     * @param c2 Конечная координата
     * @return Возвращает результат вычесления координат
     */
    @Override
    public Double calculateDistance(Coordinates c1, Coordinates c2) {
        Double distance = 0.0;
        if (c1.getSpeed() > 0) {
            distance += c1.getDistance();
        }
        if (c2.getSpeed() > 0) {
            distance += c1.distanceTo(c2);
        }
        return distance;
    }

    /**
     * Принимает на вход переменную типа String GPGGA и разбивает её на атрибуты класса Coordinates
     * @param line String тпа GPGGA
     * @return Преобразованный GPGGA
     */
    private Coordinates parseGPGGA(String line) {
        String[] fields = line.split(",");
        if (fields.length >= 14 && fields[0].equals("$GPGGA")) {
            Double lat = parseLatitude(fields[2], fields[3]);
            Double lon = parseLongitude(fields[4], fields[5]);
            Double alt = Double.parseDouble(fields[9]);
            Double speed = parseSpeed(fields[7]); // assuming speed is in knots
            Double bearing = parseBearing(fields[8]); // assuming bearing is in degrees
            Double distance = parseDistance(fields[10]); // assuming distance is in meters
            Double timestamp = Double.parseDouble(fields[1]);
            return new Coordinates(lat, lon, alt, speed, bearing, distance, timestamp);
        }
        return null;
    }
    /**
     Метод для парсинга широты из строки координат
     @param lat строка, содержащая координату широты
     @param latDir строка, содержащая направление широты (N - север, S - юг)
     @return координату широты в десятичном формате
     */
    private Double parseLatitude(String lat, String latDir) {
        Double degree = Double.parseDouble(lat.substring(0, 2));
        Double minute = Double.parseDouble(lat.substring(2));
        Double result = degree + minute / 60;
        if (latDir.equals("S")) {
            result *= -1;
        }
        return result;
    }
    /**
     Метод для парсинга долготы из строки координат
     @param lon строка, содержащая координату долготы
     @param lonDir строка, содержащая направление долготы (E - восток, W - запад)
     @return координату долготы в десятичном формате
     */
    private Double parseLongitude(String lon, String lonDir) {
        Double degree = Double.parseDouble(lon.substring(0, 3));
        Double minute = Double.parseDouble(lon.substring(3));
        Double result = degree + minute / 60;
        if (lonDir.equals("W")) {
            result *= -1;
        }
        return result;
    }
    /**
     * Парсит значение скорости из поля скорости входной строки GPGGA в м/с.
     * @param speedField значение поля скорости входной строки GPGGA
     * @return скорость в м/с или null, если поле скорости пустое или не может быть преобразовано в число
     */
    private Double parseSpeed(String speedField) {
        if (!speedField.isEmpty()) {
            try {
                double speedInKnots = Double.parseDouble(speedField);
                double speedInMetersPerSecond = speedInKnots * 0.514444; // 1 knot = 0.514444 m/s
                return speedInMetersPerSecond;
            } catch (NumberFormatException e) {
                // ignore and return null
            }
        }
        return null;
    }

    /**
     * Парсит значение направления из поля направления входной строки GPGGA в градусах.
     * @param bearingField значение поля направления входной строки GPGGA
     * @return направление в градусах или null, если поле направления пустое или не может быть преобразовано в число
     */
    private Double parseBearing(String bearingField) {
        if (!bearingField.isEmpty()) {
            try {
                return Double.parseDouble(bearingField);
            } catch (NumberFormatException e) {
                // ignore and return null
            }
        }
        return null;
    }

    /**
     * Парсит значение расстояния из поля геоидальной разности высот входной строки GPGGA в метрах.
     * @param distanceField значение поля геоидальной разности высот входной строки GPGGA
     * @return расстояние в метрах или null, если поле геоидальной разности высот пустое или не может быть преобразовано в число
     */
    private Double parseDistance(String distanceField) {
        if (!distanceField.isEmpty()) {
            try {
                return Double.parseDouble(distanceField);
            } catch (NumberFormatException e) {
                // ignore and return null
            }
        }
        return null;
    }
    /**
     Метод для обновления информации о направлении и скорости передвижения объекта
     на основе строки формата GNVTG.
     @param line строка формата GNVTG, содержащая информацию о направлении и скорости
     @param coordinates список координат объекта
     */
    private void updateGNVTG(String line, List<Coordinates> coordinates) {
        String[] fields = line.split(",");
        if (fields.length >= 8) {
            Double speed = Double.parseDouble(fields[7]);
            if (speed > 0) {
                Double bearing = Double.parseDouble(fields[1]);
                Coordinates lastCoord = coordinates.get(coordinates.size() - 1);
                lastCoord.setBearing(bearing);
                if (lastCoord.getSpeed() == 0) {
                    lastCoord.setSpeed(speed);
                } else {
                    Double elapsedTime = (lastCoord.getTimestamp() == 0) ? 0 :
                            (Double.parseDouble(fields[1]) - lastCoord.getTimestamp()) / 1000;
                    Double distance = lastCoord.getSpeed() * elapsedTime;
                    lastCoord.setDistance(lastCoord.getDistance() + distance);
                    lastCoord.setSpeed(speed);
                }
                lastCoord.setTimestamp(Double.parseDouble(fields[1]));
            }
        }
    }

}
