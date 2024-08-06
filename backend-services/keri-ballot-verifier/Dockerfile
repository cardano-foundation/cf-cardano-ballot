# Builder stage
FROM python:3.12 AS builder

WORKDIR /src

RUN curl https://sh.rustup.rs -sSf | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"

COPY requirements.txt setup.py .
RUN mkdir src
RUN pip3 install -r requirements.txt

# Runtime stage
FROM python:3.12-slim AS base

WORKDIR /src

RUN apt update -qq && \
    apt install -y libsodium23 && \
    apt clean

COPY --from=builder /usr/local /usr/local

COPY . .

ENV CONFIG_DIR /usr/local/var/keri
WORKDIR $CONFIG_DIR
RUN ln -s /src/scripts/start_verifier.sh /usr/local/bin/ballot-verifier

RUN chmod +x /usr/local/bin/ballot-verifier

FROM base AS ballot-verifier
ENTRYPOINT ["/usr/local/bin/ballot-verifier"]
