# Tech Knife - Installation & Environment Setup Guide

## Prerequisites
- **Node.js**: v18.0.0 or higher
- **npm**: v9.0.0 or higher

## Setup Steps

### 1. Clone & Install Dependencies
```bash
git clone https://github.com/techknife/techknife-frontend.git
cd techknife-frontend
npm install
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Define environment variables:
```env
VITE_API_BASE_URL=https://api.techknife.com/v1
VITE_APP_ENV=development
GEMINI_API_KEY=your_gemini_key_here
```

### 3. Run Development Server
```bash
npm run dev
```
Access the application on `http://localhost:3000`.

### 4. Type Checks & Production Build
```bash
# Run TypeScript compilation check
npm run lint

# Build production bundle
npm run build
```
Outputs static assets into `dist/`.
