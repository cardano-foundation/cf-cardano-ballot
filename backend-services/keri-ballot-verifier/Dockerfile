FROM python:3.12-slim

WORKDIR /src

RUN curl https://sh.rustup.rs -sSf | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"

COPY . /src
RUN pip3 install -r requirements.txt

RUN apt update -qq && \
    apt install -y libsodium23 && \
    apt clean

ENV VERIFIER_CONFIG_DIR /usr/local/var/keri
WORKDIR $VERIFIER_CONFIG_DIR

RUN ln -s /src/scripts/start_verifier.sh /usr/local/bin/ballot-verifier && \
    chmod +x /usr/local/bin/ballot-verifier

ENTRYPOINT ["/usr/local/bin/ballot-verifier"]
