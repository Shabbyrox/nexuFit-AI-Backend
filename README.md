# NexusFit AI - Backend Engine

NexusFit AI is a high-performance, enterprise-grade health and fitness tracking backend built with **Java 21** and **Spring Boot**. It features a robust, stateless authentication system using **JWT (JSON Web Tokens)** and integrates with cloud-based PostgreSQL and SMTP services.

## 🚀 Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 21
- **Security:** Spring Security, JJWT (JSON Web Tokens), BCrypt Hashing
- **Database:** PostgreSQL (Hosted on Supabase)
- **ORM:** Spring Data JPA / Hibernate
- **Communication:** Java Mail Sender (Gmail SMTP)
- **Build Tool:** Maven

## 🔐 Key Security Features

- **Stateless Authentication:** Implemented JWT-based security to handle user sessions without server-side state.
- **OTP Verification:** Multi-stage registration process with timed 6-digit verification codes sent via Gmail SMTP.
- **Password Protection:** Industry-standard password hashing using BCrypt.

## 🛠️ Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone <your-backend-repo-url>
   cd nexusfit-backend

