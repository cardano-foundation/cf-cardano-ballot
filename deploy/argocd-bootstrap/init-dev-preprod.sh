#!/usr/bin/env bash

set +x

echo "Checking argocd namespace existence"
kubectl get ns argocd > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "argocd namespace does not exist, creating..."
  kubectl create ns argocd > /dev/null 2>&1
fi

echo "Checking cf-voting-app namespace existence"
kubectl get ns cf-voting-app > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "cf-voting-app namespace does not exist, creating..."
  kubectl create ns cf-voting-app > /dev/null 2>&1
fi

## Create a Master Key
# openssl req -new -newkey rsa:4096 -x509 -sha256 -days 365 -nodes -out tls.crt -keyout tls.key

## DockerHub secret
kubectl create secret -n cf-voting-app generic regcred \
  --from-file=.dockerconfigjson=../../.keys/docker-cred.json \
  --type=kubernetes.io/dockerconfigjson \
  --save-config \
  --dry-run=client \
  -o yaml \
  | kubectl apply -f -

# Git Hub deploy key
kubectl create secret generic github-deploy-key \
  --save-config \
  --dry-run=client \
  -o yaml \
  -n argocd \
  --from-file=../../.keys/cf-cardano-ballot \
  | kubectl apply -f -

#echo "Fetching helm dependencies for main app"
helm dependency build

echo "Updating helm dependencies for main app"
helm dependency update

helm upgrade --install argocd -n argocd . \
  --set git.targetRevision=HEAD \
  --set valueFile=values-dev-preprod.yaml \
  -f values-secrets.yaml \
  -f values-dev-preprod.yaml
