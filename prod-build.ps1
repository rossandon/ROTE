$tradingEngineServiceTag = "048545230017.dkr.ecr.us-east-2.amazonaws.com/rote:rote-trading-$(git rev-parse --short HEAD)"
$webServiceTag = "048545230017.dkr.ecr.us-east-2.amazonaws.com/rote:rote-webService-$(git rev-parse --short HEAD)"

echo $tradingEngineServiceTag
echo $webServiceTag

echo Building...

docker build --target=tradingEngineService -t $tradingEngineServiceTag -q .
docker build --target=webService -t $webServiceTag -q .

echo Pushing...

aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 048545230017.dkr.ecr.us-east-2.amazonaws.com

docker push $tradingEngineServiceTag
docker push $webServiceTag