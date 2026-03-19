# AI_Finance_Coach
Ai Finance coach is an app which lets you analyse your spending and income &amp; help you make decisions.



<div align="center">

<img src="assets/ic_launcher.png" width="100" height="100"
style="border-radius: 22px"/>

# FinSense — AI Finance Coach

### Your personal AI-powered financial companion

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini%20AI-8E75B2?style=for-the-badge&logo=google&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-00C896?style=for-the-badge)

[Download APK](#download) ·
[Screenshots](#screenshots) ·
[Architecture](#architecture) ·
[Setup](#setup)

</div>

---

## What Is FinSense?

FinSense is not just another expense tracker.

It is an AI-powered personal finance coach that understands
your actual spending habits, answers questions about your
money using your real transaction data, and gives you
genuinely personalised financial advice — not generic tips.

Built entirely with modern Android development practices:
zero XML layouts, full Jetpack Compose UI,
Clean Architecture, and a RAG pipeline powered by Gemini AI.

---

## Screenshots

<div align="center">

| Onboarding | Dashboard | Transactions |
|:---:|:---:|:---:|
| <img src="assets/screenshots/onboarding.png" width="200"/> | <img src="assets/screenshots/dashboard.png" width="200"/> | <img src="assets/screenshots/transactions.png" width="200"/> |

| Add Transaction | Budget | AI Coach |
|:---:|:---:|:---:|
| <img src="assets/screenshots/add_transaction.png" width="200"/> | <img src="assets/screenshots/budget.png" width="200"/> | <img src="assets/screenshots/ai_chat.png" width="200"/> |

| AI Insights | Monthly Report | Profile |
|:---:|:---:|:---:|
| <img src="assets/screenshots/insights.png" width="200"/> | <img src="assets/screenshots/report.png" width="200"/> | <img src="assets/screenshots/profile.png" width="200"/> |

</div>

---

## Download

<div align="center">

### [⬇️ Download Latest APK (v1.0.0)](../../releases/latest)

Minimum Android version: Android 8.0 (API 26)

</div>

---

## Key Features

### 💰 Finance Tracking
- Track income and expenses across Cash, UPI, Bank, Credit Card
- 14 smart spending categories with custom emoji
- Recurring transaction automation
- Full transaction history with search and filters
- Month-by-month navigation

### 📊 Smart Budgeting
- Set monthly spending limits per category
- Real-time budget health score (0–100)
- Visual progress tracking with status indicators
- AI-suggested budget limits based on spending history
- Alerts before you overspend — not after

### 🤖 AI Coach (RAG-Powered)
- Ask anything about your finances in plain English
- AI answers grounded in YOUR actual transaction data
- Streaming responses token by token
- Proactive AI insights about spending patterns
- AI-generated monthly financial reports
- Chat history persists across sessions

### 🔐 Security & Privacy
- Google Sign-In via Firebase Auth
- All transaction data stored locally on device first
- Optional cloud sync via Firebase Firestore
- No third-party data sharing

---

## How The AI Works
```
┌─────────────────────────────────────────────────┐
│              RAG Pipeline                        │
│                                                  │
│  User Question                                   │
│       ↓                                          │
│  BuildFinancialContextUseCase                    │
│  → Last 30 transactions from Room DB             │
│  → Active budgets + spending totals              │
│  → Income, savings rate, top categories         │
│       ↓                                          │
│  Structured Financial Context                    │
│  (real numbers, real categories, real dates)     │
│       ↓                                          │
│  Gemini 1.5 Flash API                            │
│  [System Prompt] + [Financial Context]           │
│  + [Chat History] + [User Question]              │
│       ↓                                          │
│  Streamed Response (token by token)              │
│       ↓                                          │
│  Personalised Answer based on YOUR data          │
└─────────────────────────────────────────────────┘
```

**Result:**
- ❌ Generic: "Try to spend less on food"
- ✅ RAG: "You spent ₹8,400 on Zomato last month —
  that's 34% above your food budget of ₹6,200"

---

## Architecture
```
┌────────────────────────────────────────────────┐
│                Presentation Layer               │
│   Jetpack Compose Screens + ViewModels          │
│   StateFlow + collectAsStateWithLifecycle()     │
└──────────────────┬─────────────────────────────┘
                   │
┌──────────────────▼─────────────────────────────┐
│                 Domain Layer                    │
│   Use Cases + Repository Interfaces            │
│   Pure Kotlin — zero Android dependencies      │
└──────────┬───────────────────┬─────────────────┘
           │                   │
┌──────────▼──────┐   ┌────────▼────────────────┐
│   Local Data    │   │     Remote Data          │
│                 │   │                          │
│  Room DB        │   │  Gemini 1.5 Flash API    │
│  (Transactions, │   │  Firebase Auth           │
│   Budgets,      │   │  Firebase Firestore      │
│   AI Messages)  │   │  Firebase Storage        │
└─────────────────┘   └──────────────────────────┘
```

**Pattern:** MVVM + Clean Architecture  
**DI:** Hilt  
**Async:** Kotlin Coroutines + Flow  
**Navigation:** Jetpack Navigation Compose

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (zero XML) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Local DB | Room |
| Vector Store | ObjectBox |
| AI | Gemini 1.5 Flash via Google AI SDK |
| Auth | Firebase Auth (Google Sign-In) |
| Cloud DB | Firebase Firestore |
| Storage | Firebase Storage |
| Charts | Vico (Compose native) |
| Image Loading | Coil |
| Background Jobs | WorkManager |
| Preferences | DataStore |
| Fonts | Sora + DM Sans (Google Fonts) |

---

## Folder Structure
```
app/src/main/java/com/yourname/finsense/
│
├── data/
│   ├── local/          # Room DB, DAOs, Entities
│   ├── remote/         # Gemini API, Firebase sources
│   ├── repository/     # Repository implementations
│   ├── mapper/         # Entity ↔ Domain mappers
│   ├── datastore/      # DataStore preferences
│   └── worker/         # WorkManager workers
│
├── domain/
│   ├── model/          # Pure Kotlin domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # All business logic
│
├── presentation/
│   ├── auth/           # Onboarding, Login, Profile Setup
│   ├── dashboard/      # Home screen
│   ├── transaction/    # List, Add, Detail, Recurring
│   ├── budget/         # Tracking + Setup
│   ├── ai/             # Chat, Insights, Report
│   ├── settings/       # Profile + Preferences
│   ├── components/     # Reusable Compose components
│   ├── navigation/     # NavGraph + Screen routes
│   └── theme/          # Color, Type, Shape, Theme
│
└── di/                 # Hilt modules
```

---

## Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Kotlin 1.9+
- A Google account for Firebase

### Step 1 — Clone the repository
```bash
git clone https://github.com/yourusername/finsense.git
cd finsense
```

### Step 2 — Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named "FinSense"
3. Add an Android app with package name `com.yourname.finsense`
4. Download `google-services.json`
5. Place it in the `app/` directory
6. Enable these Firebase services:
    - Authentication → Google Sign-In
    - Firestore Database
    - Storage

### Step 3 — Gemini API Key
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create a free API key
3. Open `local.properties` in root directory
4. Add:
```
GEMINI_API_KEY=your_api_key_here
```

### Step 4 — Build and Run
```bash
./gradlew assembleDebug
```
Or simply press **Run** in Android Studio.

---

## APK Installation

1. Download the APK from [Releases](../../releases/latest)
2. On your Android device:
    - Go to Settings → Security
    - Enable "Install from Unknown Sources"
3. Open the downloaded APK
4. Follow installation prompts

---

## What I Learned Building This

- **RAG on mobile** is more powerful than expected and completely
  feasible with Gemini's free tier
- **Clean Architecture** paid off significantly as the app grew —
  each layer stayed completely independent
- **Streaming AI responses** dramatically improves perceived
  performance and user experience
- **Jetpack Compose** with proper state management
  (StateFlow + collectAsStateWithLifecycle) is genuinely
  production-ready
- **Hilt** makes dependency injection nearly invisible
  once set up correctly


















## Contributing

Pull requests are welcome. For major changes please
open an issue first to discuss what you would like to change.



<h4 align="center">

<b>Built with ❤️ by Samarth</b>

⭐ Star this repo if you found it useful

</h4>