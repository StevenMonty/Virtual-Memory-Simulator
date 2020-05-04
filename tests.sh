
echo "=======SWIM / LRU========"
./vmsim.sh -n 8 -a lru swim.trace
./vmsim.sh -n 16 -a lru swim.trace
./vmsim.sh -n 32 -a lru swim.trace
./vmsim.sh -n 64 -a lru swim.trace

echo "=======SWIM / OPT========"
./vmsim.sh -n 8 -a opt swim.trace
./vmsim.sh -n 16 -a opt swim.trace
./vmsim.sh -n 32 -a opt swim.trace
./vmsim.sh -n 64 -a opt swim.trace

echo "=======SWIM / SECOND========"
./vmsim.sh -n 8 -a second swim.trace
./vmsim.sh -n 16 -a second swim.trace
./vmsim.sh -n 32 -a second swim.trace
./vmsim.sh -n 64 -a second swim.trace

echo "=======GCC / LRU========"
./vmsim.sh -n 8 -a lru gcc.trace
./vmsim.sh -n 16 -a lru gcc.trace
./vmsim.sh -n 32 -a lru gcc.trace
./vmsim.sh -n 64 -a lru gcc.trace

echo "=======GCC / OPT========"
./vmsim.sh -n 8 -a opt gcc.trace
./vmsim.sh -n 16 -a opt gcc.trace
./vmsim.sh -n 32 -a opt gcc.trace
./vmsim.sh -n 64 -a opt gcc.trace

echo "=======GCC / SECOND========"
./vmsim.sh -n 8 -a second gcc.trace
./vmsim.sh -n 16 -a second gcc.trace
./vmsim.sh -n 32 -a second gcc.trace
./vmsim.sh -n 64 -a second gcc.trace

echo "=======GZIP / LRU========"
./vmsim.sh -n 8 -a lru gzip.trace
./vmsim.sh -n 16 -a lru gzip.trace
./vmsim.sh -n 32 -a lru gzip.trace
./vmsim.sh -n 64 -a lru gzip.trace

echo "=======GZIP / OPT========"
./vmsim.sh -n 8 -a opt gzip.trace
./vmsim.sh -n 16 -a opt gzip.trace
./vmsim.sh -n 32 -a opt gzip.trace
./vmsim.sh -n 64 -a opt gzip.trace

echo "=======GZIP / SECOND========"
./vmsim.sh -n 8 -a second gzip.trace
./vmsim.sh -n 16 -a second gzip.trace
./vmsim.sh -n 32 -a second gzip.trace
./vmsim.sh -n 64 -a second gzip.trace
