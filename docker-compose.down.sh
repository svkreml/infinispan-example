V2=$(docker compose version)
echo $V2
if  [[ $V2 == Docker\ Compose\ version\ v2* ]];
then
docker compose --file docker-compose.yml down --volumes --remove-orphans
else
docker-compose --file docker-compose.yml down --volumes --remove-orphans
fi
