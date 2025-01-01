# E-Commerce Website

## Introduction

- **Project Overview**:
  - This is a Eccomerce WebSite
- **Technologies Used**:
  - Frontend: Angular, TailwindCss
  - Backend: Spring boot 3, Java JDK 21
  - Database: PostgreSQL
  - Supporting tools:Keycloak, Jasper, Camunda

---

## Key Features

- **For Customers**:
  - Login, Logout, forgot password
- **For Admins**:
  - CRUD products, brands, categories, sub-categoreis, etc.
  - Authorization and authentication with keycloak
  - Using Jasper to import and export report
  - Using Camunda for following add new product. User can easily manage the process in admin dashboard

---

## Installation Guide

1. **System Requirements**:
   - Environment: Node.js, Java 21, Docker
2. **Setup Instructions and run the Project**:

   - Clone the repository.
   - Run "docker compose up -d" in Keycloak_Provider/UserProvider
   - Go to keycloak with port 9091, username and password are "admin", then creating realm, client id, client secret in keycloak. Then, you need to copy and paste the config above to project at "application.properties" EcommerceBE in "src/main/resources/application.properties"
   - Run the backend at EcommerceBE with command "mvn spring-boot:run". The backend with run with port 8081
   - To run FE you need to copy config of Keycloak about to paste to "src\app\services\keycloak\keycloak.service.ts" in EcommerceFE
   - Run the frontend at EcommerceFE with command "npm start". The frontend will run with port 4200

---

## Usage

After run the project go to localhost with port 4200 and register your account. When you register sucessful, you need to go to Keycloak at localhost port 9091, then go to your realm, create real roles admin and click Users, then choose your account, click role mapping and assign your account with role admin at reaml role. Now you can go to this path [Ecommerce Shop](http://localhost:4200/admin) with your account above.

---

## Database

![Database Ecommerce](image.png)
