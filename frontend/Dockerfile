# node image da wir node für den bau der vue.js dateien brauchen
FROM node:lts-alpine AS dev
# in /app im container arbeiten
WORKDIR /app
# package.json enhält in einem projekt was mit npm
# (node package manager, glaube ich) gebaut wird, alle
# dependencies. kopiere diese datei aus dem repo in
# den container
COPY package*.json .
# mit npm die dependencies installieren
RUN npm install

# hier machen wir einen multi stage build damit u.a.
# dateien die wir aus dem letzten schritt nicht mehr
# brauchen nicht im finalen image landen (also größe
# minimieren!)
# multi stage builds erlauben nämlich mehrere FROM anweisungen
# https://docs.docker.com/build/building/multi-stage/
FROM dev as build
# die restlichen frontend dateien in den container kopieren
COPY . .
# die vue.js dateien bauen
RUN npm run build

# wieder multi stage build
# nginx ist unser webserver der die gebauten vue.js
# dateien (html/css/js) hostet
FROM nginx:stable-alpine as prod
# aus dem build schritt die gebauten dateien kopieren
# npm run build packt seinen output in /app/dist
COPY --from=build /app/dist /usr/share/nginx/html
# webserver config datei aus dem repo in den container
COPY nginx.conf /etc/nginx/nginx.conf
# port 80 freigeben
EXPOSE 80
# nginx starten
# '-g daemon off;' muss man hinzufügen wenn man
# nginx im docker laufen lassen möchte
ENTRYPOINT ["nginx","-g","daemon off;"]
