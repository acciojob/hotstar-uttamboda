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

        if (webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) != null) {
            throw new Exception("Series is already present");
        }

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId())
            .orElseThrow(() -> new Exception("Production house not found"));

        WebSeries webSeries = convertDtoToEntity(webSeriesEntryDto);
        webSeries = webSeriesRepository.save(webSeries);

        List<WebSeries> webSeriesList = webSeriesRepository.findByProductionHouseId(productionHouse.getId());
        double averageRating = webSeriesList.stream().mapToDouble(WebSeries::getRating).average().orElse(0.0);
        productionHouse.setRatings(averageRating);
        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }
    private WebSeries convertDtoToEntity(WebSeriesEntryDto webSeriesEntryDto) {
        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get());
        return webSeries;
    }
}
