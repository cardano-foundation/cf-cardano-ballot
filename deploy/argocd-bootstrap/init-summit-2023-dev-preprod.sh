#!/usr/bin/env bash

set +x

echo "Checking argocd namespace existence"
kubectl get ns argocd > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "argocd namespace does not exist, creating..."
  kubectl create ns argocd > /dev/null 2>&1
fi

echo "Checking cf-summit-2023 namespace existence"
kubectl get ns cf-summit-2023 > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "cf-summit-2023 namespace does not exist, creating..."
  kubectl create ns cf-summit-2023 > /dev/null 2>&1
fi


## Blockfrost secrets
kubectl create secret -n cf-summit-2023 generic blockfrost-secrets \
  --from-env-file=../../.keys/blockfrost-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## Submit API secrets
kubectl create secret -n cf-summit-2023 generic submit-api-secrets \
  --from-env-file=../../.keys/submit-api-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## Wallet Mnemonic secrets
kubectl create secret -n cf-summit-2023 generic wallet-secrets \
  --from-env-file=../../.keys/wallet-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## DockerHub secret
kubectl create secret -n cf-summit-2023 generic regcred \
  --from-file=.dockerconfigjson=../../.keys/docker-cred.json \
  --type=kubernetes.io/dockerconfigjson \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## SNS Secrets
kubectl create secret -n cf-summit-2023 generic sns-secrets \
  --from-env-file=../../.keys/sns-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## User Verification Secrets
kubectl create secret -n cf-summit-2023 generic user-verification-secrets \
  --from-env-file=../../.keys/user-verification-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## Voting App Secrets
kubectl create secret -n cf-summit-2023 generic voting-app-secrets \
  --from-env-file=../../.keys/voting-app-secrets \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

## Git Hub deploy key
kubectl create secret generic github-deploy-key \
  --save-config \
  --dry-run=client \
  -o yaml \
  -n argocd \
  --from-file=../../.keys/cf-cardano-ballot \
  | kubectl apply -f -

## Prometheus / Alertmanager secrets
kubectl create secret -n cf-summit-2023 generic prometheus-alertmanager-secrets \
  --from-env-file=../../.keys/prometheus-alertmanager-secrets-summit-dev-preprod \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

#echo "Fetching helm dependencies for main app"
helm dependency build

echo "Updating helm dependencies for main app"
helm dependency update

helm upgrade --install argocd -n argocd . \
  --set git.targetRevision=infra-develop \
  --set valueFile=values-dev-preprod.yaml \
  -f values-secrets.yaml \
  -f values-summit-2023-dev-preprod.yaml
