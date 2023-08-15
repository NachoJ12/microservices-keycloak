package rta.msbills.msbills.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('PROVIDERS')")
    public ResponseEntity<?> save(@RequestBody Bill bill) {
        return ResponseEntity.ok().body(service.save(bill));
    }

    @GetMapping("/findAllByCustomerBill/{customerBill}")
    public ResponseEntity<List<Bill>> getAllByCustomerBill(@PathVariable String customerBill) {
        return ResponseEntity.ok().body(service.findAllByCustomerBill(customerBill));
    }
}
