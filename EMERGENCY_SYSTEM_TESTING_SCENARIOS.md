# Emergency System Testing Scenarios

This document provides comprehensive testing scenarios for the Emergency System, covering all endpoints, validation cases, error handling, and edge cases.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Test Data Setup](#test-data-setup)
3. [Ambulance Management Tests](#ambulance-management-tests)
4. [Location Management Tests](#location-management-tests)
5. [Emergency Dispatch Tests](#emergency-dispatch-tests)
6. [WebSocket Tests](#websocket-tests)
7. [Security & Authorization Tests](#security--authorization-tests)
8. [Integration Test Scenarios](#integration-test-scenarios)
9. [Performance Test Scenarios](#performance-test-scenarios)

---

## Prerequisites

### Required Test Users

- **Paramedic User 1**: `paramedic1@test.com` (Role: PARAMEDIC)
- **Paramedic User 2**: `paramedic2@test.com` (Role: PARAMEDIC)
- **Regular User**: `user@test.com` (Role: USER)
- **Admin User**: `admin@test.com` (Role: ADMIN)

### Base URL

```
http://localhost:8080
```

### Authentication

All endpoints (except public ones) require JWT token in header:

```
Authorization: Bearer {token}
```

---

## Test Data Setup

### Sample Coordinates (Amman, Jordan)

- **City Center**: `31.9522, 35.2332`
- **North Location**: `31.9700, 35.2332`
- **South Location**: `31.9300, 35.2332`
- **East Location**: `31.9522, 35.2500`
- **West Location**: `31.9522, 35.2100`

### Sample Plate Numbers

- `AMB-001`
- `AMB-002`
- `AMB-003`

---

## Ambulance Management Tests

### Test Case 1.1: Create Ambulance (Success)

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC  
**Request Body**:

```json
{
  "x": 31.9522,
  "y": 35.2332,
  "PlateNumber": "AMB-001"
}
```

**Expected Response**: `200 OK`

```json
{
  "ambulance_id": 1,
  "plateNumber": "AMB-001",
  "paramedicId": 5,
  "paramedicName": "John Doe",
  "ambulanceStatus": "AVAILABLE"
}
```

**Validation Points**:

- ✅ Response contains correct ambulance ID
- ✅ Status is set to AVAILABLE by default
- ✅ Paramedic ID matches authenticated user
- ✅ Location is saved correctly

---

### Test Case 1.2: Create Ambulance - Duplicate Plate Number

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC  
**Request Body**:

```json
{
  "x": 31.9522,
  "y": 35.2332,
  "PlateNumber": "AMB-001"
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Ambulance with plate number AMB-001 already exists",
  "description": "Ambulance with plate number AMB-001 already exists"
}
```

---

### Test Case 1.3: Create Ambulance - Paramedic Already Has Ambulance

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC (who already has an ambulance)  
**Request Body**:

```json
{
  "x": 31.9522,
  "y": 35.2332,
  "PlateNumber": "AMB-002"
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Paramedic already has an assigned ambulance",
  "description": "Paramedic already has an assigned ambulance"
}
```

---

### Test Case 1.4: Create Ambulance - Invalid Latitude (> 90)

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC  
**Request Body**:

```json
{
  "x": 91.0,
  "y": 35.2332,
  "PlateNumber": "AMB-003"
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Latitude must be between -90 and 90. Received: 91.000000",
  "description": "Latitude must be between -90 and 90. Received: 91.000000"
}
```

---

### Test Case 1.5: Create Ambulance - Invalid Longitude (> 180)

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC  
**Request Body**:

```json
{
  "x": 31.9522,
  "y": 181.0,
  "PlateNumber": "AMB-003"
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Longitude must be between -180 and 180. Received: 181.000000"
}
```

---

### Test Case 1.6: Create Ambulance - Missing Plate Number

**Endpoint**: `POST /ambulance/create`  
**Authorization**: PARAMEDIC  
**Request Body**:

```json
{
  "x": 31.9522,
  "y": 35.2332,
  "PlateNumber": ""
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Validation failed",
  "description": "plateNumber: Plate number is required"
}
```

---

### Test Case 1.7: Create Ambulance - Unauthorized (Non-Paramedic)

**Endpoint**: `POST /ambulance/create`  
**Authorization**: USER (Regular user, not paramedic)  
**Expected Response**: `403 Forbidden`

---

### Test Case 1.8: Get Ambulance by ID (Success)

**Endpoint**: `GET /ambulance/{id}`  
**Authorization**: None required  
**Path Parameter**: `id = 1`  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "AVAILABLE"
}
```

---

### Test Case 1.9: Get Ambulance by ID (Not Found)

**Endpoint**: `GET /ambulance/999`  
**Expected Response**: `404 Not Found`

---

### Test Case 1.10: Get Ambulance by Plate Number (Success)

**Endpoint**: `GET /ambulance/plate/{plate}`  
**Path Parameter**: `plate = AMB-001`  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "AVAILABLE"
}
```

---

### Test Case 1.11: Get Ambulance by Plate Number (Not Found)

**Endpoint**: `GET /ambulance/plate/INVALID-999`  
**Expected Response**: `404 Not Found`

```json
{
  "detail": "No Ambulance found with plate number INVALID-999"
}
```

---

### Test Case 1.12: Get My Ambulance (Success)

**Endpoint**: `GET /ambulance/my-ambulance`  
**Authorization**: PARAMEDIC (who has an ambulance)  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "AVAILABLE"
}
```

---

### Test Case 1.13: Get My Ambulance (No Ambulance Assigned)

**Endpoint**: `GET /ambulance/my-ambulance`  
**Authorization**: PARAMEDIC (who doesn't have an ambulance)  
**Expected Response**: `404 Not Found`

```json
{
  "detail": "No ambulance assigned to paramedic"
}
```

---

### Test Case 1.14: Update Ambulance Status (Success - BUSY)

**Endpoint**: `PUT /ambulance/{id}/status?status=BUSY`  
**Authorization**: PARAMEDIC or ADMIN  
**Path Parameter**: `id = 1`  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "BUSY"
}
```

**Validation Points**:

- ✅ Status changed from AVAILABLE to BUSY
- ✅ Log entry created for status change

---

### Test Case 1.15: Update Ambulance Status (Success - AVAILABLE)

**Endpoint**: `PUT /ambulance/{id}/status?status=AVAILABLE`  
**Authorization**: PARAMEDIC or ADMIN  
**Path Parameter**: `id = 1`  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "AVAILABLE"
}
```

---

### Test Case 1.16: Update Ambulance Status (Invalid Status)

**Endpoint**: `PUT /ambulance/1/status?status=INVALID`  
**Authorization**: PARAMEDIC  
**Expected Response**: `400 Bad Request` (Spring validation error)

---

### Test Case 1.17: Update Ambulance Status (Not Found)

**Endpoint**: `PUT /ambulance/999/status?status=BUSY`  
**Authorization**: PARAMEDIC  
**Expected Response**: `404 Not Found`

```json
{
  "detail": "Ambulance not found with id: 999"
}
```

---

### Test Case 1.18: Update Ambulance Status (Unauthorized)

**Endpoint**: `PUT /ambulance/1/status?status=BUSY`  
**Authorization**: USER (Regular user, not paramedic/admin)  
**Expected Response**: `403 Forbidden`

---

## Location Management Tests

### Test Case 2.1: Update User Location (Success)

**Endpoint**: `POST /user/location?lat=31.9522&lng=35.2332`  
**Authorization**: Any authenticated user  
**Expected Response**: `200 OK`

```json
{
  "userId": 1,
  "latitude": 31.9522,
  "longitude": 35.2332
}
```

---

### Test Case 2.2: Update User Location (Invalid Latitude)

**Endpoint**: `POST /user/location?lat=91.0&lng=35.2332`  
**Authorization**: Any authenticated user  
**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Latitude must be between -90 and 90. Received: 91.000000"
}
```

---

### Test Case 2.3: Update User Location (Invalid Longitude)

**Endpoint**: `POST /user/location?lat=31.9522&lng=181.0`  
**Authorization**: Any authenticated user  
**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Longitude must be between -180 and 180. Received: 181.000000"
}
```

---

### Test Case 2.4: Get User Location (Success)

**Endpoint**: `GET /user/location`  
**Authorization**: User who has location set  
**Expected Response**: `200 OK`

```json
{
  "userId": 1,
  "latitude": 31.9522,
  "longitude": 35.2332
}
```

---

### Test Case 2.5: Get User Location (Not Found)

**Endpoint**: `GET /user/location`  
**Authorization**: User who has no location set  
**Expected Response**: `404 Not Found`

---

### Test Case 2.6: Update Ambulance Location via REST (Success)

**Endpoint**: `PUT /ambulance/location`  
**Authorization**: None required  
**Request Body**:

```json
{
  "ambulanceId": 1,
  "latitude": 31.97,
  "longitude": 35.2332,
  "speed": 60.5,
  "direction": 90.0
}
```

**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "latitude": 31.97,
  "longitude": 35.2332,
  "speed": 60.5,
  "direction": 90.0
}
```

**Validation Points**:

- ✅ Location updated in database
- ✅ `recordedAt` timestamp updated
- ✅ Speed and direction saved correctly

---

### Test Case 2.7: Update Ambulance Location - Invalid Speed (> 300)

**Endpoint**: `PUT /ambulance/location`  
**Request Body**:

```json
{
  "ambulanceId": 1,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 350.0,
  "direction": 90.0
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Validation failed",
  "description": "speed: Speed must not exceed 300 km/h"
}
```

---

### Test Case 2.8: Update Ambulance Location - Invalid Direction (> 360)

**Endpoint**: `PUT /ambulance/location`  
**Request Body**:

```json
{
  "ambulanceId": 1,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 60.0,
  "direction": 370.0
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Validation failed",
  "description": "direction: Direction must not exceed 360 degrees"
}
```

---

### Test Case 2.9: Update Ambulance Location - Ambulance Not Found

**Endpoint**: `PUT /ambulance/location`  
**Request Body**:

```json
{
  "ambulanceId": 999,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 60.0,
  "direction": 90.0
}
```

**Expected Response**: `404 Not Found`

```json
{
  "detail": "Ambulance not found with id: 999"
}
```

---

### Test Case 2.10: Update Ambulance Location - Create Location if Missing

**Scenario**: Update location for ambulance that doesn't have a location record yet  
**Endpoint**: `PUT /ambulance/location`  
**Request Body**:

```json
{
  "ambulanceId": 2,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 0.0,
  "direction": 0.0
}
```

**Expected Response**: `200 OK`  
**Validation Points**:

- ✅ New location record created automatically
- ✅ Warning logged: "Location record not found for ambulanceId=2, creating new location record"

---

## Emergency Dispatch Tests

### Test Case 3.1: Find Closest Available Ambulance (Success)

**Prerequisites**:

- Create 3 ambulances at different locations:
  - Ambulance 1: `31.9522, 35.2332` (City Center) - AVAILABLE
  - Ambulance 2: `31.9700, 35.2332` (North) - AVAILABLE
  - Ambulance 3: `31.9300, 35.2332` (South) - BUSY

**Endpoint**: `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`  
**Expected Response**: `200 OK`

```json
{
  "ambulanceId": 1,
  "plateNumber": "AMB-001",
  "status": "AVAILABLE"
}
```

**Validation Points**:

- ✅ Returns closest AVAILABLE ambulance (Ambulance 1)
- ✅ Does NOT return BUSY ambulance (Ambulance 3)
- ✅ Distance calculation is correct

---

### Test Case 3.2: Find Closest Available Ambulance (No Available Ambulances)

**Prerequisites**:

- All ambulances are BUSY or OFFLINE

**Endpoint**: `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`  
**Expected Response**: `404 Not Found`

---

### Test Case 3.3: Find Closest Available Ambulance (Invalid Coordinates)

**Endpoint**: `GET /ambulance/closest?latitude=91.0&longitude=35.2332`  
**Expected Response**: `400 Bad Request`

```json
{
  "detail": "Latitude must be between -90 and 90. Received: 91.000000"
}
```

---

### Test Case 3.4: Find Closest Available Ambulance (Multiple Ambulances - Correct Selection)

**Prerequisites**:

- Ambulance 1: `31.9522, 35.2332` (City Center) - AVAILABLE
- Ambulance 2: `31.9700, 35.2332` (North, 2km away) - AVAILABLE
- User Location: `31.9522, 35.2332` (City Center)

**Endpoint**: `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`  
**Expected Response**: `200 OK`  
**Validation Points**:

- ✅ Returns Ambulance 1 (closest to user)
- ✅ Does NOT return Ambulance 2 (farther away)

---

## WebSocket Tests

### Test Case 4.1: WebSocket Location Update (Success)

**WebSocket Endpoint**: `/app/ambulance/updateLocation`  
**Subscribe To**: `/topic/ambulance/locations`  
**Message**:

```json
{
  "ambulanceId": 1,
  "latitude": 31.97,
  "longitude": 35.2332,
  "speed": 65.0,
  "direction": 90.0
}
```

**Expected Behavior**:

- ✅ Message broadcasted to all subscribers
- ✅ Database updated with new location
- ✅ `recordedAt` timestamp updated
- ✅ No errors in logs

---

### Test Case 4.2: WebSocket Location Update (Null Message)

**WebSocket Endpoint**: `/app/ambulance/updateLocation`  
**Message**: `null`  
**Expected Behavior**:

- ✅ `IllegalArgumentException` thrown
- ✅ Error logged: "Received null location update via WebSocket"
- ✅ Connection remains stable

---

### Test Case 4.3: WebSocket Location Update (Invalid Validation)

**WebSocket Endpoint**: `/app/ambulance/updateLocation`  
**Message**:

```json
{
  "ambulanceId": 1,
  "latitude": 91.0,
  "longitude": 35.2332,
  "speed": 60.0,
  "direction": 90.0
}
```

**Expected Behavior**:

- ✅ `IllegalArgumentException` thrown
- ✅ Warning logged: "Validation error in WebSocket location update"
- ✅ Error propagated to GlobalExceptionHandler

---

### Test Case 4.4: WebSocket Location Update (Ambulance Not Found)

**WebSocket Endpoint**: `/app/ambulance/updateLocation`  
**Message**:

```json
{
  "ambulanceId": 999,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 60.0,
  "direction": 90.0
}
```

**Expected Behavior**:

- ✅ `NotFoundException` thrown
- ✅ Error logged
- ✅ Error handled gracefully

---

### Test Case 4.5: WebSocket Location Update (Create Location Record)

**Scenario**: Update location for ambulance without existing location record  
**WebSocket Endpoint**: `/app/ambulance/updateLocation`  
**Message**:

```json
{
  "ambulanceId": 2,
  "latitude": 31.9522,
  "longitude": 35.2332,
  "speed": 0.0,
  "direction": 0.0
}
```

**Expected Behavior**:

- ✅ New location record created automatically
- ✅ Warning logged: "Location record not found for ambulanceId=2, creating new location record"
- ✅ Location saved successfully

---

## Security & Authorization Tests

### Test Case 5.1: Create Ambulance Without Token

**Endpoint**: `POST /ambulance/create`  
**Authorization**: None  
**Expected Response**: `401 Unauthorized`

---

### Test Case 5.2: Create Ambulance with Invalid Token

**Endpoint**: `POST /ambulance/create`  
**Authorization**: `Bearer invalid_token`  
**Expected Response**: `403 Forbidden`

---

### Test Case 5.3: Create Ambulance with Expired Token

**Endpoint**: `POST /ambulance/create`  
**Authorization**: `Bearer expired_token`  
**Expected Response**: `403 Forbidden`

```json
{
  "detail": "The JWT token has expired"
}
```

---

### Test Case 5.4: Update Status - Paramedic Can Update Own Ambulance

**Endpoint**: `PUT /ambulance/{id}/status?status=BUSY`  
**Authorization**: PARAMEDIC (owner of ambulance)  
**Expected Response**: `200 OK`

---

### Test Case 5.5: Update Status - Admin Can Update Any Ambulance

**Endpoint**: `PUT /ambulance/{id}/status?status=BUSY`  
**Authorization**: ADMIN (not owner)  
**Expected Response**: `200 OK`

---

### Test Case 5.6: Get My Ambulance - Only Paramedic

**Endpoint**: `GET /ambulance/my-ambulance`  
**Authorization**: USER (not paramedic)  
**Expected Response**: `403 Forbidden`

---

## Integration Test Scenarios

### Scenario 6.1: Complete Emergency Dispatch Flow

**Steps**:

1. **Setup**: Create 2 ambulances

   - Ambulance 1: `31.9522, 35.2332` - AVAILABLE
   - Ambulance 2: `31.9700, 35.2332` - AVAILABLE

2. **User Updates Location**:

   - `POST /user/location?lat=31.9522&lng=35.2332`
   - Expected: `200 OK`

3. **Find Closest Ambulance**:

   - `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`
   - Expected: Returns Ambulance 1

4. **Update Ambulance Status to BUSY**:

   - `PUT /ambulance/1/status?status=BUSY`
   - Expected: `200 OK`, status = BUSY

5. **Verify Ambulance Not Available**:

   - `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`
   - Expected: Returns Ambulance 2 (Ambulance 1 is now BUSY)

6. **Update Ambulance Location via WebSocket**:

   - Send location update via WebSocket
   - Expected: Location updated, broadcasted to subscribers

7. **Mark Ambulance as AVAILABLE**:
   - `PUT /ambulance/1/status?status=AVAILABLE`
   - Expected: `200 OK`, status = AVAILABLE

---

### Scenario 6.2: Paramedic Registration and Ambulance Assignment

**Steps**:

1. **Paramedic Creates Ambulance**:

   - `POST /ambulance/create` with plate "AMB-001"
   - Expected: `200 OK`, ambulance created

2. **Paramedic Gets Own Ambulance**:

   - `GET /ambulance/my-ambulance`
   - Expected: Returns created ambulance

3. **Paramedic Tries to Create Second Ambulance**:
   - `POST /ambulance/create` with plate "AMB-002"
   - Expected: `400 Bad Request`, "Paramedic already has an assigned ambulance"

---

### Scenario 6.3: Concurrent Location Updates

**Steps**:

1. **Setup**: Create ambulance with ID 1

2. **Send Multiple Location Updates Simultaneously**:

   - REST: `PUT /ambulance/location` (Location A)
   - WebSocket: Send location update (Location B)
   - REST: `PUT /ambulance/location` (Location C)

3. **Expected Behavior**:
   - All updates processed
   - Final location is Location C (last update)
   - No data corruption
   - All `recordedAt` timestamps updated correctly

---

### Scenario 6.4: Ambulance Status Lifecycle

**Steps**:

1. **Create Ambulance**:

   - Status: AVAILABLE (default)

2. **Update Status to BUSY**:

   - `PUT /ambulance/1/status?status=BUSY`
   - Expected: Status = BUSY

3. **Verify Not in Closest Search**:

   - `GET /ambulance/closest?latitude=31.9522&longitude=35.2332`
   - Expected: Ambulance 1 NOT returned

4. **Update Status to OFFLINE**:

   - `PUT /ambulance/1/status?status=OFFLINE`
   - Expected: Status = OFFLINE

5. **Update Status to MAINTENANCE**:

   - `PUT /ambulance/1/status?status=MAINTENANCE`
   - Expected: Status = MAINTENANCE

6. **Update Status Back to AVAILABLE**:
   - `PUT /ambulance/1/status?status=AVAILABLE`
   - Expected: Status = AVAILABLE, now appears in closest search

---

## Performance Test Scenarios

### Test Case 7.1: High-Frequency Location Updates

**Scenario**: Send 100 location updates per second via WebSocket  
**Expected Behavior**:

- ✅ All updates processed
- ✅ Database remains consistent
- ✅ No memory leaks
- ✅ Response time < 100ms per update

---

### Test Case 7.2: Multiple Ambulances Closest Search

**Scenario**: Search with 1000 available ambulances in database  
**Expected Behavior**:

- ✅ Response time < 500ms
- ✅ Correct closest ambulance returned
- ✅ PostGIS query optimized

---

### Test Case 7.3: Concurrent WebSocket Connections

**Scenario**: 100 concurrent WebSocket connections sending updates  
**Expected Behavior**:

- ✅ All connections stable
- ✅ Messages broadcasted correctly
- ✅ No connection drops
- ✅ Memory usage stable

---

## Edge Cases & Boundary Tests

### Test Case 8.1: Coordinate Boundaries

- **Latitude = -90**: Should pass validation
- **Latitude = 90**: Should pass validation
- **Latitude = -90.1**: Should fail validation
- **Latitude = 90.1**: Should fail validation
- **Longitude = -180**: Should pass validation
- **Longitude = 180**: Should pass validation
- **Longitude = -180.1**: Should fail validation
- **Longitude = 180.1**: Should fail validation

### Test Case 8.2: Speed Boundaries

- **Speed = 0**: Should pass validation
- **Speed = 300**: Should pass validation
- **Speed = -0.1**: Should fail validation
- **Speed = 300.1**: Should fail validation

### Test Case 8.3: Direction Boundaries

- **Direction = 0**: Should pass validation
- **Direction = 360**: Should pass validation
- **Direction = -0.1**: Should fail validation
- **Direction = 360.1**: Should fail validation

### Test Case 8.4: Plate Number Edge Cases

- **Empty string**: Should fail validation
- **Whitespace only**: Should fail validation
- **Very long string (> 50 chars)**: Should fail database constraint
- **Special characters**: Should be handled (depends on business rules)

---

## Test Execution Checklist

### Pre-Test Setup

- [ ] Database is clean/reset
- [ ] Test users created with correct roles
- [ ] JWT tokens obtained for each user role
- [ ] WebSocket connection client ready
- [ ] PostGIS extension enabled in database

### Test Execution

- [ ] Run all Ambulance Management Tests (1.1 - 1.18)
- [ ] Run all Location Management Tests (2.1 - 2.10)
- [ ] Run all Emergency Dispatch Tests (3.1 - 3.4)
- [ ] Run all WebSocket Tests (4.1 - 4.5)
- [ ] Run all Security Tests (5.1 - 5.6)
- [ ] Run all Integration Scenarios (6.1 - 6.4)
- [ ] Run Performance Tests (7.1 - 7.3)
- [ ] Run Edge Cases (8.1 - 8.4)

### Post-Test Validation

- [ ] All test cases passed
- [ ] No errors in application logs
- [ ] Database integrity maintained
- [ ] No memory leaks detected
- [ ] Performance metrics within acceptable ranges

---

## Test Tools & Utilities

### Recommended Tools

1. **Postman/Insomnia**: For REST API testing
2. **WebSocket Client**: For WebSocket testing (e.g., Postman, wscat, custom client)
3. **JMeter/Gatling**: For performance testing
4. **PostgreSQL Client**: For database validation
5. **JUnit/TestNG**: For automated unit/integration tests

### Sample Postman Collection Structure

```
Emergency System Tests/
├── Authentication/
│   ├── Login Paramedic
│   ├── Login User
│   └── Login Admin
├── Ambulance Management/
│   ├── Create Ambulance
│   ├── Get Ambulance by ID
│   ├── Get Ambulance by Plate
│   ├── Get My Ambulance
│   └── Update Status
├── Location Management/
│   ├── Update User Location
│   ├── Get User Location
│   └── Update Ambulance Location
└── Emergency Dispatch/
    └── Find Closest Ambulance
```

---

## Notes

1. **Database State**: Ensure database is in a known state before each test run
2. **Token Expiry**: JWT tokens may expire during long test sessions - refresh as needed
3. **WebSocket Testing**: Use a proper WebSocket client that supports STOMP protocol
4. **Coordinate Precision**: PostGIS uses high precision - ensure test coordinates are accurate
5. **Concurrent Tests**: Some tests may interfere with each other - run sequentially or use isolated test data

---

## Version History

- **v1.0** (Current): Initial comprehensive test scenarios covering all endpoints and edge cases
