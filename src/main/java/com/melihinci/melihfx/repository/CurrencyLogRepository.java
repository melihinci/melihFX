package com.melihinci.melihfx.repository;

import com.melihinci.melihfx.model.CurrencyLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyLogRepository extends CrudRepository<CurrencyLog, Long> {
}
