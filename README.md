# Illumio-Challenge

## Main File: FireWall.java

## Algorithm : 
1. read rules from CSV file and load into a hashmap
2. For checking a packet is valid or not, it will take O(1)

## Test :
I've tested some edge cases such like:
EX1: inbound,udp,53,192.168.1.1-192.168.2.5
test case1: inbound,udp,53,192.168.1.1
test case2: inbound,udp,53,192.168.2.5
test case3: inbound,udp,53,192.168.2.6

EX1: outbound,tcp,10000-20000,192.168.10.11
test case1: outbound,tcp,10000,192.168.10.11
test case2: outbound,tcp,20000,192.168.10.11
test case3: outbound,tcp,20001,192.168.10.11


## Optimization:
I encode address and port as one long number to speed up the packet check.
addr:      0.0.0.1         -> 1
port:      6000            -> 6000
encoded:   1 *10000 + 6000 -> 16000
