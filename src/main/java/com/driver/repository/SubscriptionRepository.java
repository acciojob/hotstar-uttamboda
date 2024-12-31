package com.driver.repository;

import com.driver.model.Subscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {
   Optional<Subscription> findByUserId(Integer userId);
}
