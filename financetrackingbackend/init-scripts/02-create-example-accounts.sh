#!/bin/bash

awslocal dynamodb put-item \
    --table-name Accounts \
    --item '{
        "id": {"S": "acc-001"},
        "name": {"S": "Savings Account"},
        "bank": {"S": "HSBC"},
        "accountNumber": {"N": "87654321"},
        "sortCode": {"N": "654321"},
        "balance": {"N": "10000.00"},
        "stocks": {"L": []},
        "currentGains": {"N": "0.0"},
        "fees": {"L": []},
        "dateCreatedOn": {"S": "2024-01-17"},
        "interestRate": {"M": {
            "aer": {"N": "4.2"},
            "type": {"S": "VARIABLE"},
            "paidTime": {"S": "MONTHLY"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }},
        "user": {"S": "admin"},
    }'

awslocal dynamodb put-item \
    --table-name Accounts \
    --item '{
        "id": {"S": "acc-002"},
        "name": {"S": "Investment Account"},
        "bank": {"S": "HSBC"},
        "accountNumber": {"N": "87654321"},
        "sortCode": {"N": "654321"},
        "balance": {"N": "10000.00"},
        "stocks": {"L": [
            {"M": {
                "name": {"S": "Google"},
                "currentAmount": {"N": "8"},
                "gains": {"N": "132.40"},
                "currency": {"S": "USD"},
                "tickerSymbol": {"S": "GOOGL"},
                "purchaseDate": {"S": "2023-09-10"},
                "purchasePrice": {"N": "125.75"},
                "currentPrice": {"N": "142.30"}
            }},
            {"M": {
                "name": {"S": "Amazon"},
                "currentAmount": {"N": "15"},
                "gains": {"N": "384.25"},
                "currency": {"S": "USD"},
                "tickerSymbol": {"S": "AMZN"},
                "purchaseDate": {"S": "2023-07-05"},
                "purchasePrice": {"N": "130.25"},
                "currentPrice": {"N": "155.80"}
            }},
            {"M": {
                "name": {"S": "Tesla"},
                "currentAmount": {"N": "12"},
                "gains": {"N": "303.00"},
                "currency": {"S": "USD"},
                "tickerSymbol": {"S": "TSLA"},
                "purchaseDate": {"S": "2023-10-15"},
                "purchasePrice": {"N": "200.50"},
                "currentPrice": {"N": "225.75"}
            }}
        ]},
        "currentGains": {"N": "832.40"},
        "fees": {"L": [
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "25.00"},
                "frequency": {"S": "QUARTERLY"},
                "date": {"S": "2023-12-31"},
                "description": {"S": "Quarterly account maintenance fee"}
            }},
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "12.99"},
                "frequency": {"S": "ONE_TIME"},
                "date": {"S": "2023-09-10"},
                "description": {"S": "Stock purchase commission - GOOGL"}
            }},
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "15.50"},
                "frequency": {"S": "ONE_TIME"},
                "date": {"S": "2023-07-05"},
                "description": {"S": "FX conversion fee"}
            }}
        ]},
        "dateCreatedOn": {"S": "2024-01-17"},
        "interestRate": {"M": {
            "aer": {"N": "4.2"},
            "type": {"S": "VARIABLE"},
            "paidTime": {"S": "MONTHLY"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }},
        "user": {"S": "admin"},
    }'

awslocal dynamodb put-item \
    --table-name Accounts \
    --item '{
        "id": {"S": "acc-003"},
        "name": {"S": "ETF Portfolio"},
        "bank": {"S": "Nationwide"},
        "accountNumber": {"N": "11223344"},
        "sortCode": {"N": "112233"},
        "balance": {"N": "5000.75"},
        "stocks": {"L": [
            {"M": {
                "name": {"S": "Vanguard S&P 500 ETF"},
                "currentAmount": {"N": "20"},
                "gains": {"N": "510.75"},
                "currency": {"S": "USD"},
                "tickerSymbol": {"S": "VOO"},
                "purchaseDate": {"S": "2023-05-20"},
                "purchasePrice": {"N": "350.25"},
                "currentPrice": {"N": "375.80"}
            }},
            {"M": {
                "name": {"S": "Vanguard Total Stock Market ETF"},
                "currentAmount": {"N": "15"},
                "gains": {"N": "300.00"},
                "currency": {"S": "USD"},
                "tickerSymbol": {"S": "VTI"},
                "purchaseDate": {"S": "2023-11-15"},
                "purchasePrice": {"N": "200.75"},
                "currentPrice": {"N": "220.50"}
            }}
        ]},
        "currentGains": {"N": "810.75"},
        "fees": {"L": [
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "5.99"},
                "frequency": {"S": "MONTHLY"},
                "date": {"S": "2024-01-01"},
                "description": {"S": "Monthly platform fee"}
            }},
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "8.99"},
                "frequency": {"S": "ONE_TIME"},
                "date": {"S": "2023-05-20"},
                "description": {"S": "ETF purchase commission - VOO"}
            }},
            {"M": {
                "type": {"S": "FIXED"},
                "percentage": {"N": "0"},
                "amount": {"N": "8.99"},
                "frequency": {"S": "ONE_TIME"},
                "date": {"S": "2023-11-15"},
                "description": {"S": "ETF purchase commission - VTI"}
            }}
        ]},
        "dateCreatedOn": {"S": "2024-01-17"},
        "interestRate": {"M": {
            "aer": {"N": "2.8"},
            "type": {"S": "FIXED"},
            "paidTime": {"S": "MONTHLY"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }},
        "user": {"S": "user"},
    }'

echo "Accounts table:"
awslocal dynamodb scan --table-name Accounts