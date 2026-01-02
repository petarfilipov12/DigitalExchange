FROM node:25

EXPOSE 3000

RUN apt-get update && apt-get install -y \
    iputils-ping \
    vim-common \
    nano \
    && rm -rf /var/lib/apt/lists/*
    
WORKDIR /app

RUN npm install lightweight-charts

COPY code/ ./

CMD ["npm", "run", "dev"]

