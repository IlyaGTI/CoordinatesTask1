package com.example.coordinatestask.service;

import com.example.coordinatestask.model.Coordinates;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

public interface CoordinateService {
    /**
     * Метода чтения координат их файла логов nmea.log
     * @return List объектов коорднат (Coordinates)
     * @throws IOException Выбрасывается в ошибках файла
     */
    List<Coordinates> getCoordinatesFromLog() throws IOException;

    /**
     * Метод высчета дистанции между двумя координатами
     * @param coord1 Начальная координата
     * @param coord2 Конечная координата
     * @return Возвращает результат подсчета в double
     */
    Double calculateDistance(Coordinates coord1, Coordinates coord2);
}
