FROM openjdk:18-slim-bullseye

# Create bot user
ARG GUID=1000
ARG UUID=1000

RUN groupadd --force -g $GUID bot
RUN useradd -ms /bin/bash --no-user-group -g $GUID -u $UUID bot

# Configure the start script
COPY docker/start-container.sh /usr/local/bin/start-container
RUN chmod +x /usr/local/bin/start-container

# Configure the supervisor
COPY docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Add build output to /bot directory and fix possible permission issues
ADD build/ /bot
RUN chown -R bot:bot /bot

# Run the container
ENTRYPOINT ["start-container"]
