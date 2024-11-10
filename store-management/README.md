# Store Management System

## Overview

The **Store Management System (SMS)** is a comprehensive system designed to manage a store's operations, including product management, inventory tracking, order processing, and user management. This system enables **admins** to manage the store, while **employees** handle daily operations such as updating product stock, managing orders, and processing inventory.

The system offers a centralized way for store staff to track and manage products, orders, and other important data, all while ensuring proper security and user role separation.

## Features

### 1. **Product Management**
Users/Admin can fully manage products in the store, allowing them to:
- **Add Products**: Create new products by providing details like product name, description, price, and stock quantity.
- **Update Products**: Modify product details such as price, stock levels, or descriptions.
- **Delete Products**: Remove products from the system if they are no longer offered.
- **View Products**: Access a list of all products with their current details.

### 2. **Order Management**
While **customers** will not place orders, **employees** will handle order management tasks:
- **Create Orders**: Place new orders by selecting products from the inventory, with quantities and customer information.
- **View Orders**: View order details, including product items, quantities, customer info, and the total price.
- **Order Status**: Orders will pass through various statuses during processing (I've added a dummy implementation of this idea):
    - **Created**: Order placed but not yet processed.
    - **Processed**: Order confirmed and in preparation.
    - **Shipped**: Order dispatched to the customer.
    - **Delivered**: Order successfully delivered.
    - **Cancelled**: Order canceled due to stock issues or user request.

### 3. **Inventory Management**
- **Track Stock Levels**: Keep track of product stock levels in real time.
- **Low Stock Alerts**: Receive notifications when a product's stock falls below a defined threshold.
- **Stock Validation**: Ensure stock availability when placing orders, preventing over-ordering and stock discrepancies.

### 4. **Employee and Admin Management**
Not implemented (I should have added CRUD for users, roles  and permissions, then restrict access)
- **Employee Accounts**: Employees are given access to manage orders, products, and stock, depending on their permissions.
- **Admin Accounts**: Admins have complete control over the system, including managing employees, products, and order statuses.
- **Role-based Access**: Admins can control who can perform specific operations, ensuring proper security for different user types.

### 5. **Security**
I've created a dummy controller to see that this security measures works.
The system incorporates strong security measures to safeguard user data and ensure that only authorized individuals can perform certain tasks:
- **Authentication**: Secure login using credentials with hashed passwords.
- **Authorization**: Role-based access control (RBAC) ensures that only admins can manage employee accounts, while employees can only perform their designated tasks.
- **Secure API Endpoints**: All endpoints are protected, and sensitive actions require proper authorization.

### 6. **Payment Integration (Optional Feature that I've would implemented if this project wasn't only for an interview)**
To allow admins to track sales and payment statuses, future updates may integrate with payment gateways (e.g., Stripe, PayPal) for processing payments directly through the system.

## Why H2 Database and Not a Relational Database? Short Answer: it's only a interview project :))

### 1. **H2 Database**
**H2** is used during development and testing for the following reasons:
- **In-Memory Database**: It allows quick testing and development without needing a persistent database.
- **Lightweight**: As an embedded, in-memory database, it simplifies development and testing workflows.
- **Fast Setup**: Easily deployable for testing environments where persistence is not necessary.

### 2. **Future Database Considerations**
For production, the application will migrate to a **full relational database** such as **MySQL** or **PostgreSQL**, offering:
- **Data Persistence**: Ensures that store data is saved across system restarts.
- **Scalability**: A relational database will handle larger datasets and more complex queries as the store grows.

## Features Development and Ideas

### 1. **Advanced Order Status**
Expand order statuses to include more specific stages such as:
- **Packed**: When the order is packed and ready for shipment.
- **Out for Delivery**: When the order is en route to the customer.
- **Returned**: For orders that have been returned by the customer due to various reasons (e.g., incorrect items, damage).

### 2. **Employee Performance and Sales Analytics**
Admins can track employee performance in managing orders and inventory. This feature could include:
- **Sales Performance**: Track the number of orders processed by each employee.
- **Order Processing Speed**: Measure how fast employees can process orders.
- **Inventory Management**: Evaluate employee accuracy in maintaining stock levels.

### 3. **Inventory Forecasting**
Admins can receive insights and forecasts on future stock levels based on historical data and trends. This helps optimize inventory purchasing and reduce overstock or stockouts.

### 4. **Promotions and Discounts Management**
Admins can create promotions or discounts for certain products. For example:
- **Limited-time Discounts**: Discount on specific products for a limited time.
- **Bulk Purchase Discounts**: Discounts for bulk orders, applicable to employees or other stores.

## Security Features

### 1. **Authentication and Authorization**
- **JWT (JSON Web Tokens)**: Used for user authentication with role-based access.
- **Role-Based Access Control (RBAC)**: Ensures employees have access only to relevant parts of the system (e.g., managing products or orders), while admins can perform all actions.
- **Password Hashing**: Secure password storage using **BCrypt** or another secure hashing algorithm.

### 2. **Endpoint Protection**
- **Spring Security** or other frameworks protect sensitive routes (e.g., order processing, employee management) by ensuring that only authorized roles can access them.
- **CSRF Protection**: Prevents malicious attacks from unauthorized parties.

### 3. **Data Protection**
- **Encrypted Communication**: Use HTTPS and encryption protocols to ensure the security of user data during transmission.
- **Input Validation**: Prevent common vulnerabilities such as SQL injection or XSS by validating and sanitizing all user inputs.

