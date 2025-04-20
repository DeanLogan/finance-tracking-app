import json
import uuid

# filepath: c:\Users\dloga\documents\code\java\finance-tracking-app\wiremock\__files\monzo\pots\acc_1.json
# Load the JSON file
file_path = r"c:\Users\dloga\documents\code\java\finance-tracking-app\wiremock\__files\monzo\pots\acc_1.json"

def generate_new_id(prefix):
    """Generate a new unique ID with the given prefix."""
    return f"{prefix}_{uuid.uuid4().hex[:16]}"

def update_ids_and_product_ids(data):
    """Update all 'id' and 'product_id' fields in the JSON data."""
    for pot in data.get("pots", []):
        if "id" in pot:
            pot["id"] = generate_new_id("pot")
        if "product_id" in pot:
            pot["product_id"] = generate_new_id("issue")
    return data

# Read the JSON file
with open(file_path, "r") as file:
    data = json.load(file)

# Update the IDs and product IDs
updated_data = update_ids_and_product_ids(data)

# Write the updated JSON back to the file
with open(file_path, "w") as file:
    json.dump(updated_data, file, indent=2)

print(f"Updated IDs and product IDs in {file_path}")