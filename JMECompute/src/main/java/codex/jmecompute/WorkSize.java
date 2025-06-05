/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute;

import org.lwjgl.PointerBuffer;

/**
 *
 * @author codex
 */
public final class WorkSize {
    
    private int globalX, globalY, globalZ;
    private int localX, localY, localZ;
    
    public WorkSize() {
        set(1, 1, 1, 1, 1, 1);
    }
    public WorkSize(WorkSize size) {
        set(size);
    }
    public WorkSize(int global) {
        set(global, global, global, 1, 1, 1);
    }
    public WorkSize(int global, int local) {
        set(global, global, global, local, local, local);
    }
    public WorkSize(int globalX, int globalY, int globalZ) {
        set(globalX, globalY, globalZ, 1, 1, 1);
    }
    public WorkSize(int globalX, int globalY, int globalZ, int localX, int localY, int localZ) {
        set(globalX, globalY, globalZ, localX, localY, localZ);
    }
    
    private int clamp(int val) {
        return Math.max(val, 1);
    }
    
    public WorkSize set(WorkSize size) {
        globalX = size.globalX;
        globalY = size.globalY;
        globalZ = size.globalZ;
        localX = size.localX;
        localY = size.localY;
        localZ = size.localZ;
        return this;
    }
    public WorkSize set(int global, int local) {
        return set(global, global, global, local, local, local);
    }
    public WorkSize set(int globalX, int globalY, int globalZ, int localX, int localY, int localZ) {
        this.globalX = clamp(globalX);
        this.globalY = clamp(globalY);
        this.globalZ = clamp(globalZ);
        this.localX = clamp(localX);
        this.localY = clamp(localY);
        this.localZ = clamp(localZ);
        return this;
    }
    
    public WorkSize setGlobal(WorkSize size) {
        return setGlobal(size.globalX, size.globalY, size.globalZ);
    }
    public WorkSize setGlobal(int global) {
        return setGlobal(global, global, global);
    }
    public WorkSize setGlobal(int x, int y, int z) {
        this.globalX = clamp(x);
        this.globalY = clamp(y);
        this.globalZ = clamp(z);
        return this;
    }
    
    public WorkSize setLocal(WorkSize size) {
        return setLocal(size.localX, size.localY, size.localZ);
    }
    public WorkSize setLocal(int local) {
        return setLocal(local, local, local);
    }
    public WorkSize setLocal(int x, int y, int z) {
        this.localX = clamp(x);
        this.localY = clamp(y);
        this.localZ = clamp(z);
        return this;
    }

    public WorkSize setGlobalX(int globalX) {
        this.globalX = clamp(globalX);
        return this;
    }
    public WorkSize setGlobalY(int globalY) {
        this.globalY = clamp(globalY);
        return this;
    }
    public WorkSize setGlobalZ(int globalZ) {
        this.globalZ = clamp(globalZ);
        return this;
    }
    public WorkSize setLocalX(int localX) {
        this.localX = clamp(localX);
        return this;
    }
    public WorkSize setLocalY(int localY) {
        this.localY = clamp(localY);
        return this;
    }
    public WorkSize setLocalZ(int localZ) {
        this.localZ = clamp(localZ);
        return this;
    }

    public WorkSize setX(int global, int local) {
        return setGlobalX(global).setLocalX(local);
    }
    public WorkSize setY(int global, int local) {
        return setGlobalY(global).setLocalY(local);
    }
    public WorkSize setZ(int global, int local) {
        return setGlobalZ(global).setLocalZ(local);
    }

    public WorkSize clear() {
        return set(1, 1, 1, 1, 1, 1);
    }
    public WorkSize clearX() {
        return setGlobalX(1).setLocalX(1);
    }
    public WorkSize clearY() {
        return setGlobalY(1).setLocalY(1);
    }
    public WorkSize clearZ() {
        return setGlobalZ(1).setLocalZ(1);
    }
    
    public WorkSize offloadToGlobal(int n) {
        return offloadToGlobal(n, n, n);
    }
    public WorkSize offloadToGlobal(int x, int y, int z) {
        globalX = clamp(globalX * x);
        globalY = clamp(globalY * y);
        globalZ = clamp(globalZ * x);
        localX = clamp(localX / x);
        localY = clamp(localY / y);
        localZ = clamp(localZ / z);
        return this;
    }
    public WorkSize shiftToGlobal(int n) {
        globalX = clamp(globalX << n);
        globalY = clamp(globalY << n);
        globalZ = clamp(globalZ << n);
        localX = clamp(localX >> n);
        localY = clamp(localY >> n);
        localZ = clamp(localZ >> n);
        return this;
    }
    public WorkSize offloadToLocal(int n) {
        return offloadToLocal(n, n, n);
    }
    public WorkSize offloadToLocal(int x, int y, int z) {
        globalX = clamp(globalX / x);
        globalY = clamp(globalY / y);
        globalZ = clamp(globalZ / x);
        localX = clamp(localX * x);
        localY = clamp(localY * y);
        localZ = clamp(localZ * z);
        return this;
    }
    public WorkSize shiftToLocal(int n) {
        globalX = clamp(globalX >> n);
        globalY = clamp(globalY >> n);
        globalZ = clamp(globalZ >> n);
        localX = clamp(localX << n);
        localY = clamp(localY << n);
        localZ = clamp(localZ << n);
        return this;
    }

    public int getGlobalX() {
        return globalX;
    }
    public int getGlobalY() {
        return globalY;
    }
    public int getGlobalZ() {
        return globalZ;
    }
    public int getLocalX() {
        return localX;
    }
    public int getLocalY() {
        return localY;
    }
    public int getLocalZ() {
        return localZ;
    }
    
    public int getNumWorkGroups() {
        return globalX * globalY * globalZ;
    }
    public int getWorkGroupVolume() {
        return localX * localY * localZ;
    }
    public int getNumInvocations() {
        return globalX * globalY * globalZ * localX * localY * localZ;
    }
    public int getNumGlobalDemensions() {
        if (globalY <= 1) {
            return 1;
        } else if (globalZ <= 1) {
            return 2;
        } else {
            return 3;
        }
    }
    public int getNumLocalDemensions() {
        if (localY <= 1) {
            return 1;
        } else if (localZ <= 1) {
            return 2;
        } else {
            return 3;
        }
    }
    
    public PointerBuffer fillGlobalBuffer(PointerBuffer buf) {
        return buf.put(globalX).put(globalY).put(globalZ);
    }
    public PointerBuffer fillLocalBuffer(PointerBuffer buf) {
        return buf.put(localX).put(localY).put(localZ);
    }

    public boolean equals(Object obj, boolean global, boolean local) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkSize other = (WorkSize) obj;
        if (global) {
            if (this.globalX != other.globalX) {
                return false;
            }
            if (this.globalY != other.globalY) {
                return false;
            }
            if (this.globalZ != other.globalZ) {
                return false;
            }
        }
        if (local) {
            if (this.localX != other.localX) {
                return false;
            }
            if (this.localY != other.localY) {
                return false;
            }
            return this.localZ == other.localZ;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "WorkSize[global=(" + globalX + ", " + globalY +
                ", " + globalZ + "); local=(" + localX + ", " + localY +
                ", " + localZ + ")]";
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.globalX;
        hash = 11 * hash + this.globalY;
        hash = 11 * hash + this.globalZ;
        hash = 11 * hash + this.localX;
        hash = 11 * hash + this.localY;
        hash = 11 * hash + this.localZ;
        return hash;
    }
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return equals(obj, true, true);
    }
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public WorkSize clone() {
        return new WorkSize(this);
    }
    
}
