package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
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

    // -----------------------------------------------------------------------------------------
    // 1st API - done

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        // Save The subscription Object into the Db and return the total Amount that user has to pay

        Date date = new Date();

        Subscription subscription = new Subscription();

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(date);

        int totalAmount = 0;

        if(subscriptionEntryDto.getSubscriptionType() == SubscriptionType.ELITE)
            totalAmount = 1000 + 350 * subscription.getNoOfScreensSubscribed();
        else if(subscriptionEntryDto.getSubscriptionType() == SubscriptionType.PRO)
            totalAmount = 800 + 250 * subscription.getNoOfScreensSubscribed();
        else
            totalAmount = 500 + 200 * subscription.getNoOfScreensSubscribed();

        subscription.setTotalAmountPaid(totalAmount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);

        subscription.setUser(user);

        subscriptionRepository.save(subscription);
        userRepository.save(user);

        return user.getSubscription().getTotalAmountPaid();
    }

    // 2nd API - done
    public Integer upgradeSubscription(Integer userId)throws Exception{

        // If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        // In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        // update the subscription in the repository

        int difference = 0;

        User user = userRepository.findById(userId).get();

        if(user.getSubscription().getSubscriptionType() == SubscriptionType.BASIC)
        {
            difference = (800 + 250 * user.getSubscription().getNoOfScreensSubscribed() )- user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            user.getSubscription().setStartSubscriptionDate(new Date());
            user.getSubscription().setTotalAmountPaid(800 + 250 * user.getSubscription().getNoOfScreensSubscribed());
        }
        else if(user.getSubscription().getSubscriptionType() == SubscriptionType.PRO){

            difference = (1000 + 350 * user.getSubscription().getNoOfScreensSubscribed()) - user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            user.getSubscription().setStartSubscriptionDate(new Date());
            user.getSubscription().setTotalAmountPaid(1000 + 350 * user.getSubscription().getNoOfScreensSubscribed());
        }
        else
            throw new Exception("Already the best Subscription");

        userRepository.save(user);

        return difference;
    }

    // 3rd API - done
    public Integer calculateTotalRevenueOfHotstar(){

        // We need to find out total Revenue of hot-star : from all the subscriptions combined
        // Hint is to use findAll function from the SubscriptionDb

        int amount = 0;
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for(Subscription st : subscriptions){
            amount += st.getTotalAmountPaid();
        }
        return amount;
    }

}
