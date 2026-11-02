# OCMS - Online Courier Management System

Complete full-stack courier management application with role-based access control, PDF receipts, and city-based staff management.


## 📌 Features

### 🔐 Authentication & Security
- JWT-based login & role authorization  
- BCrypt password hashing  
- Role-based navigation (USER / STAFF / ADMIN)  
- City-based access control for staff  

### 📦 User Features
- Create courier shipments  
- Download PDF receipt  
- Track courier status  
- Update profile  
- View shipment history  

### 👨‍💼 Staff Features
- View pickups from assigned city  
- Assign shipments  
- Mark pickup & delivery  
- View staff activity logs  

### 🛠 Admin Features
- Approve/Reject staff requests  
- View staff details  
- Overall courier status summary  
- Track courier by ID  
- 
## 🏗️ Project Structure

```
courier_task/
├── online_courier/          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/online_courier/
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # JPA repositories
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── security/        # JWT & security config
│   │   │   │   └── OnlineCourierApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── sql/
│   │   │           ├── schema.sql         # Database DDL
│   │   │           └── sample_data.sql    # Sample data with test accounts
│   │   └── test/
│   └── pom.xml
└── frontend/                # Static HTML/CSS/JS frontend
    ├── css/
    │   └── styles.css
    ├── js/
    │   ├── api.js          # API utilities & auth
    │   ├── menu.js         # Role-based navigation
    │   └── cities.js       # Indian cities list
    ├── index.html           # Home page
    ├── login.html           # Login page
    ├── staff-request.html   # Staff registration request
    ├── userhome.html        # USER pages
    ├── user-profile.html
    ├── send-courier.html
    ├── courier-status.html
    ├── staffhome.html       # STAFF pages
    ├── staff-pickups.html
    ├── staff-deliveries.html
    ├── staff-logs.html
    ├── adminhome.html       # ADMIN pages
    ├── admin-staff-requests.html
    ├── admin-staff-details.html
    ├── admin-total-status.html
    └── admin-track.html
```
## Screenshots
<img width="1248" height="859" alt="Screenshot 2025-11-14 183907" src="https://github.com/user-attachments/assets/c7b5866f-a253-4ac1-b336-2fad81b2cdd0" />
<img width="1511" height="804" alt="Screenshot 2025-11-14 183925" src="https://github.com/user-attachments/assets/e7c09983-3481-440a-8a17-f6525558f778" />
<img width="1511" height="799" alt="Screenshot 2025-11-14 184026" src="https://github.com/user-attachments/assets/c3e3e43a-265f-446f-aa18-8f9bc96039dc" />
<img width="1497" height="694" alt="Screenshot 2025-11-14 184155" src="https://github.com/user-attachments/assets/dbcde8ab-271f-4ed0-8398-22b930bc7c75" />

## 🛠 Technology Stack

### Backend
- Java 21  
- Spring Boot 3.5.7  
- Spring Security + JWT  
- Spring Data JPA + Hibernate  
- MySQL 8.x  
- OpenPDF (PDF receipts)  
- Maven  

### Frontend
- HTML5  
- CSS3  
- Vanilla JavaScript  
- Fetch API (AJAX)  
- CSS Grid / Flexbox responsive UI  

---

## 📦 Prerequisites

- Java 21+  
- Maven 3.9+  
- MySQL 8.0+  
- Python/Node.js OR Live Server for frontend  

---

## 🗄️ Database Setup

```sql
mysql -u root -p
SOURCE /path/to/schema.sql;
SOURCE /path/to/sample_data.sql;

```

Enter your MySQL root password (configured as `1111` in application.properties), then execute:

```sql
SOURCE /path/to/couirer_task/online_courier/src/main/resources/sql/schema.sql;
SOURCE /path/to/couirer_task/online_courier/src/main/resources/sql/sample_data.sql;
```

Or on Windows:
```cmd
mysql -u root -p < d:\projects\couirer_task\online_courier\src\main\resources\sql\schema.sql
mysql -u root -p < d:\projects\couirer_task\online_courier\src\main\resources\sql\sample_data.sql
```

This creates:
- Database: `ocms`
- Tables: `users`, `staff_requests`, `couriers`, `courier_logs`
- Test accounts (password for all: **Admin@123**):
  - Admin: `admin@ocms.com`
  - Staff Mumbai: `staff.mumbai@ocms.com`
  - Staff Delhi: `staff.delhi@ocms.com`
  - User Alice: `alice@ocms.com`
  - User Bob: `bob@ocms.com`

### 2. Backend Configuration

The backend is pre-configured for local MySQL. If needed, edit:

```
online_courier/src/main/resources/application.properties
```

Key settings:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ocms
spring.datasource.username=root
spring.datasource.password=1111

app.jwt.secret=change-this-demo-secret-please-change
app.jwt.expiration-minutes=120
```

### 3. Start Backend

Navigate to backend folder and run:

```bash
cd online_courier
mvn clean install
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**

Verify backend is running:
```bash
curl http://localhost:8080/api/auth/login
```

### 4. Serve Frontend

#### Option A: Python HTTP Server (Recommended)
From the project root:
```bash
cd d:\projects\couirer_task
python -m http.server 5500
```

Frontend accessible at: **http://localhost:5500/frontend/**

#### Option B: VS Code Live Server
1. Open `d:\projects\couirer_task` in VS Code
2. Install "Live Server" extension
3. Right-click `frontend/index.html` → "Open with Live Server"

#### Option C: Node.js http-server
```bash
npm install -g http-server
cd d:\projects\couirer_task
http-server -p 5500
```

**Important**: If using a port other than 5500, update CORS origins in `application.properties`:
```properties
# Add your frontend origin
spring.web.cors.allowed-origins=http://localhost:YOUR_PORT
```

### 5. Access Application

Open browser:
- **Home**: http://localhost:5500/frontend/index.html
- **Login**: http://localhost:5500/frontend/login.html

## User Roles & Features

### USER Role
**Top Menu**: User Home | Update Profile | Send Courier | Courier Status | Logout

**Test Account**: `alice@ocms.com` / `Admin@123`

### STAFF Role
**Top Menu**: User Home | Pickups | Deliveries | Logs | Logout

**Features**:
- View available pickups in assigned city only
- Assign couriers and mark as picked up
- Mark assigned couriers as delivered
- View activity logs
- City-based access control (staff can only handle couriers from their city)

**Test Accounts**:
- Mumbai: `staff.mumbai@ocms.com` / `Admin@123`
- Delhi: `staff.delhi@ocms.com` / `Admin@123`

### ADMIN Role
**Top Menu**: Admin Home | Staff Requests | Staff Details | Total Couriers Status | Track Courier by ID | Logout

**Features**:
- Approve/reject staff registration requests
- View all staff with city assignments
- View courier status summary (counts by status)
- Track any courier by ID with full history
- Admin dashboard with system overview

**Test Account**: `admin@ocms.com` / `Admin@123`

## API Endpoints

### Authentication
```
POST   /api/auth/login          # Login with email & password
POST   /api/auth/register       # Register new USER
POST   /api/staff/request       # Submit staff registration request
```

### USER Endpoints (Requires ROLE_USER)
```
GET    /api/user/profile                          # Get user profile
PUT    /api/user/profile                          # Update profile
POST   /api/couriers                              # Create courier
GET    /api/couriers/{courierId}                  # Get courier details
GET    /api/couriers/{courierId}/receipt          # Download PDF receipt
GET    /api/users/{userId}/couriers               # List user's couriers
```

### STAFF Endpoints (Requires ROLE_STAFF)
```
GET    /api/staff/summary                         # Get dashboard summary
GET    /api/staff/available-pickups               # List available pickups (city-filtered)
POST   /api/staff/couriers/{courierId}/assign     # Assign courier to staff
POST   /api/staff/couriers/{courierId}/pickup     # Mark as picked up
GET    /api/staff/deliveries                      # List staff deliveries
POST   /api/staff/couriers/{courierId}/deliver    # Mark as delivered
GET    /api/staff/logs                            # Get staff activity logs
```

### ADMIN Endpoints (Requires ROLE_ADMIN)
```
GET    /api/admin/staff-requests                  # List pending staff requests
POST   /api/admin/staff-requests/{id}/approve     # Approve request
POST   /api/admin/staff-requests/{id}/reject      # Reject request
GET    /api/admin/staff                           # List all staff
GET    /api/admin/dashboard/status-summary        # Courier counts by status
GET    /api/admin/couriers/search?courier_id=...  # Track courier by ID
```

## Demo Flow (Acceptance Test)

### 1. User Creates Courier
1. Navigate to http://localhost:5500/frontend/login.html
2. Login as **alice@ocms.com** / **Admin@123**
3. Click "Send Courier"
4. Fill form:
   - Sender: Alice, Mumbai address, Mumbai city
   - Receiver: Bob, Delhi address, Delhi city
   - Weight: 2.5 kg
5. Submit → Get courier ID (e.g., OCMS-20251102-00004)
6. Click "Download Receipt (PDF)" → Verify PDF contains courier details
7. Navigate to "Courier Status" → Enter courier ID → See status: CREATED

### 2. Staff Picks Up Courier (City Enforcement)
1. Logout → Login as **staff.mumbai@ocms.com** / **Admin@123**
2. Click "Pickups" → See courier from Mumbai (Alice's courier)
3. Click "Mark as Picked Up" → Confirm
4. Verify courier status changes to PICKED_UP
5. Logout → Login as **staff.delhi@ocms.com** / **Admin@123**
6. Click "Pickups" → Should NOT see Mumbai courier (city restriction enforced)
7. Try to access Mumbai courier via API → Expect 403 Forbidden

### 3. Staff Delivers Courier
1. As **staff.mumbai@ocms.com**, navigate to "Deliveries"
2. See picked-up courier
3. Click "Mark as Delivered" → Confirm
4. Check "Logs" → See pickup and delivery actions logged

### 4. Admin Tracks Courier
1. Logout → Login as **admin@ocms.com** / **Admin@123**
2. Navigate to "Track Courier by ID"
3. Enter courier ID
4. Verify full history: CREATED → PICKED_UP → DELIVERED with timestamps and staff info

### 5. Admin Approves Staff Request
1. Navigate to http://localhost:5500/frontend/staff-request.html
2. Submit new staff request for Bengaluru
3. Logout → Login as admin
4. Navigate to "Staff Requests"
5. See pending request → Click "Approve"
6. Navigate to "Staff Details" → Verify new staff appears with status ACTIVE
7. Logout → Login with approved staff email / default password **Staff@123**

## Business Rules

### City-Based Access Control
- Staff can only view and handle couriers where:
  - `courier.senderCity == staff.city` (for pickups)
  - `courier.assignedStaff == staff` (for deliveries)
- Backend enforces this rule; attempts to bypass return HTTP 403
- Frontend filters lists to show only relevant couriers

### Courier Lifecycle
1. **CREATED**: Initial state when user creates courier
2. **PICKED_UP**: Staff from sender city marks as picked up
3. **IN_TRANSIT**: (Optional intermediate state)
4. **DELIVERED**: Assigned staff marks as delivered
5. **CANCELLED**: (Future enhancement)

### Authentication & Authorization
- JWT tokens expire after 120 minutes (configurable)
- Passwords hashed with BCrypt (strength 10)
- Role-based method security on all endpoints
- CORS enabled for frontend origin

## Testing

### Manual Testing Checklist
- [ ] User can register, login, create courier, download PDF
- [ ] Staff can only see couriers from their city
- [ ] Staff cannot access couriers from other cities (403 error)
- [ ] Admin can approve/reject staff requests
- [ ] Admin can track any courier by ID
- [ ] PDF receipt contains correct courier details
- [ ] Role-based menus display correctly
- [ ] Unauthorized access returns 401/403

### Unit Tests (Optional)
Run backend tests:
```bash
cd online_courier
mvn test
```

## Troubleshooting

### Backend won't start
- Check MySQL is running: `mysql -u root -p`
- Verify database exists: `SHOW DATABASES;` → should see `ocms`
- Check port 8080 not in use: `netstat -ano | findstr :8080`

### Frontend CORS errors
- Verify backend CORS configuration includes your frontend origin
- Check browser console for specific origin error
- Restart backend after changing CORS settings

### JWT Token Issues
- Token expires after 120 minutes → Re-login
- Check browser localStorage for `ocms_token`
- Clear localStorage if seeing authentication errors: `localStorage.clear()`

### PDF Download Not Working
- Verify OpenPDF dependency in pom.xml
- Check backend logs for PDF generation errors
- Test direct URL: `http://localhost:8080/api/couriers/{courierId}/receipt`

### Staff Can't Pick Up Courier
- Verify staff.city matches courier.senderCity
- Check staff account status is ACTIVE
- Verify courier status is CREATED (not already picked up)

## Security Notes

- **Default passwords** (`Admin@123`, `Staff@123`) are for demo only
- Change JWT secret in production
- Use HTTPS in production
- Implement rate limiting for login endpoints
- Add password strength requirements
- Enable CSRF protection if using session-based auth

## Future Enhancements

- Real-time notifications (WebSocket)
- Email notifications for status changes
- QR code on PDF receipt for quick tracking
- Mobile responsive design improvements
- File upload for proof documents
- Advanced search and filtering
- Multi-language support
- Analytics dashboard

## License

This is a demo project for educational purposes.

## Support

For issues or questions, create an issue in the project repository.

---

**Built with Spring Boot + Vanilla JavaScript**


## Local MySQL setup (Windows / IntelliJ)

1. Install and start MySQL Server 8.0 or newer.
2. In MySQL Workbench or the MySQL command line, run `CREATE DATABASE ocms;`.
3. Open `online_courier/src/main/resources/application.properties` and set `spring.datasource.username` and `spring.datasource.password` to your local MySQL credentials.
4. Run `schema_mysql.sql` first, then `sample_data_mysql.sql` from the `online_courier/src/main/resources/sql` folder.
5. In IntelliJ, reload Maven, then run the Spring Boot main class.
6. Serve the `frontend` folder on port 5500 and open its `index.html`.

The MySQL conversion has not been validated against a live MySQL server; if startup or SQL import reports an error, use the error output to make the remaining compatibility adjustments.
