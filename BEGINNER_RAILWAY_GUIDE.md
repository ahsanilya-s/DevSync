# Complete Beginner's Guide: Deploy Backend to Railway

## 📋 What You'll Need
- A GitHub account (free)
- A Railway account (free)
- Your project code (already ready!)

---

## STEP 1: Push Code to GitHub

### 1.1 Create GitHub Account (if you don't have one)
1. Go to https://github.com
2. Click "Sign up"
3. Follow the registration steps
4. Verify your email

### 1.2 Create a New Repository
1. Log in to GitHub
2. Click the **"+"** icon (top right corner)
3. Select **"New repository"**
4. Fill in:
   - **Repository name**: `devsync-backend` (or any name you like)
   - **Description**: "DevSync FYP Project Backend"
   - **Visibility**: Choose "Public" or "Private"
   - **DO NOT** check "Add a README file"
5. Click **"Create repository"**

### 1.3 Push Your Code to GitHub

#### Option A: Using Git Command Line (Recommended)

**Open Command Prompt in your project folder:**
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy"
```

**Initialize Git and push:**
```cmd
git init
git add .
git commit -m "Initial commit - Ready for Railway deployment"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/devsync-backend.git
git push -u origin main
```

**Replace `YOUR_USERNAME` with your actual GitHub username!**

#### Option B: Using GitHub Desktop (Easier for Beginners)

1. Download GitHub Desktop: https://desktop.github.com
2. Install and sign in with your GitHub account
3. Click **"Add"** → **"Add Existing Repository"**
4. Browse to: `d:\temp\devsync - stable - version 2.1 - Copy`
5. Click **"Publish repository"**
6. Choose public/private and click **"Publish Repository"**

### 1.4 Verify Upload
1. Go to your GitHub repository page
2. You should see all your files (pom.xml, src folder, etc.)

---

## STEP 2: Create Railway Account & Project

### 2.1 Sign Up for Railway
1. Go to https://railway.app
2. Click **"Login"**
3. Choose **"Login with GitHub"**
4. Authorize Railway to access your GitHub account
5. Complete the registration

### 2.2 Create New Project
1. On Railway dashboard, click **"New Project"**
2. Select **"Deploy from GitHub repo"**
3. Click **"Configure GitHub App"**
4. Select your repository: `devsync-backend`
5. Click **"Deploy Now"**

### 2.3 Wait for Initial Deployment
- Railway will start building your app
- You'll see logs appearing (this takes 2-5 minutes)
- **DON'T WORRY if it fails** - we need to add the database first!

---

## STEP 3: Add MySQL Database

### 3.1 Add Database to Your Project
1. In your Railway project, click **"New"** button (top right)
2. Select **"Database"**
3. Choose **"Add MySQL"**
4. Wait 10-20 seconds for MySQL to provision

### 3.2 Link Database to Your App
Railway automatically creates these environment variables:
- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLDATABASE`
- `MYSQLUSER`
- `MYSQLPASSWORD`

**You don't need to do anything - it's automatic!**

### 3.3 Verify Database Connection
1. Click on your **MySQL service** (in Railway dashboard)
2. Go to **"Variables"** tab
3. You should see all the MySQL variables listed

---

## STEP 4: Configure Your Backend Service

### 4.1 Generate Public Domain
1. Click on your **backend service** (not MySQL)
2. Go to **"Settings"** tab
3. Scroll down to **"Networking"** section
4. Click **"Generate Domain"**
5. Railway will create a URL like: `https://devsync-production-xxxx.up.railway.app`
6. **COPY THIS URL** - you'll need it for frontend!

### 4.2 Verify Environment Variables
1. Still in your backend service, click **"Variables"** tab
2. You should see:
   - `MYSQLHOST` = (some value)
   - `MYSQLPORT` = 3306
   - `MYSQLDATABASE` = railway
   - `MYSQLUSER` = root
   - `MYSQLPASSWORD` = (some random password)

**If you see these, you're good!**

---

## STEP 5: (Optional) Add Volume for File Uploads

### 5.1 Create Volume
1. In your **backend service**, go to **"Settings"** tab
2. Scroll to **"Volumes"** section
3. Click **"New Volume"**
4. Set **Mount Path**: `/data`
5. Click **"Add"**

### 5.2 Add Environment Variable
1. Go to **"Variables"** tab
2. Click **"New Variable"**
3. Add:
   - **Variable Name**: `RAILWAY_VOLUME_MOUNT_PATH`
   - **Value**: `/data`
4. Click **"Add"**

---

## STEP 6: Deploy & Verify

### 6.1 Trigger Deployment
1. Go to **"Deployments"** tab
2. Click **"Deploy"** (or it may auto-deploy)
3. Watch the build logs

### 6.2 Check Build Logs
You should see:
```
[INFO] Building devsync 0.0.1-SNAPSHOT
[INFO] BUILD SUCCESS
Starting application...
Started DevsyncApplication in X.XXX seconds
```

### 6.3 Test Your Backend
1. Copy your Railway domain (from Step 4.1)
2. Open browser and go to: `https://your-domain.up.railway.app/api/auth/test`
3. If you see a response (even an error is OK), your backend is running!

---

## 🎉 SUCCESS CHECKLIST

- ✅ Code pushed to GitHub
- ✅ Railway project created
- ✅ MySQL database added and connected
- ✅ Public domain generated
- ✅ Backend is deployed and running
- ✅ (Optional) Volume added for uploads

---

## 🔧 Troubleshooting

### Problem: Build Fails
**Solution:**
1. Check **"Deployments"** → Click failed deployment → **"View Logs"**
2. Look for errors mentioning Java version or Maven
3. Verify your `pom.xml` has Java 17 (not 21)

### Problem: "Application failed to respond"
**Solution:**
1. Check if MySQL service is running (green dot)
2. Go to **"Variables"** tab and verify all MySQL variables exist
3. Redeploy: **"Deployments"** → **"Redeploy"**

### Problem: Can't access the URL
**Solution:**
1. Make sure you generated a domain in Settings → Networking
2. Wait 2-3 minutes after deployment completes
3. Try accessing: `https://your-domain.up.railway.app` (without /api)

### Problem: Database connection error
**Solution:**
1. Click on MySQL service
2. Check if it's running (green indicator)
3. Restart MySQL: Click **"..."** → **"Restart"**
4. Redeploy your backend service

---

## 📝 Save These Important URLs

**Your Backend URL:**
```
https://your-domain.up.railway.app
```

**Your Backend API URL (for frontend):**
```
https://your-domain.up.railway.app/api
```

**Railway Dashboard:**
```
https://railway.app/project/YOUR_PROJECT_ID
```

---

## ⏭️ Next Steps

After backend is deployed:
1. Deploy your frontend to Vercel/Netlify
2. Update frontend environment variable with your Railway backend URL
3. Test the complete application

---

## 💡 Tips

- **Free Tier Limits**: 500 hours/month (about 20 days of continuous running)
- **Logs**: Always check logs if something goes wrong
- **Redeploy**: You can redeploy anytime from Deployments tab
- **Environment Variables**: Changes require a redeploy to take effect
- **Database**: Railway MySQL is persistent - your data won't be lost

---

## 🆘 Need Help?

If you get stuck:
1. Check Railway logs first
2. Verify all environment variables are set
3. Make sure MySQL service is running
4. Try redeploying both services

**Common First-Time Issues:**
- Forgot to generate domain → Go to Settings → Networking → Generate Domain
- MySQL not connected → Check if both services are in the same project
- Build fails → Check Java version in pom.xml (should be 17)

---

**You're all set! Your backend is now live on Railway! 🚀**
