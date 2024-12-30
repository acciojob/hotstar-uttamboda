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
        switch (subscriptionType) {
            case BASIC:
                baseAmount = 100;
                break;
            case PRO:
                baseAmount = 200;
                break;
            case ELITE:
                baseAmount = 300;
                break;
        }
        return baseAmount * noOfScreensRequired;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        Subscription subscription = user.getSubscription();

        if (subscription == null) {
            throw new RuntimeException("User does not have an active subscription");
        }

        SubscriptionType currentType = subscription.getSubscriptionType();
        if (currentType == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        int priceDifference = 0;
        switch (currentType) {
            case BASIC:
                subscription.setSubscriptionType(SubscriptionType.PRO);
                priceDifference = 100;
                break;
            case PRO:
                subscription.setSubscriptionType(SubscriptionType.ELITE);
                priceDifference = 100;
                break;
        }

        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + priceDifference);

        subscriptionRepository.save(subscription);

        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar(){



        java.util.List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        int totalRevenue = 0;
        for (Subscription subscription : allSubscriptions) {
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
