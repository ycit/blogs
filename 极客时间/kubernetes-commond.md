- 查看节点状态

  > kubectl  get nodes
  >
  > kubectl get pods

- 查看节点/pod 对象的详细信息、状态和事件

  > kubectl describe node \<node-name>
  >
  > kubectl describe pod \<pod-name>

- 查看节点上各个系统 Pod 的状态

  > kubectl get pods -n kube-system

- 创建/替换/应用/删除 pod

  > kubectl create -f xxx.yaml
  >
  > kubectl replace -f xxx.yaml
  >
  > kubectl apply -f xxx.yaml
  >
  > kubectl delete -f xxx.yaml

- 默认Master 节点不允许运行 Pod（Taint/Toleration）

  > 一旦某个节点被加上了 Taint，那么所有 Pod 就不能在这个节点上运行，除非声明自己能容忍 这个污点.
  >
  > 若允许单节点，需要删除这个 Taint
  >
  > kubectl taint nodes -all node-role.kubernetes.io/master-

- 进入到 Pod 当中

  > kubectl exec -it \<pod-name> -- /bin/bash