# Tech Knife - Environment Variables Specification

## 1. Frontend Repository (`tech-knife-frontend`)
Configured via `.env` file or Vercel Environment Settings. All client variables must be prefixed with `VITE_`.

| Variable Name | Required | Default Value | Description |
|---|---|---|---|
| `VITE_API_URL` | **Yes** | `https://api.techknife.com/v1` | Public HTTPS REST API gateway base URL |
| `VITE_APP_NAME` | **Yes** | `Tech Knife` | Official brand application name |
| `VITE_COMPANY_NAME` | **Yes** | `Tech Knife Corporation` | Registered parent organization name |
| `VITE_SLOGAN` | No | `It's time for technology` | Corporate tagline |
| `VITE_ENVIRONMENT` | **Yes** | `production` | Deployment stage (`development`, `staging`, `production`) |
| `GEMINI_API_KEY` | Optional | - | Server-side Gemini AI key for AI-assisted features |

---

## 2. Backend Repository (`tech-knife-backend`)
Configured via `.env` file or Render Environment Settings.

| Variable Name | Required | Default Value | Description |
|---|---|---|---|
| `PORT` | **Yes** | `5000` or `10000` | HTTP listening port |
| `NODE_ENV` | **Yes** | `production` | Runtime environment (`development`, `production`) |
| `MONGODB_URI` | **Yes** | - | MongoDB Atlas connection URI string |
| `JWT_SECRET` | **Yes** | - | Secret key for signing short-lived JWT access tokens |
| `JWT_REFRESH_SECRET` | **Yes** | - | Secret key for signing long-lived JWT refresh tokens |
| `CLIENT_URL` | **Yes** | `https://www.techknife.com` | Allowed CORS origin URL for frontend requests |
| `CLOUDINARY_CLOUD_NAME` | No | - | Cloudinary cloud account name |
| `CLOUDINARY_API_KEY` | No | - | Cloudinary API Key |
| `CLOUDINARY_API_SECRET` | No | - | Cloudinary API Secret |
| `SMTP_HOST` | No | `smtp.gmail.com` | Outbound email gateway host |
| `SMTP_PORT` | No | `587` | Outbound email gateway port |
| `SMTP_USER` | No | - | SMTP email account username |
| `SMTP_PASS` | No | - | SMTP app password |
