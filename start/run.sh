set -eu pipefail

cd /root/fantastic
docker build --no-cache -f Dockerfile -t fantastic .

docker rm -f fantastic || true
docker run -itd -p 5005:5005 -p 8888:8888 --name=fantastic -v /etc/download/xunlei/downloads:/etc/download/xunlei/downloads fantastic 
docker image prune -f

 