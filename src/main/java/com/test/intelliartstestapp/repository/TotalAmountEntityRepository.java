package com.test.intelliartstestapp.repository;

import com.test.intelliartstestapp.model.TotalAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalAmountEntityRepository extends JpaRepository<TotalAmountEntity, Long> {
}
