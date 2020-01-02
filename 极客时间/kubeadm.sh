kubeadm init \
  --kubernetes-version=v1.13.0 \
  --pod-network-cidr=10.244.0.0/16 \
  --apiserver-advertise-address=192.168.61.11