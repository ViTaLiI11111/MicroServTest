# Comprehensive Microservices & Mobile Ecosystem 🚀
# Name: Ukrainian Style Restaurant

This project is a full-stack distributed system that demonstrates the integration between a microservices backend, native mobile applications, and a desktop administration panel, all unified by a robust CI/CD pipeline.

---

## 🏗 System Architecture
<img width="1408" height="1126" alt="image" src="https://github.com/user-attachments/assets/03f25a00-f369-416b-a7da-60b8770e0097" />


---

## 📱 Mobile Applications
The ecosystem includes two native Android applications, each serving different purposes or demonstrating different language capabilities.

### Features:
- **Kotlin App**: Modern UI, Coroutines for async operations, and Jetpack Compose.
- **Java App**: Robust implementation using classic Android patterns.
- **Pattern**: MVVM (Model-View-ViewModel) for clean separation of concerns.

| App Screen 1 | App Screen 2 |
| :---: | :---: |
|<img width="183" height="405" alt="image" src="https://github.com/user-attachments/assets/6d5bf551-a54a-402b-8d95-f619690cdb7f" />|<img width="156" height="350" alt="image" src="https://github.com/user-attachments/assets/07eee672-9c2a-49ac-bf6b-9002547b4c3f" />|

---

## ⚙️ Backend & Infrastructure
A scalable backend architecture designed to handle requests from multiple clients.

- **Framework**: ASP.NET Core
- **Architecture**: Microservices
- **Containerization**: Docker & Docker Compose
- **Desktop Admin**: C# WinForms/WPF application for system management.

---

## 🚀 DevOps & CI/CD
This project features a production-ready automation workflow using **GitHub Actions**.

- **Automated Builds**: Every push triggers a build for both backend services and mobile apps.
- **Docker Integration**: Microservices are automatically built into images and pushed to **Docker Hub**.
- **Mobile Delivery**: Compiled `.apk` files are automatically delivered to a **Telegram Bot** for instant testing and distribution.

### Workflow Visualization:
<img width="2214" height="967" alt="image" src="https://github.com/user-attachments/assets/cf3e4a89-9a97-447a-a4a8-ad646ffdf087" />

---

## 🛠 Tech Stack

- **Languages**: Kotlin, Java, C#
- **Backend**: ASP.NET Core, REST API
- **Mobile**: Android SDK, Jetpack Compose
- **DevOps**: GitHub Actions, Docker, Docker Hub
- **Databases**: PostgreSQL, Redis
- **Tools**: Telegram Bot API (for distribution)

---

## 🔧 How to Run
1. **Backend**:
   ```bash
   docker-compose up --build
2. **Mobile**: Open the Android folder in Android Studio and build the project.

3. **Desktop**: Open the .sln file in Visual Studio.
