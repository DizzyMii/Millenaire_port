/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 */
package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;
import org.millenaire.common.pathing.atomicstryker.AStarWorker;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.village.VillageMapInfo;

public class RegionMapper {
    private static final int MIN_SIZE_FOR_REGION_BRIDGING = 200;
    private static final AStarConfig JPS_CONFIG = new AStarConfig(true, false, false, false, true);
    public VillageMapInfo winfo;
    public boolean[][] top;
    public boolean[][] bottom;
    public boolean[][] left;
    public boolean[][] right;
    public short[][] topGround;
    public short[][] regions;
    public short thRegion;
    public List<Node> nodes;

    private int boolDisplay(boolean a, boolean b, boolean c, boolean d) {
        int i = a ? 1 : 0;
        i += b ? 2 : 0;
        i += c ? 4 : 0;
        return i += d ? 8 : 0;
    }

    private void buildNodes() {
        for (int i = 0; i < this.winfo.length; ++i) {
            for (int j = 0; j < this.winfo.width; ++j) {
                boolean isNode = false;
                int cornerSide = 0;
                if (i > 0 && j > 0 && this.top[i][j] && this.left[i][j] && (!this.left[i - 1][j] || !this.top[i][j - 1])) {
                    isNode = true;
                    cornerSide |= 1;
                }
                if (i < this.winfo.length - 1 && j > 0 && this.bottom[i][j] && this.left[i][j] && (!this.left[i + 1][j] || !this.bottom[i][j - 1])) {
                    isNode = true;
                    cornerSide += 2;
                    cornerSide |= 2;
                }
                if (i > 0 && j < this.winfo.width - 1 && this.top[i][j] && this.right[i][j] && (!this.right[i - 1][j] || !this.top[i][j + 1])) {
                    isNode = true;
                    cornerSide |= 4;
                }
                if (i < this.winfo.length - 1 && j < this.winfo.width - 1 && this.bottom[i][j] && this.right[i][j] && (!this.right[i + 1][j] || !this.bottom[i][j + 1])) {
                    isNode = true;
                    cornerSide |= 8;
                }
                if (!isNode) continue;
                this.nodes.add(new Node(new Point2D(i, j), this.nodes.size(), cornerSide, false));
            }
        }
        for (Node n : this.nodes) {
            int tz;
            int tx;
            if (n.cornerSide == 1 && n.pos.x < this.winfo.length - 1 && n.pos.z < this.winfo.width - 1 && this.bottom[n.pos.x][n.pos.z] && this.right[n.pos.x][n.pos.z] && this.bottom[n.pos.x][n.pos.z + 1] && this.right[n.pos.x + 1][n.pos.z]) {
                tx = n.pos.x + 1;
                tz = n.pos.z + 1;
                if (tx < this.winfo.length - 1 && tz < this.winfo.width - 1 && this.bottom[tx][tz] && this.right[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 2 && n.pos.x > 0 && n.pos.z < this.winfo.width - 1 && this.top[n.pos.x][n.pos.z] && this.right[n.pos.x][n.pos.z] && this.top[n.pos.x][n.pos.z + 1] && this.right[n.pos.x - 1][n.pos.z]) {
                tx = n.pos.x - 1;
                tz = n.pos.z + 1;
                if (tx > 0 && tz < this.winfo.width - 1 && this.top[tx][tz] && this.right[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 4 && n.pos.x < this.winfo.length - 1 && n.pos.z > 0 && this.bottom[n.pos.x][n.pos.z] && this.left[n.pos.x][n.pos.z] && this.bottom[n.pos.x][n.pos.z - 1] && this.left[n.pos.x + 1][n.pos.z]) {
                tx = n.pos.x + 1;
                tz = n.pos.z - 1;
                if (tx < this.winfo.length - 1 && tz > 0 && this.bottom[tx][tz] && this.left[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 8 && n.pos.x > 0 && n.pos.z > 0 && this.top[n.pos.x][n.pos.z] && this.left[n.pos.x][n.pos.z] && this.top[n.pos.x][n.pos.z - 1] && this.left[n.pos.x - 1][n.pos.z]) {
                tx = n.pos.x - 1;
                tz = n.pos.z - 1;
                if (tx > 0 && tz > 0 && this.top[tx][tz] && this.left[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 3 && n.pos.z < this.winfo.width - 1 && this.right[n.pos.x][n.pos.z]) {
                tx = n.pos.x;
                tz = n.pos.z + 1;
                if (tz < this.winfo.width - 1 && this.bottom[tx][tz] && this.right[tx][tz] && this.top[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 5 && n.pos.x < this.winfo.length - 1 && this.bottom[n.pos.x][n.pos.z]) {
                tx = n.pos.x + 1;
                tz = n.pos.z;
                if (tx < this.winfo.length - 1 && this.bottom[tx][tz] && this.right[tx][tz] && this.left[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide == 10 && n.pos.x > 0 && this.top[n.pos.x][n.pos.z]) {
                tx = n.pos.x - 1;
                tz = n.pos.z;
                if (tx > 0 && this.top[tx][tz] && this.right[tx][tz] && this.left[tx][tz]) {
                    n.pos.x = tx;
                    n.pos.z = tz;
                }
            }
            if (n.cornerSide != 12 || n.pos.z <= 0 || !this.left[n.pos.x][n.pos.z]) continue;
            tx = n.pos.x;
            tz = n.pos.z - 1;
            if (tx <= 0 || !this.top[tx][tz] || !this.bottom[tx][tz] || !this.left[tx][tz]) continue;
            n.pos.x = tx;
            n.pos.z = tz;
        }
        block3: for (int i = this.nodes.size() - 1; i > -1; --i) {
            for (int j = i - 1; j > -1; --j) {
                if (!this.nodes.get(i).equals(this.nodes.get(j))) continue;
                this.nodes.remove(i);
                continue block3;
            }
        }
    }

    public boolean canSee(Point2D p1, Point2D p2) {
        int xdist = p2.x - p1.x;
        int zdist = p2.z - p1.z;
        if (xdist == 0 && zdist == 0) {
            return true;
        }
        int xsign = 1;
        int zsign = 1;
        if (xdist < 0) {
            xsign = -1;
        }
        if (zdist < 0) {
            zsign = -1;
        }
        int x = p1.x;
        int z = p1.z;
        int xdone = 0;
        int zdone = 0;
        while (x != p2.x || z != p2.z) {
            int nx;
            int nz;
            if (xdist == 0 || zdist != 0 && xdone * 1000 / xdist > zdone * 1000 / zdist) {
                nz = z + zsign;
                nx = x;
                zdone += zsign;
                if (zsign == 1 && !this.right[x][z]) {
                    return false;
                }
                if (zsign == -1 && !this.left[x][z]) {
                    return false;
                }
            } else {
                nx = x + xsign;
                nz = z;
                xdone += xsign;
                if (xsign == 1 && !this.bottom[x][z]) {
                    return false;
                }
                if (xsign == -1 && !this.top[x][z]) {
                    return false;
                }
            }
            x = nx;
            z = nz;
        }
        return true;
    }

    public boolean createConnectionsTable(VillageMapInfo winfo, Point thStanding) throws MillLog.MillenaireException {
        long startTime;
        long totalStartTime = startTime = System.nanoTime();
        this.winfo = winfo;
        this.top = new boolean[winfo.length][winfo.width];
        this.bottom = new boolean[winfo.length][winfo.width];
        this.left = new boolean[winfo.length][winfo.width];
        this.right = new boolean[winfo.length][winfo.width];
        this.regions = new short[winfo.length][winfo.width];
        this.topGround = VillageMapInfo.shortArrayDeepClone(winfo.topGround);
        this.nodes = new ArrayList<Node>();
        for (int i = 0; i < winfo.length; ++i) {
            for (int j = 0; j < winfo.width; ++j) {
                boolean connected;
                short nspace;
                short ny;
                short y = winfo.topGround[i][j];
                short space = winfo.spaceAbove[i][j];
                if (winfo.danger[i][j] || winfo.water[i][j] || space <= 1) continue;
                if (i > 0) {
                    ny = winfo.topGround[i - 1][j];
                    nspace = winfo.spaceAbove[i - 1][j];
                    connected = false;
                    if (ny == y && nspace > 1) {
                        connected = true;
                    } else if (ny == y - 1 && nspace > 2) {
                        connected = true;
                    } else if (ny == y + 1 && nspace > 1 && space > 2) {
                        connected = true;
                    }
                    if (connected) {
                        this.top[i][j] = true;
                        this.bottom[i - 1][j] = true;
                    }
                }
                if (j <= 0) continue;
                ny = winfo.topGround[i][j - 1];
                nspace = winfo.spaceAbove[i][j - 1];
                connected = false;
                if (ny == y && nspace > 1) {
                    connected = true;
                } else if (ny == y - 1 && nspace > 2) {
                    connected = true;
                } else if (ny == y + 1 && nspace > 1 && space > 2) {
                    connected = true;
                }
                if (!connected) continue;
                this.left[i][j] = true;
                this.right[i][j - 1] = true;
            }
        }
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, "Time taken for connection building: " + (double)(System.nanoTime() - startTime) / 1000000.0);
        }
        startTime = System.nanoTime();
        this.buildNodes();
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, "Time taken for nodes finding: " + (double)(System.nanoTime() - startTime) / 1000000.0);
        }
        startTime = System.nanoTime();
        for (Node n : this.nodes) {
            for (Node n2 : this.nodes) {
                if (n.id >= n2.id || !this.canSee(n.pos, n2.pos)) continue;
                Integer distance = n.pos.distanceTo(n2.pos);
                n.costs.put(n2, distance);
                n.neighbours.add(n2);
                n2.costs.put(n, distance);
                n2.neighbours.add(n);
            }
        }
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, "Time taken for nodes linking: " + (double)(System.nanoTime() - startTime) / 1000000.0);
        }
        startTime = System.nanoTime();
        this.findRegions(thStanding);
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, "Time taken for group finding: " + (double)(System.nanoTime() - startTime) / 1000000.0);
        }
        if (MillConfigValues.LogConnections >= 1) {
            MillLog.major(this, "Node graph complete. Size: " + this.nodes.size() + " Time taken: " + (double)(System.nanoTime() - totalStartTime) / 1000000.0);
        }
        if (MillConfigValues.LogConnections >= 3 && MillConfigValues.DEV) {
            MillLog.major(this, "Calling displayConnectionsLog");
            this.displayConnectionsLog();
        }
        return true;
    }

    private void displayConnectionsLog() {
        int j;
        int i;
        int j2;
        long startTime = System.nanoTime();
        MillLog.minor(this, "Connections:");
        String s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + MathHelper.func_76141_d((float)(j2 / 10)) % 10;
        }
        MillLog.minor(this, s);
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + j2 % 10;
        }
        MillLog.minor(this, s);
        for (i = 0; i < this.winfo.length; ++i) {
            s = i < 10 ? i + "   " : (i < 100 ? i + "  " : i + " ");
            for (j = 0; j < this.winfo.width; ++j) {
                s = s + Integer.toHexString(this.boolDisplay(this.top[i][j], this.left[i][j], this.bottom[i][j], this.right[i][j]));
            }
            s = i < 10 ? s + "   " + i : (i < 100 ? s + "  " + i : s + " " + i);
            MillLog.minor(this, s);
        }
        MillLog.minor(this, "spaceAbove:");
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + MathHelper.func_76141_d((float)(j2 / 10)) % 10;
        }
        MillLog.minor(this, s);
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + j2 % 10;
        }
        MillLog.minor(this, s);
        for (i = 0; i < this.winfo.length; ++i) {
            s = i < 10 ? i + "   " : (i < 100 ? i + "  " : i + " ");
            for (j = 0; j < this.winfo.width; ++j) {
                s = s + this.winfo.spaceAbove[i][j];
            }
            s = i < 10 ? s + "   " + i : (i < 100 ? s + "  " + i : s + " " + i);
            MillLog.minor(this, s);
        }
        MillLog.minor(this, "Y pos:");
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + MathHelper.func_76141_d((float)(j2 / 10)) % 10;
        }
        MillLog.minor(this, s);
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + j2 % 10;
        }
        MillLog.minor(this, s);
        for (i = 0; i < this.winfo.length; ++i) {
            s = i < 10 ? i + "   " : (i < 100 ? i + "  " : i + " ");
            for (j = 0; j < this.winfo.width; ++j) {
                s = s + this.winfo.topGround[i][j] % 10;
            }
            s = i < 10 ? s + "   " + i : (i < 100 ? s + "  " + i : s + " " + i);
            MillLog.minor(this, s);
        }
        MillLog.minor(this, "Nodes:");
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + MathHelper.func_76141_d((float)(j2 / 10)) % 10;
        }
        MillLog.minor(this, s);
        s = "    ";
        for (j2 = 0; j2 < this.winfo.width; ++j2) {
            s = s + j2 % 10;
        }
        MillLog.minor(this, s);
        for (i = 0; i < this.winfo.length; ++i) {
            s = i < 10 ? i + "   " : (i < 100 ? i + "  " : i + " ");
            for (j = 0; j < this.winfo.width; ++j) {
                boolean found = false;
                for (Node n : this.nodes) {
                    if (n.pos.x != i || n.pos.z != j) continue;
                    s = s + Integer.toHexString(n.id % 10);
                    found = true;
                }
                if (found) continue;
                s = !this.top[i][j] && !this.bottom[i][j] && !this.left[i][j] && !this.right[i][j] ? s + "#" : (!this.top[i][j] || !this.bottom[i][j] || !this.left[i][j] || !this.right[i][j] ? s + "." : s + " ");
            }
            s = i < 10 ? s + "   " + i : (i < 100 ? s + "  " + i : s + " " + i);
            MillLog.minor(this, s);
        }
        MillLog.minor(this, "Displaying connections finished. Time taken: " + (double)(System.nanoTime() - startTime) / 1000000.0);
    }

    private void findRegions(Point thStanding) throws MillLog.MillenaireException {
        int i;
        int nodesMarked = 0;
        int nodeGroup = 0;
        while (nodesMarked < this.nodes.size()) {
            ++nodeGroup;
            ArrayList<Node> toVisit = new ArrayList<Node>();
            Node fn = null;
            int i2 = 0;
            while (fn == null) {
                if (this.nodes.get((int)i2).region == 0) {
                    fn = this.nodes.get(i2);
                }
                ++i2;
            }
            fn.region = nodeGroup;
            ++nodesMarked;
            toVisit.add(fn);
            while (toVisit.size() > 0) {
                for (Node n : ((Node)toVisit.get((int)0)).neighbours) {
                    if (n.region == 0) {
                        n.region = nodeGroup;
                        toVisit.add(n);
                        ++nodesMarked;
                        continue;
                    }
                    if (n.region == nodeGroup) continue;
                    throw new MillLog.MillenaireException("Node belongs to group " + n.region + " but reached from " + nodeGroup);
                }
                toVisit.remove(0);
            }
        }
        for (int i3 = 0; i3 < this.winfo.length; ++i3) {
            for (int j = 0; j < this.winfo.width; ++j) {
                this.regions[i3][j] = -1;
            }
        }
        for (Node n : this.nodes) {
            this.regions[n.pos.x][n.pos.z] = (short)n.region;
        }
        boolean spreaddone = true;
        while (spreaddone) {
            spreaddone = false;
            for (int i4 = 0; i4 < this.winfo.length; ++i4) {
                for (int j = 0; j < this.winfo.width; ++j) {
                    if (this.regions[i4][j] <= 0) continue;
                    short regionid = this.regions[i4][j];
                    int x = i4;
                    while (x > 1 && this.top[x][j] && this.regions[x - 1][j] == -1) {
                        this.regions[--x][j] = regionid;
                        spreaddone = true;
                    }
                    x = i4;
                    while (x < this.winfo.length - 1 && this.bottom[x][j] && this.regions[x + 1][j] == -1) {
                        this.regions[++x][j] = regionid;
                        spreaddone = true;
                    }
                    x = j;
                    while (x > 1 && this.left[i4][x] && this.regions[i4][x - 1] == -1) {
                        this.regions[i4][--x] = regionid;
                        spreaddone = true;
                    }
                    x = j;
                    while (x < this.winfo.width - 1 && this.right[i4][x] && this.regions[i4][x + 1] == -1) {
                        this.regions[i4][++x] = regionid;
                        spreaddone = true;
                    }
                }
            }
        }
        this.thRegion = this.regions[thStanding.getiX() - this.winfo.mapStartX][thStanding.getiZ() - this.winfo.mapStartZ];
        long startTime = System.nanoTime();
        int maxRegionId = -1;
        for (Node n : this.nodes) {
            if (n.region <= maxRegionId) continue;
            maxRegionId = n.region;
        }
        int[] regionsSize = new int[maxRegionId + 1];
        Point2D[] pointsInRegion = new Point2D[maxRegionId + 1];
        for (i = 0; i <= maxRegionId; ++i) {
            regionsSize[i] = 0;
        }
        for (i = 0; i < this.winfo.length; ++i) {
            for (int j = 0; j < this.winfo.width; ++j) {
                if (this.regions[i][j] <= -1) continue;
                regionsSize[this.regions[i][j]] = regionsSize[this.regions[i][j]] + 1;
            }
        }
        for (Node n : this.nodes) {
            pointsInRegion[n.region] = n.pos;
        }
        for (int i5 = 0; i5 <= maxRegionId; ++i5) {
            if (regionsSize[i5] <= 200 || i5 == this.thRegion) continue;
            try {
                Point targetPoint = new Point(pointsInRegion[i5].x + this.winfo.mapStartX, this.winfo.topGround[pointsInRegion[i5].x][pointsInRegion[i5].z] - 1, pointsInRegion[i5].z + this.winfo.mapStartZ);
                ArrayList<AStarNode> path = this.getPath(thStanding.getiX(), thStanding.getiY(), thStanding.getiZ(), targetPoint.getiX(), targetPoint.getiY() + 1, targetPoint.getiZ());
                if (path == null) continue;
                for (int x = 0; x < this.winfo.length; ++x) {
                    for (int z = 0; z < this.winfo.width; ++z) {
                        if (this.regions[x][z] != i5) continue;
                        this.regions[x][z] = this.thRegion;
                    }
                }
                continue;
            }
            catch (ThreadSafeUtilities.ChunkAccessException e) {
                if (MillConfigValues.LogChunkLoader < 1) continue;
                MillLog.major(this, e.getMessage());
            }
        }
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, "Time taken for region bridging: " + (double)(System.nanoTime() - startTime) / 1000000.0);
        }
        if (MillConfigValues.LogConnections >= 2) {
            MillLog.minor(this, nodeGroup + " node groups found.");
        }
    }

    private ArrayList<AStarNode> getPath(int startx, int starty, int startz, int destx, int desty, int destz) throws ThreadSafeUtilities.ChunkAccessException {
        if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG)) {
            --starty;
        }
        if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG)) {
            starty += 2;
        }
        if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG)) {
            --starty;
        }
        AStarNode starter = new AStarNode(startx, starty, startz, 0, null);
        AStarNode finish = new AStarNode(destx, desty, destz, -1, null);
        AStarWorker pathWorker = new AStarWorker();
        pathWorker.setup(this.winfo.world, starter, finish, JPS_CONFIG);
        return pathWorker.runSync();
    }

    public boolean isInArea(Point p) {
        return !(p.x < (double)this.winfo.mapStartX || p.x >= (double)(this.winfo.mapStartX + this.winfo.length) || p.z < (double)this.winfo.mapStartZ || p.z >= (double)(this.winfo.mapStartZ + this.winfo.width));
    }

    public boolean isValidPoint(Point p) {
        if (!this.isInArea(p)) {
            return false;
        }
        return this.winfo.spaceAbove[p.getiX() - this.winfo.mapStartX][p.getiZ() - this.winfo.mapStartZ] > 1;
    }

    public static class Point2D {
        int x;
        int z;

        public Point2D(int px, int pz) {
            this.x = px;
            this.z = pz;
        }

        public int distanceTo(Point2D p) {
            int d = p.x - this.x;
            int d1 = p.z - this.z;
            return (int)Math.sqrt(d * d + d1 * d1);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Point2D)) {
                return false;
            }
            Point2D p = (Point2D)obj;
            return this.x == p.x && this.z == p.z;
        }

        public int hashCode() {
            return this.x << 16 & this.z;
        }

        public String toString() {
            return this.x + "/" + this.z;
        }
    }

    private static class Node {
        Point2D pos;
        List<Node> neighbours;
        HashMap<Node, Integer> costs;
        int id;
        int fromDist;
        int toDist;
        int cornerSide;
        int region = 0;

        public Node(Point2D p, int pid, int cornerSide, boolean ptemp) {
            this.pos = p;
            this.id = pid;
            this.cornerSide = cornerSide;
            this.neighbours = new ArrayList<Node>();
            this.costs = new HashMap();
        }

        public boolean equals(Object obj) {
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            Node n = (Node)obj;
            return n.hashCode() == this.hashCode();
        }

        public int hashCode() {
            return this.pos.x + (this.pos.z << 16);
        }

        public String toString() {
            return "Node " + this.id + ": " + this.pos + " group: " + this.region + " neighbours: " + this.neighbours.size() + "(fromDist: " + this.fromDist + ", toDist: " + this.toDist + ")";
        }
    }
}

