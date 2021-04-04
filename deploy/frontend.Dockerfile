FROM nginx:1.19

ENV BACKEND_HOSTNAME backend
ENV BACKEND_PORT 4000

COPY deploy/nginx /etc/nginx/templates/

COPY frontend/dist/spa /srv/public

