package yeelp.distinctdamagedescriptions.util.lib;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * A collection of useful methods for dealing with ResourceLocations. Mainly to
 * reduce successive calls like:
 * {@code stack.getItem().getRegistryName().toString()}, at the cost of a couple
 * stack frames.
 * 
 * @author Yeelp
 *
 */
public final class YResources {
	public static ResourceLocation getRegistryName(ItemStack stack) {
		return getRegistryName(stack.getItem());
	}

	public static ResourceLocation getRegistryName(Item item) {
		return item.getRegistryName();
	}

	public static String getRegistryString(ItemStack stack) {
		return getRegistryName(stack.getItem()).toString();
	}

	public static String getRegistryString(Item item) {
		return item.getRegistryName().toString();
	}

	public static Optional<ResourceLocation> getEntityID(Entity entity) {
		return Optional.ofNullable(EntityList.getKey(entity));
	}

	public static Optional<String> getEntityIDString(Entity entity) {
		Optional<ResourceLocation> loc = getEntityID(entity);
		if(loc.isPresent()) {
			return Optional.of(loc.get().toString());
		}
		return Optional.empty();
	}
}
