# Stage 1: Builder
FROM node:20-alpine AS builder

WORKDIR /usr/src/app

# Copy root package.json and workspace package.json files
COPY package*.json ./
COPY backend/package*.json ./backend/

# Install all dependencies including devDependencies for building
RUN npm install

COPY . .

RUN npm run build

# Stage 2: Production
FROM node:20-alpine AS production

# Install ffmpeg for audio conversion
RUN apk add --no-cache ffmpeg

WORKDIR /usr/src/app

# Copy built backend files
COPY --from=builder /usr/src/app/backend/dist ./dist
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY --from=builder /usr/src/app/package.json ./package.json
COPY --from=builder /usr/src/app/backend/package.json ./backend/package.json

EXPOSE 3000

CMD [ "node", "dist/index.js" ]

# Stage 3: Development
FROM node:20-alpine AS development

# Install ffmpeg for audio conversion
RUN apk add --no-cache ffmpeg

WORKDIR /usr/src/app

# Copy workspace package.json files for development
COPY package*.json ./
COPY backend/package*.json ./backend/

RUN npm install

COPY . .

EXPOSE 3000

CMD [ "npm", "run", "dev" ]
