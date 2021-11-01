package com.ridvan.eventaggregator.rest.controllers;

import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.rest.exceptions.NotFoundException;
import com.ridvan.eventaggregator.rest.exceptions.ValidationException;
import com.ridvan.eventaggregator.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/statistics")
public class VehicleStatisticsController {
    private final StatisticService statisticService;

    @Autowired
    public VehicleStatisticsController(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Gets the vehicle statistic, if exists, for the given id.
     */
    @GetMapping(path = "/{id}", produces = "application/json")
    public VehicleStatistic get(@PathVariable("id") final String id) {
        final UUID idUUID;
        try {
            idUUID = UUID.fromString(id);
        } catch (final Exception e) {
            throw new ValidationException("Invalid");
        }

        return this.statisticService.findStatistic(idUUID)
                .orElseThrow(() -> new NotFoundException("Vehicle not found. Id: " + id));
    }
}
