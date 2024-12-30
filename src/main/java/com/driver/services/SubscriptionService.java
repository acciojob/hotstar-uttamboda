package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        int userId = subscriptionEntryDto.getUserId();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreensRequired = subscriptionEntryDto.getNoOfScreensRequired();

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (user.getSubscription() != null) {
            throw new RuntimeException("User already has an active subscription");
        }

        int finalAmount = calculateFinalAmount(subscriptionType, noOfScreensRequired);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setNoOfScreensSubscribed(noOfScreensRequired);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(finalAmount);
        subscription.setUser(user);

        subscriptionRepository.save(subscription);

        return finalAmount;
    }
    private int calculateFinalAmount(SubscriptionType subscriptionType, int noOfScreensRequired) {
        int baseAmount = 0;
        int screenCost = 0;

        switch (subscriptionType) {
            case BASIC:
                baseAmount = 500;
                screenCost = 200;
                break;
            case PRO:
                baseAmount = 800;
                screenCost = 250;
                break;
            case ELITE:
                baseAmount = 1000;
                screenCost = 350;
                break;
        }

        return baseAmount + (screenCost * noOfScreensRequired);
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) { // Replace isEmpty() with !isPresent()
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        Subscription subscription = user.getSubscription();

        // Check if the user has an active subscription
        if (subscription == null) {
            throw new RuntimeException("User does not have an active subscription");
        }

        // Check if the user is already on the best subscription (ELITE)
        SubscriptionType currentType = subscription.getSubscriptionType();
        if (currentType == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        // Calculate the price difference for the upgrade
        int priceDifference = calculatePriceDifference(currentType);

        // Upgrade the subscription
        if (currentType == SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
        } else if (currentType == SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }

        // Update the total amount paid after the upgrade
        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + priceDifference);

        // Save the updated subscription
        subscriptionRepository.save(subscription);

        return priceDifference;
    }

    // Method to calculate the price difference between current and upgraded subscription
    private int calculatePriceDifference(SubscriptionType currentType) {
        int priceDifference = 0;

        // Determine the price difference for the upgrade
        switch (currentType) {
            case BASIC:
                priceDifference = 300; // Basic to Pro difference
                break;
            case PRO:
                priceDifference = 200; // Pro to Elite difference
                break;
        }

        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar() {

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        return allSubscriptions.stream()
            .mapToInt(Subscription::getTotalAmountPaid)
            .sum();
    }
}
