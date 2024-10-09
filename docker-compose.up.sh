V2=$(docker compose version)
echo $V2
if  [[ $V2 == Docker\ Compose\ version\ v2* ]];
then
docker compose -f docker-compose.yml up -d
else
docker-compose -f docker-compose.yml up -d
fi
