package yeelp.distinctdamagedescriptions.integration.hwyla;

import java.util.Collections;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import yeelp.distinctdamagedescriptions.ModConsts;
import yeelp.distinctdamagedescriptions.handlers.Handler;
import yeelp.distinctdamagedescriptions.integration.IModIntegration;

public class Hwyla implements IModIntegration {

	public Hwyla() {

	}

	@Override
	public boolean init(FMLInitializationEvent evt) {
		return FMLInterModComms.sendMessage(new Hwyla().getModID(), "register", "yeelp.distinctdamagedescriptions.integration.hwyla.Hwyla.registerHwyla");
	}

	public static void registerHwyla(IWailaRegistrar registrar) {
		EntityHandler handler = new EntityHandler();
		registrar.registerBodyProvider(handler, Entity.class);
		registrar.registerNBTProvider(handler, Entity.class);
	}

	@Override
	public String getModID() {
		return ModConsts.HWYLA_ID;
	}

	@Override
	public Iterable<Handler> getHandlers() {
		return Collections.emptyList();
	}
}
