# Comprehensive Microservices & Mobile Ecosystem 🚀

This project is a full-stack distributed system that demonstrates the integration between a microservices backend, native mobile applications, and a desktop administration panel, all unified by a robust CI/CD pipeline.

---

## 🏗 System Architecture
Here you can place a diagram showing how Android apps communicate with the ASP.NET Core backend via REST API, and how Docker containers are managed.

![Architecture Diagram](PLACE_LINK_TO_IMAGE_HERE)

---

## 📱 Mobile Applications
The ecosystem includes two native Android applications, each serving different purposes or demonstrating different language capabilities.

### Features:
- **Kotlin App**: Modern UI, Coroutines for async operations, and Jetpack Compose.
- **Java App**: Robust implementation using classic Android patterns.
- **Pattern**: MVVM (Model-View-ViewModel) for clean separation of concerns.

| App Screen 1 | App Screen 2 |
| :---: | :---: |
| ![Screen 1](PLACE_LINK_TO_SCREENSHOT_HERE) | ![Screen 2](PLACE_LINK_TO_SCREENSHOT_HERE) |

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
![CI/CD Pipeline](PLACE_LINK_TO_WORKFLOW_IMAGE_HERE)

---

## 🛠 Tech Stack

- **Languages**: Kotlin, Java, C#
- **Backend**: ASP.NET Core, REST API
- **Mobile**: Android SDK, Jetpack Compose
- **DevOps**: GitHub Actions, Docker, Docker Hub
- **Tools**: Telegram Bot API (for distribution)

---

## 🔧 How to Run
1. **Backend**:
   ```bash
   docker-compose up --build
2. **Mobile**: Open the Android folder in Android Studio and build the project.

3. **Desktop**: Open the .sln file in Visual Studio.
