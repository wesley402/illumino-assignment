# Illumio-Challenge

## Main File: FireWall.java

## Algorithm : 
1. read rules from CSV file and load into a hashmap
2. For checking a packet is valid or not, it will take O(1)

## Test :
I've tested some edge cases such like: <br />
EX1: inbound,udp,53,192.168.1.1-192.168.2.5 <br />
test case1: inbound,udp,53,192.168.1.1 <br />
test case2: inbound,udp,53,192.168.2.5 <br />
test case3: inbound,udp,53,192.168.2.6 <br />

EX1: outbound,tcp,10000-20000,192.168.10.11  <br />
test case1: outbound,tcp,10000,192.168.10.11  <br />
test case2: outbound,tcp,20000,192.168.10.11  <br />
test case3: outbound,tcp,20001,192.168.10.11 <br />


## Optimization:
I encode address and port as one long number to speed up the packet check.  <br />
addr:      0.0.0.1         -> 1  <br />
port:      6000            -> 6000  <br />
encoded:   1 *10000 + 6000 -> 16000  <br />
