# Contributing to Bazario

Thanks for your interest in contributing! This document describes how we work, how branches are named, and what a pull request needs before it can be merged.

> **Public repo notice:** Bazario is a public learning/interview-prep project. Everything you write here is visible to everyone. Do not reference internal tools, private boards, or non-public tracking systems in issues, PRs, code comments, or commit messages. GitHub issues are the single source of truth for what is being worked on.

---

## Branching strategy

We use a **GitHub Flow** model — `main` is always deployable, and all work happens on short-lived branches merged via pull request.

```
main ────────────────────────●──────────────●────── (always green)
        \
         ●── feature/21-bean-validation ──► PR ──► merged ──► branch deleted
        /
```

### The rules

1. **`main` must always be green** — `./mvnw test` (`apps/backend`) must pass before any PR is merged.
2. **Never commit or push directly to `main`.** Every change goes through a pull request.
3. **Branch from `main`, PR back to `main`.**
4. **One issue per branch.** If you fix two unrelated things, use two branches (or two stacked PRs).
5. **Keep branches short-lived** — merge or close within days, not months.
6. **Delete the branch after merge** (GitHub does this automatically if you enable it).
7. **Keep `main` history clean** — squash-merge for feature branches, or keep small, logically separate commits.

### Branch naming

```
<type>/<issue-number>-<short-slug>
```

| Type | Use for | Example |
|---|---|---|
| `feature/` | New functionality | `feature/21-bean-validation` |
| `fix/` | Bug / regression fixes | `fix/26-product-pagination` |
| `docs/` | Documentation only | `docs/onboarding-diagram` |
| `chore/` | Build, deps, tooling — no behavior change | `chore/gradle-deps` |
| `refactor/` | Code restructure, no behavior change | `refactor/order-hexagonal` |
| `test/` | Tests only | `test/order-service-tx` |

Guidelines:

- Use the GitHub issue number after the type (`feature/21-bean-validation` — issue #21).
- Slug is lowercase, hyphen-separated, 2–4 words.
- No personal branches (`anjan/fix`, `biswajit/stuff`) — the branch describes the work, not the person.

---

## Contribution workflow

### External contributors (fork flow)

1. Find an issue labelled `good first issue` / `help wanted`.
2. Comment on the issue that you'd like to take it.
3. Fork the repo, clone your fork.
4. Create a branch: `git checkout -b fix/<issue>-<slug>`.
5. Make the change, run tests, commit using conventional commits.
6. Open a PR back to `anjanx44/Bazario:main` — link the issue (`Closes #21`).
7. A maintainer will review, request changes, and merge.

### Team members (branch flow)

1. Pick an issue from the list; create a branch in `anjanx44/Bazario` directly.
2. Open a PR early — mark it **Draft** if it is not ready for review yet.
3. Move to **Ready for review** when tests are green and the description is filled in.
4. Maintainer merges.

---

## Commit messages — Conventional Commits

```
<type>(<scope>): <short summary, imperative, lower case>

<body — what & why, not how. Optional for typos/simple docs>
```

**Examples from this repo:**

```
feat(auth): implement JwtService, JwtAuthenticationFilter, and security config
feat(orders): add bean validation to CreateOrderRequest
fix(config): add default development fallback for jwt secret
build(deps): add jjwt dependencies and update gitignore
docs: update backend README and add GitHub issue drafts
```

| Type | Meaning |
|---|---|
| `feat` | New capability (API, behavior) |
| `fix` | Bug fix |
| `docs` | Docs only |
| `refactor` | Code structure change, no behavior change |
| `test` | Tests only |
| `build` | Build system, dependencies |
| `ci` | CI/CD configuration |
| `chore` | Maintenance |

Scope is optional but encouraged: `auth`, `orders`, `customers`, `inventory`, `catalog`, `config`, `admin`, `docs`.

---

## Pull request requirements

Every PR must include:

- [ ] **Green tests** — `./mvnw test` from `apps/backend` passes (mention the result in the PR).
- [ ] **Issue linkage** — `Closes #<issue>` or `Refs #<issue>` in the description.
- [ ] **What & why** — a short description (the template in this repo asks for scope, notes, and testing).
- [ ] **No secrets** — no `.env`, no credentials; update `.env.example` instead of pasting values.
- [ ] **Small diff** — if the diff is > ~400 lines, split it.

### Review & merge rules

- At least **one approving review** before merge.
- Maintainer will squash-merge focused branches (keeps `main` history readable).
- Feedback is requested in comments — please respond or push fixes; PRs unattended for 2 weeks may be closed.
- Never merge a red build.

---

## Code standards

- **Java 21, Spring Boot**, hexagonal architecture (ports & adapters).
- **Domain layer** stays framework-free: no Spring/JPA annotations on domain models or services.
- **No cross-module adapter imports** — DTOs in one module must not import another module's DTOs or domain types (see `AdminDtos` for the ACL pattern).
- **Validation at the edge** — request DTOs carry `jakarta.validation` constraints; controllers use `@Valid` and rely on the central `GlobalExceptionHandler`.
- **Money never as float** — `BigDecimal`.
- **Public repo** — code comments, Javadoc, test names, PR text and commits in English.

---

## Reporting issues

- Open a GitHub issue with: expected behavior, actual behavior, steps to reproduce, and versions.
- Pick the label if one applies (`bug`, `enhancement`, `documentation`).
- If you want to fix it yourself, say so in the issue and go through the fork flow above.

---

## Where to find things

| I want to… | Go to |
|---|---|
| Set up my local environment | [`docs/onboarding-guide.md`](docs/onboarding-guide.md) |
| Understand the code layout | [`docs/architecture-overview.md`](docs/architecture-overview.md) |
| Read the API contract | [`docs/development/api-contracts.md`](docs/development/api-contracts.md) |
| Pick a first issue | [Issues](https://github.com/anjanx44/Bazario/issues) — look for `good first issue` |
