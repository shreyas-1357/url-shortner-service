# 🔗 URL Shortener Service

A full-stack **URL Shortener** application built using **Java**, **Spring Boot**, and **MySQL**, with support for:
- ✅ Anonymous short URL generation
- ✅ User registration & login
- ✅ Custom short URLs for authenticated users
- ✅ JDBC (No ORM)
- ✅ Secure password handling (BCrypt)
- ✅ GitHub Actions for CI
- 

---

## 🚀 Features

- 🔒 User Registration & Login using Spring Security
- 🔗 Generate short URLs (with or without account)
- ✏️ Authenticated users can create custom aliases
- 🌐 Redirects handled via short codes
- 🧪 Unit-tested controllers & services
- ⚙️ GitHub Actions CI on pull requests
- 🧩 Modular code with DTOs and layered architecture

---

## 📦 Tech Stack

| Layer         | Technology         |
|---------------|--------------------|
| Backend       | Java, Spring Boot 3 |
| Database      | MySQL / H2 (dev only) |
| Security      | Spring Security, BCrypt |
| Persistence   | JDBC (No ORM)      |
| Testing       | JUnit, Mockito     |
| Build Tool    | Maven              |
| CI/CD         | GitHub Actions     |

---

## 🛠️ Setup Instructions

### 📑 Prerequisites
- Java 17+
- Maven
- MySQL installed & running

### ⚙️ Clone the repo

```bash
git clone https://github.com/shreyas-1357/url-shortener-service.git
cd url-shortener-service
