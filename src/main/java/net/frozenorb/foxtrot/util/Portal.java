package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.server.listener.impl.PortalTrapListener;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Portal {

    private PortalDirection direction;
    private Cuboid portal;

    public Portal(Block block, PortalDirection direction) {
        if (block.getType() != Material.NETHER_PORTAL) {
            return;
        }
        this.direction = direction;

        if (direction == null) {
            return;
        }

        if (direction == PortalDirection.NORTH_SOUTH) {

            int minX = block.getX();
            int maxX = block.getX();
            int minY = block.getY() - 1;

            int maxZ = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // z++
                Block next = block.getRelative(BlockFace.SOUTH, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    maxZ = next.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getZ();
                    maxZ = maxZ < 0 ? maxZ + 1 : maxZ - 1;
                    break;
                }
            }

            int minZ = Integer.MAX_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // z--
                Block next = block.getRelative(BlockFace.NORTH, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    minZ = next.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getZ();
                    minZ = minZ < 0 ? minZ - 1 : minZ + 1;
                    break;
                }
            }

            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // y++
                Block next = block.getRelative(BlockFace.UP, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    maxY = next.getY();
                    break;
                }
            }

            this.portal = new Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            int minZ = block.getZ();
            int maxZ = block.getZ();
            int minY = block.getY() - 1;

            int maxX = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // x++
                Block next = block.getRelative(BlockFace.EAST, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    maxX = next.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getX();
                    maxX = maxX < 0 ? maxX + 1 : maxX - 1;
                    break;
                }
            }

            int minX = Integer.MAX_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // x--
                Block next = block.getRelative(BlockFace.WEST, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    minX = next.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getX();
                    minX = minX < 0 ? minX - 1 : minX + 1;
                    break;
                }
            }

            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // y++
                Block next = block.getRelative(BlockFace.UP, i);
                if (next.getType() != Material.NETHER_PORTAL) {
                    maxY = next.getY();
                    break;
                }
            }

            this.portal = new Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    public void show(Cuboid cuboid) {
        for (Block block : cuboid.corners()) {
            block.setType(Material.STONE);
        }
    }

    public void patchOverworld() {
        Cuboid expanded;
        if (direction == PortalDirection.NORTH_SOUTH) {
            expanded = portal.expand(Cuboid.CuboidDirection.South, 1).expand(Cuboid.CuboidDirection.North, 1);
        } else {
            expanded = portal.expand(Cuboid.CuboidDirection.East, 1).expand(Cuboid.CuboidDirection.West, 1);
        }

        first:
        for (Block block : expanded) {
            if (!portal.contains(block)) {
                for (BlockFace f : PortalTrapListener.FACES) {
                    Block relative = block.getRelative(f);
                    if (relative.getType() == Material.NETHER_PORTAL) {
                        if (block.getType() != Material.AIR) {
                            Claim bClaim = LandBoard.getInstance().getClaim(block.getLocation());

                            if (bClaim == null || bClaim.contains(relative)) {
                                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
                            }

                            continue first;
                        }
                    }
                }
            }
        }
    }

    public void patchNether() {
        Cuboid expanded;
        if (direction == PortalDirection.NORTH_SOUTH) {
            expanded = portal.expand(Cuboid.CuboidDirection.South, 4).expand(Cuboid.CuboidDirection.North, 4).expand(Cuboid.CuboidDirection.East, 4).expand(Cuboid.CuboidDirection.West, 4);
        } else {
            expanded = portal.expand(Cuboid.CuboidDirection.East, 4).expand(Cuboid.CuboidDirection.West, 4).expand(Cuboid.CuboidDirection.South, 4).expand(Cuboid.CuboidDirection.North, 4);
        }
        Cuboid down = expanded.getFace(Cuboid.CuboidDirection.Down);

        for (Block block : expanded) {
            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }

            if (!portal.contains(block) && block.getType() != Material.AIR & !down.contains(block)) {
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
            }
        }

        for (Block block : down) {
            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }
            if (block.getType() == Material.OBSIDIAN) {
                continue;
            }

            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
        }
    }
}
