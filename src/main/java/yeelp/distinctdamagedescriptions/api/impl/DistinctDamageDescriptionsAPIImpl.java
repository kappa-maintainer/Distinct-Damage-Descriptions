package yeelp.distinctdamagedescriptions.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import yeelp.distinctdamagedescriptions.api.DDDAPI;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.IDistinctDamageDescriptionsAccessor;
import yeelp.distinctdamagedescriptions.api.IDistinctDamageDescriptionsMutator;
import yeelp.distinctdamagedescriptions.capability.IArmorDistribution;
import yeelp.distinctdamagedescriptions.capability.ICreatureType;
import yeelp.distinctdamagedescriptions.capability.IDamageDistribution;
import yeelp.distinctdamagedescriptions.capability.IMobResistances;
import yeelp.distinctdamagedescriptions.capability.ShieldDistribution;
import yeelp.distinctdamagedescriptions.capability.providers.ArmorDistributionProvider;
import yeelp.distinctdamagedescriptions.capability.providers.CreatureTypeProvider;
import yeelp.distinctdamagedescriptions.capability.providers.DamageDistributionProvider;
import yeelp.distinctdamagedescriptions.capability.providers.MobResistancesProvider;
import yeelp.distinctdamagedescriptions.capability.providers.ShieldDistributionProvider;
import yeelp.distinctdamagedescriptions.handlers.CapabilityHandler;
import yeelp.distinctdamagedescriptions.init.config.DDDConfigurations;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;
import yeelp.distinctdamagedescriptions.util.ArmorMap;
import yeelp.distinctdamagedescriptions.util.ArmorValues;
import yeelp.distinctdamagedescriptions.util.DDDDamageSource;
import yeelp.distinctdamagedescriptions.util.ResistMap;
import yeelp.distinctdamagedescriptions.util.lib.YResources;

public enum DistinctDamageDescriptionsAPIImpl implements IDistinctDamageDescriptionsAccessor, IDistinctDamageDescriptionsMutator {
	INSTANCE;

	private static final EntityEquipmentSlot[] armorSlots = {EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS};

	private DistinctDamageDescriptionsAPIImpl() {
		DDDAPI.accessor = this;
		DDDAPI.mutator = this;
	}

	/*
	 * *********** ACCESSOR *
	 ************/

	@Override
	@Nullable
	public IDamageDistribution getDamageDistribution(ItemStack stack) {
		return stack.isEmpty() ? null : DamageDistributionProvider.getDamageDistribution(stack);
	}

	@Override
	public IDamageDistribution getDamageDistribution(EntityLivingBase entity) {
		return DamageDistributionProvider.getDamageDistribution(entity);
	}

	@Override
	public IDamageDistribution getDamageDistribution(IProjectile projectile) {
		return DamageDistributionProvider.getDamageDistribution(projectile);
	}

	@Override
	@Nullable
	public IArmorDistribution getArmorResistances(ItemStack stack) {
		if(stack == null || !(stack.getItem() instanceof ItemArmor)) {
			return null;
		}
		return ArmorDistributionProvider.getArmorResistances(stack);
	}

	@Override
	public IMobResistances getMobResistances(EntityLivingBase entity) {
		return MobResistancesProvider.getMobResistances(entity);
	}

	@Override
	public ICreatureType getMobCreatureType(EntityLivingBase entity) {
		return CreatureTypeProvider.getCreatureType(entity);
	}

	@Override
	@Nullable
	public ShieldDistribution getShieldDistribution(ItemStack stack) {
		if(stack == null || !(stack.getItem() instanceof ItemShield)) {
			return null;
		}
		return ShieldDistributionProvider.getShieldDistribution(stack);
	}

	@Override
	public Map<EntityEquipmentSlot, IArmorDistribution> getArmorDistributionsForEntity(EntityLivingBase entity) {
		HashMap<EntityEquipmentSlot, IArmorDistribution> map = new HashMap<EntityEquipmentSlot, IArmorDistribution>();
		for(EntityEquipmentSlot slot : armorSlots) {
			map.put(slot, DDDAPI.accessor.getArmorResistances(entity.getItemStackFromSlot(slot)));
		}
		return map;
	}

	@Override
	public ArmorMap getArmorValuesForEntity(EntityLivingBase entity, Iterable<EntityEquipmentSlot> slots) {
		ArmorMap map = new ArmorMap();
		for(EntityEquipmentSlot slot : slots) {
			ItemStack stack = entity.getItemStackFromSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}
			if(stack.getItem() instanceof ItemArmor) {
				ItemArmor armorItem = (ItemArmor) stack.getItem();
				IArmorDistribution armorResists = getArmorResistances(stack);
				// merge this distribution with all previous armor distribution maps
				armorResists.distributeArmor(armorItem.damageReduceAmount, armorItem.toughness).forEach((k, v) -> map.merge(k, v, ArmorValues::merge));
			}
		}
		return map;
	}

	@Override
	public Optional<IDamageDistribution> classifyDamage(@Nonnull DamageSource src, EntityLivingBase target) {
		IDamageDistribution dist = DDDRegistries.distributions.getDamageDistribution(src, target);
		if(dist.getWeight(DDDBuiltInDamageType.NORMAL) == 1) {
			Entity sourceEntity = src.getImmediateSource();
			if(sourceEntity == null && src.getTrueSource() instanceof EntityPlayer) {
				dist = getDistForLivingEntity((EntityLivingBase) src.getTrueSource());
			}
			else if(sourceEntity instanceof EntityLivingBase) {
				dist = getDistForLivingEntity((EntityLivingBase) sourceEntity);
			}
			else if(isValidProjectile(src)) {
				IProjectile projectile = (IProjectile) src.getImmediateSource();
				dist = getDamageDistribution(projectile);
			}
		}
		return Optional.ofNullable(dist);
	}

	@Override
	public ResistMap classifyResistances(Set<DDDDamageType> types, IMobResistances resists) {
		ResistMap map = new ResistMap();
		if(types.isEmpty() || resists == null) {
			return map;
		}
		for(DDDDamageType s : types) {
			map.put(s, resists.getResistance(s));
		}
		return map;
	}

	@Override
	public boolean isPhysicalDamageOnly(DDDDamageSource src) {
		for(DDDDamageType type : src.getExtendedTypes()) {
			if(isPhysicalDamage(type)) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidProjectile(DamageSource src) {
		if(src.getImmediateSource() == null) {
			return false;
		}
		return DDDConfigurations.projectiles.configured(YResources.getEntityIDString(src.getImmediateSource()).orElse(""));
	}

	private IDamageDistribution getDistForLivingEntity(EntityLivingBase attacker) {
		ItemStack heldItem = attacker.getHeldItemMainhand();
		boolean hasEmptyHand = heldItem.isEmpty();
		if(!hasEmptyHand) {
			return getDamageDistribution(heldItem);
		}
		else {
			return getDamageDistribution(attacker);
		}
	}

	/***********
	 * MUTATOR *
	 ***********/
	@Override
	public void setPlayerResistances(EntityPlayer player, ResistMap newResists, Set<DDDDamageType> newImmunities, boolean adaptive, float adaptiveAmount) {
		IMobResistances mobResists = getMobResistances(player);
		for(Entry<DDDDamageType, Float> entry : newResists.entrySet()) {
			mobResists.setResistance(entry.getKey(), entry.getValue());
		}
		mobResists.clearImmunities();
		for(DDDDamageType type : newImmunities) {
			mobResists.setImmunity(type, true);
		}
		mobResists.setAdaptiveResistance(adaptive);
		mobResists.setAdaptiveAmount(adaptiveAmount);
		CapabilityHandler.syncResistances(player);
	}

	@Override
	public boolean updateAdaptiveResistances(EntityLivingBase entity, DDDDamageType... types) {
		IMobResistances resists = getMobResistances(entity);
		if(resists.hasAdaptiveResistance()) {
			boolean sync = resists.updateAdaptiveResistance(types);
			if(entity instanceof EntityPlayer) {
				CapabilityHandler.syncResistances((EntityPlayer) entity);
			}
			return sync;
		}
		return false;
	}
}