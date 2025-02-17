import boto3

def list_dynamodb_tables():
    dynamodb = boto3.client(
        'dynamodb',
        region_name='eu-west-1',
        endpoint_url='http://localhost:4566'
    )

    response = dynamodb.list_tables()
    tables = response.get('TableNames', [])

    if tables:
        print("DynamoDB Tables:")
        for table in tables:
            print(f"- {table}")
    else:
        print("No tables found.")

def get_dynamodb_item(table_name, key):
    dynamodb = boto3.client(
        'dynamodb',
        region_name='eu-west-1',
        endpoint_url='http://localhost:4566'
    )

    response = dynamodb.get_item(
        TableName=table_name,
        Key=key
    )

    item = response.get('Item')
    if item:
        print(f"Item from table {table_name}:")
        print(item)
    else:
        print(f"No item found in table {table_name} with key {key}.")

if __name__ == "__main__":
    list_dynamodb_tables()
    
    # Example usage of get_dynamodb_item
    table_name = 'Accounts'
    key = {'id': {'S': 'acc-001'}}
    get_dynamodb_item(table_name, key)