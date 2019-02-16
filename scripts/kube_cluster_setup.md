# Kubernetes Cluster set-up

## Prerequisites
* vagrant
* git
* [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [kompose](https://github.com/kubernetes/kompose)

## Set-Up
Run the following script to set up a cluster with 1 master (k8s1) and 2 worker nodes (k8s2, k8s3).
```bash
git clone https://github.com/stephde/k8s-playground
cd k8s-playground
vagrant up
```

Once the setup is done ssh into the master node and setup additional stuff
```bash
# connect to vm
vagrant ssh k8s1
# get rights for admin config
sudo cp /etc/kubernetes/admin.conf $HOME/ && sudo chown $(id -u):$(id -g) $HOME/admin.conf && export KUBECONFIG=$HOME/admin.conf
# setup networking
start-weave
# check that everything works, by querying pods
kubectl get pods --all-namespaces
# check that nodes are connected by
kubectl get nodes
```

Set-Up kubectl connection from host to VM
Therefore, get ca-data and token from kubernetes similar to 
http://docs.shippable.com/deploy/tutorial/create-kubeconfig-for-self-hosted-kubernetes-cluster/ 
```bash
# make sure port forward is right / or network is connect otherwise
# in vagrant file forward port 6443

# create auth service in kubernetes
# get token from default service account
kubectl describe serviceaccount default
kubectl describe secrets default-token-f46pz
# copy token and insert in sample-config
 
# get certificate auth data
kubectl config view --flatten --minify > cluster-cert.txt
less cluster-cert.txt
# copy certificate-authority-data to ca-data in sample-config
# copy sample-config to ~/.kube/conf on server

# copy admin role yml from host to vm
vagrant scp ../distributed-monitoring/scripts/admin-role.yml k8s1:~/admin-role.yml
# apply admin role to default user in VM
# also required for kube-consumer to query the API
kubectl apply -f admin-role.yml
```
now you should be able to reach the cluster by using kubectl
if you get something like Unable to connect to the server: x509: certificate is valid for 10.96.0.1, 172.42.42.11, not 127.0.0.1
use flag to ignore tls certificate
```bash
kubectl --insecure-skip-tls-verify get pods
``` 


```bash
# add port foward
kubectl port-forward svc/gateway 80:80 --namespace=dm
# make sure nginx conf matches the port
```


Without kubectl connection from host
```bash
# copy compose file from host to vm
# vagrant plugin install vagrant-scp
vagrant scp docker-compose.hub.yml k8s1:~/docker-compose.hub.yml
```

Run docker registry in VM
```bash
docker run -d -p 5000:5000 --restart=always --name registry registry:2
```
Additionally, you need to add registry as insecure in docker.json on your development computer so that you can push images
So add `fb14srv7:5000` there.
Now you should be able to push docker images to the server with `scripts/tagAndPushDocker.sh`
(Alternatively, you can use the flag `--insecure-registry=fb14srv7:5000` with your docker push command)




### Resources
https://medium.com/@lizrice/kubernetes-in-vagrant-with-kubeadm-21979ded6c63


---------------------------------------------------------------------

## DEPRECATED!!!

## Resources
kubeadm - https://kubernetes.io/docs/setup/independent/
cluster - https://kubernetes.io/docs/setup/independent/create-cluster-kubeadm/
in VM - https://www.mirantis.com/blog/how-install-kubernetes-kubeadm/
in VM - https://medium.com/@KevinHoffman/building-a-kubernetes-cluster-in-virtualbox-with-ubuntu-22cd338846dd

#### Preconditions
* >= 2 CPUs, >= 2GB RAM
* turn off 
```bash
swapoff -a
# remove swap from /etc/fstab
vim /etc/fstab
```
* start docker daemon

```bash
# general
apt-get install -y curl apt-transport-https docker.io
systemctl enable docker && systemctl start docker && systemctl status docker

# add kubernetes repo
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
cat <<EOF >/etc/apt/sources.list.d/kubernetes.list 
deb http://apt.kubernetes.io/ kubernetes-xenial main 
EOF
apt-get update
apt-get install -y kubelet kubeadm kubectl kubernetes-cni
```

```bash
# on master node
# see https://www.mirantis.com/blog/how-install-kubernetes-kubeadm/

kubeadm init --pod-network-cidr=192.168.0.0/16
# systemctl enable kubelet && systemctl start kubelet
```

```bash
# on worker node
kubeadm join 10.0.2.15:6443 --token wyb6cz.nd88nvbv8xz7lt55 --discovery-token-ca-cert-hash sha256:3dfb63772dcddf3e5429de9562b4040c0639df88f432a089a595bc6dcca3a245
kubeadm join 192.168.99.1:6443 --token wyb6cz.nd88nvbv8xz7lt55 --discovery-token-ca-cert-hash sha256:3dfb63772dcddf3e5429de9562b4040c0639df88f432a089a595bc6dcca3a245

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

```bash
# /etc/hosts on host system for easy access to vms
192.168.99.1 kmaster
192.168.57.1 kworker
```


### working with VMs
```bash
VBoxManage list vms
VBoxManage export kube_ubuntu_2_master_copy -o ./kube_master.ova
VBoxManage import <path-to-vm>/kube_master.ova

VBoxManage startvm kube_master --type headless
VBoxManage controlvm kube_master poweroff
```
