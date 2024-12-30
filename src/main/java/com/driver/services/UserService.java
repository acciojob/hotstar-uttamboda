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

        User savedUser = userRepository.save(user);
        System.out.println("User saved with ID: " + savedUser.getId());
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        List<WebSeries> availableWebSeries;

        List<WebSeries> allWebSeries = webSeriesRepository.findAll();

        if (subscriptionType == SubscriptionType.ELITE) {
            availableWebSeries = allWebSeries;
        } else if (subscriptionType == SubscriptionType.PRO) {
            availableWebSeries = allWebSeries.stream()
                .filter(webSeries -> webSeries.getSubscriptionType() != SubscriptionType.ELITE)
                .collect(Collectors.toList());
        } else {
            availableWebSeries = allWebSeries.stream()
                .filter(webSeries -> webSeries.getSubscriptionType() == SubscriptionType.BASIC)
                .collect(Collectors.toList());
        }

        return availableWebSeries.size();
    }

    public double calculateSubscriptionCost(User user) {
        if (user == null || user.getSubscription() == null) {
            throw new IllegalArgumentException("User or subscription cannot be null");
        }

        Subscription subscription = user.getSubscription();
        int noOfScreens = subscription.getNoOfScreensSubscribed();

        System.out.println("Subscription Type: " + subscription.getSubscriptionType());
        System.out.println("Number of Screens: " + noOfScreens);

        switch (subscription.getSubscriptionType()) {
            case BASIC:
                return 500 + (200 * noOfScreens);  // Basic plan cost
            case PRO:
                return 800 + (250 * noOfScreens);  // Pro plan cost
            case ELITE:
                return 1000 + (350 * noOfScreens);  // Elite plan cost
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }
    }

    public boolean canWatchMatch(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Subscription subscription = user.getSubscription();

        if (subscription == null) {
            throw new RuntimeException("User does not have an active subscription");
        }

        return subscription.getSubscriptionType() == SubscriptionType.ELITE; // Only ELITE can watch matches
    }

}
