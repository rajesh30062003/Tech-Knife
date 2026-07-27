# Tech Knife - Production Deployment Manual

## Architecture Overview
The **Tech Knife** platform is structured into two completely independent repositories:
1. **Frontend Repository (`tech-knife-frontend`)**: Deployed on **Vercel**
2. **Backend Repository (`tech-knife-backend`)**: Deployed on **Render**
3. **Database Layer**: **MongoDB Atlas** (Cloud Managed Cluster)

```
[ User Browser / PWA ] 
          │
          ▼
   [ Vercel CDN ] (tech-knife-frontend)
          │
          ▼ API Requests (HTTPS / CORS Secured)
  [ Render Service ] (tech-knife-backend)
          │
          ▼ Mongoose Wire Protocol
 [ MongoDB Atlas Cluster ]
```

---

## 1. MongoDB Atlas Configuration
1. Log into [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
2. Create a dedicated Database Cluster (M0 Free or M10+ Production Cluster).
3. Under **Database Access**, create a database user (e.g., `techknife_db_user`) with read-write access to the `techknife` database.
4. Under **Network Access**, whitelist Render server IP addresses or add `0.0.0.0/0` (secured with strong password & TLS).
5. Copy the connection string:
   `mongodb+srv://<username>:<password>@cluster0.mongodb.net/techknife?retryWrites=true&w=majority`

---

## 2. Backend Deployment on Render (`tech-knife-backend`)
1. Connect your GitHub repository `tech-knife-backend` to [Render](https://render.com).
2. Create a new **Web Service**.
3. Render automatically detects `render.yaml` or set parameters manually:
   - **Environment**: Node
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npm start`
   - **Health Check Path**: `/api/v1/health`
4. Add Environment Variables in the Render Dashboard:
   - `PORT`: `10000`
   - `NODE_ENV`: `production`
   - `MONGODB_URI`: `<your_mongodb_atlas_connection_string>`
   - `JWT_SECRET`: `<secure_random_string_32_chars>`
   - `JWT_REFRESH_SECRET`: `<secure_random_string_32_chars>`
   - `CLIENT_URL`: `https://www.techknife.com`
   - `CLOUDINARY_CLOUD_NAME`: `<your_cloudinary_cloud_name>`
   - `CLOUDINARY_API_KEY`: `<your_cloudinary_key>`
   - `CLOUDINARY_API_SECRET`: `<your_cloudinary_secret>`
   - `SMTP_HOST`: `smtp.gmail.com`
   - `SMTP_PORT`: `587`
   - `SMTP_USER`: `no-reply@techknife.com`
   - `SMTP_PASS`: `<smtp_app_password>`
5. Click **Deploy Web Service**.

---

## 3. Frontend Deployment on Vercel (`tech-knife-frontend`)
1. Import `tech-knife-frontend` repository into [Vercel](https://vercel.com).
2. Vercel automatically selects Vite settings:
   - **Framework Preset**: Vite
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
3. Configure Environment Variables in Vercel Project Settings:
   - `VITE_API_URL`: `https://tech-knife-backend.onrender.com/api/v1` (or custom backend domain)
   - `VITE_APP_NAME`: `Tech Knife`
   - `VITE_COMPANY_NAME`: `Tech Knife Corporation`
   - `VITE_ENVIRONMENT`: `production`
4. Deploy the project. Vercel handles SSL certificates, custom domain routing, and global CDN caching.

---

## 4. Verification & Health Monitoring
- **Backend Health Check**: `GET https://<your-render-backend-url>/api/v1/health` should return `{"status": "ok", "uptime": ...}`.
- **Frontend SPA Check**: Access `https://<your-vercel-domain>` and verify single-page application routing, logo display, PWA install prompt, and login portal access.
