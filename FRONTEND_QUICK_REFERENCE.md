# Frontend Deployment - Quick Reference

## 🚀 Quick Deploy Steps

### 1. Push Frontend to GitHub
```cmd
cd "d:\temp\devsync - stable - version 2.1 - Copy\frontend"
git init
git add .
git commit -m "Frontend ready"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/devsync-frontend.git
git push -u origin main
```

### 2. Deploy to Vercel
1. Go to https://vercel.com
2. Sign up with GitHub
3. Click "Add New..." → "Project"
4. Import "devsync-frontend"
5. Add environment variable:
   - Name: `VITE_API_URL`
   - Value: `https://your-railway-url.up.railway.app/api`
6. Click "Deploy"

### 3. Done!
Your frontend will be live at: `https://devsync-frontend-xxxx.vercel.app`

---

## ⚙️ Environment Variable

**CRITICAL:** Set this in Vercel before deploying!

```
VITE_API_URL=https://your-railway-backend.up.railway.app/api
```

**Don't forget the `/api` at the end!**

---

## 🔧 Vercel Configuration

These are auto-detected (no changes needed):

| Setting | Value |
|---------|-------|
| Framework | Vite |
| Build Command | `npm run build` |
| Output Directory | `dist` |
| Install Command | `npm install` |
| Node Version | 18.x (auto) |

---

## 📝 Important URLs

**Vercel Dashboard:**
https://vercel.com/dashboard

**Your Frontend (after deploy):**
https://your-project.vercel.app

**Your Backend API:**
https://your-railway-backend.up.railway.app/api

---

## 🔄 Update Frontend

### Auto-Deploy (Recommended):
```cmd
cd frontend
git add .
git commit -m "Update message"
git push
```
Vercel auto-deploys on push!

### Manual Redeploy:
1. Vercel Dashboard → Your Project
2. Deployments → "..." → "Redeploy"

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Build fails | Check build logs in Vercel |
| White screen | Check browser console (F12) |
| API errors | Verify `VITE_API_URL` is correct |
| CORS errors | Check Railway backend is running |
| 404 on refresh | Already fixed with vercel.json |

---

## ✅ Testing Checklist

After deployment, test:
- [ ] Homepage loads
- [ ] Can register new account
- [ ] Can login
- [ ] Can upload project
- [ ] Can view analysis
- [ ] All pages work
- [ ] No console errors

---

## 💡 Pro Tips

- Vercel auto-deploys on every GitHub push
- Free tier: 100GB bandwidth/month
- Build time: ~1-2 minutes
- Custom domains supported (free)
- Automatic HTTPS enabled
- Global CDN included

---

## 🆘 Common Issues

**Issue: "VITE_API_URL is not defined"**
- Go to Vercel → Settings → Environment Variables
- Add `VITE_API_URL` with your Railway URL
- Redeploy

**Issue: "Failed to fetch"**
- Check Railway backend is running
- Verify URL ends with `/api`
- Test backend: `https://your-backend.up.railway.app/api`

**Issue: "CORS policy error"**
- Backend SecurityConfig already allows all origins
- If still occurs, redeploy Railway backend

---

**Save this for quick reference during deployment!**
