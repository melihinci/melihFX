package com.melihinci.melihfx.repository;
import com.melihinci.melihfx.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends CrudRepository<Transaction, Long> {

    @Query(value = "SELECT u FROM Transaction u WHERE u.currencyExchangeCode = ?1 ORDER BY u.processedDate")
    List<Transaction> findAllByCurrencyExchangeCode( );

}
