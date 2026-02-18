# 🏥 Patient Management System

A full-stack healthcare management web application built with **Spring Boot 4**, **Spring Security**, **Thymeleaf**, and **MySQL**, containerized with **Docker**.

---

## 📸 Features

- 👤 **Patient Management** — Create, view, update, delete patient records
- 🩺 **Consultations** — Track medical consultations (diagnosis, treatment, notes)
- 📅 **Appointments** — Schedule and manage appointments with status tracking
- 🔔 **Notifications** — Real-time notification system (INFO, ALERT, REMINDER, URGENT)
- 📊 **Dashboard** — Analytics and statistics overview
- 📄 **PDF Export** — Generate patient records as PDF documents
- 📧 **Email Notifications** — SMTP email integration via Mailtrap
- 🔐 **Security** — Role-based access control (ADMIN / USER)
- 📖 **REST API** — Full API documented with Swagger / OpenAPI 3.0

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.0.2 |
| Security | Spring Security, BCrypt |
| ORM | Spring Data JPA, Hibernate |
| Frontend | Thymeleaf, Bootstrap |
| Database | MySQL 8.0 |
| PDF | OpenPDF |
| API Docs | SpringDoc OpenAPI (Swagger) |
| DevOps | Docker, Docker Compose |
| Build | Maven 3.9.6 |

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) & Docker Compose installed

### Run with Docker

```bash
# Clone the repository
git clone https://github.com/your-username/patient-management.git
cd patient-management

# Start all services (MySQL + Spring Boot app)
docker compose up --build -d
```

The application will be available at: **http://localhost:8086**

---

## 🔑 Default Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| User | `user` | `user123` |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/ma/enset/patienttest/
│   │   ├── Config/          # Security & Data Initialization
│   │   ├── entities/        # JPA Entities (Patient, Consultation, etc.)
│   │   ├── Repositories/    # Spring Data JPA Repositories
│   │   ├── service/         # Business Logic
│   │   └── web/             # Controllers (REST & MVC)
│   └── resources/
│       ├── Templates/       # Thymeleaf Views
│       └── application.properties
├── Dockerfile               # Multi-stage Docker build
└── docker-compose.yml       # Docker Compose orchestration
```

---

## 🔌 API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/patients` | ADMIN | List all patients |
| GET | `/api/patients/{id}` | ADMIN | Get patient by ID |
| POST | `/api/patients` | ADMIN | Create patient |
| PUT | `/api/patients/{id}` | ADMIN | Update patient |
| DELETE | `/api/patients/{id}` | ADMIN | Delete patient |
| GET | `/api/notifications/count` | AUTH | Unread notifications count |

📖 Full API documentation: **http://localhost:8086/swagger-ui.html**

---

## 🔐 Role-Based Access Control

| Route | ADMIN | USER |
|---|---|---|
| `/index`, `/patient/**` | ✅ | ✅ |
| `/dashboard` | ✅ | ✅ |
| `/consultations/**` | ✅ | ✅ |
| `/rendez-vous/**` | ✅ | ✅ |
| `/admin/**`, `/deletePatient` | ✅ | ❌ |
| `/api/**` | ✅ | ❌ |

---

## 🐳 Docker Architecture

```
docker-compose.yml
├── patients-mysql   (MySQL 8.0 — port 3307)
│     └── Volume: mysql_data (persistent)
└── patients-app     (Spring Boot — port 8086)
      └── Depends on MySQL healthcheck
```

**Multi-stage Dockerfile:**
- **Stage 1** — Maven build (produces JAR)
- **Stage 2** — Lightweight Alpine JRE runtime

---

## 📊 Database Schema

```
patients          ←── consultations
    └──────────── ←── rendez_vous
    └──────────── ←── notifications

users ←──────────── user_roles ──── app_roles
```

---

## 🧪 Test Accounts Setup

Default users are created automatically on first startup by `DataInitializer`:

```java
admin / admin123  →  ROLE_ADMIN
user  / user123   →  ROLE_USER
```

---

## 📬 Contact

**Abderrahmane** — [LinkedIn](https://www.linkedin.com/in/your-profile)
📧 your-email@example.com

---

> Built with ❤️ using Spring Boot & Docker
