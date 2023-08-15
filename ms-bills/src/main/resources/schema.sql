create table bill
(
    id_bill       varchar(255) not null,
    customer_bill varchar(255),
    product_bill  varchar(255),
    total_price   double,
    primary key (id_bill)
);