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

        // Calculate the total amount to be paid based on the subscription type and number of screens
        int totalAmount = calculateTotalAmount(subscriptionEntryDto.getSubscriptionType(), subscriptionEntryDto.getNoOfScreensRequired());

        // Create a new Subscription object
        Subscription newSubscription = new Subscription();
        newSubscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        newSubscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        newSubscription.setStartSubscriptionDate(new Date()); // Set the current date as the start date
        newSubscription.setTotalAmountPaid(totalAmount);
        newSubscription.setUser(user);

        // Save the subscription to the database
        subscriptionRepository.save(newSubscription);

        // Return the total amount the user has to pay
        return totalAmount;
    }
    private int calculateTotalAmount(SubscriptionType subscriptionType, int noOfScreensRequired) {
        int totalAmount = 0;

        // Calculate the cost based on the subscription type and number of screens
        switch (subscriptionType) {
            case BASIC:
                totalAmount = 500 + 200 * noOfScreensRequired;
                break;
            case PRO:
                totalAmount = 800 + 250 * noOfScreensRequired;
                break;
            case ELITE:
                totalAmount = 1000 + 350 * noOfScreensRequired;
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription type: " + subscriptionType);
        }

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional<Subscription> currentSubscriptionOptional = subscriptionRepository.findByUserId(userId);

        // Check if the subscription exists
        if (!currentSubscriptionOptional.isPresent()) {
            throw new IllegalArgumentException("User does not have an active subscription.");
        }

        // Retrieve the subscription from the Optional
        Subscription currentSubscription = currentSubscriptionOptional.get();
        SubscriptionType currentType = currentSubscription.getSubscriptionType();

        // Check if the user is already at the highest subscription level
        if (currentType == SubscriptionType.ELITE) {
            throw new Exception("Already the best subscription");
        }

        // Determine the next subscription level
        SubscriptionType newType = getNextSubscriptionType(currentType);

        // Calculate the price difference
        int currentPrice = calculateTotalAmount(currentType, currentSubscription.getNoOfScreensSubscribed());
        int newPrice = calculateTotalAmount(newType, currentSubscription.getNoOfScreensSubscribed());
        int priceDifference = newPrice - currentPrice;

        // Update the subscription to the new type
        currentSubscription.setSubscriptionType(newType);
        subscriptionRepository.save(currentSubscription);

        return priceDifference;
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

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        // Step 2: Calculate the total revenue by summing up the totalAmountPaid for each subscription
        int totalRevenue = 0;

        for (Subscription subscription : allSubscriptions) {
            totalRevenue += subscription.getTotalAmountPaid();
        }

        // Step 3: Return the total revenue
        return totalRevenue;
    }
}
