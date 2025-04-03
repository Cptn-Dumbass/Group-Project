CREATE TABLE OrderStages (
    Stage VARCHAR(20) NOT NULL,
    PRIMARY KEY (Stage)
);

CREATE TABLE OrderSources (
    Source VARCHAR(20) NOT NULL,
    PRIMARY KEY (Source)
);

CREATE TABLE OrderSubSources (
    Subsource VARCHAR(20) NOT NULL,
    PRIMARY KEY (Subsource)
);

CREATE TABLE OrderPaymentMethods (
    PaymentMethod VARCHAR(20) NOT NULL, 
    PRIMARY KEY (PaymentMethod) 
);

CREATE TABLE Customers (
    CustomerID INT NOT NULL AUTO_INCREMENT,
    FirstName VARCHAR(35) NOT NULL,
    Surname VARCHAR(35),
    EmailAddress VARCHAR(60) NOT NULL,
    PhoneNumber VARCHAR(11) NOT NULL,
    Company VARCHAR(80),
    PRIMARY KEY (CustomerID)
);

CREATE TABLE Countries (
    CountryName VARCHAR(50) NOT NULL, 
    CountryTaxRate INT NOT NULL,
    PRIMARY KEY (CountryName)
);

CREATE TABLE Addresses (
    AddressID INT NOT NULL AUTO_INCREMENT,
    CountryName VARCHAR(50) NOT NULL,
    PostCode VARCHAR(7) NOT NULL,
    City VARCHAR(20) NOT NULL,
    County VARCHAR(20) NOT NULL,
    Line1 VARCHAR(60) NOT NULL,
    Line2 VARCHAR(60),
    PRIMARY KEY (AddressID),
    FOREIGN KEY (CountryName) REFERENCES Countries(CountryName)
);

CREATE TABLE Orders (
    ReferenceNum VARCHAR(20),
    CustomerID INT NOT NULL,
    Stage VARCHAR(10) NOT NULL,
    Source VARCHAR(20) NOT NULL,
    Subsource VARCHAR(20),
    AddressID INT NOT NULL,
    ExternalReference VARCHAR(50),
    ChannelReference VARCHAR(50) NOT NULL,
    ReceivedDate DATE NOT NULL,
    PaymentMethod VARCHAR(20) NOT NULL,
    PurchasePrice DECIMAL(19,4) NOT NULL,
    ProcessedDate DATE, 
    PRIMARY KEY (ReferenceNum),
    FOREIGN KEY (Stage) REFERENCES OrderStages(Stage),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID),
    FOREIGN KEY (Source) REFERENCES OrderSources(Source),
    FOREIGN KEY (Subsource) REFERENCES OrderSubSources(Subsource),
    FOREIGN KEY (PaymentMethod) REFERENCES OrderPaymentMethods(PaymentMethod)
);

CREATE TABLE Categories (
    CategoryID INT NOT NULL AUTO_INCREMENT,
    CategoryName VARCHAR(100) NOT NULL,
    PRIMARY KEY (CategoryID)
);

CREATE TABLE Products (
    ProductID INT NOT NULL AUTO_INCREMENT,
    CategoryID INT NOT NULL,
    Title VARCHAR(100) NOT NULL,
    UnitCost DECIMAL(19,4) NOT NULL,
    Weight INT NOT NULL,
    MinLevel INT NOT NULL,
    PRIMARY KEY (ProductID),
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);

CREATE TABLE ProductSKU (
    SKU VARCHAR(20) NOT NULL,
    ProductID INT NOT NULL,
    Amount INT NOT NULL,
    WarehouseLocation VARCHAR(10) NOT NULL,
    PRIMARY KEY (SKU, ProductID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE OrderItems (
    ReferenceNum VARCHAR(20) NOT NULL,
    SKU VARCHAR(20) NOT NULL,
    Quantity INT NOT NULL,
    PRIMARY KEY (ReferenceNum, SKU),
    FOREIGN KEY (ReferenceNum) REFERENCES Orders(ReferenceNum),
    FOREIGN KEY (SKU) REFERENCES ProductSKU(SKU)
);

CREATE TABLE Restocks (
    RestockID INT NOT NULL AUTO_INCREMENT,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    TotalCost DECIMAL(19,4) NOT NULL,
    PurchaseDate DATE NOT NULL,
    Supplier VARCHAR(100),
    PRIMARY KEY (RestockID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE PostageSizes (
    Size VARCHAR(20) NOT NULL,
    PRIMARY KEY (Size)
);

CREATE TABLE PostageClasses (
    Class VARCHAR(20) NOT NULL,
    PRIMARY KEY (Class)
);

CREATE TABLE PostalServices (
    Service VARCHAR(20) NOT NULL,
    PRIMARY KEY (Service)
);

CREATE TABLE Postage (
    ReferenceNum VARCHAR(20) NOT NULL,
    Weight INT NOT NULL,
    Size VARCHAR(20) NOT NULL,
    Class VARCHAR(20) NOT NULL,
    Service VARCHAR(20) NOT NULL,
    TrackingNumber VARCHAR(40),
    PRIMARY KEY (ReferenceNum),
    FOREIGN KEY (Size) REFERENCES PostageSizes(Size),
    FOREIGN KEY (Class) REFERENCES PostageClasses(Class),
    FOREIGN KEY (Service) REFERENCES PostalServices(Service)
);

CREATE TABLE AlertPriorities (
    AlertPriority VARCHAR(10) NOT NULL,
    PRIMARY KEY (AlertPriority)
);

CREATE TABLE Alerts (
    AlertID INT NOT NULL,
    AlertPriority VARCHAR(10) NOT NULL,
    tempDescription VARCHAR(100) NOT NULL,
    PRIMARY KEY (AlertID),
    FOREIGN KEY (AlertPriority) REFERENCES AlertPriorities(AlertPriority)
); 

CREATE TABLE NotificationPriorities (
    Priority VARCHAR(10) NOT NULL,
    PRIMARY KEY (Priority)
);

CREATE TABLE Notifications (
    NotificationID INT NOT NULL,
    AlertID INT NOT NULL,
    ReceivedDateTime DATETIME NOT NULL,
    tempProperties VARCHAR(100) NOT NULL,
    Priority VARCHAR(10) NOT NULL,
    PRIMARY KEY (NotificationID),
    FOREIGN KEY (AlertID) REFERENCES Alerts(AlertID),
    FOREIGN KEY (Priority) REFERENCES NotificationPriorities(Priority)
);

CREATE TABLE AccessLevels (
    AccessLevel VARCHAR(20) NOT NULL,
    PRIMARY KEY (AccessLevel)
);

CREATE TABLE Users (
    Username VARCHAR(50) NOT NULL,
    AccessLevel VARCHAR(20) NOT NULL,
    PRIMARY KEY (Username),
    FOREIGN KEY (AccessLevel) REFERENCES AccessLevels(AccessLevel)
);


INSERT INTO NotificationPriorities(Priority)
VALUES
('Regular'),
('High');

INSERT INTO PostageSizes(Size)
VALUES
('Parcel'),
('Large letter');

INSERT INTO PostageClasses(Class)
VALUES
('Special'), 
('2nd class'),
('1st class');

INSERT INTO PostalServices(Service)
VALUES
('DHL'), 
('FedEx'),
('Royal Mail');

INSERT INTO OrderStages(Stage)
VALUES
('Print'), 
('Process'), 
('Packed'), 
('Approved'),
('Sent');

INSERT INTO OrderSources(Source)
VALUES
('AMAZON'), 
('EBAY'), 
('DIRECT');

INSERT INTO OrderSubSources(Subsource)
VALUES
('AEL'), 
('candyrush2015'), 
('Default'),
('EBAY1');

INSERT INTO OrderPaymentMethods(PaymentMethod)
VALUES
('Default');

INSERT INTO Categories(CategoryName)
VALUES
('Cello Bags'),
('Cone-clear'),
('Envelopes - 5x5');

INSERT INTO Products(Title, UnitCost, CategoryID, Weight, MinLevel)
VALUES
('18x37cm (7x14.5") Clear Cone Bags',4.5699, 1, 10, 15),
('4.3x8.7" (115mm x 220mm ) Sticky Seal Cello',3.6200, 2, 5, 10),
('5x5" (130x130mm) Envelopes - KRAFT',2.6699, 3, 50, 5);

INSERT INTO Restocks(ProductID, Quantity, TotalCost, PurchaseDate, Supplier)
VALUES
(1,31,100.0,'2007-01-04','ConeCo'),
(2,5,15.1,'2007-01-03','Cello Inc.'),
(3,29,65.43,'2007-01-02','Envezon');

INSERT INTO ProductSKU(SKU, ProductID, Amount, WarehouseLocation)
VALUES
("cone bag sku x100",1, 100,"no sample"),
("cone bag sku x50",1, 50,"no sample"),
("AEL-180-CELLO-100",2, 100,"no sample"),
("AEL-180-CELLO-50",2, 50,"no sample"),
("AEL-180-CELLO-20",2, 20,"no sample"),
("AEL-180-CELLO-10",2, 10,"no sample"),
("envelope sku x20",3, 20,"no sample"),
("envelope sku x10",3, 10,"no sample"),
("envelope sku x5",3, 5,"no sample");

INSERT INTO Customers(FirstName, Surname, EmailAddress, PhoneNumber, Company)
VALUES
('John','Doe','jdoe@email.com','07731844541','Doe Co.'),
('Foo','Bar','fbar@email.com','07731949239','Bar Inc.');

INSERT INTO Customers(FirstName, Surname, EmailAddress, PhoneNumber)
VALUES
('Annie','Mous','amous@email.com','07733959239');

INSERT INTO Countries(CountryName, CountryTaxRate)
VALUES
('United Kingdom','120');

INSERT INTO Addresses(CountryName, PostCode, Line1, Line2, City, County)
VALUES
('United Kingdom','BT48AB','123 Foo St.','Flat 5', 'Cityville', 'CountyTown'),
('United Kingdom','BT19CD','456 Bar Boulvd.','Flat 3', 'Cityville', 'CountyTown');

INSERT INTO Addresses(CountryName, PostCode, Line1, City, County)
VALUES
('United Kingdom','BT87EF','789 Baz Rd.', 'Cityville', 'CountyTown');

INSERT INTO Orders(ReferenceNum, CustomerID, AddressID, Stage, ExternalReference, ChannelReference, ReceivedDate, PaymentMethod, PurchasePrice, Source, Subsource)
VALUES
('204-1222672-8606731',1,1,'Print','1921585709901','10-10881-75822','2008-11-11','Default',12.34,'AMAZON','AEL'),
('206-1353273-0137968',2,2,'Print','1917901810401','10-10951-34386','2008-12-03','Default',5.60,'EBAY','EBAY1'),
('026-2241241-8607516',2,2,'Print','1917901321401','10-10900-67401','2008-09-12','Default',78.90,'EBAY','candyrush2015'),
('026-2241241-8607513',3,3,'Packed', null, '10-10900-67402', '2024-06-04','Default', 54.50, 'AMAZON', 'AEL'),
('205-3487797-3617966',3,3,'Print','1342901810401','16-10872-15229','2008-02-09','Default',1.23,'DIRECT','Default');

INSERT INTO OrderItems(ReferenceNum, SKU, Quantity)
VALUES
('204-1222672-8606731',"envelope sku x5",3),
('204-1222672-8606731',"cone bag sku x100",1),
('206-1353273-0137968',"cone bag sku x50",2),
('026-2241241-8607516',"AEL-180-CELLO-100",3),
('026-2241241-8607516',"envelope sku x5",3),
('026-2241241-8607516',"cone bag sku x50",2),
('026-2241241-8607513',"envelope sku x5",4),
('026-2241241-8607513',"cone bag sku x100",10),
('205-3487797-3617966',"AEL-180-CELLO-50",1);

INSERT INTO AccessLevels(AccessLevel)
VALUES
('admin'),
('basic_trainee'),
('adv_trainee');

INSERT INTO Users(Username, AccessLevel)
VALUES
('AA','admin'),
('BT','basic_trainee'),
('AT','adv_trainee'),
('jkerr','admin');
