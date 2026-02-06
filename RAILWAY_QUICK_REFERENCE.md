# Railway Deployment - Quick Reference

## 🚀 Quick Commands

### Push to GitHub (First Time)
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy"
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/devsync-backend.git
git push -u origin main
```

### Push Updates (After First Time)
```cmd
git add .
git commit -m "Your update message"
git push
```

---

## 📋 Railway Setup Checklist

### Step 1: GitHub
- [ ] Create GitHub account
- [ ] Create new repository
- [ ] Push code to GitHub

### Step 2: Railway
- [ ] Sign up at railway.app
- [ ] Create new project from GitHub repo
- [ ] Wait for initial build

### Step 3: Database
- [ ] Click "New" → "Database" → "Add MySQL"
- [ ] Wait for MySQL to start (green indicator)
- [ ] Verify environment variables are auto-created

### Step 4: Domain
- [ ] Go to backend service → Settings
- [ ] Scroll to "Networking"
- [ ] Click "Generate Domain"
- [ ] Copy the URL (you'll need it!)

### Step 5: (Optional) Volume
- [ ] Settings → Volumes → New Volume
- [ ] Mount path: `/data`
- [ ] Add variable: `RAILWAY_VOLUME_MOUNT_PATH=/data`

### Step 6: Deploy
- [ ] Check Deployments tab
- [ ] Wait for "Success" status
- [ ] Test your URL in browser

---

## 🔗 Important URLs

**Railway Dashboard:**
https://railway.app

**Your Backend URL (after deployment):**
https://your-app-name.up.railway.app

**Your API Endpoint (for frontend):**
https://your-app-name.up.railway.app/api

---

## ⚙️ Environment Variables (Auto-Created)

Railway automatically creates these when you add MySQL:
- `MYSQLHOST` - Database host
- `MYSQLPORT` - Database port (3306)
- `MYSQLDATABASE` - Database name (railway)
- `MYSQLUSER` - Database username (root)
- `MYSQLPASSWORD` - Database password (auto-generated)

**You don't need to set these manually!**

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Build fails | Check logs, verify Java 17 in pom.xml |
| Can't access URL | Generate domain in Settings → Networking |
| Database error | Check MySQL is running (green dot) |
| 502 Bad Gateway | Wait 2-3 minutes, backend is starting |
| Changes not showing | Redeploy from Deployments tab |

---

## 📊 Free Tier Limits

- **Execution**: 500 hours/month (~20 days)
- **RAM**: 512MB
- **Storage**: 1GB (with volume)
- **MySQL**: Included free
- **Bandwidth**: 100GB/month

---

## 🔄 Common Tasks

### View Logs
1. Go to your service
2. Click "Deployments"
3. Click on latest deployment
4. Click "View Logs"

### Redeploy
1. Go to "Deployments" tab
2. Click "..." on latest deployment
3. Click "Redeploy"

### Restart Service
1. Go to service
2. Click "..." (top right)
3. Click "Restart"

### Delete Service
1. Go to Settings
2. Scroll to bottom
3. Click "Delete Service"

---

## ✅ Verification Steps

After deployment, verify:

1. **Service is running**: Green indicator on service card
2. **MySQL is running**: Green indicator on MySQL card
3. **Domain exists**: Settings → Networking shows a URL
4. **Variables set**: Variables tab shows MySQL credentials
5. **Logs are clean**: No errors in deployment logs

---

## 📞 Support

- Railway Docs: https://docs.railway.app
- Railway Discord: https://discord.gg/railway
- GitHub Issues: Check your repo's issues tab

---

**Save this file for quick reference during deployment!**
