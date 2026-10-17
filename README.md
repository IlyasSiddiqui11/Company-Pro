# CompanyPro - Employee Management System

A modern full-stack Enterprise Employee Management System built with **Spring Boot 3**, **Neon PostgreSQL**, and a responsive web **Frontend**.

---

## 📁 Project Structure

```text
CompanyPro/
├── backend/                      # Spring Boot REST API
│   ├── src/                      # Java source code and configuration
│   │   ├── main/
│   │   │   ├── java/com/example/employeemanagement/
│   │   │   │   ├── config/       # Security, Web MVC & Data Initializer
│   │   │   │   ├── controller/   # REST Controllers (Auth, Employees, Departments, Dashboard)
│   │   │   │   ├── dto/          # Data Transfer Objects
│   │   │   │   ├── entity/       # JPA Entities (AppUser, Department, Employee)
│   │   │   │   ├── exception/    # Custom Exception Handlers
│   │   │   │   ├── repository/   # Spring Data JPA Repositories
│   │   │   │   └── service/      # Business Logic Services
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                 # Test suites (uses in-memory H2)
│   ├── .env.example              # Neon DB environment variables template
│   ├── pom.xml                   # Maven dependencies and build configuration
│   └── mvnw.cmd                  # Maven wrapper
│
├── frontend/                     # Web Dashboard Frontend
│   ├── css/                      # Stylesheets
│   ├── js/                       # Client-side scripts
│   │   ├── auth.js               # Authentication & session management
│   │   ├── common.js             # Shared helpers and utilities
│   │   ├── dashboard.js          # Real-time dashboard statistics
│   │   ├── departments.js        # Department management
│   │   ├── employees.js          # Employee management & filters
│   │   └── reports.js            # Exportable Word / PDF reporting
│   ├── index.html                # Welcome page
│   ├── login.html                # Admin authentication
│   ├── dashboard.html            # Analytics dashboard
│   ├── employees.html            # Employee directory
│   ├── departments.html          # Department records
│   ├── reports.html              # Management reports
│   └── profile.html              # Administrator profile
│
└── README.md
```

---

## 🚀 Quick Start

### 1. Configure Database Credentials
Copy `backend/.env.example` to `backend/.env` (and/or `.env` in the root) and provide your Neon PostgreSQL credentials:

```env
DB_HOST=your-neon-host.us-east-2.aws.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USERNAME=neondb_owner
DB_PASSWORD=your_neon_password
DB_SSLMODE=require
DB_URL=jdbc:postgresql://your-neon-host.us-east-2.aws.neon.tech:5432/neondb?sslmode=require
```

### 2. Run the Backend Application
```powershell
cd backend
.\mvn.cmd spring-boot:run
```

The server starts on `http://localhost:8080/`.

### 3. Access the Frontend
Open `http://localhost:8080/` in your browser.
Default login credentials:
- **Username**: `admin`
- **Password**: `admin123`
