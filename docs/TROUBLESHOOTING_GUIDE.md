# Tech Knife - Operations & Troubleshooting Guide

## 1. Common Issues & Resolution Steps

### Issue 1: CORS Error on API Request (`Access to XMLHttpRequest at '...' from origin '...' has been blocked`)
- **Root Cause**: `CLIENT_URL` environment variable on backend does not match the frontend origin URL.
- **Fix**: Update `CLIENT_URL` in Render environment settings to match Vercel production domain (e.g., `https://www.techknife.com`) and restart the service.

### Issue 2: MongoDB Connection Timeout (`MongooseServerSelectionError`)
- **Root Cause**: Database credentials incorrect or IP address not whitelisted in MongoDB Atlas.
- **Fix**: Verify `MONGODB_URI` connection string username and password. Ensure Network Access in MongoDB Atlas includes `0.0.0.0/0` or Render's outbound IP ranges.

### Issue 3: JWT Authentication Fails (`UNAUTHORIZED_ACCESS` or `jwt expired`)
- **Root Cause**: `JWT_SECRET` key mismatch between backend restarts or expired token payload.
- **Fix**: Verify `JWT_SECRET` in environment variables. Have user sign out and re-authenticate to receive fresh JWT bearer token.

### Issue 4: Frontend Route Returns 404 on Browser Refresh
- **Root Cause**: Missing SPA route rewrite configuration on web host.
- **Fix**: Ensure `vercel.json` contains `{"rewrites": [{"source": "/(.*)", "destination": "/index.html"}]}` or Nginx `try_files $uri $uri/ /index.html` directive is active.
