# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Builder
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Cache dependency layer — only invalidated when package files change.
COPY package.json package-lock.json ./
RUN npm ci --frozen-lockfile

# Copy source and build the Next.js application.
COPY . .
RUN npm run build

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runner
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS runner

# Create a non-root system user for security hardening.
RUN addgroup -S nextjs && adduser -S -G nextjs nextjs

WORKDIR /app

ENV NODE_ENV=production

# Copy only the minimal artifacts required to run the standalone server.
COPY --from=builder --chown=nextjs:nextjs /app/public ./public
COPY --from=builder --chown=nextjs:nextjs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nextjs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

CMD ["node", "server.js"]
