/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.cluster;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.redisson.misc.URLBuilder;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class ClusterPartition {

    public enum Type {MASTER, SLAVE}
    
    private Type type = Type.MASTER;
    
    private final String nodeId;
    private boolean masterFail;
    private URL masterAddress;
    private final Set<URL> slaveAddresses = new HashSet<URL>();
    private final Set<URL> failedSlaves = new HashSet<URL>();
    
    private final Set<Integer> slots = new HashSet<Integer>();
    private final Set<ClusterSlotRange> slotRanges = new HashSet<ClusterSlotRange>();

    private ClusterPartition parent;
    
    public ClusterPartition(String nodeId) {
        super();
        this.nodeId = nodeId;
    }
    
    public ClusterPartition getParent() {
        return parent;
    }

    public void setParent(ClusterPartition parent) {
        this.parent = parent;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getNodeId() {
        return nodeId;
    }

    public void setMasterFail(boolean masterFail) {
        this.masterFail = masterFail;
    }
    public boolean isMasterFail() {
        return masterFail;
    }

    public void addSlots(Set<Integer> slots) {
        this.slots.addAll(slots);
    }

    public void removeSlots(Set<Integer> slots) {
        this.slots.removeAll(slots);
    }

    public void addSlotRanges(Set<ClusterSlotRange> ranges) {
        for (ClusterSlotRange clusterSlotRange : ranges) {
            for (int i = clusterSlotRange.getStartSlot(); i < clusterSlotRange.getEndSlot() + 1; i++) {
                slots.add(i);
            }
        }
        slotRanges.addAll(ranges);
    }
    public void removeSlotRanges(Set<ClusterSlotRange> ranges) {
        for (ClusterSlotRange clusterSlotRange : ranges) {
            for (int i = clusterSlotRange.getStartSlot(); i < clusterSlotRange.getEndSlot() + 1; i++) {
                slots.remove(i);
            }
        }
        slotRanges.removeAll(ranges);
    }
    public Set<ClusterSlotRange> getSlotRanges() {
        return slotRanges;
    }
    public Set<Integer> getSlots() {
        return slots;
    }

    public InetSocketAddress getMasterAddr() {
        return new InetSocketAddress(masterAddress.getHost(), masterAddress.getPort());
    }

    public URL getMasterAddress() {
        return masterAddress;
    }
    public void setMasterAddress(String masterAddress) {
        setMasterAddress(URLBuilder.create(masterAddress));
    }
    public void setMasterAddress(URL masterAddress) {
        this.masterAddress = masterAddress;
    }

    public void addFailedSlaveAddress(URL address) {
        failedSlaves.add(address);
    }
    public Set<URL> getFailedSlaveAddresses() {
        return Collections.unmodifiableSet(failedSlaves);
    }
    public void removeFailedSlaveAddress(URL uri) {
        failedSlaves.remove(uri);
    }

    public void addSlaveAddress(URL address) {
        slaveAddresses.add(address);
    }
    public Set<URL> getSlaveAddresses() {
        return Collections.unmodifiableSet(slaveAddresses);
    }
    public void removeSlaveAddress(URL uri) {
        slaveAddresses.remove(uri);
        failedSlaves.remove(uri);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClusterPartition other = (ClusterPartition) obj;
        if (nodeId == null) {
            if (other.nodeId != null)
                return false;
        } else if (!nodeId.equals(other.nodeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClusterPartition [nodeId=" + nodeId + ", masterFail=" + masterFail + ", masterAddress=" + masterAddress
                + ", slaveAddresses=" + slaveAddresses + ", failedSlaves=" + failedSlaves + ", slotRanges=" + slotRanges
                + "]";
    }
    
}
