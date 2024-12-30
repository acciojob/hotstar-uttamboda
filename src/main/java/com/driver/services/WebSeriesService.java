package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    @Transactional
    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries existingWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (existingWebSeries != null) {
            throw new Exception("Series is already present");
        }

        // Retrieve the production house
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId())
            .orElseThrow(() -> new Exception("Production house not found"));

        // Create the new WebSeries entity
        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        System.out.println("Saving WebSeries: " + webSeries.getSeriesName());

        // Save the web series to the database
        WebSeries savedWebSeries = webSeriesRepository.save(webSeries);

        System.out.println("Saved WebSeries ID: " + savedWebSeries.getId());

        // Return the ID of the saved WebSeries
        return savedWebSeries.getId();
    }

}
