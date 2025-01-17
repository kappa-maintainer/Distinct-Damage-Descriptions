package yeelp.distinctdamagedescriptions.capability.distributors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import yeelp.distinctdamagedescriptions.capability.DDDCapabilityBase;
import yeelp.distinctdamagedescriptions.config.IDDDConfiguration;
import yeelp.distinctdamagedescriptions.config.ModConfig;

public abstract class AbstractCapabilityDistributorGeneratable<T, U, C extends DDDCapabilityBase<? extends NBTBase>> extends AbstractCapabilityDistributor<T, U, C> {

	protected AbstractCapabilityDistributorGeneratable(ResourceLocation loc) {
		super(loc);
	}

	@Override
	protected final C getCapability(T t, String key) {
		IDDDConfiguration<U> config = this.getConfig();
		if(config.configured(key)) {
			return this.createCapability(config.get(key));
		}
		else if(ModConfig.core.generateStats) {
			return this.generateCapability(t, new ResourceLocation(key));
		}
		else {
			return this.createCapability(config.getDefaultValue());
		}
	}

	protected abstract C generateCapability(T t, ResourceLocation key);

	protected abstract C createCapability(U configResult);
}
