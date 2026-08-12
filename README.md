# NEW Advanced Fake Profile & Bot Detector

This is a NEW standalone project. Your previous project is untouched.

## Top menu
Dashboard | Analyze Profile | Detection History | Reports | Admin | About | Logout

## Stack
React + Vite, Spring Boot, MySQL, JWT, Python FastAPI, Random Forest, Docker.

## Docker
```bash
docker compose up --build
```
Open http://localhost:5173

## Manual
Run MySQL, the ML service, Spring Boot backend, and React frontend as described in the original README.

Register an account. To enable Admin locally:
```sql
UPDATE users SET role='ADMIN' WHERE email='your@email.com';
```

The ML model uses synthetic demo data. Treat predictions as risk indicators, not proof.
