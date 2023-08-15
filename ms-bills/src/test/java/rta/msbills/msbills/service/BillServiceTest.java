package rta.msbills.msbills.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rta.msbills.msbills.entity.Bill;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BillServiceTest {
    @Autowired
    private BillService billService;

    @Test
    public void testSaveBill() {
        // Create a Bill object
        Bill bill = new Bill();
        bill.setCustomerBill("739add32-31a3-40e1-b129-f8af261284e3");
        bill.setProductBill("courses/Cobol");
        bill.setTotalPrice(1500.0);

        // Save the Bill
        Bill savedBill = billService.save(bill);

        // Assert that the saved Bill has a non-null ID
        assertNotNull(savedBill.getIdBill());
    }

    @Test
    public void testFindAllByCustomerBill() {
        String idBill1 = "739add32-31a3-40e1-b129-f8ad721984e0";
        String idBill2 = "739add32-31a3-40e1-b129-f8ad721984e1";

        // Create some test data
        Bill bill1 = new Bill();
        bill1.setCustomerBill(idBill1);
        bill1.setProductBill("courses/Cobol");
        bill1.setTotalPrice(1500.0);

        Bill bill2 = new Bill();
        bill2.setCustomerBill(idBill1);
        bill2.setProductBill("courses/Java");
        bill2.setTotalPrice(1300.0);

        Bill bill3 = new Bill();
        bill3.setCustomerBill(idBill2);
        bill3.setProductBill("courses/Java");
        bill3.setTotalPrice(1300.0);

        billService.save(bill1);
        billService.save(bill2);
        billService.save(bill3);

        // Find Bills by customerBill
        List<Bill> bills = billService.findAllByCustomerBill(idBill1);

        // Assert that the list is not empty and contains the correct number of Bills
        assertFalse(bills.isEmpty());
        assertEquals(2, bills.size());
    }

}