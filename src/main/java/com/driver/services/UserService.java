package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        User user1 = userRepository.save(user);
        return userRepository.save(user1).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){
        User user = userRepository.findById(userId).get();
        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int count = 0;
        for (WebSeries webSeries : webSeriesList) {
            if (webSeries.getAgeLimit() <= user.getAge() && isSubscriptionTypeAllowed(subscriptionType, webSeries)) {
                count++;
            }
        }
        return count;
    }

    private boolean isSubscriptionTypeAllowed(SubscriptionType subscriptionType, WebSeries webSeries) {
        switch (subscriptionType) {
            case BASIC:
                return webSeries.getSubscriptionType() == SubscriptionType.BASIC;
            case PRO:
                return webSeries.getSubscriptionType() == SubscriptionType.BASIC || webSeries.getSubscriptionType() == SubscriptionType.PRO;
            case ELITE:
                return true;
            default:
                return false;
        }
    }

}
