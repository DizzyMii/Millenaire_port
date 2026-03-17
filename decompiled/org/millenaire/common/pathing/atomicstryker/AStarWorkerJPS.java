/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.PriorityQueue;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;
import org.millenaire.common.pathing.atomicstryker.AStarWorker;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class AStarWorkerJPS
extends AStarWorker {
    private static final int MAX_SKIP_DISTANCE = 25;
    private static final int[][] neighbourOffsets = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private final PriorityQueue<AStarNode> openQueue = new PriorityQueue();
    private AStarNode currentNode;

    public AStarWorkerJPS(AStarPathPlannerJPS creator) {
        super(creator);
    }

    private void addOrUpdateNode(AStarNode newNode) {
        boolean found = false;
        for (AStarNode toUpdate : this.closedNodes) {
            if (!newNode.equals(toUpdate)) continue;
            toUpdate.updateDistance(newNode.getG(), newNode.parent);
            found = true;
            break;
        }
        if (!found) {
            this.openQueue.offer(newNode);
        }
    }

    private ArrayList<AStarNode> backTrace(AStarNode start) throws ThreadSafeUtilities.ChunkAccessException {
        ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
        foundpath.add(this.currentNode);
        while (!this.currentNode.equals(start)) {
            int x = this.currentNode.x;
            int y = this.currentNode.y;
            int z = this.currentNode.z;
            int px = this.currentNode.parent.x;
            int pz = this.currentNode.parent.z;
            int dx = (px - x) / Math.max(Math.abs(x - px), 1);
            int dz = (pz - z) / Math.max(Math.abs(z - pz), 1);
            x += dx;
            z += dz;
            while (x != px || z != pz) {
                y = this.getGroundNodeHeight(x, y, z);
                foundpath.add(new AStarNode(x, y, z, 0, null));
                x += dx;
                z += dz;
            }
            foundpath.add(this.currentNode.parent);
            this.currentNode = this.currentNode.parent;
        }
        return foundpath;
    }

    private ArrayList<AStarNode> findNeighbours(AStarNode node) throws ThreadSafeUtilities.ChunkAccessException {
        ArrayList<AStarNode> r;
        block11: {
            int z;
            int y;
            int x;
            block9: {
                int nY;
                int dx;
                int dist;
                block14: {
                    boolean stairs;
                    block12: {
                        int nY2;
                        int dz;
                        block13: {
                            block10: {
                                r = new ArrayList<AStarNode>();
                                x = node.x;
                                y = node.y;
                                z = node.z;
                                dist = node.getG();
                                if (node.parent == null) break block9;
                                int px = node.parent.x;
                                int py = node.parent.y;
                                int pz = node.parent.z;
                                stairs = py != y;
                                dx = (x - px) / Math.max(Math.abs(x - px), 1);
                                dz = (z - pz) / Math.max(Math.abs(z - pz), 1);
                                if (dx == 0 || dz == 0) break block10;
                                if (stairs) {
                                    return this.getAllNeighborsWithoutParent(x, y, z, dx, dz, node);
                                }
                                int left = 0;
                                int right = 0;
                                int nY3 = this.getGroundNodeHeight(x, y, z + dz);
                                if (nY3 > 0) {
                                    left = nY3;
                                    r.add(new AStarNode(x, nY3, z + dz, dist + 1, node));
                                }
                                if ((nY3 = this.getGroundNodeHeight(x + dx, y, z)) > 0) {
                                    right = nY3;
                                    r.add(new AStarNode(x + dx, nY3, z, dist + 1, node));
                                }
                                if (left != 0 || right != 0) {
                                    r.add(new AStarNode(x + dx, Math.max(left, right), z + dz, dist + 2, node));
                                }
                                if (left != 0 && this.getGroundNodeHeight(x - dx, py, z) == 0) {
                                    r.add(new AStarNode(x - dx, left, z + dz, dist + 2, node));
                                }
                                if (right != 0 && this.getGroundNodeHeight(x, py, z - dz) == 0) {
                                    r.add(new AStarNode(x + dx, right, z - dz, dist + 2, node));
                                }
                                break block11;
                            }
                            if (dx != 0) break block12;
                            nY2 = this.getGroundNodeHeight(x, y, z + dz);
                            if (nY2 <= 0) break block11;
                            r.add(new AStarNode(x, nY2, z + dz, dist + 1, node));
                            if (!stairs) break block13;
                            r.add(new AStarNode(x + 1, nY2, z + dz, dist + 2, node));
                            r.add(new AStarNode(x - 1, nY2, z + dz, dist + 2, node));
                            break block11;
                        }
                        int nnY = this.getGroundNodeHeight(x + 1, nY2, z);
                        if (nnY == 0) {
                            r.add(new AStarNode(x + 1, nY2, z + dz, dist + 2, node));
                        }
                        if ((nnY = this.getGroundNodeHeight(x - 1, nY2, z)) != 0) break block11;
                        r.add(new AStarNode(x - 1, nY2, z + dz, dist + 2, node));
                        break block11;
                    }
                    nY = this.getGroundNodeHeight(x + dx, y, z);
                    if (nY <= 0) break block11;
                    r.add(new AStarNode(x + dx, nY, z, dist + 1, node));
                    if (!stairs) break block14;
                    r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node));
                    r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node));
                    break block11;
                }
                int nnY = this.getGroundNodeHeight(x, nY, z + 1);
                if (nnY == 0) {
                    r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node));
                }
                if ((nnY = this.getGroundNodeHeight(x, nY, z - 1)) != 0) break block11;
                r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node));
                break block11;
            }
            for (int[] offset : neighbourOffsets) {
                int nY = this.getGroundNodeHeight(x + offset[0], y, z + offset[1]);
                if (nY <= 0) continue;
                r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node));
            }
        }
        return r;
    }

    private ArrayList<AStarNode> getAllNeighborsWithoutParent(int x, int y, int z, int dx, int dz, AStarNode node) throws ThreadSafeUtilities.ChunkAccessException {
        ArrayList<AStarNode> r = new ArrayList<AStarNode>();
        for (int[] offset : neighbourOffsets) {
            int nY;
            if (offset[0] == -dx && offset[1] == -dz || (nY = this.getGroundNodeHeight(x + offset[0], y, z + offset[1])) <= 0) continue;
            r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node));
        }
        return r;
    }

    private int getGroundNodeHeight(int xN, int yN, int zN) throws ThreadSafeUtilities.ChunkAccessException {
        if (AStarStatic.isViable(this.world, xN, yN, zN, 0, this.config)) {
            return yN;
        }
        if (AStarStatic.isViable(this.world, xN, yN - 1, zN, -1, this.config)) {
            return yN - 1;
        }
        if (AStarStatic.isViable(this.world, xN, yN + 1, zN, 1, this.config)) {
            return yN + 1;
        }
        return 0;
    }

    @Override
    public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end, boolean searchMode) throws ThreadSafeUtilities.ChunkAccessException {
        this.openQueue.offer(start);
        this.targetNode = end;
        this.currentNode = start;
        int nbLoop = 0;
        while (!this.openQueue.isEmpty() && !this.shouldInterrupt()) {
            this.currentNode = this.openQueue.poll();
            this.closedNodes.add(this.currentNode);
            if (this.isNodeEnd(this.currentNode, end) || this.identifySuccessors(this.currentNode, nbLoop)) {
                return this.backTrace(start);
            }
            ++nbLoop;
        }
        return null;
    }

    private boolean identifySuccessors(AStarNode node, int nbLoop) throws ThreadSafeUtilities.ChunkAccessException {
        int x = node.x;
        int y = node.y;
        int z = node.z;
        ArrayList<AStarNode> successors = this.findNeighbours(node);
        for (AStarNode s : successors) {
            AStarNode jumpPoint = this.jump(s.x, s.y, s.z, x, y, z);
            if (jumpPoint == null || this.closedNodes.contains(jumpPoint)) continue;
            this.addOrUpdateNode(jumpPoint);
        }
        if (nbLoop == 0 && this.openQueue.isEmpty() && MillConfigValues.LogChunkLoader >= 1) {
            MillLog.major(this, "Failed on first loop. Neighbours: " + successors.toArray());
        }
        return false;
    }

    private AStarNode jump(int nextX, int nextY, int nextZ, int px, int py, int pz) throws ThreadSafeUtilities.ChunkAccessException {
        int nzY;
        int x = nextX;
        int y = nextY;
        int z = nextZ;
        int dist = this.currentNode.getG() + Math.abs(x - this.currentNode.x) + Math.abs(y - this.currentNode.y) + Math.abs(z - this.currentNode.z);
        int dx = x - px;
        int dz = z - pz;
        py = y;
        if ((y = this.getGroundNodeHeight(x, py, z)) == 0) {
            return null;
        }
        if (this.isCoordsEnd(x, y, z, this.targetNode) || dist >= 25) {
            return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode);
        }
        int nxY = dx != 0 ? this.getGroundNodeHeight(x + dx, y, z) : 0;
        int n = nzY = dz != 0 ? this.getGroundNodeHeight(x, y, z + dz) : 0;
        if (dx != 0 && dz != 0 ? this.getGroundNodeHeight(x - dx, y, z + dz) != 0 && this.getGroundNodeHeight(x - dx, py, z) == 0 || this.getGroundNodeHeight(x + dx, y, z - dz) != 0 && this.getGroundNodeHeight(x, py, z - dz) == 0 : (dx != 0 ? nxY != y || this.getGroundNodeHeight(x, y, z + 1) == 0 && this.getGroundNodeHeight(x + dx, nxY, z + 1) != 0 || this.getGroundNodeHeight(x, y, z - 1) == 0 && this.getGroundNodeHeight(x + dx, nxY, z - 1) != 0 : nzY != y || this.getGroundNodeHeight(x + 1, y, z) == 0 && this.getGroundNodeHeight(x + 1, nzY, z + dz) != 0 || this.getGroundNodeHeight(x - 1, y, z) == 0 && this.getGroundNodeHeight(x - 1, nzY, z + dz) != 0)) {
            return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode);
        }
        if (dx != 0 && dz != 0) {
            AStarNode jx = this.jump(x + dx, y, z, x, y, z);
            AStarNode jy = this.jump(x, y, z + dz, x, y, z);
            if (jx != null || jy != null) {
                return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode);
            }
        }
        if (nxY != 0 || nzY != 0) {
            return this.jump(x + dx, y, z + dz, x, y, z);
        }
        return null;
    }
}

