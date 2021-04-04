FROM openjdk:11-slim

ENV PORT 4000
ENV SESSION_STORAGE_DIR /srv/sessions
ENV FILES_STORAGE_DIR /srv/storage
RUN useradd -u 999 -m app && mkdir "$SESSION_STORAGE_DIR" && mkdir "$FILES_STORAGE_DIR" && chown -R app $SESSION_STORAGE_DIR $FILES_STORAGE_DIR
USER app

COPY backend/build/libs/backend.jar /srv/backend.jar

CMD [ \
    "java", "-server", "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
    "-Dfile.encoding=utf-8", "-Djava.net.preferIPv4Stack=true", \
    "-jar", "/srv/backend.jar" \
]
