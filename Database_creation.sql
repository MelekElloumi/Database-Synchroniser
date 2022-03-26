Create Table Product_Sales(
`id` integer primary key,
`Date` varchar(10) not null,
`Region` varchar(10) not null,
`Product` varchar(20) not null,
`Qty` integer not null,
`Cost` double not null,
`Amt` double not null,
`Tax` double not null,
`Total` double not null,
`Envoie` integer not null check (Envoie IN (0,1))
);


INSERT INTO product_sales (date, region, product, qty, cost,amt,tax,total,envoie) VALUES
('2020-02-01', 'East', 'Pens', 70, 12.95,945.35,66.7,1011.52,0),
('2020-02-01', 'West', 'Pens', 31, '12.95','427.35','29.91','457.26',0),
('2020-02-02', 'East', 'Paper', 12, '2.19','30.55','2.15','32.81',0),
('2020-02-02', 'East', 'Pens', 42, '2.19','87.60','6.13','93.73',0),
('2020-02-03', 'East', 'Paper', 21, '12.95','271.95','19.04','290.99',0),
('2020-02-03', 'West', 'Paper', 11, '12.95','129.50','9.07','138.57',0);