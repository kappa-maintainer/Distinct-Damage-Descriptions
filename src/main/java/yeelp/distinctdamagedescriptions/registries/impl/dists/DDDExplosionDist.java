package yeelp.distinctdamagedescriptions.registries.impl.dists;

import java.util.Collections;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import yeelp.distinctdamagedescriptions.ModConfig;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.DDDPredefinedDistribution;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.capability.IDamageDistribution;
import yeelp.distinctdamagedescriptions.capability.impl.DamageDistribution;
import yeelp.distinctdamagedescriptions.config.DefaultValues;
import yeelp.distinctdamagedescriptions.config.readers.DDDSingleStringConfigReader;
import yeelp.distinctdamagedescriptions.config.readers.exceptions.ConfigParsingException;
import yeelp.distinctdamagedescriptions.util.ConfigReaderUtilities;

public final class DDDExplosionDist implements DDDPredefinedDistribution {
	
	private static final class ConfigReader extends DDDSingleStringConfigReader {
		
		ConfigReader() {
			super(() -> ModConfig.dmg.extraDamage.explosionDist, () -> DefaultValues.EXPLOSION_DIST);
		}

		@Override
		protected boolean validEntry(String entry) {
			return entry.matches(ConfigReaderUtilities.DIST_REGEX);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		protected void parseEntry(String entry) {
			try {
				dist = new DamageDistribution(ConfigReaderUtilities.parseMap(entry, ConfigReaderUtilities::parseDamageType, Float::parseFloat, 0.0f));
			}
			catch(ConfigParsingException e) {
				// If we get here, something went really badly. The default fallback should always work
				e.printStackTrace();
			}
		}
	}
	private static IDamageDistribution dist;
	private static final ConfigReader READER = new ConfigReader();

	@Override
	public boolean enabled() {
		return ModConfig.dmg.extraDamage.enableExplosionDamage;
	}

	@Override
	public Set<DDDDamageType> getTypes(DamageSource source, EntityLivingBase target) {
		return source.isExplosion() && this.enabled() ? dist.getCategories() : Collections.emptySet();
	}

	@Override
	public IDamageDistribution getDamageDistribution(DamageSource src, EntityLivingBase target) {
		return src.isExplosion() ? dist : DDDBuiltInDamageType.NORMAL.getBaseDistribution();
	}

	@Override
	public String getName() {
		return "explosion";
	}

	public static void update() {
		if(ModConfig.dmg.extraDamage.enableExplosionDamage) {
			READER.read();
		}
	}
}
