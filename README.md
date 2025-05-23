# docker
- -p 5005:5005 8888:8888
- -v /root:/file
```
docker rm -f fantastic && \
docker run -itd -p 5005:5005 -p 8888:8888 --name=fantastic fantastic

 