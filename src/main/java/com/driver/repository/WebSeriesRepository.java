package com.driver.repository;

import com.driver.model.WebSeries;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebSeriesRepository extends JpaRepository<WebSeries,Integer> {

    WebSeries findBySeriesName(String seriesName);

    List<WebSeries> findByProductionHouseId(Integer productionHouseId);
}
