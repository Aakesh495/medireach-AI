# MediReach AI — Firestore Security Specification

## 1. Data Invariants

1. **Strict Multi-Tenant User Isolation**:
   - Every document in `/users/{userId}/health_records/{recordId}`, `/users/{userId}/reminders/{reminderId}`, and `/users/{userId}/privacy_settings/{settingId}` belongs exclusively to `userId`.
   - `request.auth != null` and `request.auth.uid == userId` are mandatory for all reads (`get`, `list`) and writes (`create`, `update`, `delete`).
   - `incoming().userId == request.auth.uid` and `incoming().userId == userId` are enforced on every write.
2. **Immutable Metadata (`userId`, `createdAt`)**:
   - `userId` and `createdAt` cannot be modified during `update` (`incoming().userId == existing().userId && incoming().createdAt == existing().createdAt`).
3. **Temporal Integrity**:
   - `createdAt` and `updatedAt` must be valid Firestore `timestamp` objects satisfying `<= request.time`.
4. **Schema & Bounds Validation**:
   - All string fields have strict `.size()` upper bounds to prevent Denial-of-Wallet storage exhaustion.
   - All document IDs (`userId`, `recordId`, `reminderId`, `settingId`) are validated with `isValidId()` (`^[a-zA-Z0-9_\-]+$`, max 128 chars).
   - Every `create` and `update` enforces `hasOnly(...)` on keys/affectedKeys so no shadow/ghost fields can be injected.

## 2. The "Dirty Dozen" Adversarial Payloads

1. **Unauthenticated Read**: `unauthDb. doc('/users/alice_123/health_records/rec_1').get()` -> REJECTED (`request.auth != null`).
2. **Cross-User Read (PII Leak)**: `bobDb.doc('/users/alice_123/health_records/rec_1').get()` -> REJECTED (`request.auth.uid == userId`).
3. **Cross-User List Scraping**: `bobDb.collection('/users/alice_123/health_records').get()` -> REJECTED (`request.auth.uid == userId` & `resource.data.userId == request.auth.uid`).
4. **Identity Spoofing on Create**: `aliceDb.doc('/users/alice_123/health_records/rec_1').set({ userId: 'bob_456', ... })` -> REJECTED (`incoming().userId == request.auth.uid`).
5. **Shadow / Ghost Field Injection on Create**: Adding `isAdmin: true` to a health record create payload -> REJECTED (`keys().hasOnly(...)`).
6. **Shadow / Ghost Field Injection on Update**: Adding `isVerified: true` during an update -> REJECTED (`affectedKeys().hasOnly(...)`).
7. **Immutable `createdAt` Tampering**: Updating `createdAt` to a new timestamp on an existing health record -> REJECTED (`incoming().createdAt == existing().createdAt`).
8. **Immutable `userId` Transfer**: Updating `userId` to another user's UID -> REJECTED (`incoming().userId == existing().userId`).
9. **Enum Poisoning**: Setting `category: 'INVALID_CATEGORY'` on `health_records` or `reminderType: 'HACK'` on `reminders` -> REJECTED (`isValidHealthRecord` / `isValidHealthReminder`).
10. **String Overflow (Denial of Wallet)**: Injecting a 5,000-character string into `title` (max 150) -> REJECTED (`data.title.size() <= 150`).
11. **Type Confusion on Booleans**: Passing `completed: "yes"` instead of a boolean -> REJECTED (`data.completed is bool`).
12. **Future Timestamp Spoofing**: Passing a timestamp in the future (`> request.time`) for `createdAt` or `updatedAt` -> REJECTED (`<= request.time`).
