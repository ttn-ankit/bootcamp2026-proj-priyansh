# 🧪 COMPREHENSIVE API TESTING PLAN
# E-commerce Platform - Category & Product APIs

## 📋 IMPLEMENTATION STATUS
✅ **ALL APIS FROM SPECIFICATION ARE IMPLEMENTED**

---

## 🔧 TEST ENVIRONMENT SETUP

### Prerequisites
1. **Authentication Tokens Required:**
   - Admin Token: `Bearer {admin_jwt_token}`
   - Seller Token: `Bearer {seller_jwt_token}`
   - Customer Token: `Bearer {customer_jwt_token}`

2. **Base URL:** `http://localhost:8080`

3. **Content-Type:** `application/json` (for POST/PUT requests)

---

## 🧪 DETAILED TEST CASES

### FEATURE 1: CATEGORY METADATA MANAGEMENT

#### Test 1.1: Add Metadata Field
**Endpoint:** `POST /api/admin/category/metadata-fields`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 1.1.1: Valid metadata field creation**
```http
POST /api/admin/category/metadata-fields?fieldName=Color
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Metadata field created successfully",
  "status": 200
}
```

**Test 1.1.2: Another valid field**
```http
POST /api/admin/category/metadata-fields?fieldName=Size
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Metadata field created successfully", 
  "status": 200
}
```

##### Negative Test Cases:

**Test 1.1.3: Empty field name**
```http
POST /api/admin/category/metadata-fields?fieldName=
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Field name is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.1.4: Duplicate field name**
```http
POST /api/admin/category/metadata-fields?fieldName=Color
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Field name must be unique",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.1.5: Field name with numbers**
```http
POST /api/admin/category/metadata-fields?fieldName=Color123
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Numbers not allowed in field name",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.1.6: Unauthorized access**
```http
POST /api/admin/category/metadata-fields?fieldName=Brand
Authorization: Bearer {seller_token}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.1.7: No authentication**
```http
POST /api/admin/category/metadata-fields?fieldName=Brand

Expected Response:
Status: 401 Unauthorized
Body:
{
  "message": "Authentication is required to access the resource.",
  "status": 401,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 1.2: View All Metadata Fields
**Endpoint:** `GET /api/admin/category/metadata-fields`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 1.2.1: Get all fields without pagination**
```http
GET /api/admin/category/metadata-fields
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [
    {
      "id": 1,
      "name": "Color"
    },
    {
      "id": 2,
      "name": "Size"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": false,
      "empty": true
    }
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 2
}
```

**Test 1.2.2: Get fields with pagination**
```http
GET /api/admin/category/metadata-fields?max=5&offset=0
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body: {Paginated response with max 5 fields}
```

**Test 1.2.3: Search by field name**
```http
GET /api/admin/category/metadata-fields?fieldName=Color
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body: {Filtered results containing "Color"}
```

##### Negative Test Cases:

**Test 1.2.4: Invalid pagination parameters**
```http
GET /api/admin/category/metadata-fields?max=-1&offset=-1
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid pagination parameters",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.2.5: Unauthorized access**
```http
GET /api/admin/category/metadata-fields
Authorization: Bearer {customer_token}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---
#### Test 1.3: Add Category
**Endpoint:** `POST /api/admin/category`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 1.3.1: Create root category**
```http
POST /api/admin/category?categoryName=Electronics
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Category created successfully",
  "status": 200
}
```

**Test 1.3.2: Create subcategory**
```http
POST /api/admin/category?categoryName=Smartphones&parentId=1
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Category created successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 1.3.3: Empty category name**
```http
POST /api/admin/category?categoryName=
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Category name is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.3.4: Category name with numbers**
```http
POST /api/admin/category?categoryName=Electronics123
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Numbers not allowed in category name",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.3.5: Invalid parent ID**
```http
POST /api/admin/category?categoryName=Tablets&parentId=999
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid parent category ID",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.3.6: Subcategory same name as parent**
```http
POST /api/admin/category?categoryName=Electronics&parentId=1
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Subcategory cannot have same name as parent",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 1.4: Add Category Metadata
**Endpoint:** `POST /api/admin/category/{categoryId}/metadata-fields`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 1.4.1: Add metadata to category**
```http
POST /api/admin/category/1/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 1,
    "values": ["Red", "Blue", "Green"]
  }
]

Expected Response:
Status: 200 OK
Body:
{
  "message": "Metadata fields added to category successfully",
  "status": 200
}
```

**Test 1.4.2: Add multiple metadata fields**
```http
POST /api/admin/category/1/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 1,
    "values": ["Red", "Blue", "Green"]
  },
  {
    "fieldId": 2,
    "values": ["Small", "Medium", "Large"]
  }
]

Expected Response:
Status: 200 OK
Body:
{
  "message": "Metadata fields added to category successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 1.4.3: Invalid category ID**
```http
POST /api/admin/category/999/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 1,
    "values": ["Red"]
  }
]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid category ID",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.4.4: Invalid field ID**
```http
POST /api/admin/category/1/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 999,
    "values": ["Red"]
  }
]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid metadata field ID",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.4.5: Empty values array**
```http
POST /api/admin/category/1/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 1,
    "values": []
  }
]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Metadata field value is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 1.4.6: Duplicate values**
```http
POST /api/admin/category/1/metadata-fields
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
[
  {
    "fieldId": 1,
    "values": ["Red", "Red"]
  }
]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Metadata field value must be unique",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

### FEATURE 2: PRODUCT MANAGEMENT

#### Test 2.1: Add Product
**Endpoint:** `POST /api/seller/products`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 2.1.1: Valid product creation**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "iPhone 15",
  "description": "Latest iPhone model with advanced features",
  "brand": "Apple",
  "categoryId": 2
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product added successfully",
  "status": 200
}

Note: Product is created in inactive state by default
Email notification sent to admin about new product creation
```

**Test 2.1.2: Product with minimal required fields**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "Samsung Galaxy S24",
  "brand": "Samsung",
  "categoryId": 2
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product added successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 2.1.3: Missing required fields**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "iPhone"
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z",
  "errors": [
    {
      "field": "brand",
      "message": "Brand is required"
    },
    {
      "field": "categoryId", 
      "message": "Category ID is required"
    }
  ]
}
```

**Test 2.1.4: Invalid category ID (non-leaf)**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "Test Product",
  "brand": "Test Brand",
  "categoryId": 1
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Category must be valid leaf",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.1.5: Duplicate product**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "iPhone 15",
  "brand": "Apple",
  "categoryId": 2
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product must be unique for brand and category",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.1.6: Non-existent category**
```http
POST /api/seller/products
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "Test Product",
  "brand": "Test Brand", 
  "categoryId": 999
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Category must be valid leaf",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---
#### Test 2.2: Add Product Variation
**Endpoint:** `POST /api/seller/{productId}/variations`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 2.2.1: Valid variation creation**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {
    "Color": "Red",
    "Size": "64GB"
  },
  "primaryImageUrl": "1.jpg",
  "secondaryImageUrls": ["image_1.jpg", "image_2.jpg"],
  "quantityAvailable": 100,
  "price": 999.99
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product variation created successfully",
  "status": 200
}
```

**Test 2.2.2: Variation without secondary images**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {
    "Color": "Blue",
    "Size": "128GB"
  },
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 50,
  "price": 1099.99
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product variation created successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 2.2.3: Product not found**
```http
POST /api/seller/999/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "Red"},
  "primaryImageUrl": "999.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product not found",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.4: Inactive product**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "Green"},
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product must be active to add variation",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.5: Invalid primary image naming**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "Red"},
  "primaryImageUrl": "wrong_name.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Primary image format invalid. Expected format: {productId}.{extension}",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.6: Invalid secondary image naming**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "Red"},
  "primaryImageUrl": "1.jpg",
  "secondaryImageUrls": ["wrong_format.jpg"],
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Secondary image format invalid. Expected format: image_{number}.{extension}",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.7: Missing required metadata**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z",
  "errors": [
    {
      "field": "metadata",
      "message": "Metadata is mandatory"
    }
  ]
}
```

**Test 2.2.8: Invalid metadata field**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"InvalidField": "Value"},
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid metadata field",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.9: Invalid metadata value**
```http
POST /api/seller/1/variations
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "InvalidColor"},
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid metadata value",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.2.10: Not product owner**
```http
POST /api/seller/1/variations
Authorization: Bearer {different_seller_token}
Content-Type: application/json

Request Body:
{
  "metadata": {"Color": "Red"},
  "primaryImageUrl": "1.jpg",
  "quantityAvailable": 10,
  "price": 100.00
}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 2.3: View All Products (Seller)
**Endpoint:** `GET /api/seller/products`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 2.3.1: Get all products without pagination**
```http
GET /api/seller/products
Authorization: Bearer {seller_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "brand": "Apple",
      "description": "Latest iPhone model",
      "isActive": false,
      "isDeleted": false,
      "categoryName": "Smartphones",
      "createdDate": "2024-01-01T10:00:00Z",
      "lastModifiedDate": "2024-01-01T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

**Test 2.3.2: Get products with pagination**
```http
GET /api/seller/products?max=5&offset=0
Authorization: Bearer {seller_token}

Expected Response:
Status: 200 OK
Body: {Paginated response with max 5 products}
```

**Test 2.3.3: Sort by name ascending**
```http
GET /api/seller/products?sort=name&order=ASC
Authorization: Bearer {seller_token}

Expected Response:
Status: 200 OK
Body: {Products sorted by name in ascending order}
```

**Test 2.3.4: Sort by creation date descending**
```http
GET /api/seller/products?sort=created&order=DESC
Authorization: Bearer {seller_token}

Expected Response:
Status: 200 OK
Body: {Products sorted by creation date in descending order}
```

##### Negative Test Cases:

**Test 2.3.5: Invalid pagination parameters**
```http
GET /api/seller/products?max=-1&offset=-1
Authorization: Bearer {seller_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid pagination parameters",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.3.6: Unauthorized access**
```http
GET /api/seller/products
Authorization: Bearer {customer_token}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---
#### Test 2.4: Update Product
**Endpoint:** `PUT /api/seller/{productId}`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 2.4.1: Valid product update**
```http
PUT /api/seller/1
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "iPhone 15 Pro",
  "description": "Updated iPhone model with Pro features",
  "brand": "Apple"
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product updated successfully",
  "status": 200
}
```

**Test 2.4.2: Partial update (description only)**
```http
PUT /api/seller/1
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "description": "Updated description only"
}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product updated successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 2.4.3: Product not found**
```http
PUT /api/seller/999
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": "Updated Product"
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product not found",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.4.4: Not product owner**
```http
PUT /api/seller/1
Authorization: Bearer {different_seller_token}
Content-Type: application/json

Request Body:
{
  "name": "Updated Product"
}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.4.5: Invalid data (empty name)**
```http
PUT /api/seller/1
Authorization: Bearer {seller_token}
Content-Type: application/json

Request Body:
{
  "name": ""
}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z",
  "errors": [
    {
      "field": "name",
      "message": "Product name cannot be empty"
    }
  ]
}
```

---

#### Test 2.5: Delete Product
**Endpoint:** `DELETE /api/seller/{productId}`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 2.5.1: Valid product deletion**
```http
DELETE /api/seller/1
Authorization: Bearer {seller_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product deleted successfully",
  "status": 200
}
```

##### Negative Test Cases:

**Test 2.5.2: Product not found**
```http
DELETE /api/seller/999
Authorization: Bearer {seller_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product not found",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.5.3: Not product owner**
```http
DELETE /api/seller/1
Authorization: Bearer {different_seller_token}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 2.5.4: Invalid product ID format**
```http
DELETE /api/seller/abc
Authorization: Bearer {seller_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product ID must be a positive number",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

### FEATURE 3: ADMIN PRODUCT MANAGEMENT

#### Test 3.1: List All Products (Admin)
**Endpoint:** `GET /api/admin/products`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 3.1.1: Get all products**
```http
GET /api/admin/products
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "brand": "Apple",
      "description": "Latest iPhone model",
      "isActive": false,
      "isDeleted": false,
      "categoryName": "Smartphones",
      "sellerName": "John Doe",
      "sellerEmail": "john@example.com",
      "createdDate": "2024-01-01T10:00:00Z",
      "lastModifiedDate": "2024-01-01T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

**Test 3.1.2: Get products with pagination**
```http
GET /api/admin/products?max=10&offset=0
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body: {Paginated response with max 10 products}
```

##### Negative Test Cases:

**Test 3.1.3: Unauthorized access**
```http
GET /api/admin/products
Authorization: Bearer {seller_token}

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 3.2: Toggle Product Status
**Endpoint:** `PUT /api/admin/products/{id}/status`
**Auth:** Admin Token Required

##### Positive Test Cases:

**Test 3.2.1: Activate inactive product**
```http
PUT /api/admin/products/1/status?activate=true
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product activated successfully",
  "status": 200
}

Note: Email notification sent to seller about product activation
```

**Test 3.2.2: Deactivate active product**
```http
PUT /api/admin/products/1/status?activate=false
Authorization: Bearer {admin_token}

Expected Response:
Status: 200 OK
Body:
{
  "message": "Product deactivated successfully",
  "status": 200
}

Note: Email notification sent to seller about product deactivation
```

##### Negative Test Cases:

**Test 3.2.3: Product not found**
```http
PUT /api/admin/products/999/status?activate=true
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product not found",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 3.2.4: Already active product**
```http
PUT /api/admin/products/1/status?activate=true
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product is already active",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 3.2.5: Already inactive product**
```http
PUT /api/admin/products/1/status?activate=false
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product is already deactivated",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 3.2.6: Missing activate parameter**
```http
PUT /api/admin/products/1/status
Authorization: Bearer {admin_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Activate parameter is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---
### FEATURE 4: CUSTOMER PRODUCT VIEWING

#### Test 4.1: View Single Product
**Endpoint:** `GET /api/customer/product/{productId}`
**Auth:** Customer Token Required

##### Positive Test Cases:

**Test 4.1.1: View active product**
```http
GET /api/customer/product/1
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body:
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "description": "Latest iPhone model with advanced features",
  "categoryName": "Smartphones",
  "sellerName": "John Doe",
  "isActive": true,
  "variations": [
    {
      "id": 1,
      "price": 999.99,
      "quantityAvailable": 100,
      "metadata": {
        "Color": "Red",
        "Size": "64GB"
      },
      "primaryImageUrl": "/api/products/1/images/primary/1.jpg",
      "secondaryImageUrls": [
        "/api/products/1/images/secondary/1_1.jpg",
        "/api/products/1/images/secondary/1_2.jpg"
      ],
      "isActive": true
    }
  ],
  "createdDate": "2024-01-01T10:00:00Z"
}
```

##### Negative Test Cases:

**Test 4.1.2: Product not found**
```http
GET /api/customer/product/999
Authorization: Bearer {customer_token}

Expected Response:
Status: 404 Not Found
Body:
{
  "message": "Product not found",
  "status": 404,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 4.1.3: Inactive product**
```http
GET /api/customer/product/1
Authorization: Bearer {customer_token}

Expected Response:
Status: 404 Not Found
Body:
{
  "message": "Product not found",
  "status": 404,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 4.1.4: Invalid product ID format**
```http
GET /api/customer/product/abc
Authorization: Bearer {customer_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product ID must be a positive number",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 4.2: View Products by Category
**Endpoint:** `GET /api/customer/category/{categoryId}/products`
**Auth:** Customer Token Required

##### Positive Test Cases:

**Test 4.2.1: Get products for leaf category**
```http
GET /api/customer/category/2/products
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "brand": "Apple",
      "description": "Latest iPhone model",
      "categoryName": "Smartphones",
      "sellerName": "John Doe",
      "minPrice": 999.99,
      "maxPrice": 1199.99,
      "primaryImageUrl": "/api/products/1/images/primary/1.jpg",
      "isActive": true,
      "availableVariations": 3
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

**Test 4.2.2: Get products with pagination**
```http
GET /api/customer/category/2/products?max=5&offset=0
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body: {Paginated response with max 5 products}
```

**Test 4.2.3: Get products for parent category**
```http
GET /api/customer/category/1/products
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body: {Products from all subcategories of Electronics}
```

##### Negative Test Cases:

**Test 4.2.4: Invalid category ID**
```http
GET /api/customer/category/999/products
Authorization: Bearer {customer_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid category ID",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 4.2.5: Category with no products**
```http
GET /api/customer/category/3/products
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 0,
  "totalPages": 0,
  "last": true,
  "first": true
}
```

---

#### Test 4.3: View Similar Products
**Endpoint:** `GET /api/customer/{productId}/similar`
**Auth:** Customer Token Required

##### Positive Test Cases:

**Test 4.3.1: Get similar products**
```http
GET /api/customer/1/similar
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [
    {
      "id": 2,
      "name": "Samsung Galaxy S24",
      "brand": "Samsung",
      "description": "Latest Samsung flagship",
      "categoryName": "Smartphones",
      "sellerName": "Jane Smith",
      "minPrice": 899.99,
      "maxPrice": 1099.99,
      "primaryImageUrl": "/api/products/2/images/primary/2.jpg",
      "isActive": true,
      "availableVariations": 2
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

**Test 4.3.2: Similar products with pagination**
```http
GET /api/customer/1/similar?max=5&offset=0
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body: {Paginated similar products}
```

##### Negative Test Cases:

**Test 4.3.3: Product not found**
```http
GET /api/customer/999/similar
Authorization: Bearer {customer_token}

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Product not found",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 4.3.4: No similar products**
```http
GET /api/customer/1/similar
Authorization: Bearer {customer_token}

Expected Response:
Status: 200 OK
Body:
{
  "content": [],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 0,
  "totalPages": 0,
  "last": true,
  "first": true
}
```

---

### FEATURE 5: PRODUCT IMAGE MANAGEMENT

#### Test 5.1: Upload Primary Image
**Endpoint:** `POST /api/seller/products/{productId}/images/primary`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 5.1.1: Valid primary image upload**
```http
POST /api/seller/products/1/images/primary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
image: [JPG/PNG file, max 5MB]

Expected Response:
Status: 200 OK
Body:
{
  "message": "Image uploaded successfully",
  "status": 200
}

Note: Image saved as {productId}.{extension} in uploads/products/{productId}/ directory
```

##### Negative Test Cases:

**Test 5.1.2: File too large**
```http
POST /api/seller/products/1/images/primary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
image: [File > 5MB]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Image file is too large. Maximum size allowed is 5MB",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 5.1.3: Invalid file format**
```http
POST /api/seller/products/1/images/primary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
image: [PDF/TXT file]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Invalid image format. Only JPG, JPEG, PNG formats are allowed",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 5.1.4: No file provided**
```http
POST /api/seller/products/1/images/primary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
(empty)

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Image file is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 5.1.5: Product not owned by seller**
```http
POST /api/seller/products/1/images/primary
Authorization: Bearer {different_seller_token}
Content-Type: multipart/form-data

Request Body:
image: [Valid JPG file]

Expected Response:
Status: 403 Forbidden
Body:
{
  "message": "Access denied. You do not have permission to access this resource.",
  "status": 403,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 5.2: Upload Secondary Images
**Endpoint:** `POST /api/seller/products/{productId}/images/secondary`
**Auth:** Seller Token Required

##### Positive Test Cases:

**Test 5.2.1: Valid secondary images upload**
```http
POST /api/seller/products/1/images/secondary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
images: [Multiple JPG/PNG files, each max 5MB]

Expected Response:
Status: 200 OK
Body:
{
  "message": "Images uploaded successfully",
  "status": 200
}

Note: Images saved as {productId}_1.{ext}, {productId}_2.{ext}, etc.
```

##### Negative Test Cases:

**Test 5.2.2: No files provided**
```http
POST /api/seller/products/1/images/secondary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
(empty)

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Image file is required",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

**Test 5.2.3: One file too large**
```http
POST /api/seller/products/1/images/secondary
Authorization: Bearer {seller_token}
Content-Type: multipart/form-data

Request Body:
images: [Valid JPG, File > 5MB, Valid PNG]

Expected Response:
Status: 400 Bad Request
Body:
{
  "message": "Image file is too large. Maximum size allowed is 5MB",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

#### Test 5.3: Get Product Images (Public Access)

##### Test 5.3.1: Get Primary Image
```http
GET /api/products/1/images/primary/1.jpg

Expected Response:
Status: 200 OK
Content-Type: image/jpeg
Body: [Image binary data]
Headers:
Content-Disposition: inline; filename="1.jpg"
```

##### Test 5.3.2: Get Secondary Image
```http
GET /api/products/1/images/secondary/1_1.jpg

Expected Response:
Status: 200 OK
Content-Type: image/jpeg
Body: [Image binary data]
Headers:
Content-Disposition: inline; filename="1_1.jpg"
```

##### Test 5.3.3: Get Secondary Image Filenames
```http
GET /api/products/1/images/secondary

Expected Response:
Status: 200 OK
Body: ["1_1.jpg", "1_2.jpg", "1_3.jpg"]
```

##### Test 5.3.4: Image Not Found
```http
GET /api/products/1/images/primary/nonexistent.jpg

Expected Response:
Status: 404 Not Found
Body:
{
  "message": "Image not found",
  "status": 404,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

## 📊 TEST EXECUTION CHECKLIST

### Pre-Test Setup:
- [ ] Create admin, seller, and customer accounts
- [ ] Generate authentication tokens for all user types
- [ ] Create test categories with metadata fields
- [ ] Prepare test image files (JPG, PNG) of various sizes
- [ ] Set up test database with clean state

### Test Execution Order:
1. [ ] Category Metadata Management (Tests 1.1-1.4)
2. [ ] Product Management (Tests 2.1-2.5)
3. [ ] Admin Product Management (Tests 3.1-3.2)
4. [ ] Customer Product Viewing (Tests 4.1-4.3)
5. [ ] Product Image Management (Tests 5.1-5.3)

### Post-Test Validation:
- [ ] Verify email notifications are sent correctly
- [ ] Check database state consistency
- [ ] Validate file system state (uploaded images)
- [ ] Confirm proper cleanup of test data

---

## 🎯 EXPECTED OUTCOMES

### Success Criteria:
- All positive test cases return expected responses
- All negative test cases return appropriate error messages
- All edge cases are handled gracefully
- Email notifications are sent when required
- File uploads work correctly with proper validation
- Authentication and authorization work as expected
- Database constraints are properly enforced

### Performance Expectations:
- API response times < 2 seconds for normal operations
- Image upload operations < 10 seconds for 5MB files
- Pagination works efficiently for large datasets
- Database queries are optimized

---

**Total Test Cases: 85+**
- **Positive Cases:** 35+
- **Negative Cases:** 40+
- **Edge Cases:** 10+

This comprehensive testing plan covers all implemented APIs with realistic request/response examples based on the actual codebase implementation.