# VeryGoodBank Trade Enrichment Service

## Table of Contents
- [Overview](#overview)
- [Running the Service](#running-the-service)
- [Using the API](#using-the-api)
- [Limitations](#limitations)
- [Design Discussion](#design-discussion)
- [Ideas for Improvement](#ideas-for-improvement)

## Overview
The VeryGoodBank Trade Enrichment Service is designed to process trade data, enrich it with additional information, and return the enriched data. The service reads trade data from a CSV file, segments it into manageable batches, and enriches each trade with additional information such as product name.

## Running the Service

### Prerequisites
- Java 8 or higher
- Maven

### Steps
1. Clone the repository:
    ```bash
    git clone https://github.com/ArtemBykovWork/trade-enrichment-task.git
    cd trade-enrichment-task
    ```

2. Build the project using Maven:
    ```bash
    mvn clean install
    ```

3. Run the service:
    ```bash
    mvn spring-boot:run
    ```

## Using the API

### Enrich Trades Endpoint
- **URL**: `/api/v1/enrich`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
    - `file`: The CSV file containing trade data.

### Sample Request
```bash
curl -F "file=@trade.csv" http://localhost:8080/api/v1/enrich
```
### Sample Response
```bash
date,product_name,currency,price
20160101,REPO Domestic,EUR,30.34
20160101,Corporate Bonds Domestic,EUR,20.1
20160101,Treasury Bills Domestic,EUR,10.0
20160101,Missing Product Name,EUR,35.34
```

### Limitations
The CSV parsing assumes a fixed schema with specific columns (date, product ID, currency, price).

### Design Discussion
Trade Segmentation and Enrichment
The service segments the CSV data into batches and processes each batch concurrently. This approach balances memory usage and processing time. The TradeSegmentationService and TradeEnrichmentService handle segmentation and enrichment, respectively.

### Error Handling
The service logs errors for invalid rows and skips them during processing. This ensures that the processing continues even if some rows are invalid.

### Ideas for Improvement
- Enhanced Date Validation: Implement a more robust date validation that checks for realistic dates.
- Database Integration: Integrate with a database to store processed trades for future analysis and retrieval.
- Detailed Error Reporting: Provide more detailed error messages and reporting mechanisms for better debugging.
- Integration with Caching Systems (e.g., Redis or Memcached) to improve performance by reducing the need to repeatedly fetch the same data from the database or external APIs.
- Integration with Message Queues (e.g., RabbitMQ, Kafka) to improve scalability and reliability by decoupling the components and allowing asynchronous processing of trades.