package nickguletskii200;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
/**
 * Listen for entity events.
 * 
 * @author nickguletskii200
 */
public class MobListener extends EntityListener {
	private SpyerFun sa;

	public MobListener(SpyerFun _sa) {
		sa = _sa;
	}

	public void onEntityTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player)
				|| !(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		LivingEntity mob = (LivingEntity) event.getEntity();
		Player target = (Player) event.getTarget();
		if (isMonster(mob)) {
			if (sa.getPlayerListener().commonPlayers.contains(target.getName())
					&& !sa.getSettings().isVisibleByMobs(target.getName())) {
				event.setCancelled(true);
			}
		}
	}

	public void onEntityDamage(EntityDamageEvent event) {
		if (!sa.getSettings().onDamage) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {
			Entity ent = ((EntityDamageByEntityEvent) event).getDamager();
			if (ent instanceof Player) {
				Player plr = (Player) ent;
				if (sa.getPlayerListener().commonPlayers.contains(plr.getName())) {
					sa.getPlayerListener().reappear(plr);
				}
			}
		}
	}

	public boolean isMonster(LivingEntity le) {
		if (le instanceof Creeper || le instanceof Ghast || le instanceof Giant
				|| le instanceof PigZombie || le instanceof Monster
				|| le instanceof Skeleton || le instanceof Slime
				|| le instanceof Spider || le instanceof Wolf
				|| le instanceof Zombie) {
			return true;
		}
		return false;
	}
}
