# MediReach AI

**One Platform. Smarter Healthcare. Accessible to Everyone.**

MediReach AI is an India-focused healthcare access platform for patients and underserved communities. The project is being built incrementally for web and installable PWA use. AI-assisted coding and a hybrid free/paid provider strategy are planning assumptions; no AI provider, health database, or third-party service is connected yet.

> Health safety: MediReach AI is an information and navigation tool, not a diagnostic or treatment service. Emergency guidance, sources, consent, and human clinical care must remain clear in every health-related flow.

## Project status

- **Phase 0 — requirements:** first specification drafted in [`docs/requirements.md`](docs/requirements.md).
- **Phase 1 — foundation:** beginner-friendly monorepo scaffold created.
- **Runnable now:** frontend shell and backend health-check endpoint, subject to installing dependencies below.
- **Not implemented:** all 20 product features, authentication, database persistence, AI providers, external APIs, or production security controls.

## Stack

- Frontend: Next.js App Router + TypeScript
- Backend: Python + FastAPI
- Planned data layer: PostgreSQL via Supabase (not connected yet)
- Planned delivery: responsive website + installable PWA

## Complete development roadmap

Hum MediReach AI ko phase-by-phase build karenge. Har phase pichhle phase ke base par depend karega.

| Phase | Kya hoga | Complete kab mana jayega |
|---|---|---|
| 0 | Planning aur requirements | Vision, users, safety boundaries aur all 20 features document honge |
| 1 | Development foundation | Repo structure, local setup aur basic frontend/backend ready honge |
| 2 | UI/UX aur app structure | Screens, navigation aur mobile-first design ready hoga |
| 3 | Database, login aur privacy | Supabase/PostgreSQL plan, authentication aur consent model ready hoga |
| 4 | Backend APIs aur common services | API contracts, error handling aur shared services work karenge |
| 5 | Core healthcare flows | Care finder aur doctor visit preparation usable honge |
| 6 | AI intelligence | Safe, sourced, multilingual AI features provider abstraction ke saath honge |
| 7 | Advanced healthcare features | Capacity, schemes, timeline, reminders aur public health modules honge |
| 8 | PWA, accessibility aur privacy | Offline mode, accessibility checks aur privacy controls ready honge |
| 9 | Integration aur testing | End-to-end flows aur failure cases verify honge |
| 10 | Deployment aur live demo | Production-like deployment aur clearly labelled demo data ready hoga |
| 11 | Product documentation | Architecture, user guide, safety notes aur technical documentation ready hogi |

### Phase 0 — Project planning and requirements

- India-focused healthcare access problem, target users aur underserved communities ki needs define karna.
- All 20 planned features, safety boundaries aur success criteria document karna.
- MediReach ko health-information aur navigation platform rakhna; diagnosis, prescriptions aur emergency replacement nahi.

### Phase 1 — Development environment and repository foundation

- Beginner-friendly monorepo, Next.js + TypeScript frontend aur FastAPI backend setup.
- Frontend-backend health-check connection, environment examples, Git workflow aur setup guide.

### Phase 2 — UI/UX design and application structure

- Home Dashboard, AI Health Assistant, Care Finder, Health Workspace, Public Health Access aur Trust & Privacy sections design karna.
- Mobile-first, low-bandwidth, accessible aur Hindi/English/Odia experience define karna.

### Phase 3 — Database, authentication and security

- PostgreSQL/Supabase data model, user login, consent, data deletion/export aur access controls plan karna.

### Phase 4 — Backend APIs and shared services

- API versioning, validation, error handling, API client, uploads, source citations aur provider adapters build karna.

### Phase 5 — Core healthcare features

- Hospital and Clinic Finder, Emergency Connect, Doctor Appointment Preparation, Personal Health Timeline aur Smart Health Reminders.

### Phase 6 — AI healthcare intelligence

- AI Symptom Guide, Medical Report Explainer, Multilingual Patient Translator, Medicine Safety Information aur Healthcare Navigation Agent.
- Hybrid free/paid provider strategy, safe fallbacks, budget limits aur source citations.

### Phase 7 — Healthcare access and advanced features

- Smart Hospital Capacity Dashboard, Government Benefits Assistant, Multilingual Voice Assistant, Healthcare Desert Detector, Public Health Insights aur Community Health Resources.

### Phase 8 — Offline mode, accessibility and privacy

- Installable PWA, selected offline healthcare content, low-bandwidth mode, accessibility review aur Trust & Privacy Center.

### Phase 9 — Integration and testing

- End-to-end integration, AI/source safety checks, API failure, slow network, offline, mobile, accessibility aur privacy-flow testing.

### Phase 10 — Deployment and live demo

- Hosting, Supabase production setup, environment variables, monitoring, demo data labelling aur deployment verification.

### Phase 11 — Product documentation and long-term maintenance

- Architecture/data flow, safety and limitations, installation guide, API documentation aur user guide complete karna.
- Future improvements, maintenance aur feedback process document karna.

## All 20 planned features

1. AI Symptom Guide
2. Medical Report Explainer
3. Multilingual Patient Translator
4. Medicine Safety Information
5. AI Healthcare Navigation Agent
6. Smart Hospital Capacity Dashboard
7. Emergency Connect
8. Doctor Appointment Preparation
9. Personal Health Timeline
10. Smart Health Reminders
11. Government Healthcare Benefits Assistant
12. Hospital and Clinic Finder
13. Doctor Visit Preparation Workspace
14. Multilingual Voice Assistant
15. Offline Healthcare Mode
16. Healthcare Desert Detector
17. Trust & Privacy Center
18. Public Health Insights
19. Community Health Resources
20. Accessibility and Low-Bandwidth Mode

## Beginner setup

Install Node.js (LTS), pnpm, and Python 3.11 or newer. In a terminal, open this folder.

### 1. Start the frontend

```powershell
cd frontend
pnpm install
pnpm dev
```

Open http://localhost:3000.

### 2. Start the backend in a second terminal

```powershell
cd backend
py -m venv .venv
.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
uvicorn app.main:app --reload
```

Open http://127.0.0.1:8000/health or the interactive API docs at http://127.0.0.1:8000/docs. The frontend reads `NEXT_PUBLIC_API_URL` and defaults to `http://127.0.0.1:8000`.

## Repository map

```text
docs/                 product requirements, roadmap, and decisions
frontend/             Next.js user interface
backend/              FastAPI service
```

See [`docs/roadmap.md`](docs/roadmap.md) for phase sequencing and [`docs/decisions.md`](docs/decisions.md) for assumptions that can be revisited.
