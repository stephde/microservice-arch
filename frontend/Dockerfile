FROM node:lts-alpine

WORKDIR /app/frontend

# install dependencies
RUN npm install -g yarn

# add frontend and install dependencies
COPY . /app/frontend
RUN cd /app/frontend && yarn install --production

CMD ["yarn", "serve-prod"]
EXPOSE 8080