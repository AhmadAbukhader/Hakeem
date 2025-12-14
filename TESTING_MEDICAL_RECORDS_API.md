# Testing Medical Records POST API

## Endpoint

```
POST /medical-records/interview
```

## Authentication

Requires JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

## Required Role

- PATIENT role is required (enforced by `@PreAuthorize("hasAnyRole('PATIENT')")`)

## Request Headers

```
Content-Type: application/json
Authorization: Bearer <your_jwt_token>
```

## Request Body Structure

### Complete Example (with all fields)

See `test_medical_records_post_request.json`

### Minimal Example (minimum required fields)

See `test_medical_records_post_request_minimal.json`

## Field Descriptions

### `questions_and_answers` (Required)

- Array of question-answer pairs
- Must have at least one Q&A pair
- Each object contains:
  - `question` (String): The question text
  - `answer` (String): The answer text

### `symptoms` (Optional)

- Array of symptom names (strings)
- Can be empty array or null
- Example: `["fever", "headache", "cough"]`

### `diagnoses` (Optional)

- Array of diagnosis objects
- Can be empty array or null
- Each diagnosis contains:
  - `name` (String): Diagnosis name
  - `probability` (Float): Probability value (0.0 to 1.0)

### `doctor_recommendation` (Optional)

- Object containing doctor specialty recommendation
- Can be null
- Contains:
  - `name` (String): Doctor specialty name (e.g., "Cardiologist", "General Practitioner")

## cURL Examples

### Complete Request

```bash
curl -X POST http://localhost:8089/medical-records/interview \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "questions_and_answers": [
      {
        "question": "How long have you been experiencing these symptoms?",
        "answer": "About 3 days now"
      },
      {
        "question": "Can you describe the severity of your pain?",
        "answer": "Around 7 out of 10"
      }
    ],
    "symptoms": ["fever", "headache", "cough"],
    "diagnoses": [
      {
        "name": "Common Cold",
        "probability": 0.65
      }
    ],
    "doctor_recommendation": {
      "name": "General Practitioner"
    }
  }'
```

### Minimal Request

```bash
curl -X POST http://localhost:8089/medical-records/interview \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "questions_and_answers": [
      {
        "question": "What symptoms are you experiencing?",
        "answer": "Headache"
      }
    ]
  }'
```

## Expected Response

### Success Response (200 OK)

```json
{
  "interview_id": 1,
  "patient_id": 5,
  "questions_and_answers": [
    {
      "question": "How long have you been experiencing these symptoms?",
      "answer": "About 3 days now"
    },
    {
      "question": "Can you describe the severity of your pain?",
      "answer": "Around 7 out of 10"
    }
  ],
  "started_at": "2024-01-15T10:30:00",
  "symptoms": [
    {
      "id": 1,
      "name": "fever"
    },
    {
      "id": 2,
      "name": "headache"
    },
    {
      "id": 3,
      "name": "cough"
    }
  ],
  "diagnoses": [
    {
      "id": 1,
      "name": "Common Cold",
      "probability": 0.65
    }
  ],
  "doctor_recommendation": {
    "id": 1,
    "name": "General Practitioner"
  }
}
```

## Error Responses

### 400 Bad Request

- Missing `questions_and_answers` field
- Empty `questions_and_answers` array
- Invalid data format

### 401 Unauthorized

- Missing or invalid JWT token
- User doesn't have PATIENT role

### 500 Internal Server Error

- Server-side error occurred

## Testing Checklist

- [ ] Test with complete request (all fields)
- [ ] Test with minimal request (only required fields)
- [ ] Test with empty symptoms array
- [ ] Test with null symptoms
- [ ] Test with empty diagnoses array
- [ ] Test with null diagnoses
- [ ] Test with null doctor_recommendation
- [ ] Test without authentication (should return 401)
- [ ] Test with invalid JWT token (should return 401)
- [ ] Test with user without PATIENT role (should return 403)
- [ ] Verify response contains all saved data with generated IDs
