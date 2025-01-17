package yeelp.distinctdamagedescriptions.api;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import yeelp.distinctdamagedescriptions.capability.IDamageDistribution;

/**
 * Interface for implementing custom damage distributions.
 * 
 * @author Yeelp
 *
 */
public interface DDDPredefinedDistribution extends Comparable<DDDPredefinedDistribution> {
	/**
	 * Is this distribution enabled?
	 * 
	 * @return true if enabled, false if not.
	 */
	boolean enabled();

	/**
	 * Get applicable damage types for this distribution
	 * 
	 * @param src    The DamageSource inflicted
	 * @param target the target of the damage source
	 * @return a Set containing types applicable to this context, or an empty Set if
	 *         no types are applicable.
	 */
	Set<DDDDamageType> getTypes(@Nonnull DamageSource src, @Nonnull EntityLivingBase target);

	/**
	 * Get the internal name of this distribution
	 * 
	 * @return The name
	 */
	String getName();

	/**
	 * Get an implementation of {@link IDamageDistribution} that contains a
	 * distribution that matches the given context. The types referenced in the
	 * returned distribution should be the exact same as the types returned from
	 * {@link #getTypes(DamageSource, EntityLivingBase)}.
	 * 
	 * @param src    The damage source
	 * @param target the target
	 * @return a non null IDamageDistribution reflecting the damage context, or an
	 *         empty Optional
	 */
	Optional<IDamageDistribution> getDamageDistribution(@Nonnull DamageSource src, @Nonnull EntityLivingBase target);

	/**
	 * Get the priority of this distribution. The distributions are checked in the
	 * order of highest priority to lowest. The first predefined distribution to
	 * give a valid distribution is used.
	 * 
	 * @return the priority
	 */
	default int priority() {
		return 0;
	}

	@Override
	default int compareTo(DDDPredefinedDistribution o) {
		return Integer.compare(this.priority(), o.priority());
	}

}
