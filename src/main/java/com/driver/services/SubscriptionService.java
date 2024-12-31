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

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Optional<User> optionalUser = userRepository.findById(subscriptionEntryDto.getUserId());
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("User not found with ID: " + subscriptionEntryDto.getUserId());
        }

        User user = optionalUser.get();

        // Calculate the total price based on subscription type and number of screens
        int pricePerScreen = getSubscriptionPrice(subscriptionEntryDto.getSubscriptionType());
        int totalAmount = pricePerScreen * subscriptionEntryDto.getNoOfScreensRequired();

        // Create a new Subscription object
        Subscription newSubscription = new Subscription();
        newSubscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        newSubscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        newSubscription.setStartSubscriptionDate(new Date()); // Set the start date to the current date
        newSubscription.setTotalAmountPaid(totalAmount);
        newSubscription.setUser(user);

        // Save the subscription to the database
        subscriptionRepository.save(newSubscription);

        // Return the total amount the user has to pay
        return totalAmount;


    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = optionalUser.get();

        List<Subscription> userSubscriptions = subscriptionRepository.findAll();
        if (userSubscriptions.isEmpty()) {
            throw new IllegalArgumentException("User does not have an active subscription.");
        }

        Subscription currentSubscription = userSubscriptions.get(0);
        SubscriptionType currentType = currentSubscription.getSubscriptionType();

        if (currentType == SubscriptionType.ELITE) {
            throw new Exception("Already the best subscription.");
        }

        SubscriptionType newType = getNextSubscriptionType(currentType);

        int priceDifference = getSubscriptionPrice(newType) - getSubscriptionPrice(currentType);

        currentSubscription.setSubscriptionType(newType);
        subscriptionRepository.save(currentSubscription);

        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        int totalRevenue = 0;
        for (Subscription subscription : allSubscriptions) {
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }


    private SubscriptionType getNextSubscriptionType(SubscriptionType currentType) {
        switch (currentType) {
            case BASIC:
                return SubscriptionType.PRO;
            case PRO:
                return SubscriptionType.ELITE;
            default:
                throw new IllegalStateException("No next subscription available for " + currentType);
        }
    }

    // Helper method to get the price of a subscription type
    private int getSubscriptionPrice(SubscriptionType subscriptionType) {
        switch (subscriptionType) {
            case BASIC:
                return 500 + 200;
            case PRO:
                return 800 + 250;
            case ELITE:
                return 1000 + 350;
            default:
                throw new IllegalArgumentException("Unknown subscription type: " + subscriptionType);
        }
    }
}
