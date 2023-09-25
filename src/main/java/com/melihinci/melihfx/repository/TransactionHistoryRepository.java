package com.melihinci.melihfx.repository;
import com.melihinci.melihfx.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionHistoryRepository extends CrudRepository<Transaction, Long> {

}
