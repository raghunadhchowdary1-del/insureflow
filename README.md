# InsureFlow — Insurance Policy & Claims Management System

A full-stack Spring Boot application simulating a real-world insurance platform, covering policy management, claims processing, secure authentication, and payment integration.

## Features

- **Authentication**: Session-based login (Spring Security) + OAuth2 "Sign in with Google" (OIDC), unified via a shared `AppUserPrincipal` interface
- **Role-based access control**: CUSTOMER, AGENT, ADMIN dashboards with distinct permissions
- **Policy management**: Admin creates insurance plans (Health/Vehicle/Life); customers browse and purchase
- **Payments**: Razorpay integration (test mode) — order creation, checkout, signature verification, and webhook-based reconciliation for reliability
- **Claims workflow**: File → Under Review → Approve/Reject, with async email notifications at every stage
- **Scheduled jobs**: Automatic policy expiry job (`@Scheduled`) with a manual trigger for demo purposes
- **Dashboards**: Admin analytics with Chart.js (claims by status, policies by type, premium collected)
- **Pagination & search**: Server-side pagination and filtering across claims, policies, and payment history
- **Global exception handling**: Custom exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, `UnauthorizedActionException`) with a styled error page
- **Audit fields**: `createdAt`/`updatedAt` tracked automatically via JPA Auditing

## Tech Stack

**Backend**: Java 21, Spring Boot 3.5.16, Spring Security, Spring Data JPA (Hibernate), MySQL 8
**Frontend**: Thymeleaf, Bootstrap 5, Chart.js
**Integrations**: Razorpay Payment Gateway (test mode), Google OAuth2/OIDC, Gmail SMTP (async email)
**Build tool**: Maven

## Architecture Highlights

- **Dual authentication support**: local (session/BCrypt) and OAuth2/OIDC users are unified behind a single `AppUserPrincipal` interface so controllers don't need to know which login method was used
- **Payment reliability**: payment confirmation uses *two* independent paths — the browser's post-checkout callback (fast UX) and Razorpay webhooks (reliable server-to-server confirmation) — so payment status stays accurate even if the browser closes mid-transaction
- **Async operations**: email sending is `@Async` so it never blocks the main request thread

## Local Setup

1. Clone the repo: `git clone https://github.com/YOUR_USERNAME/insureflow.git`
2. Create a MySQL database: `CREATE DATABASE insureflow_db;`
3. Set the following environment variables:
   - `DB_PASSWORD` — your MySQL root password
   - `MAIL_USERNAME`, `MAIL_PASSWORD` — Gmail address + App Password
   - `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` — from Google Cloud Console
   - `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` — from Razorpay Dashboard (test mode)
   - `RAZORPAY_WEBHOOK_SECRET` — your chosen webhook secret
4. Run: `./mvnw spring-boot:run`
5. Visit `http://localhost:8080`

## Default Seeded Accounts

| Role | Email | Password |
|---|---|---|
| Admin | admin@insureflow.com | admin123 |
| Agent | agent@insureflow.com | agent123 |

## Future Enhancements

- Refund handling for rejected claims with prior payment
- Multi-currency support
- SMS notifications alongside email