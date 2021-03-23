package com.test.intelliartstestapp.repository;

import com.test.intelliartstestapp.model.TotalAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalAmountRepository extends JpaRepository<TotalAmount, Long> {
}
