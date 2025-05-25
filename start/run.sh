docker build -t fantastic /root/fantastic
docker rm -f fantastic 
docker run -itd -p 5005:5005 -p 8888:8888 --name=fantastic fantastic 
docker image prune

 