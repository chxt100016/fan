
if [ -n "$1" ]; then
  cd "$1"
fi
pwd

set -eu pipefail
docker build --no-cache -f Dockerfile -t fan .
# docker build  -f Dockerfile -t fan .

docker rm -f fan || true
docker run \
  -itd \
  -p 5005:5005 \
  -p 8080:8888 \
  --name=fan \
  --restart=always \
  -v /etc/download/xunlei/downloads:/etc/download/xunlei/downloads \
  fan 
docker image prune -f

