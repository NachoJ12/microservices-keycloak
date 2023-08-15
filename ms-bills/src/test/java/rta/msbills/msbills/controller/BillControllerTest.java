package rta.msbills.msbills.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import rta.msbills.msbills.entity.Bill;
import rta.msbills.msbills.repository.BillRepository;
import rta.msbills.msbills.service.BillService;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillControllerTest {

    private String customerBill = "739add32-31a3-40e1-b129-f8ad721984e0";
    private String productBill = "courses/Java";
    private Double totalPrice = 2000.0;

    @LocalServerPort
    private int port;

    @Autowired
    private BillService billService;
    @Autowired
    private BillRepository billRepository;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    public void getAllBillsEmptyListTest() {
        given()
                .when()
                .get("/bills/all")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(0));
    }

    @Test
    @Order(2)
    public void getAllBillsNotEmptyListTest() {
        Bill bill = new Bill();
        bill.setCustomerBill(customerBill);
        bill.setProductBill(productBill);
        bill.setTotalPrice(totalPrice);

        billService.save(bill);

        given()
                .when()
                .get("/bills/all")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    public void saveBillTest() {
        Bill bill = new Bill();
        bill.setCustomerBill(customerBill);
        bill.setProductBill(productBill);
        bill.setTotalPrice(totalPrice);

        // POST request
        Response response = given()
                .contentType("application/json")
                .body(bill)
                .when()
                .post("/bills/save");

        // Check the response status code and body
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("idBill", notNullValue())
                .body("customerBill", equalTo(bill.getCustomerBill()))
                .body("productBill", equalTo(bill.getProductBill()))
                .body("totalPrice", equalTo(bill.getTotalPrice().floatValue()));
    }

    @Test
    @Order(4)
    public void getAllByCustomerBillTest() {
        Bill bill = new Bill();
        bill.setCustomerBill(customerBill);
        bill.setProductBill(productBill);
        bill.setTotalPrice(totalPrice);
        billService.save(bill);

        // GET request
        List<Bill> bills = Arrays.asList(
                given()
                        .when()
                        .get("/bills/findAllByCustomerBill/{customerBill}", customerBill)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Bill[].class)
        );

        //Check that the response is not empty and contains the previously saved invoice
        assertFalse(bills.isEmpty());
        assertEquals(1, bills.size());
        assertEquals(customerBill, bills.get(0).getCustomerBill());
    }

    @AfterEach
    public void clearDatabase() {
        billRepository.deleteAll(); // Clear all records from the bill table
    }

}