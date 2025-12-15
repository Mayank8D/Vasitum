# Automatic Interview Scheduler

Author: Mayank  
Stack: Spring Boot 3, Java 17, MySQL (Docker), JPA; H2 only for tests.

Backend that lets teachers (interviewers) set weekly rules, auto-build the next two weeks of interview slots, and lets students (candidates) grab and move a single booking. A small static UI ships at `/` so you can drive the APIs without Postman.

## What’s inside
- `docker-compose.yml` – MySQL 8 with a named volume so data survives restarts.
- `src/main/java` – controllers, services, domain models, repositories.
- `src/main/resources/application.yml` – datasource + JPA knobs.
- `src/main/resources/static/index.html` – the lightweight UI.
- `src/test` – H2-based tests.
- `project.txt` – the original brief.
- `img/` – add screenshots/diagrams here and link them below if you have them.

## Run it locally
1) Start MySQL (from repo root):
   ```bash
   docker compose up -d
   ```
   Defaults: db `scheduler`, user/pass `root`/`root`, port `3306`, volume `scheduler-mysql-data`.
2) Boot the app:
   ```bash
   mvn spring-boot:run
   ```
3) Open `http://localhost:8080/` and use the forms. Change DB creds/port in `src/main/resources/application.yml` if you override the compose env.

## How the flow works
- Teacher adds weekly availability (day + time range + slot length).
- System generates dated slots for the next 14 days, respecting weekly limits.
- Students see only future slots with remaining capacity, paged by cursor.
- Each student keeps one booking; moving a booking frees the old slot.

## API surface
| Endpoint | Method | Purpose | Example payload |
| --- | --- | --- | --- |
| `/interviewers` | POST | Create teacher | `{ "name": "Alex", "weeklyInterviewLimit": 5 }` |
| `/interviewers/{id}/availability` | POST | Add weekly rule | `{ "dayOfWeek": "MONDAY", "startTime": "09:00", "endTime": "12:00", "slotDurationMinutes": 30 }` |
| `/interviewers/{id}/slots/generate` | POST | Build slots for next 14 days | – |
| `/slots?cursor=ISO&limit=20` | GET | List available slots (future only) | – |
| `/bookings` | POST | Student books a slot | `{ "candidateEmail": "student@x.com", "slotId": 12 }` |
| `/bookings/{bookingId}` | PUT | Move booking to another slot | `{ "slotId": 15 }` |
| `/bookings?email=student@x.com` | GET | Show bookings for a student | – |

Notes:
- `cursor` is an ISO datetime; omit it to start from “now.” Use `nextCursor` from the response to page forward.
- Weekly cap is enforced during slot generation. Duplicate slot times are skipped by DB constraint.
- Booking/update paths are transactional with optimistic locking to avoid double-booking.

## Tests
```bash
mvn test
```
Runs against H2 via `src/test/resources/application-test.yml`; MySQL is not required for tests.

## Screens / diagrams
Existing images:
- ![Screen 1](img/1.png)
- ![Screen 2](img/2.png)
- ![Screen 3](img/3.png)
- ![Screen 4](img/4.png)

