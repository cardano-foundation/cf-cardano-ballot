# Cluster Bootstrapping

This module is responsible for bootstrapping Kubernetes cluster for the Explorer projects.

Clusters can be bootstrapped using the [init.sh](init.sh) script. Please check next paragraph to learn about the 
init script requirements

## Requirements

The init script will deploy both [ArgoCD](https://argo-cd.readthedocs.io/en/stable/) and the [Main App](https://argo-cd.readthedocs.io/en/stable/operator-manual/cluster-bootstrapping/).
Once installed, ArgoCD will observe the patterns of the GitOps approach and ensure that the kubernetes cluster deployment(s) are in sync
with the definition contained in the relevant git projects.

In order for ArgoCD to access GitHub, a number of _secrets_ need to be created.
1. Deployment keys for all the GitHub projects involved
2. [Sealed Secret](https://github.com/bitnami-labs/sealed-secrets) main key required to decrypt deployment keys at the point above

### Deployment Keys

In order to work, ArgoCD will need access to many GitHub repositories. At least it will require access to the main app repository: the one
that bootstraps the cluster.

GitHub Deployment Keys can be setup to grant ArgoCD with the required permissions.

Configuring deployment keys secrets is done in two steps:
1. deploying the relevant secrets via the [init.sh](https://github.com/cardano-foundation/cf-explorer/blob/feat/MET-755-Create_explorer_helm_chart/argocd-bootstrap/init.sh#L27) script
2. associate secret to repos: [values.yaml](https://github.com/cardano-foundation/cf-explorer/blob/feat/MET-755-Create_explorer_helm_chart/argocd-bootstrap/values.yaml#L37)

## Sealed Secret

Kubernetes `Secrets` are not encrypted, but rather just base64 encoded. So it is recommended to **NOT** version control them.

[Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets) is the simplest solution for safely dealing with secrets and allow
developer to version control them.

From their own documentation:

> Problem: "I can manage all my K8s config in git, except Secrets."

> Solution: Encrypt your Secret into a SealedSecret, which is safe to store - even inside a public repository. 
> The SealedSecret can be decrypted only by the controller running in the target cluster and nobody else (not even the original author) 
> is able to obtain the original Secret from the SealedSecret.

Creating a `Sealed Secrets` is simple and can be done via `openssl req -new -newkey rsa:4096 -x509 -sha256 -days 365 -nodes -out tls.crt -keyout tls.key`.

This key needs to:
1. be used to encrypt all the secrets
2. deployed on the cluster as per the [init.sh](https://github.com/cardano-foundation/cf-explorer/blob/feat/MET-755-Create_explorer_helm_chart/argocd-bootstrap/init.sh#L17) script

The key should be ideally secured on AWS Kms.
