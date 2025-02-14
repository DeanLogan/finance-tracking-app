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
            "rate": {"N": "4.2"},
            "type": {"S": "VARIABLE"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }}
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
                "symbol": {"S": "GOOGL"},
                "quantity": {"N": "8"},
                "purchasePrice": {"N": "125.75"},
                "currentPrice": {"N": "142.30"},
                "purchaseDate": {"S": "2023-09-10"}
            }},
            {"M": {
                "symbol": {"S": "AMZN"},
                "quantity": {"N": "15"},
                "purchasePrice": {"N": "130.25"},
                "currentPrice": {"N": "155.80"},
                "purchaseDate": {"S": "2023-07-05"}
            }},
            {"M": {
                "symbol": {"S": "TSLA"},
                "quantity": {"N": "12"},
                "purchasePrice": {"N": "200.50"},
                "currentPrice": {"N": "225.75"},
                "purchaseDate": {"S": "2023-10-15"}
            }}
        ]},
        "currentGains": {"N": "832.40"},
        "fees": {"L": [
            {"M": {
                "type": {"S": "ACCOUNT_MAINTENANCE"},
                "amount": {"N": "25.00"},
                "date": {"S": "2023-12-31"},
                "description": {"S": "Quarterly account maintenance fee"}
            }},
            {"M": {
                "type": {"S": "TRADING"},
                "amount": {"N": "12.99"},
                "date": {"S": "2023-09-10"},
                "description": {"S": "Stock purchase commission - GOOGL"}
            }},
            {"M": {
                "type": {"S": "FOREIGN_EXCHANGE"},
                "amount": {"N": "15.50"},
                "date": {"S": "2023-07-05"},
                "description": {"S": "FX conversion fee"}
            }}
        ]},
        "dateCreatedOn": {"S": "2024-01-17"},
        "interestRate": {"M": {
            "rate": {"N": "4.2"},
            "type": {"S": "VARIABLE"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }}
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
                "symbol": {"S": "VOO"},
                "quantity": {"N": "20"},
                "purchasePrice": {"N": "350.25"},
                "currentPrice": {"N": "375.80"},
                "purchaseDate": {"S": "2023-05-20"}
            }},
            {"M": {
                "symbol": {"S": "VTI"},
                "quantity": {"N": "15"},
                "purchasePrice": {"N": "200.75"},
                "currentPrice": {"N": "220.50"},
                "purchaseDate": {"S": "2023-11-15"}
            }}
        ]},
        "currentGains": {"N": "810.75"},
        "fees": {"L": [
            {"M": {
                "type": {"S": "PLATFORM"},
                "amount": {"N": "5.99"},
                "date": {"S": "2024-01-01"},
                "description": {"S": "Monthly platform fee"}
            }},
            {"M": {
                "type": {"S": "TRADING"},
                "amount": {"N": "8.99"},
                "date": {"S": "2023-05-20"},
                "description": {"S": "ETF purchase commission - VOO"}
            }},
            {"M": {
                "type": {"S": "TRADING"},
                "amount": {"N": "8.99"},
                "date": {"S": "2023-11-15"},
                "description": {"S": "ETF purchase commission - VTI"}
            }}
        ]},
        "dateCreatedOn": {"S": "2024-01-17"},
        "interestRate": {"M": {
            "rate": {"N": "2.8"},
            "type": {"S": "FIXED"},
            "startDate": {"S": "2024-01-17"},
            "endDate": {"S": "2025-01-17"}
        }}
    }'


echo "Accounts table:"
awslocal dynamodb scan --table-name Accounts
