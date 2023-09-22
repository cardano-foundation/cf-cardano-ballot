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
1. deploying the relevant secrets via the `init.sh`
2. associate secret to repos: `values.yaml`

## Prometheus and Alertmanager secrets

https://stackoverflow.com/questions/74254241/how-to-insert-a-secret-in-alertmanager-configuration-file
