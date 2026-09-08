# 🌾 Lyros Market: Full System Summary

## 1. Unified Clean Architecture
Both the **Android Frontend** and **Ktor Backend** follow a mirrored **Clean Architecture** pattern (`Presentation` → `Domain` → `Data`). This ensures that business logic is isolated, making the system incredibly easy to test, maintain, and scale as the farm grows.

---

## 2. Production Hardening & Reliability
Based on professional e-commerce standards, the system includes:
- **Secure Sessions:** JWT with **Access + Refresh Tokens** and **Token Rotation** to prevent hijacking.
- **Data Integrity:** **Exposed ORM Transactions** and **Atomic Stock Updates** wrap all inventory changes.
- **Idempotency:** Unique request keys prevent duplicate orders or double M-Pesa charges during network retries.
- **Financial Security:** **Server-Side Delivery Fee Calculation** (Eldoret distance matrix) and **Secured M-Pesa Callbacks** (IP/Secret validation).
- **Performance:** **HikariCP** for backend connection pooling and **Room DB** for an offline-first mobile experience.
- **Monitoring:** **Sentry & Firebase** for real-time error tracking and performance profiling.

---

## 3. The Full Lifecycle
1. **Listing:** Admin adds produce via a secure route. Produce includes standard metrics and specialized **Agricultural Attributes** (e.g., sunlight/soil).
2. **Shopping:** Clients browse a high-performance, Pinterest-style catalog. Coil handles optimized image caching.
3. **Checkout:** App-side location pinning triggers a **Secure Backend Calculation** of transport fees.
4. **Payment:** Integration with **Safaricom Daraja API** for STK Pushes. System handles polling and callback verification.
5. **Logistics:** Orders are assigned to Drivers. Status updates (Pending → In Transit → Delivered) trigger state changes across the system.
6. **Retention:** Clients leave **Verified Purchase Reviews**; Admin can reply to feedback, creating a trusted marketplace.

---

## 4. User Role Summary

| Role | Core Capability |
| :--- | :--- |
| **Admin** | Full system control, Inventory management, Driver assignment, Review replies, Financial auditing. |
| **Client** | Browsing, Secure Purchasing (M-Pesa), Order tracking, Profile management, Verified reviews. |
| **Delivery Driver**| Task roster management, Map navigation, Real-time fulfillment status updates. |

---

## 5. Scalability & Future Growth
- **Caching:** Ready for Redis integration for high-traffic session management.
- **Real-Time:** Structured for Ktor WebSocket implementation for live driver tracking.
- **Intelligence:** Prepared for AI-driven produce recommendations based on purchase history.
