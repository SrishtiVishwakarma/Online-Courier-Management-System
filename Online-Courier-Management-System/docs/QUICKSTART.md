# OCMS Quick Start Guide

Get the Online Courier Management System running in 5 minutes.

## Prerequisites Check

```bash
# Check Java version (need 21+)
java -version

# Check Maven
mvn -version

# Check MySQL is running
mysql -u root -p
# Enter password: 1111
```

## Step 1: Database Setup (2 minutes)

Open MySQL and execute:

```bash
cd d:\projects\couirer_task\online_courier\src\main\resources\sql
mysql -u root -p < schema.sql
mysql -u root -p < sample_data.sql
```

Or manually:
```sql
SOURCE d:/projects/couirer_task/online_courier/src/main/resources/sql/schema.sql;
SOURCE d:/projects/couirer_task/online_courier/src/main/resources/sql/sample_data.sql;
```

**Verify database created:**
```sql
SHOW DATABASES;  -- Should see 'ocms'
USE ocms;
SHOW TABLES;     -- Should see: users, staff_requests, couriers, courier_logs
SELECT * FROM users;  -- Should see 5 test users
```

## Step 2: Start Backend (1 minute)

```bash
cd d:\projects\couirer_task\online_courier
mvn spring-boot:run
```

**Wait for:** `Started OnlineCourierApplication in X seconds`

Backend running at: **http://localhost:8080**

**Test backend:**
```bash
curl http://localhost:8080/api/auth/login
# Should return 400 (missing body) - backend is up!
```

## Step 3: Start Frontend (1 minute)

Open NEW terminal:

```bash
cd d:\projects\couirer_task
python -m http.server 5500
```

Frontend running at: **http://localhost:5500/frontend/index.html**

**IMPORTANT**: You MUST include `/frontend/` in the URL. Don't just go to `http://localhost:5500/`

## Step 4: Test the Application (1 minute)

### Test 1: User Login & Create Courier
1. Open browser: http://localhost:5500/frontend/login.html
2. Login: `alice@ocms.com` / `Admin@123`
3. Click "Send Courier"
4. Fill form (any values, select Mumbai → Delhi cities)
5. Submit → Get courier ID
6. Click "Download Receipt (PDF)" → PDF should download

### Test 2: Staff Pickup (City Enforcement)
1. Logout → Login: `staff.mumbai@ocms.com` / `Admin@123`
2. Click "Pickups"
3. Should see couriers from Mumbai only
4. Click "Mark as Picked Up" → Confirm
5. Logout → Login: `staff.delhi@ocms.com` / `Admin@123`
6. Click "Pickups" → Should NOT see Mumbai courier (city restriction works!)

### Test 3: Admin Dashboard
1. Logout → Login: `admin@ocms.com` / `Admin@123`
2. See dashboard with courier counts
3. Click "Track Courier by ID"
4. Enter courier ID from Test 1
5. See full history with status changes

## Test Accounts

| Role | Email | Password | City |
|------|-------|----------|------|
| Admin | admin@ocms.com | Admin@123 | - |
| Staff | staff.mumbai@ocms.com | Admin@123 | Mumbai |
| Staff | staff.delhi@ocms.com | Admin@123 | Delhi |
| User | alice@ocms.com | Admin@123 | - |
| User | bob@ocms.com | Admin@123 | - |

## Common Issues

### Backend won't start
**Error:** `Could not connect to MySQL`
- Check MySQL is running: `mysql -u root -p`
- Check password in `application.properties` matches your MySQL root password

### Frontend CORS error
**Error:** `CORS policy blocked`
- Verify backend is running on port 8080
- Check `application.properties` has correct CORS origins:
  ```properties
  spring.web.cors.allowed-origins=http://localhost:5500
  ```
- Restart backend after changing CORS

### Can't download PDF
**Error:** PDF download fails
- Verify OpenPDF dependency in `pom.xml`
- Check backend logs for errors
- Try direct URL: `http://localhost:8080/api/couriers/OCMS-20251102-00001/receipt`

### JWT token expired
**Error:** "Unauthorized" after some time
- Token expires after 120 minutes
- Simply logout and login again
- Or clear localStorage: F12 → Console → `localStorage.clear()`

## Quick API Test with curl

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@ocms.com","password":"Admin@123"}'

# Copy the "token" from response, then:

# Create courier
curl -X POST http://localhost:8080/api/couriers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "senderName":"Alice Test",
    "senderAddress":"123 Test St",
    "senderCity":"Mumbai",
    "receiverName":"Bob Test",
    "receiverAddress":"456 Test Ave",
    "receiverCity":"New Delhi",
    "weight":1.5,
    "notes":"Test courier"
  }'

# Get courier details
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8080/api/couriers/OCMS-20251102-00001
```

## Project URLs

- **Frontend Home**: http://localhost:5500/frontend/index.html
- **Backend API**: http://localhost:8080/api
- **H2 Console** (if enabled): http://localhost:8080/h2-console

## Next Steps

1. **Explore the UI**: Try all three roles (USER, STAFF, ADMIN)
2. **Test Staff Request Flow**: 
   - Visit http://localhost:5500/frontend/staff-request.html
   - Submit request
   - Login as admin → approve request
   - Login with new staff account (password: `Staff@123`)
3. **Test City Enforcement**:
   - Create courier from Mumbai
   - Try to pickup with Delhi staff → Should fail
4. **Read API Docs**: See `API_DOCUMENTATION.md`
5. **Customize**: Edit frontend styling in `frontend/css/styles.css`

## Stop Services

```bash
# Stop backend: Ctrl+C in backend terminal
# Stop frontend: Ctrl+C in frontend terminal
```

## Reset Database

```bash
# Drop and recreate
mysql -u root -p -e "DROP DATABASE ocms;"
mysql -u root -p < online_courier/src/main/resources/sql/schema.sql
mysql -u root -p < online_courier/src/main/resources/sql/sample_data.sql
```

## Development Tips

### Watch backend logs
Backend terminal shows all API requests and SQL queries.

### Browser DevTools
- F12 → Network tab: See all API calls
- F12 → Console: See JavaScript errors
- F12 → Application → Local Storage: See stored JWT token

### Hot Reload
- **Backend**: Restart with `mvn spring-boot:run` after code changes
- **Frontend**: Just refresh browser (no rebuild needed)

## Architecture Overview

```
┌─────────────┐         HTTP          ┌──────────────┐
│   Browser   │ ◄──────────────────► │   Frontend   │
│  (Client)   │    HTML/CSS/JS       │  localhost   │
└─────────────┘                       │    :5500     │
                                      └──────────────┘
                                            │
                                       fetch() API
                                            │
                                            ▼
                                      ┌──────────────┐
                                      │   Backend    │
                                      │  Spring Boot │
                                      │   :8080      │
                                      └──────────────┘
                                            │
                                         JDBC
                                            │
                                            ▼
                                      ┌──────────────┐
                                      │    MySQL     │
                                      │     ocms     │
                                      │    :3306     │
                                      └──────────────┘
```

## File Structure Reference

```
courier_task/
├── README.md                    ← Full documentation
├── QUICKSTART.md               ← This file
├── API_DOCUMENTATION.md        ← API reference
├── online_courier/             ← Backend (Spring Boot)
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../
│       │   ├── controller/     ← REST endpoints
│       │   ├── service/        ← Business logic
│       │   ├── entity/         ← Database models
│       │   └── security/       ← JWT & auth
│       └── resources/
│           ├── application.properties
│           └── sql/
│               ├── schema.sql  ← CREATE DATABASE & TABLES
│               └── sample_data.sql ← Test accounts
└── frontend/                   ← Frontend (HTML/JS/CSS)
    ├── index.html              ← Landing page
    ├── login.html              ← Login form
    ├── userhome.html           ← USER dashboard
    ├── staffhome.html          ← STAFF dashboard
    ├── adminhome.html          ← ADMIN dashboard
    ├── js/
    │   ├── api.js              ← Fetch wrapper + auth
    │   ├── menu.js             ← Dynamic navigation
    │   └── cities.js           ← Indian cities list
    └── css/
        └── styles.css          ← All styling
```

## Success Checklist

- [ ] MySQL database `ocms` created with tables
- [ ] Backend started without errors
- [ ] Frontend accessible at http://localhost:5500/frontend/
- [ ] Can login as admin/staff/user
- [ ] Can create courier and download PDF receipt
- [ ] Staff sees only couriers from their city
- [ ] Admin can track any courier by ID

---

**You're all set! Start exploring OCMS.** 🚀

For detailed information, see **README.md** and **API_DOCUMENTATION.md**.
