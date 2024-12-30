package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
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

        userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        // Get all web series
        List<WebSeries> allWebSeries = webSeriesRepository.findAll();

        // Filter based on the subscription type
        return (int) allWebSeries.stream().filter(webSeries ->
            (subscriptionType == SubscriptionType.ELITE) ||
                (subscriptionType == SubscriptionType.PRO && webSeries.getSubscriptionType() != SubscriptionType.ELITE) ||
                (subscriptionType == SubscriptionType.BASIC && webSeries.getSubscriptionType() == SubscriptionType.BASIC)
        ).count();
    }

    public double calculateSubscriptionCost(User user) {
        Subscription subscription = user.getSubscription();
        int noOfScreens = subscription.getNoOfScreensSubscribed();

        switch (subscription.getSubscriptionType()) {
            case BASIC:
                return 500 + (200 * noOfScreens); // BASIC plan cost
            case PRO:
                return 800 + (250 * noOfScreens); // PRO plan cost
            case ELITE:
                return 1000 + (350 * noOfScreens); // ELITE plan cost
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }
    }

    public boolean canWatchMatch(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Subscription subscription = user.getSubscription();
        return subscription.getSubscriptionType() == SubscriptionType.ELITE; // ELITE plan can watch matches
    }

}
