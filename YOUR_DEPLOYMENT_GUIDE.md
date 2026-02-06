# 🚀 YOUR DEPLOYMENT GUIDE - DevSync FYP

## ✅ STEP 1: Frontend Code - DONE!

Your frontend code is now on GitHub:
**https://github.com/ahsanilya-s/DevSyncFrontend.git**

---

## 🎯 STEP 2: Deploy to Vercel (15 minutes)

### 2.1 Sign Up for Vercel
1. Go to **https://vercel.com**
2. Click **"Sign Up"**
3. Choose **"Continue with GitHub"**
4. Authorize Vercel
5. Complete registration

### 2.2 Import Your Project
1. On Vercel dashboard, click **"Add New..."** → **"Project"**
2. Find **"DevSyncFrontend"** in the list
3. Click **"Import"**

### 2.3 Configure Settings

**Framework Preset:** Vite (should auto-detect)

**Build Settings:** (Leave as default)
- Build Command: `npm run build`
- Output Directory: `dist`
- Install Command: `npm install`

### 2.4 Add Environment Variable ⚠️ CRITICAL!

Scroll to **"Environment Variables"** section:

```
Name:  VITE_API_URL
Value: https://devsync-production-4a6c.up.railway.app/api
```

**IMPORTANT:** Copy this EXACTLY as shown above!

Click **"Add"**

### 2.5 Deploy!
1. Click **"Deploy"** button
2. Wait 2-3 minutes
3. You'll see success screen! 🎉

---

## 📝 YOUR URLS

**Backend (Railway):**
```
https://devsync-production-4a6c.up.railway.app
```

**Backend API:**
```
https://devsync-production-4a6c.up.railway.app/api
```

**Frontend GitHub:**
```
https://github.com/ahsanilya-s/DevSyncFrontend.git
```

**Frontend (Vercel) - After deployment:**
```
https://dev-sync-frontend-xxxx.vercel.app
(You'll get this after deploying)
```

---

## ✅ VERIFICATION CHECKLIST

After Vercel deployment completes:

### 1. Test Backend (Do this first!)
Open in browser:
```
https://devsync-production-4a6c.up.railway.app/api
```

You should see a response (even an error page is OK - it means backend is running!)

### 2. Test Frontend
1. Click "Visit" on Vercel success screen
2. You should see your DevSync landing page
3. Try to register a new account
4. Try to login
5. Try to upload a project

### 3. Check Browser Console
1. Press **F12** on your frontend
2. Go to **"Console"** tab
3. Look for any red errors
4. If you see CORS or network errors, check the troubleshooting section below

---

## 🐛 TROUBLESHOOTING

### Issue: "Failed to fetch" or Network Error

**Check:**
1. Is Railway backend running? (Check Railway dashboard - should have green indicator)
2. Is the environment variable correct in Vercel?
   - Go to Vercel → Your Project → Settings → Environment Variables
   - Verify: `VITE_API_URL = https://devsync-production-4a6c.up.railway.app/api`
3. Test backend directly: Open `https://devsync-production-4a6c.up.railway.app/api` in browser

**Fix:**
- If backend is down: Go to Railway → Redeploy
- If env var is wrong: Update in Vercel → Redeploy

### Issue: CORS Error

**This shouldn't happen** (your SecurityConfig allows all origins), but if it does:

1. Go to Railway → Your backend service
2. Check if it's running
3. Click "Deployments" → "Redeploy"

### Issue: White Screen / Blank Page

**Check:**
1. Browser console (F12) for errors
2. Vercel build logs (Vercel → Deployments → Click deployment → View Logs)
3. Environment variable is set correctly

**Fix:**
- Redeploy: Vercel → Deployments → "..." → "Redeploy"

### Issue: Can't Register/Login

**Check:**
1. Railway → MySQL service → Is it running? (green indicator)
2. Railway → Backend service → Variables tab → Are MySQL variables set?
3. Railway → Backend service → Deployments → Check logs for database errors

**Fix:**
- Restart MySQL: Railway → MySQL service → "..." → "Restart"
- Redeploy backend: Railway → Backend service → "Redeploy"

---

## 🔄 MAKING UPDATES

### Update Frontend Code:
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy\frontend"
git add .
git commit -m "Your update message"
git push
```

Vercel will automatically redeploy! (Wait 1-2 minutes)

### Update Backend Code:
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy"
git add .
git commit -m "Your update message"
git push
```

Railway will automatically redeploy! (Wait 2-3 minutes)

---

## 📊 MONITORING

### Check Backend Status:
- Railway Dashboard: https://railway.app
- Click your project → Backend service
- Check "Metrics" tab for CPU/Memory usage
- Check "Deployments" tab for logs

### Check Frontend Status:
- Vercel Dashboard: https://vercel.com/dashboard
- Click your project
- Check "Deployments" for build status
- Check "Analytics" for visitor stats (optional)

---

## 🎉 SUCCESS CRITERIA

Your deployment is successful when:

- ✅ Backend URL responds: `https://devsync-production-4a6c.up.railway.app/api`
- ✅ Frontend loads without errors
- ✅ Can register new account
- ✅ Can login with created account
- ✅ Can upload ZIP file
- ✅ Can see analysis results
- ✅ No errors in browser console

---

## 💡 IMPORTANT NOTES

1. **Free Tier Limits:**
   - Railway: 500 hours/month (~20 days continuous)
   - Vercel: 100GB bandwidth/month
   - Both are MORE than enough for FYP!

2. **Database:**
   - Your MySQL data is persistent
   - Won't be lost on redeployment
   - Backed up by Railway

3. **Auto-Deploy:**
   - Both Railway and Vercel auto-deploy on git push
   - No manual intervention needed after setup

4. **HTTPS:**
   - Both provide free HTTPS automatically
   - Your app is secure by default

---

## 🆘 NEED HELP?

### Quick Checks (in order):
1. ✅ Railway backend running? (green indicator)
2. ✅ Railway MySQL running? (green indicator)
3. ✅ Vercel frontend deployed? (success status)
4. ✅ Environment variable set in Vercel?
5. ✅ Backend URL accessible in browser?

### Still Stuck?
- Check Railway logs: Backend service → Deployments → View Logs
- Check Vercel logs: Your project → Deployments → View Logs
- Check browser console: F12 → Console tab

---

## 📞 SUPPORT RESOURCES

- Railway Docs: https://docs.railway.app
- Vercel Docs: https://vercel.com/docs
- Railway Discord: https://discord.gg/railway

---

**Follow this guide step-by-step and your FYP will be live in 15 minutes! 🚀**

**Good luck with your defense! 🎓**
