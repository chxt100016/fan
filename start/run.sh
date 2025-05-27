set -euo pipefail

cd /root/fantastic
docker build --no-cache -f Dockerfile -t fantastic .

docker rm -f fantastic || true
docker run -itd -p 5005:5005 -p 8888:8888 --name=fantastic fantastic 
docker image prune -f

 