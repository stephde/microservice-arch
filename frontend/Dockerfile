FROM node:lts-alpine

WORKDIR /app/frontend

# install dependencies
RUN npm install -g yarn

# add frontend and install dependencies
COPY ./package.json /app/frontend/package.json
COPY ./yarn.lock /app/frontend/yarn.lock
RUN cd /app/frontend && yarn install

COPY . /app/frontend

CMD ["yarn", "serve-prod"]
EXPOSE 8080