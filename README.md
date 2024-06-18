# ÉTAPES KUBERNETES

## Pointer son docker client vers l'instance docker de minikube
```eval $(minikube docker-env -p nom_profil) ```

## Builder une image docker
```docker build -t nom_image:version .```

## Run le docker
```docker run -d -p port:port --name=nom_container nom_image:version```

## Appliquer les fichiers k8s au namespace
```kubectl apply -f chemin_fichier -n nom_namespace```

## Lancer avec le profil kstorage
``` minikube start -p nom_profil ```

## Executer la commande tunnel
``` minikube tunnel -p nom_profil```
