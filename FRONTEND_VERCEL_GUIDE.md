# Complete Beginner's Guide: Deploy Frontend to Vercel

## 📋 What You'll Need
- Your backend URL from Railway (from previous deployment)
- A Vercel account (free)
- Your frontend code (already in the `frontend` folder)

---

## STEP 1: Prepare Frontend for Deployment

### 1.1 Get Your Backend URL
Before starting, you need your Railway backend URL:
1. Go to your Railway project
2. Click on your backend service
3. Copy the domain (e.g., `https://devsync-production-xxxx.up.railway.app`)
4. **SAVE THIS URL** - you'll need it!

### 1.2 Update Frontend Configuration
Your frontend is already configured! The file `frontend/src/config.js` will automatically use the environment variable we'll set in Vercel.

---

## STEP 2: Push Frontend to GitHub (Separate Repo)

### 2.1 Create New Repository for Frontend
1. Go to https://github.com
2. Click **"+"** icon → **"New repository"**
3. Fill in:
   - **Repository name**: `devsync-frontend`
   - **Description**: "DevSync FYP Project Frontend"
   - **Visibility**: Public or Private
   - **DO NOT** check "Add a README file"
4. Click **"Create repository"**

### 2.2 Push Frontend Code to GitHub

**Open Command Prompt in your frontend folder:**
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy\frontend"
```

**Initialize Git and push:**
```cmd
git init
git add .
git commit -m "Initial commit - Frontend ready for Vercel"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/devsync-frontend.git
git push -u origin main
```

**Replace `YOUR_USERNAME` with your GitHub username!**

### 2.3 Verify Upload
1. Go to your GitHub repository: `https://github.com/YOUR_USERNAME/devsync-frontend`
2. You should see: `package.json`, `src/`, `public/`, `vite.config.js`, etc.

---

## STEP 3: Create Vercel Account & Deploy

### 3.1 Sign Up for Vercel
1. Go to https://vercel.com
2. Click **"Sign Up"**
3. Choose **"Continue with GitHub"**
4. Authorize Vercel to access your GitHub account
5. Complete the registration

### 3.2 Import Your Frontend Project
1. On Vercel dashboard, click **"Add New..."** → **"Project"**
2. You'll see a list of your GitHub repositories
3. Find **"devsync-frontend"**
4. Click **"Import"**

### 3.3 Configure Project Settings

Vercel will show a configuration screen:

**Framework Preset:**
- Should auto-detect as **"Vite"** ✅
- If not, select **"Vite"** from dropdown

**Root Directory:**
- Leave as **"./"** (root)
- Do NOT change this

**Build and Output Settings:**
- **Build Command**: `npm run build` (auto-filled)
- **Output Directory**: `dist` (auto-filled)
- **Install Command**: `npm install` (auto-filled)

**Leave these as default - they're correct!**

### 3.4 Add Environment Variable

This is the MOST IMPORTANT step!

1. Scroll down to **"Environment Variables"** section
2. Click **"Add"** or expand the section
3. Add this variable:

```
Name: VITE_API_URL
Value: https://your-railway-backend-url.up.railway.app/api
```

**IMPORTANT:** 
- Replace `your-railway-backend-url.up.railway.app` with your ACTUAL Railway URL
- Make sure to add `/api` at the end!
- Example: `https://devsync-production-a1b2.up.railway.app/api`

4. Click **"Add"**

### 3.5 Deploy!
1. Click **"Deploy"** button
2. Vercel will start building your frontend
3. Wait 2-3 minutes for deployment to complete
4. You'll see a success screen with confetti! 🎉

---

## STEP 4: Get Your Frontend URL

### 4.1 Copy Your Vercel URL
After successful deployment:
1. You'll see your project dashboard
2. At the top, you'll see your URL: `https://devsync-frontend-xxxx.vercel.app`
3. Click **"Visit"** to open your deployed frontend
4. **SAVE THIS URL** - this is your live application!

### 4.2 Test Your Application
1. Open your Vercel URL in browser
2. Try to:
   - Register a new account
   - Login
   - Upload a project
   - View analysis results

---

## STEP 5: Verify Backend Connection

### 5.1 Check Browser Console
1. Open your frontend in browser
2. Press **F12** to open Developer Tools
3. Go to **"Console"** tab
4. Try to login or register
5. Check for any errors

### 5.2 Common Issues & Fixes

**Issue: CORS Error**
```
Access to fetch at 'https://...' from origin 'https://...' has been blocked by CORS policy
```

**Solution:**
Your backend SecurityConfig already allows all origins, but if you see this:
1. Go to Railway → Your backend service
2. Check if it's running (green indicator)
3. Redeploy backend if needed

**Issue: Network Error / Failed to Fetch**
```
TypeError: Failed to fetch
```

**Solution:**
1. Verify your `VITE_API_URL` in Vercel is correct
2. Make sure it ends with `/api`
3. Check if Railway backend is running
4. Test backend directly: `https://your-backend.up.railway.app/api/auth/test`

**Issue: 404 Not Found on Refresh**
```
404 - Page Not Found (when refreshing any page)
```

**Solution:**
Already fixed! Your `vercel.json` handles this correctly.

---

## STEP 6: Update Environment Variables (If Needed)

### 6.1 Change Backend URL Later
If you need to update the backend URL:

1. Go to Vercel dashboard
2. Click on your **"devsync-frontend"** project
3. Go to **"Settings"** tab
4. Click **"Environment Variables"** in left sidebar
5. Find `VITE_API_URL`
6. Click **"Edit"** (pencil icon)
7. Update the value
8. Click **"Save"**
9. Go to **"Deployments"** tab
10. Click **"..."** on latest deployment → **"Redeploy"**

---

## 🎉 SUCCESS CHECKLIST

- ✅ Frontend code pushed to GitHub
- ✅ Vercel account created
- ✅ Project imported to Vercel
- ✅ Environment variable `VITE_API_URL` set correctly
- ✅ Frontend deployed successfully
- ✅ Application is accessible via Vercel URL
- ✅ Backend connection working

---

## 📝 Save These Important URLs

**Your Frontend URL (Vercel):**
```
https://devsync-frontend-xxxx.vercel.app
```

**Your Backend URL (Railway):**
```
https://devsync-production-xxxx.up.railway.app
```

**Your Backend API (for reference):**
```
https://devsync-production-xxxx.up.railway.app/api
```

**Vercel Dashboard:**
```
https://vercel.com/dashboard
```

---

## 🔧 Troubleshooting

### Problem: Build Fails on Vercel
**Solution:**
1. Check build logs in Vercel
2. Common issues:
   - Missing dependencies → Check `package.json`
   - Node version mismatch → Vercel uses Node 18+ (should work)
3. Try redeploying: Deployments → "..." → "Redeploy"

### Problem: White Screen / Blank Page
**Solution:**
1. Check browser console (F12) for errors
2. Verify `VITE_API_URL` is set correctly in Vercel
3. Make sure backend is running on Railway
4. Check if backend URL is accessible

### Problem: API Calls Failing
**Solution:**
1. Open browser console (F12)
2. Go to "Network" tab
3. Try an action (login/register)
4. Check the failed request:
   - Is the URL correct?
   - Does it point to your Railway backend?
   - Is `/api` included in the URL?

### Problem: Can't Login/Register
**Solution:**
1. Check if backend database is connected
2. Go to Railway → MySQL service → Check if running
3. Go to Railway → Backend service → Check logs
4. Look for database connection errors

---

## 🔄 Making Updates to Your Frontend

### When You Make Code Changes:

**Option 1: Auto-Deploy (Recommended)**
1. Make changes to your code locally
2. Commit and push to GitHub:
   ```cmd
   cd "d:\temp\devsync - stable - version 2.1 - Copy\frontend"
   git add .
   git commit -m "Your update message"
   git push
   ```
3. Vercel automatically detects the push and redeploys!
4. Wait 1-2 minutes for new deployment

**Option 2: Manual Deploy**
1. Go to Vercel dashboard
2. Click your project
3. Go to "Deployments" tab
4. Click "..." on any deployment → "Redeploy"

---

## 💡 Vercel Free Tier Limits

- **Bandwidth**: 100GB/month
- **Builds**: 6,000 minutes/month
- **Deployments**: Unlimited
- **Custom Domain**: 1 free domain
- **Team Members**: 1 (just you)

**This is MORE than enough for your FYP! ✅**

---

## 🎨 Optional: Add Custom Domain

### If You Have a Custom Domain:

1. Go to Vercel → Your Project → **"Settings"**
2. Click **"Domains"** in left sidebar
3. Click **"Add"**
4. Enter your domain (e.g., `devsync.yourdomain.com`)
5. Follow Vercel's instructions to update DNS records
6. Wait for DNS propagation (5-30 minutes)

---

## 🆘 Need Help?

### Quick Checks:
1. ✅ Is backend running on Railway?
2. ✅ Is `VITE_API_URL` set correctly in Vercel?
3. ✅ Does backend URL end with `/api`?
4. ✅ Is MySQL database connected on Railway?
5. ✅ Are there any errors in browser console?

### Still Stuck?
- Check Vercel build logs
- Check Railway backend logs
- Test backend directly in browser
- Verify environment variables

---

## 🎯 Testing Your Complete Application

### Test Checklist:

1. **Homepage Loads** ✅
   - Open your Vercel URL
   - Should see landing page

2. **Registration Works** ✅
   - Click "Sign Up"
   - Create new account
   - Should redirect to dashboard

3. **Login Works** ✅
   - Logout
   - Login with created account
   - Should access dashboard

4. **Upload Project** ✅
   - Upload a ZIP file
   - Should see analysis results

5. **View Reports** ✅
   - Check code analysis
   - View charts and metrics

6. **All Pages Work** ✅
   - Navigate between pages
   - Refresh pages (should not 404)

---

## 🚀 You're Done!

**Your complete DevSync application is now live!**

- ✅ Backend on Railway with MySQL
- ✅ Frontend on Vercel
- ✅ Both connected and working
- ✅ Ready for your FYP defense!

**Share your Vercel URL with your supervisor and examiners! 🎓**

---

## 📊 Monitoring Your Application

### Vercel Analytics (Optional):
1. Go to your project in Vercel
2. Click "Analytics" tab
3. See visitor stats, performance metrics

### Railway Metrics:
1. Go to your backend service in Railway
2. Click "Metrics" tab
3. See CPU, memory, network usage

---

**Congratulations! Your FYP is deployed and ready! 🎉🚀**
