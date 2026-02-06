# Railway Deployment Guide for DevSync

## Prerequisites
- GitHub repository with your code
- Railway account (free tier)
- Vercel/Netlify account for frontend

## Backend Deployment (Railway)

### Step 1: Create New Project
1. Go to Railway.app
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your repository

### Step 2: Add MySQL Database
1. Click "New" → "Database" → "Add MySQL"
2. Railway automatically creates these environment variables:
   - MYSQLHOST
   - MYSQLPORT
   - MYSQLDATABASE
   - MYSQLUSER
   - MYSQLPASSWORD

### Step 3: Configure Service
Your backend will automatically use:
- `nixpacks.toml` for build configuration
- `Procfile` for start command
- `railway.json` for Railway-specific settings

### Step 4: (Optional) Add Volume for File Uploads
1. Go to your service → "Settings" → "Volumes"
2. Click "Add Volume"
3. Mount path: `/data`
4. Size: 1GB (free tier limit)
5. Add environment variable: `RAILWAY_VOLUME_MOUNT_PATH=/data`

### Step 5: Deploy
Railway will automatically build and deploy your app.
Your backend URL will be: `https://your-app.up.railway.app`

## Frontend Deployment (Vercel/Netlify)

### For Vercel:
1. Go to vercel.com
2. Import your repository
3. Set root directory to: `frontend`
4. Add environment variable:
   ```
   VITE_API_URL=https://your-backend.up.railway.app/api
   ```
5. Deploy

### For Netlify:
1. Go to netlify.com
2. Import your repository
3. Set base directory to: `frontend`
4. Build command: `npm run build`
5. Publish directory: `frontend/dist`
6. Add environment variable:
   ```
   VITE_API_URL=https://your-backend.up.railway.app/api
   ```
7. Deploy

## Post-Deployment

### Update CORS (if needed)
If you face CORS issues, update SecurityConfig.java:
```java
configuration.setAllowedOriginPatterns(java.util.List.of(
    "https://your-frontend.vercel.app",
    "http://localhost:5173"
));
```

## Free Tier Limits
- **Railway**: 500 hours/month, 512MB RAM, 1GB storage
- **MySQL**: Included in Railway free tier
- **Vercel**: 100GB bandwidth/month
- **Netlify**: 100GB bandwidth/month

## Monitoring
- Railway Dashboard: Monitor logs, metrics, and deployments
- Check logs: Railway → Your Service → "Deployments" → Click deployment → "View Logs"

## Troubleshooting

### Build Fails
- Check Java version (should be 17)
- Verify `mvn clean package` works locally
- Check Railway build logs

### Database Connection Issues
- Verify MySQL service is running
- Check environment variables are set
- Review connection string in application-prod.properties

### CORS Errors
- Verify frontend URL in SecurityConfig
- Check browser console for specific CORS errors
- Ensure credentials are allowed

### File Upload Issues
- Verify volume is mounted at `/data`
- Check `RAILWAY_VOLUME_MOUNT_PATH` environment variable
- Review FileStorageService.java logs
