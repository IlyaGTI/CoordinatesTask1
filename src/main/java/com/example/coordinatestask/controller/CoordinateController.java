package com.example.coordinatestask.controller;

import com.example.coordinatestask.controller.converter.CoordinateConverter;
import com.example.coordinatestask.model.Coordinates;
import com.example.coordinatestask.service.CoordinateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class CoordinateController {

    private CoordinateService coordinateService;
    private CoordinateConverter converter;

    @Autowired
    public CoordinateController(CoordinateService coordinateService, CoordinateConverter converter) {
        this.coordinateService = coordinateService;
        this.converter = converter;
    }

    @GetMapping("/read")
    public String readCoordinatesFromFile(Model model) throws IOException {
        List<Coordinates> coordinates = coordinateService.getCoordinatesFromLog();
        model.addAttribute("coordinates", coordinates);
        return "index";
    }

    @PostMapping("/distance")
    @ResponseBody
    public ResponseEntity<Double> calculateDistance(@RequestBody Map<String, String> coordinates) {
        String c1Str = coordinates.get("c1");
        String c2Str = coordinates.get("c2");
        Coordinates c1 = converter.convertStringToCoordinates(c1Str);
        Coordinates c2 = converter.convertStringToCoordinates(c2Str);
        Double distance = coordinateService.calculateDistance(c1, c2);
        return ResponseEntity.ok(distance);
    }

}