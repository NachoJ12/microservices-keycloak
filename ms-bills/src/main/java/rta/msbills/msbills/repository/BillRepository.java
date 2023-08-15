package rta.msbills.msbills.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rta.msbills.msbills.entity.Bill;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, String> {
    List<Bill> findAllByCustomerBill(String customerId);
}
