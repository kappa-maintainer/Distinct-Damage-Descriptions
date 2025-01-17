package yeelp.distinctdamagedescriptions.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public final class GeneralCategory {
	@Name("Suppress Warnings")
	@Comment("If warning messages from Distinct Damage Descriptions are clogging the log, you can disable them here. This may be indicative of a real issue though, so make sure there's no real issue first!")
	public boolean suppressWarnings = false;

	@Name("Suppress Registration Info")
	@Comment("If registration messages from Distinct Damage Descriptions are clogging the log, you can disable them here.")
	public boolean suppressRegistrationInfo = false;

	@Name("Generate Configs")
	@Comment("If set to true, and Distinct Damage Description will try to generate appropriate config values for weapons, mobs, armor and projectiles. This isn't super accurate and is rather primitive.")
	public boolean generateStats = false;

	@Name("Generate JSON")
	@Comment("If set, DistinctDamageDescriptions will generate example JSON files on startup for custom damage types and creature types.")
	public boolean generateJSON = true;

	@Name("Use Custom Damage Types")
	@Comment("If true, Distinct Damage Descriptions will load and enable custom damage types from JSON found in config/distinctdamagedescriptions/damageTypes")
	@RequiresMcRestart
	public boolean useCustomDamageTypes = false;

	@Name("Use Custom Death Messages")
	@Comment("Should Distinct Damage Descriptions use it's custom death messages for damage types?")
	@RequiresWorldRestart
	public boolean useCustomDeathMessages = false;

	@Name("Use Creature Types")
	@Comment({
			"If true, DistinctDamageDescriptions will load custom creature types from JSON located in config/distinctdamagedescriptions/creatureTypes.",
			"These JSON files can be used to apply potion/critical hit immunities to large swaths of mobs at once. Also usuable in CraftTweaker."})
	@RequiresMcRestart
	public boolean useCreatureTypes = false;

	@Name("Enable Adaptive Weakness")
	@Comment({
			"Enable Adaptive Weakness",
			"Adaptive Weakness kicks in when a mob that is adaptive is hit by type(s) they are weak to.",
			"Their adaptive amount is set to a percentage of the base amount, that percentage being equal to exp(avg), where: ",
			"   exp being the exponential function",
			"   avg is the average of all of the mobs weakness values that were hit (which is negative)",
			"This triggers only when a mob's adaptability is triggered."})
	public boolean enableAdaptiveWeakness = false;

	@Name("Register Potions")
	@Comment("If true, DDD will register and add potions and potion effects that grant resistance buffs/debuffs")
	@RequiresMcRestart
	public boolean enablePotionRegistration = false;
}
