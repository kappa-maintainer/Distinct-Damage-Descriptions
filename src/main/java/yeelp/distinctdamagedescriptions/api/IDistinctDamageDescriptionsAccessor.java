package yeelp.distinctdamagedescriptions.api;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import yeelp.distinctdamagedescriptions.ModConsts;
import yeelp.distinctdamagedescriptions.capability.IArmorDistribution;
import yeelp.distinctdamagedescriptions.capability.ICreatureType;
import yeelp.distinctdamagedescriptions.capability.IDamageDistribution;
import yeelp.distinctdamagedescriptions.capability.IMobResistances;
import yeelp.distinctdamagedescriptions.capability.impl.CreatureType;
import yeelp.distinctdamagedescriptions.capability.impl.ShieldDistribution;
import yeelp.distinctdamagedescriptions.util.ArmorMap;
import yeelp.distinctdamagedescriptions.util.DDDDamageSource;
import yeelp.distinctdamagedescriptions.util.ResistMap;

public abstract interface IDistinctDamageDescriptionsAccessor {
	/**
	 * Get the damage distribution for an ItemStack - an instance of
	 * {@link IDamageDistribution}
	 * 
	 * @param stack
	 * @return the IDamageDistribution capability for this ItemStack, null for an
	 *         empty stack.
	 */
	@Nullable
	IDamageDistribution getDamageDistribution(ItemStack stack);

	/**
	 * Get the damage distribution for an EntityLivingBase - an instance of
	 * {@link IDamageDistribution}
	 * 
	 * @param entity
	 * @return the IDamageDistribution capability for this EntityLivingBase.
	 */
	@Nullable
	IDamageDistribution getDamageDistribution(EntityLivingBase entity);

	/**
	 * Get the damage distribution for an IProjectile - an instance of
	 * {@link IDamageDistribution}
	 * 
	 * @param projectile
	 * @return the IDamageDistribution capability for this IProjectile.
	 */
	@Nullable
	IDamageDistribution getDamageDistribution(IProjectile projectile);

	/**
	 * Get the armor resistances for an ItemStack - an instance of
	 * {@link IArmorDistribution}
	 * 
	 * @param stack
	 * @return the IArmorResistances capability for this ItemStack, or null if it
	 *         doesn't have it.
	 */
	@Nullable
	IArmorDistribution getArmorResistances(ItemStack stack);

	/**
	 * Get the mob resistances for an EntityLivingBase - an instance of
	 * {@link IMobResistances}
	 * 
	 * @param entity
	 * @return the IMobResistances for that entity
	 */
	@Nullable
	IMobResistances getMobResistances(EntityLivingBase entity);

	/**
	 * Get the mob's creature type - an instance of {@link ICreatureType}
	 * 
	 * @param entity
	 * @return the ICreatureType the mob has. Always returns
	 *         {@link CreatureType.UNKNOWN} for mobs that are instances of
	 *         EntityPlayer
	 */
	@Nullable
	ICreatureType getMobCreatureType(EntityLivingBase entity);

	/**
	 * Get a stack's shield distribution
	 * 
	 * @param stack
	 * @return a ShieldDistribution, or null if the stack doesn't have this
	 *         capability.
	 */
	@Nullable
	ShieldDistribution getShieldDistribution(ItemStack stack);

	/**
	 * Get a map of armor resistance for an entity
	 * 
	 * @param entity
	 * @return a Map that maps equipment slots to specific IArmorReistances present
	 *         in that slot
	 */
	Map<EntityEquipmentSlot, IArmorDistribution> getArmorDistributionsForEntity(EntityLivingBase entity);

	/**
	 * Get all armor values per damage type for an entity
	 * 
	 * @param entity
	 * @return A Map mapping damage types to a tuple (armor, toughness)
	 */
	default ArmorMap getArmorValuesForEntity(EntityLivingBase entity) {
		return getArmorValuesForEntity(entity, ModConsts.ARMOR_SLOTS_ITERABLE);
	}

	/**
	 * Get a map of damage types to armor values for that damage type.
	 * 
	 * @param entity
	 * @param slots  the slots to consider. Other slots are ignored, even if they
	 *               have armor in them.
	 * @return A Map mapping damage types to a tuple (armor, toughness).
	 */
	ArmorMap getArmorValuesForEntity(EntityLivingBase entity, Iterable<EntityEquipmentSlot> slots);

	/**
	 * classify and categorize damage.
	 * 
	 * @param src    DamageSource
	 * @param target the defending EntityLivingBase
	 * @return The damage distribution that gives the context for the damage.
	 */
	Optional<IDamageDistribution> classifyDamage(@Nonnull DamageSource src, EntityLivingBase target);

	/**
	 * Divide resistances into categories
	 * 
	 * @param types
	 * @param resists
	 * @return a map of relevant resistances. Immunities aren't included.
	 */
	ResistMap classifyResistances(Set<DDDDamageType> types, IMobResistances resists);

	/**
	 * Check if a {@link DDDDamageSource} is physical (slash, pierce, bludgeoning)
	 * only.
	 * 
	 * @param src
	 * @return true if only physical.
	 */
	boolean isPhysicalDamageOnly(DDDDamageSource src);

	/**
	 * Check if a damage type string is physical.
	 * 
	 * @param damageType
	 * @return true if physical, false if not.
	 */
	default boolean isPhysicalDamage(DDDDamageType type) {
		return type.getType() == DDDDamageType.Type.PHYSICAL;
	}

}
