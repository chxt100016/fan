set -eu pipefail

cd /Users/chenxintong/space/docker/fan
# docker build --no-cache -f Dockerfile -t fantastic .
docker build  -f Dockerfile -t fan .

docker rm -f fan || true
docker run -itd -p 5005:5005 -p 8080:8888 --name=fan -v /etc/download/xunlei/downloads:/etc/download/xunlei/downloads fan 
docker image prune -f

