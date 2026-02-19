# Comprehensive Microservices & Mobile Ecosystem 🚀

This project is a full-stack distributed system that demonstrates the integration between a microservices backend, native mobile applications, and a desktop administration panel, all unified by a robust CI/CD pipeline.

---

## 🏗 System Architecture
<img width="482" height="293" alt="image" src="https://github.com/user-attachments/assets/5ab9b42f-5518-4bdd-bc48-96777a6ed7df" />

---

## 📱 Mobile Applications
The ecosystem includes two native Android applications, each serving different purposes or demonstrating different language capabilities.

### Features:
- **Kotlin App**: Modern UI, Coroutines for async operations, and Jetpack Compose.
- **Java App**: Robust implementation using classic Android patterns.
- **Pattern**: MVVM (Model-View-ViewModel) for clean separation of concerns.

| App Screen 1 | App Screen 2 |
| :---: | :---: |
|<img width="183" height="405" alt="image" src="https://github.com/user-attachments/assets/6d5bf551-a54a-402b-8d95-f619690cdb7f" />
|<img width="156" height="350" alt="image" src="https://github.com/user-attachments/assets/07eee672-9c2a-49ac-bf6b-9002547b4c3f" />|

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
graph TD
    %% Тригери (Події)
    Start((Push to Main / <br/>Manual Trigger)) --> PathCheck{Identify Changed <br/>Paths}

    %% Маршрутизація за шляхами
    PathCheck -- "API/**" --> Backend[Backend Services CI]
    PathCheck -- "ClientSide/**" --> AndroidClient[Android Client CI]
    PathCheck -- "StuffSide/**" --> AndroidStaff[Android Stuff CI]
    PathCheck -- "gateway/**" --> Gateway[Gateway Config Check]

    %% Backend Flow
    subgraph Backend_Workflow [Backend CI]
        Backend --> Matrix[Matrix Strategy: <br/>Auth, Delivery, Menu, Order]
        Matrix --> DockerLogin[Login to Docker Hub]
        DockerLogin --> DockerBuild[Docker Build & Push]
        DockerBuild --> DockerHub[(Docker Hub)]
    end

    %% Android Clients Flow
    subgraph Mobile_Workflow [Android CI]
        AndroidClient --> SetupJavaC[Setup JDK 17]
        AndroidStaff --> SetupJavaS[Setup JDK 17]
        
        SetupJavaC --> BuildAPK[Run Unit Tests & <br/>Build APK]
        SetupJavaS --> BuildAPK
        
        BuildAPK --> Artifacts[Upload Artifacts]
        Artifacts --> Telegram[Send APK to Telegram Bot]
        Telegram --> Devs((Telegram Users))
    end

    %% Gateway Flow
    subgraph Gateway_Workflow [Gateway Check]
        Gateway --> NginxTest[Docker Run: nginx -t]
        NginxTest --> Success{Config OK?}
    end

    %% Стилізація
    style Start fill:#f9f,stroke:#333,stroke-width:2px
    style DockerHub fill:#0db7ed,stroke:#333,color:#fff
    style Telegram fill:#0088cc,stroke:#333,color:#fff
    style Backend_Workflow fill:#f5f5f5,stroke:#666
    style Mobile_Workflow fill:#f5f5f5,stroke:#666

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
