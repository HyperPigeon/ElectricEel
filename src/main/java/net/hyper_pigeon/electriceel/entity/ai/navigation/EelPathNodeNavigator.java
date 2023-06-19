package net.hyper_pigeon.electriceel.entity.ai.navigation;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EelPathNodeNavigator extends PathNodeNavigator {

    public EelPathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        super(pathNodeMaker, range);
    }

    @Nullable
    @Override
    public Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {

        Path path = super.findPathToAny(world, mob, positions, followRange, distance, rangeMultiplier);

        System.out.println(path.getManhattanDistanceFromTarget());
        if(path != null && path.getManhattanDistanceFromTarget() >= 2) {
            path = getBezierPath(path);
        }
        return path;
    }

    public BlockPos calculateBezierBlockPos(BlockPos blockPos1, BlockPos blockPos2, BlockPos blockPos3, BlockPos blockPos4, double t){
        int x = (int) (blockPos1.getX() + t*(-3*blockPos1.getX() + 3*blockPos2.getX()) +
                (t*t)*(3*blockPos1.getX() - 6*blockPos2.getX() + 3*blockPos3.getX()) +
                (t*t*t)*(-blockPos1.getX() + 3*blockPos2.getX() - 3*blockPos3.getX() + blockPos4.getX()));
        int y = (int) (blockPos1.getY() + t*(-3*blockPos1.getY() + 3*blockPos2.getY()) +
                (t*t)*(3*blockPos1.getY() - 6*blockPos2.getY() + 3*blockPos3.getY()) +
                (t*t*t)*(-blockPos1.getY() + 3*blockPos2.getY() - 3*blockPos3.getY() + blockPos4.getY()));
        int z = (int) (blockPos1.getZ() + t*(-3*blockPos1.getZ() + 3*blockPos2.getZ()) +
                (t*t)*(3*blockPos1.getZ() - 6*blockPos2.getZ() + 3*blockPos3.getZ()) +
                (t*t*t)*(-blockPos1.getZ() + 3*blockPos2.getZ() - 3*blockPos3.getZ() + blockPos4.getZ()));

        return new BlockPos(x,y,z);
    }

    public Set<BlockPos> getBezierPathPositions(Set<BlockPos> blockPosSet){
        int n = blockPosSet.size();
        Set<BlockPos> splineSet = new HashSet<>();

        BlockPos[] blockPosArray = blockPosSet.toArray(new BlockPos[0]);


        for(int i = 0; i < n-4; i += 3){
            BlockPos pos1 = blockPosArray[i];
            BlockPos pos2 = blockPosArray[i+1];
            BlockPos pos3 = blockPosArray[i+2];
            BlockPos pos4 = blockPosArray[i+3];

            for(double t = 0; t <= 1; t += 0.05) {
                BlockPos newPos = calculateBezierBlockPos(pos1, pos2, pos3, pos4, t);
                splineSet.add(newPos);
            }

        }

        return splineSet;
    }



    public PathNode calculateBezierPathNode(PathNode pathNode1, PathNode pathNode2, PathNode pathNode3, PathNode pathNode4, double t){
        int x = (int) (pathNode1.x + t*(-3*pathNode1.x + 3*pathNode2.x) +
                (t*t)*(3*pathNode1.x - 6*pathNode2.x + 3*pathNode3.x) +
                (t*t*t)*(-pathNode1.x + 3*pathNode2.x - 3*pathNode3.x + pathNode4.x));
        int y = (int) (pathNode1.y + t*(-3*pathNode1.y + 3*pathNode2.y) +
                (t*t)*(3*pathNode1.y - 6*pathNode2.y + 3*pathNode3.y) +
                (t*t*t)*(-pathNode1.y + 3*pathNode2.y - 3*pathNode3.y + pathNode4.y));
        int z = (int) (pathNode1.z + t*(-3*pathNode1.z + 3*pathNode2.z) +
                (t*t)*(3*pathNode1.z - 6*pathNode2.z + 3*pathNode3.z) +
                (t*t*t)*(-pathNode1.z + 3*pathNode2.z - 3*pathNode3.z + pathNode4.z));

        return new PathNode(x,y,z);
    }

    public Path getBezierPath(Path path){
        int n = path.getLength();

        PathNode startNode = path.getNode(0);
        PathNode currentNode = startNode;


        List<PathNode> pathNodeList = new ArrayList<>();

        for(int i = 0; i < n-4; i += 3){
            PathNode pathNode1 = path.getNode(i);
            PathNode pathNode2 = path.getNode(i+1);
            PathNode pathNode3 = path.getNode(i+2);
            PathNode pathNode4 = path.getNode(i+3);

            for(double t = 0.05; t <= 1; t += 0.05) {
                PathNode pathNode = calculateBezierPathNode(pathNode1, pathNode2, pathNode3, pathNode4, t);
                pathNode.previous = currentNode;
                currentNode = pathNode;
                pathNodeList.add(pathNode);
            }

        }

        return new Path(pathNodeList, path.getTarget(), path.reachesTarget());
    }



}
