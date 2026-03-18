package dev.rotator.pvplabs.game.behaviors;

import dev.rotator.pvplabs.game.GameBehavior;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class NaturalRegenToggleBehavior implements GameBehavior {
    private boolean allowNaturalRegeneration;

    public NaturalRegenToggleBehavior(boolean allowNaturalRegeneration) {
        this.allowNaturalRegeneration = allowNaturalRegeneration;
    }

    public void setAllowNaturalRegeneration(boolean allowNaturalRegeneration) {
        this.allowNaturalRegeneration = allowNaturalRegeneration;
    }

    @Override
    public boolean onRegenerate(Player p, EntityRegainHealthEvent e) {
        if (allowNaturalRegeneration) return true;
        EntityRegainHealthEvent.RegainReason reason = e.getRegainReason();
        return !reason.equals(EntityRegainHealthEvent.RegainReason.SATIATED)
                && !reason.equals(EntityRegainHealthEvent.RegainReason.REGEN);
    }
}
