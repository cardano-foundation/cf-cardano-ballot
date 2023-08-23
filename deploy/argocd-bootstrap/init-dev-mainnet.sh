#!/usr/bin/env bash

set +x

echo "Checking argocd namespace existence"
kubectl get ns argocd > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "argocd namespace does not exist, creating..."
  kubectl create ns argocd > /dev/null 2>&1
fi

echo "Checking cf-explorer namespace existence"
kubectl get ns cf-explorer > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "cf-explorer namespace does not exist, creating..."
  kubectl create ns cf-explorer > /dev/null 2>&1
fi

## DockerHub secret
kubectl create secret -n cf-explorer generic regcred \
  --from-file=.dockerconfigjson=../.keys/docker-cred.json \
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
  --from-file=../.keys/cf-explorer \
  | kubectl apply -f -

#echo "Fetching helm dependencies for main app"
#helm dependency build

echo "Updating helm dependencies for main app"
helm dependency update

helm upgrade --install argocd -n argocd . \
  --set git.targetRevision=HEAD \
  --set valueFile=values-dev-mainnet.yaml \
  -f values-secrets.yaml
