#!/usr/bin/env bash

set +x

echo "Checking argocd namespace existence"
kubectl get ns argocd > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "argocd namespace does not exist, creating..."
  kubectl create ns argocd > /dev/null 2>&1
fi

echo "Checking cf-cip1694 namespace existence"
kubectl get ns cf-cip1694 > /dev/null 2>&1

if [ $? != 0 ]; then
  echo "cf-cardano-ballot namespace does not exist, creating..."
  kubectl create ns cf-cardano-ballot > /dev/null 2>&1
fi

# Installing ArgoCD CRD
kubectl apply -k "https://github.com/argoproj/argo-cd/manifests/crds?ref=v2.8.4"

### DockerHub secret
#kubectl create secret -n cf-cip1694 generic regcred \
#  --from-file=.dockerconfigjson=../../.keys/docker-cred.json \
#  --type=kubernetes.io/dockerconfigjson \
#  --save-config \
#  --dry-run=client \
#  -o yaml \
#  | kubectl apply -f -

## Git Hub deploy key
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
  --set git.targetRevision=infra-develop-cip1694 \
  --set valueFile=values-perf-preprod.yaml \
  -f values-secrets.yaml \
  -f values-cip-1694-perf-preprod.yaml
