package com.drstudio.psmenus.hooks;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ProtectionStonesHook {

    public PSRegion getRegionAt(Location location) {
        return PSRegion.fromLocation(location);
    }

    public List<PSRegion> getPlayerRegions(Player player) {
        return ProtectionStones.getPSRegions(player.getWorld(), player.getUniqueId());
    }

    public boolean setFlag(PSRegion region, String flagName, String value) {
        if (region == null) return false;
        region.setFlag(flagName, value);
        region.save();
        return true;
    }

    public String getFlag(PSRegion region, String flagName) {
        if (region == null) return null;
        return region.getFlag(flagName);
    }

    public boolean addMember(PSRegion region, UUID uuid) {
        if (region == null) return false;
        region.getMembers().add(uuid);
        region.save();
        return true;
    }

    public boolean removeMember(PSRegion region, UUID uuid) {
        if (region == null) return false;
        region.getMembers().remove(uuid);
        region.save();
        return true;
    }
}
