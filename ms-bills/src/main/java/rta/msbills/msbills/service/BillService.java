package rta.msbills.msbills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rta.msbills.msbills.entity.Bill;
import rta.msbills.msbills.repository.BillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {
    private final BillRepository repository;

    public List<Bill> getAllBill() {
        return repository.findAll();
    }

    public Bill save(Bill bill) {
        return repository.save(bill);
    }

    public List<Bill> findAllByCustomerBill(String customerBill){
        return repository.findAllByCustomerBill(customerBill);
    }
}
