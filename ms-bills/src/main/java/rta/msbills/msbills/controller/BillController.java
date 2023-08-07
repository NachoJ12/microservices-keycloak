package rta.msbills.msbills.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rta.msbills.msbills.entity.Bill;
import rta.msbills.msbills.service.BillService;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService service;

    @GetMapping("/all")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Bill bill) {
        return ResponseEntity.ok().body(service.save(bill));
    }

    @GetMapping("/findAllByCustomerBill/{customerBill}")
    public ResponseEntity<List<Bill>> getAllByCustomerBill(@PathVariable String customerBill) {
        return ResponseEntity.ok().body(service.findAllByCustomerBill(customerBill));
    }
}
